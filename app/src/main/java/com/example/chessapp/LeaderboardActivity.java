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
import android.widget.ImageView;
import android.util.Log;

import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private static final String TAG = "LeaderboardActivity";
    
    private LinearLayout leaderboardContainer;
    private DatabaseHelper databaseHelper;
    private AuthManager authManager;

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
        
        // Initialize database helper and auth manager
        databaseHelper = new DatabaseHelper(this);
        authManager = AuthManager.getInstance(this);

        // Load leaderboard data from database
        populateLeaderboard();
    }

    private void populateLeaderboard() {
        try {
            // Clear existing views
            leaderboardContainer.removeAllViews();
            
            // Get all users sorted for leaderboard (by wins desc, then by time asc)
            List<User> users = databaseHelper.getAllUsersForLeaderboard();
            
            if (users.isEmpty()) {
                // Show message when no users exist
                showEmptyLeaderboard();
                return;
            }
            
            // Get current logged-in user for highlighting
            User currentUser = authManager.getCurrentUser();
            
            LayoutInflater inflater = LayoutInflater.from(this);
            
            // Create leaderboard entries
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                View item = inflater.inflate(R.layout.item_leaderboard, leaderboardContainer, false);
                
                populateLeaderboardItem(item, user, i + 1, currentUser);
                leaderboardContainer.addView(item);
            }
            
            Log.d(TAG, "Leaderboard populated with " + users.size() + " users");
            
        } catch (Exception e) {
            Log.e(TAG, "Error populating leaderboard", e);
            showErrorMessage();
        }
    }
    
    private void populateLeaderboardItem(View item, User user, int rank, User currentUser) {
        TextView textRank = item.findViewById(R.id.textRank);
        TextView textUsername = item.findViewById(R.id.textUsername);
        TextView textStats = item.findViewById(R.id.textStats);
        ImageView imageTrophy = item.findViewById(R.id.imageTrophy);
        
        // Set rank
        textRank.setText(String.valueOf(rank));
        
        // Set username
        textUsername.setText(user.getUsername());
        
        // Format and set statistics
        String timeFormatted = formatTime(user.getTotalTimePlayed());
        String statsText = "Wins: " + user.getWins() + " | Time: " + timeFormatted;
        textStats.setText(statsText);
        
        // Highlight current user
        if (currentUser != null && currentUser.getId() == user.getId()) {
            textUsername.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
            textStats.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
        }
        
        // Show trophy for top 3 players
        if (rank <= 3 && user.getWins() > 0) {
            imageTrophy.setVisibility(View.VISIBLE);
            // Different trophy colors for different ranks
            switch (rank) {
                case 1:
                    imageTrophy.setImageResource(R.drawable.white_king); // Gold
                    break;
                case 2:
                    imageTrophy.setImageResource(R.drawable.white_queen); // Silver
                    break;
                case 3:
                    imageTrophy.setImageResource(R.drawable.white_rook); // Bronze
                    break;
            }
        } else {
            imageTrophy.setVisibility(View.GONE);
        }
    }
    
    private void showEmptyLeaderboard() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View emptyView = inflater.inflate(R.layout.item_leaderboard, leaderboardContainer, false);
        
        TextView textRank = emptyView.findViewById(R.id.textRank);
        TextView textUsername = emptyView.findViewById(R.id.textUsername);
        TextView textStats = emptyView.findViewById(R.id.textStats);
        ImageView imageTrophy = emptyView.findViewById(R.id.imageTrophy);
        
        textRank.setText("-");
        textUsername.setText("No players yet");
        textStats.setText("Start playing to see rankings!");
        imageTrophy.setVisibility(View.GONE);
        
        leaderboardContainer.addView(emptyView);
    }
    
    private void showErrorMessage() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View errorView = inflater.inflate(R.layout.item_leaderboard, leaderboardContainer, false);
        
        TextView textRank = errorView.findViewById(R.id.textRank);
        TextView textUsername = errorView.findViewById(R.id.textUsername);
        TextView textStats = errorView.findViewById(R.id.textStats);
        ImageView imageTrophy = errorView.findViewById(R.id.imageTrophy);
        
        textRank.setText("!");
        textUsername.setText("Error loading leaderboard");
        textStats.setText("Please try again later");
        imageTrophy.setVisibility(View.GONE);
        
        leaderboardContainer.addView(errorView);
    }
    
    private String formatTime(long totalTimeMillis) {
        if (totalTimeMillis <= 0) {
            return "0:00";
        }
        
        long totalSeconds = totalTimeMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh leaderboard data when activity is resumed
        populateLeaderboard();
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
