package se.persandstrom.ploxworm.core;

import se.persandstrom.ploxworm.core.worm.HumanWorm;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.ai.StupidWorm;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.core.worm.board.BoardManager;

import java.util.ArrayList;

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
    long score = 0;
    int aliveAiCount = 0;

    Board board;
    ArrayList<Worm> wormList;

    // specific game settings:

    // resurrect on death and never spawn gold apple
    private boolean eternalGame = false;
    private float xacc;

    private Core(GameController gameController) {
        this.gameController = gameController;
    }

    public void setLevel(int level) {
        this.level = level;
        Board board = BoardManager.getBoard(this, level);
        this.board = board;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public ArrayList<Worm> getWormList() {
        return wormList;
    }

    public void startGame(int level, long score) {
        // if (Constant.DEBUG)
        // Log.d(TAG, "startGame: " + level + ", " + score);

        Board board = BoardManager.getBoard(this, level);
        this.level = level;
        this.score = score;
        this.board = board;

        wormList = board.getWormList();
        setAiCounter(wormList);

        gameController.setNewBoard(board);

        gameController.setTitle(board.title);

        new StartCountDownThread().start();
    }

    public void startGame() {
        // if (Constant.DEBUG)
        // Log.d(TAG, "startGame");

        wormList = board.getWormList();

        setAiCounter(wormList);

        gameController.setNewBoard(board);

        gameController.setTitle(board.title);

        new StartCountDownThread().start();
    }

    private void setAiCounter(ArrayList<Worm> wormList) {
        for (Worm worm : wormList) {
            if (worm.isAi()) {
                aliveAiCount++;
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

        gameController.setScoreBoard(String.valueOf(score));
    }

    public void stop() {
        counter = 0;
        gameStarted = false;
        gameRunning = false;
    }

    public void death(boolean waitBeforeExiting) {
        stop();

        if (waitBeforeExiting) {
            gameController.endWithWait(score);
        } else {
            gameController.end(score);

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
                if (worm instanceof HumanWorm) {
                    death(true);
                } else {
                    if (eternalGame) {
                        worm.reset();
                    } else {
                        worm.isAlive = false;
                        aliveAiCount--;
                    }
                }
            }
        }

        gameController.render();
    }

    public void ateApple() {
        increaseScore(POINTS_APPLE);
    }

    public void victory() {

        if (eternalGame) {

        }

        increaseScore(POINTS_VICTORY);
        stop();

        gameController.victory(score);
    }

    private void increaseScore(int newScore) {
        score += newScore;
        gameController.updateScore(score);
    }


    public void backPress() {
        death(false);
    }

    public float getXacc(Worm worm) {
        return gameController.getXacc(worm);
    }

    public float getYacc(Worm worm) {
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

//	private class StartCountDownThread extends AsyncTask<Void, Integer, Void> {
//
//		private static final int STEP_WAITING_TIME = 300;
//
//		int countSteps = 3;
//
//		public StartCountDownThread() {
//		}
//
//		@Override
//		protected Void doInBackground(Void... params) {
//
//			do {
//				publishProgress(countSteps);
//				try {
//					Thread.sleep(STEP_WAITING_TIME);
//				} catch (InterruptedException quiet) {
//				}
//				countSteps--;
//			} while (countSteps > 0);
//
//			return null;
//		}
//
//		@Override
//		protected void onProgressUpdate(Integer... values) {
//			gameController.setMessage(String.valueOf(values[0]));
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			gameController.hideTitle();
//			gameController.hideMessage();
//
//			go();
//		}
//	}

    private class StartCountDownThread extends Thread {
        private static final int STEP_WAITING_TIME = 300;

        int countSteps = 3;

        @Override
        public void run() {
            do {
                gameController.setMessage("" + countSteps);
                try {
                    Thread.sleep(STEP_WAITING_TIME);
                } catch (InterruptedException quiet) {
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
            core.setLevel(level);
            core.setScore(score);
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
            ArrayList<Worm> wormList = core.board.getWormList();
            for (int i = 0; i < wormList.size(); i++) {
                Worm worm = wormList.get(i);
                if (worm instanceof HumanWorm) {
                    StupidWorm stupidWorm = new StupidWorm(core, worm.color, worm.xPos, worm.yPos, worm.xForce,
                            worm.yForce);
                    stupidWorm.init(worm.board);
                    wormList.remove(i);
                    wormList.add(i, stupidWorm);
                }
            }
        }
    }
}