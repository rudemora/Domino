package logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;

public class Hand implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<Piece> pieces;
	
	public Hand() {
		this.pieces = new ArrayList<>();
	}
	
	public Hand(Memento memento) {
		this.pieces = new ArrayList<>();
		for(Piece.Memento p: memento.getHand())
			pieces.add(new Piece(p));			
	}
	
	/**
	 * Gets the biggest double piece of a hand. If there is none, returns null.
	 * @return The index of the piece.
	 */
	public Integer biggestDoublePiece() {
		Integer result = null;
		int higher = -1;
		for(int i = 0; i < pieces.size(); i++) {
			Piece p = pieces.get(i);
			if(p.isDouble() && p.getLeft() > higher) {
				higher = p.getLeft();
				result = i;
			}
		}
		return result;
	}
	
	/**
	 * Receives a dealt piece and adds it to the piece list.
	 * @param p The piece.
	 */
	public void receive(Piece p) {
		pieces.add(p);
	}
	
	/**
	 * Tries to place a certain piece on the board.
	 * @param piece The piece to play.
	 * @param side The preferred side of the board.
	 * @param snake The board.
	 * @return True if the piece could be added, false otherwise.
	 */
	public boolean place(Piece piece, Snake.Side side, Snake snake) {
		if(snake.couldAdd(piece, this)) {
			pieces.remove(piece);
			snake.add(piece, side);
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the hand is empty.
	 * @return The result of the check.
	 */
	public boolean empty() {
		return pieces.size() == 0;
	}
	
	/**
	 * Clears the hand, removing all pieces.
	 */
	public void clear() {
		pieces.clear();
	}
	
	/**
	 * Filters the pieces in the hand, according to a predicate.
	 * @param predicate The predicate.
	 * @return The indexes of the pieces that make the predicate true.
	 */
	public List<Integer> filter(Predicate<Piece> predicate) {
		List<Integer> result = new ArrayList<>();
		for(int i = 0; i < pieces.size(); i++) {
			if(predicate.test(pieces.get(i))) {
				result.add(i);
			}
		}
		return result;
	}
	
	public List<Piece> pieces() {
		return Collections.unmodifiableList(pieces);
	}
	
	@Override
	public String toString() {
		return pieces.stream()
				 .map((Piece p) -> p.toString())
				 .collect(Collectors.joining(" "));
	}
	
	public Memento createMemento() throws JSONException {
		return new Memento(this);
	}
	
	public static class Memento{
		private List<Piece.Memento> mementoHand;
		private JSONArray state;
		
		public Memento(Hand hand) throws JSONException {
			mementoHand = new ArrayList<>();
			for(Piece p: hand.pieces) 
				mementoHand.add(p.createMemento());
			
			state = new JSONArray();
			for(Piece.Memento p: mementoHand)
				state.put(p.state);
		}
		
		public Memento() throws JSONException {
			this(new Hand());
		}
		
		public Memento(JSONArray objArray) throws JSONException {
			state = objArray;
			
			mementoHand = new ArrayList<>();
			for(int i = 0; i < objArray.length();i++) 
				mementoHand.add(new Piece.Memento(objArray.getJSONObject(i)));
		}
		
		JSONArray getState() {
			return state;
		}
		
		List<Piece.Memento> getHand(){
			return Collections.unmodifiableList(mementoHand);
		}
	}
}
