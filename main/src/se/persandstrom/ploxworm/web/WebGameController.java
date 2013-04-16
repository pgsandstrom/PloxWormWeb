package se.persandstrom.ploxworm.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.GameController;
import se.persandstrom.ploxworm.core.Line;
import se.persandstrom.ploxworm.core.worm.HumanWorm;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.board.Apple;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.web.api.ApiObjectFactory;
import se.persandstrom.ploxworm.web.api.objects.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebGameController implements GameController {

    InitHolder initHolder;

    private final ApiObjectFactory apiObjectFactory = new ApiObjectFactory();

    private Core core;
    private final Player[] playerArray;
    private final float[][] playerAcc;

    private final Map<Integer, Player> playerNumberToPlayer = new HashMap<Integer, Player>();
    private final Map<Player, Worm> playerToWorm = new HashMap<Player, Worm>();
    private final Map<Worm, Player> wormToPlayer = new HashMap<Worm, Player>();

    public WebGameController(InitHolder initHolder, Player player) {
        playerArray = new Player[]{player};
        playerAcc = new float[playerArray.length][2];
        this.initHolder = initHolder;
    }

    public WebGameController(InitHolder initHolder, Player[] playerArray) {
        this.playerArray = playerArray;
        playerAcc = new float[this.playerArray.length][2];
        this.initHolder = initHolder;

        for (Player player : playerArray) {
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
        return playerAcc[worm.getPlayerNumber()][0];
    }

    @Override
    public float getYacc(HumanWorm worm) {
        return playerAcc[worm.getPlayerNumber()][1];
    }

    @Override
    public void death(Worm worm, boolean expected) {
        Death death;
        if (!worm.isAi()) {
            Player player = wormToPlayer.get(worm);
            death = new Death(player.getPlayerNumber());
        } else {
            death = new Death();
        }

        JsonObject apiObject = apiObjectFactory.createApiObject(death);
        sendToAll(apiObject);
    }

    public void setAcc(int playerNumber, float xAcc, float yAcc) {
        playerAcc[playerNumber][0] = xAcc;
        playerAcc[playerNumber][1] = yAcc;
    }

    @Override
    public void updateScore(List<Worm> wormList) {
        ScoreBoard scoreBoard = new ScoreBoard();
        for (Worm worm : wormList) {
            String name;
            if (worm.isAi()) {
                name = "CPU";
            } else {
                name = wormToPlayer.get(worm).getName();
            }
            scoreBoard.addScore(scoreBoard.new Score(name, worm.score));
        }

        JsonObject apiObject = apiObjectFactory.createApiObject(scoreBoard);
        sendToAll(apiObject);
    }

    @Override
    public void showTitle(String title) {
        System.out.println("showTitle");
        JsonObject apiObject = apiObjectFactory.createApiObject(new Title(title));
        sendToAll(apiObject);
    }

    @Override
    public void showMessage(String message) {
        System.out.println("showMessage");
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

        Match match = new Match(board.getXSize(), board.getYSize(), board.getObstacles());

        List<HumanWorm> humanWormList = board.getHumanWormList();

        for (int i = 0; i < playerArray.length; i++) {
            Player player = playerArray[i];
            HumanWorm humanWorm = humanWormList.get(i);
            match.setYourNumber(i);
            player.setPlayerNumber(i);
            humanWorm.setPlayerNumber(i);

            wormToPlayer.put(humanWorm, player);
            playerToWorm.put(player, humanWorm);
            playerNumberToPlayer.put(player.getPlayerNumber(), player);

            sendToPlayer(player, apiObjectFactory.createApiObject(match));
        }
    }

    @Override
    public void render() {
//        System.out.println("render");

        //Gson is at least 3 times slower than manually building it, so build it manually...
        JsonObject data = new JsonObject();

        JsonArray wormArray = new JsonArray();
        data.add("worms", wormArray);

        List<Worm> wormList = core.getWormList();
        for (int wormNumber = 0; wormNumber < wormList.size(); wormNumber++) {
            Worm worm = wormList.get(wormNumber);
            JsonArray lineArray = new JsonArray();
            wormArray.add(lineArray);

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
        System.out.println("end: " + worm.getPlayerNumber());

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
            endRound = new EndRound(endType, worm.score);
        }

        Player player = wormToPlayer.get(worm);
        JsonObject apiObject = apiObjectFactory.createApiObject(endRound);
        sendToPlayer(player, apiObject);
        initHolder.addPlayer(player);

    }

    private void sendToAll(JsonElement apiObject) {
        String string = apiObject.toString();
        for (Player player : playerArray) {
            player.send(string);
        }
    }

    private void sendToPlayer(Player player, JsonElement apiObject) {
        player.send(apiObject.toString());
    }

}
