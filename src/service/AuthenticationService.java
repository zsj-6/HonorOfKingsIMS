package service;

import exception.AuthenticationException;
import model.Admin;
import model.Person;
import model.enums.Role;

/**
 * Handles user authentication and session management.
 *
 * <p>Uses constructor injection to receive the
 * {@link GameDataManager} for user lookup. Supports login/logout,
 * current user retrieval, and role-based access checks.</p>
 */
public class AuthenticationService {

    private final GameDataManager dataManager;
    private Person currentUser;

    /**
     * Constructs an AuthenticationService backed by the given data manager.
     *
     * @param dataManager the central data store
     */
    public AuthenticationService(GameDataManager dataManager) {
        if (dataManager == null) {
            throw new IllegalArgumentException(
                    "GameDataManager cannot be null.");
        }
        this.dataManager = dataManager;
        this.currentUser = null;
    }

    /**
     * Authenticates a user by searching both Admin and Player collections.
     *
     * @param username the username
     * @param password the password
     * @return the authenticated Person (Admin or Player)
     * @throws AuthenticationException if credentials are invalid
     */
    public Person login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password cannot be empty.");
        }

        for (Admin admin : dataManager.getAllAdmins()) {
            if (admin.getUsername().equals(username)
                    && admin.getPassword().equals(password)) {
                this.currentUser = admin;
                return admin;
            }
        }

        for (Person player : dataManager.getAllPlayers()) {
            if (player.getUsername().equals(username)
                    && player.getPassword().equals(password)) {
                this.currentUser = player;
                return player;
            }
        }

        throw new AuthenticationException(
                "Invalid username or password.");
    }

    /**
     * Logs out the current user by clearing the session.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return the current user, or null if not logged in
     */
    public Person getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks whether a user is currently logged in.
     *
     * @return true if a user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Checks whether the current user has the Admin role.
     * Returns false if no user is logged in.
     *
     * @return true if the current user is an Admin
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }

    /**
     * Checks whether the current user has the Player role.
     * Returns false if no user is logged in.
     *
     * @return true if the current user is a Player
     */
    public boolean isPlayer() {
        return currentUser != null && currentUser.getRole() == Role.PLAYER;
    }
}
