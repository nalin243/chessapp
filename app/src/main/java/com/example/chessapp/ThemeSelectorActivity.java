package com.example.chessapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ThemeSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this); // Apply current theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_selector);

        Button btnLight = findViewById(R.id.btnLight);
        Button btnDark = findViewById(R.id.btnDark);
        Button btnWood = findViewById(R.id.btnWood);

        btnLight.setOnClickListener(v -> {
            ThemeHelper.setTheme(this, "light");
            restartApp();
        });

        btnDark.setOnClickListener(v -> {
            ThemeHelper.setTheme(this, "dark");
            restartApp();
        });

        btnWood.setOnClickListener(v -> {
            ThemeHelper.setTheme(this, "wood");
            restartApp();
        });
    }

    private void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
