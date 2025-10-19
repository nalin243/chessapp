package com.example.chessapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this); // Apply saved theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnLeaderboard = findViewById(R.id.btnLeaderboard);
        Button btnRules = findViewById(R.id.btnRules);
        Button btnAbout = findViewById(R.id.btnAbout);

        // PLAY -> open UserRegistrationActivity (so user can enter names before Board)
        btnPlay.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, GameModeActivity.class))
        );

        // LEADERBOARD -> open leaderboard activity if exists
        // btnLeaderboard.setOnClickListener(v -> {
        //     startActivity(new Intent(MainActivity.this, LeaderboardActivity.class));
        // });

        // RULES -> open RulesActivity
        btnRules.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RulesActivity.class))
        );

        // ABOUT -> open AboutActivity
        // btnAbout.setOnClickListener(v ->
        //         startActivity(new Intent(MainActivity.this, AboutActivity.class))
        // );
    }
}
