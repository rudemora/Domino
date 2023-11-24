package logic.exceptions;

public class UnallowedMovementException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public UnallowedMovementException() {
		super("The chosen movement cannot take place!");
	}
	
	public UnallowedMovementException(String msg) {
		super(msg);
	}
	
}
