package se.persandstrom.ploxworm.web.api.objects;


import java.util.ArrayList;
import java.util.List;

public class ScoreBoard implements AbstractApiObject {

    public static final String TYPE = "scoreboard";

    final List<Score> scores;

    public ScoreBoard() {
        this.scores = new ArrayList<Score>();
    }

    public void addScore(Score score) {
        scores.add(score);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public class Score {
        String player_name;
        long score;

        public Score(String name, long score) {
            this.player_name = name;
            this.score = score;
        }
    }
}
