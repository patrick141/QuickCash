package com.example.quickcash;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.quickcash.adapters.NotificationsAdapter;
import com.example.quickcash.databinding.ActivityNotificationsBinding;
import com.example.quickcash.models.Notification;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationsActivity
 *
 * This class
 */
public class NotificationsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tvInfo;
    private RecyclerView rvNotifs;
    private NotificationsAdapter nAdapters;
    private List<Notification> notifications;
    private SwipeRefreshLayout nSwipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNotificationsBinding binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        toolbar = findViewById(R.id.toolbar_Home);
        setSupportActionBar(toolbar);

        tvInfo = binding.notifcationInfo;
        tvInfo.setVisibility(View.GONE);
        rvNotifs = binding.rvNotifications;
        notifications = new ArrayList<>();
        nAdapters = new NotificationsAdapter(this, notifications);
        rvNotifs.setAdapter(nAdapters);
        rvNotifs.setLayoutManager(new LinearLayoutManager(this));

        nSwipeContainer = binding.swipeContainerNotifs;

        nSwipeContainer.setColorSchemeResources(
                R.color.background_color_orange,
                R.color.background_color_pink,
                R.color.background_color_dark_blue
        );

        nSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryNotifications();
            }
        });

        queryNotifications();
    }


    private void queryNotifications() {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);
        query.whereEqualTo(Notification.KEY_NOTIF_RECIEVE, ParseUser.getCurrentUser());
        query.addDescendingOrder(Notification.KEY_CREATED_AT);
        query.setLimit(20);
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> notifications, ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                }
                if(notifications == null){
                    tvInfo.setVisibility(View.VISIBLE);
                    return;
                }
                nAdapters.clear();
                nAdapters.addAll(notifications);
                nSwipeContainer.setRefreshing(false);
                nAdapters.notifyDataSetChanged();
            }
        });
    }

}