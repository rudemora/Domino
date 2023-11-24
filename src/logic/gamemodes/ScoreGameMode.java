package logic.gamemodes;

import java.util.function.Function;

import org.json.JSONException;
import org.json.JSONObject;

import logic.AIStrategy;
import logic.Game;
import logic.Piece;
import logic.Player;
import logic.RandomPlacementStrategy;
import logic.movements.Movement;
import logic.HighestScorePlacementStrategy;

public class ScoreGameMode implements GameMode {

	private static final long serialVersionUID = 1L;

	private static final int MINIMUM_WINNING_SCORE = 50;
	
	private static final String[] PARAMETERS = {
		"Winning score"
	};
	
	private static final AIStrategy[] VIABLE_STRATEGIES = {
		new RandomPlacementStrategy(),
		new HighestScorePlacementStrategy()
	};
	
	private Integer scoreEnd;
	
	public ScoreGameMode() {
		this.scoreEnd = null;
	}
	
	public ScoreGameMode(MementoGameMode memento) throws JSONException {
		this.scoreEnd = memento.getState().getInt("score end");
	}
	
	public int pieceScore(Piece p) {
		return p != null ? p.getLeft() + p.getRight() : 0;
	}
	
	@Override
	public String[] parameters() {
		return PARAMETERS;
	}

	@Override
	public void initialize(String[] params) throws IllegalArgumentException {
		try {
			Integer score = Integer.parseInt(params[0]);
			if(score < MINIMUM_WINNING_SCORE) {
				throw new IllegalArgumentException("The winning score must be " + MINIMUM_WINNING_SCORE + " or more!");
			}
			scoreEnd = score;
		}
		catch(NumberFormatException nfe) {
			throw new IllegalArgumentException("The winning score must be an integer!");
		}		
	}

	@Override
	public boolean checkGameEnd(Game game) {
		return game.players().stream().anyMatch((Player p) -> p.globalScore() > scoreEnd);
	}

	@Override 
	public Player currentWinner(Game game) {
		Player playerAux = null;
		for(Player p : game.players()) {
			if(playerAux == null || p.globalScore() > playerAux.globalScore()) {
				playerAux = p;
			}
		}
		return playerAux;
	}

	@Override
	public void updateGlobalScore(Game game, Player roundWinner) {
		game.players().forEach((Player player) -> player.addGlobalScore(player.roundScore()));
	}

	@Override
	public Player checkRoundWinner(Game game) {
		for(Player p : game.players()) {
			if(p.hand().empty()) {
				return p;
			}
		}
		if(game.status().stalledPlayers().size() == game.players().size()) {
			int min = Integer.MAX_VALUE;
			Player winner = null;
			for(Player p : game.players()) {
				if(p.roundScore() < min) {
					min = p.roundScore();
					winner = p;
				}
			}
			return winner;
		}
		return null;
	}

	@Override
	public void updateScore(Movement m) {
		m.updateScore(this);
	}

	@Override
	public Function<Player, Integer> initialScoreFunction(){
		return (Player p) -> 0;
	}

	@Override
	public AIStrategy[] viableStrategies() {
		return VIABLE_STRATEGIES;
	}
	
	@Override
	public MementoGameMode createMemento() throws JSONException {
		return new Memento(this);
	}
	
	public static GameMode initialiseGameMode(JSONObject json) throws JSONException {
		return json.getInt("mode") == Memento.gameModeIndex ? new ScoreGameMode(new Memento(json)):null;
	}
	
	public static class Memento implements MementoGameMode{
		protected static final int gameModeIndex = 1;           //Indice lista de modos de juego
		protected int scoreEnd;
		protected JSONObject state;
		
		public Memento(ScoreGameMode mode) throws JSONException {
			scoreEnd = mode.scoreEnd;
			state = new JSONObject();
			state.put("mode", gameModeIndex);
			state.put("score end", scoreEnd);
		}
		
		public Memento(JSONObject json) throws JSONException {
			state = json;
			scoreEnd = json.getInt("score end");
		}

		@Override
		public JSONObject getState() {
			return state;
		}
	}
	
	@Override
	public String toString() {
		return "Score-based";
	}

	@Override
	public String globalScoreName() {
		return "Piece score";
	}

}
