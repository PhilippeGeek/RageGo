package com.ragego.network;

import com.ragego.engine.HumanPlayer;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.io.IOException;

public class OnlinePlayer extends HumanPlayer{

    private final int id;

    private OnlinePlayer(int id, String code) {
        super(code, new OnlinePlayerListener());
        this.id = id;
    }

    /**
     * Create an online player from server data
     * @param object Server data for this Player
     * @throws JSONException if server data is incorrect
     * @throws IOException if server data is incorrect
     */
    public static OnlinePlayer loadFromJSON(JSONObject object) throws IOException, JSONException {
        if (object == null) return null;
        int id = object.getInt("id");
        final String code = object.get("code").toString();
        return new OnlinePlayer(id,code);
    }

    public int getId() {
        return id;
    }
}
