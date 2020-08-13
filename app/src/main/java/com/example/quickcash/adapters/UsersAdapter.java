package com.example.quickcash.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.quickcash.ProfileActivity;
import com.example.quickcash.R;
import com.example.quickcash.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import static com.example.quickcash.adapters.JobsAdapter.timeNeed;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private Context context;
    private List<ParseUser> users;

    public UsersAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addAll(List<ParseUser> users){
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public void clear(){
        this.users.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName;
        private TextView tvPhone;
        private TextView tvDes;
        private TextView tvEmail;
        private RatingBar rbUser;
        private ImageView ivUser;

        // TODO: Finish all views.
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_Username);
            ivUser = itemView.findViewById(R.id.iv_profliePic);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvDes = itemView.findViewById(R.id.tv_UserSince);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            rbUser = itemView.findViewById(R.id.rb_user_rating);
            itemView.setOnClickListener(this);
        }

        public void bind(ParseUser user) {
            tvName.setText(user.getUsername());
            ParseFile image = user.getParseFile(User.KEY_USER_IMAGE);
            if(image != null){
                Glide.with(context).load(image.getUrl()).placeholder(R.drawable.logo).transform(new CircleCrop()).into(ivUser);
            } else{
                Glide.with(context).load(R.drawable.logo).transform(new CircleCrop()).into(ivUser);
            }
            tvEmail.setText(user.getEmail());
            tvDes.setText(context.getString(R.string.user_since) + " " + timeNeed(user.getCreatedAt()));
            String phone = user.getString(User.KEY_USER_PHONE);
            if(phone != null){
                tvPhone.setText(phone);
            } else {
                tvPhone.setText(context.getString(R.string.PA_no_phone));
            }
            rbUser.setRating((float) user.getDouble(User.KEY_USER_RATING));
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context, (View) itemView, context.getString(R.string.tr_profile));
            if(position != RecyclerView.NO_POSITION) {
                ParseUser user = users.get(position);
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
                context.startActivity(i, options.toBundle());
            }
        }
    }
}
