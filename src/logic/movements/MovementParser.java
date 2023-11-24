package logic.movements;

import logic.exceptions.MovementFormatException;
import logic.exceptions.UnrecognizedMovementException;

public class MovementParser {
	
	private static final Movement[] AVAILABLE_MOVEMENTS = {
		new PassMovement(),
		new PlaceMovement(),
		new BetMovement()
	};

	public static Movement parseMovement(String input) throws MovementFormatException, UnrecognizedMovementException {
		String trimmedInput = input.trim();
		for(Movement m : AVAILABLE_MOVEMENTS) {
			Movement result = m.parse(trimmedInput);
			if(result != null) {
				return result;
			}
		}
		throw new UnrecognizedMovementException("The given input doesn't match any command!"); 
	}
	
	// Prevent instantiation
	private MovementParser() {}
	
}
