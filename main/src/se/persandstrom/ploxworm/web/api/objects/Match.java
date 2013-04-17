package se.persandstrom.ploxworm.web.api.objects;


import se.persandstrom.ploxworm.core.worm.board.Obstacle;

import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class Match implements AbstractApiObject{

    public static final String TYPE = "match";

    int your_number;

    private final int size_x;
    private final int size_y;

    List<Obstacle> obstacles;

    List<PlayerDto> players;

    public Match(int size_x, int size_y, List<Obstacle> obstacles) {
        this.size_x = size_x;
        this.size_y = size_y;
        this.obstacles = obstacles;
    }

    public void setYourNumber(int yourNumber) {
        this.your_number = yourNumber;
    }

    public void setPlayers(List<PlayerDto> players) {
        this.players = players;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
