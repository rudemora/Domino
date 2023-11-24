package logic.movements;

import logic.Game;
import logic.Player;
import logic.Piece;
import logic.exceptions.UnallowedMovementException;
import logic.gamemodes.ClassicGameMode;

public class PassMovement extends Movement {

	private static final long serialVersionUID = 1L;
	
	private static final String NAME = "pass";
	
	private Piece drawn;
	private Player player;
	
	public PassMovement() {
		super(NAME);
		this.drawn = null;
		this.player = null;
	}
	
	@Override
	public void execute(Game game, Player player) throws UnallowedMovementException {
		
		this.player = player;
		
		if(game.board().playablePieces(player.hand()).isEmpty()) {
			if(game.deck().size() == 0) {
				drawn = null;
				game.status().stall(player);
			}
			else {
				drawn = game.deck().deal(player.hand());
			}
			game.status().gameMode().updateScore(this);
		}
		else {
			throw new UnallowedMovementException("You can't pass if there are pieces you could place!");
		}
	}
	
	@Override
	public void updateScore(ClassicGameMode mode) {
		if(player != null && drawn != null) {
			player.addRoundScore(mode.pieceScore(drawn));
		}
	}
	
}
