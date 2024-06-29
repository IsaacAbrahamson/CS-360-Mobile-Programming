package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The user database is used to store a user's username and password for logging in.
 */
public class UserDatabase extends SQLiteOpenHelper {
    public static final long USER_INVALID = -1;
    private static final String DATABASE_NAME = "user.db";
    private static final int VERSION = 1;

    public UserDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class UserTable {
        private static final String TABLE = "user";
        private static final String ID = "user_id";
        private static final String USERNAME = "username";
        private static final String PASSWORD = "password";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + UserTable.TABLE + " (" +
                UserTable.ID + " integer primary key autoincrement, " +
                UserTable.USERNAME + " text, " +
                UserTable.PASSWORD + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("drop table if exists " + UserTable.TABLE);
        onCreate(db);
    }

    /**
     * Saves a new user to the database.
     * @param username The user's username.
     * @param password The user's password.
     */
    public void registerUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserTable.USERNAME, username);
        values.put(UserTable.PASSWORD, password);

        db.insert(UserTable.TABLE, null, values);
    }

    /**
     * Checks if a username can be created.
     * @param username The user's username.
     * @return True if the username does not exist.
     */
    public boolean verifyUsername(String username) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from " + UserTable.TABLE + " where username = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { username });
        int count = cursor.getCount();
        cursor.close();
        return count == 0;
    }

    /**
     * Gets a user from the database.
     * @param username The user's username.
     * @param password The user's password.
     * @return The user id or -1 if user does not exist.
     */
    public long getUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from " + UserTable.TABLE + " where username = ? and password = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { username, password });
        long id = USER_INVALID;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0);
        }
        cursor.close();
        return id;
    }
}
