package com.ragego.network;

import com.ragego.engine.GameBoard;
import com.ragego.engine.GameNode;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Server class to communicate with the game server.
 */
@SuppressWarnings("UseSparseArrays")
public class RageGoServer extends Resty {

    private static final String RAGEGO_SERVER = "http://ragego-server.herokuapp.com";
    private static RageGoServer instance;
    private static Map<Integer, OnlineGame> games = Collections.synchronizedMap(new HashMap<Integer, OnlineGame>());
    private static Map<Integer, OnlinePlayer> players = Collections.synchronizedMap(new HashMap<Integer, OnlinePlayer>());
    private static Map<Integer, OnlineNode> nodes = Collections.synchronizedMap(new HashMap<Integer, OnlineNode>());

    public static RageGoServer getInstance(){
        if(instance == null)
            instance = new RageGoServer();
        return instance;
    }

    public static OnlinePlayer getPlayer(int id) throws RageGoServerException {
        if (players.containsKey(id)) {
            return players.get(id);
        }
        try {
            final OnlinePlayer onlinePlayer = OnlinePlayer.loadFromJSON(getPlayerJSONObject(id));
            players.put(id, onlinePlayer);
            return onlinePlayer;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    private static JSONObject getPlayerJSONObject(int id) throws IOException, JSONException {
        return getInstance().json(getPlayerURL(id)).object();
    }

    private static String getPlayerURL(int id) {
        return RAGEGO_SERVER + "/players/" + String.valueOf(id) + ".json";
    }

    public static OnlinePlayer createPlayer(boolean playing) throws RageGoServerException{
        try{
            final JSONResource resource = getInstance().json(getPlayersURL(), form(data("player[playing]", playing ? "1" : "0")));
            final OnlinePlayer onlinePlayer = OnlinePlayer.loadFromJSON(resource.object());
            players.put(onlinePlayer.getId(), onlinePlayer);
            return onlinePlayer;
        } catch (Exception e){
            throw handleException(e);
        }
    }

    private static String getPlayersURL() {
        return RAGEGO_SERVER + "/players.json";
    }

    public static void deletePlayer(OnlinePlayer player) throws RageGoServerException{
        try {
            getInstance().json(getPlayerURL(player.getId()), delete());
            if (players.containsValue(player)) {
                players.remove(player.getId());
            }
        } catch (IOException e) {
            throw handleException(e);
        }
    }

    public static OnlineGame getGame(int id) throws RageGoServerException{
        if (games.get(id) != null)
            return games.get(id);
        try {
            final OnlineGame onlineGame = OnlineGame.loadFromJSON(getGameJSONObject(id));
            games.put(id, onlineGame);
            return onlineGame;
        } catch(Exception e){
            throw handleException(e);
        }
    }

    private static JSONObject getGameJSONObject(int id) throws IOException, JSONException {
        return getInstance().json(getGameURL(id)).object();
    }

    private static String getGameURL(int id) {
        return RAGEGO_SERVER + "/games/" + String.valueOf(id) + ".json";
    }

    public static OnlineGame createGame(OnlinePlayer blacks, OnlinePlayer whites) throws RageGoServerException{
        try {
            final JSONResource resource = getInstance().json(getGamesURL(),
                    form(
                            data("game[whites_id]", String.valueOf(whites.getId())),
                            data("game[blacks_id]", String.valueOf(blacks.getId()))
                    ));
            final OnlineGame onlineGame = new OnlineGame(resource.object().getInt("id"), blacks, whites);
            games.put(onlineGame.getId(), onlineGame);
            return onlineGame;
        } catch (Exception e){
            throw handleException(e);
        }
    }

    private static String getGamesURL() {
        return RAGEGO_SERVER + "/games.json";
    }

    public static OnlineNode getNode(int id, GameBoard board) throws RageGoServerException {
        if (nodes.containsKey(id))
            return nodes.get(id);
        try {
            final OnlineNode onlineNode = OnlineNode.loadFromJSON(getInstance().json(RAGEGO_SERVER + "/nodes/" + String.valueOf(id) + ".json").object(), board);
            nodes.put(id, onlineNode);
            return onlineNode;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    public static OnlineNode createNode(GameNode node, OnlineGame game, OnlinePlayer player) throws RageGoServerException {
        try {
            final OnlineNode onlineNode = OnlineNode.loadFromJSON(getInstance().json(RAGEGO_SERVER + "/nodes.json", form(
                    data("node[player_id]", String.valueOf(player.getId())),
                    data("node[game_id]", String.valueOf(game.getId())),
                    data("node[data]", GameNode.serialize(node))
            )).object(), node.getBoard());
            nodes.put(onlineNode.getId(), onlineNode);
            return onlineNode;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    public static void purgeCache() {
        games.clear();
        players.clear();
        nodes.clear();
    }

    public static RageGoServerException handleException(Exception e) {
        if(e instanceof IOException){
            return new RageGoServerException(RageGoServerException.ExceptionType.OFFLINE, e);
        }
        if(e instanceof JSONException){
            return new RageGoServerException(RageGoServerException.ExceptionType.DATA_MALFORMED, e);
        }
        return new RageGoServerException(RageGoServerException.ExceptionType.UNKNOWN, e);
    }
}
