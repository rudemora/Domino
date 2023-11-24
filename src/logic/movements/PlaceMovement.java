package logic.movements;

import logic.Game;
import logic.Piece;
import logic.Player;
import logic.Snake;
import logic.exceptions.MovementFormatException;
import logic.exceptions.UnallowedMovementException;
import logic.gamemodes.ClassicGameMode;
import logic.gamemodes.ScoreGameMode;

public class PlaceMovement extends Movement {
	
	private static final long serialVersionUID = 1L;
	
	private static final String NAME = "place";
	private static final int MAX_ARGS = 3;
	
	private Integer index;
	private Snake.Side side;
	private Piece piece;
	private Player player;
	
	public PlaceMovement(Integer index, Snake.Side side) {
		super(NAME);
		this.index = index;
		this.side = side;
		this.piece = null;
		this.player = null;
	}
	
	public PlaceMovement(Integer index, Character side) {
		this(index, Snake.Side.fromCharacter(side));
	}
	
	public PlaceMovement() {
		this(null, (Snake.Side) null);
	}

	@Override
	public Movement parse(String input) throws MovementFormatException {
		String[] arr = input.split("\\s+");
		if(arr[0].equals(name)) {
			if(arr.length == 1) {
				throw new MovementFormatException("Expected a piece to place on the board!");
			}
			else if(arr.length <= 3) {
				try {
					Integer ind = Integer.parseInt(arr[1]);
					if(arr.length == 3) {
						if(arr[2].length() != 1) {
							throw new MovementFormatException("Expected a single character as second argument!");
						}
						for(char c : Snake.Side.allowedCharacters()) {
							if(String.valueOf(c).toLowerCase().equals(arr[2])) {
								return new PlaceMovement(ind, c);
							}
						}
						throw new MovementFormatException("The given character doesn't specify a valid position!");
					}
					return new PlaceMovement(ind, 'L');
				}
				catch(NumberFormatException nfe) {
					throw new MovementFormatException("Expected a number as first argument!");
				}
			}
			else {
				throw new MovementFormatException("The maximum number of arguments for the given command is " + MAX_ARGS + "!");
			}
		}
		return null;
	}
	
	@Override
	public void execute(Game game, Player player) throws UnallowedMovementException {
		if(index < 0 || index >= player.hand().pieces().size()) {
			throw new UnallowedMovementException("The chosen piece doesn't exist!");
		} 
		piece = player.hand().pieces().get(index);
		this.player = player;
		if(!player.hand().place(piece, side, game.board())) {
			if(game.board().matchesMultipleSides(piece)) {
				throw new UnallowedMovementException("You have to specify the position to place the piece!");
			}
			throw new UnallowedMovementException("You can't place the chosen piece!");
		}
		game.status().gameMode().updateScore(this);
		game.status().clearStalled();
	}
	
	@Override
	public void updateScore(ClassicGameMode mode) {
		if(player != null && piece != null) {
			player.addRoundScore(-mode.pieceScore(piece));
		}
	}
	
	@Override
	public void updateScore(ScoreGameMode mode) {
		if(player != null && piece != null) {
			player.addGlobalScore(mode.pieceScore(piece));
		}
	}
	
}
