package com.example.quickcash.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.models.Request;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {
    private final Context context;
    private final List<Request> requests;

    public RequestsAdapter(Context context, List<Request> requests){
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = requests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void addAll(List<Request> requests){
        requests.addAll(requests);
        notifyDataSetChanged();
    }

    public void clear() {
        requests.clear();
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Request request) {
        }
    }
}
