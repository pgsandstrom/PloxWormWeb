package se.persandstrom.ploxworm.core.worm;

import org.apache.log4j.Logger;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.Line;
import se.persandstrom.ploxworm.core.worm.board.Apple;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.core.worm.board.Obstacle;
import se.persandstrom.ploxworm.core.worm.board.StartPosition;

import java.util.ArrayList;
import java.util.List;

public abstract class Worm {

    static final String TAG = "Worm";

    static Logger log = Logger.getLogger(Worm.class.getName());

    public final static int MOVE_OK = 0;
    public final static int MOVE_DEATH = 1;

    private static final int INITIAL_LENGTH = 50;
    private static final int APPLE_LENGTH_INCREASE = 10;

    protected static final float SPEED = 10;
    protected static final float MAX_DEGREE_TURN = 0.3f;

    protected Core core;

    private int maxLength;

    private StartPosition startPosition;

    //positions
    public double xPos;
    public double yPos;
    double xPosOld;
    double yPosOld;

    //the worms current direction is this vector:
    public double xForce;
    public double yForce;

    //board size
    private float boardSizeX;
    private float boardSizeY;

    public Board board;
    public final List<Line> lineList;
    public List<Obstacle> obstacleList;
    public List<Apple> appleList;

    public boolean isAlive;
    public long score;

    int playerNumber;

    public Worm(Core core, StartPosition startPosition) {

        this.core = core;

        this.startPosition = startPosition;


        xPosOld = xPos;
        yPosOld = yPos;

        reset();

        lineList = new ArrayList<Line>();

        isAlive = true;
    }

    public void init(Board board) {
        this.board = board;
        this.obstacleList = board.getObstacles();
        this.appleList = board.getApples();
        boardSizeX = board.getXSize();
        boardSizeY = board.getYSize();
    }

    public List<Line> getLineList() {
        return lineList;
    }

    protected abstract void fixDirection();

    public int makeMove() {

        if (!isAlive) {
            return MOVE_OK;
        }

        fixDirection();

        Line newLine = makeLine(true);

        lineList.add(newLine);

        int lineListSize = lineList.size();

        //allow worm to shrink by doing this check twice:
        if (lineListSize > maxLength) {
            lineList.remove(0);
            lineListSize = lineList.size();
        }
        if (lineListSize > maxLength) {
            lineList.remove(0);
            lineListSize = lineList.size();
        }

        if (isLineColliding(newLine, lineListSize)) {
//            log.debug("death");
            return MOVE_DEATH;
        }

        Apple eatApple = checkAppleEating(newLine);
        if (eatApple != null) {
            eat(eatApple);
        }

        return MOVE_OK;
    }

    public abstract boolean isAi();

    /**
     * @param newLine
     * @return an apple if it was eaten, or null
     */
    protected Apple checkAppleEating(Line newLine) {
        //compare to fruits:
        for (Apple apple : appleList) {
            if (apple.exists && apple.isCollide(newLine)) {
                return apple;
            }
        }
        return null;
    }

    public void reset() {
        xPos = startPosition.x;
        yPos = startPosition.y;
        xForce = startPosition.startSpeedX;
        yForce = startPosition.startSpeedY;

        xPosOld = xPos;
        yPosOld = yPos;

        maxLength = INITIAL_LENGTH;
    }

    protected boolean isLineColliding(Line newLine) {
        return isLineColliding(newLine, lineList.size());
    }

    protected boolean isLineColliding(Line newLine, int lineListSize) {
        //TODO we still fail at finding collision if worm is traveling perfectly  horizontally
        int iterationStop = Math.min(lineListSize, maxLength) - 10;

        //compare to our worm:
        for (int i = 0; i < iterationStop; i++) {
            Line line = lineList.get(i);
            if (isIntersection(newLine, line)) {
//                log.debug("bam to self");
                return true;
            }
        }

        //compare to worms:
        List<Worm> worms = board.getWormList();
        for (Worm worm : worms) {
            if (!worm.equals(this)) {
                List<Line> otherWormLineList = worm.getLineList();
                for (Line line : otherWormLineList) {
                    if (isIntersection(newLine, line)) {
//                        log.debug("bam to other");
                        return true;
                    }
                }
            }
        }

        //compare to objects:
        for (Obstacle obstacle : obstacleList) {
            if (obstacle.isCollide(newLine)) {
                return true;
            }
        }
        return false;
    }

