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
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        etPlayer1Name = findViewById(R.id.etPlayer1Name);
        etPlayer2Name = findViewById(R.id.etPlayer2Name);
        radioGroupColor = findViewById(R.id.radioGroupColor);
        btnStartGame = findViewById(R.id.btnStartGame);

        // Initialize AuthManager
        authManager = AuthManager.getInstance(this);

        // This code sets up the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Setup player 1 field based on authentication status
        setupPlayer1Field();

        btnStartGame.setOnClickListener(v -> {
            if (!validateInputs()) {
                return;
            }

            String p1 = etPlayer1Name.getText().toString().trim();
            String p2 = etPlayer2Name.getText().toString().trim();
            
            // Save to SharedPreferences (optional) for display on board/leaderboard:
            getSharedPreferences("players", MODE_PRIVATE).edit()
                    .putString("player1", p1).putString("player2", p2).apply();

            RadioButton selectedColorButton = (findViewById(radioGroupColor.getCheckedRadioButtonId()));
            String playerColor = selectedColorButton.getText().toString();

            // Start BoardActivity
            Intent i = new Intent(UserRegistrationActivity.this, BoardActivity.class);
            // Pass player names and authentication status
            i.putExtra("player1", p1);
            i.putExtra("player2", p2);
            i.putExtra("color", playerColor);
            i.putExtra("isPlayer1Registered", authManager.isLoggedIn());
            startActivity(i);
            finish();
        });
    }

    /**
     * Sets up the Player 1 name field based on authentication status.
     * If user is logged in, disables the field and auto-populates with username.
     */
    private void setupPlayer1Field() {
        if (authManager.isLoggedIn()) {
            // User is logged in - auto-populate and disable Player 1 field
            String currentUsername = authManager.getCurrentUsername();
            etPlayer1Name.setText(currentUsername);
            etPlayer1Name.setEnabled(false);
            etPlayer1Name.setFocusable(false);
            etPlayer1Name.setClickable(false);
            etPlayer1Name.setHint("Logged in as: " + currentUsername);
            
            // Update the hint color to indicate it's disabled
            etPlayer1Name.setTextColor(getResources().getColor(android.R.color.white));
            etPlayer1Name.setAlpha(0.7f); // Make it slightly transparent to show it's disabled
        } else {
            // User is not logged in - enable normal input
            etPlayer1Name.setEnabled(true);
            etPlayer1Name.setFocusable(true);
            etPlayer1Name.setClickable(true);
            etPlayer1Name.setHint("Enter Player 1 Name");
            etPlayer1Name.setAlpha(1.0f);
        }
    }

    /**
     * Validates user inputs before starting the game.
     * 
     * @return true if inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        String p1 = etPlayer1Name.getText().toString().trim();
        String p2 = etPlayer2Name.getText().toString().trim();

        // Validate Player 1 name
        if (p1.isEmpty()) {
            Toast.makeText(this, "Player 1 name cannot be empty", Toast.LENGTH_SHORT).show();
            if (etPlayer1Name.isEnabled()) {
                etPlayer1Name.requestFocus();
            }
            return false;
        }

        // Validate Player 2 name
        if (p2.isEmpty()) {
            Toast.makeText(this, "Player 2 name cannot be empty", Toast.LENGTH_SHORT).show();
            etPlayer2Name.requestFocus();
            return false;
        }

        // Check if both players have the same name
        if (p1.equalsIgnoreCase(p2)) {
            Toast.makeText(this, "Player names must be different", Toast.LENGTH_SHORT).show();
            etPlayer2Name.requestFocus();
            return false;
        }

        // Validate color selection
        if (radioGroupColor.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a color for Player 1", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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