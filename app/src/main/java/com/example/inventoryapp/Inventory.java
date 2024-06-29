package com.example.inventoryapp;

import android.Manifest;
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

        // Get list of all items
        refreshItems();

        // SEND_SMS needed to send SMS notifications to the user.
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Inventory.this, new String[]{
                    Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS}, 101);
        }
        // These permissions needed to get the user's phone number
        // TODO: This should be changed to get the user to enter what phone number to receive
        //  notifications at, but that feature is not necessary for initial release
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Inventory.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Inventory.this, new String[]{Manifest.permission.READ_SMS}, 101);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Inventory.this, new String[]{Manifest.permission.READ_PHONE_NUMBERS}, 101);
        }

        // Register event listeners
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

        // Set inventory list to be our inventory base adapter
        ListView listView = findViewById(R.id.inventoryList);
        InventoryBaseAdapter inventoryBaseAdapter = new InventoryBaseAdapter(this, items);
        listView.setAdapter(inventoryBaseAdapter);
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

    @Override
    public void onResume() {
        super.onResume();
        // Get a fresh list of inventory items
        refreshItems();
    }
}