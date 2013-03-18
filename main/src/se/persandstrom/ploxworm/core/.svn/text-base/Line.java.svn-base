package se.persandstrom.ploxworm.core;

public class Line {

    //start:
    public final float xStart;
    public final float yStart;

    //stop:
    public final float xStop;
    public final float yStop;

    //func:
    public final float a;
    public final float c;

    public Line(float xStart, float yStart, float xStop, float yStop) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xStop = xStop;
        this.yStop = yStop;

        a = (yStop - yStart) / (xStop - xStart);
        c = yStart - a * xStart;
    }

   
}