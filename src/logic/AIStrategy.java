package logic;

import logic.movements.Movement;

public interface AIStrategy {
	public Movement decideMovement(Game game, Hand hand);
}
