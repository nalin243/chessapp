# Design Document

## Overview

The SQLite User Authentication system will integrate seamlessly into the existing chess application architecture. The design follows Android best practices with a simple SQLite database for local storage, maintaining the current UI theme and navigation patterns. The system will be implemented as a lightweight authentication layer that doesn't interfere with existing game logic.

## Architecture

### Database Layer
- **SQLiteOpenHelper**: Custom database helper class managing database creation and version updates
- **UserDAO**: Data Access Object pattern for user operations (CRUD)
- **Database Schema**: Single `users` table with essential fields

### Authentication Layer
- **AuthManager**: Singleton class managing user sessions and authentication state
- **Session Management**: SharedPreferences for maintaining login state across app restarts
- **Password Security**: Simple hashing for student-level security (SHA-256)

### UI Integration
- **LoginActivity**: New activity following existing design theme
- **Modified MainActivity**: Updated to show greeting and handle authentication flow
- **Updated UserRegistrationActivity**: Modified to use logged-in user as Player 1
- **Enhanced LeaderboardActivity**: Connected to database for real user data

## Components and Interfaces

### Database Schema

```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    wins INTEGER DEFAULT 0,
    total_time_played INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Core Classes

#### DatabaseHelper
```java
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database configuration
    // Table creation and upgrade logic
    // Basic CRUD operations
}
```

#### AuthManager
```java
public class AuthManager {
    // Singleton pattern
    // Login/logout functionality
    // Session management with SharedPreferences
    // Current user tracking
}
```

#### User Model
```java
public class User {
    // Simple POJO with id, username, wins, totalTime
    // Basic getters/setters
    // Constructor methods
}
```

### Activity Flow

1. **App Launch** → **LoginActivity** (if not logged in) → **MainActivity**
2. **MainActivity** → Shows greeting with username
3. **Play Button** → **UserRegistrationActivity** (Player 1 pre-filled)
4. **Game End** → **BoardActivity** updates winner's stats
5. **Leaderboard** → **LeaderboardActivity** displays database data

## Data Models

### User Entity
- `id`: Primary key (auto-increment)
- `username`: Unique identifier (String, max 50 chars)
- `password_hash`: SHA-256 hashed password
- `wins`: Integer count of games won
- `total_time_played`: Cumulative time in milliseconds
- `created_at`: Registration timestamp

### Session Data (SharedPreferences)
- `is_logged_in`: Boolean flag
- `current_user_id`: Integer user ID
- `current_username`: String username for quick access

## Error Handling

### Database Errors
- Connection failures: Graceful degradation with user notification
- Constraint violations: User-friendly error messages
- Data corruption: Database recreation with user consent

### Authentication Errors
- Invalid credentials: Clear error message, remain on login screen
- Duplicate username: Immediate feedback during registration
- Session timeout: Automatic logout with navigation to login

### Network Independence
- Fully offline operation using SQLite
- No network dependencies or external authentication services
- Local data persistence across app updates

## Testing Strategy

### Unit Testing Focus
- Database operations (CRUD)
- Authentication logic
- Password hashing/verification
- User session management

### Integration Testing
- Login flow end-to-end
- Game completion to database update
- Leaderboard data accuracy
- Activity navigation with authentication

### Manual Testing Scenarios
- New user registration and first login
- Existing user login and game play
- Multiple users and leaderboard ranking
- App restart with maintained session

## UI Design Consistency

### Login/Registration Activity Design
- **Background**: Same mandala background (`@drawable/mandala_bg`)
- **Color Scheme**: Gold text (`#FFD700`) on dark overlay (`#A0000000`)
- **Typography**: Cinzel Decorative font family
- **Layout**: Centered form with semi-transparent background
- **Buttons**: Consistent with existing teal theme (`#0E6D97`)

### Integration Points
- **MainActivity**: Add greeting TextView below existing menu
- **UserRegistrationActivity**: Disable Player 1 EditText, show logged-in user
- **LeaderboardActivity**: Replace static data with database queries
- **BoardActivity**: Add database update calls in win condition handlers

## Security Considerations

### Password Security
- SHA-256 hashing for password storage
- No plain text password storage
- Salt generation for enhanced security (optional enhancement)

### Session Security
- Session data stored in private SharedPreferences
- Automatic logout on app uninstall/data clear
- No sensitive data in logs or temporary files

### Data Privacy
- All data stored locally on device
- No external data transmission
- User data accessible only within app context