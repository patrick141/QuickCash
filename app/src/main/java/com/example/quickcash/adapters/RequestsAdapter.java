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
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcash.R;
import com.example.quickcash.detailactivities.RequestDetailsActivity;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Request;
import com.example.quickcash.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import static com.example.quickcash.adapters.JobsAdapter.getRelativeTimeAgo;
import static com.example.quickcash.detailactivities.MyJobsDetailsActivity.REQUEST_CODE_MYDA_RDA;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {
    public static final int SHORT_COMMENT_LIMIT = 35;

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
            itemView.setOnClickListener(this);
        }

        public void bind(Request request) {
            ParseUser user = request.getUser();
            tvRequestUsername.setText(user.getUsername());
            tvRequestComment.setText(getShortenText(request.getComment()) + context.getString(R.string.extra_text));
            ParseFile userImage = null;
            try {
                userImage = (ParseFile) user.fetchIfNeeded().getParseFile(User.KEY_USER_IMAGE);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(userImage == null){
                Glide.with(context).load(R.drawable.logo).into(ivRequest);
            } else{
                Glide.with(context).load(userImage.getUrl()).into(ivRequest);
            }
            if(userAssigned(request.getUser(), request)){
                tvRequestUsername.setBackground(context.getDrawable(R.drawable.assigned_user));
            }
            tvRequestCreatedAt.setText(getRelativeTimeAgo(request.getCreatedAt().toString()));
        }

        /**
         * Not only does this open RequestDetailsActivity, it also creates a transition
         * animation for the request's username and image.
         * @param view
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                Request request = requests.get(position);
                Intent i = new Intent(context, RequestDetailsActivity.class);
                i.putExtra(Request.class.getSimpleName(), Parcels.wrap(request));

                Pair<View, String> p1 = Pair.create((View) ivRequest, context.getString(R.string.tr_request_image));
                Pair<View, String> p2 = Pair.create((View) tvRequestUsername, context.getString(R.string.tr_request_name));

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, p1, p2);
                ((Activity) context).startActivityForResult(i, REQUEST_CODE_MYDA_RDA , options.toBundle());
            }
        }
    }

    /**
     * This function shortens the request's comment if it's not greater than 35 characters.
     * @param text
     * @return
     */
    public static String getShortenText(String text){
        return text.length() < SHORT_COMMENT_LIMIT ? text: text.substring(0,SHORT_COMMENT_LIMIT);
    }

    public static boolean userAssigned(ParseUser user, Request request){
        Job job = request.getJob();
        if(job.getAssignedUser() == null){
            return false;
        }
        return user.hasSameId(job.getAssignedUser());
    }
}
