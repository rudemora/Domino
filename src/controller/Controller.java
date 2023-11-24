package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;

import logic.GameObserver;
import logic.Observable;
import logic.Player;
import logic.movements.Movement;

public interface Controller extends Observable<GameObserver> {
	public void execute(Movement movement);
	public void loadGame(File file) throws JSONException, FileNotFoundException, IOException;
	public void saveGame(File file) throws JSONException, FileNotFoundException;
	public boolean usesThisController(Player player);
	public boolean allowsLoadingAndSaving();
}
