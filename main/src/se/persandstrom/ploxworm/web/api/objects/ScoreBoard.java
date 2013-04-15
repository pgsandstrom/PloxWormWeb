package se.persandstrom.ploxworm.web.api.objects;


import se.persandstrom.ploxworm.core.worm.board.Obstacle;

import java.util.ArrayList;
import java.util.List;

public class ScoreBoard {

    public static final String TYPE = "scoreboard";


    final List<Score> scores;

    public ScoreBoard() {
        this.scores = new ArrayList<Score>();
    }

    public void addScore(Score score) {
        scores.add(score);
    }

    public class Score {
        String name;
        long score;

        public Score(String name, long score) {
            this.name = name;
            this.score = score;
        }
    }
}
