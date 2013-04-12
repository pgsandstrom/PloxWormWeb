package se.persandstrom.ploxworm.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.GameController;
import se.persandstrom.ploxworm.core.Line;
import se.persandstrom.ploxworm.core.worm.HumanWorm;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.web.api.ApiObjectFactory;
import se.persandstrom.ploxworm.web.api.objects.EndRound;
import se.persandstrom.ploxworm.web.api.objects.Match;

import java.util.List;

public class WebGameController implements GameController {

    InitHolder initHolder;

    private final ApiObjectFactory apiObjectFactory = new ApiObjectFactory();

    private Core core;
    private final Player[] playerArray;
    private final float[][] playerAcc;

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
        //XXX NOT IMPLEMENTED
    }

    public void setAcc(int playerNumber, float xAcc, float yAcc) {
        playerAcc[playerNumber][0] = xAcc;
        playerAcc[playerNumber][1] = yAcc;
    }

    @Override
    public void updateScore(List<Worm> wormList) {
        System.out.println("updateScore: " + wormList);
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void hideTitle() {

    }

    @Override
    public void hideMessage() {

    }

    @Override
    public void setNewBoard(Board board) {
//        System.out.println("setNewBoard");
//        System.out.println("obstacles: " + board.getObstacles().size());

        Match match = new Match(board.getXSize(), board.getYSize(), board.getObstacles());

        for (int i = 0; i < playerArray.length; i++) {
            Player player = playerArray[i];
            match.setYourNumber(i);
            player.setYourNumber(i);
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

        JsonObject apiObject = apiObjectFactory.createApiObject(ApiObjectFactory.TYPE_FRAME, data);
        sendToAll(apiObject);
    }

    @Override
    public void end(HumanWorm worm, boolean victory, boolean expected) {
        System.out.println("endWithWait");

        //XXX determine endtype better. Do we really need 3 types?
        EndRound.EndType endType;
        if(victory) {
            endType = EndRound.EndType.won;
        } else {
            endType = EndRound.EndType.end;
        }

        for (Player player : playerArray) {
            if (player.getYourNumber() == worm.getPlayerNumber()) {
                JsonObject apiObject = apiObjectFactory.createApiObject(new EndRound(endType, worm.score));
                sendToAll(apiObject);
                initHolder.addPlayer(player);
            }
        }
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
