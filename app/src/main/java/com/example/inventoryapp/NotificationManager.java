package com.example.inventoryapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class NotificationManager {
    private final Context context;

    public NotificationManager(Context context) {
        this.context = context;
    }

    /**
     * Returns the phone number for the device to send SMS notifications to.
     * TODO: Ask the user for this and store it as TelephonyManager is not reliable for getting
     *  number in all regions and devices.
     */
    private String getDeviceNumber() {
        TelephonyManager tMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        // Permissions are checked in constructor above
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Dummy number
            return "";
        }
        return tMgr.getLine1Number();
    }

    /**
     * Sends an SMS notification to the user.
     *
     * @param message The message to send to the user.
     */
    public void sendSmsNotification(String message) {
        // Trying to send a notification without permission crashes app
        if (context.checkCallingOrSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Do not send message if we cannot access the device's number
        String deviceNumber = getDeviceNumber();
        if (Objects.equals(deviceNumber, "")) return;

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(deviceNumber, null, message, null, null);
    }
}
