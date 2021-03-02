package com.example.client_leger.profile.model;

import java.util.ArrayList;

public class UserStats {
    public ArrayList<UserSession> activityHistory;
    public ArrayList<PlayedParty> matchHistory;

    public UserStats(ArrayList<UserSession> activityHistory,
                     ArrayList<PlayedParty> matchHistory) {
        this.activityHistory = activityHistory;
        this.matchHistory = matchHistory;
    }
}


