package com.example.quickcash;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.quickcash.databinding.ActivityMainBinding;
import com.example.quickcash.fragments.ComposeFragment;
import com.example.quickcash.fragments.HomeFragment;
import com.example.quickcash.fragments.MyJobsFragment;
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
    private Button logOutbutton;
    private final FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        logOutbutton = binding.MAbutton;
        bottomNavigationView = binding.bottomNavigation;
        toolbar = (Toolbar) findViewById(R.id.toolbar_Home);
        setSupportActionBar(toolbar);
        logOutbutton.setVisibility(View.GONE);

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
                        fragment = new MyJobsFragment();
                        break;
                    default:
                        fragment = new ProfileFragment();
                        logOutbutton.setVisibility(View.VISIBLE);
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Default fragment when user signs is in the Home Fragment
        bottomNavigationView.setSelectedItemId(R.id.action_home);

        /**
         * This method logs the users out of QuickCash. They return to the login Screen.
         */


        logOutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}