package se.persandstrom.ploxworm.web;

import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.GameController;

/**
 * User: pesandst
 * Date: 2013-03-15
 * Time: 12:42
 */
public class Game implements PlayerParent {

    private final GameController gameController;
    private final Core core;

    public Game(GameController gameController, Core core) {
        this.gameController = gameController;
        this.core = core;
    }

    public void start() {
        core.startGame();
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
