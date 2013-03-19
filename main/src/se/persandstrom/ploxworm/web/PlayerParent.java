package se.persandstrom.ploxworm.web;

import com.google.gson.JsonObject;

public interface PlayerParent {

    public void received(Player player, JsonObject message);

    /**
     * A player has been removed from the game (disconnected)
     * @param player
     */
    public void remove(Player player);

    /**
     * A connection has opened
     * @param player
     */
    public void open(Player player);
}
