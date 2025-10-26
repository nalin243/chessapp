package com.example.chessapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private AuthManager authManager;
    private TextView tvUserGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this); // Apply saved theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize AuthManager
        authManager = AuthManager.getInstance(this);

        // Check authentication status
        if (!authManager.isLoggedIn()) {
            // User is not authenticated, redirect to LoginActivity
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Close MainActivity so user can't go back without logging in
            return;
        }

        // Initialize views
        tvUserGreeting = findViewById(R.id.tvUserGreeting);
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnLeaderboard = findViewById(R.id.btnLeaderboard);
        Button btnRules = findViewById(R.id.btnRules);
        Button btnAbout = findViewById(R.id.btnAbout);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Display user greeting
        displayUserGreeting();

        // PLAY -> open UserRegistrationActivity
        btnPlay.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, GameModeActivity.class))
        );

        // LEADERBOARD
        btnLeaderboard.setOnClickListener(v ->
             startActivity(new Intent(MainActivity.this, LeaderboardActivity.class))
        );

        // RULES
        btnRules.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RulesActivity.class))
        );

        // ABOUT -> open AboutActivity
        // btnAbout.setOnClickListener(v ->
        //         startActivity(new Intent(MainActivity.this, AboutActivity.class))
        // );

        // LOGOUT -> logout user and return to login screen
        btnLogout.setOnClickListener(v -> {
            authManager.logout();
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Check authentication status when returning to activity
        if (!authManager.isLoggedIn()) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }
        
        // Refresh user greeting in case user data changed
        displayUserGreeting();
    }

    /**
     * Displays personalized greeting message with logged-in username.
     * Shows the greeting TextView and sets the message with current user's username.
     */
    private void displayUserGreeting() {
        if (authManager.isLoggedIn()) {
            String username = authManager.getCurrentUsername();
            if (username != null) {
                String greetingMessage = "Welcome, " + username + "!";
                tvUserGreeting.setText(greetingMessage);
                tvUserGreeting.setVisibility(TextView.VISIBLE);
            }
        } else {
            tvUserGreeting.setVisibility(TextView.GONE);
        }
    }
}
