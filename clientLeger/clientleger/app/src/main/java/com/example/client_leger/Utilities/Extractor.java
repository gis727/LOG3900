package com.example.client_leger.Utilities;

import android.graphics.PointF;

import com.example.client_leger.LiveGame.DrawingUpdate;
import com.example.client_leger.LiveGame.Game;
import com.example.client_leger.LiveGame.GameImage;
import com.example.client_leger.LiveGame.GameImagePath;
import com.example.client_leger.LiveGame.GameStats;
import com.example.client_leger.MainLobby.PartyModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class Extractor {
    public PartyModel.Party extractParty(JSONObject partyObj) throws JSONException {
        PartyModel.Party party = null;
        // Get the party
        party = new PartyModel.Party(
                partyObj.getString("id"),
                partyObj.getString("name"),
                PartyModel.Difficulty.values()[partyObj.getInt("difficulty")],
                PartyModel.GameMode.values()[partyObj.getInt("mode")],
                partyObj.getInt("playersCount"),
                partyObj.getInt("playerCapacity"),
                PartyModel.Platform.values()[partyObj.getInt("platform")],
                partyObj.getBoolean("started"));
        // Get the players
        JSONArray playersArray = partyObj.getJSONArray("players");
        for (int i = 0; i < playersArray.length(); i++) {
            JSONObject playerObj = playersArray.getJSONObject(i);
            party.players.add(extractPlayer(playerObj, false));
        }
        return party;
    }

    public PartyModel.Player extractPlayer(JSONObject playerObj, boolean withPartyId) throws JSONException {
        PartyModel.Player player = new PartyModel.Player(playerObj.getString("id"), playerObj.getString("avatar"), playerObj.getBoolean("isVirtual"));
        if (withPartyId) player.partyId = playerObj.getString("partyId");
        return player;
    }

    public GameImagePath extractImagePath(JSONObject pathObj) throws JSONException {
        GameImagePath path = new GameImagePath(pathObj.getString("hexColor"), pathObj.getString("stylusPoint"), pathObj.getDouble("strokeWidth"));
        JSONArray pointsObj = pathObj.getJSONArray("points");
        for (int j=0; j < pointsObj.length(); j++) {
            String[] pointString = pointsObj.getString(j).split(",");
            path.points.add(new PointF((float)Double.parseDouble(pointString[0]), (float)Double.parseDouble(pointString[1])));
        }
        if (pathObj.has("isNewStroke")) path.isNewStroke = pathObj.getBoolean("isNewStroke");
        return path;
    }

    public GameImage extractGameImage(JSONArray pathsObj) throws JSONException {
        GameImage gameImage = new GameImage();
        for(int i=0; i < pathsObj.length(); i++) {
            JSONObject pathObj = pathsObj.getJSONObject(i);
            gameImage.paths.add(extractImagePath(pathObj));
        }
        return gameImage;
    }

    public DrawingUpdate extractDrawingUpdate(JSONObject updateObj) throws JSONException {
        return new DrawingUpdate(updateObj.has("path")? extractImagePath(updateObj.getJSONObject("path")): null,
                updateObj.has("image")? extractGameImage(updateObj.getJSONObject("image").getJSONArray("paths")): null);
    }

    public Game extractGame(JSONObject gameObj) throws JSONException {
        return new Game(
                gameObj.has("secretWord")? gameObj.getString("secretWord"): "",
                Game.Difficulty.values()[gameObj.getInt("difficulty")],
                Game.DrawingMode.values()[gameObj.getInt("drawingMode")],
                Game.DrawingDirection.values()[gameObj.getInt("drawingDirection")],
                Game.SelectedDrawer.values()[gameObj.getInt("selectedDrawer")],
                gameObj.has("image")? extractGameImage(gameObj.getJSONObject("image").getJSONArray("paths")): null
        );
    }

    public GameStats extractGameStats(JSONObject statsObj) throws JSONException {
        return new GameStats(statsObj.getInt("score"), statsObj.getInt("timeLeft"), statsObj.getInt("trialsLeft"));
    }
}
