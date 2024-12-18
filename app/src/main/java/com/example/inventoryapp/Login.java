package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        Context context = getApplicationContext();
        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Don't let user log in if already logged in
        if (sharedPref.contains("userId")) {
            // Send user information to inventory page
            long userId = sharedPref.getLong("userId", -1);

            Intent intent = new Intent(context, Inventory.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_login);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            Button login = findViewById(R.id.login);
            login.setOnClickListener(v -> {
                handleLogin();
            });

            TextView register = findViewById(R.id.register);
            register.setOnClickListener(v -> {
                startActivity(new Intent(context, Register.class));
            });
        }
    }

    /**
     * Confirms a user's credentials before showing inventory page.
     */
    private void handleLogin() {
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();

        try {
            UserDatabase db = new UserDatabase(this);
            long userId = db.getUser(username, password);
            db.close();

            // Check that the user's input matches a user in the database
            if (userId == UserDatabase.USER_INVALID) {
                Alert.showAlert(this, "Invalid Credentials", "Your login credentials are incorrect. Please try again or create a new user.");
                return;
            }

            // Set user as logged in
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong("userId", userId);
            editor.apply();

            // Send user information to inventory page
            Intent intent = new Intent(getApplicationContext(), Inventory.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("Error:", e.getMessage(), e);
            Alert.showAlert(this, "Failed Login", "Could not login. Please try again later.");
        }
    }
}