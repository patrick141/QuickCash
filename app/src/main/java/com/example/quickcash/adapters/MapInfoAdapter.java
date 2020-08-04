package com.example.quickcash.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quickcash.R;
import com.example.quickcash.models.Job;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.parse.ParseFile;

import java.util.HashMap;

import static com.example.quickcash.adapters.JobsAdapter.dateDisplay;

public class MapInfoAdapter implements GoogleMap.InfoWindowAdapter {
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
        Job job = markers.get(marker);
        View view = LayoutInflater.from(context).inflate(R.layout.map_job_view, null);

        TextView tvName = view.findViewById(R.id.tv_map_name);
        TextView tvDate = view.findViewById(R.id.tv_map_date);
        TextView tvPrice = view.findViewById(R.id.tv_map_price);
        ImageView ivJob = view.findViewById(R.id.iv_mapImage);

        tvName.setText(job.getName());
        tvDate.setText(dateDisplay(job.getJobDate()));
        ParseFile image = job.getImage();
        if(image!=null){
            Glide.with(context).load(image.getUrl()).placeholder(R.drawable.logo).error(R.drawable.logo).into(ivJob);
        } else {
            Glide.with(context).load(R.drawable.logo).placeholder(R.drawable.logo).error(R.drawable.logo).into(ivJob);
        }
        tvPrice.setText("$" + String.format("%.2f", job.getPrice()));
        return view;
    }
}
