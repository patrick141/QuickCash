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
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Request;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import static com.example.quickcash.MyJobsDetailsActivity.REQUEST_CODE_MYDA_RDA;
import static com.example.quickcash.adapters.JobsAdapter.getRelativeTimeAgo;

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
        private ImageView ivStar;
        private TextView tvRequestUsername;
        private TextView tvRequestComment;
        private TextView tvRequestCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRequest = itemView.findViewById(R.id.iv_request_User);
            tvRequestUsername = itemView.findViewById(R.id.request_user);
            tvRequestComment = itemView.findViewById(R.id.request_comment);
            tvRequestCreatedAt = itemView.findViewById(R.id.request_createdAt);
            ivStar = itemView.findViewById(R.id.iv_assigned);
            itemView.setOnClickListener(this);
        }

        public void bind(Request request) {
            ParseUser user = request.getUser();
            tvRequestUsername.setText(user.getUsername());
            tvRequestComment.setText(request.getComment());
            ParseFile userImage = null;
            try {
                userImage = (ParseFile) user.fetchIfNeeded().getParseFile("profilePic");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ivStar.setVisibility(View.GONE);
            if(userImage == null){
                Glide.with(context).load(R.drawable.logo).into(ivRequest);
            } else{
                Glide.with(context).load(userImage.getUrl()).into(ivRequest);
            }
            if(userAssigned(request.getUser(), request)){
                ivStar.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.star_user).into(ivStar);
            }
            tvRequestCreatedAt.setText(getRelativeTimeAgo(request.getCreatedAt().toString()));
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
                ((Activity) context).startActivityForResult(i, REQUEST_CODE_MYDA_RDA , options.toBundle());
            }
        }
    }

    public static boolean userAssigned(ParseUser user, Request request){
        Job job = request.getJob();
        if(job.getAssignedUser() == null){
            return false;
        }
        return user.hasSameId(job.getAssignedUser());
    }
}
