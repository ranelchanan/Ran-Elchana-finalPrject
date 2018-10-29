package mta.com.final_project;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static com.firebase.ui.auth.AuthUI.TAG;

public class EditProfileActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneNumberEditText;
    private Button submitButton;
    private ImageButton cameraIcon;
    private ProgressBar progressBar;
    private Uri userPhotoUri;
    private ImageView userImageView;
    private FirebaseUser currentUser;
    private DatabaseReference userDB;
    private StorageReference mStorageRef;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        initViews();
        getDataFromFirebase();
        onClickSubmitHandler();
        onClickChangePhotoHandler();
        onClickCameraIconHandler();
    }


    private void initViews() {
        usernameEditText = findViewById(R.id.changeUsernameEditText_editProfileActivity);
        passwordEditText = findViewById(R.id.changePasswordEditText_editProfileActivity);
        emailEditText = findViewById(R.id.changeEmailEditText_editProfileActivity);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText_editProfileActivity);
        cameraIcon = findViewById(R.id.cameraIcon_editProfileActivity);
        submitButton = findViewById(R.id.submitButton_editProfileActivity);
        progressBar = findViewById(R.id.progressBar_editProfileActivity);
        userImageView = findViewById(R.id.userImageView_editProfileActivity);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userDB = FirebaseDatabase.getInstance().getReference().child("users");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        toolbar = findViewById(R.id.toolbar_editProfileActivity);
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
            HomeActivity.setIsOnResumeFromEditProfile();
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void onClickChangePhotoHandler() {
        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraAndGallery();
            }
        });
    }

    private void onClickCameraIconHandler() {
        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraAndGallery();
            }
        });
    }

    private void openCameraAndGallery() {
        progressBar.setVisibility(View.VISIBLE);
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(EditProfileActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        progressBar.setVisibility(View.GONE);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                userPhotoUri = result.getUri();
                try {
                Picasso.get()
                        .load(userPhotoUri)
                        .placeholder(R.drawable.no_user_image)
                        .into(userImageView);
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String getFileExtension(Uri uri) { //This method gets the extension of the image (like JPG, PNG, ...)
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void updateUserPhoto(Uri newPhotoUri){
        StorageReference userImageRef = mStorageRef.child("userImages").child(currentUser.getUid()).child(newPhotoUri.getLastPathSegment() + "." + getFileExtension(newPhotoUri));
        userImageRef.putFile(newPhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String photoStringLink = uri.toString();
                        userDB.child(currentUser.getUid()).child("photoUrl").setValue(photoStringLink);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Uploaded failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onClickSubmitHandler(){
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String newUsername = usernameEditText.getText().toString().trim();
                String newEmail = emailEditText.getText().toString().trim();
                String newPassword = passwordEditText.getText().toString().trim();
                String newPhoneNumber = phoneNumberEditText.getText().toString().trim();

                updateUserInfo(newUsername, newEmail, newPassword, newPhoneNumber);

                cleanTextViewsAfterSubmitting();

            }
        });
    }

    private void cleanTextViewsAfterSubmitting() {
        usernameEditText.setText("");
        emailEditText.setText("");
        passwordEditText.setText("");
        phoneNumberEditText.setText("");
    }

    private void updateUserInfo(final String newUsername, final String newEmail, final String newPassword, final String newPhoneNumber) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build();

        currentUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                progressBar.setVisibility(View.GONE);

                if (!newUsername.isEmpty()) {
                    userDB.child(currentUser.getUid()).child("username").setValue(newUsername);
                }
                if (!newEmail.isEmpty()) {
                    if (newEmail.equals("") || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                        emailEditText.setError("please enter valid email address");
                        emailEditText.requestFocus();
                    } else { // New email is fine
                        userDB.child(currentUser.getUid()).child("email").setValue(newEmail);
                        currentUser.updateEmail(newEmail);
                    }
                }
                if (!newPhoneNumber.isEmpty()) {
                    userDB.child(currentUser.getUid()).child("phoneNumber").setValue(newPhoneNumber);
                }
                if (!newPassword.isEmpty()) {
                    if (newPassword.length() < 6) {
                        passwordEditText.setError("password minimum contain 6 character");
                        passwordEditText.requestFocus();
                    } else if (newPassword.equals("")) {
                        passwordEditText.setError("please enter password");
                        passwordEditText.requestFocus();
                    } else { // New password is fine
                        currentUser.updatePassword(newPassword);
                    }
                }
                if (userPhotoUri != null) {
                    updateUserPhoto(userPhotoUri);
                }

                Toast.makeText(getApplicationContext(), "User Info Updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void getDataFromFirebase(){
        userDB.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String photoUrl = dataSnapshot.child("photoUrl").getValue(String.class);

                usernameEditText.setHint(String.format("Username: %s", username));
                emailEditText.setHint(String.format("Email: %s", email));

                if (photoUrl.isEmpty()) {
                    Picasso.get()
                            .load(R.drawable.no_user_image)
                            .into(userImageView);
                } else {
                    Picasso.get()
                            .load(photoUrl)
                            .placeholder(R.drawable.no_user_image)
                            .into(userImageView);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
