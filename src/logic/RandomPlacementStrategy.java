package logic;

import java.util.List;

import logic.movements.Movement;
import logic.movements.PassMovement;
import logic.movements.PlaceMovement;

public class RandomPlacementStrategy implements AIStrategy {

	@Override
	public Movement decideMovement(Game game, Hand hand) {
		List<Integer> playablePieces = game.board().playablePieces(hand);
		if(playablePieces.isEmpty()) {
			return new PassMovement();
		}
		Integer index = playablePieces.get(RandomUtilities.getRandomInteger(playablePieces.size()));
		List<Character> allowedCharacters = Snake.Side.allowedCharacters();
		Integer chosenCharacter = RandomUtilities.getRandomInteger(allowedCharacters.size());
		Character randomSide = allowedCharacters.get(chosenCharacter);
		return new PlaceMovement(index, randomSide);
	}

}
