package com.example.chessapp;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

public class BoardActivity extends AppCompatActivity {

    GridLayout chessboard;
    private ImageView selectedCell = null;
    private Map<String, Integer> initialPieces;

    public String gridIndexToChessSquare(int row, int col) {
        char file = (char) ('a' + col);       // a–h
        int rank = 8 - row;                   // 8–1 (top to bottom)
        return "" + file + rank;
    }

    private void handleCellClick(ImageView clickedCell) {
        //handler function to move the pieces
        if (selectedCell == null) {
            // First click — select a piece only if the cell has one
            if (clickedCell.getDrawable() != null) {
                selectedCell = clickedCell;
                selectedCell.setColorFilter(0x7700FF00); // green highlight
            }
        } else {
            // Second click — either move or cancel selection

            if (clickedCell == selectedCell) {
                // Deselect
                selectedCell.clearColorFilter();
                selectedCell = null;
            } else {
                // Move piece to the new cell
                Drawable pieceToMove = selectedCell.getDrawable();
                selectedCell.setImageDrawable(null);
                selectedCell.clearColorFilter();

                clickedCell.setImageDrawable(pieceToMove);
                selectedCell = null;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initialPieces = new HashMap<>();

        // White pawns row 6
        initialPieces.put("6_0", R.drawable.white_pawn);
        initialPieces.put("6_1", R.drawable.white_pawn);
        initialPieces.put("6_2", R.drawable.white_pawn);
        initialPieces.put("6_3", R.drawable.white_pawn);
        initialPieces.put("6_4", R.drawable.white_pawn);
        initialPieces.put("6_5", R.drawable.white_pawn);
        initialPieces.put("6_6", R.drawable.white_pawn);
        initialPieces.put("6_7", R.drawable.white_pawn);

        // White major pieces row 7
        initialPieces.put("7_0", R.drawable.white_rook);
        initialPieces.put("7_1", R.drawable.white_knight);
        initialPieces.put("7_2", R.drawable.white_bishop);
        initialPieces.put("7_3", R.drawable.white_queen);
        initialPieces.put("7_4", R.drawable.white_king);
        initialPieces.put("7_5", R.drawable.white_bishop);
        initialPieces.put("7_6", R.drawable.white_knight);
        initialPieces.put("7_7", R.drawable.white_rook);

        // Black pawns row 1
        initialPieces.put("1_0", R.drawable.black_pawn);
        initialPieces.put("1_1", R.drawable.black_pawn);
        initialPieces.put("1_2", R.drawable.black_pawn);
        initialPieces.put("1_3", R.drawable.black_pawn);
        initialPieces.put("1_4", R.drawable.black_pawn);
        initialPieces.put("1_5", R.drawable.black_pawn);
        initialPieces.put("1_6", R.drawable.black_pawn);
        initialPieces.put("1_7", R.drawable.black_pawn);

        // Black major pieces row 0
        initialPieces.put("0_0", R.drawable.black_rook);
        initialPieces.put("0_1", R.drawable.black_knight);
        initialPieces.put("0_2", R.drawable.black_bishop);
        initialPieces.put("0_3", R.drawable.black_queen);
        initialPieces.put("0_4", R.drawable.black_king);
        initialPieces.put("0_5", R.drawable.black_bishop);
        initialPieces.put("0_6", R.drawable.black_knight);
        initialPieces.put("0_7", R.drawable.black_rook);

        chessboard = findViewById(R.id.chessboard);
        chessboard.post(new Runnable() {//using this as width is not immediately available in onCreate
            @Override
            public void run() {
                int widthInPixels = chessboard.getWidth();
                int tileSize = widthInPixels/8;

                int counter = 0;

                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        final int index = counter;

                        boolean isWhite = (row + col) % 2 == 0;

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = tileSize;
                        params.height = tileSize;
                        params.rowSpec = GridLayout.spec(row);
                        params.columnSpec = GridLayout.spec(col);

                        ImageView emptyview = new ImageView(getApplicationContext());
                        emptyview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        emptyview.setAdjustViewBounds(true);

                        // No background, no image - fully empty square
                        emptyview.setBackgroundColor(isWhite ? Color.WHITE : Color.DKGRAY);
                        emptyview.setImageDrawable(null);

                        emptyview.setOnClickListener(v -> handleCellClick((ImageView) v) );
                        chessboard.addView(emptyview,params);

                        String key = row + "_" + col;
                        Integer pieceDrawableId = initialPieces.get(key);
                        if (pieceDrawableId != null) {
                            //adding the pieces
                            ImageView pieceImage = new ImageView(getApplicationContext());
                            pieceImage.setImageResource(pieceDrawableId);
                            pieceImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                            GridLayout.LayoutParams pieceParams = new GridLayout.LayoutParams();
                            pieceParams.width = tileSize;
                            pieceParams.height = tileSize;
                            pieceParams.rowSpec = GridLayout.spec(row);
                            pieceParams.columnSpec = GridLayout.spec(col);

                            pieceImage.setOnClickListener(v -> handleCellClick((ImageView) v) );

                            chessboard.addView(pieceImage, pieceParams);
                            counter++;
                        }
                    }
                }
            }
        });
    }
}
