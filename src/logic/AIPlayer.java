package logic;

import org.json.JSONException;
import org.json.JSONObject;
import logic.movements.Movement;

public class AIPlayer extends Player {
	
	private static final long serialVersionUID = 1L;

	private static final int MOVEMENT_DELAY = 1000;
	
	private static int AICount = 0;
	
	private static String generateName() {
		return "CPU " + (AICount + 1);
	}
	
	public static void resetAICount() {
		AICount = 0;
	}
	
	private transient AIStrategy strategy;
	
	/**
	 * Constructs a new AI player, assigning a random strategy to it, out of all that are viable for that specific game mode.
	 * @param game The game.
	 */
	public AIPlayer(Game game) {
		super(game, generateName());
		AIStrategy[] viableStrategies = game.status().gameMode().viableStrategies();
		this.strategy = viableStrategies[RandomUtilities.getRandomInteger(viableStrategies.length)];
		AICount++;
	}
	
	public AIPlayer(Player.Memento memento) {
		super(memento);
		AICount++;
	}
	
	/**
	 * Generates a movement according to the state of the game and the chosen strategy.
	 */
	@Override
	public Movement decideMovement() {
		try {
			Thread.sleep(MOVEMENT_DELAY);		// Let the AI "think"
		}
		catch(InterruptedException ie) {
			// This should never happen
		}
		return strategy.decideMovement(game, hand);
	}
	
	@Override
	public boolean dependsOnUser() {
		return false;
	}
	
	@Override
	public Player.Memento createMemento() throws JSONException {
		return new Memento(this);
	}
	
	public static class Memento extends Player.Memento{
		public Memento(AIPlayer player) throws JSONException {
			super(player);
			state.put("human", false);
		}
		
		public Memento(JSONObject obj, Game game) throws JSONException {
			super(obj, game);
		}

		@Override
		public boolean isHuman() {
			return false;
		}

	}
	
}
