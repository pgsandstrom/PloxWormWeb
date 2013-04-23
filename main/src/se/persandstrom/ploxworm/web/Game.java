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

    private final List<HumanPlayer> playerList;
    private final WebGameController gameController;
    private final Core core;


    public Game(List<HumanPlayer> playerList, WebGameController gameController, Core core) {
        this.playerList = playerList;
        this.gameController = gameController;
        this.core = core;

        for (HumanPlayer player : playerList) {
            player.setParent(this);
        }
    }

    public void start() {
        core.startGame();
    }

    @Override
    public void received(HumanPlayer player, JsonObject message) {

        int playerNumber = playerList.indexOf(player);

        //use this when we got more potential messages
        Class messageClass = apiObjectFactory.getTypeClass(message);


        String type = message.get("type").getAsString();
        JsonObject data = message.get("data").getAsJsonObject();
        log.debug("type: " + type);
        if ("direction".equals(type)) {
            gameController.setAcc(playerNumber, data.get("x").getAsFloat(), data.get("y").getAsFloat());
        } else {
            log.debug("Game unknown type: " + type);
        }
    }

    @Override
    public void remove(HumanPlayer player) {
        //TODO don't just stop
        core.stop();
    }

    @Override
    public void open(HumanPlayer player) {
        throw new UnsupportedOperationException("you should already be open you doofus!");
    }
}
