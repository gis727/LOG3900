package com.example.client_leger.profile.model;

public class User {

    public String nickname, name, surname, avatar;
    public UserStats userStats;

    public User(String nickname, String name, String surname, String avatar, UserStats userStats) {
        this.nickname = nickname;
        this.name = name;
        this.surname = surname;
        this.avatar = avatar;
        this.userStats = userStats;
    }

}

