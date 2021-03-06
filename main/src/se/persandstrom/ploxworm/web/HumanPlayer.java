package se.persandstrom.ploxworm.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * User: Per Sandstrom
 * Date: 2013-04-17 10:02
 */
public class HumanPlayer extends Player {

    static Logger log = Logger.getLogger(HumanPlayer.class.getName());

    private final SimpleWebSocket ws;
    private boolean connected;
    private final JsonParser parser;

    private PlayerParent parent;

    public HumanPlayer(SimpleWebSocket ws) {
        this.ws = ws;
        ws.setOwner(this);
        connected = false;
        parser = new JsonParser();
    }

    public void setParent(PlayerParent parent) {
        this.parent = parent;
    }

    public void send(String message) {
        try {
            ws.send(message);
        } catch (IOException e) {
            log.debug("IOException: disconnected!");
            disconnected();
//            e.printStackTrace();
        }
    }

    public void received(String message) {
        JsonObject msgJson = (JsonObject) parser.parse(message);
        parent.received(this, msgJson);
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnected() {
        synchronized (this) {   //to prevent multiple disconnect-messages
            if (connected) {
                connected = false;
                parent.remove(this);
                log.debug("removing player, disconnected from " + parent.getClass().getName());
            }
        }
    }

    public void open() {
        log.debug("player connection open!");
        connected = true;
        parent.open(this);
    }

    @Override
    public boolean isHuman() {
        return true;
    }
}
