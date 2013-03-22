package se.persandstrom.ploxworm.web.api.objects;


import se.persandstrom.ploxworm.core.worm.board.Obstacle;

import java.util.List;

public class Match {

    public static final String TYPE = "match";

    int your_number;

    int size_x;
    int size_y;

    List<Obstacle> obstacles;

    public Match(int size_x, int size_y, List<Obstacle> obstacles) {
        this.size_x = size_x;
        this.size_y = size_y;
        this.obstacles = obstacles;
    }

    public void setYour_number(int your_number) {
        this.your_number = your_number;
    }
}
