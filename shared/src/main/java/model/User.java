package model;

import java.util.Objects;

public class User {
    String username;
    String password;
    String email;
    Integer authToken;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.email = "";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthToken(Integer authToken) {
        this.authToken = authToken;
    }

    public Integer getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    public boolean verifyPassword(String password) {
        return password.equals(this.password);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof User user)) return false;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password)
                && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email);
    }
}
