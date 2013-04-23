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
import se.persandstrom.ploxworm.core.worm.board.Apple;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.web.api.ApiObjectFactory;
import se.persandstrom.ploxworm.web.api.objects.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebGameController implements GameController {

    static Logger log = Logger.getLogger(WebGameController.class.getName());

    InitHolder initHolder;

    private final ApiObjectFactory apiObjectFactory = new ApiObjectFactory();

    private Core core;
    private final HumanPlayer[] humanPlayerArray;
    private final float[][] humanPlayerAcceleration;

    private List<Player> playerList = new ArrayList<Player>();

    private final Map<Integer, Player> playerNumberToPlayer = new HashMap<Integer, Player>();
    private final Map<Player, Worm> playerToWorm = new HashMap<Player, Worm>();
    private final Map<Worm, Player> wormToPlayer = new HashMap<Worm, Player>();

    public WebGameController(InitHolder initHolder, HumanPlayer player) {
        humanPlayerArray = new HumanPlayer[]{player};
        humanPlayerAcceleration = new float[humanPlayerArray.length][2];
        this.initHolder = initHolder;
    }

    public WebGameController(InitHolder initHolder, HumanPlayer[] humanPlayerArray) {
        this.humanPlayerArray = humanPlayerArray;
        humanPlayerAcceleration = new float[this.humanPlayerArray.length][2];
        this.initHolder = initHolder;

        for (Player player : humanPlayerArray) {
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
        return humanPlayerAcceleration[worm.getPlayerNumber()][0];
    }

    @Override
    public float getYacc(HumanWorm worm) {
        return humanPlayerAcceleration[worm.getPlayerNumber()][1];
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
        humanPlayerAcceleration[playerNumber][0] = xAcc;
        humanPlayerAcceleration[playerNumber][1] = yAcc;
    }

    @Override
    public void updateScore(List<Worm> wormList) {
        ScoreBoard scoreBoard = new ScoreBoard();
        for (Worm worm : wormList) {
            String playerName = wormToPlayer.get(worm).getName();
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
        List<Worm> humanWormList = board.getWormList();
        CpuPlayerGenerator cpuPlayerGenerator = new CpuPlayerGenerator();
        int humanIndex = 0;
        for (int wormIndex = 0; wormIndex < humanWormList.size(); wormIndex++) {
            Worm worm = humanWormList.get(wormIndex);
            Player player;
            if (!worm.isAi()) {
                player = humanPlayerArray[humanIndex];
                humanIndex++;
            } else {
                player = cpuPlayerGenerator.get();
            }

            player.setPlayerNumber(wormIndex);
            worm.setPlayerNumber(wormIndex);

            wormToPlayer.put(worm, player);
            playerToWorm.put(player, worm);
            playerNumberToPlayer.put(player.getPlayerNumber(), player);

            playerList.add(player);
        }

        //complete the match object:
        Match match = new Match(board.getXSize(), board.getYSize(), board.getObstacles());
        List<PlayerDto> playerDtoList = new ArrayList<PlayerDto>();
        for (Player player : playerList) {
            playerDtoList.add(new PlayerDto(player));
        }
        match.setPlayers(playerDtoList);

        //send the match:
        for (HumanPlayer humanPlayer : humanPlayerArray) {
            match.setYourNumber(humanPlayer.getPlayerNumber());
            sendToPlayer(humanPlayer, apiObjectFactory.createApiObject(match));
        }
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
        for (HumanPlayer player : humanPlayerArray) {
            player.send(string);
        }
    }

    private void sendToPlayer(HumanPlayer player, JsonElement apiObject) {
        player.send(apiObject.toString());
    }

}
