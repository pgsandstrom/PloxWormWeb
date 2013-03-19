package se.persandstrom.ploxworm.web;

import se.persandstrom.ploxworm.core.Core;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

        WebGameController gameController = new WebGameController(player);
        Core.Builder builder = new Core.Builder(gameController);
        builder.setEternalGame(false);
        builder.setLevel(4);
        builder.setMakePlayersToAi(false);
        builder.setScore(0);
        Core core = builder.build();

        gameController.setCore(core);


        List<Player> playerList = new ArrayList<>();
        playerList.add(player);

        //when a match has been made:
        Game game = new Game(playerList,gameController, core);


        game.start();

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
