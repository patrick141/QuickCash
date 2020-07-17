package com.example.quickcash.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.quickcash.models.Job;

import java.util.List;

/**
 * This class will be used to modify the Jobs RV in our profile activity
 */

public class MyJobsSentAdapter extends JobsAdapter{

    public MyJobsSentAdapter(Context context, List<Job> jobs) {
        super(context, jobs);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }
}
