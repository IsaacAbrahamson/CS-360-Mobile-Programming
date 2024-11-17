package com.example.inventoryapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

        // Create the quantity with color based on warning
        TextView qty = (TextView) convertView.findViewById(R.id.itemQty);
        qty.setText(String.valueOf(item.qty));
        if (item.qty <= item.warning) {
            qty.setTextColor(ContextCompat.getColor(context, R.color.red));
            sendNotification(item);
        } else {
            qty.setTextColor(ContextCompat.getColor(context, R.color.gray));
        }

        // Create the name
        TextView name = (TextView) convertView.findViewById(R.id.itemName);
        name.setText(item.name);

        // Create the delete button
        ImageView delete = (ImageView) convertView.findViewById(R.id.itemDelete);
        delete.setImageResource(R.drawable.delete);
        delete.setOnClickListener(v -> {
            deleteItem(item);
        });

        // Setup the on click for editing
        GridLayout gridLayout = convertView.findViewById(R.id.inventoryItem);
        gridLayout.setOnClickListener(v -> {
            // Send user information to inventory detail page
            Intent intent = new Intent(context, InventoryDetail.class);
            intent.putExtra("isUpdate", true);
            intent.putExtra("itemId", item.itemId);
            intent.putExtra("userId", item.userId);
            intent.putExtra("qty", item.qty);
            intent.putExtra("name", item.name);
            intent.putExtra("warning", item.warning);
            context.startActivity(intent);
        });

        return convertView;
    }

    /**
     * Sends a push notification message to the user.
     * @param item The item for the notification.
     */
    private void sendNotification(Item item) {
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
}
