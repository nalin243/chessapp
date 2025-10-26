package com.example.chessapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * AuthManager singleton class for handling user authentication and session management.
 * Manages login/logout operations and maintains user session state using SharedPreferences.
 */
public class AuthManager {
    
    private static final String TAG = "AuthManager";
    
    // SharedPreferences configuration
    private static final String PREFS_NAME = "chess_app_auth";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";
    private static final String KEY_CURRENT_USERNAME = "current_username";
    
    // Singleton instance
    private static AuthManager instance;
    
    // Dependencies
    private Context context;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;
    
    // Current session data
    private User currentUser;
    private boolean isLoggedIn;

    /**
     * Private constructor for singleton pattern.
     * 
     * @param context Application context
     */
    private AuthManager(Context context) {
        this.context = context.getApplicationContext();
        this.sharedPreferences = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.databaseHelper = new DatabaseHelper(this.context);
        
        // Load existing session on initialization
        loadSession();
    }

    /**
     * Gets the singleton instance of AuthManager.
     * 
     * @param context Application context
     * @return AuthManager instance
     */
    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    /**
     * Attempts to log in a user with provided credentials.
     * 
     * @param username The username to authenticate
     * @param password The plain text password
     * @return LoginResult indicating success or failure with message
     */
    public LoginResult login(String username, String password) {
        Log.d(TAG, "Attempting login for username: " + username);
        
        // Validate input parameters
        if (username == null || username.trim().isEmpty()) {
            return new LoginResult(false, "Username cannot be empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            return new LoginResult(false, "Password cannot be empty");
        }
        
        try {
            // Retrieve user from database
            User user = databaseHelper.getUserByUsername(username.trim());
            
            if (user == null) {
                Log.d(TAG, "User not found: " + username);
                return new LoginResult(false, "Invalid username or password");
            }
            
            // Verify password
            if (!PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
                Log.d(TAG, "Password verification failed for user: " + username);
                return new LoginResult(false, "Invalid username or password");
            }
            
            // Login successful - create session
            createSession(user);
            Log.d(TAG, "Login successful for user: " + username);
            
            return new LoginResult(true, "Login successful");
            
        } catch (Exception e) {
            Log.e(TAG, "Error during login process", e);
            return new LoginResult(false, "Login failed due to system error");
        }
    }

    /**
     * Logs out the current user and clears session data.
     */
    public void logout() {
        Log.d(TAG, "Logging out user: " + (currentUser != null ? currentUser.getUsername() : "unknown"));
        
        // Clear session data
        clearSession();
        
        // Reset current user
        currentUser = null;
        isLoggedIn = false;
        
        Log.d(TAG, "Logout completed");
    }

    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return isLoggedIn && currentUser != null;
    }

    /**
     * Gets the currently logged in user.
     * 
     * @return Current User object, or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Gets the username of the currently logged in user.
     * 
     * @return Current username, or null if not logged in
     */
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    /**
     * Gets the user ID of the currently logged in user.
     * 
     * @return Current user ID, or -1 if not logged in
     */
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    /**
     * Validates user credentials against the database without logging in.
     * 
     * @param username The username to validate
     * @param password The plain text password
     * @return true if credentials are valid, false otherwise
     */
    public boolean validateCredentials(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        
        try {
            User user = databaseHelper.getUserByUsername(username.trim());
            return user != null && PasswordUtils.verifyPassword(password, user.getPasswordHash());
        } catch (Exception e) {
            Log.e(TAG, "Error validating credentials", e);
            return false;
        }
    }

    /**
     * Refreshes the current user data from the database.
     * Useful after user statistics are updated.
     */
    public void refreshCurrentUser() {
        if (currentUser != null) {
            try {
                User refreshedUser = databaseHelper.getUserById(currentUser.getId());
                if (refreshedUser != null) {
                    currentUser = refreshedUser;
                    Log.d(TAG, "Current user data refreshed");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error refreshing current user", e);
            }
        }
    }

    /**
     * Creates a new session for the authenticated user.
     * 
     * @param user The authenticated user
     */
    private void createSession(User user) {
        currentUser = user;
        isLoggedIn = true;
        
        // Save session to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_CURRENT_USER_ID, user.getId());
        editor.putString(KEY_CURRENT_USERNAME, user.getUsername());
        editor.apply();
        
        Log.d(TAG, "Session created for user: " + user.getUsername());
    }

    /**
     * Clears the current session data from SharedPreferences.
     */
    private void clearSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        
        Log.d(TAG, "Session data cleared");
    }

    /**
     * Loads existing session data from SharedPreferences.
     */
    private void loadSession() {
        isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        
        if (isLoggedIn) {
            int userId = sharedPreferences.getInt(KEY_CURRENT_USER_ID, -1);
            String username = sharedPreferences.getString(KEY_CURRENT_USERNAME, null);
            
            if (userId != -1 && username != null) {
                try {
                    // Verify user still exists in database
                    currentUser = databaseHelper.getUserById(userId);
                    
                    if (currentUser == null) {
                        // User no longer exists, clear session
                        Log.w(TAG, "Stored user no longer exists, clearing session");
                        clearSession();
                        isLoggedIn = false;
                    } else {
                        Log.d(TAG, "Session restored for user: " + username);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading session", e);
                    clearSession();
                    isLoggedIn = false;
                }
            } else {
                // Invalid session data, clear it
                clearSession();
                isLoggedIn = false;
            }
        }
    }

    /**
     * Inner class representing the result of a login attempt.
     */
    public static class LoginResult {
        private final boolean success;
        private final String message;

        public LoginResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}