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
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.quickcash.R;
import com.example.quickcash.detailactivities.NotificationDetailsActivity;
import com.example.quickcash.models.Notification;
import com.example.quickcash.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import static com.example.quickcash.adapters.JobsAdapter.getRelativeTimeAgo;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private final Context context;
    private final List<Notification> notifications;

    public NotificationsAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void addAll(List<Notification> notifications){
        this.notifications.addAll(notifications);
        notifyDataSetChanged();
    }

    public void clear(){
        notifications.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView ivMessenger;
        private TextView tvUser;
        private TextView tvMessenge;
        private TextView tvCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMessenger = itemView.findViewById(R.id.iv_messenger);
            tvUser = itemView.findViewById(R.id.tv_messenger_name);
            tvMessenge = itemView.findViewById(R.id.tv_messenge);
            tvCreatedAt = itemView.findViewById(R.id.tv_messenge_CA);
            itemView.setOnClickListener(this);
        }

        public void bind(final Notification notification) {
            ParseUser user = notification.getSender();
            ParseFile image = null;
            try {
                image = user.fetchIfNeeded().getParseFile(User.KEY_USER_IMAGE);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(image == null){
                Glide.with(context).load(R.drawable.logo).transform(new CircleCrop()).placeholder(R.drawable.logo).into(ivMessenger);
            } else{
                Glide.with(context).load(image.getUrl()).transform(new CircleCrop()).placeholder(R.drawable.logo).into(ivMessenger);
            }
            tvUser.setText(user.getUsername());
            tvMessenge.setText(notification.getMessage());
            tvCreatedAt.setText(getRelativeTimeAgo(notification.getCreatedAt().toString()));
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Notification notification = notifications.get(position);
                Intent intent = new Intent(context, NotificationDetailsActivity.class);
                intent.putExtra(Notification.class.getSimpleName(), Parcels.wrap(notification));
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation((Activity) context, (View) ivMessenger, context.getString(R.string.tr_profile_pic));
                context.startActivity(intent, options.toBundle());
            }
        }
    }
}
