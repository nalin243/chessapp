package com.example.chessapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper class extending SQLiteOpenHelper for managing user data.
 * Handles database creation, upgrades, and CRUD operations for users.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String TAG = "DatabaseHelper";
    
    // Database configuration
    private static final String DATABASE_NAME = "chess_app.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table and column names
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD_HASH = "password_hash";
    private static final String COLUMN_WINS = "wins";
    private static final String COLUMN_TOTAL_TIME = "total_time_played";
    private static final String COLUMN_CREATED_AT = "created_at";
    
    // SQL statement for creating users table
    private static final String CREATE_TABLE_USERS = 
        "CREATE TABLE " + TABLE_USERS + " (" +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
        COLUMN_PASSWORD_HASH + " TEXT NOT NULL, " +
        COLUMN_WINS + " INTEGER DEFAULT 0, " +
        COLUMN_TOTAL_TIME + " INTEGER DEFAULT 0, " +
        COLUMN_CREATED_AT + " INTEGER DEFAULT " + System.currentTimeMillis() +
        ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database tables");
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    /**
     * Creates a new user in the database.
     * 
     * @param user The user object to insert
     * @return The row ID of the newly inserted user, or -1 if an error occurred
     */
    public long createUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD_HASH, user.getPasswordHash());
        values.put(COLUMN_WINS, user.getWins());
        values.put(COLUMN_TOTAL_TIME, user.getTotalTimePlayed());
        values.put(COLUMN_CREATED_AT, user.getCreatedAt());
        
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        
        Log.d(TAG, "User created with ID: " + result);
        return result;
    }

    /**
     * Retrieves a user by username.
     * 
     * @param username The username to search for
     * @return User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        
        db.close();
        return user;
    }

    /**
     * Retrieves a user by ID.
     * 
     * @param userId The user ID to search for
     * @return User object if found, null otherwise
     */
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        
        db.close();
        return user;
    }

    /**
     * Updates user statistics (wins and total time played).
     * 
     * @param userId The ID of the user to update
     * @param wins The new win count
     * @param totalTime The new total time played
     * @return Number of rows affected
     */
    public int updateUserStats(int userId, int wins, long totalTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_WINS, wins);
        values.put(COLUMN_TOTAL_TIME, totalTime);
        
        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};
        
        int result = db.update(TABLE_USERS, values, whereClause, whereArgs);
        db.close();
        
        Log.d(TAG, "Updated user stats for ID: " + userId + ", rows affected: " + result);
        return result;
    }

    /**
     * Retrieves all users ordered by wins (descending) and total time (ascending for ties).
     * 
     * @return List of all users sorted for leaderboard display
     */
    public List<User> getAllUsersForLeaderboard() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String orderBy = COLUMN_WINS + " DESC, " + COLUMN_TOTAL_TIME + " ASC";
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, orderBy);
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                users.add(cursorToUser(cursor));
            }
            cursor.close();
        }
        
        db.close();
        return users;
    }

    /**
     * Checks if a username already exists in the database.
     * 
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, selection, selectionArgs, null, null, null);
        
        boolean exists = cursor != null && cursor.getCount() > 0;
        
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        
        return exists;
    }

    /**
     * Converts a database cursor to a User object.
     * 
     * @param cursor The cursor pointing to a user record
     * @return User object created from cursor data
     */
    private User cursorToUser(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
        String passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH));
        int wins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WINS));
        long totalTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_TIME));
        long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT));
        
        return new User(id, username, passwordHash, wins, totalTime, createdAt);
    }

    /**
     * Deletes all users from the database (for testing purposes).
     * 
     * @return Number of rows deleted
     */
    public int deleteAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USERS, null, null);
        db.close();
        
        Log.d(TAG, "Deleted all users, rows affected: " + result);
        return result;
    }
}