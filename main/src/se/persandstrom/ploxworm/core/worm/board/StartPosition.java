package se.persandstrom.ploxworm.core.worm.board;

/**
 * User: pesandst
 * Date: 2013-04-03
 * Time: 15:45
 */
public class StartPosition {

    public final int x;
    public final int y;

    public final int startSpeedX;
    public final int startSpeedY;

    public StartPosition(int x, int y, int startSpeedX, int startSpeedY) {
        this.x = x;
        this.y = y;
        this.startSpeedX = startSpeedX;
        this.startSpeedY = startSpeedY;
    }
}
