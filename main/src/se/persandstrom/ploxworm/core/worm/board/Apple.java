package se.persandstrom.ploxworm.core.worm.board;

import se.persandstrom.ploxworm.core.Line;

public class Apple {

	protected static final String TAG = "Apple";

	private static final float STANDARD_SIZE = 20;

	public boolean exists;

	public boolean isGold;

	public final float positionX;
	public final float positionY;

	public final float radius;

	

	public Apple(boolean isGold, float positionX, float positionY, float radius) {
//		if (Constant.DEBUG) Log.d(TAG, "Apple created");
		this.isGold = isGold;
		this.positionX = positionX;
		this.positionY = positionY;
		this.radius = radius;
	}

	public Apple(boolean isGold, float positionX, float positionY) {
		this(isGold, positionX, positionY, STANDARD_SIZE);
	}

	public boolean isCollide(Line line) {
		double xStop = line.xStop;
		double yStop = line.yStop;
		double distance = Math.sqrt((Math.pow(positionX - xStop, 2) + Math.pow(
				positionY - yStop, 2)));
		// if (Constant.DEBUG) Log.d(TAG, "distance:" + distance);
		if (distance < radius) {
			return true;
		} else {
			return false;
		}
	}

	

	public void eat() {
		exists = false;
	}
}
