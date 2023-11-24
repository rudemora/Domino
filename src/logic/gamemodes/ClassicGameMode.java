package logic.gamemodes;

import java.util.function.Function;

import org.json.JSONException;
import org.json.JSONObject;

import logic.AIStrategy;
import logic.BothersomePlacementStrategy;
import logic.Game;
import logic.Piece;
import logic.Player;
import logic.RandomPlacementStrategy;
import logic.movements.Movement;
import logic.Hand;

public class ClassicGameMode implements GameMode {

	private static final long serialVersionUID = 1L;

	private static final String[] PARAMETERS = {
		"Max. number of rounds"
	};
	
	private static final AIStrategy[] VIABLE_STRATEGIES = {
		new RandomPlacementStrategy(),
		new BothersomePlacementStrategy()
	};
	
	private Integer nRounds;

	private int handScore(Hand h) {
		return h.pieces().stream()
						 .map((Piece p) -> pieceScore(p))
						 .reduce(0, (Integer a, Integer b) -> a + b);
	}
	
	public ClassicGameMode() {
		this.nRounds = null;
	}
	
	public ClassicGameMode(int maxRounds) {
		this.nRounds = maxRounds;
	}
	
	public ClassicGameMode(MementoGameMode memento) throws JSONException {
		this.nRounds = memento.getState().getInt("number rounds");
	}
	
	public int pieceScore(Piece p) {
		return p == null ? 0 : p.getLeft() + p.getRight();
	}
	
	@Override
	public String[] parameters() {
		return PARAMETERS;
	}

	@Override
	public void initialize(String[] params) throws IllegalArgumentException {
		try {
			Integer rounds = Integer.parseInt(params[0]);
			if(rounds <= 0) {
				throw new IllegalArgumentException("The maximum number of rounds must be positive!");
			}
			else if(rounds % 2 == 0) {
				throw new IllegalArgumentException("The maximum number of rounds must be odd!");
			}
			nRounds = rounds;
		}
		catch(NumberFormatException nfe) {
			throw new IllegalArgumentException("The maximum number of rounds must be an integer!");
		}		
	}

	@Override
	public boolean checkGameEnd(Game game){
		return game.players().stream().anyMatch((Player p) -> p.globalScore() > nRounds / 2);
	}

	@Override 
	public Player currentWinner(Game game){
		Player playerAux = null;
		for(Player p : game.players()) {
			if(playerAux == null || p.globalScore() > playerAux.globalScore()){
				playerAux = p;
			}
		}
		return playerAux;
	}

	@Override
	public void updateGlobalScore(Game game, Player roundWinner){
		roundWinner.addGlobalScore(1);
	}

	@Override
	public Player checkRoundWinner(Game game) {
		for(Player p : game.players()) {
			if(p.hand().empty()) {
				return p;
			}
		}
		if(game.status().stalledPlayers().size() == game.players().size()) {
			int max = -1;
			Player winner = null;
			for(Player p : game.players()) {
				if(p.roundScore() > max) {
					max = p.roundScore();
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
		return (Player p) -> handScore(p.hand());
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
		return json.getInt("mode") == Memento.gameModeIndex ? new ClassicGameMode(new Memento(json)):null;
	}
	
	public static class Memento implements MementoGameMode{
		protected static final int gameModeIndex = 0;           //Indice lista de modos de juego
		protected int nRounds;
		protected JSONObject state;
		
		public Memento(ClassicGameMode mode) throws JSONException {
			nRounds = mode.nRounds;
			state = new JSONObject();
			state.put("mode", gameModeIndex);
			state.put("number rounds", nRounds);
		}
		
		public Memento(JSONObject json) throws JSONException {
			state = json;
			nRounds = json.getInt("number rounds");
		}

		@Override
		public JSONObject getState() {
			return state;
		}

	}
	
	@Override
	public String toString() {
		return "Classic";
	}

	@Override
	public String globalScoreName() {
		return "Rounds won";
	}
	
}
