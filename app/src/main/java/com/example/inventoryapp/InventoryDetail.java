package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InventoryDetail extends AppCompatActivity {
    private Item item;
    private boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get item information from other pages
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isUpdate = extras.getBoolean("isUpdate");
            item = new Item();
            item.itemId = extras.getLong("itemId");
            item.userId = extras.getLong("userId");
            item.qty = extras.getInt("qty");
            item.name = extras.getString("name");
            item.warning = extras.getInt("warning");
        }

        Button addItem = findViewById(R.id.addItem);
        addItem.setOnClickListener(v -> {
            if (isUpdate) {
                handleUpdateItem();
            } else {
                handleAddItem();
            }
        });

        // Update button based on update
        String btnText;
        if (isUpdate) {
            btnText = "Update Item";
        } else {
            btnText = "Add Item";
        }
        addItem.setText(btnText);

        // Update inputs to match existing item
        if (isUpdate) {
            EditText name = (EditText) findViewById(R.id.name);
            EditText qty = (EditText) findViewById(R.id.qty);
            EditText warning = (EditText) findViewById(R.id.warning);
            qty.setText(String.valueOf(item.qty));
            name.setText(item.name);
            warning.setText(String.valueOf(item.warning));
        }
    }

    /**
     * Adds a new item to the inventory
     */
    private void handleAddItem() {
        String name = ((EditText) findViewById(R.id.name)).getText().toString();
        int qty = Integer.parseInt(((EditText) findViewById(R.id.qty)).getText().toString());
        int warning = Integer.parseInt(((EditText) findViewById(R.id.warning)).getText().toString());

        try {
            InventoryDatabase db = new InventoryDatabase(this, item.userId);
            db.addItem(name, qty, warning);
            db.close();
        } catch (Exception e) {
            Log.e("Error:", e.getMessage(), e);
        }

        startActivity(new Intent(getApplicationContext(), Inventory.class));
    }

    /**
     * Updates the item in the inventory
     */
    private void handleUpdateItem() {
        String name = ((EditText) findViewById(R.id.name)).getText().toString();
        int qty = Integer.parseInt(((EditText) findViewById(R.id.qty)).getText().toString());
        int warning = Integer.parseInt(((EditText) findViewById(R.id.warning)).getText().toString());

        try {
            InventoryDatabase db = new InventoryDatabase(this, item.userId);

            Item newItem = new Item(item);
            newItem.name = name;
            newItem.qty = qty;
            newItem.warning = warning;

            db.updateItem(newItem);
            db.close();
        } catch (Exception e) {
            Log.e("Error:", e.getMessage(), e);
        }

        startActivity(new Intent(getApplicationContext(), Inventory.class));
    }
}