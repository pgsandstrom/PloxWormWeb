package se.persandstrom.ploxworm.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.GameController;
import se.persandstrom.ploxworm.core.Line;
import se.persandstrom.ploxworm.core.worm.HumanWorm;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.ai.StupidWorm;
import se.persandstrom.ploxworm.core.worm.board.Apple;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.core.worm.board.StartPosition;
import se.persandstrom.ploxworm.web.api.ApiObjectFactory;
import se.persandstrom.ploxworm.web.api.objects.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Connects the game core with the players. Also holds CpuPlayers.
 */
public class WebGameController implements GameController {

    static Logger log = Logger.getLogger(WebGameController.class.getName());

    private final InitHolder initHolder;

    private final ApiObjectFactory apiObjectFactory = new ApiObjectFactory();

    private Core core;
    private final List<HumanPlayer> humanPlayerList;
    private final Map<Worm, Float[]> humanPlayerAcceleration;

    //it might change often, so use synchronizedList:
    private final List<HumanPlayer> observerList = Collections.synchronizedList(new ArrayList<HumanPlayer>());

    private final List<Player> playerList = new CopyOnWriteArrayList<Player>();

    private final Map<Integer, Player> playerNumberToPlayer = new HashMap<Integer, Player>();
    private final Map<Player, Worm> playerToWorm = new HashMap<Player, Worm>();
    private final Map<Worm, Player> wormToPlayer = new HashMap<Worm, Player>();

    private int highestPlayerNumber;

    public WebGameController(InitHolder initHolder) {
        humanPlayerList = new CopyOnWriteArrayList<HumanPlayer>();
        humanPlayerAcceleration = new HashMap<Worm, Float[]>();
        this.initHolder = initHolder;
    }

    public WebGameController(InitHolder initHolder, HumanPlayer player) {
        humanPlayerList = new CopyOnWriteArrayList<HumanPlayer>();
        humanPlayerList.add(player);
        humanPlayerAcceleration = new HashMap<Worm, Float[]>();
        this.initHolder = initHolder;
    }

    public WebGameController(InitHolder initHolder, List<HumanPlayer> humanPlayerList) {
        this.humanPlayerList = humanPlayerList;
        humanPlayerAcceleration = new HashMap<Worm, Float[]>();
        this.initHolder = initHolder;

        for (Player player : humanPlayerList) {
            if (player == null) {
                throw new IllegalArgumentException("a player was null");
            }
        }
    }

    public void setCore(Core core) {
        this.core = core;
    }

    @Override
    public float getXacc(HumanWorm worm) {
        Float xAcc = humanPlayerAcceleration.get(worm)[0];
        if (xAcc == null) {
            return 0f;
        } else {
            return xAcc;
        }
    }

    @Override
    public float getYacc(HumanWorm worm) {
        Float yAcc = humanPlayerAcceleration.get(worm)[1];
        if (yAcc == null) {
            return 0.1f;
        } else {
            return yAcc;
        }
    }

    @Override
    public void death(Worm worm, boolean expected) {
        log.debug("death: " + wormToPlayer.get(worm).getName());
        Player player = wormToPlayer.get(worm);
        Death death = new Death(player.getPlayerNumber());

        JsonObject apiObject = apiObjectFactory.createApiObject(death);
        sendToAll(apiObject);
    }

    public void setAcc(int playerNumber, float xAcc, float yAcc) {
        Player player = playerNumberToPlayer.get(playerNumber);
        Worm worm = playerToWorm.get(player);
        Float[] floats = humanPlayerAcceleration.get(worm);
        if (floats != null) {
            floats[0] = xAcc;
            floats[1] = yAcc;
        }
    }

    @Override
    public void updateScore(List<Worm> wormList) {
        ScoreBoard scoreBoard = new ScoreBoard();
        for (Worm worm : wormList) {
            Player player = wormToPlayer.get(worm);
            if (player == null) {
                log.error("player was null, skipping");
                continue;
            }
            String playerName = player.getName();
            scoreBoard.addScore(scoreBoard.new Score(playerName, worm.score));
        }

        JsonObject apiObject = apiObjectFactory.createApiObject(scoreBoard);
        sendToAll(apiObject);
    }

