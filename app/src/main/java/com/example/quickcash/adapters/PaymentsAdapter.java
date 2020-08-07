package com.example.quickcash.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcash.R;
import com.example.quickcash.detailactivities.PaymentDetailsActivity;
import com.example.quickcash.models.Job;
import com.example.quickcash.models.Payment;
import com.example.quickcash.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.ViewHolder> {
    private final Context context;
    private final List<Payment> payments;

    public PaymentsAdapter(Context context, List<Payment> payments) {
        this.context = context;
        this.payments = payments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payment payment = payments.get(position);
        holder.bind(payment);
    }

    public void addAll(List<Payment> payments){
        this.payments.addAll(payments);
        notifyDataSetChanged();
    }

    public void clear(){
        payments.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView ivPaymentStatus;
        private ImageView ivRecipient;
        private TextView tvRecipient;
        private TextView tvReferJob;
        private TextView tvPaymentPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPaymentStatus = itemView.findViewById(R.id.ip_payment_pending);
            ivRecipient = itemView.findViewById(R.id.ip_image);
            tvRecipient = itemView.findViewById(R.id.ip_name);
            tvReferJob = itemView.findViewById(R.id.ip_job);
            tvPaymentPrice = itemView.findViewById(R.id.ip_price);
            itemView.setOnClickListener(this);
        }

        public void bind(Payment payment) {
            ParseUser recipient = payment.getRecipient();
            try {
                ParseFile image = recipient.fetchIfNeeded().getParseFile(User.KEY_USER_IMAGE);
                if(image != null){
                    Glide.with(context).load(image.getUrl()).into(ivRecipient);
                } else{
                    Glide.with(context).load(R.drawable.logo).into(ivRecipient);
                }
                tvRecipient.setText(recipient.getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Job job = payment.getJob();
            tvReferJob.setText(job.getName());
            tvPaymentPrice.setText("$" + String.format("%.2f", job.getPrice()));
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Payment payment = payments.get(position);
                Intent i = new Intent(context, PaymentDetailsActivity.class);
                i.putExtra(Payment.class.getSimpleName(), Parcels.wrap(payment));
                context.startActivity(i);
            }
        }
    }
}
