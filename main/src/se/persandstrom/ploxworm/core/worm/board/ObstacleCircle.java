package se.persandstrom.ploxworm.core.worm.board;

import se.persandstrom.ploxworm.core.Line;


public class ObstacleCircle implements Obstacle {

	protected static final String TAG = "ObstacleCircle";

	public final float positionX;
	public final float positionY;

	public final float radius;

	public ObstacleCircle(float positionX, float positionY, float radius) {
		this.positionX = positionX;
		this.positionY = positionY;

		this.radius = radius;
	}

	@Override
	public boolean isCollide(Line line) {
		double xStop = line.xStop;
		double yStop = line.yStop;

		float distance = (float) Math.sqrt(Math.pow(positionX - xStop, 2) + Math.pow(positionY - yStop, 2));
		//if (Constant.DEBUG) Log.d(TAG, "distance:" + distance);
		if (distance < radius) {
			return true;
		} else {
			return false;
		}
	}

}
