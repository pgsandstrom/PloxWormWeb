package se.persandstrom.ploxworm.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.web.api.ApiObjectFactory;

import java.util.List;

/**
 * User: pesandst
 * Date: 2013-03-15
 * Time: 12:42
 */
public class Game implements PlayerParent {

    static Logger log = Logger.getLogger(Game.class.getName());

    private final ApiObjectFactory apiObjectFactory = new ApiObjectFactory();
    private final MatchMaker matchMaker;

    private final List<HumanPlayer> playerList;
    private final WebGameController gameController;
    private final Core core;


    public Game(MatchMaker matchMaker, List<HumanPlayer> playerList, WebGameController gameController, Core core) {
        this.matchMaker = matchMaker;
        this.playerList = playerList;
        this.gameController = gameController;
        this.core = core;

        for (HumanPlayer player : playerList) {
            player.setParent(this);
        }

        if(playerList.size() == 0) {
            throw new IllegalStateException("cant create game without players");
        }

        log.info("Game created. Current player size: " + playerList.size());
    }

    public void start() {
        core.startGame();
    }

    public void addPlayer(HumanPlayer player) {
        player.setParent(this);
        playerList.add(player);
        gameController.addPlayer(player);
    }

    @Override
    public void received(HumanPlayer player, JsonObject message) {

        int playerNumber = playerList.indexOf(player);

        //use this when we got more potential messages
        Class messageClass = apiObjectFactory.getTypeClass(message);


        String type = message.get("type").getAsString();
        JsonObject data = message.get("data").getAsJsonObject();
//        log.debug("type: " + type);
        if ("direction".equals(type)) {
            gameController.setAcc(playerNumber, data.get("x").getAsFloat(), data.get("y").getAsFloat());
        } else {
            log.debug("Game unknown type: " + type);
        }
    }

    @Override
    public void remove(HumanPlayer player) {
        log.debug("remove. Current player size: " + playerList.size());
        boolean removed = playerList.remove(player);
        if (!removed) {
            log.warn("player already removed. Could be dual remove signals");
        }
        boolean gameEnded = gameController.removePlayer(player);
        log.debug("game ended: "+gameEnded);

        if (gameEnded) {
            matchMaker.gamedStopped(this);
        }
    }

    @Override
    public void open(HumanPlayer player) {
        throw new UnsupportedOperationException("you should already be open you doofus!");
    }
}
