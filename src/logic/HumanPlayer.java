package logic;

import org.json.JSONException;
import org.json.JSONObject;

import logic.movements.Movement;

public class HumanPlayer extends Player {

	private static final long serialVersionUID = 1L;

	public HumanPlayer(Game game, String name) {
		super(game, name);
	}
	
	public HumanPlayer(Player.Memento memento) {
		super(memento);
	}
	
	// The choice is left to the user.
	@Override
	public Movement decideMovement() {
		return null;
	}
	
	@Override
	public boolean dependsOnUser() {
		return true;
	}
	
	@Override
	public Player.Memento createMemento() throws JSONException {
		return new Memento(this);
	}
	
	public static class Memento extends Player.Memento{
		public Memento(HumanPlayer player) throws JSONException {
			super(player);
			state.put("human", true);
		}
		
		public Memento(JSONObject obj, Game game) throws JSONException {
			super(obj, game);
		}

		@Override
		public boolean isHuman() {
			return true;
		}
	}

}
