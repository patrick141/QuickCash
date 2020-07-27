package com.example.quickcash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.quickcash.databinding.ActivitySignUpBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * SignUpActivity
 *
 * This class creates a new account for the user if they wish
 * to create one.
 *
 * @author Patrick Amaro Rivera
 */
public class SignUpActivity extends LoginActivity {
    public static final String TAG = "SignUpActivity";
    private EditText etNewUsername;
    private EditText etNewPassword;
    private Button btnSignUpNew;
    private Toolbar toolbar;

    /**
     * This is method calls upon getting the layout items from activity_sign_up.xml.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignUpBinding binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        etNewUsername = binding.SAUsername;
        etNewPassword = binding.SAPassword;
        btnSignUpNew = binding.SABtnSignUp;
        toolbar = binding.toolbarSu;
        toolbar.setTitle(getString(R.string.sign_up_text));
        setSupportActionBar(toolbar);

        /**
         * Clicking on sign up button should create a new account into the Parse DB.
         */
        btnSignUpNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = etNewUsername.getText().toString();
                final String password = etNewPassword.getText().toString();
                Toast.makeText(SignUpActivity.this, "Signing up " + username, Toast.LENGTH_SHORT).show();

                //We are creating a new account based off the user's details.
                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Log.e(TAG, "creating new user");
                            // If successful, user logs into account.
                            loginUser(username, password);
                        }
                        else{
                            Log.e(TAG, " failure on creating new account");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * This override makes sure that we finish LoginActivity as well as SignUpActivity.
     */
    @Override
    protected void goMainActivity() {
        Intent i = new Intent();
        setResult(RESULT_OK);
        finish();
    }
}