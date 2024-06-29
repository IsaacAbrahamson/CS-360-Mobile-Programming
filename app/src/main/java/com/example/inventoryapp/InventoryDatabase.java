package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * The inventory database is used to store inventory data for a user.
 */
public class InventoryDatabase extends SQLiteOpenHelper {
    private final long userId;
    private static final String DATABASE_NAME = "inventory.db";
    private static final int VERSION = 1;

    public InventoryDatabase(Context context, long userId) {
        super(context, DATABASE_NAME, null, VERSION);
        this.userId = userId;
    }

    private static final class InventoryTable {
        private static final String TABLE = "inventory";
        private static final String ITEM_ID = "item_id";
        private static final String USER_ID = "user_id";
        private static final String NAME = "name";
        private static final String QTY = "qty";
        private static final String WARNING = "warning";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + InventoryTable.TABLE + " (" +
                InventoryTable.ITEM_ID + " integer primary key autoincrement, " +
                InventoryTable.USER_ID + " integer, " +
                InventoryTable.NAME + " text, " +
                InventoryTable.QTY + " integer, " +
                InventoryTable.WARNING + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("drop table if exists " + InventoryTable.TABLE);
        onCreate(db);
    }

    /**
     * Adds a new item to the inventory database.
     * @param name The item name
     * @param qty The item qty
     * @param warning The amount that triggers a warning
     */
    public void addItem(String name, int qty, int warning) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryTable.USER_ID, userId);
        values.put(InventoryTable.NAME, name);
        values.put(InventoryTable.QTY, qty);
        values.put(InventoryTable.WARNING, warning);

        db.insert(InventoryTable.TABLE, null, values);
    }

    /**
     * Gets all the inventory items.
     */
    public ArrayList<Item> getItems() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from " + InventoryTable.TABLE;
        Cursor cursor = db.rawQuery(sql, new String[] {});
        ArrayList<Item> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                long itemId = cursor.getLong(0);
                long userId = cursor.getLong(1);
                String name = cursor.getString(2);
                int qty = cursor.getInt(3);
                int warning = cursor.getInt(4);

                Item item = new Item();
                item.itemId = itemId;
                item.userId = userId;
                item.name = name;
                item.qty = qty;
                item.warning = warning;
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    /**
     * Updates an inventory item.
     * @param item The item to update
     */
    public void updateItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryTable.QTY, item.qty);
        values.put(InventoryTable.NAME, item.name);
        values.put(InventoryTable.WARNING, item.warning);
        db.update(InventoryTable.TABLE, values, InventoryTable.ITEM_ID + " = ?", new String[] { Float.toString(item.itemId) });
    }

    /**
     * Deletes an inventory item.
     * @param itemId The item to delete
     */
    public void deleteItem(long itemId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(InventoryTable.TABLE, InventoryTable.ITEM_ID + " = ?", new String[] { Long.toString(itemId) });
    }
}
