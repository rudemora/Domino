package logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import logic.gamemodes.GameMode;
import logic.gamemodes.MementoGameMode;

public class GameStatus implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private transient Game game;
	private GameMode gameMode;
	private Player currentPlayer;
	private List<Player> stalledPlayers;
	private int round;
	private int turn;
	
	public GameStatus(Game game, GameMode gameMode) {
		this.game = game;
		this.gameMode = gameMode;
		this.round = 0;
		this.turn = 0;
		this.currentPlayer = null;
		this.stalledPlayers = new ArrayList<>();		
	}
	
	public GameStatus(Memento memento)throws JSONException {
		this.game = memento.mementoGame;
		this.gameMode = game.initialise(memento.gameMode);                 
		this.round = memento.round;
		this.turn = memento.turn;
		this.stalledPlayers = new ArrayList<>();
		this.currentPlayer = memento.currentPlayer.isHuman() ? new HumanPlayer(memento.currentPlayer): 
			new AIPlayer(memento.currentPlayer);
		
		for(Player.Memento p: memento.stalledPlayers) {
			Player player = p.isHuman() ? new HumanPlayer(p): new AIPlayer(p);
			stalledPlayers.add(player);
		}
	}
	
	/**
	 * Determines whether the game must go on or someone has already won (called after the end of a round).
	 * @return The result of the check.
	 */
	public boolean checkEnd() {
		return gameMode.checkGameEnd(game);
	}
	
	/**
	 * Advances the game status to the next turn.
	 */
	public void nextTurn() {
		turn++;
		if(currentPlayer == game.players().get(game.players().size() - 1)) {	// The current player is the last one
			currentPlayer = game.players().get(0);
		}
		else {
			int i = 0;
			boolean changed = false;
			while(!changed && i < game.players().size() - 1) {
				if(currentPlayer == game.players().get(i)) {
					currentPlayer = game.players().get(i+1);
					changed = true;
				}
				else {
					i++;
				}
			}
		}
	}
	
	/**
	 * Advances the game status to the next round.
	 * @param initialPlayer The player that starts the following round.
	 */
	public void nextRound(Player initialPlayer) {
		round++;
		turn = 1;
		currentPlayer = initialPlayer;
	}
	
	/**
	 * Updates the game status considering that a given player has become stalled.
	 * @param p The player.
	 */
	public void stall(Player p) {
		if(!stalledPlayers.contains(p)) {
			stalledPlayers.add(p);
		}
	}
	
	/**
	 * Clears the stalled players list.
	 */
	public void clearStalled() {
		stalledPlayers.clear();
	}
	
	/**
	 * Finds the player that's currently in the lead.
	 * @return The current winner.
	 */
	public Player currentWinner() {
		return gameMode.currentWinner(game);
	}
	
	public int round() {
		return round;
	}
	
	public Player currentPlayer() {
		return currentPlayer;
	}
	
	public List<Player> stalledPlayers() {
		return Collections.unmodifiableList(stalledPlayers);
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Round " + round + ", Turn " + turn + System.lineSeparator());
		s.append("Player: " + currentPlayer.name() + System.lineSeparator());
		s.append("Remaining deck pieces: " + game.deck().size() + System.lineSeparator());
		s.append(System.lineSeparator());
		s.append(game.board() + System.lineSeparator());
		s.append(System.lineSeparator());
		if(currentPlayer.dependsOnUser()) {
			s.append(currentPlayer.hand());
		}
		return s.toString();
	}
	
	public Memento createMemento() throws JSONException {
		return new Memento(this);
	}
	
	public static class Memento{
		protected MementoGameMode gameMode;
		protected Player.Memento currentPlayer;
		protected List<Player.Memento> stalledPlayers;
		protected int round;
		protected int turn;
		protected JSONObject state;
		protected Game mementoGame;
		
		public Memento(GameStatus status) throws JSONException {
			gameMode = status.gameMode.createMemento();
			currentPlayer = status.currentPlayer.createMemento();
			
			stalledPlayers = new ArrayList<>();
			for(Player p: status.stalledPlayers)
				stalledPlayers.add(p.createMemento());
			
			JSONArray stalled = new JSONArray();
			for(Player.Memento p: stalledPlayers)
				stalled.put(p.state);
		
			round = status.round;
			turn = status.turn;
			
			state = new JSONObject();
			state.put("game mode", gameMode.getState());
			state.put("current player", currentPlayer.state);
			state.put("stalled players",stalled);
			state.put("round", round);
			state.put("turn", turn);
		}
		
		public Memento(JSONObject obj, Game game) throws JSONException {
			state = obj;
			turn = state.getInt("turn");
			round = state.getInt("round");
			gameMode = game.initialise(obj.getJSONObject("game mode"));
			currentPlayer = obj.getJSONObject("current player").getBoolean("human")?		
			new HumanPlayer.Memento(state.getJSONObject("current player"), game):
			new AIPlayer.Memento(state.getJSONObject("current player"), game);
			
			stalledPlayers = new ArrayList<>();
			JSONArray stalled = obj.getJSONArray("stalled players");
			for(int i = 0; i < stalled.length();i++) {
				Player.Memento player = stalled.getJSONObject(i).getBoolean("human")?
				new HumanPlayer.Memento(stalled.getJSONObject(i), game):
				new AIPlayer.Memento(stalled.getJSONObject(i), game);
				stalledPlayers.add(player);
			}
			
			mementoGame = game;
		}
		
	}

	public GameMode gameMode(){
		return gameMode;
	}

}
