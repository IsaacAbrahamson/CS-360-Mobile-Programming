package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity {
    Context context;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        context = getApplicationContext();
        sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean showNotifications = sharedPref.getBoolean("showNotifications", false);

        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(this::handleLogout);

        Switch notificationsToggle = findViewById(R.id.notificationsToggle);
        notificationsToggle.setChecked(showNotifications);
        notificationsToggle.setOnClickListener(this::toggleNotifications);
    }

    /**
     * Toggle whether to show notifications.
     */
    private void toggleNotifications(View view) {
        boolean showNotifications = sharedPref.getBoolean("showNotifications", false);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("showNotifications", !showNotifications);
        editor.apply();
    }

    /**
     * Log the user out from the application.
     */
    private void handleLogout(View view) {
        // Clear preferences
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("userId");
        editor.apply();

        startActivity(new Intent(getApplicationContext(), Login.class));
    }
}
