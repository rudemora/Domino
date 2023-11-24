package logic.exceptions;

public class UnrecognizedMovementException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnrecognizedMovementException() {
		super("The specified movement doesn't exist!");
	}
	
	public UnrecognizedMovementException(String msg) {
		super(msg);
	}
	
}
