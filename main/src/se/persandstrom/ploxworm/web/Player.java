package se.persandstrom.ploxworm.web;

import java.io.IOException;

public class Player {

    private final SimpleWebSocket ws;

    private boolean connected;

    private PlayerParent parent;

    public Player(SimpleWebSocket ws) {
        this.ws = ws;
        ws.setOwner(this);
        connected = false;
    }

    public void setParent(PlayerParent parent) {
        this.parent = parent;
    }

    public void send(String message) {
        System.out.println(this + " send: " + message);
        try {
            ws.send(message);
        } catch (IOException e) {
            e.printStackTrace();
            disconnected();
        }
    }

    public void received(String message) {
        System.out.println(this + " received: " + message);
        parent.received(this, message);
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
}