    @Override
    public void showTitle(String title) {
        JsonObject apiObject = apiObjectFactory.createApiObject(new Title(title));
        sendToAll(apiObject);
    }

    @Override
    public void showMessage(String message) {
        JsonObject apiObject = apiObjectFactory.createApiObject(new Message(message));
        sendToAll(apiObject);
    }

    @Override
    public void hideTitle() {
        JsonObject apiObject = apiObjectFactory.createApiObject(ApiObjectFactory.HIDE_TITLE);
        sendToAll(apiObject);
    }

    @Override
    public void hideMessage() {
        JsonObject apiObject = apiObjectFactory.createApiObject(ApiObjectFactory.HIDE_MESSAGE);
        sendToAll(apiObject);
    }

    @Override
    public void setNewBoard(Board board) {
        /**
         * Sets up all connections Player to Worm and sends the board to the players
         */


        //Set up all players, human and cpu:
        List<Worm> wormList = board.getWormList();
        CpuPlayerGenerator cpuPlayerGenerator = new CpuPlayerGenerator();
        int humanIndex = 0;
        for (int wormIndex = 0; wormIndex < wormList.size(); wormIndex++) {
            Worm worm = wormList.get(wormIndex);
            Player player;
            if (!worm.isAi()) {
                player = humanPlayerList.get(humanIndex);
                humanIndex++;
            } else {
                player = cpuPlayerGenerator.get();
            }

            addPlayerData(player, worm, wormIndex);
        }

        //complete the match object:
        Match match = getMatchObject(board);

        //send the match:
        for (HumanPlayer humanPlayer : humanPlayerList) {
            match.setYourNumber(humanPlayer.getPlayerNumber());
            sendToPlayer(humanPlayer, apiObjectFactory.createApiObject(match));
        }
    }

    @Override
    public void addPlayer(Player player) {
        synchronized (this) {
            //add his worm:
            StartPosition startposition = core.getRandomStartposition();
            Worm worm;
            if (player instanceof HumanPlayer) {
                worm = new HumanWorm(core, startposition);
            } else if (player instanceof CpuPlayer) {
                worm = new StupidWorm(core, startposition);
            } else {
                throw new IllegalStateException("wtf is that player???");
            }

            highestPlayerNumber++;
            addPlayerData(player, worm, highestPlayerNumber);

            core.addWorm(worm);

            if (player instanceof HumanPlayer) {
                log.debug("adding as human player");
                humanPlayerList.add((HumanPlayer) player);

                Match match = getMatchObject(core.getBoard());
                match.setYourNumber(player.getPlayerNumber());
                sendToPlayer((HumanPlayer) player, apiObjectFactory.createApiObject(match));
            }
        }
    }

    private Match getMatchObject(Board board) {
        Match match = new Match(board.getXSize(), board.getYSize(), board.getObstacles());
        List<PlayerDto> playerDtoList = new ArrayList<PlayerDto>();
        for (Player player : playerList) {
            playerDtoList.add(new PlayerDto(player));
        }
        match.setPlayers(playerDtoList);
        return match;
    }

    @Override
    public boolean removePlayer(Player player) {

        Worm worm = playerToWorm.get(player);
        boolean endGame = core.death(worm, false);
        core.removeWorm(worm);
        log.debug("removePlayer. endGame: " + endGame);

        playerToWorm.remove(player);
        wormToPlayer.remove(worm);
        playerNumberToPlayer.remove(player.getPlayerNumber());
        humanPlayerAcceleration.remove(worm);

        playerList.remove(player);

        if (player instanceof HumanPlayer) {
            log.debug("removing from human player list");
            boolean removed = humanPlayerList.remove(player);
            if (!removed) {
                log.error("well, removing failed...");
            }
        }

        return endGame;
    }

    private void addPlayerData(Player player, Worm worm, int playerNumber) {
        player.setPlayerNumber(playerNumber);
        highestPlayerNumber = Math.max(highestPlayerNumber, playerNumber);
        worm.setPlayerNumber(playerNumber);

        wormToPlayer.put(worm, player);
        playerToWorm.put(player, worm);
        playerNumberToPlayer.put(player.getPlayerNumber(), player);

        humanPlayerAcceleration.put(worm, new Float[2]);

        playerList.add(player);
    }

