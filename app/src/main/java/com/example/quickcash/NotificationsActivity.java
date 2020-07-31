package com.example.quickcash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.adapters.NotificationsAdapter;
import com.example.quickcash.models.Notification;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rvNotifs;
    private NotificationsAdapter nAdapters;
    private List<Notification> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        toolbar = findViewById(R.id.toolbar_Home);
        rvNotifs = findViewById(R.id.rv_notifications);
        setSupportActionBar(toolbar);
        notifications = new ArrayList<>();
        nAdapters = new NotificationsAdapter(this, notifications);
        rvNotifs.setAdapter(nAdapters);

        rvNotifs.setLayoutManager(new LinearLayoutManager(this));

        queryNotifications();
    }

    private void queryNotifications() {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);
        query.whereEqualTo(Notification.KEY_NOTIF_RECIEVE, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> notifications, ParseException e) {
                nAdapters.clear();
                nAdapters.addAll(notifications);
            }
        });
    }

}