
package logic;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class which describes the structure of dominoes
 */

public class Piece implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int LOWER_BOUND = 0;
	public static final int UPPER_BOUND = 6;
	
    private int left;
    private int right;

    public Piece(int left, int right){
        this.right = right;
        this.left = left;
    }
    
    public Piece(Memento memento) {
    	this(memento.left, memento.right);
    }
    
    /**
     * Checks if both values of the domino are equal, resulting a piece of the form (6|6) or (0|0)
     * @return True if it is double, False in other cases
     */
    public boolean isDouble(){
        return left == right;
    }

    public int getLeft(){
        return left;
    }

    public int getRight(){
        return right;
    }

    /**
     * It exchanges the left and right values of the piece.
     */
    public void flip() {
        int a = right;
        right = left;
        left = a;
    }
    
    /**
     * Checks if the piece has at least one side with a specific value.
     * @param val The value.
     * @return true if the left or right sides of the piece equal a given value, false otherwise.
     */
    public boolean hasValue(int val) {
    	return left == val || right == val;
    }

    @Override
    public String toString() {
        return "(" + left + "|" + right + ")";
    }
    
    public int score() {
    	return left + right;
    }
    
    public Memento createMemento() throws JSONException {
    	return new Memento(this);
    }

	static public class Memento{
		protected int left;
		protected int right;
	    protected JSONObject state;
	    
	    public Memento(Piece p) throws JSONException {
	    	right = p.right;
	    	left = p.left;
	    	state = new JSONObject();
	    	state.put("right", p.right);
	    	state.put("left", p.left);
	    }
	    
	    public Memento(JSONObject obj) throws JSONException {
	    	 state = obj;
	    	 left = obj.getInt("left");
	    	 right = obj.getInt("right");
	    }
	}
	
}