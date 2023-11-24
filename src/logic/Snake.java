package logic;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;

public class Snake implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Side {
		LEFT, RIGHT;
		
		private static final Map<Character, Side> CHARACTER_MAP;
		
		static {
			Map<Character, Side> initializerMap = new HashMap<>();
			initializerMap.put('l', LEFT);
			initializerMap.put('L', LEFT);
			initializerMap.put('r', RIGHT);
			initializerMap.put('R', RIGHT);
			CHARACTER_MAP = initializerMap;
		}
		
		public static Side fromCharacter(Character c) {
			return CHARACTER_MAP.get(c);
		}
		
		public static List<Character> allowedCharacters() {
			return CHARACTER_MAP.keySet().stream().toList();
		}
		
	}
	
	private transient Game game;
	private LinkedList<Piece> snake;
	
	private int sidesMatched(Piece p) {
		if(snake.isEmpty()) {
			return 0;
		}
		int result = 0;
		for(Side side : Side.values()) {
			if(p.hasValue(end(side))) {
				++result;
			}
		}
		return result;
	}
	
	private boolean matchesASide(Piece p) {
		return sidesMatched(p) >= 1;
	}
	
	private void insert(Piece p, Side side) {
		if(snake.size() == 0) {
			snake.addFirst(p);
		}
		else if(p.hasValue(end(Side.LEFT)) && !(matchesMultipleSides(p) && side == Side.RIGHT)) {
			if(p.getRight() != end(Side.LEFT)) {
				p.flip();
			}
			snake.addFirst(p);
		}
		else if(p.hasValue(end(Side.RIGHT))) {
			if(p.getLeft() != end(Side.RIGHT)) {
				p.flip();
			}
			snake.addLast(p);
		}
	}
	
	public Snake(Game game) {
		this.game = game;
		this.snake = new LinkedList<>();
	}
	
	public Snake(Memento memento) {
		game = memento.mementoGame;
		this.snake = new LinkedList<>();
		
		for(Piece.Memento p: memento.mementoSnake)
			snake.add(new Piece(p));
	}
	
	/**
	 * Finds the value of a certain end of the board.
	 * @param side The side to check.
	 * @return The value, or null if the board is empty.
	 */
	public Integer end(Side side) {
		if(snake.isEmpty()) {
			return null;
		}
		switch(side) {
			case LEFT: {
				return snake.getFirst().getLeft();
			}
			case RIGHT: {
				return snake.getLast().getRight();
			}
			default: {
				return null;
			}
		}
	}
	
	/**
	 * Checks if a certain piece could be appended to multiple sides of the snake.
	 * @param p The piece to check.
	 * @return true if the piece could be added to multiple sides, false otherwise.
	 */
	public boolean matchesMultipleSides(Piece p) {
		return sidesMatched(p) >= 2;
	}
	
	/**
	 * Adds a valid piece to one of the ends of the snake. If the given piece is invalid, it does nothing.
	 * @param p The piece to add.
	 * @param side The side of the board. It is only used if necessary, to disambiguate.
	 */
	
	public void add(Piece p, Side side) {
		insert(p, side);
		for(GameObserver o : game.observers()) {
			o.onPieceAdded(game.status(), this);
		}
	}
	
	/**
	 * Finds the number of elements in a hand that could be placed after a certain piece is played
	 * @param piece The piece to be played.
	 * @param side The side of the snake where the piece will be placed.
	 * @param hand The hand.
	 * @return A number representing the result.
	 */
	public Integer piecesAddableAfter(Piece piece, Side side, Hand hand) {
		insert(piece, side);
		int result = playablePieces(hand).size();
		snake.remove(piece);
		return result;
	}
	
	/**
	 * Checks if a given piece could be added to the board.
	 * @param piece The piece.
	 * @param hand The player's hand.
	 * @return The result of the check.
	 */
	public boolean couldAdd(Piece piece, Hand hand) {
		if(snake.isEmpty()) {
			Integer biggestDoublePieceIndex = hand.biggestDoublePiece();
			return biggestDoublePieceIndex == null || piece == hand.pieces().get(biggestDoublePieceIndex);
		}
		return matchesASide(piece);
	}
	
	/**
	 * Constructs a list with the pieces of a hand that can be placed in the current turn.
	 * If the board is empty it looks for the biggest double piece.
	 * If there are no doubles, then any piece can be placed.
	 * @return The indexes of the pieces.
	 */
	public List<Integer> playablePieces(Hand hand) {
		Predicate<Piece> filteringCriteria = (Piece p) -> matchesASide(p);
		if(snake.isEmpty()) {
			Integer index = hand.biggestDoublePiece();
			if(index != null) {
				return Collections.singletonList(index);
			}
		}
		return hand.filter(filteringCriteria);
	}
	
	/**
	 * Removes all pieces in the board.
	 */
	public void clear() {
		snake.clear();
	}
	
	public List<Piece> pieces() {
		return Collections.unmodifiableList(snake);
	}
	

 	@Override
 	public String toString() {
 		return snake.stream()
 					.map((Piece p) -> p.toString())
 					.collect(Collectors.joining(" "));
 	}
	
	public Memento createMemento() throws JSONException {
		return new Memento(this);
	}
	
	static public class Memento{
		protected LinkedList<Piece.Memento> mementoSnake;
		protected JSONArray state;
		protected Game mementoGame;
		
		public Memento(Snake snake) throws JSONException {
			mementoSnake = new LinkedList<>();
			for(Piece p: snake.snake)
				mementoSnake.add(p.createMemento());
			
			state = new JSONArray();
			for(Piece.Memento p: mementoSnake) 
				state.put(p.state);
		}
		
		public Memento(JSONArray objArray, Game game) throws JSONException {
			state = objArray;
			mementoSnake = new LinkedList<>();
			for(int i = 0; i < objArray.length();i++) 
				mementoSnake.add(new Piece.Memento(objArray.getJSONObject(i)));
			
			mementoGame = game;
		}
	}
	
}
