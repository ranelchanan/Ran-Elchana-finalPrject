package mta.com.final_project;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressDialog loginProgressDialog;
    private TextView signUpTextView;
    private TextView forgotPasswordTextView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        registerReceiver(broadcast_reciever, new IntentFilter("finish"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast_reciever);
    }

    private void initViews() {

        emailEditText = findViewById(R.id.emailEditText_login);
        passwordEditText = findViewById(R.id.passwordEditText_login);
        loginButton = findViewById(R.id.loginButton_login);
        signUpTextView = findViewById(R.id.signUpTextView_login);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView_login);

        loginProgressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        loginUser();
        goToSignUpActivityHandler();
        forgotPasswordHandler();
    }

    private void forgotPasswordHandler() {
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEmailValid(emailEditText.getText().toString().trim())) {
                    Toast.makeText(LoginActivity.this, "Please enter your email",
                            Toast.LENGTH_SHORT).show();
                } else {

                    mAuth.sendPasswordResetEmail(emailEditText.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "New password sent to your mail",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "something went wrong",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void goToSignUpActivityHandler() {
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    private void loginUser(){

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String emailStr = emailEditText.getText().toString().trim();
                final String passwordStr = passwordEditText.getText().toString().trim();

                if (!isEmailValid(emailStr)) {
                    emailEditText.setError("please enter valid email address");
                    emailEditText.requestFocus();
                } else if (passwordStr.length() < 6) {
                    passwordEditText.setError("password minimum contain 6 character");
                    passwordEditText.requestFocus();
                } else if (passwordStr.equals("")) {
                    passwordEditText.setError("please enter password");
                    passwordEditText.requestFocus();
                } else {

                    loginProgressDialog.setMessage("Please wait...");
                    loginProgressDialog.show();
                    createAccount(emailStr, passwordStr);
                }
            }

        });
    }

    private boolean isEmailValid(String email){
        boolean res;
        if (email.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            res = false;
        } else {
            res = true;
        }

        return res;
    }

    private void createAccount(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        loginProgressDialog.dismiss();

                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //this function gets notified if the login activity needed to be finished (if a new user signed up and is now in the home activity)
    BroadcastReceiver broadcast_reciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if (action.equals("finish")) {
            //finishing the activity
                finish();
            }
        }
    };



}
