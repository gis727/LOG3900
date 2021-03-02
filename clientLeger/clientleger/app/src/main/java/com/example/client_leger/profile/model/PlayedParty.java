package com.example.client_leger.profile.model;

import java.util.List;

public class PlayedParty {
    public String matchDate;
    public List<String> players;
    public boolean won;
    public int matchType, matchScore, matchDuration;


    public PlayedParty(String matchDate,
                       List<String> players,
                       boolean won,
                       int matchType,
                       int matchScore,
                       int matchDuration) {
        this.matchDate = matchDate;
        this.players = players;
        this.won = won;
        this.matchType = matchType;
        this.matchScore = matchScore;
        this.matchDuration = matchDuration;
    }
}
