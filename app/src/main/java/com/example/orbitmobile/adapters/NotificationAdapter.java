package com.example.orbitmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orbitmobile.R;
import com.example.orbitmobile.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;
    private Context context;

    public NotificationAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.titleTextView.setText(notification.getTitle());
        holder.bodyTextView.setText(notification.getBody());
        holder.createdAtTextView.setText(notification.getCreatedAt());

        if (notification.isSeenStatus()) {
            holder.itemView.setAlpha(0.5f);  // Reduce opacity for seen notifications
        } else {
            holder.itemView.setAlpha(1.0f);  // Full opacity for unseen notifications
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, bodyTextView, createdAtTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notification_title);
            bodyTextView = itemView.findViewById(R.id.notification_body);
            createdAtTextView = itemView.findViewById(R.id.notification_created_at);
        }
    }
}
