package se.persandstrom.ploxworm.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named("matchMaker")
@ApplicationScoped
public class MatchMaker implements Serializable, PlayerParent {

    Player waitingPlayer;

    public void addPlayer(Player player) {
        player.setParent(this);

        if(!player.isConnected()) {
            //TODO
            System.out.println("wtf player not connected");
        }

        JsonObject point1 = new JsonObject();
        point1.addProperty("x", 3);
        point1.addProperty("y", 3);
        JsonObject point2 = new JsonObject();
        point2.addProperty("x", 35);
        point2.addProperty("y", 36);
        JsonObject point3 = new JsonObject();
        point3.addProperty("x", 54);
        point3.addProperty("y", 51);
        JsonArray youArray = new JsonArray();
        youArray.add(point1);
        youArray.add(point2);
        youArray.add(point3);

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("you", youArray);
//        jsonObject.addProperty("enemies", errorName);
//        jsonObject.addProperty("objects", getMessage());


        JsonObject root = new JsonObject();
        root.addProperty("type","frame");
        root.add("data",jsonObject);

        player.send(root.toString());

//        synchronized (this) {
//            if (waitingPlayer == null) {
//                waitingPlayer = player;
//                player.send(WsMessage.IN_QUEUE);
//            } else {
//                //TODO ping the other player?
//                new Game(waitingPlayer, player).start();
//                waitingPlayer = null;
//            }
//        }
    }

    @Override
    public void received(Player player, String message) {
        //NOT IMPLEMENTED
    }

    @Override
    public void remove(Player player) {
        //NOT IMPLEMENTED
    }

    @Override
    public void open(Player player) {
        throw new UnsupportedOperationException("you should already be open you doofus!");
    }
}
