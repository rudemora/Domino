package controller;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import logic.GameObserver;
import logic.GameStatus;
import logic.Player;
import logic.Snake;
import logic.movements.Movement;

public class Client implements Controller, GameObserver {

	private static final long serialVersionUID = 1L;

	private class ClientListener extends Thread {
		
		private volatile boolean listening;
		
		public ClientListener() {
			this.listening = true;
		}
		
		@Override
		public void run() {
			while(listening) {
				try {
					@SuppressWarnings("unchecked")
					SerializableConsumer<GameObserver> func = (SerializableConsumer<GameObserver>) input.readObject();
					for(GameObserver o : observers) {
						func.accept(o);
					}
				}
				catch(IOException ioe) {
					observers.forEach((GameObserver o) -> o.onError(new ConnectException("Disconnected from server!")));
					observers.forEach((GameObserver o) -> o.onGameEnd(null));
				}
				catch(Exception e) {
					observers.forEach((GameObserver o) -> o.onError(e));
					observers.forEach((GameObserver o) -> o.onGameEnd(null));
				}
			}
		}
		
	}
	
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private String name;
	private ClientListener listener;
	private List<GameObserver> observers;
	
	private void stop() {
		listener.listening = false;
		try {
			socket.close();
			input.close();
			output.close();
		}
		catch(IOException ioe) {
			for(GameObserver o : observers) {
				o.onError(ioe);
			}
		}
	}
	
	public Client(String name, InetAddress ip) throws IOException {
		this.socket = new Socket();
		socket.connect(new InetSocketAddress(ip, Server.DEFAULT_PORT), 5000);
		this.output = new ObjectOutputStream(socket.getOutputStream());
		output.writeObject(name);
		this.input = new ObjectInputStream(socket.getInputStream());
		this.name = name;
		this.listener = new ClientListener();
		this.observers = new ArrayList<>();
		addObserver(this);
	}
	
	public void startListening() {
		listener.listening = true; 
		listener.start();
	}
	
	@Override
	public void execute(Movement movement) {
		try {
			output.writeObject(movement);
			output.reset();
		}
		catch(IOException ioe) {
			observers.forEach((GameObserver o) -> o.onError(ioe));
		}
	}

	@Override
	public void addObserver(GameObserver o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(GameObserver o) {
		observers.remove(o);
	}
	
	@Override
	public boolean usesThisController(Player player) {
		return player.name().equals(name);
	}
	
	@Override
	public void loadGame(File file) {}

	@Override
	public void saveGame(File file) {}

	@Override
	public boolean allowsLoadingAndSaving() {
		return false;
	}

	@Override
	public void onTurnChange(GameStatus status, Snake board) {}

	@Override
	public void onPieceAdded(GameStatus status, Snake board) {}

	@Override
	public void onRoundStart(GameStatus status, Snake board) {}

	@Override
	public void onRoundEnd(GameStatus status, Snake board, List<Player> players, Player winner) {}

	@Override
	public void onGameEnd(Player winner) {
		stop();
	}

	@Override
	public void onSpecificError(Player player, Exception e) {}

	@Override
	public void onError(Exception e) {}

}
