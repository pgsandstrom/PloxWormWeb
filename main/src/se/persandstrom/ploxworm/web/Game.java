package se.persandstrom.ploxworm.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import se.persandstrom.ploxworm.core.Core;

import java.util.List;

/**
 * User: pesandst
 * Date: 2013-03-15
 * Time: 12:42
 */
public class Game implements PlayerParent {

    private final List<Player> playerList;
    private final WebGameController gameController;
    private final Core core;



    public Game(List<Player> playerList, WebGameController gameController, Core core) {
        this.playerList = playerList;
        this.gameController = gameController;
        this.core = core;

        for (Player player : playerList) {
            player.setParent(this);
        }


    }

    public void start() {
        core.startGame();
    }

    @Override
    public void received(Player player, JsonObject message) {

        int playerNumber = playerList.indexOf(player);


        String type = message.get("type").getAsString();
        JsonObject data = message.get("data").getAsJsonObject();
//        System.out.println("type: " + type);
        if ("direction".equals(type)) {
            gameController.setAcc(playerNumber, data.get("x").getAsFloat(),data.get("y").getAsFloat());
        } else {
            System.out.println("unknown type: " + type);
        }
    }

    @Override
    public void remove(Player player) {
        //TODO if multiplayer, make other player winner
        core.stop();
    }

    @Override
    public void open(Player player) {
        throw new UnsupportedOperationException("you should already be open you doofus!");
    }
}
