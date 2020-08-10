package com.example.quickcash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.quickcash.databinding.ActivityLoginBinding;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.facebook.ParseFacebookUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    public static final int REQUEST_SIGN_UP = 132;
    public static final List<String> mPermissions = new ArrayList<String>(){{
        add("public_profile");
        add("email");
    }
    };
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private Button btnSignup;
    private Button btnFace;
    private Toolbar toolbar;
    private String fbUsername;
    private String fbEmail;
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
        btnFace = binding.btnLoginFacebook;
        toolbar = binding.toolbarLi;
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
                startActivityForResult(i, REQUEST_SIGN_UP);
            }
        });

        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, mPermissions,new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user == null) {
                            Log.d(TAG, getString(R.string.la_facebook_fail));
                        } else if (user.isNew()) {
                            Log.d(TAG, getString(R.string.la_facebook_new_user));
                            saveContents();
                        } else {
                            Log.d(TAG, getString(R.string.la_facebook_al_sign_in));
                            goMainActivity();
                        }
                    }
                });
            }
        });
    }

    /**
     * This method
     */
    private void saveContents() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String username = object.getString("name");
                    fbUsername = username;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    String email = object.getString("email");
                    fbEmail = email;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ParseUser user = ParseUser.getCurrentUser();
                user.setUsername(fbUsername);
                user.setEmail(fbEmail);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!= null){
                            e.printStackTrace();
                        }
                        Toast.makeText(LoginActivity.this, getString(R.string.login_fb_sucess), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","name,emails");
        request.setParameters(parameters);
        request.executeAsync();

        goMainActivity();
    }


    /**
     * This method signs our user into the App using Parse.
     * @param username
     * @param password
     * If username and password match an existing account, the user will be signed in.
     */
    protected void loginUser(final String username, final String password){
        Log.i(TAG, getString(R.string.attempt_log));
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if( e!= null){
                    Log.e(TAG, getString(R.string.issue_login), e);
                    Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                    return;
                }
                goMainActivity();
                Log.i(TAG, username + getString(R.string.login_sucess));
                Toast.makeText(LoginActivity.this, getString(R.string.login_sucess), Toast.LENGTH_SHORT).show();
            };
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

    /**
     * If the user goes to SignUpActivity and successfully
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SIGN_UP && resultCode == RESULT_OK){
            goMainActivity();
        }
    }
}