package logic.exceptions;

public class MovementFormatException extends Exception {

	private static final long serialVersionUID = 1L;

	public MovementFormatException() {
		super("The specified movement doesn't match the expected format!");
	}
	
	public MovementFormatException(String msg) {
		super(msg);
	}
	
}
