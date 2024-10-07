package com.example.orbitmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orbitmobile.models.Order;
import com.google.gson.Gson;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView orderCancelledText, orderPendingText, orderDispatchedText, orderPartialDeliveredText, orderDeliveredText, orderItemsCount, shippingAddress,viewAllButton;
    private ImageView orderCancelledIcon, orderPendingIcon, orderDispatchedIcon, orderPartialDeliveredIcon, orderDeliveredIcon;

    //private Button ;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        // Initialize UI components
        orderCancelledText = findViewById(R.id.order_cancelled_text);
        orderPendingText = findViewById(R.id.order_pending_text);
        orderDispatchedText = findViewById(R.id.order_dispatched_text);
        orderPartialDeliveredText = findViewById(R.id.order_partial_delivered_text);
        orderDeliveredText = findViewById(R.id.order_delivered_text);

        orderCancelledIcon = findViewById(R.id.order_cancelled_icon);
        orderPendingIcon = findViewById(R.id.order_pending_icon);
        orderDispatchedIcon = findViewById(R.id.order_dispatched_icon);
        orderPartialDeliveredIcon = findViewById(R.id.order_partial_delivered_icon);
        orderDeliveredIcon = findViewById(R.id.order_delivered_icon);

        orderItemsCount = findViewById(R.id.order_items_count);
        shippingAddress = findViewById(R.id.shipping_address);
        viewAllButton = findViewById(R.id.view_all_button);

        // Retrieve the order data from intent
        String orderJson = getIntent().getStringExtra("order");
        order = new Gson().fromJson(orderJson, Order.class);

        // Set order status and icons visibility based on status
        if (order.getStatus().equalsIgnoreCase("Cancelled")) {
            orderCancelledText.setVisibility(View.VISIBLE);
            orderCancelledIcon.setVisibility(View.VISIBLE);
        } else {
            orderCancelledText.setVisibility(View.GONE);
            orderCancelledIcon.setVisibility(View.GONE);

            if (order.getStatus().equalsIgnoreCase("Pending")) {
                orderPendingIcon.setVisibility(View.VISIBLE);
            } else if (order.getStatus().equalsIgnoreCase("Dispatched")) {
                orderPendingIcon.setVisibility(View.VISIBLE);
                orderDispatchedIcon.setVisibility(View.VISIBLE);
            } else if (order.getStatus().equalsIgnoreCase("Partial Delivered")) {
                orderPendingIcon.setVisibility(View.VISIBLE);
                orderDispatchedIcon.setVisibility(View.VISIBLE);
                orderPartialDeliveredIcon.setVisibility(View.VISIBLE);
            } else if (order.getStatus().equalsIgnoreCase("Delivered")) {
                orderPendingIcon.setVisibility(View.VISIBLE);
                orderDispatchedIcon.setVisibility(View.VISIBLE);
                orderPartialDeliveredIcon.setVisibility(View.VISIBLE);
                orderDeliveredIcon.setVisibility(View.VISIBLE);
            }
        }

        // Set order items count
        orderItemsCount.setText(order.getOrderItems().size() + " items");

        // Set shipping address
        shippingAddress.setText(order.getAddress());

        // View All button click event
        viewAllButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailsActivity.this, OrderItemsActivity.class);
            intent.putExtra("orderItems", new Gson().toJson(order.getOrderItems()));
            startActivity(intent);
        });
    }
}
