package com.example.client_leger.utils;

import android.util.Log;

import com.example.client_leger.chat.model.Message;
import com.example.client_leger.profile.model.PlayedParty;
import com.example.client_leger.profile.model.User;
import com.example.client_leger.profile.model.UserSession;
import com.example.client_leger.profile.model.UserStats;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public final class MyJson {

    private MyJson() {
    }

    public static Message parseMessage(String messageString) {

        try {

            JSONObject messageJSON = new JSONObject(messageString);

            long timestamp = messageJSON.getLong("timestamp");

            String date = getDateStringForMessage(timestamp);
            String author = new String(messageJSON.getString("author").getBytes("UTF-8"));
            String message = new String(messageJSON.getString("message").getBytes("UTF-8"));
            String channelName = new String(messageJSON.getString("roomId").getBytes("UTF-8"));
            String avatar = messageJSON.getString("avatar");

            return new Message(author, message, date, avatar, channelName);

        } catch (Exception e) {
            return null;
        }
    }

    public static List<Message> parseChannelHistory(String channelHistoryString) {

        try {

            JSONArray channelHistoryJSON = new JSONArray(channelHistoryString);
            Log.d("MYJSON", channelHistoryJSON.toString());
            List<Message> channelHistory = new ArrayList<>();

            for (int i = 0; i < channelHistoryJSON.length(); ++i) {
                Message message = parseMessage(channelHistoryJSON.getString(i));
                Log.d("iwjdiwjidw", message.getMessage());
                channelHistory.add(message);
            }

            return channelHistory;
        } catch (Exception e) {
            return null;
        }
    }

    public static User parseUser(String userString) {

        try {

            JSONObject userJSON = new JSONObject(userString);
            JSONObject userStatsJSON = userJSON.getJSONObject("stats");
            JSONArray matchHistoryJSON = userStatsJSON.getJSONArray("previousGames");
            JSONArray activityHistoryJSON = userStatsJSON.getJSONArray("activity");

            ArrayList<PlayedParty> matchHistory = new ArrayList<PlayedParty>();

            for (int i = 0; i < matchHistoryJSON.length(); ++i) {
                PlayedParty playedParty = parsePlayedMatch(matchHistoryJSON.getString(i));
                matchHistory.add(playedParty);
            }

            Collections.reverse(matchHistory);

            ArrayList<UserSession> activityHistory = new ArrayList<UserSession>();
            for (int i = 0; i < activityHistoryJSON.length(); ++i) {
                UserSession userSession = parseUserSession(activityHistoryJSON.getString(i));
                activityHistory.add(userSession);
            }

            UserStats userStats = new UserStats(
                    activityHistory,
                    matchHistory
            );

            String nickname = userJSON.getString("nickname");
            String name = userJSON.getString("name");
            String surname = userJSON.getString("surname");
            String avatar = userJSON.getString("avatar");

            return new User(nickname, name, surname, avatar, userStats);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static PlayedParty parsePlayedMatch(String playedMatchString) {

        try {

            JSONObject playedMatchJSON = new JSONObject(playedMatchString);
            String matchDate = getDateString(playedMatchJSON.getLong("date"));
            Boolean won = playedMatchJSON.getBoolean("won");
            int matchType = playedMatchJSON.getInt("type");
            int matchScore = playedMatchJSON.getInt("score");
            int matchDuration = playedMatchJSON.getInt("duration");

            JSONArray playersJSON = playedMatchJSON.getJSONArray("players");

            List<String> players = new ArrayList<String>();
            for (int j = 0; j < playersJSON.length(); ++j) {
                players.add(playersJSON.getString(j));
            }

            return new PlayedParty(matchDate, players, won, matchType, matchScore, matchDuration);

        }
        catch (Exception e) {
            return null;
        }
    }

    public static UserSession parseUserSession(String userSessionString) {

        try {

            JSONObject userSessionJSON = new JSONObject(userSessionString);

            String connectionDate = getDateString(userSessionJSON.getLong("connectionDate"));
            String disconnectionDate = getDateString(userSessionJSON.getLong("disconnectionDate"));

            return new UserSession(connectionDate, disconnectionDate);

        }
        catch (Exception e) {
            return null;
        }
    }

    private static String getDateString(long timestamp){
        Date date = new Date(timestamp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        dateFormat.setTimeZone(TimeZone.getDefault());

        return dateFormat.format(date);
    }

    private static String getDateStringForMessage(long timestamp){
        Date date = new Date(timestamp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());

        return dateFormat.format(date);
    }
}
