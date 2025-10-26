package com.example.chessapp;

/**
 * User model class representing a registered user in the chess application.
 * Contains user credentials, game statistics, and identification information.
 */
public class User {
    private int id;
    private String username;
    private String passwordHash;
    private int wins;
    private long totalTimePlayed; // in milliseconds
    private long createdAt;

    // Default constructor
    public User() {
        this.wins = 0;
        this.totalTimePlayed = 0;
        this.createdAt = System.currentTimeMillis();
    }

    // Constructor for new user registration
    public User(String username, String passwordHash) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
    }

    // Constructor for database retrieval
    public User(int id, String username, String passwordHash, int wins, long totalTimePlayed, long createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.wins = wins;
        this.totalTimePlayed = totalTimePlayed;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getWins() {
        return wins;
    }

    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setTotalTimePlayed(long totalTimePlayed) {
        this.totalTimePlayed = totalTimePlayed;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Utility methods
    public void incrementWins() {
        this.wins++;
    }

    public void addGameTime(long gameTimeMillis) {
        this.totalTimePlayed += gameTimeMillis;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", wins=" + wins +
                ", totalTimePlayed=" + totalTimePlayed +
                ", createdAt=" + createdAt +
                '}';
    }
}