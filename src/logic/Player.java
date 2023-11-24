package logic;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

import logic.gamemodes.GameMode;
import logic.movements.Movement;

public abstract class Player implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private int roundScore;
	private int globalScore;
	protected transient Game game;
	protected Hand hand;
	
	public Player(Game game, String name) {
		this.game = game;
		this.name = name;
		this.hand = new Hand();
		this.globalScore = 0;
		this.roundScore = 0;
 		
	}
	
	public Player(Memento memento) {
		this.name = memento.name;
		this.roundScore = memento.scoreRound;
		this.globalScore = memento.scoreGlobal;
		this.hand = new Hand(memento.hand);
		this.game = memento.mementoGame;
	}
	
	/**
	 * If the player doesn't depend on user interaction, chooses a movement for that turn.
	 * Otherwise, it does nothing.
	 * @return The generated movement.
	 */
	public abstract Movement decideMovement();
	
	/**
	 * Resets a player's state after ending a round, clearing the corresponding hand and updating their total score.
	 */
	public void reset(GameMode mode, Deck deck) {
		hand.clear();
		deck.deal(hand, game.piecesPerPlayer());
		roundScore = mode.initialScoreFunction().apply(this);
	}
	
	public String name() {
		return name;
	}
	
	public Hand hand() {
		return hand;
	}

	public int roundScore() {
		return roundScore;
	}

	public int globalScore() {
		return globalScore;
	}

	public void addRoundScore(int extra) {
		roundScore += extra;
	}

	public void addGlobalScore(int extra) {
		globalScore += extra;
	}

	abstract Memento createMemento() throws JSONException;
	
	public abstract boolean dependsOnUser();
	
	public static abstract class Memento {
		protected String name;
		protected int scoreRound;
		protected int scoreGlobal;
		protected Hand.Memento hand;
		protected JSONObject state;
		protected Game mementoGame;
		
		public Memento(Player player) throws JSONException {
			name = player.name;
			scoreRound = player.roundScore;
			scoreGlobal = player.globalScore;
			hand = player.hand.createMemento();
			state = new JSONObject();
			state.put("name", name);
			state.put("score round", scoreRound);
			state.put("score global", scoreGlobal);
			state.put("hand", hand.getState());
		}
		
		public Memento(JSONObject obj, Game game) throws JSONException {
			state = obj;
			name = obj.getString("name");
			scoreRound = obj.getInt("score round");
			scoreGlobal = obj.getInt("score global");
			hand = new Hand.Memento();
			hand = new Hand.Memento(obj.getJSONArray("hand"));
			mementoGame = game;
		}
		
		public abstract boolean isHuman();
	}	

}
