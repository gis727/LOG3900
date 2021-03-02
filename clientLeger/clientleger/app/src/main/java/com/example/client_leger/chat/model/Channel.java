package com.example.client_leger.chat.model;

import java.util.ArrayList;
import java.util.List;

public class Channel {
    private String name;
    private boolean isJoined;
    private boolean isOwner;
    private boolean isLeavable;
    private List<Message> messages = new ArrayList<Message>();
    private int newMessages;

    public Channel(String name, boolean isJoined, boolean isOwner, boolean isLeavable) {
        this.name = name;
        this.isJoined = isJoined;
        this.isOwner = isOwner;
        this.isLeavable = isLeavable;
    }

    public String getName() {
        return name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public boolean isJoined() {
        return isJoined;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public boolean isLeavable() {
        return isLeavable;
    }

    public int getNewMessages() {
        return newMessages;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJoined(boolean joined) {
        isJoined = joined;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public void setLeavable(boolean leavable) {
        isLeavable = leavable;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setNewMessages(int newMessages) {
        this.newMessages = newMessages;
    }

    public void addNewMessages(int nNewMessages) {
        newMessages += nNewMessages;
    }
}
