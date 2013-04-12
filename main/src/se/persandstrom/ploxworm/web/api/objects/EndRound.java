package se.persandstrom.ploxworm.web.api.objects;

public class EndRound {

    public static final String TYPE = "end_round";

    private EndType endType;
    private long score;

    public EndRound(EndType endType, long score) {
        this.endType = endType;
        this.score = score;
    }

    public enum EndType {
        won, lost, end
    }

    public static String getType() {
        return TYPE;
    }
}
