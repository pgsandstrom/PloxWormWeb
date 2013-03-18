package se.persandstrom.ploxworm.core.worm;

import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.Line;
import se.persandstrom.ploxworm.core.worm.board.Apple;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.core.worm.board.Obstacle;

import java.util.ArrayList;

public abstract class Worm {

	protected static final String TAG = "Worm";

	public final static int MOVE_OK = 0;
	public final static int MOVE_DEATH = 1;

	private static final int INITIAL_LENGTH = 50;
	private static final int APPLE_LENGTH_INCREASE = 10;

	protected static final float SPEED = 10;
	protected static final float MAX_DEGREE_TURN = 0.3f;

	Core core;

	//is actually just a number, like 0,1,2
	public final int color;

	int maxLength = INITIAL_LENGTH;

	//positions
	public double xPosStart;
	public double yPosStart;
	public double xPos;
	public double yPos;
    double xPosOld;
    double yPosOld;

	//the worms current direction is this vector:
	public double xForceStart;
	public double yForceStart;
	public double xForce;
	public double yForce;

	//board size
	float boardSizeX;
	float boardSizeY;

	public Board board;
	public final ArrayList<Line> lineList;
	public ArrayList<Obstacle> obstacleList;
	public ArrayList<Apple> appleList;

	public boolean isAlive;

	public Worm(Core core, int color, double startPositionX, double startPositionY, double startSpeedX, double startSpeedY) {

		this.core = core;
		this.color = color;

		xPosStart = startPositionX;
		yPosStart = startPositionY;

		xPos = xPosStart;
		yPos = yPosStart;
		xPosOld = xPos;
		yPosOld = yPos;

		xForceStart = startSpeedX;
		yForceStart = startSpeedY;

		xForce = xForceStart;
		yForce = yForceStart;

		lineList = new ArrayList<Line>();

		isAlive = true;
	}

	public void init(Board board) {
		this.board = board;
		this.obstacleList = board.getObstacles();
		this.appleList = board.getApples();
		boardSizeX = board.getXSize();
		boardSizeY = board.getYSize();
//		if (Constant.DEBUG) Log.d(TAG, "obstacleList:" + obstacleList.size());
	}

	public ArrayList<Line> getLineList() {
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
			return MOVE_DEATH;
		}

		Apple eatApple = checkAppleEating(newLine);
		if (eatApple != null) {
			eat(eatApple);
		}

		return MOVE_OK;
	}

	public abstract boolean isAi();

	protected Apple checkAppleEating(Line newLine) {
		//compare to fruits:
		for (Apple apple : appleList) {
			if (apple.exists && apple.isCollide(newLine)) {
				//				if (Constant.DEBUG) Log.d(TAG, "BAM ON APPLE!");
				return apple;
			}
		}
		return null;
	}

	public void reset() {
//		if (Constant.DEBUG) Log.d(TAG, "reset");
		xPos = xPosStart;
		yPos = yPosStart;
		xPosOld = xPos;
		yPosOld = yPos;

		xForce = xForceStart;
		yForce = yForceStart;

		maxLength = INITIAL_LENGTH;
	}

	protected boolean isLineColliding(Line newLine) {
		return isLineColliding(newLine, lineList.size());
	}

	protected boolean isLineColliding(Line newLine, int lineListSize) {
		int iterationStop = Math.min(lineListSize, maxLength) - 10;

		//compare to our worm:
		for (int i = 0; i < iterationStop; i++) {
			Line line = lineList.get(i);
			if (isIntersection(newLine, line)) {
				//				if (Constant.DEBUG) Log.d(TAG, "BAM!");
				return true;
			}
		}

		//compare to worms:
		ArrayList<Worm> worms = board.getWormList();
		for (Worm worm : worms) {
			if (!worm.equals(this)) {
				ArrayList<Line> otherWormLineList = worm.getLineList();
				for (Line line : otherWormLineList) {
					if (isIntersection(newLine, line)) {
						//						if (Constant.DEBUG) Log.d(TAG, "BAM ON WORM!");
						return true;
					}
				}
			}
		}

		//compare to objects:
		for (Obstacle obstacle : obstacleList) {
			if (obstacle.isCollide(newLine)) {
				//				if (Constant.DEBUG) Log.d(TAG, "BAM ON OBSTACLE!");
				return true;
			}
		}
		return false;
	}

	private void eat(Apple apple) {

		board.ateApple(apple);
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

		boolean isLine1StartAboveLine2 = isDotAboveFunction(line1.xStart, line1.yStart, line2);
		boolean isLine1StopAboveLine2 = isDotAboveFunction(line1.xStop, line1.yStop, line2);

		if (isLine1StartAboveLine2 == isLine1StopAboveLine2) {
			return false;
		}

		boolean isLine2StartAboveLine1 = isDotAboveFunction(line2.xStart, line2.yStart, line1);
		boolean isLine2StopAboveLine1 = isDotAboveFunction(line2.xStop, line2.yStop, line1);

		if (isLine2StartAboveLine1 == isLine2StopAboveLine1) {
			return false;
		}

		return true;
	}

	private boolean isDotAboveFunction(double x, double y, Line line) {
		double lineY = x * line.a + line.c;
		return y > lineY;
	}
}
