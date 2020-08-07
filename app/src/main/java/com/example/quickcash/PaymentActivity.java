package com.example.quickcash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.adapters.PaymentsAdapter;
import com.example.quickcash.models.Payment;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rvPayments;
    private PaymentsAdapter pAdapter;
    private List<Payment> payments;
    private TabLayout tlPayments;
    private TabItem tiPay;
    private TabItem tiRecieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        toolbar = findViewById(R.id.toolbar_Home);
        setSupportActionBar(toolbar);

        rvPayments = findViewById(R.id.rv_payments);
        payments = new ArrayList<>();
        pAdapter = new PaymentsAdapter(this,payments);
        rvPayments.setAdapter(pAdapter);
        rvPayments.setLayoutManager(new LinearLayoutManager(this));

        tlPayments = findViewById(R.id.tl_payments);
        tlPayments.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch(position){
                    case 0:
                        queryMyPayments();
                        break;
                    case 1:
                        queryPayments();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                return;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                return;
            }
        });
        queryPayments();
    }

    private void queryMyPayments() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<Payment> query = ParseQuery.getQuery(Payment.class);
        query.setLimit(20);
        query.whereEqualTo(Payment.KEY_RECIEVER, currentUser);
        query.findInBackground(new FindCallback<Payment>() {
            @Override
            public void done(List<Payment> paymentList, ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                }
                pAdapter.clear();
                pAdapter.addAll(paymentList);
            }
        });
    }

    private void queryPayments(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<Payment> query = ParseQuery.getQuery(Payment.class);
        query.setLimit(20);
        query.whereEqualTo(Payment.KEY_BUYER, currentUser);
        query.findInBackground(new FindCallback<Payment>() {
            @Override
            public void done(List<Payment> paymentList, ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                }
                pAdapter.clear();
                pAdapter.addAll(paymentList);
            }
        });
    }
}