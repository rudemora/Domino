package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.Game;
import logic.GameObserver;
import logic.GameStatus;
import logic.Player;
import logic.Snake;
import logic.gamemodes.GameMode;
import logic.movements.Movement;

public class Server implements GameObserver {
	
	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_PORT = 7000;
	public static final int DEFAULT_QUEUE_SIZE = 10;
	
	private ServerSocket serverSocket;
	private Map<String, Socket> sockets;
	private Map<String, ObjectOutputStream> outputs;
	private Map<String, ObjectInputStream> inputs;
	private Game game;
	
	private void closeSocket(String name) {
		try {
			sockets.get(name).close();
			outputs.get(name).close();
			inputs.get(name).close();
		}
		catch(IOException ioe) {}
		sockets.remove(name);
		outputs.remove(name);
		inputs.remove(name);
	}
	
	private void stop() {
		try {
			serverSocket.close();
			String[] clients = sockets.keySet().toArray(String[]::new);
			for(String client : clients) {
				closeSocket(client);
			}
		}
		catch(IOException ioe) {}
	}
	
	private void listenMovements() {
		try {
			Movement m = (Movement) inputs.get(game.status().currentPlayer().name()).readObject();
			game.execute(m);
		}
		catch(IOException e) {
			onError(new ConnectException("One of the players has left the game!"));
			onGameEnd(null);
		}
		catch(Exception e) {
			onError(e);
			onGameEnd(null);
		}
	}
	
	private void notifyClients(SerializableConsumer<GameObserver> func) {
		for(String client : outputs.keySet()) {
			try {
				outputs.get(client).writeObject(func);
				outputs.get(client).reset();
			}
			catch(Exception e) {		// The player has left the game or there is another connection problem.
				closeSocket(client);
			}
		}
	}
	
	public Server() throws IOException {
		this.serverSocket = new ServerSocket(DEFAULT_PORT, DEFAULT_QUEUE_SIZE, InetAddress.getLocalHost());
		this.sockets = new HashMap<>();
		this.outputs = new HashMap<>();
		this.inputs = new HashMap<>();
		this.game = new Game();
		game.addObserver(this);
	}
	
	public void reset(int humanPlayerCount, int AIPlayerCount, GameMode gameMode) {
		try {
			List<String> names = new ArrayList<>();
			while(humanPlayerCount > 0) {					// Wait for everyone to connect before starting the game
				Socket client = serverSocket.accept();
				ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
				String name = (String) ois.readObject();
				sockets.put(name, client);
				inputs.put(name, ois);
				outputs.put(name, new ObjectOutputStream(client.getOutputStream()));
				names.add(name);
				--humanPlayerCount;
			}
			game.reset(names, AIPlayerCount, gameMode);
		}
		catch(Exception e) {
			onError(e);
		}
	}
	
	public InetAddress address() {
		return serverSocket.getInetAddress();
	}

	@Override
	public void onTurnChange(GameStatus status, Snake board) {
		notifyClients((GameObserver o) -> o.onTurnChange(status, board));
		if(game.status().currentPlayer().dependsOnUser()) {
			listenMovements();
		}
	}

	@Override
	public void onPieceAdded(GameStatus status, Snake board) {
		notifyClients((GameObserver o) -> o.onPieceAdded(status, board));
	}

	@Override
	public void onRoundStart(GameStatus status, Snake board) {
		notifyClients((GameObserver o) -> o.onRoundStart(status, board));
	}

	@Override
	public void onRoundEnd(GameStatus status, Snake board, List<Player> players, Player winner) {
		notifyClients((GameObserver o) -> o.onRoundEnd(status, board, players, winner));
	}

	@Override
	public void onGameEnd(Player winner) {
		notifyClients((GameObserver o) -> o.onGameEnd(winner));
		stop();
	}

	@Override
	public void onSpecificError(Player player, Exception e) {
		notifyClients((GameObserver o) -> o.onSpecificError(player, e));
	}

	@Override
	public void onError(Exception e) {
		notifyClients((GameObserver o) -> o.onError(e));
	}
	
}
