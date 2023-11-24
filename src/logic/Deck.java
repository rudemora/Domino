package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Deck {

    private Stack<Piece> pieces;
    private int deckCount;

    /**
     * Draws a domino piece from the deck
     * @return The drawn piece
     */
    private Piece draw() {
        return pieces.pop();
    }
    
    /**
     * Clears the deck and adds all the corresponding pieces to it.
     */
    public void initialize() {
    	pieces.clear();
    	for(int m = 0; m < deckCount; m++) {
            for(int i = Piece.LOWER_BOUND; i <= Piece.UPPER_BOUND; i++) {
                for(int j = i; j <= Piece.UPPER_BOUND; j++) {
                    pieces.add(new Piece(i,j));
                }
            }
        }
        Collections.shuffle(pieces);
    }
    
    /**
     * Class constructor for decks
     * @param numberDecks Number of decks we want to play with. By default initialized to 1
     */
    public Deck(int numberDecks) {
        this.pieces = new Stack<>();
        this.deckCount = numberDecks;
    }

    public Deck() {
       this(1);
    }
    
    public Deck(Memento memento) {
    	this.pieces = new Stack<>();
        this.deckCount = memento.deckCount;
        for(Piece.Memento p: memento.mementoDeck)
        	pieces.push(new Piece(p));
    }
    
    /**
	 * Deals pieces to a hand if the deck has enough of them. Otherwise it doesn't do anything.
	 */
	
	public void deal(Hand h, int pieceCount) {
		if(pieces.size() >= pieceCount) {
			for(int i = 0; i < pieceCount; i++) {
				h.receive(draw());
			}
		}
	}
    
    
    /**
     * Deals only one piece and return it to the asking method.
     */
	public Piece deal(Hand h) {
        Piece drawn = draw();
        h.receive(drawn);
        return drawn;
	}

    /**
     * 
     * @return Size of the deck of pieces
     */
    public int size() {
        return pieces.size();
    }
    
    public Memento createMemento() throws JSONException {
		return new Memento(this);
	}
	
	public static class Memento{
		protected List<Piece.Memento> mementoDeck;
		protected int deckCount;
		protected JSONObject state;
		
		public Memento(Deck deck) throws JSONException {
			mementoDeck = new ArrayList<>();
			for(Piece p: deck.pieces) 
				mementoDeck.add(p.createMemento());
			
			JSONArray stateArray = new JSONArray();
			for(Piece.Memento p: mementoDeck)
				stateArray.put(p.state);
			
			state = new JSONObject();
			state.put("deck", stateArray);
			state.put("count", deck.deckCount);
		}
		
		public Memento(JSONObject obj) throws JSONException {
			state = obj;
			
			deckCount = obj.getInt("count");
			JSONArray objArray = obj.getJSONArray("deck");
			mementoDeck = new ArrayList<>();
			for(int i = 0; i < objArray.length();i++) 
				mementoDeck.add(new Piece.Memento(objArray.getJSONObject(i)));
		}
		
	}

}
