package com.example.quickcash;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.quickcash.databinding.ActivityMainBinding;
import com.example.quickcash.fragments.ComposeFragment;
import com.example.quickcash.fragments.HomeFragment;
import com.example.quickcash.fragments.JobTasksFragment;
import com.example.quickcash.fragments.ProfileFragment;
import com.example.quickcash.fragments.SearchFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    public static final int MAIN_LOCATION = 1930;
    public static final int MAIN_TO_MAP = 1842;
    public static Button notifCount;
    private Menu toolbarMenu;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    public SharedPreferences prefs;

    private Location myLocation;
    private FusedLocationProviderClient client;

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

        client = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
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
        toolbarMenu = menu;
        return true;
    }

    /**
     * When user clicks on sign out menu item on toolbar, they sign out of Quickcash.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.map_main:
                Intent i = new Intent(this, MapActivity.class);
                startActivityForResult(i, MAIN_TO_MAP);
                break;
            case R.id.sign_out_main:
                playSignOutAD();
                break;
            case R.id.notifications_main:
                Intent intent = new Intent(this, NotificationsActivity.class);
                startActivity(intent);
                break;
            case R.id.payments_main:
                Intent in = new Intent(this, PaymentActivity.class);
                startActivity(in);
                break;
            case R.id.friends_main:
                Intent intent2 = new Intent(this, FriendActivity.class);
                startActivity(intent2);
            default:
                break;
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
        startActivityForResult(i, MAIN_TO_MAP);
        finish();
    }

    public Location getMyLocation(){
        return myLocation;
    }

    /**
     * This method gets our last location.
     */
    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MAIN_LOCATION);
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    myLocation = new Location(location);
                    myLocation.setLatitude(location.getLatitude());
                    myLocation.setLongitude(location.getLongitude());
                }
            }
        });
    }

    /**
     * User will be asked for permission, if they say yes, they can use their location.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case MAIN_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MAIN_TO_MAP && resultCode == RESULT_OK){
            HomeFragment hf = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.flContainer, hf).commit();
        }
    }

    /**
    public void updateIcon(){
        MenuInflater inflater = getMenuInflater();
        ParseLiveQueryClient client = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);
        SubscriptionHandling<Notification> subscriptionHandling = client.subscribe(query);

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Notification>() {
            @Override
            public void onEvent(ParseQuery<Notification> query, Notification object) {
                return;
            }
        });
    }*/
}