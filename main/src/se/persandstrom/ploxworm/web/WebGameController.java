package se.persandstrom.ploxworm.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.core.worm.board.Obstacle;
import se.persandstrom.ploxworm.core.worm.board.ObstacleCircle;
import se.persandstrom.ploxworm.core.worm.board.ObstacleRectangle;
import se.persandstrom.ploxworm.web.Player;

import java.util.ArrayList;

public class GameController {

    private Core core;
    private final Player[] playerList;

    public GameController(Player player) {
        playerList = new Player[]{player};
    }

    public void setCore(Core core) {
        this.core = core;
    }

    public void end(long score) {
        System.out.println("end");
    }

    public void victory(long score) {
        System.out.println("victory");
    }

    public void setScoreBoard(String score) {
        System.out.println("setScoreBoard: " + score);
    }

    public void setTitle(String title) {

    }

    public void setMessage(String message) {

    }

    public void hideTitle() {

    }

    public void hideMessage() {

    }

    public void showMessage() {

    }

    public void setNewBoard(Board board) {
        System.out.println("setNewBoard");
        System.out.println("obstacles: "+board.getObstacles().size());

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
                obstacleData.addProperty("top",rec.top);
                obstacleData.addProperty("right",rec.right);
                obstacleData.addProperty("bottom",rec.bottom);
                obstacleData.addProperty("left",rec.left);
            } else if (obstacle instanceof ObstacleCircle) {
                ObstacleCircle circle = (ObstacleCircle) obstacle;
                obstacleJson.addProperty("type", "circle");
                obstacleData.addProperty("x",circle.positionX);
                obstacleData.addProperty("y",circle.positionY);
                obstacleData.addProperty("radius",circle.radius);
            }
        }

        send("board", data);
    }

    public void render() {
        System.out.println("render");


        JsonObject data = new JsonObject();


        JsonArray wormArray = new JsonArray();
        data.add("worms", wormArray);

        ArrayList<Worm> wormList = core.wormList;
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


    public void endWithWait(long score) {
        System.out.println("endWithWait");
    }

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
