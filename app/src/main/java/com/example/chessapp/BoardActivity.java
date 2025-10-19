package com.example.chessapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.Map;

public class BoardActivity extends AppCompatActivity {

    GridLayout chessboard;
    private ImageView selectedCell = null;
    private Map<String, Integer> initialPieces;
    private String currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applyTheme(this); // Apply saved theme
        setContentView(R.layout.activity_board);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chess Board");

        // Load saved theme
        currentTheme = getSharedPreferences("theme_pref", MODE_PRIVATE)
                .getString("selected_theme", "default");

        chessboard = findViewById(R.id.chessboard);
        initializePieces();

        // Draw board after layout is ready
        chessboard.post(() -> drawChessBoard(currentTheme));
    }

    private void initializePieces() {
        initialPieces = new HashMap<>();

        // White pawns
        for (int i = 0; i < 8; i++) initialPieces.put("6_" + i, R.drawable.white_pawn);

        // White major pieces
        initialPieces.put("7_0", R.drawable.white_rook);
        initialPieces.put("7_1", R.drawable.white_knight);
        initialPieces.put("7_2", R.drawable.white_bishop);
        initialPieces.put("7_3", R.drawable.white_queen);
        initialPieces.put("7_4", R.drawable.white_king);
        initialPieces.put("7_5", R.drawable.white_bishop);
        initialPieces.put("7_6", R.drawable.white_knight);
        initialPieces.put("7_7", R.drawable.white_rook);

        // Black pawns
        for (int i = 0; i < 8; i++) initialPieces.put("1_" + i, R.drawable.black_pawn);

        // Black major pieces
        initialPieces.put("0_0", R.drawable.black_rook);
        initialPieces.put("0_1", R.drawable.black_knight);
        initialPieces.put("0_2", R.drawable.black_bishop);
        initialPieces.put("0_3", R.drawable.black_queen);
        initialPieces.put("0_4", R.drawable.black_king);
        initialPieces.put("0_5", R.drawable.black_bishop);
        initialPieces.put("0_6", R.drawable.black_knight);
        initialPieces.put("0_7", R.drawable.black_rook);
    }

    private void drawChessBoard(String theme) {
        chessboard.removeAllViews();
        int size = chessboard.getWidth() / 8;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean isWhite = (row + col) % 2 == 0;

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = size;
                params.height = size;
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);

                ImageView square = new ImageView(this);
                square.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                square.setAdjustViewBounds(true);

                if ("amber".equalsIgnoreCase(theme)) {
                    // Amber Classic Theme (balanced warm contrast)
                    square.setBackgroundColor(isWhite
                            ? Color.parseColor("#E5C29F")   // light amber
                            : Color.parseColor("#8B5A2B")); // dark bronze brown
                } else {
                    // Mystic Silver Theme (cool modern tone)
                    square.setBackgroundColor(isWhite
                            ? Color.parseColor("#3C4A53")   // smoky slate
                            : Color.parseColor("#1F262B")); // deep metallic gray
                }

                String key = row + "_" + col;
                Integer pieceDrawableId = initialPieces.get(key);
                if (pieceDrawableId != null) {
                    square.setImageResource(pieceDrawableId);
                }

                square.setOnClickListener(v -> handleCellClick((ImageView) v));
                chessboard.addView(square, params);
            }
        }
    }

    private void handleCellClick(ImageView clickedCell) {
        if (selectedCell == null) {
            if (clickedCell.getDrawable() != null) {
                selectedCell = clickedCell;
                selectedCell.setColorFilter(0x7700FF00); // highlight green
            }
        } else {
            if (clickedCell == selectedCell) {
                selectedCell.clearColorFilter();
                selectedCell = null;
            } else {
                Drawable pieceToMove = selectedCell.getDrawable();
                selectedCell.setImageDrawable(null);
                selectedCell.clearColorFilter();
                clickedCell.setImageDrawable(pieceToMove);
                selectedCell = null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board, menu);

        // Add toggle to toolbar
        MenuItem toggleItem = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, "Theme");
        toggleItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        ToggleButton themeToggle = new ToggleButton(this);
        themeToggle.setTextOn("AMBER MODE");
        themeToggle.setTextOff("MYSTIC MODE");
        themeToggle.setChecked("amber".equalsIgnoreCase(currentTheme));
        themeToggle.setTextColor(Color.WHITE);
        themeToggle.setTextSize(12);
        themeToggle.setAllCaps(true);
        themeToggle.setPadding(16, 8, 16, 8);
        themeToggle.setBackgroundResource(R.drawable.toggle_button_style);
        themeToggle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // subtle shadow for text
        themeToggle.setShadowLayer(4, 2, 2, Color.BLACK);

        // animation on change
        themeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonView.animate().rotationYBy(360).setDuration(600).start();
            currentTheme = isChecked ? "amber" : "mystic";
            ThemeHelper.setTheme(this, currentTheme);
            drawChessBoard(currentTheme);
        });

        toggleItem.setActionView(themeToggle);


        themeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentTheme = isChecked ? "amber" : "mystic";
            ThemeHelper.setTheme(this, currentTheme);
            drawChessBoard(currentTheme);
        });



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
