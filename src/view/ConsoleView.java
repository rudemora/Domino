
package view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import controller.Controller;
import logic.GameObserver;
import logic.GameStatus;
import logic.Player;
import logic.Snake;
import logic.movements.Movement;
import logic.movements.MovementParser;

public class ConsoleView implements GameObserver {
	
	private static final long serialVersionUID = 1L;
	
	private static final ArrayList<Integer> VALID_ROUND_COUNTS = new ArrayList<>(Arrays.asList(1, 3, 5));
	private static final ArrayList<Integer> VALID_PLAYER_COUNTS = new ArrayList<>(Arrays.asList(1, 2, 3, 4));

	private Controller controller;
	private Scanner sc;
	
	public ConsoleView(Controller controller) {
		this.controller = controller;
		this.sc = new Scanner(System.in);
		controller.addObserver(this);
	}
	
	/**
	 * Asks the user what to do in their turn.
	 * @return The movement chosen by the player.
	 */
	private Movement askMovement() {
		Movement m = null;
		boolean valid;
		do {
			valid = true;
			try {
				System.out.print("Please, enter a movement: ");
				m = MovementParser.parseMovement(sc.nextLine());
			}
			catch(Exception e) {
				onError(e);
				valid = false;
			}
		} while(!valid);
		return m;
	}
	
	/**
	 * Asks the user the maximum amount of rounds to play.
	 * @return The answer.
	 */
	public int askRoundCount() {
		String hint = "[" + VALID_ROUND_COUNTS.stream()
		   								   .map((Integer i) -> i.toString())
		   								   .collect(Collectors.joining(", ")) + "]";
		boolean valid = false;
		int roundCount = Integer.MIN_VALUE;
		while(!valid) {
			try {
				System.out.print("Please, enter the number of rounds you want to play at most (best of) " + hint + ": ");
				roundCount = Integer.parseInt(sc.nextLine());
				if(VALID_ROUND_COUNTS.contains(roundCount)) {
					valid = true;
				}
				else {
					System.out.println("The introduced number is not valid!");
				}
			}
			catch(NumberFormatException nfe) {
				System.out.println("You must enter a number!");
			}
		}
		return roundCount;
	}

	/**
	 * Asks the user how many players there are going to be in the game and their respective names.
	 * @return The list of names.
	 */
	public List<String> askHumanPlayers() {	
		String hint = "[" + VALID_PLAYER_COUNTS.get(0) + "-" + VALID_PLAYER_COUNTS.get(VALID_PLAYER_COUNTS.size() - 1) + "]"; 
		boolean valid = false;
		int humanPlayerCount = Integer.MIN_VALUE;
		while(!valid) {
			try {
				System.out.print("Please, enter the amount of human players in the game " + hint + ": ");
				humanPlayerCount = Integer.parseInt(sc.nextLine());
				if(VALID_PLAYER_COUNTS.contains(humanPlayerCount)) {
					valid = true;
				}
				else {
					System.out.println("The introduced number is not valid!");
				}
				
			}
			catch(NumberFormatException nfe) {
				System.out.println("You must enter a number!");
			}
		}
		
		List<String> players = new ArrayList<>();
		int i = 1;
		while(i <= humanPlayerCount) {
			System.out.print("Player " + i + "'s name: ");
			String name = sc.nextLine();
			if(!players.contains(name)) {
				players.add(i-1, name);
				i++;
			}
			else {
				System.out.println("The introduced player is already on the list!");
			}	
		}
		return players;
	}
	
	/**
	 * Asks the user the amount of players controlled by the AI.
	 * @return The answer.
	 */
	public int askAiPlayers(int humanPlayerCount) {
		int aiPlayerCount = 0;
		if(humanPlayerCount != VALID_PLAYER_COUNTS.get(VALID_PLAYER_COUNTS.size() - 1)) {
			int minAIPlayerCount = humanPlayerCount == 1 ? 1 : 0;
			String hint = "[" + minAIPlayerCount + "-" + (VALID_PLAYER_COUNTS.get(VALID_PLAYER_COUNTS.size() - 1) - humanPlayerCount) + "]"; 
			boolean valid = false;
			while(!valid) {
				try {
					System.out.print("Please, enter the amount of AI players in the game " + hint + ": ");
					aiPlayerCount = Integer.parseInt(sc.nextLine());
					if(aiPlayerCount >= minAIPlayerCount && VALID_PLAYER_COUNTS.contains(aiPlayerCount + humanPlayerCount)) {
						valid = true;
					}
					else {
						System.out.println("The introduced number is not valid!");
					}
				}
				catch(NumberFormatException nfe) {
					System.out.println("You must enter a number!");
				}
			}
			
		}
		return aiPlayerCount;
	}


	@Override
	public void onRoundStart(GameStatus status, Snake board) {}
	
	/**
	 * Prints information about the player that won the round, and all the scores, to stdout.
	 */
	@Override
	public void onRoundEnd(GameStatus status, Snake board, List<Player> players, Player winner) {
		System.out.println();
		System.out.println("The round's over!");
		System.out.println("Winner: " + winner.name());
		System.out.println();
		
		StringBuilder h = new StringBuilder();
		h.append("Statistics:\n");
		for(Player p : players) {
			h.append(p.name()).append(" : ").append(p.globalScore()).append("\n");
		}
		System.out.println(h.toString());
	}
	
	/**
	 * Prints the name of the winner of the whole game, including all rounds.
	 */
	@Override
	public void onGameEnd(Player winner) {
		if(winner == null) {
			System.out.println("The game was aborted!");
		}
		else {
			System.out.println("The game's over!");
			System.out.println("Winner: " + winner.name());
		}
	}


	@Override
	public void onPieceAdded(GameStatus status, Snake board) {}

	@Override
	public void onTurnChange(GameStatus status, Snake board) {
		System.out.println(status);
		
		if(controller.usesThisController(status.currentPlayer())) {
			String pieces = board.playablePieces(status.currentPlayer().hand()).stream()
																			   .map((Integer i) -> i.toString())
																			   .collect(Collectors.joining(", "));
			System.out.print("Valid pieces: ");
			System.out.println(pieces.length() > 0 ? pieces : "None");
			Movement m = askMovement();
			controller.execute(m);
		}
	}

	@Override
	public void onSpecificError(Player currentPlayer, Exception e) {
		if(controller.usesThisController(currentPlayer)) {
			System.err.println(e.getMessage());
			Movement m = askMovement();
			controller.execute(m);
		}
	}

	@Override
	public void onError(Exception e) {
		System.err.println(e.getMessage());
	}

}
