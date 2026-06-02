package model;

import model.enums.Role;

public class Admin extends Person {

    public Admin() {
        setRole(Role.ADMIN);
    }

    public Admin(String id, String username, String password) {
        super(id, username, password, Role.ADMIN);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + getId() + '\'' +
                ", username='" + getUsername() + '\'' +
                '}';
    }
}
