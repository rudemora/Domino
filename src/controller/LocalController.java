package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import logic.Game;
import logic.GameObserver;
import logic.Player;
import logic.gamemodes.GameMode;
import logic.movements.Movement;

public class LocalController implements Controller {

	private Game game;
	
	public LocalController() {
		this.game = new Game();
	}
	
	public void reset(List<String> playerNames, int AIPlayerCount, GameMode gameMode) {
		game.reset(playerNames, AIPlayerCount, gameMode);
	}
	
	@Override
	public void execute(Movement movement) {
		game.execute(movement);
	}
	
	@Override
	public void addObserver(GameObserver o) {
		game.addObserver(o);
	}
	
	@Override
	public void removeObserver(GameObserver o) {
		game.removeObserver(o);
	}

	@Override
	public boolean usesThisController(Player player) {
		return player.dependsOnUser();
	}

		
	@Override
	public void loadGame(File file) throws JSONException, FileNotFoundException, IOException {
		try(FileInputStream fileInputStream = new FileInputStream(file)) {
			JSONTokener jsonTokener = new JSONTokener(fileInputStream);
			JSONObject json = new JSONObject(jsonTokener);
			game.reset(json);		
		}
		catch(JSONException | FileNotFoundException ex) {
			throw ex;
		}
	}

	@Override
	public void saveGame(File file) throws JSONException, FileNotFoundException {
		try(PrintWriter printWriter = new PrintWriter(new FileOutputStream(file))) {
			printWriter.print(game.save().toString());
		}
		catch(JSONException | FileNotFoundException ex) {
			throw ex;
		}
	}

	@Override
	public boolean allowsLoadingAndSaving() {
		return true;
	}
	
}
