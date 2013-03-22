package se.persandstrom.ploxworm.web.api.objects;


import com.google.gson.annotations.SerializedName;

public class MatchRequest {

    public static final String TYPE = "match_request";

    @SerializedName("game_type")
    private GameType gameType;

    private int level;

    public GameType getGameType() {
        return gameType;
    }

    public int getLevel() {
        return level;
    }
}
