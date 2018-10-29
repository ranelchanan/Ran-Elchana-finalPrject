package mta.com.final_project;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.lang.reflect.Field;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userDB;
    private BottomNavigationView bottomNavigationView;
    private static boolean isOnResumeFromEditProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        createNotificationChannel();
        initViews();
        checkIfUserLoggedIn();
        bottomMenuHandler();

        disableShiftMode(bottomNavigationView);
        if (getIntent()!=null) {
            Object openPage = getIntent().getStringExtra("Action");
            if (openPage != null) {
                Fragment selectedFragment=  new FoundAndLostTabsContainer();
                if(openPage.toString().equals("Message")) {
                     selectedFragment = new ShowMessagesFragment();
                }
                if(openPage.toString().equals("NewPost")) {
                    Bundle args = new Bundle();
                    Boolean isLost = getIntent().getStringExtra("isLost").toString().equals("true");
                    args.putBoolean("isLost",isLost );
                    selectedFragment.setArguments(args);
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
            }
        }

    }


    private void initViews(){
        mAuth = FirebaseAuth.getInstance();
        userDB = FirebaseDatabase.getInstance().getReference().child("users");
    }


    private void checkIfUserLoggedIn(){

        if (mAuth.getCurrentUser() == null) {
            goToLoginPage();
        }

    }

    private void goToLoginPage(){
        finish();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void bottomMenuHandler() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.homeItem_bottomNavigationMenu:
                                selectedFragment = new FoundAndLostTabsContainer();
                                break;
                            case R.id.lostAnimalItem_bottomNavigationMenu:
                                selectedFragment = new LostFormFragment();
                                break;

                            case R.id.profileItem_bottomNavigationMenu:
                                selectedFragment = new ProfileFragment();
                                break;
                            case R.id.message_bottomNavigationMenu:
                                selectedFragment = new ShowMessagesFragment();
                                break;
                        }

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new FoundAndLostTabsContainer());//new AnimalsListFragment());
        transaction.commit();
    }

    public static void setIsOnResumeFromEditProfile() {
        isOnResumeFromEditProfile = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isOnResumeFromEditProfile) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new ProfileFragment());//new AnimalsListFragment());
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.profileItem_bottomNavigationMenu);
            isOnResumeFromEditProfile = false;
        }
    }

    @SuppressLint("RestrictedApi")
    private void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }
}
