package logic;

import java.util.List;

import logic.movements.Movement;
import logic.movements.PassMovement;
import logic.movements.PlaceMovement;

public class HighestScorePlacementStrategy implements AIStrategy {

	@Override
	public Movement decideMovement(Game game, Hand hand) {
		List<Integer> playablePieces = game.board().playablePieces(hand);
		if(playablePieces.isEmpty()) {
			return new PassMovement();
		}
		
		Integer result = -1;
		Integer max = Integer.MIN_VALUE;
		for(Integer index : playablePieces) {
			if(hand.pieces().get(index).score() > max) {
				max = hand.pieces().get(index).score();
				result = index;
			}
		}
		
		Snake.Side side = Snake.Side.LEFT;
		Integer boardMax = Integer.MIN_VALUE;
		for(Snake.Side s : Snake.Side.values()) {
			Integer sideNumber = game.board().end(s);
			if(sideNumber != null && sideNumber > boardMax) {
				boardMax = sideNumber;
				side = s;
			}
		}
		
		return new PlaceMovement(result, side);
	}

}
