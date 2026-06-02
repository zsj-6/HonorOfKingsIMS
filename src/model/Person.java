package model;

import model.enums.Role;
import java.util.Objects;

public abstract class Person {
    private String id;
    private String username;
    private String password;
    private Role role;

    protected Person() {
    }

    protected Person(String id, String username, String password, Role role) {
        setId(id);
        setUsername(username);
        setPassword(password);
        setRole(role);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or blank.");
        }
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or blank.");
        }
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or blank.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }
        this.role = role;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
