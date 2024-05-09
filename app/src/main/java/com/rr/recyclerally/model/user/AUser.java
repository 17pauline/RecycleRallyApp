package com.rr.recyclerally.model.user;

import java.util.Date;

public abstract class AUser {
    private String email;
    private String username;
    private String password;
    private EUserType userType;
    private Date joinDate;

    public AUser(String email, String username, String password, EUserType userType) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.joinDate = new Date();
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

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    @Override
    public String toString() {
        return "AUser{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", userType=" + userType +
                ", joinDate=" + joinDate +
                '}';
    }


}
