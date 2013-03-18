package se.persandstrom.ploxworm.core.worm.board;

import java.util.ArrayList;

import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.GameView;
import se.persandstrom.ploxworm.core.worm.HumanWorm;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.ai.StupidWorm;

public class BoardManager {

	protected final static String TAG = "BoardManager";

	public final static int TOTAL_LEVELS = 7;

	public static Board getBoard(Core core, int level) {
		//		level = TOTAL_LEVELS;
		Board board;
		switch (level) {
		case 1:
			board = level1(core);
			break;
		case 2:
			board = level2(core);
			break;
		case 3:
			board = level3(core);
			break;
		case 4:
			board = level4(core);
			break;
		case 5:
			board = level5(core);
			break;
		case 6:
			board = level6(core);
			break;
		case 7:
			board = level7(core);
			break;
		default:
			throw new AssertionError("wtf level did not exist");
		}

		for (Worm worm : board.getWormList()) {
			worm.init(board);
		}

		return board;
	}

	private static Board level1(Core core) {
		String title = "Journey begins!";

		ArrayList<Worm> wormList = new ArrayList<Worm>();
		ArrayList<Obstacle> obstacleList = new ArrayList<Obstacle>();
		ArrayList<Apple> appleList = new ArrayList<Apple>();
		int xSize = 800;
		int ySize = 800;
		int appleEatGoal = 4;
		int appleVisibleAtOnce = 2;

		//obstacles:

		//apples:
		appleList.add(new Apple(false, 200, 200));
		appleList.add(new Apple(false, 600, 200));
		appleList.add(new Apple(false, 200, 600));
		appleList.add(new Apple(false, 600, 600));

		//worms:
		float startPositionX = 400;
		float startPositionY = 400;
		float startSpeedX = 1;
		float startSpeedY = 1;

		wormList.add(new HumanWorm(core, GameView.wormPaints[0], startPositionX, startPositionY, startSpeedX,
				startSpeedY));

		return new Board(core, title, wormList, obstacleList, appleList, xSize, ySize, appleEatGoal, appleVisibleAtOnce);
	}

	private static Board level2(Core core) {
		String title = "Obstacles in the way!";

		ArrayList<Worm> wormList = new ArrayList<Worm>();
		ArrayList<Obstacle> obstacleList = new ArrayList<Obstacle>();
		ArrayList<Apple> appleList = new ArrayList<Apple>();
		int xSize = 800;
		int ySize = 800;
		int appleEatGoal = 4;
		int appleVisibleAtOnce = 2;

		//obstacles:
		obstacleList.add(new ObstacleCircle(0, 0, 200));
		obstacleList.add(new ObstacleRectangle(600, 600, 800, 800));

		//apples:
		appleList.add(new Apple(false, 150, 550));
		appleList.add(new Apple(false, 250, 650));
		appleList.add(new Apple(false, 550, 150));
		appleList.add(new Apple(false, 650, 250));

		//worms:
		float startPositionX = 400;
		float startPositionY = 400;
		float startSpeedX = 1;
		float startSpeedY = 1;

		wormList.add(new HumanWorm(core, GameView.wormPaints[0], startPositionX, startPositionY, startSpeedX,
				startSpeedY));

		return new Board(core, title, wormList, obstacleList, appleList, xSize, ySize, appleEatGoal, appleVisibleAtOnce);
	}

	private static Board level3(Core core) {
		String title = "Trancend the border!";

		ArrayList<Worm> wormList = new ArrayList<Worm>();
		ArrayList<Obstacle> obstacleList = new ArrayList<Obstacle>();
		ArrayList<Apple> appleList = new ArrayList<Apple>();
		int xSize = 800;
		int ySize = 800;
		int appleEatGoal = 4;
		int appleVisibleAtOnce = 1;

		//obstacles:
		obstacleList.add(new ObstacleRectangle(390, 0, 410, 800));

		//apples:
		appleList.add(new Apple(false, 200, 400));
		appleList.add(new Apple(false, 600, 400));

		//worms:
		float startPositionX = 200;
		float startPositionY = 200;
		float startSpeedX = 1;
		float startSpeedY = 1;

		wormList.add(new HumanWorm(core, GameView.wormPaints[0], startPositionX, startPositionY, startSpeedX,
				startSpeedY));

		return new Board(core, title, wormList, obstacleList, appleList, xSize, ySize, appleEatGoal, appleVisibleAtOnce);
	}

