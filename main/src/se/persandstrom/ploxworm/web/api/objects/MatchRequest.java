package se.persandstrom.ploxworm.web.api.objects;


import com.google.gson.annotations.SerializedName;

public class MatchRequest implements AbstractApiObject {

    public static final String TYPE = "match_request";

    @SerializedName("game_type")
    private GameType gameType;

    private int level;

    @SerializedName("player_name")
    private String playerName;

    @SerializedName("winning_message")
    private String winningMessage;

    public GameType getGameType() {
        return gameType;
    }

    public int getLevel() {
        return level;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getWinningMessage() {
        return winningMessage;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
