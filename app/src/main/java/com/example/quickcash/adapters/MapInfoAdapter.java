package com.example.quickcash.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quickcash.R;
import com.example.quickcash.detailactivities.JobDetailsActivity;
import com.example.quickcash.models.Job;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.HashMap;

import static com.example.quickcash.adapters.JobsAdapter.dateDisplay;

public class MapInfoAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    public static final int GM_REQUEST_CODE = 27;
    private final Context context;
    private HashMap<Marker, Job> markers;

    public MapInfoAdapter(Context context, HashMap<Marker,Job> markers){
        this.context = context;
        this.markers = markers;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = LayoutInflater.from(context).inflate(R.layout.map_job_view, null);
        Job job = markers.get(marker);
        if(job != null) {
            TextView tvName = view.findViewById(R.id.tv_map_name);
            TextView tvDate = view.findViewById(R.id.tv_map_date);
            TextView tvPrice = view.findViewById(R.id.tv_map_price);
            ImageView ivJob = view.findViewById(R.id.iv_mapImage);

            tvName.setText(job.getName());
            tvDate.setText(dateDisplay(job.getJobDate()));
            ParseFile image = job.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).placeholder(R.drawable.logo).error(R.drawable.logo).into(ivJob);
            } else {
                Glide.with(context).load(R.drawable.logo).placeholder(R.drawable.logo).error(R.drawable.logo).into(ivJob);
            }
            tvPrice.setText("$" + String.format("%.2f", job.getPrice()));
            return view;
        }
        else{
            return null;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Job job = markers.get(marker);
        Intent i = new Intent(context, JobDetailsActivity.class);
        i.putExtra(Job.class.getSimpleName(), Parcels.wrap(job));
        ((Activity) context).startActivityForResult(i, GM_REQUEST_CODE);
    }
}
