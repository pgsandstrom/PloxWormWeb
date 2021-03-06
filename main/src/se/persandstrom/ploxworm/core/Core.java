package se.persandstrom.ploxworm.core;

import org.apache.log4j.Logger;
import se.persandstrom.ploxworm.core.worm.HumanWorm;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.ai.StupidWorm;
import se.persandstrom.ploxworm.core.worm.board.*;
import se.persandstrom.ploxworm.web.MatchMaker;

import java.util.List;
import java.util.Random;

/**
 * Holds the game thread and ties all parts of the game together
 */
public class Core {

    static final String TAG = "Core";
    static Logger log = Logger.getLogger(Core.class.getName());

    // point related constants
    private static final int POINTS_APPLE = 50;
    private static final int POINTS_VICTORY = 500;

    private final GameController gameController;
    private final Random random;

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
        this.random = new Random();
    }

    public void setLevel(int level, BoardType boardType) {  //TODO level should be inside BoardType or something
        this.level = level;
        Board board = BoardManager.getBoard(this, level, boardType);
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public List<Worm> getWormList() {
        return wormList;
    }

    public List<Apple> getAppleList() {
        return board.getApples();
    }

    public StartPosition getRandomStartposition() {
        List<StartPosition> startPositionList = board.getStartPositionList();
        int position = random.nextInt(startPositionList.size());
        return startPositionList.get(position);
    }

    public void startGame(int level, BoardType boardType) {
        log.debug("startGame: " + level);

        Board board = BoardManager.getBoard(this, level, boardType);
        this.level = level;
        this.board = board;

        startGame();
    }

    public void startGame() {
        log.debug("startGame");

        counterWormTypes();

        wormList = board.getWormList();
        gameController.setNewBoard(board);
        gameController.showTitle(board.title);

        if (aliveHumanCount > 0) {
            new StartCountDownThread().start();
        } else {
            go();
        }
    }

    public void addWorm(Worm worm) {
        board.addWorm(worm);
        worm.init(board);
        setScore(worm, 0);
        counterWormTypes();
    }

    public void removeWorm(Worm worm) {
        if (worm.isAlive) {
            throw new IllegalStateException("kill the worm first");
        }
        board.removeWorm(worm);

    }

    private void counterWormTypes() {
        int aliveAiCountTemp = 0;
        int aliveHumanCountTemp = 0;
        for (Worm worm : board.getWormList()) {
            if (worm.isAi()) {
                aliveAiCountTemp++;
            } else {
                aliveHumanCountTemp++;
            }
        }
        aliveHumanCount = aliveHumanCountTemp;
        aliveAiCount = aliveAiCountTemp;
    }

    public int getAliveHumanCount() {
        return aliveHumanCount;
    }

    /**
     * Starts the game thread.
     */
    public void go() {
        log.info("game go");
        gameStarted = true;
        gameRunning = true;
        if (gameThread != null) {
            gameThread.run = false;
        }
        gameThread = new GameThread();
        gameThread.start();

        gameController.updateScore(wormList);
    }

    public void stop() {
        log.info("game stopped");
        counter = 0;
        gameStarted = false;
        gameRunning = false;
    }

    /**
     * This can be called even if the worm disconnects, closes the app or whatever
     *
     * @param deadWorm
     * @param expected if the dead worm expected the game to end, i.e. terminated the session themselves somehow
     * @return if the game ended
     */
    public boolean death(Worm deadWorm, boolean expected) {
        gameController.death(deadWorm, expected);

        if (deadWorm.isAi()) {
            aliveAiCount--;
        } else {
            aliveHumanCount--;
        }

        log.debug("death. AliveHumanCount: " + aliveHumanCount);
        log.debug("AliveAiCount: " + aliveAiCount + ", observers: " + gameController.getObserverCount());

        gameController.updateScore(wormList);
        deadWorm.isAlive = false;

        boolean endGame = false;


        if (board.getType() == BoardType.ETERNAL) {
            //eternal game ends only when all observers and players are gone
            if (gameController.getObserverCount() == 0 && aliveHumanCount == 0) {
                endGame = true;
            }
        }else if(aliveHumanCount == 0) {
            //death and no human left? Bam, exit.
            endGame = true;
        } else if (aliveHumanCount == 1 && aliveAiCount == 0) {
            //death and only one human left? EXIT TIME!
            endGame = true;
        }


        if (!endGame) {
            return false;
        } else {
            stop();

            //we only support ONE winner...
            Worm victoryWorm = null;
            for (Worm worm : wormList) {
                if (worm.isAlive) {
                    victoryWorm = worm;
                    increaseScore(victoryWorm, POINTS_VICTORY);
                    break;
                }
            }

            for (Worm worm : wormList) {
                if (!worm.isAi()) {
                    if (victoryWorm == null) {
                        gameController.end((HumanWorm) worm, worm.isAlive, false, -1);
                    } else if (victoryWorm instanceof HumanWorm) {
                        HumanWorm humanWinner = (HumanWorm) victoryWorm;
                        gameController.end((HumanWorm) worm, worm.isAlive, false, humanWinner.getPlayerNumber());
                    } else {
                        //computer won:
                        //TODO send out cpu winner message?
                        gameController.end((HumanWorm) worm, worm.isAlive, false, -1);
                    }
                }
            }

            return true;
        }
    }


    public void victory(Worm victoryWorm) {
        //WARNING: This is not always called when the game ends or a worm is appointed victor.

        increaseScore(victoryWorm, POINTS_VICTORY);

        int winnerPlayerId;
        if (victoryWorm instanceof HumanWorm) {
            winnerPlayerId = ((HumanWorm) victoryWorm).getPlayerNumber();
        } else {
            winnerPlayerId = -1;
        }

        for (Worm worm : wormList) {
            if (!(worm instanceof HumanWorm)) {
                continue;
            }
            gameController.end((HumanWorm) worm, worm == victoryWorm, false, winnerPlayerId);
        }

        stop();
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
                    worm.setStartPosition(getRandomStartposition());
                    setScore(worm, 0);
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

    private void increaseScore(Worm worm, long newScore) {
        setScore(worm, worm.score + newScore);
    }

    private void setScore(Worm worm, long newScore) {
        worm.score = newScore;
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
            }
        }
    }

    private class StartCountDownThread extends Thread {
        private static final int STEP_WAITING_TIME = 1000;

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
        boolean makePlayersToAi = false;    //for demonstration games etc
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
            if (makePlayersToAi) {
                makePlayersToAi();
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
                    StupidWorm stupidWorm = new StupidWorm(core, new StartPosition(worm.xPos, worm.yPos, worm.xForce,
                            worm.yForce));
                    stupidWorm.init(worm.board);
                    wormList.remove(i);
                    wormList.add(i, stupidWorm);
                }
            }
        }
    }
}