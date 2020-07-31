package com.example.quickcash.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcash.R;
import com.example.quickcash.models.Notification;
import com.example.quickcash.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivMessenger;
        private TextView tvUser;
        private TextView tvMessenge;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMessenger = itemView.findViewById(R.id.iv_messenger);
            tvUser = itemView.findViewById(R.id.tv_messenger_name);
            tvMessenge = itemView.findViewById(R.id.tv_messenge);
        }

        public void bind(final Notification notification) {
            ParseUser user = notification.getSender();
            ParseFile image = user.getParseFile(User.KEY_USER_IMAGE);
            if(image != null){
                Glide.with(context).load(R.drawable.logo).placeholder(R.drawable.logo).into(ivMessenger);
            } else{
                Glide.with(context).load(image.getUrl()).placeholder(R.drawable.logo).into(ivMessenger);
            }
            tvUser.setText(user.getUsername());
            tvMessenge.setText(notification.getMessage());
        }
    }
}