    @Override
    public void render() {
        //Gson is at least 3 times slower than manually building it, so build it manually...
        JsonObject data = new JsonObject();

        JsonArray wormArray = new JsonArray();
        data.add("worms", wormArray);

        List<Worm> wormList = core.getWormList();
        for (int wormNumber = 0; wormNumber < wormList.size(); wormNumber++) {
            Worm worm = wormList.get(wormNumber);

            JsonObject wormObject = new JsonObject();
            wormArray.add(wormObject);

            wormObject.addProperty("player_number", worm.getPlayerNumber());

            JsonArray lineArray = new JsonArray();
            wormObject.add("lines", lineArray);

            List<Line> lineList = worm.getLineList();
            for (int lineNumber = 0; lineNumber < lineList.size(); lineNumber++) {
                Line line = lineList.get(lineNumber);

                if (lineNumber == 0) {
                    JsonObject point = new JsonObject();
                    point.addProperty("x", line.xStart);
                    point.addProperty("y", line.yStart);
                    lineArray.add(point);
                }

                JsonObject point = new JsonObject();
                point.addProperty("x", line.xStop);
                point.addProperty("y", line.yStop);
                lineArray.add(point);
            }
        }

        //XXX an optimization could be to send apples only when they change
        //XXX well, the same thing could be true for worms...

        JsonArray appleArray = new JsonArray();
        data.add("apples", appleArray);
        List<Apple> appleList = core.getAppleList();
        for (Apple apple : appleList) {
            if (!apple.exists) {
                continue;
            }

            JsonObject appleObject = new JsonObject();
            if (apple.isGold) {
                appleObject.addProperty("type", "gold");
            } else {
                appleObject.addProperty("type", "red");
            }
            appleObject.addProperty("x", apple.positionX);
            appleObject.addProperty("y", apple.positionY);
            appleArray.add(appleObject);
        }

        JsonObject apiObject = apiObjectFactory.createApiObject(ApiObjectFactory.TYPE_FRAME, data);
        sendToAll(apiObject);
    }

    @Override
    public void end(HumanWorm worm, boolean victory, boolean expected, int winnerNumber) {
        log.debug("end winnerNumber: " + winnerNumber);

        //XXX determine endtype better. Do we really need 3 types?
        EndRound.EndType endType;
        if (victory) {
            endType = EndRound.EndType.won;
        } else {
            endType = EndRound.EndType.end;
        }

        EndRound endRound;
        if (winnerNumber != -1) {
            String winningMessage = playerNumberToPlayer.get(winnerNumber).getWinningMessage();
            endRound = new EndRound(endType, worm.score, winnerNumber, winningMessage);
        } else {
            //if singleplayer then no winner exists:
            endRound = new EndRound(endType, worm.score);
        }

        HumanPlayer player = (HumanPlayer) wormToPlayer.get(worm);
        JsonObject apiObject = apiObjectFactory.createApiObject(endRound);
        sendToPlayer(player, apiObject);
        initHolder.addPlayer(player);
    }

    private void sendToAll(JsonElement apiObject) {
        String string = apiObject.toString();
        for (HumanPlayer player : humanPlayerList) {
            player.send(string);
        }

        for (HumanPlayer player : observerList) {
            player.send(string);
        }
    }

    private void sendToPlayer(HumanPlayer player, JsonElement apiObject) {
        player.send(apiObject.toString());
    }

    @Override
    public void addObserver(HumanPlayer player) {
        Match match = getMatchObject(core.getBoard());
        match.setYourNumber(-1);
        sendToPlayer(player, apiObjectFactory.createApiObject(match));
        observerList.add(player);
    }

    @Override
    public boolean removeObserver(HumanPlayer player) {
        observerList.remove(player);
        if (getObserverCount() == 0 && core.getAliveHumanCount() == 0) {
            core.stop();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getObserverCount() {
        return observerList.size();
    }

}
