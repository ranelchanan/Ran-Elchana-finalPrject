package mta.com.final_project;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.Objects;


public class CardViewItemActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private String userIdStr;
    private Toolbar toolbar;
    private DatabaseReference userDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view_item);

        initViews();
        getUserDetailsFromFirebase();
        setAnimalImageView((String) getIntent().getExtras().get("animalPhotoUrl"));
        setTimeAndDateTextView((String) getIntent().getExtras().get("timeAndDate"));
        setTitleTextView((String) getIntent().getExtras().get("title"));
        setLocationTextView((String) getIntent().getExtras().get("location"));
        setDescriptionTextView((String) getIntent().getExtras().get("description"));

//        if (Objects.equals(userIdStr, currentUser.getUid())) {  //if the current user is the user who created the post
//
//        } else {
//
//        }
    }

    private void initViews() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userIdStr = (String) getIntent().getExtras().get("userId");
        toolbar = findViewById(R.id.toolbar_cardViewItemActivity);
        userDB = FirebaseDatabase.getInstance().getReference().child("users");
        setToolbar();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUserImageView(String userImageUrlStr) {
        ImageView userImageView = findViewById(R.id.userImageView_cardViewItemActivity);
        Picasso.get()
                .load(userImageUrlStr)
                .placeholder(R.drawable.no_user_image)
                .into(userImageView);
    }

    public void setAnimalImageView(String animalImageUrlStr) {
        ImageView animalImageView = findViewById(R.id.animalImageView_cardViewItemActivity);
        Picasso.get()
                .load(animalImageUrlStr)
                .placeholder(R.drawable.no_animal_image)
                .into(animalImageView);
    }

    public void setUsernameTextView(String usernameStr) {
        TextView usernameTextView = findViewById(R.id.usernameTextView_cardViewItemActivity);
        usernameTextView.setText(usernameStr);
    }

    public void setTimeAndDateTextView(String timeAndDateStr) {
        TextView timeAndDateTextView = findViewById(R.id.timeAndDate_cardViewItemActivity);
        timeAndDateTextView.setText(timeAndDateStr);
    }

    public void setTitleTextView(String titleStr) {
        TextView titleTextView = findViewById(R.id.titleTextView_cardViewItemActivity);
        titleTextView.setText(titleStr);
    }

    public void setLocationTextView(String locationStr) {
        TextView locationTextView = findViewById(R.id.locationTextView_cardViewItemActivity);
        locationTextView.setText(locationStr);
    }

    public void setDescriptionTextView(String descriptionStr) {
        TextView descriptionTextView = findViewById(R.id.animalDescriptionTextView_cardViewItemActivity);
        descriptionTextView.setText(descriptionStr);
    }


    private void getUserDetailsFromFirebase(){
        userDB.child(userIdStr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String usernameStr = dataSnapshot.child("username").getValue(String.class);
                String userPhotoUrlStr = dataSnapshot.child("photoUrl").getValue(String.class);

                setUsernameTextView(usernameStr);
                setUserImageView(userPhotoUrlStr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}