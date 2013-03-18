package se.persandstrom.ploxworm.core.worm.board;

import se.persandstrom.ploxworm.Constant;
import se.persandstrom.ploxworm.core.Line;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class Apple {

	protected static final String TAG = "Apple";

	private static final float STANDARD_SIZE = 20;

	public boolean exists;

	public boolean isGold;

	final float positionX;
	final float positionY;

	final float radius;

	private RectF rectF;

	public Apple(boolean isGold, float positionX, float positionY, float radius) {
		if (Constant.DEBUG) Log.d(TAG, "Apple created");
		this.isGold = isGold;
		this.positionX = positionX;
		this.positionY = positionY;
		this.radius = radius;
	}

	public Apple(boolean isGold, float positionX, float positionY) {
		this(isGold, positionX, positionY, STANDARD_SIZE);
	}

	public boolean isCollide(Line line) {
		float xStop = line.xStop;
		float yStop = line.yStop;
		float distance = (float) android.util.FloatMath.sqrt((float) (Math.pow(positionX - xStop, 2) + Math.pow(
				positionY - yStop, 2)));
		// if (Constant.DEBUG) Log.d(TAG, "distance:" + distance);
		if (distance < radius) {
			return true;
		} else {
			return false;
		}
	}

	public void onDraw(Canvas canvas, float xNormalizer, float yNormalizer, Paint paint) {
		if (exists) {
			if (rectF == null) {
				rectF = new RectF((positionX - radius) * xNormalizer, (positionY - radius) * yNormalizer,
						(positionX + radius) * xNormalizer, (positionY + radius) * yNormalizer);
			}
			canvas.drawOval(rectF, paint);
		}
	}

	public void eat() {
		exists = false;
	}
}
