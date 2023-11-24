package logic.movements;

import logic.Game;
import logic.Player;

public class BetMovement extends Movement {

	private static final long serialVersionUID = 1L;
	
	private static final String NAME = "bet";
	
	public BetMovement() {
		super(NAME);
	}
	
	@Override
	public void execute(Game game, Player player) {
		// Not implemented
	}

}
