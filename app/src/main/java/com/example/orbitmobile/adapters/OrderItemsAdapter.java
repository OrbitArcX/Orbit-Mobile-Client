package com.example.orbitmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orbitmobile.R;
import com.example.orbitmobile.models.OrderItem;

import java.util.List;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder> {

    private List<OrderItem> orderItems;
    private Context context;

    public OrderItemsAdapter(List<OrderItem> orderItems, Context context) {
        this.orderItems = orderItems;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_details, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);

        holder.productName.setText(orderItem.getProduct().getName());
        holder.productPrice.setText(String.format("Price - Rs.%.2f", orderItem.getProduct().getPrice()));
        holder.productTotalPrice.setText(String.format("Rs.%.2f", orderItem.getTotalPrice()));
        holder.quantityText.setText("Qty - " + orderItem.getQuantity());

        // Load product image using Glide
        Glide.with(context).load(orderItem.getProduct().getImageUrl()).into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productTotalPrice, quantityText;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productTotalPrice = itemView.findViewById(R.id.product_total_price);
            quantityText = itemView.findViewById(R.id.product_quantity);
        }
    }
}
