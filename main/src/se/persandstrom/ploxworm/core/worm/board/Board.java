package se.persandstrom.ploxworm.core.worm.board;

import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.worm.HumanWorm;
import se.persandstrom.ploxworm.core.worm.Worm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {

    protected static final String TAG = "Board";

    private final Random random;

    private final Core core;
    private BoardType type;

    public final String title;

    private final List<Obstacle> obstacleList;

    private final List<Apple> appleList;

    private final List<StartPosition> startPositionList;

    private int appleEatGoal;
    private int applesEaten;
    private boolean hasPlacedGoldApple;

    private final int xSize;
    private final int ySize;

    private final List<Worm> wormList;
    private final List<HumanWorm> humanWormList;

    public Board(Core core, String title, ArrayList<Obstacle> obstacleList, ArrayList<Apple> appleList, int xSize,
                 int ySize, int appleEatGoal, int appleVisibleAtOnce, List<StartPosition> startPositionList) {
        this.core = core;
        this.title = title;
        this.obstacleList = obstacleList;
        this.appleList = appleList;
        this.xSize = xSize;
        this.ySize = ySize;
        this.appleEatGoal = appleEatGoal;
        this.startPositionList = startPositionList;
        this.wormList = new ArrayList<Worm>();
        humanWormList = new ArrayList<HumanWorm>();

        hasPlacedGoldApple = false;
        random = new Random();

        while (appleVisibleAtOnce > 0) {
            int randomNumber = random.nextInt(appleList.size());
            Apple apple = appleList.get(randomNumber);
            if (!apple.exists) {
                apple.exists = true;
                appleVisibleAtOnce--;
            }
        }
    }

    public void addWorm(Worm worm) {
        wormList.add(worm);
        if (worm instanceof HumanWorm) {
            humanWormList.add((HumanWorm) worm);
        }
    }

    public List<Worm> getWormList() {
        return wormList;
    }

    public List<Obstacle> getObstacles() {
        return obstacleList;
    }

    public void setAppleEatGoal(int appleEatGoal) {
        this.appleEatGoal = appleEatGoal;
    }

    public List<Apple> getApples() {
        return appleList;
    }

    public List<StartPosition> getStartPositionList() {
        return startPositionList;
    }

    public void ateApple(Worm worm, Apple eatenApple) {

        if (!worm.isAi() && eatenApple.isGold) {
            core.victory(worm);
            return;
        }

        applesEaten++;
        core.ateApple(worm);

        while (true) {
            int randomNumber = random.nextInt(appleList.size());
            Apple apple = appleList.get(randomNumber);

            //check so the apple does not exist, and also that it is not the apple that was just eaten
            if (!apple.exists) {
                apple.exists = true;
                if (!hasPlacedGoldApple && applesEaten >= appleEatGoal) {
                    apple.isGold = true;
                    hasPlacedGoldApple = true;
                }
                break;
            }
        }
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public BoardType getType() {
        return type;
    }

    public void setType(BoardType type) {
        this.type = type;
    }
}
