package se.persandstrom.ploxworm.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;

/**
 * User: Per Sandstrom
 * Date: 2013-04-17 10:02
 */
public class HumanPlayer extends Player {


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
//        System.out.println(this + " send: " + message);
        try {
            ws.send(message);
        } catch (IOException e) {
            System.out.println("PLAYER DISCONNECTED!");
            disconnected();
        }
    }

    public void received(String message) {
//        System.out.println(this + " received: " + message);

        JsonObject msgJson = (JsonObject) parser.parse(message);
        parent.received(this, msgJson);
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnected() {
        connected = false;
        parent.remove(this);
    }

    public void open() {
        System.out.println("open!");
        connected = true;
        parent.open(this);
    }

    @Override
    public boolean isHuman() {
        return true;
    }
}