	private static Board level4(Core core) {
		String title = "Dont trancend the border!";

		ArrayList<Worm> wormList = new ArrayList<Worm>();
		ArrayList<Obstacle> obstacleList = new ArrayList<Obstacle>();
		ArrayList<Apple> appleList = new ArrayList<Apple>();
		int xSize = 800;
		int ySize = 800;
		int appleEatGoal = 6;
		int appleVisibleAtOnce = 3;

		//obstacles:
		obstacleList.add(new ObstacleCircle(400, 400, 200));
		obstacleList.add(new ObstacleRectangle(0, 0, 10, 800));
		obstacleList.add(new ObstacleRectangle(0, 0, 800, 10));
		obstacleList.add(new ObstacleRectangle(0, 790, 800, 800));
		obstacleList.add(new ObstacleRectangle(790, 0, 800, 800));

		//apples:
		appleList.add(new Apple(false, 150, 150));
		appleList.add(new Apple(false, 150, 650));
		appleList.add(new Apple(false, 650, 150));
		appleList.add(new Apple(false, 650, 650));

		//worms:
		float startPositionX = 120;
		float startPositionY = 200;
		float startSpeedX = 1;
		float startSpeedY = 1;

		wormList.add(new HumanWorm(core, GameView.wormPaints[0], startPositionX, startPositionY, startSpeedX,
				startSpeedY));

		return new Board(core, title, wormList, obstacleList, appleList, xSize, ySize, appleEatGoal, appleVisibleAtOnce);
	}

	private static Board level5(Core core) {
		String title = "Le Lind";

		ArrayList<Worm> wormList = new ArrayList<Worm>();
		ArrayList<Obstacle> obstacleList = new ArrayList<Obstacle>();
		ArrayList<Apple> appleList = new ArrayList<Apple>();
		int xSize = 800;
		int ySize = 800;
		int appleEatGoal = 10;
		int appleVisibleAtOnce = 2;

		//obstacles:
		obstacleList.add(new ObstacleRectangle(-100, -100, 10, 900));
		obstacleList.add(new ObstacleRectangle(-100, -100, 900, 10));
		obstacleList.add(new ObstacleRectangle(-100, 790, 900, 900));
		obstacleList.add(new ObstacleRectangle(790, -100, 900, 900));
		obstacleList.add(new ObstacleCircle(200, 200, 120));
		obstacleList.add(new ObstacleCircle(200, 600, 120));
		obstacleList.add(new ObstacleCircle(600, 200, 120));
		obstacleList.add(new ObstacleCircle(600, 600, 120));

		//apples:
		appleList.add(new Apple(false, 75, 75));
		appleList.add(new Apple(false, 350, 450));
		appleList.add(new Apple(false, 450, 350));
		appleList.add(new Apple(false, 725, 725));

		//worms:
		float startPositionX = 200;
		float startPositionY = 400;
		float startSpeedX = 1;
		float startSpeedY = 1;

		wormList.add(new HumanWorm(core, GameView.wormPaints[0], startPositionX, startPositionY, startSpeedX,
				startSpeedY));

		return new Board(core, title, wormList, obstacleList, appleList, xSize, ySize, appleEatGoal, appleVisibleAtOnce);
	}

