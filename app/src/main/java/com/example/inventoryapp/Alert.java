package com.example.inventoryapp;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class Alert {
    /**
     * Shows an alert dialog.
     * @param title The title of the dialog.
     * @param message The message of the dialog.
     */
    public static void showAlert(Context context, String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", ((dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }))
                .create();
        dialog.show();;
    }
}
