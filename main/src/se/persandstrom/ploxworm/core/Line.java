package se.persandstrom.ploxworm.core;

public class Line {

    public final double xStart;
    public final double yStart;

    public final double xStop;
    public final double yStop;

    public Line(double xStart, double yStart, double xStop, double yStop) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xStop = xStop;
        this.yStop = yStop;
    }
}