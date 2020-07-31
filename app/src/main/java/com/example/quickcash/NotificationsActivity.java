package com.example.quickcash;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rvNotifs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        toolbar = findViewById(R.id.toolbar_Home);
        rvNotifs = findViewById(R.id.rv_notifications);
        setSupportActionBar(toolbar);
    }

}