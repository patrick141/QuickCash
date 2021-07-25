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
import com.example.quickcash.models.Friends;
import com.example.quickcash.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private EditText etEmail;
    private EditText etPhone;
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
        etEmail = binding.SAEmail;
        etPhone = binding.SAPhone;
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
                final String email = etEmail.getText().toString();
                final String phone = etPhone.getText().toString();
                if(username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()){
                    Toast.makeText(SignUpActivity.this, getString(R.string.sa_enterAll), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isValidEmail(email) || !isValidPhone(phone) ){
                    Toast.makeText(SignUpActivity.this, getString(R.string.sa_get_valid), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(SignUpActivity.this, "Signing up " + username, Toast.LENGTH_SHORT).show();
                //We are creating a new account based off the user's details.
                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.put(User.KEY_USER_PHONE,phone);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Log.e(TAG, "creating new user");
                            Friends friends = new Friends();
                            friends.setUser(user);
                            friends.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e!=null){
                                        e.printStackTrace();
                                    }
                                    user.put(User.KEY_USER_FRIENDS, friends);
                                    user.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e!= null){
                                                e.printStackTrace();
                                            }
                                            // If successful, user logs into account.
                                            loginUser(username, password);
                                        }
                                    });
                                }
                            });
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
     * This methods ensures that the email that is entered is valid. It uses a pattern and matcher
     * to determine if it fits the typical email style.
     * @param email
     * @return
     */
    private boolean isValidEmail(String email){
        String ePattern = getString(R.string.sa_email_valid);
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * This method ensures that the phone number entered is valid. Uses Pattern and Matcher to deter
     * mine if the phone number meets criteria.
     * @param phone
     * @return
     */
    private boolean isValidPhone(String phone){
        String num1 = phone.replace("(","");
        String num2 = num1.replace(")","");
        String number = num2.replace("-","");
        Pattern p = Pattern.compile(getString(R.string.sa_phone_valid));
        Matcher m = p.matcher(number);
        return (m.find() && m.group().equals(num1));
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