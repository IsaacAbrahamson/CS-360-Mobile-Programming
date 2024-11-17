package com.example.inventoryapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class InventoryBaseAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Item> items;
    LayoutInflater inflater;

    public InventoryBaseAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).itemId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_inventory_item, null);
        Item item = items.get(position);

        // Create the quantity
        EditText qty = (EditText) convertView.findViewById(R.id.itemQty);
        qty.setText(String.valueOf(item.qty));
        qty.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String numberText = v.getText().toString();
                int newQty = Integer.parseInt(numberText.trim());

                item.qty = newQty;
                updateItem(item);
                updateQtyWarning(item, qty);

                return true;
            }
            return false;
        });

        updateQtyWarning(item, qty);

        // Create the name
        EditText name = (EditText) convertView.findViewById(R.id.itemName);
        name.setText(item.name);
        name.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                item.name = v.getText().toString();
                updateItem(item);
                return true;
            }
            return false;
        });

        // Create the delete button
        ImageView delete = (ImageView) convertView.findViewById(R.id.itemDelete);
        delete.setImageResource(R.drawable.delete);
        delete.setOnClickListener(v -> {
            deleteItem(item);
        });

        return convertView;
    }

    /**
     * Sends a push notification message to the user.
     * @param item The item for the notification.
     */
    private void sendNotification(Item item) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean showNotifications = sharedPref.getBoolean("showNotifications", false);
        if (!showNotifications) return;

        String channelId = context.getString(R.string.channel_id);

        String message = String.format(
                "Item %s (#%s) quantity is less than the set warning level: %s",
                item.name,
                item.itemId,
                item.warning);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Inventory Warning Exceeded")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        int permission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            try {
                manager.notify((int) item.itemId, builder.build());
            } catch (Exception e) {
                System.out.println("Notification error: " + e.getMessage());
            }
        }
    }

    /**
     * Updates the quantity number input to be styled based on warning and sends a notification.
     */
    private void updateQtyWarning(Item item, EditText qty) {
        if (item.qty <= item.warning) {
            qty.setTextColor(ContextCompat.getColor(context, R.color.red));
            sendNotification(item);
        } else {
            qty.setTextColor(ContextCompat.getColor(context, R.color.gray));
        }
    }


    /**
     * Deletes and item from the database.
     * @param item The item to delete.
     */
    private void deleteItem(Item item) {
        try {
            InventoryDatabase db = new InventoryDatabase(context, item.userId);
            db.deleteItem(item.itemId);
            db.close();

            // Reload the inventory page
            ((Activity) context).recreate();
        } catch (Exception e) {
            Log.e("Error:", e.getMessage(), e);
        }
    }

    /**
     * Updates the item in the inventory database
     */
    private void updateItem(Item item) {
        try {
            InventoryDatabase db = new InventoryDatabase(context, item.userId);
            db.updateItem(item);
            db.close();
        } catch (Exception e) {
            Log.e("Error:", e.getMessage(), e);
        }
    }
}
