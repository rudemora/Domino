package logic;

import java.util.List;

import logic.movements.Movement;
import logic.movements.PassMovement;
import logic.movements.PlaceMovement;

public class BothersomePlacementStrategy implements AIStrategy {

	@Override
	public Movement decideMovement(Game game, Hand hand) {
		List<Integer> playablePieces = game.board().playablePieces(hand);
		if(playablePieces.isEmpty()) {
			return new PassMovement();
		}
		
		Player nextPlayer = game.players().get(0);
		for(int i = 0; i < game.players().size() - 1; i++) {
			if(game.players().get(i) == game.status().currentPlayer()) {
				nextPlayer = game.players().get(i + 1);
			}
		}
		
		int index = -1;
		int min = Integer.MAX_VALUE;
		Snake.Side side = null;
		for(Integer i : playablePieces) {
			Piece p = hand.pieces().get(i);
			for(Snake.Side s : Snake.Side.values()) {
				if(game.board().couldAdd(p, hand)) {
					Integer l = game.board().piecesAddableAfter(p, s, nextPlayer.hand());
					if(l < min) {
						min = l;
						index = i;
						side = s;
					}
				}
			}
		}
		
		return new PlaceMovement(index, side);
	}
	
}
