package com.example.client_leger.profile.model;

public class UserSession {
    public String connectionDate, disconnectionDate;

    public UserSession(String connectionDate, String disconnectionDate) {
        this.connectionDate = connectionDate;
        this.disconnectionDate = disconnectionDate;
    }
}
