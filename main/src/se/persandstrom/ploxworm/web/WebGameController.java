package se.persandstrom.ploxworm.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.GameController;
import se.persandstrom.ploxworm.core.Line;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.web.api.ApiObjectFactory;
import se.persandstrom.ploxworm.web.api.objects.Match;

import java.util.ArrayList;

public class WebGameController implements GameController {

    private final ApiObjectFactory apiObjectFactory = new ApiObjectFactory();

    private Core core;
    private final Player[] playerList;
    private final float[][] playerAcc;

    public WebGameController(Player player) {
        playerList = new Player[]{player};
        playerAcc = new float[playerList.length][2];
    }

    public void setCore(Core core) {
        this.core = core;
    }

    @Override
    public float getXacc(Worm player) {
        //TODO FIXME i need to give each worm corresponding number, so I can give correct acceleration
        return playerAcc[0][0];
    }

    @Override
    public float getYacc(Worm player) {
        //TODO FIXME as above
        return playerAcc[0][1];
    }

    public void setAcc(int playerNumber, float xAcc, float yAcc) {
//        System.out.println("setAcc: " + xAcc + ", " + yAcc);
        playerAcc[playerNumber][0] = xAcc;
        playerAcc[playerNumber][1] = yAcc;
    }

    @Override
    public void end(long score) {
        System.out.println("end");
    }

    @Override
    public void victory(long score) {
        System.out.println("victory");
    }

    @Override
    public void setScoreBoard(String score) {
        System.out.println("setScoreBoard: " + score);
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setMessage(String message) {

    }

    @Override
    public void hideTitle() {

    }

    @Override
    public void hideMessage() {

    }

    @Override
    public void showMessage() {

    }

    @Override
    public void setNewBoard(Board board) {
//        System.out.println("setNewBoard");
//        System.out.println("obstacles: " + board.getObstacles().size());

        Match match = new Match(board.getXSize(), board.getYSize(), board.getObstacles());

        for (int i = 0; i < playerList.length; i++) {
            Player player = playerList[i];
            match.setYour_number(i);
            sendToPlayer(player, "match", apiObjectFactory.createApiObject(match));
        }

    }

    @Override
    public void render() {
//        System.out.println("render");

        //TODO should these be gson or would tht slow stuff down? TEST IT
        JsonObject data = new JsonObject();

        JsonArray wormArray = new JsonArray();
        data.add("worms", wormArray);

        ArrayList<Worm> wormList = core.getWormList();
        for (int wormNumber = 0; wormNumber < wormList.size(); wormNumber++) {
            Worm worm = wormList.get(wormNumber);
            JsonArray lineArray = new JsonArray();
            wormArray.add(lineArray);

            ArrayList<Line> lineList = worm.getLineList();
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

        send(ApiObjectFactory.TYPE_FRME, data);
    }

    @Override
    public void endWithWait(long score) {
        System.out.println("endWithWait");
    }

    @Override
    public void updateScore(long score) {

    }

    private void send(String type, JsonElement data) {
//TODO dess funktioner borde snrre ligg i piboecjtfctory eller nåt
        JsonObject root = new JsonObject();
        root.addProperty("type", type);
        root.add("data", data);

        for (Player player : playerList) {
            player.send(root.toString());
        }
    }

    private void sendToPlayer(Player player, String type, JsonElement data) {

        JsonObject root = new JsonObject();
        root.addProperty("type", type);
        root.add("data", data);

        player.send(root.toString());
    }

}
