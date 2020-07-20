package com.example.quickcash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.quickcash.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * LoginActivity
 *
 * This class creates a login screen for our user to
 * sign into QuickCash or directs user to sign up
 * for an account.
 *
 * @author Patrick Amaro Rivera
 */
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;
    private Toolbar toolbar;
    /**
     * This is method calls upon getting the layout items from activity_login.xml.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        if(ParseUser.getCurrentUser() != null){
            goMainActivity();
        }
        View view = binding.getRoot();
        setContentView(view);
        etUsername = binding.laUsername;
        etPassword = binding.laPassword;
        btnLogin = binding.btnLogin;
        btnSignup = binding.btnSignup;
        toolbar = (Toolbar) binding.toolbarLi;
        setSupportActionBar(toolbar);

        /**
         * This methods signs the user into QuickCash by clicking the Login Botton. If the user
         * does not sign in, they will have a Toast saying sign up failed.
         */
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Login onClick button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Going to sign up");
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * This method signs our user into the App using Parse.
     * @param username
     * @param password
     * If username and password match an existing account, the user will be signed in.
     */
    protected void loginUser(final String username, final String password){
        Log.i(TAG, "Attempting to log");
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if( e!= null){
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Login failed. Try again", Toast.LENGTH_LONG).show();
                    return;
                }
                goMainActivity();
                Log.i(TAG, username + " is logged in.");
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Once user is ready to sign in or has signed up for a new account,
     * this starts the Main Activity class and ends the Login Activity
     * (or SignUp Activity) class.
     */
    protected void goMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

}