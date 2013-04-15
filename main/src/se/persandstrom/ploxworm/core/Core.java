package se.persandstrom.ploxworm.core;

import se.persandstrom.ploxworm.core.worm.HumanWorm;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.ai.StupidWorm;
import se.persandstrom.ploxworm.core.worm.board.*;

import java.util.List;

public class Core {

    protected static final String TAG = "Core";

    // point related constants
    private static final int POINTS_APPLE = 50;
    private static final int POINTS_VICTORY = 500;

    final GameController gameController;

    // the game thread:
    GameThread gameThread;

    public boolean gameStarted = false;
    public boolean gameRunning = false;

    // number of tics since start!
    int counter = 0;

    int level;
    int aliveHumanCount = 0;
    int aliveAiCount = 0;

    Board board;
    List<Worm> wormList;

    // specific game settings:

    // resurrect on death and never spawn gold apple
    private boolean eternalGame = false;

    private Core(GameController gameController) {
        this.gameController = gameController;
    }

    public void setLevel(int level, BoardType boardType) {  //TODO level should be inside BoardType or something
        this.level = level;
        Board board = BoardManager.getBoard(this, level, boardType);
        this.board = board;
    }

    public List<Worm> getWormList() {
        return wormList;
    }

    public List<Apple> getAppleList() {
        return board.getApples();
    }

    public void startGame(int level, long score, BoardType boardType) {
        // if (Constant.DEBUG)
        // Log.d(TAG, "startGame: " + level + ", " + score);

        Board board = BoardManager.getBoard(this, level, boardType);
        this.level = level;
        this.board = board;

        startGame();
    }

    public void startGame() {
        // if (Constant.DEBUG)
        // Log.d(TAG, "startGame");

        wormList = board.getWormList();
        setWormTypeCounter(wormList);

        gameController.setNewBoard(board);
        gameController.setTitle(board.title);

        new StartCountDownThread().start();
    }

    private void setWormTypeCounter(List<Worm> wormList) {
        for (Worm worm : wormList) {
            if (worm.isAi()) {
                aliveAiCount++;
            } else {
                aliveHumanCount++;
            }
        }
    }

    /**
     * Starts the game thread.
     */
    public void go() {

        gameStarted = true;
        gameRunning = true;
        if (gameThread != null) {
            gameThread.run = false;
        }
        gameThread = new GameThread();
        gameThread.start();
    }

    public void stop() {
        counter = 0;
        gameStarted = false;
        gameRunning = false;
    }

    /**
     * This can be called even if the worm disconnects, closes the app or whatever
     *
     * @param deadWorm
     * @param expected if the dead worm expected the game to end, i.e. terminated the session themselves somehow
     */
    public void death(Worm deadWorm, boolean expected) {
        gameController.death(deadWorm, expected);

        if (deadWorm.isAi()) {
            aliveAiCount--;
        } else {
            aliveHumanCount--;
        }

        deadWorm.isAlive = false;

        boolean endGame = false;

        if ((!deadWorm.isAi() && aliveHumanCount == 1)) {
            //human died, only one human left
            endGame = true;
        } else if ((!deadWorm.isAi() && aliveHumanCount == 0)) {
            //human died, no humans left
            endGame = true;
        } else if (deadWorm.isAi() && aliveAiCount == 0 && aliveHumanCount == 1) {
            //ai died, a human is all that is left
            endGame = true;
        }

        if (endGame) {
            stop();
        }

        for (Worm worm : wormList) {
            if (!worm.isAi()) {
                gameController.end((HumanWorm) worm, worm.isAlive, false);
            }
        }
    }

    public void victory(Worm victoryWorm) {
        for (Worm worm : wormList) {
            if (worm instanceof HumanWorm) {

            }
        }
    }


    /**
     * Moves the gameworld one tic forward!
     */
    public void tic() {

        counter++;

        // maybe it would be prettier to call tic on the board? I dunno, think
        // about it.
        // we cant till for ALL worms.... what if computer worm dies?
        int size = wormList.size() - 1;
        for (int i = size; i >= 0; i--) {
            Worm worm = wormList.get(i);
            int makeMove = worm.makeMove();
            if (makeMove == Worm.MOVE_DEATH) {
                if (eternalGame) {
                    worm.reset();
                } else {
                    death(worm, false);
                }
            }
        }

        gameController.render();
    }

    public void ateApple(Worm worm) {
        increaseScore(worm, POINTS_APPLE);
    }

    private void increaseScore(Worm worm, int newScore) {
        worm.score += newScore;
        gameController.updateScore(wormList);
    }

    public float getXacc(HumanWorm worm) {
        return gameController.getXacc(worm);
    }

    public float getYacc(HumanWorm worm) {
        return gameController.getYacc(worm);
    }

    private class GameThread extends Thread {

        long duration;

        public boolean run = true;

        @Override
        public void run() {

            while (gameRunning && run) {
                try {
                    Thread.sleep(Math.max(0, 50 - duration));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long currentTimeMillis = System.currentTimeMillis();
                tic();
                duration = System.currentTimeMillis() - currentTimeMillis;
                // if (Constant.DEBUG) Log.d(TAG, "tic time: " + duration);
            }
        }
    }

    private class StartCountDownThread extends Thread {
        private static final int STEP_WAITING_TIME = 300;

        int countSteps = 3;

        @Override
        public void run() {
            do {
                gameController.showMessage("" + countSteps);
                try {
                    Thread.sleep(STEP_WAITING_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countSteps--;
            } while (countSteps > 0);

            gameController.hideTitle();
            gameController.hideMessage();

            go();
        }
    }


    /**
     * Builder class
     *
     * @author Per Sandström
     */
    public static class Builder {

        private Core core;

        private int level = 1;
        private BoardType boardType = BoardType.SINGLE;
        private long score = 0;
        boolean makePlayersToAi = false;
        boolean eternalGame = false;

        private boolean isBuilt = false;

        public Builder(GameController gameController) {
            core = new Core(gameController);
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public void setBoardType(BoardType boardType) {
            this.boardType = boardType;
        }

        public void setScore(long score) {
            this.score = score;
        }

        public void setEternalGame(boolean eternalGame) {
            this.eternalGame = eternalGame;
        }

        public void setMakePlayersToAi(boolean makePlayersToAi) {
            this.makePlayersToAi = makePlayersToAi;
        }

        public Core build() {
            core.setLevel(level, boardType);
            if (isBuilt) {
                throw new IllegalStateException("cannot build twice");
            } else {
                setupCore();
                isBuilt = true;
                return core;
            }
        }

        public Core start() {
            setupCore();
            core.startGame();
            return core;
        }

        /**
         * set the variables that has been specified to the core
         */
        private void setupCore() {
            core.eternalGame = eternalGame;
            if (eternalGame) {
                makePlayersToAi();
                core.board.setAppleEatGoal(Integer.MAX_VALUE / 2);
            }
        }

        /**
         * Set all human controlled worms to become computers controlled.
         */
        private void makePlayersToAi() {
            List<Worm> wormList = core.board.getWormList();
            for (int i = 0; i < wormList.size(); i++) {
                Worm worm = wormList.get(i);
                if (worm instanceof HumanWorm) {
                    StupidWorm stupidWorm = new StupidWorm(core, worm.color, new StartPosition(worm.xPos, worm.yPos,
                            worm.xForce, worm.yForce));
                    stupidWorm.init(worm.board);
                    wormList.remove(i);
                    wormList.add(i, stupidWorm);
                }
            }
        }
    }
}