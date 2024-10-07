package com.example.orbitmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orbitmobile.R;
import com.example.orbitmobile.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private Context context;
    private OnCartItemUpdatedListener listener;

    public CartAdapter(List<CartItem> cartItemList, Context context, OnCartItemUpdatedListener listener) {
        this.cartItemList = cartItemList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        holder.productName.setText(cartItem.getProduct().getName());
        holder.productPrice.setText("Price - Rs." + cartItem.getProduct().getPrice());
        holder.productTotal.setText("Rs." + cartItem.getTotalPrice());
        holder.quantityText.setText(String.valueOf(cartItem.getQuantity()));

        // Load product image using Glide
        Glide.with(context).load(cartItem.getProduct().getImageUrl()).into(holder.productImage);

        // Quantity change listeners
        holder.increaseQuantity.setOnClickListener(v -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItem.setTotalPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());
            notifyItemChanged(position);
            listener.onCartUpdated(calculateSubtotal());
        });

        holder.decreaseQuantity.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                cartItem.setTotalPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());
                notifyItemChanged(position);
                listener.onCartUpdated(calculateSubtotal());
            }
        });

        // Remove button listener
        holder.removeButton.setOnClickListener(v -> {
            cartItemList.remove(position);
            notifyItemRemoved(position);
            listener.onCartUpdated(calculateSubtotal());
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    // Method to calculate subtotal
    private double calculateSubtotal() {
        double subtotal = 0;
        for (CartItem cartItem : cartItemList) {
            subtotal += cartItem.getTotalPrice();
        }
        return subtotal;
    }

    // Clear cart items method
    public void clearCart() {
        cartItemList.clear();
        notifyDataSetChanged();
        listener.onCartUpdated(0);  // Reset the prices
    }

    // Listener to update the prices in CartActivity
    public interface OnCartItemUpdatedListener {
        void onCartUpdated(double newSubtotal);
    }

    // Getter to access updated cart items in CartActivity
    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productTotal, quantityText;
        ImageButton increaseQuantity, decreaseQuantity;
        TextView removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productTotal = itemView.findViewById(R.id.product_total_price);
            quantityText = itemView.findViewById(R.id.product_quantity);
            increaseQuantity = itemView.findViewById(R.id.increase_quantity);
            decreaseQuantity = itemView.findViewById(R.id.decrease_quantity);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}
