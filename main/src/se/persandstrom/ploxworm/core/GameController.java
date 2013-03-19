package se.persandstrom.ploxworm.core;

import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.board.Board;

public interface GameController {


    public float getXacc(Worm player);

    public float getYacc(Worm player);

    public void end(long score);

    public void victory(long score);

    public void setScoreBoard(String score);

    public void setTitle(String title);

    public void setMessage(String message);

    public void hideTitle();

    public void hideMessage();

    public void showMessage();

    public void setNewBoard(Board board);

    public void render();

    public void endWithWait(long score);

    public void updateScore(long score);
}
