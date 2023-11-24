package logic.gamemodes;

import java.io.Serializable;
import java.util.function.Function;

import org.json.JSONException;

import logic.AIStrategy;
import logic.Game;
import logic.Player;
import logic.movements.Movement;

public interface GameMode extends Serializable {
	public String[] parameters();
	public String globalScoreName();
	public void initialize(String[] params) throws IllegalArgumentException;
	public void updateScore(Movement m);
	public Function<Player, Integer> initialScoreFunction();
	public Player checkRoundWinner(Game game);
	public boolean checkGameEnd(Game game);
	public Player currentWinner(Game game);
	public void updateGlobalScore(Game game, Player roundWinner);
	public AIStrategy[] viableStrategies();
	MementoGameMode createMemento() throws JSONException;
}

