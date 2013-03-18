package se.persandstrom.ploxworm.core.worm.ai;

import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.worm.ComputerWorm;

public class StupidWorm extends ComputerWorm {

	protected static final String TAG = "StupidWorm";

	public StupidWorm(Core core, int color, double startPositionX, double startPositionY, double startSpeedX,
			double startSpeedY) {
		super(core, color, startPositionX, startPositionY, startSpeedX, startSpeedY);
	}

	@Override
	protected void fixDirection() {

		int headingForCrash = isHeadingForCrash();

		if (headingForCrash != CRASH_NEITHER) {
//			if (Constant.DEBUG) Log.d(TAG, "headingForCrash!");
			if (headingForCrash == CRASH_LEFT) {
				turnRight(true);
				return;
			}

			if (headingForCrash == CRASH_RIGHT) {
				turnLeft(true);
				return;
			}

			//if the code reach here, then we are heading straight for a crash. Lets see what we do:
			switch (currentDecision) {
			case DIRECTION_LEFT:
				if (currentCrisisDecision == DIRECTION_LEFT) {
					turnLeft(true);
				} else {
					turnRight(true);
				}
				return;
			case DIRECTION_RIGHT:
				if (currentCrisisDecision == DIRECTION_RIGHT) {
					turnRight(true);
				} else {
					turnLeft(true);
				}
				return;
			case DIRECTION_STRAIGHT:
				switch (random.nextInt(2)) {
				case 0:
					turnLeft(true);
					return;
				case 1:
					turnRight(true);
					return;
				}
				return;
			}
		} else {

			if (isHeadingForApple()) {
				goStraight();
				return;
			}

			switch (currentDecision) {
			case DIRECTION_LEFT:
				int r = random.nextInt(100);

				if (r < 20) {
					goStraight();
				} else {
					turnLeft(false);
				}
				return;
			case DIRECTION_RIGHT:
				r = random.nextInt(100);

				if (r < 20) {
					goStraight();
				} else {
					turnRight(false);
				}
				return;
			case DIRECTION_STRAIGHT:
				r = random.nextInt(100);
				if (r < 3) {
					turnRight(false);
				} else if (r < 6) {
					turnLeft(false);
				} else {
					goStraight();
				}
				return;
			}
			goStraight();
		}
	}

	@Override
	public boolean isAi() {
		return true;
	}
}