    private void eat(Apple apple) {

        board.ateApple(this, apple);
        apple.eat();

        maxLength += APPLE_LENGTH_INCREASE;
    }

    protected Line makeLine(boolean updateWormPosition) {
        return makeLine(SPEED, updateWormPosition);
    }

    protected Line makeLine(float speed, boolean updateWormPosition) {
        double xQuota;
        double xAbs = Math.abs(xForce);
        double yAbs = Math.abs(yForce);

        xQuota = xAbs / (xAbs + yAbs);

        double yQuota = 1 - xQuota;

        double xChange = speed * xQuota;
        if (xForce < 0) {
            xChange *= -1;
        }
        double yChange = speed * yQuota;
        if (yForce < 0) {
            yChange *= -1;
        }

        double xPosOldTemp = xPos;
        double yPosOldTemp = yPos;

        double xPosTemp = xPos + xChange;
        double yPosTemp = yPos + yChange;

        //fix if the worm has crossed the board boarders
        if (xPosOldTemp > boardSizeX) {
            xPosOldTemp -= boardSizeX;
            xPosTemp -= boardSizeX;
        } else if (xPosOldTemp < 0) {
            xPosOldTemp += boardSizeX;
            xPosTemp += boardSizeX;
        }
        if (yPosOldTemp > boardSizeY) {
            yPosOldTemp -= boardSizeY;
            yPosTemp -= boardSizeY;
        } else if (yPosOldTemp < 0) {
            yPosOldTemp += boardSizeY;
            yPosTemp += boardSizeY;
        }

        Line newLine = new Line(xPosOldTemp, yPosOldTemp, xPosTemp, yPosTemp);

        if (updateWormPosition) {
            xPosOld = xPosOldTemp;
            yPosOld = yPosOldTemp;
            xPos = xPosTemp;
            yPos = yPosTemp;
        }

        return newLine;
    }

    private boolean isIntersection(Line line1, Line line2) {

        //copypasta from alexM answer here:
        //http://stackoverflow.com/questions/9043805/test-if-two-lines-intersect-javascript-function
        double x = ((line1.xStart * line1.yStop - line1.yStart * line1.xStop) * (line2.xStart - line2.xStop) - (line1
                .xStart - line1.xStop) * (line2.xStart * line2.yStop - line2.yStart * line2.xStop)) / ((line1.xStart
                - line1.xStop) * (line2.yStart - line2.yStop) - (line1.yStart - line1.yStop) * (line2.xStart - line2
                .xStop));
        double y = ((line1.xStart * line1.yStop - line1.yStart * line1.xStop) * (line2.yStart - line2.yStop) - (line1
                .yStart - line1.yStop) * (line2.xStart * line2.yStop - line2.yStart * line2.xStop)) / ((line1.xStart
                - line1.xStop) * (line2.yStart - line2.yStop) - (line1.yStart - line1.yStop) * (line2.xStart - line2
                .xStop));

        if (line1.xStart >= line1.xStop) {
            if (!(line1.xStop <= x && x <= line1.xStart)) {
                return false;
            }
        } else {
            if (!(line1.xStart <= x && x <= line1.xStop)) {
                return false;
            }
        }
        if (line1.yStart >= line1.yStop) {
            if (!(line1.yStop <= y && y <= line1.yStart)) {
                return false;
            }
        } else {
            if (!(line1.yStart <= y && y <= line1.yStop)) {
                return false;
            }
        }
        if (line2.xStart >= line2.xStop) {
            if (!(line2.xStop <= x && x <= line2.xStart)) {
                return false;
            }
        } else {
            if (!(line2.xStart <= x && x <= line2.xStop)) {
                return false;
            }
        }
        if (line2.yStart >= line2.yStop) {
            if (!(line2.yStop <= y && y <= line2.yStart)) {
                return false;
            }
        } else {
            if (!(line2.yStart <= y && y <= line2.yStop)) {
                return false;
            }
        }

        return true;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public void setStartPosition(StartPosition startPosition) {
        this.startPosition = startPosition;
    }
}
