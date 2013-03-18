package se.persandstrom.ploxworm.core.worm.board;

import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.worm.Worm;

import java.util.ArrayList;
import java.util.Random;

public class Board {

	protected static final String TAG = "Board";

	Random random;

	private final Core core;

	public final String title;

	private final ArrayList<Obstacle> obstacleList;

	private final ArrayList<Apple> appleList;

	private int appleEatGoal;
	private int applesEaten;
	private boolean hasPlacedGoldApple;

	private final int xSize;
	private final int ySize;

	ArrayList<Worm> wormList;

	//TODO: Fix "aliveWorms" that calculates stuff u know

	public Board(Core core, String title, ArrayList<Worm> wormList, ArrayList<Obstacle> obstacleList,
			ArrayList<Apple> appleList, int xSize, int ySize, int appleEatGoal, int appleVisibleAtOnce) {
		this.core = core;
		this.title = title;
		this.wormList = wormList;
		this.obstacleList = obstacleList;
		this.appleList = appleList;
		this.xSize = xSize;
		this.ySize = ySize;
		this.appleEatGoal = appleEatGoal;

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

	public ArrayList<Worm> getWormList() {
		return wormList;
	}

	public ArrayList<Obstacle> getObstacles() {
		return obstacleList;
	}

	public void setAppleEatGoal(int appleEatGoal) {
		this.appleEatGoal = appleEatGoal;
	}

	public ArrayList<Apple> getApples() {
		return appleList;
	}

	public void ateApple(Apple eatenApple) {
		//		if (Constant.DEBUG) Log.d(TAG, "ateApple:" + eatenApple.positionX);

		if (eatenApple.isGold) {
			core.victory();
			return;
		}

		applesEaten++;
		core.ateApple();

		while (true) {
			int randomNumber = random.nextInt(appleList.size());
			//			if (Constant.DEBUG) Log.d(TAG, "randomNumber:" + randomNumber);
			Apple apple = appleList.get(randomNumber);
			//			if (Constant.DEBUG) Log.d(TAG, "apple:" + apple.positionX);

			//check so the apple does not exist, and also that it is not the apple that was just eaten
			if (!apple.exists) {
//				if (Constant.DEBUG) Log.d(TAG, "adding apple:" + apple.positionX);
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
}
