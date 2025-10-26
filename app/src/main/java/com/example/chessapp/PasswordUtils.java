package com.example.chessapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility class for password hashing and verification using SHA-256.
 * Provides secure password storage and validation methods.
 */
public class PasswordUtils {
    
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String CHARSET = "UTF-8";

    /**
     * Hashes a plain text password using SHA-256.
     * 
     * @param password The plain text password to hash
     * @return The SHA-256 hash of the password as a hexadecimal string
     * @throws RuntimeException if hashing fails
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(password.getBytes(CHARSET));
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    /**
     * Verifies a plain text password against a stored hash.
     * 
     * @param password The plain text password to verify
     * @param storedHash The stored hash to compare against
     * @return true if the password matches the hash, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        
        try {
            String hashedInput = hashPassword(password);
            return hashedInput.equals(storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Converts byte array to hexadecimal string representation.
     * 
     * @param bytes The byte array to convert
     * @return Hexadecimal string representation
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * Validates password strength (basic validation).
     * 
     * @param password The password to validate
     * @return true if password meets minimum requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        
        // Basic validation: at least 4 characters for simplicity
        return password.length() >= 4;
    }

    /**
     * Validates username format.
     * 
     * @param username The username to validate
     * @return true if username is valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        
        // Basic validation: 3-50 characters, alphanumeric and underscore only
        return username.matches("^[a-zA-Z0-9_]{3,50}$");
    }
}