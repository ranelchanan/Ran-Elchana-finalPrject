package mta.com.final_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import mta.com.final_project.model.User;


public class SignUpActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signUpButton;
    private ProgressDialog registerProgressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
        signUpNewUserHandler();
    }

    private void initViews() {

        usernameEditText = findViewById(R.id.usernameEditText_signUp);
        emailEditText = findViewById(R.id.emailEditText_signUp);
        passwordEditText = findViewById(R.id.passwordEditText_signUp);
        signUpButton = findViewById(R.id.signUpButton_signUp);
        registerProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        userDB = FirebaseDatabase.getInstance().getReference().child("users");
    }

    private void signUpNewUserHandler(){

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String usernameStr = usernameEditText.getText().toString().trim();
                final String emailStr = emailEditText.getText().toString().trim();
                final String passwordStr = passwordEditText.getText().toString().trim();

                if (emailStr.equals("") || !Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                    emailEditText.setError("please enter valid email address");
                    emailEditText.requestFocus();
                } else if (usernameStr.isEmpty()) {
                    usernameEditText.setError("Please choose a username");
                    usernameEditText.requestFocus();
                } else if (passwordStr.length() < 6) {
                    passwordEditText.setError("password minimum contain 6 character");
                    passwordEditText.requestFocus();
                } else if (passwordStr.equals("")) {
                    passwordEditText.setError("please enter password");
                    passwordEditText.requestFocus();
                } else {

                    registerProgressDialog.setMessage("Please wait...");
                    registerProgressDialog.show();
                    createAccount(emailStr, passwordStr, usernameStr);

                }

            }

        });
    }
    private void createAccount(String email, String password, final String username){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        registerProgressDialog.dismiss();

                        if (task.isSuccessful()) {
                            final FirebaseUser currentUser = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();

                            currentUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    User newUser = new User(currentUser.getUid(), currentUser.getEmail(), "", currentUser.getDisplayName());
                                    userDB.child(currentUser.getUid()).setValue(newUser);
                                    finish();
                                    finishLoginActivity();
                                    startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            usernameEditText.setText("");
                            emailEditText.setText("");
                            passwordEditText.setText("");
                            Log.w("Error", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Email is already in use",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //this function make sure you can't get back to the login page after pressing the back button on the phone after getting to the home activity
    private void finishLoginActivity(){
        Intent intent = new Intent("finish");
        sendBroadcast(intent);
        finish();
    }

}