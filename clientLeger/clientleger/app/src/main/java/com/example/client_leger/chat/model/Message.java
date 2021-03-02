package com.example.client_leger.chat.model;

public class Message {
    private String nickname;
    private String message;
    private String date;
    private String avatar;
    private String channelName;

    public Message(String nickname, String message, String date, String avatar, String channelName) {
        this.nickname = nickname;
        this.message = message;
        this.date = date;
        this.avatar = avatar;
        this.channelName = channelName;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getMessage() {
        return this.message;
    }

    public String getDate() {
        return date;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public String getChannelName() { return channelName; }
}
