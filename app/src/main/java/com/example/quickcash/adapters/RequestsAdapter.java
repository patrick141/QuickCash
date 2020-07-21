package com.example.quickcash.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcash.R;
import com.example.quickcash.RequestDetailsActivity;
import com.example.quickcash.models.Request;
import com.parse.ParseFile;

import org.parceler.Parcels;

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
        this.requests.addAll(requests);
        notifyDataSetChanged();
    }

    public void clear() {
        requests.clear();
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView ivRequest;
        private TextView tvRequestUsername;
        private TextView tvRequestComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRequest = itemView.findViewById(R.id.iv_request_User);
            tvRequestUsername = itemView.findViewById(R.id.request_user);
            tvRequestComment = itemView.findViewById(R.id.request_comment);
            itemView.setOnClickListener(this);
        }

        public void bind(Request request) {
            tvRequestUsername.setText(request.getUser().getUsername());
            tvRequestComment.setText(request.getComment());
            ParseFile userImage = (ParseFile) request.getUser().get("profilePic");
            if(userImage == null){
                Glide.with(context).load(R.drawable.logo).into(ivRequest);
            }
            else{
                Glide.with(context).load(userImage.getUrl()).into(ivRequest);
            }
        }

        /**
         * Added a transition animation that switches between a requestor image into RDA.
         * @param view
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                Request request = requests.get(position);
                Intent i = new Intent(context, RequestDetailsActivity.class);
                i.putExtra("REQUEST", Parcels.wrap(request));
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, (View) ivRequest, context.getResources().getString(R.string.tr_request_image));
                context.startActivity(i, options.toBundle());
            }
        }
    }
}
