package se.persandstrom.ploxworm.core.worm;

import se.persandstrom.ploxworm.Constant;
import se.persandstrom.ploxworm.core.Core;
import android.graphics.Paint;
import android.util.Log;

public class HumanWorm extends Worm {

	protected static final String TAG = "HumanWorm";

	public HumanWorm(Core core, Paint paint, float startPositionX, float startPositionY, float startSpeedX,
			float startSpeedY) {
		super(core, paint, startPositionX, startPositionY, startSpeedX, startSpeedY);
		if (Constant.DEBUG) Log.d(TAG, "HumanWorm created");
	}

	@Override
	protected void fixDirection() {

		// get the acceleration from the device:
		float xAcc = core.xAcc;
		float yAcc = core.yAcc;

		float wormDegree = (float) Math.atan2(yForce, xForce);
		if (wormDegree < 0) {
			wormDegree += Math.PI * 2;
		}

		float playerDegree = (float) Math.atan2(yAcc, xAcc);
		if (playerDegree < 0) {
			playerDegree += Math.PI * 2;
		}

		double diffDegree = Math.abs(playerDegree - wormDegree);
		if (wormDegree >= Math.PI) {

			if (playerDegree < wormDegree && playerDegree > wormDegree - Math.PI) { // wormDegree ska minska:

				if (diffDegree > MAX_DEGREE_TURN) { // minska med max:
					wormDegree -= MAX_DEGREE_TURN;
				} else { // minska till playerDegree:
					wormDegree = playerDegree;
				}
			} else { // wormDegree ska öka:

				if (diffDegree > MAX_DEGREE_TURN) {
					wormDegree += MAX_DEGREE_TURN;
				} else {
					wormDegree = playerDegree;
				}
			}

		} else {
			if (playerDegree > wormDegree && playerDegree < wormDegree + Math.PI) { // wormDegree ska öka:

				if (diffDegree > MAX_DEGREE_TURN) {
					wormDegree += MAX_DEGREE_TURN;
				} else {
					wormDegree = playerDegree;
				}

			} else { // wormDegree ska minska:

				if (diffDegree > MAX_DEGREE_TURN) { // minska med max:
					wormDegree -= MAX_DEGREE_TURN;
				} else { // minska till playerDegree:
					wormDegree = playerDegree;
				}
			}
		}

		xForce = android.util.FloatMath.cos(wormDegree);
		yForce = android.util.FloatMath.sin(wormDegree);

	}

	@Override
	public boolean isAi() {
		return false;
	}

}
