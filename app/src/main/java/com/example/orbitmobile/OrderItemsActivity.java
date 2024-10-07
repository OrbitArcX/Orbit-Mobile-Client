package com.example.orbitmobile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orbitmobile.adapters.OrderItemsAdapter;
import com.example.orbitmobile.models.OrderItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class OrderItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrderItems;
    private List<OrderItem> orderItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_items);

        recyclerViewOrderItems = findViewById(R.id.recycler_view_order_items);
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the order items from the intent
        String orderItemsJson = getIntent().getStringExtra("orderItems");
        Type orderItemType = new TypeToken<List<OrderItem>>() {}.getType();
        orderItems = new Gson().fromJson(orderItemsJson, orderItemType);

        // Set up the RecyclerView with the adapter
        OrderItemsAdapter orderItemsAdapter = new OrderItemsAdapter(orderItems, this);
        recyclerViewOrderItems.setAdapter(orderItemsAdapter);
    }
}
