package com.example.chessapp;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
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

    private GridLayout chessboard;
    private ImageView selectedCell = null;
//    private Map<String, Integer> initialPieces;
    private Board board;
    private Square selectedSquare = Square.NONE;

    // This helper function remains the same
    public String gridIndexToChessSquare(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
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
                    // TODO: You could also loop through 'allLegalMoves' again to highlight destination squares
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
                board.doMove(intendedMove);

                // *** THIS IS THE FIX ***
                if (intendedMove.getPromotion() != Piece.NONE) {
                    // A promotion just happened!
                    // The 'intendedMove' object already contains the promotion piece
                    // (e.g., QUEEN), and board.doMove() has already placed it.
                    // You could add a sound effect or log message here.
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

            // Clear any old highlights
            cellView.clearColorFilter();

            // Set the correct piece
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

        // Your initial piece setup remains the same
//        setupInitialPieces();
        board = new Board();
        chessboard = findViewById(R.id.chessboard);

        // We use post() to ensure the layout is measured before we get its width
        chessboard.post(this::createChessBoard);
    }

    private void setupInitialPieces() {
//        initialPieces = new HashMap<>();
//        // White pieces
//        initialPieces.put("6_0", R.drawable.white_pawn);
//        initialPieces.put("6_1", R.drawable.white_pawn);
//        initialPieces.put("6_2", R.drawable.white_pawn);
//        initialPieces.put("6_3", R.drawable.white_pawn);
//        initialPieces.put("6_4", R.drawable.white_pawn);
//        initialPieces.put("6_5", R.drawable.white_pawn);
//        initialPieces.put("6_6", R.drawable.white_pawn);
//        initialPieces.put("6_7", R.drawable.white_pawn);
//        initialPieces.put("7_0", R.drawable.white_rook);
//        initialPieces.put("7_1", R.drawable.white_knight);
//        initialPieces.put("7_2", R.drawable.white_bishop);
//        initialPieces.put("7_3", R.drawable.white_queen);
//        initialPieces.put("7_4", R.drawable.white_king);
//        initialPieces.put("7_5", R.drawable.white_bishop);
//        initialPieces.put("7_6", R.drawable.white_knight);
//        initialPieces.put("7_7", R.drawable.white_rook);
//
//        // Black pieces
//        initialPieces.put("1_0", R.drawable.black_pawn);
//        initialPieces.put("1_1", R.drawable.black_pawn);
//        initialPieces.put("1_2", R.drawable.black_pawn);
//        initialPieces.put("1_3", R.drawable.black_pawn);
//        initialPieces.put("1_4", R.drawable.black_pawn);
//        initialPieces.put("1_5", R.drawable.black_pawn);
//        initialPieces.put("1_6", R.drawable.black_pawn);
//        initialPieces.put("1_7", R.drawable.black_pawn);
//        initialPieces.put("0_0", R.drawable.black_rook);
//        initialPieces.put("0_1", R.drawable.black_knight);
//        initialPieces.put("0_2", R.drawable.black_bishop);
//        initialPieces.put("0_3", R.drawable.black_queen);
//        initialPieces.put("0_4", R.drawable.black_king);
//        initialPieces.put("0_5", R.drawable.black_bishop);
//        initialPieces.put("0_6", R.drawable.black_knight);
//        initialPieces.put("0_7", R.drawable.black_rook);
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