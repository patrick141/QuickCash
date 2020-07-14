package com.example.quickcash;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quickcash.databinding.ActivitySignUpBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

//If user needs to sign up for a new account. They are directed here.
public class SignUpActivity extends LoginActivity {
    public static final String TAG = "SignUpActivity";
    EditText etNewUsername;
    EditText etNewPassword;
    Button btnSignUpNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignUpBinding binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        etNewUsername = binding.SAusername;
        etNewPassword = binding.SApassword;
        btnSignUpNew = binding.SAbtnSignUp;

        btnSignUpNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = etNewUsername.getText().toString();
                final String password = etNewPassword.getText().toString();
                Toast.makeText(SignUpActivity.this, "Signing up " + username, Toast.LENGTH_SHORT).show();

                //We are
                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Log.e(TAG, "creating new user");
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
}