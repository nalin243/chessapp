package com.example.chessapp; // Use your package name

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    // Splash screen duration in milliseconds
    private static final int SPLASH_DURATION = 2500; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Use a Handler to delay the start of next activity
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if user is already logged in
                AuthManager authManager = AuthManager.getInstance(SplashActivity.this);
                
                Intent nextIntent;
                if (authManager.isLoggedIn()) {
                    // User is logged in, go to MainActivity
                    nextIntent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // User is not logged in, go to LoginActivity
                    nextIntent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                
                startActivity(nextIntent);
                // Close the splash activity so it's not on the back stack
                finish();
            }
        }, SPLASH_DURATION);
    }
}
