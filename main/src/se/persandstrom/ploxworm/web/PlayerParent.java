package se.persandstrom.ploxworm.web;

import com.google.gson.JsonObject;

public interface PlayerParent {

    public void received(HumanPlayer player, JsonObject message);

    /**
     * A player has been removed from the game (disconnected)
     * @param player
     */
    public void remove(HumanPlayer player);

    /**
     * A connection has opened
     * @param player
     */
    public void open(HumanPlayer player);
}
