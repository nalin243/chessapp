# Implementation Plan

- [x] 1. Create database infrastructure and user model
  - Create User model class with id, username, password hash, wins, and total time fields
  - Implement DatabaseHelper class extending SQLiteOpenHelper with users table creation
  - Add password hashing utility methods using SHA-256
  - _Requirements: 1.5, 2.2_

- [x] 2. Implement authentication manager and session handling
  - Create AuthManager singleton class for login/logout operations
  - Implement session management using SharedPreferences for login state persistence
  - Add user credential validation methods against database
  - _Requirements: 2.2, 2.3, 2.5_

- [x] 3. Create login and registration UI
  - Design and implement LoginActivity layout following existing app theme
  - Add input validation for username and password fields
  - Implement registration logic with duplicate username checking
  - Create navigation flow from login to main activity
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.4_

- [x] 4. Modify MainActivity to show user greeting
  - Update MainActivity layout to include greeting TextView
  - Implement greeting message display with logged-in username
  - Add authentication check and redirect to login if not authenticated
  - _Requirements: 3.1, 3.2, 3.3_

- [x] 5. Update UserRegistrationActivity for logged-in user
  - Modify layout to disable Player 1 name input field
  - Auto-populate Player 1 name with logged-in username
  - Update game start logic to pass correct player names to BoardActivity
  - _Requirements: 4.1, 4.2, 4.3, 4.4_

- [x] 6. Implement game statistics tracking in BoardActivity
  - Add database update calls in game completion handlers (checkmate and timeout)
  - Implement winner identification and win count increment logic
  - Add time tracking for total game time played
  - Handle both registered and unregistered player scenarios
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [x] 7. Update LeaderboardActivity with database integration
  - Replace static leaderboard data with database queries
  - Implement user sorting by wins in descending order
  - Display username, wins, and total time for each user
  - Add tie-breaking logic for users with equal wins
  - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_

- [x] 8. Add app launch authentication flow
  - Modify SplashActivity or create startup logic to check authentication status
  - Implement automatic navigation to LoginActivity for unauthenticated users
  - Add session restoration for returning authenticated users
  - _Requirements: 1.1, 2.5_

- [ ]* 9. Create unit tests for core functionality
  - Write tests for DatabaseHelper CRUD operations
  - Test AuthManager login/logout and session management
  - Create tests for password hashing and validation
  - Test User model and data integrity
  - _Requirements: All requirements_

- [ ]* 10. Add error handling and user feedback
  - Implement database error handling with user-friendly messages
  - Add loading indicators for database operations
  - Create validation feedback for registration and login forms
  - Handle edge cases like database corruption or connection issues
  - _Requirements: 1.4, 2.3_