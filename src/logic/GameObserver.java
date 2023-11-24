package logic;

import java.io.Serializable;
import java.util.List;

public interface GameObserver extends Serializable {
	public void onTurnChange(GameStatus status, Snake board);
	public void onPieceAdded(GameStatus status, Snake board);
	public void onRoundStart(GameStatus status, Snake board);
	public void onRoundEnd(GameStatus status, Snake board, List<Player> players, Player winner);
	public void onGameEnd(Player winner);
	public void onSpecificError(Player player, Exception e);
	public void onError(Exception e);
}
