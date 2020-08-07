package com.example.quickcash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.adapters.UsersAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rvUsers;
    private List<ParseUser> users;
    private UsersAdapter uAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        toolbar = findViewById(R.id.toolbar_Home);
        setSupportActionBar(toolbar);
        rvUsers = findViewById(R.id.rv_find_friends);
        users = new ArrayList<>();
        uAdapter = new UsersAdapter(this,users);
        rvUsers.setAdapter(uAdapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        queryUsers();
    }

    private void queryUsers() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> usersList, ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                }
                uAdapter.clear();
                uAdapter.addAll(usersList);
            }
        });
    }
}