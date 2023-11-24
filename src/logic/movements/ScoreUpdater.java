package logic.movements;

import logic.gamemodes.ClassicGameMode;
import logic.gamemodes.ScoreGameMode;

public interface ScoreUpdater {
	public void updateScore(ClassicGameMode mode);
	public void updateScore(ScoreGameMode mode);
}
