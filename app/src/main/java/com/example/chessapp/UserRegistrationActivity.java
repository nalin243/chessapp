package com.example.chessapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// Make sure you have these imports
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;


public class UserRegistrationActivity extends AppCompatActivity {

    EditText etPlayer1Name, etPlayer2Name;
    RadioGroup radioGroupColor;
    Button btnStartGame;
    Toolbar toolbar; // This should be androidx.appcompat.widget.Toolbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        etPlayer1Name = findViewById(R.id.etPlayer1Name);
        etPlayer2Name = findViewById(R.id.etPlayer2Name);
        radioGroupColor = findViewById(R.id.radioGroupColor);
        btnStartGame = findViewById(R.id.btnStartGame);

        // This code sets up the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        btnStartGame.setOnClickListener(v -> {
            String p1 = etPlayer1Name.getText().toString().trim();
            String p2 = etPlayer2Name.getText().toString().trim();
            // Save to SharedPreferences (optional) for display on board/leaderboard:
            getSharedPreferences("players", MODE_PRIVATE).edit()
                    .putString("player1", p1).putString("player2", p2).apply();

            RadioButton selectedColorButton = (findViewById(radioGroupColor.getCheckedRadioButtonId()));
            String playerColor = selectedColorButton.getText().toString();

            // Start BoardActivity
            Intent i = new Intent(UserRegistrationActivity.this, BoardActivity.class);
            // optionally pass names:
            i.putExtra("player1", p1);
            i.putExtra("player2", p2);
            i.putExtra("color",playerColor);
            startActivity(i);
            finish();
        });
    }

    // --- THIS IS THE METHOD YOU ARE MISSING ---
    // This method is what *shows* the items in the menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // --- YOU ALSO NEED THIS METHOD ---
    // This method is what *handles the clicks* on the menu items.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_home) {
            // Go to Home
            Intent intent = new Intent(UserRegistrationActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_rules) {
            // Go to Rules
            Intent intent = new Intent(UserRegistrationActivity.this, RulesActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_exit) {
            // Exit the app
            finishAffinity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}