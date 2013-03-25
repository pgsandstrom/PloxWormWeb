package se.persandstrom.ploxworm.web.api.objects;

public class EndRound {

    public static final String TYPE = "end_round";

    private EndType endType;

    public EndRound(EndType endType) {
        this.endType = endType;
    }

    public enum EndType {
        won, lost, end
    }
}
