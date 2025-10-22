package com.example.chessapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class LeaderboardActivity extends AppCompatActivity {

    private LinearLayout leaderboardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView title = findViewById(R.id.toolbar_title_leaderboard);
        title.setText("LEADERBOARD");


        leaderboardContainer = findViewById(R.id.leaderboardContainer);

        // Inflate sample leaderboard entries
        populateLeaderboard();
    }

    private void populateLeaderboard() {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 1; i <= 10; i++) {
            View item = inflater.inflate(R.layout.item_leaderboard, leaderboardContainer, false);

            // Set data dynamically here if connected to a database
            // Example:
            // TextView title = item.findViewById(R.id.textMatchTitle);
            // title.setText("Player " + i + " vs Player " + (i+1));

            leaderboardContainer.addView(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_leaderboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;

        } else if (id == R.id.action_rules) {
            startActivity(new Intent(this, RulesActivity.class));
            return true;

        } else if (id == R.id.action_exit) {
            finishAffinity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
