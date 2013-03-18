package se.persandstrom.ploxworm.core;

public class Line {

    //start:
    public final double xStart;
    public final double yStart;

    //stop:
    public final double xStop;
    public final double yStop;

    //func:
    public final double a;
    public final double c;

    public Line(double xStart, double yStart, double xStop, double yStop) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xStop = xStop;
        this.yStop = yStop;

        a = (yStop - yStart) / (xStop - xStart);
        c = yStart - a * xStart;
    }

   
}