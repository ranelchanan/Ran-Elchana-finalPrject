package mta.com.final_project;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userDB;
    private Button logoutButton;
    private Button editProfileButton;
    private ImageView userImageView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView phoneNumberTextView;
    private View view;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews();
        onClickLogoutHandler();
        onClickEditProfileHandler();
        getDataFromFirebase();
        return view;
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userDB = FirebaseDatabase.getInstance().getReference().child("users");
        logoutButton = view.findViewById(R.id.logoutButton_profileFragment);
        editProfileButton = view.findViewById(R.id.EditProfileButton_profileFragment);
        userImageView = view.findViewById(R.id.userImageView_editProfileActivity);
        usernameTextView = view.findViewById(R.id.usernameTextView_profileFragment);
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView_profileFragment);
        emailTextView = view.findViewById(R.id.emailTextView_profileFragment);
    }


    private void onClickLogoutHandler() {

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Log Out");
                builder.setMessage("Are you sure you want to log out?");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

    private void onClickEditProfileHandler() {
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });
    }

    private void getDataFromFirebase(){
        userDB.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                String photoUrl = dataSnapshot.child("photoUrl").getValue(String.class);
                String phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);

                usernameTextView.setText(String.format("Username: %s", username));
                emailTextView.setText(String.format("Email: %s", email));

                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    phoneNumberTextView.setText(String.format("Phone number: %s", phoneNumber));
                }


              if (photoUrl.isEmpty()) {
                  Picasso.get()
                          .load(R.drawable.no_user_image)
                          .into(userImageView);
              } else {
                  Picasso.get()
                          .load(photoUrl)
                          .into(userImageView);

              }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