	private static Board level6(Core core) {
		String title = "Meet Bob";

		ArrayList<Worm> wormList = new ArrayList<Worm>();
		ArrayList<Obstacle> obstacleList = new ArrayList<Obstacle>();
		ArrayList<Apple> appleList = new ArrayList<Apple>();
		int xSize = 800;
		int ySize = 800;
		int appleEatGoal = 20;
		int appleVisibleAtOnce = 2;

		//obstacles:

		//apples:
		appleList.add(new Apple(false, 200, 200));
		appleList.add(new Apple(false, 600, 200));
		appleList.add(new Apple(false, 200, 600));
		appleList.add(new Apple(false, 600, 600));

		//worms:
		float startPositionX = 200;
		float startPositionY = 400;
		float startSpeedX = 1;
		float startSpeedY = 1;

		wormList.add(new HumanWorm(core, GameView.wormPaints[0], startPositionX, startPositionY, startSpeedX,
				startSpeedY));

		//computer: 
		startPositionX = 600;
		startPositionY = 400;
		startSpeedX = 1;
		startSpeedY = 1;

		wormList.add(new StupidWorm(core, GameView.wormPaints[1], startPositionX, startPositionY, startSpeedX,
				startSpeedY));

		startPositionX = 400;
		startPositionY = 200;
		startSpeedX = 1;
		startSpeedY = 1;
		//
		//		wormList.add(new StupidWorm(core,GameView.wormPaints[2], startPositionX, startPositionY, startSpeedX, startSpeedY));
		//
		//		startPositionX = 400;
		//		startPositionY = 600;
		//		startSpeedX = 1;
		//		startSpeedY = 1;
		//
		//		wormList.add(new StupidWorm(core, GameView.wormPaints[3],startPositionX, startPositionY, startSpeedX, startSpeedY));
		//		
		//		startPositionX = 200;
		//		startPositionY = 200;
		//		startSpeedX = 1;
		//		startSpeedY = 1;
		//
		//		wormList.add(new StupidWorm(core, GameView.wormPaints[4],startPositionX, startPositionY, startSpeedX, startSpeedY));
		//		
		//		startPositionX = 600;
		//		startPositionY = 200;
		//		startSpeedX = 1;
		//		startSpeedY = 1;
		//
		//		wormList.add(new StupidWorm(core, GameView.wormPaints[5],startPositionX, startPositionY, startSpeedX, startSpeedY));
		//		
		//		startPositionX = 200;
		//		startPositionY = 600;
		//		startSpeedX = 1;
		//		startSpeedY = 1;
		//
		//		wormList.add(new StupidWorm(core, GameView.wormPaints[6],startPositionX, startPositionY, startSpeedX, startSpeedY));
		//		
		//		startPositionX = 600;
		//		startPositionY = 600;
		//		startSpeedX = 1;
		//		startSpeedY = 1;
		//
		//		wormList.add(new StupidWorm(core, GameView.wormPaints[7],startPositionX, startPositionY, startSpeedX, startSpeedY));

		return new Board(core, title, wormList, obstacleList, appleList, xSize, ySize, appleEatGoal, appleVisibleAtOnce);
	}

	private static Board level7(Core core) {
		String title = "Front screen";

		ArrayList<Worm> wormList = new ArrayList<Worm>();
		ArrayList<Obstacle> obstacleList = new ArrayList<Obstacle>();
		ArrayList<Apple> appleList = new ArrayList<Apple>();
		int xSize = 800;
		int ySize = 800;
		int appleEatGoal = 8;
		int appleVisibleAtOnce = 3;

		//obstacles:
		obstacleList.add(new ObstacleRectangle(0, 0, 10, 800));
		obstacleList.add(new ObstacleRectangle(0, 0, 800, 10));
		obstacleList.add(new ObstacleRectangle(0, 790, 800, 800));
		obstacleList.add(new ObstacleRectangle(790, 0, 800, 800));

		obstacleList.add(new ObstacleRectangle(100, 100, 700, 250));
		obstacleList.add(new ObstacleRectangle(100, 350, 400, 500));
		obstacleList.add(new ObstacleRectangle(500, 350, 700, 500));
		obstacleList.add(new ObstacleRectangle(100, 600, 700, 700));

		//apples:
		appleList.add(new Apple(false, 75, 75));
		appleList.add(new Apple(false, 725, 75));
		appleList.add(new Apple(false, 75, 725));
		appleList.add(new Apple(false, 725, 725));
		appleList.add(new Apple(false, 450, 425));

		//worms:
		float startPositionX = 275;
		float startPositionY = 300;
		float startSpeedX = 1;
		float startSpeedY = 0;
		wormList.add(new HumanWorm(core, GameView.wormPaints[0], startPositionX, startPositionY, startSpeedX,
				startSpeedY));

		startPositionX = 550;
		startPositionY = 550;
		startSpeedX = 1;
		startSpeedY = 0;
		wormList.add(new StupidWorm(core, GameView.wormPaints[1], startPositionX, startPositionY, startSpeedX,
				startSpeedY));

		return new Board(core, title, wormList, obstacleList, appleList, xSize, ySize, appleEatGoal, appleVisibleAtOnce);
	}

}
