package se.persandstrom.ploxworm.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.GameController;

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

        player.setNumber(0);


        GameController gameController = new GameController(player);
        Core.Builder builder = new Core.Builder(gameController);
        builder.setEternalGame(false);
        builder.setLevel(4);
        builder.setMakePlayersToAi(false);
        builder.setScore(0);
        Core core = builder.build();

        gameController.setCore(core);

        //when a match has been made:
        new Game(gameController, core).start();

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
