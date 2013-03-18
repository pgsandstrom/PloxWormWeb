package se.persandstrom.ploxworm.core.worm.board;

import se.persandstrom.ploxworm.core.Line;


public interface Obstacle {

	public boolean isCollide(Line line);
}
