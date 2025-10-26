package com.example.chessapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.HorizontalScrollView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class BoardActivity extends AppCompatActivity {

    private TextView whiteTimerText, blackTimerText;
    private android.os.CountDownTimer whiteTimer, blackTimer;
    private long timerDuration = 10000; // default 10s
    private boolean isWhiteTurn = true;

    private GridLayout chessboard;
    private ImageView selectedCell = null;
    //    private Map<String, Integer> initialPieces;
    private Board board;
    private Square selectedSquare = Square.NONE;

    LinearLayout blackMoves;
    LinearLayout whiteMoves;
    HorizontalScrollView blackScrollView;
    HorizontalScrollView whiteScrollView;

    int moveRecordLimit = 10,movesRecorded=0;//default is 10

    String playerWhiteName,playerBlackName;

    private void startWhiteTimer() {
        cancelTimers();
        isWhiteTurn = true;
        whiteTimer = new android.os.CountDownTimer(timerDuration, 1000) {
            public void onTick(long millisUntilFinished) {
                whiteTimerText.setText("⏱ " + millisUntilFinished / 1000 + "s");
            }
            public void onFinish() {
                Toast.makeText(BoardActivity.this, "White's time over! Black wins!", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    private void startBlackTimer() {
        cancelTimers();
        isWhiteTurn = false;
        blackTimer = new android.os.CountDownTimer(timerDuration, 1000) {
            public void onTick(long millisUntilFinished) {
                blackTimerText.setText("⏱ " + millisUntilFinished / 1000 + "s");
            }
            public void onFinish() {
                Toast.makeText(BoardActivity.this, "Black's time over! White wins!", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    private void cancelTimers() {
        if (whiteTimer != null) whiteTimer.cancel();
        if (blackTimer != null) blackTimer.cancel();
    }


    private void showCustomizationMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.customization_menu, popup.getMenu());

        final int menu_timer_id = R.id.menu_timer;
        final int menu_move_record_limit_id = R.id.menu_move_record_limit;
        final int menu_clear_moves = R.id.menu_clear_moves;
        final int menu_restart_game_id = R.id.menu_restart_game;
        final int menu_exit_game_id = R.id.menu_exit_game;

        popup.setOnMenuItemClickListener(item -> {

            if(item.getItemId()==menu_timer_id) {
                showTimerDialog();
                return true;
            }
            else if(item.getItemId()==menu_move_record_limit_id) {
                showMoveRecordDialog();
                return true;
            }
            else if(item.getItemId()==menu_clear_moves) {
                clearMoveRecords();
                return true;
            }
            else if (item.getItemId() == menu_restart_game_id) {
                restartGame();
                return true;
            }
            else if (item.getItemId() == menu_exit_game_id) {
                exitGame();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void showTimerDialog() {
        String[] options = {"10s", "20s", "30s"};
        final long[] durations = {10000, 20000, 30000};
        new AlertDialog.Builder(this)
                .setTitle("Select Timer Duration")
                .setItems(options, (dialog, which) -> {
                    timerDuration = durations[which];
                    Toast.makeText(this, "Timer set to " + options[which], Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showMoveRecordDialog() {
        String[] options = {"Last 10", "Last 20", "Last 30"};
        new AlertDialog.Builder(this)
                .setTitle("Select Move Record Limit")
                .setItems(options, (dialog, which) -> {
                    if(options[which].equals("Last 10")){
                        moveRecordLimit = 10;
                    }
                    else if(options[which].equals("Last 20")){
                        moveRecordLimit = 20;
                    }
                    else if(options[which].equals("Last 30")){
                        moveRecordLimit = 30;
                    }
                    Toast.makeText(this, "Move record limit set to " + options[which], Toast.LENGTH_SHORT).show();
                })
                .show();
    }


    private void clearMoveRecords() {
        LinearLayout blackMoves = findViewById(R.id.black_moves_container);
        LinearLayout whiteMoves = findViewById(R.id.white_moves_container);

        blackMoves.removeAllViews();
        whiteMoves.removeAllViews();

        Toast.makeText(this, "Move records cleared", Toast.LENGTH_SHORT).show();
    }

    private void restartGame() {
        board = new Board();
        clearMoveRecords();
        movesRecorded = 0;
        syncBoardWithUI();
        cancelTimers();
        startWhiteTimer();

        Toast.makeText(this, "Game Restarted", Toast.LENGTH_SHORT).show();
    }

    private void exitGame() {
        cancelTimers();
        finish();
    }



// This handler function will now work correctly
    private void handleCellClick(ImageView clickedCell) {
        Square clickedSquare = (Square) clickedCell.getTag();

        // Get ALL legal moves for the current player
        List<Move> allLegalMoves = board.legalMoves();

        if (selectedSquare == Square.NONE) {
            // --- First Click (Select Piece) ---
            Piece piece = board.getPiece(clickedSquare);

            // Check if a piece was clicked and it's the correct player's turn
            if (piece != Piece.NONE && piece.getPieceSide() == board.getSideToMove()) {

                // Check if this piece has any legal moves
                boolean hasLegalMoves = false;
                for (Move move : allLegalMoves) {
                    if (move.getFrom().equals(clickedSquare)) {
                        hasLegalMoves = true;
                        break;
                    }
                }

                if (hasLegalMoves) {
                    selectedSquare = clickedSquare;
                    // Highlight selected square
                    clickedCell.setColorFilter(0x9900FF00, android.graphics.PorterDuff.Mode.SRC_ATOP);
                    for (Move move : allLegalMoves) {
                        if (move.getFrom().equals(clickedSquare)) {
                            Square toSquare = move.getTo();

                            // Loop through all 64 cells to find the matching ImageView
                            for (int i = 0; i < chessboard.getChildCount(); i++) {
                                ImageView cell = (ImageView) chessboard.getChildAt(i);
                                Square cellSquare = (Square) cell.getTag();
                                if (cellSquare.equals(toSquare)) {
                                    cell.setBackgroundColor(Color.argb(150, 255, 255, 0)); // translucent yellow overlay
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // --- Second Click (Try to Move) ---

            // Find the move in the legal move list
            Move intendedMove = null;
            for (Move move : allLegalMoves) {
                if (move.getFrom().equals(selectedSquare) && move.getTo().equals(clickedSquare)) {

                    // This logic handles the case where there are multiple promotion
                    // options (e.g., to Queen, Rook, etc.) for the same square.
                    // We will default to Queen, which is what the library does.
                    // If you want to ask the user, you'd do it here.
                    intendedMove = move;

                    // If the move is a promotion, prefer the Queen
                    if (move.getPromotion() == Piece.WHITE_QUEEN || move.getPromotion() == Piece.BLACK_QUEEN) {
                        break; // Found the best (Queen) promotion, stop searching
                    }
                }
            }

            if (intendedMove != null) {
                // --- This is a LEGAL move ---

                if( ((board.getSideToMove()).toString()).equalsIgnoreCase("BLACK") ){
                    if(movesRecorded<moveRecordLimit) {
                        TextView move = new TextView(this);
                        move.setText(" "+intendedMove.toString());
                        move.setTextColor(Color.WHITE);
                        blackMoves.addView(move);
                        blackScrollView.post(() -> blackScrollView.fullScroll(View.FOCUS_RIGHT));
                        movesRecorded++;
                    }
                }
                else{
                    TextView move = new TextView(this);
                    if(movesRecorded<moveRecordLimit) {
                        move.setText(" "+intendedMove.toString());
                        move.setTextColor(Color.WHITE);
                        whiteMoves.addView(move);
                        whiteScrollView.post(() -> whiteScrollView.fullScroll(View.FOCUS_RIGHT));
                        movesRecorded++;
                    }
                }

                board.doMove(intendedMove);
                if (isWhiteTurn) startBlackTimer();
                else startWhiteTimer();

                // --- Check for special conditions ---
                if (board.isMated()) {
                    String winnerColor = (board.getSideToMove().flip()).toString();
                    String winnerPlayerName;
                    if(winnerColor.equalsIgnoreCase("BLACK")){
                        winnerPlayerName = playerBlackName;
                    } else {
                        winnerPlayerName = playerWhiteName;
                    }
                    Toast.makeText(this, "Checkmate! " + winnerPlayerName + " wins!", Toast.LENGTH_LONG).show();
                    //****ADD CODE TO ADD WINNER TO DATABASE****
                }
                else if (board.isKingAttacked()) {
                    Toast.makeText(this, "Check!", Toast.LENGTH_SHORT).show();
                }
                else if (board.isDraw()) {
                    Toast.makeText(this, "Game drawn!", Toast.LENGTH_LONG).show();
                }

                // --- Promotion check (optional logging) ---
                if (intendedMove.getPromotion() != Piece.NONE) {
                    // A promotion just happened
                }
            }


            // --- Illegal move OR deselecting ---
            selectedSquare = Square.NONE;
            syncBoardWithUI(); // Update UI and clear all highlights
        }
    }

    private void syncBoardWithUI() {
        for (int i = 0; i < chessboard.getChildCount(); i++) {
            ImageView cellView = (ImageView) chessboard.getChildAt(i);
            Square square = (Square) cellView.getTag();
            Piece piece = board.getPiece(square);

            // --- Determine the square's original color ---
            int index = i; // position in GridLayout
            int row = index / 8;
            int col = index % 8;
            boolean isLightSquare = (row + col) % 2 == 0;

            int lightColor = Color.parseColor("#e8dda9");
            int darkColor = Color.parseColor("#649962");

            // --- Restore original background color ---
            cellView.setBackgroundColor(isLightSquare ? lightColor : darkColor);

            // --- Clear old tint from pieces ---
            cellView.clearColorFilter();

            // --- Update the piece image ---
            if (piece != Piece.NONE) {
                cellView.setImageResource(getDrawableIdForPiece(piece));
            } else {
                cellView.setImageDrawable(null);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // This can sometimes interfere with layout, enable if needed
        setContentView(R.layout.activity_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView settingsButton = findViewById(R.id.settingIcon);
        settingsButton.setOnClickListener(v -> showCustomizationMenu(v));

        blackMoves = findViewById(R.id.black_moves_container);
        whiteMoves = findViewById(R.id.white_moves_container);
        blackScrollView = findViewById(R.id.black_scroll_view);
        whiteScrollView = findViewById(R.id.white_scroll_view);

        Intent i = getIntent();
        String player1 = i.getStringExtra("player1");
        String player2 = i.getStringExtra("player2");
        String player1Color = i.getStringExtra("color");

        whiteTimerText = findViewById(R.id.white_timer);
        blackTimerText = findViewById(R.id.black_timer);
        startWhiteTimer();

        TextView playerwhitetext = findViewById(R.id.player_white_name);
        TextView playerblacktext = findViewById(R.id.player_black_name);

        if(player1Color.equals("Black")) {
            playerwhitetext.setText("Player White: "+player2);
            playerblacktext.setText("Player Black: "+player1);
            playerBlackName = player1;
            playerWhiteName = player2;

        } else {
            playerwhitetext.setText("Player White: "+player1);
            playerblacktext.setText("Player Black: "+player2);
            playerWhiteName = player1;
            playerBlackName = player2;
        }


        board = new Board();
        chessboard = findViewById(R.id.chessboard);

        // We use post() to ensure the layout is measured before we get its width
        chessboard.post(this::createChessBoard);
    }

    private void createChessBoard() {
        // Colors from the image provided
        int lightColor = Color.parseColor("#e8dda9");
        int darkColor = Color.parseColor("#649962");

        int boardSize = chessboard.getWidth();
        int tileSize = boardSize / 8;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // *** KEY CHANGE: Create ONLY ONE ImageView per cell ***
                ImageView cellView = new ImageView(this);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = tileSize;
                params.height = tileSize;
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                cellView.setLayoutParams(params);

                // Set background color for the square
                boolean isLightSquare = (row + col) % 2 == 0;
                cellView.setBackgroundColor(isLightSquare ? lightColor : darkColor);

                int index = (7 - row) * 8 + col;
                Square square = Square.squareAt(index);
                Piece piece = board.getPiece(square);

                // Check for a piece and set it as the image, not as a new View
                //String key = row + "_" + col;
                //Integer pieceDrawableId = initialPieces.get(key);
                if (piece != Piece.NONE) {
                    cellView.setImageResource(getDrawableIdForPiece(piece)); // You'll need to write this helper
                    cellView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    cellView.setPadding(8, 8, 8, 8);
                }

                // Set click listener
                cellView.setTag(square);
                cellView.setOnClickListener(v -> handleCellClick((ImageView) v));
                chessboard.addView(cellView);
            }
        }
    }
    // Helper method you need to create
    private int getDrawableIdForPiece(Piece piece) {
        switch (piece) {
            case WHITE_PAWN: return R.drawable.white_pawn;
            case WHITE_ROOK: return R.drawable.white_rook;
            case WHITE_BISHOP: return R.drawable.white_bishop;
            case WHITE_KNIGHT: return R.drawable.white_knight;
            case WHITE_QUEEN: return  R. drawable.white_queen;
            case WHITE_KING: return  R.drawable.white_king;
            case BLACK_PAWN: return R.drawable.black_pawn;
            case BLACK_ROOK: return R.drawable.black_rook;
            case BLACK_BISHOP: return R.drawable.black_bishop;
            case BLACK_KNIGHT: return R.drawable.black_knight;
            case BLACK_QUEEN: return R.drawable.black_queen;
            case BLACK_KING: return R.drawable.black_king;
            default: return 0; // Or null
        }
    }
}