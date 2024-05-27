package com.rr.recyclerally.model.user;

import java.io.Serializable;
import java.util.Date;

public abstract class AUser implements Serializable {
    private String email;
    private String username;
    private String password;
    private EUserType userType;

    public AUser() {
    }

    public AUser(String email, String username, EUserType userType) {
        this.email = email;
        this.username = username;
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EUserType getUserType() {
        return userType;
    }

    public void setUserType(EUserType userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "AUser{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", userType=" + userType +
                '}';
    }
}
