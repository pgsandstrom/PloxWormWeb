package se.persandstrom.ploxworm.core.worm.board;

import se.persandstrom.ploxworm.core.Line;

public class ObstacleRectangle implements Obstacle {

	protected static final String TAG = "ObstacleCircle";

	public final float left;
	public final float top;
	public final float right;
	public final float bottom;

	public ObstacleRectangle(float left, float top, float right, float bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	@Override
	public boolean isCollide(Line line) {
		double xStop = line.xStop;
		double yStop = line.yStop;

		if (xStop > left && xStop < right && yStop > top && yStop < bottom) {
			return true;
		} else {
			return false;
		}
	}


}
