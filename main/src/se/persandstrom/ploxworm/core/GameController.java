package se.persandstrom.ploxworm.core;

import se.persandstrom.ploxworm.core.worm.HumanWorm;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.web.Player;

import java.util.List;

public interface GameController {


    public float getXacc(HumanWorm worm);

    public float getYacc(HumanWorm worm);

    public void death(Worm worm, boolean expected);

    public void end(HumanWorm worm, boolean victory, boolean expected, int winnerNumber);

    public void updateScore(List<Worm> wormList);

    public void showTitle(String title);

    public void hideTitle();

    public void showMessage(String message);

    public void hideMessage();

    public void setNewBoard(Board board);

    public void addPlayer(Player player);

    /**
     *
     * @param player
     * @return if the game ended
     */
    public boolean removePlayer(Player player);

    public void render();
}
