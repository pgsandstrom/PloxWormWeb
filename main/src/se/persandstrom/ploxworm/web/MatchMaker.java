package se.persandstrom.ploxworm.web;

import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.worm.board.BoardType;
import se.persandstrom.ploxworm.web.api.ApiObjectFactory;
import se.persandstrom.ploxworm.web.api.objects.MatchRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Named("matchMaker")
@ApplicationScoped
public class MatchMaker implements Serializable, PlayerParent {

    static Logger log = Logger.getLogger(MatchMaker.class.getName());

    @Inject
    InitHolder initHolder;

    @Inject
    ApiObjectFactory apiObjectFactory;

    private final Random random = new Random();

    private final Object eternalGameLock = new Object();
    private Game eternalGame;

    private final Object multiplayerGameLock = new Object();
    HumanPlayer waitingPlayer;
    int waitingPlayerLevel;

    public void addPlayer(HumanPlayer player, MatchRequest matchRequest) {
        player.setName(matchRequest.getPlayerName());
        player.setParent(this);

        if (!player.isConnected()) {
            //TODO
            log.error("wtf player not connected");
        }

        switch (matchRequest.getGameType()) {
            case eternal:
                startEternalGame(player);
                break;
            case single:
                startSinglePlayer(player, false, matchRequest.getLevel());
                break;
            case vs_cpu:
                startSinglePlayer(player, true, matchRequest.getLevel());
                break;
            case multi:
                arrangeMultiPlayer(player, matchRequest);
                break;
        }
    }

    private void startEternalGame(HumanPlayer player) {
        synchronized (eternalGameLock) {
            if (eternalGame == null) {
                WebGameController gameController = new WebGameController(initHolder, player);
                Core.Builder builder = new Core.Builder(gameController);
                builder.setEternalGame(true);
                builder.setLevel(0);
                builder.setBoardType(BoardType.ETERNAL);

                List<HumanPlayer> playerList = new ArrayList<HumanPlayer>();
                playerList.add(player);

                Core core = builder.build();
                gameController.setCore(core);

                //when a match has been made:
                eternalGame = new Game(this, playerList, gameController, core);
                eternalGame.start();
            } else {
                log.debug("adding to ongoing eternal game");
                eternalGame.addPlayer(player);
            }
        }
    }

    private void startSinglePlayer(HumanPlayer player, boolean withCpu, int level) {
        log.debug("startSinglePlayer: " + level);
        WebGameController gameController = new WebGameController(initHolder, player);
        Core.Builder builder = new Core.Builder(gameController);
        builder.setEternalGame(false);
        builder.setLevel(level);
        builder.setBoardType(withCpu ? BoardType.VS_CPU : BoardType.SINGLE);

        List<HumanPlayer> playerList = new ArrayList<HumanPlayer>();
        playerList.add(player);

        Core core = builder.build();
        gameController.setCore(core);

        //when a match has been made:
        Game game = new Game(this, playerList, gameController, core);
        game.start();
    }

    private void arrangeMultiPlayer(HumanPlayer player, MatchRequest matchRequest) {

        int level = matchRequest.getLevel();
        player.setName(matchRequest.getPlayerName());
        player.setWinningMessage(matchRequest.getWinningMessage());

        synchronized (multiplayerGameLock) {
            if (waitingPlayer == null) {
                //put in queue
                waitingPlayer = player;
                waitingPlayerLevel = level;
                JsonObject putInQueueObject = apiObjectFactory.createApiObject(ApiObjectFactory.TYPE_PUT_IN_QUEUE);
                player.send(putInQueueObject.toString());
            } else {
                //start game!
                ArrayList<HumanPlayer> playerList = new ArrayList<HumanPlayer>();
                playerList.add(waitingPlayer);
                playerList.add(player);

                List<HumanPlayer> humanPlayerList = new ArrayList<HumanPlayer>();
                humanPlayerList.add(waitingPlayer);
                humanPlayerList.add(player);
                WebGameController gameController = new WebGameController(initHolder, humanPlayerList);
                Core.Builder builder = new Core.Builder(gameController);
                builder.setLevel(random.nextInt(2) == 0 ? level : waitingPlayerLevel);
                builder.setBoardType(BoardType.MULTI);
                Core core = builder.build();
                gameController.setCore(core);

                Game game = new Game(this, playerList, gameController, core);
                game.start();
                waitingPlayer = null;
            }
        }
    }

    public void gamedStopped(Game game) {
        if (game == eternalGame) {
            log.info("eternal game stopped");
            eternalGame = null;
        } else {
            log.info("non-eternal game reported stop!");
        }
    }

    @Override
    public void received(HumanPlayer player, JsonObject message) {
        //NOT IMPLEMENTED
    }

    @Override
    public void remove(HumanPlayer player) {
        if (player == waitingPlayer) {
            waitingPlayer = null;
        }
    }

    @Override
    public void open(HumanPlayer player) {
        throw new UnsupportedOperationException("you should already be open you doofus!");
    }
}
