package se.persandstrom.ploxworm.web.api.objects;

@SuppressWarnings("UnusedDeclaration")
public class EndRound implements AbstractApiObject {

    public static final String TYPE = "end_round";

    private final EndType endType;
    private final long score;
    private final Integer winnerNumber;
    private final String winnerMessage;

    public EndRound(EndType endType, long score) {
        this.endType = endType;
        this.score = score;
        this.winnerNumber = null;
        this.winnerMessage = null;
    }

    public EndRound(EndType endType, long score, int winnerNumber, String winnerMessage) {
        this.endType = endType;
        this.score = score;
        this.winnerNumber = winnerNumber;
        this.winnerMessage = winnerMessage;
    }

    public enum EndType {
        won, lost, end
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
