package com.example.quickcash;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.quickcash.databinding.ActivityMainBinding;
import com.example.quickcash.fragments.ComposeFragment;
import com.example.quickcash.fragments.HomeFragment;
import com.example.quickcash.fragments.JobTasksFragment;
import com.example.quickcash.fragments.ProfileFragment;
import com.example.quickcash.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

/**
 * MainActivity
 *
 * This class is the QuickCash Main Activity. This class has our
 * bottom nav view that switches fragment views.
 *
 * @author Patrick Amaro Rivera
 */

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    public SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkifFirstTime();

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        bottomNavigationView = binding.bottomNavigation;
        toolbar = (Toolbar) findViewById(R.id.toolbar_Home);
        setSupportActionBar(toolbar);

        /**
         *  This method handles switching between fragments. When ever a item is clicked, we create
         *  a Fragment based upon the selection. The Fragment manager that replaces the old(current)
         *  fragment with the new fragment created.
         */
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch(item.getItemId()){
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_search:
                        fragment = new SearchFragment();
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_myjobs:
                        fragment = new JobTasksFragment();
                        break;
                    default:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Default fragment when user signs is in the Home Fragment
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    /**
     * This is a function that checks to see if this is a user's first time installing this app.
     */
    private void checkifFirstTime() {
        prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        if(prefs.getBoolean(getString(R.string.app_name_ft),true)){
            playWelcome();
            prefs.edit().putBoolean(getString(R.string.app_name_ft),false).commit();
        }
    }

    /**
     * If this is the first time for user,  this method will execute.
     */
    private void playWelcome() {
        Toast.makeText(this, getString(R.string.welcome), Toast.LENGTH_LONG).show();
    }

    /**
     * Add the menu items from menu_main into our toolbar.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * When user clicks on sign out menu item on toolbar, they sign out of Quickcash.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.sign_out_main){
            playSignOutAD();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method opens an Alert Dialog and makes the user double confirm that they want to sign out.
     */
    private void playSignOutAD(){
        AlertDialog logOutDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.sign_out))
                .setMessage(getResources().getString(R.string.sign_out_text))
                .setIcon(R.drawable.logo)

                .setPositiveButton(getResources().getString(R.string.sign_out), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        signOut();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.return_now), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        logOutDialog.show();
    }

    /**
     * This method signs the user out of QuickCash.
     */
    private void signOut(){
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        Intent i = new Intent(this, LoginActivity.class);
        Toast.makeText(this, "You have signed out.", Toast.LENGTH_SHORT).show();
        startActivity(i);
        finish();
    }

}