package se.persandstrom.ploxworm.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.GameController;
import se.persandstrom.ploxworm.core.Line;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.core.worm.board.Obstacle;
import se.persandstrom.ploxworm.core.worm.board.ObstacleCircle;
import se.persandstrom.ploxworm.core.worm.board.ObstacleRectangle;

import java.util.ArrayList;

public class WebGameController implements GameController {

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
        System.out.println("setNewBoard");
        System.out.println("obstacles: " + board.getObstacles().size());

        //TODO use gson instead

        JsonObject data = new JsonObject();

        data.addProperty("size_x", board.getXSize());
        data.addProperty("size_y", board.getYSize());

        JsonArray obstacleArray = new JsonArray();
        data.add("obstacles", obstacleArray);

        for (Obstacle obstacle : board.getObstacles()) {
            //XXX snyggify this
            JsonObject obstacleJson = new JsonObject();
            JsonObject obstacleData = new JsonObject();
            obstacleJson.add("data", obstacleData);
            obstacleArray.add(obstacleJson);
            if (obstacle instanceof ObstacleRectangle) {
                ObstacleRectangle rec = (ObstacleRectangle) obstacle;
                obstacleJson.addProperty("type", "rectangle");
                obstacleData.addProperty("top", rec.top);
                obstacleData.addProperty("right", rec.right);
                obstacleData.addProperty("bottom", rec.bottom);
                obstacleData.addProperty("left", rec.left);
            } else if (obstacle instanceof ObstacleCircle) {
                ObstacleCircle circle = (ObstacleCircle) obstacle;
                obstacleJson.addProperty("type", "circle");
                obstacleData.addProperty("x", circle.positionX);
                obstacleData.addProperty("y", circle.positionY);
                obstacleData.addProperty("radius", circle.radius);
            }
        }

        send("match", data);
    }

    @Override
    public void render() {
//        System.out.println("render");

        JsonObject data = new JsonObject();

        JsonArray wormArray = new JsonArray();
        data.add("worms", wormArray);

        ArrayList<Worm> wormList = core.getWormList();
        for (int wormNumber = 0; wormNumber < wormList.size(); wormNumber++) {
            Worm worm = wormList.get(wormNumber);
            JsonArray lineArray = new JsonArray();

            //TODO fuling, lägg inte alltid i you
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

        send("frame", data);
    }

    @Override
    public void endWithWait(long score) {
        System.out.println("endWithWait");
    }

    @Override
    public void updateScore(long score) {

    }

    private void send(String type, JsonElement data) {

        JsonObject root = new JsonObject();
        root.addProperty("type", type);
        root.add("data", data);

        for (Player player : playerList) {
            player.send(root.toString());
        }
    }

}
