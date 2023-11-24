package logic.movements;

import java.io.Serializable;

import logic.Game;
import logic.Player;
import logic.exceptions.MovementFormatException;
import logic.exceptions.UnallowedMovementException;
import logic.gamemodes.ClassicGameMode;
import logic.gamemodes.ScoreGameMode;

public abstract class Movement implements Serializable, ScoreUpdater {

	private static final long serialVersionUID = 1L;
	
	protected String name;
	
	protected Movement(String name) {
		this.name = name;
	}
	
	Movement parse(String input) throws MovementFormatException {
		if(input.equals(name)) {
			return this;
		}
		return null;
	}
	
	public abstract void execute(Game game, Player player) throws UnallowedMovementException;
	
	@Override
	public void updateScore(ClassicGameMode mode) {}
	
	@Override
	public void updateScore(ScoreGameMode mode) {}
	
}
