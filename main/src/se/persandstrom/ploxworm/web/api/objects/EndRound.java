package se.persandstrom.ploxworm.web.api.objects;

@SuppressWarnings("UnusedDeclaration")
public class EndRound implements AbstractApiObject {

    public static final String TYPE = "end_round";

    private final EndType endType;
    private final long score;
    private final Integer winner_number;
    private final String winner_message;

    public EndRound(EndType endType, long score) {
        this.endType = endType;
        this.score = score;
        this.winner_number = null;
        this.winner_message = null;
    }

    public EndRound(EndType endType, long score, int winnerNumber, String winnerMessage) {
        this.endType = endType;
        this.score = score;
        this.winner_number = winnerNumber;
        this.winner_message = winnerMessage;
    }

    public enum EndType {
        won, lost, end
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
