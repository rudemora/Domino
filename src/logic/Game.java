package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import logic.exceptions.UnallowedMovementException;
import logic.gamemodes.ClassicGameMode;
import logic.gamemodes.GameMode;
import logic.gamemodes.MementoGameMode;
import logic.gamemodes.ScoreGameMode;
import logic.movements.Movement;

public class Game implements Observable<GameObserver> {
	
	public static final GameMode[] MODES = {
		new ClassicGameMode(),
		new ScoreGameMode()
	};
	
	private Deck deck;
	private List<Player> players;
	private List<GameObserver> observers;
	private GameStatus status;
	private Snake snake;
	
	/**
	 * Recovers the initial player based on the biggest double piece.
	 * If none of the players has a double piece, the first one starts the game.
	 * @return Index of the player
	 */
	private Player initialPlayer() {
		int index  = 0;
		int higher = -1;
		for(int i = 0; i < players.size(); i++){
			Hand h = players.get(i).hand();
			Integer biggestIndex = h.biggestDoublePiece();
			if(biggestIndex != null && h.pieces().get(biggestIndex).getLeft() > higher) {
				higher = h.pieces().get(biggestIndex).getLeft();
				index = i;
			}
		}
		return index < players.size() ? players.get(index) : null;
	}
	
	private void generateAIMovement() {
		Thread t = new Thread(() -> {
			Movement AIMovement = status.currentPlayer().decideMovement();
			if(AIMovement != null) {
				execute(AIMovement);
			}
		});
		t.start();
	}
	
	private void startNextTurn(boolean updateStatus) {
		if(updateStatus) {
			status.nextTurn();
		}
		if(!status.currentPlayer().dependsOnUser()) {
			generateAIMovement();
		}
		for(GameObserver o : observers) {
			o.onTurnChange(status, snake);
		}
	}
	
	private void startRound() {
		for(GameObserver o : observers) {
			o.onRoundStart(status, snake);
		}
		startNextTurn(false);
	}
	
	/**
	 * Resets all players, intial_score and board and snake, effectively starting a new round.
	 */
	private void nextRound() {
		snake.clear();
		deck.initialize();
		for(Player p : players) {
			p.reset(status.gameMode(), deck);
		}
		status.nextRound(initialPlayer());
		startRound();
	}
	
	/**
	 * Finishes the round, considering a certain player as the winner.
	 * @param winner The player that won the round.
	 */
	private void finishRound(Player winner) {
		status.gameMode().updateGlobalScore(this, winner);
		for(GameObserver o : observers) {
			o.onRoundEnd(status, snake, players(), winner);
		}
		if(status.checkEnd()) {
			for(GameObserver o : observers) {
				o.onGameEnd(status.currentWinner());
			}
		}
		else {
			nextRound();
		}
	}
	
	/**
	 * Updates the status of the game, finishing the round or advancing to the next turn.
	 */
	private void update() {
		Player winner = status.gameMode().checkRoundWinner(this);
		if(winner != null) {
			finishRound(winner);
		}
		else if(status.checkEnd()) {
			for(GameObserver o : observers) {
				o.onGameEnd(status.currentWinner());
			}
		}
		else {
			startNextTurn(true);
		}
	}
		
	/**
	 * Creates a new game with the given players.
	 * @param players The names of the human players.
	 * @param aiPlayerCount The amount of players controlled by the AI.
	 * @param maxRoundCount The maximum amount of rounds to play (best of)
	 */
	
	public Game() {
		this.deck = null;
		this.snake = null;
		this.players = null;
		this.observers = new LinkedList<>();
		this.status = null;
	}
	
	public JSONObject save() throws JSONException {
		JSONObject json = new JSONObject();
		
		json.put("deck", deck.createMemento().state);
		json.put("status", status.createMemento().state);
		json.put("snake", snake.createMemento().state);
		JSONArray playerArray = new JSONArray();
		for(Player p: players)
			playerArray.put(p.createMemento().state);
		json.put("players", playerArray);
		
		return json;
	}
	
	public void reset(JSONObject obj) throws JSONException {
		this.deck = new Deck(new Deck.Memento(obj.getJSONObject("deck")));
		this.snake = new Snake(new Snake.Memento(obj.getJSONArray("snake"),this));
		this.players = new ArrayList<>();
		this.status = new GameStatus(new GameStatus.Memento(obj.getJSONObject("status"), this));
		AIPlayer.resetAICount();
		JSONArray playerArray = obj.getJSONArray("players");
		for(int i = 0; i < playerArray.length();i++) {
			Player player = playerArray.getJSONObject(i).getBoolean("human") ? new HumanPlayer(new HumanPlayer.Memento(playerArray.getJSONObject(i), this)):
				new AIPlayer(new AIPlayer.Memento(playerArray.getJSONObject(i), this));
			players.add(player);
		}
		startRound();
	}
	
	public void reset(List<String> playerNames, int AIPlayerCount, GameMode gameMode) {
		this.deck = new Deck();
		this.snake = new Snake(this);
		this.players = new ArrayList<>();
		this.status = new GameStatus(this, gameMode);
		for(String name : playerNames) {
			players.add(new HumanPlayer(this, name));
		}
		AIPlayer.resetAICount();
		for(int i = 0; i < AIPlayerCount; i++) {
			players.add(new AIPlayer(this));
		}
		nextRound();
	}
	
	public void execute(Movement movement) {
		try {
			movement.execute(this, status.currentPlayer());
			update();
		}
		catch(UnallowedMovementException ume) {
			for(GameObserver o : observers) {
				o.onSpecificError(status.currentPlayer(), ume);
			}
		}
	}
	
	/**
	 * Returns the amount of pieces given to each player at the beginning.
	 */
	public int piecesPerPlayer() {
		return players.size() == 2 ? 7 : 5;
	}
	
	public GameStatus status() {
		return status;
	}

	public List<Player> players() {
		return Collections.unmodifiableList(players);
	}
	
	public Snake board() {
		return snake;
	}
	
	public Deck deck() {
		return deck;
	}

	public List<GameObserver> observers() {
		return Collections.unmodifiableList(observers);
	}
	
	@Override
	public void addObserver(GameObserver o) {
		observers.add(o);		
	}

	@Override
	public void removeObserver(GameObserver o) {
		observers.remove(o);
	}

	public MementoGameMode initialise(JSONObject json) throws JSONException {   //TODO Como dijo de hacer la lista en game 
		return new ClassicGameMode.Memento(json);                               //aquí para que inicie parecido a factoria
	}                                                                           //FIXME ahora mismo esta mal implementado,
	    																		//debe hacer uso de intialise de cada modo de juego
	public GameMode initialise(MementoGameMode mode) throws JSONException {     //Si se ve conveniente se puede mover a GameStatus
		return new ClassicGameMode(mode);                              
	}   
	
}
