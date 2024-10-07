package com.example.orbitmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orbitmobile.OrderDetailsActivity;
import com.example.orbitmobile.R;
import com.example.orbitmobile.models.Order;
import com.google.gson.Gson;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;

    public OrdersAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderNumberText.setText("Order #" + order.getId());
        holder.orderItemsText.setText(order.getOrderItems().size() + " items");
        holder.orderStatusText.setText(order.getStatus());

        // Click listener for individual orders
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("order", new Gson().toJson(order)); // Passing the order object as JSON
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumberText, orderItemsText, orderStatusText;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumberText = itemView.findViewById(R.id.order_number);
            orderItemsText = itemView.findViewById(R.id.order_items);
            orderStatusText = itemView.findViewById(R.id.order_status);
        }
    }
}
