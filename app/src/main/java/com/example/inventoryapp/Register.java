package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
        });

        Button register = findViewById(R.id.register);
        register.setOnClickListener(v -> {
            registerUser();
        });
    }

    /**
     * Saves a user's login credentials in the user database.
     */
    private void registerUser() {
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();

        try {
            UserDatabase db = new UserDatabase(this);

            // Ensure that user doesn't exist with that username
            boolean isValidUsername = db.verifyUsername(username);
            if (!isValidUsername) {
                db.close();
                Alert.showAlert(this, "User Already Exists", "A user with that username already exists. Please try again with a different username.");
                return;
            }

            db.registerUser(username, password);
            db.close();
            // Go to login page after account creation to verify credentials
            startActivity(new Intent(getApplicationContext(), Login.class));
        } catch (Exception e) {
            Log.e("Error:", e.getMessage(), e);
            Alert.showAlert(this, "Failed to Register", "Could not register new user. Please try again later.");
        }
    }
}