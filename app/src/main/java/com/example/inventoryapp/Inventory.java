package com.example.inventoryapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Inventory extends AppCompatActivity {
    private long userId = UserDatabase.USER_INVALID;
    private ArrayList<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get user from login page
        userId = getIntent().getLongExtra("userId", UserDatabase.USER_INVALID);

        refreshItems();
        getNotificationPermissions();
        createNotificationChannel();
        registerEventListeners();
        createInventoryList();
    }

    /**
     * Gets the latest list of inventory items.
     */
    private void refreshItems() {
        try {
            InventoryDatabase db = new InventoryDatabase(this, userId);
            items = db.getItems();
            db.close();
        } catch (Exception e) {
            Log.e("Error:", e.getMessage(), e);
        }
    }

    /**
     * Requests appropriate notification permissions
     */
    private void getNotificationPermissions() {
        int permission = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Inventory.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
    }

    /**
     * Creates the notification channel.
     */
    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        String channelId = getString(R.string.channel_id);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this.
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * Registers the event listeners for the page.
     */
    private void registerEventListeners() {
        Button addItem = findViewById(R.id.newItem);
        addItem.setOnClickListener((View.OnClickListener) v -> {
            // Send user information to inventory detail page
            Intent intent = new Intent(getApplicationContext(), InventoryDetail.class);
            intent.putExtra("isUpdate", false);
            intent.putExtra("userId", userId);
            // We need to pass these values because view expects them for update
            intent.putExtra("itemId", -1);
            intent.putExtra("qty", -1);
            intent.putExtra("name", "");
            intent.putExtra("warning", -1);
            startActivity(intent);
        });
    }

    /**
     * Creates the inventory list to show on the page.
     */
    private void createInventoryList() {
        ListView listView = findViewById(R.id.inventoryList);
        InventoryBaseAdapter inventoryBaseAdapter = new InventoryBaseAdapter(this, items);
        listView.setAdapter(inventoryBaseAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get a fresh list of inventory items
        refreshItems();
    }
}