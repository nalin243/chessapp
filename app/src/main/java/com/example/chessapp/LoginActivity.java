package com.example.chessapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * LoginActivity handles user authentication and registration.
 * Provides UI for login and registration with input validation and error handling.
 */
public class LoginActivity extends AppCompatActivity {
    
    private static final String TAG = "LoginActivity";
    
    // UI components
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvError;
    private Button btnLogin;
    private Button btnRegister;
    
    // Dependencies
    private AuthManager authManager;
    private DatabaseHelper databaseHelper;
    
    // State
    private boolean isRegistrationMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Initialize dependencies
        authManager = AuthManager.getInstance(this);
        databaseHelper = new DatabaseHelper(this);
        
        // Check if user is already logged in
        if (authManager.isLoggedIn()) {
            navigateToMainActivity();
            return;
        }
        
        // Initialize UI components
        initializeViews();
        setupClickListeners();
        
        Log.d(TAG, "LoginActivity initialized");
    }

    /**
     * Initializes all UI components.
     */
    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
    }

    /**
     * Sets up click listeners for buttons.
     */
    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleLoginClick());
        btnRegister.setOnClickListener(v -> handleRegisterClick());
    }

    /**
     * Handles login button click.
     * Validates input and attempts to authenticate user.
     */
    private void handleLoginClick() {
        if (isRegistrationMode) {
            performRegistration();
        } else {
            performLogin();
        }
    }

    /**
     * Handles register button click.
     * Toggles between login and registration modes.
     */
    private void handleRegisterClick() {
        if (isRegistrationMode) {
            switchToLoginMode();
        } else {
            switchToRegistrationMode();
        }
    }

    /**
     * Performs user login with input validation.
     */
    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();
        
        // Validate input
        if (!validateLoginInput(username, password)) {
            return;
        }
        
        // Clear previous error
        hideError();
        
        // Attempt login
        AuthManager.LoginResult result = authManager.login(username, password);
        
        if (result.isSuccess()) {
            Log.d(TAG, "Login successful for user: " + username);
            Toast.makeText(this, "Welcome back, " + username + "!", Toast.LENGTH_SHORT).show();
            navigateToMainActivity();
        } else {
            Log.d(TAG, "Login failed: " + result.getMessage());
            showError(result.getMessage());
        }
    }

    /**
     * Performs user registration with input validation and duplicate checking.
     */
    private void performRegistration() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();
        
        // Validate input
        if (!validateRegistrationInput(username, password)) {
            return;
        }
        
        // Clear previous error
        hideError();
        
        try {
            // Check if username already exists
            if (databaseHelper.isUsernameExists(username)) {
                showError("Username already exists. Please choose a different username.");
                return;
            }
            
            // Create new user
            String passwordHash = PasswordUtils.hashPassword(password);
            User newUser = new User(username, passwordHash);
            
            long userId = databaseHelper.createUser(newUser);
            
            if (userId != -1) {
                Log.d(TAG, "Registration successful for user: " + username);
                Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
                
                // Switch back to login mode and clear fields
                switchToLoginMode();
                clearFields();
            } else {
                showError("Registration failed. Please try again.");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error during registration", e);
            showError("Registration failed due to system error.");
        }
    }

    /**
     * Validates login input fields.
     * 
     * @param username The username to validate
     * @param password The password to validate
     * @return true if input is valid, false otherwise
     */
    private boolean validateLoginInput(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            showError("Please enter your username");
            etUsername.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(password)) {
            showError("Please enter your password");
            etPassword.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * Validates registration input fields with additional requirements.
     * 
     * @param username The username to validate
     * @param password The password to validate
     * @return true if input is valid, false otherwise
     */
    private boolean validateRegistrationInput(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            showError("Please enter a username");
            etUsername.requestFocus();
            return false;
        }
        
        if (username.length() < 3) {
            showError("Username must be at least 3 characters long");
            etUsername.requestFocus();
            return false;
        }
        
        if (username.length() > 20) {
            showError("Username must be less than 20 characters");
            etUsername.requestFocus();
            return false;
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError("Username can only contain letters, numbers, and underscores");
            etUsername.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(password)) {
            showError("Please enter a password");
            etPassword.requestFocus();
            return false;
        }
        
        if (password.length() < 4) {
            showError("Password must be at least 4 characters long");
            etPassword.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * Switches UI to registration mode.
     */
    private void switchToRegistrationMode() {
        isRegistrationMode = true;
        btnLogin.setText("REGISTER");
        btnRegister.setText("BACK TO LOGIN");
        hideError();
        clearFields();
        
        Log.d(TAG, "Switched to registration mode");
    }

    /**
     * Switches UI to login mode.
     */
    private void switchToLoginMode() {
        isRegistrationMode = false;
        btnLogin.setText("LOGIN");
        btnRegister.setText("REGISTER");
        hideError();
        
        Log.d(TAG, "Switched to login mode");
    }

    /**
     * Shows error message to user.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    /**
     * Hides error message.
     */
    private void hideError() {
        tvError.setVisibility(View.GONE);
        tvError.setText("");
    }

    /**
     * Clears input fields.
     */
    private void clearFields() {
        etUsername.setText("");
        etPassword.setText("");
    }

    /**
     * Navigates to MainActivity and finishes current activity.
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Handles back button press.
     * Exits app instead of going back to prevent navigation issues.
     */
    @Override
    public void onBackPressed() {
        if (isRegistrationMode) {
            switchToLoginMode();
        } else {
            super.onBackPressed();
        }
    }
}