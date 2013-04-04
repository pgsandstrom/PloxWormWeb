package se.persandstrom.ploxworm.core.worm.board;

/**
 * User: pesandst
 * Date: 2013-04-03
 * Time: 15:45
 */
public class StartPosition {

    public final double x;
    public final double y;

    public final double startSpeedX;
    public final double startSpeedY;

    public StartPosition(double x, double y, double startSpeedX, double startSpeedY) {
        this.x = x;
        this.y = y;
        this.startSpeedX = startSpeedX;
        this.startSpeedY = startSpeedY;
    }
}
