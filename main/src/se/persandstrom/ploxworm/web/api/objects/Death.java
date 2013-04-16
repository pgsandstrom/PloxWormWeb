package se.persandstrom.ploxworm.web.api.objects;

/**
 * User: Per Sandstrom
 * Date: 2013-04-16 13:31
 */
public class Death implements AbstractApiObject {

    public static final String TYPE = "death";

    private final Integer player_number;

    public Death() {
        player_number = null;
    }

    public Death(int playerNumber) {
        this.player_number = playerNumber;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
