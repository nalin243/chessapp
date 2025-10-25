package com.example.chessapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// --- ADD THIS IMPORT ---
import androidx.appcompat.widget.Toolbar;


public class GameModeActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Button btnPlayComputer = findViewById(R.id.btnPlayComputer);
        Button btnMultiplayer = findViewById(R.id.btnMultiplayer);

        // Click listener for "Play with Computer"
        btnPlayComputer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Create and start your PlayWithComputerActivity
                // Intent intent = new Intent(GameModeActivity.this, PlayWithComputerActivity.class);
                // startActivity(intent);
            }
        });

        // Click listener for "Multiplayer"
        btnMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameModeActivity.this, UserRegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_home) {
            Intent intent = new Intent(GameModeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_rules) {
            Intent intent = new Intent(GameModeActivity.this, RulesActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_exit) {
            finishAffinity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}