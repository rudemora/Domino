package view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import controller.Controller;
import logic.GameObserver;
import logic.GameStatus;
import logic.Player;
import logic.Snake;
import logic.movements.BetMovement;
import logic.movements.PassMovement;

public class MovementMenu extends JPanel implements GameObserver {
	
	private static final long serialVersionUID = 1L;
	
	private Controller controller;
	private JButton passButton;
	private JButton betButton;
	
	private void initGUI() {
		setLayout(new GridLayout(1, 0, 10, 0));
		
		this.passButton = new JButton("Pass");
		passButton.addActionListener((ActionEvent e) -> {
			controller.execute(new PassMovement());
		});
		passButton.setFocusable(false);
		Dimension d = passButton.getMaximumSize();
		d.width = 70;
		passButton.setPreferredSize(d);
		
		add(passButton);
		
		this.betButton = new JButton("Bet");
		betButton.addActionListener((ActionEvent e) -> {
			controller.execute(new BetMovement());
		});
		betButton.setEnabled(false);	// Bets haven't been implemented yet.
		betButton.setFocusable(false);
		betButton.setPreferredSize(d);
		
		add(betButton);		
	}
	
	public MovementMenu(Controller controller) {
		this.controller = controller;
		initGUI();
	}
	
	@Override
	public void onTurnChange(GameStatus status, Snake board) {
		passButton.setEnabled(board.playablePieces(status.currentPlayer().hand()).isEmpty());		
	}

	@Override
	public void onRoundStart(GameStatus status, Snake board) {
		passButton.setEnabled(board.playablePieces(status.currentPlayer().hand()).isEmpty());		
	}
	
	@Override
	public void onRoundEnd(GameStatus status, Snake board, List<Player> players, Player winner) {}

	@Override
	public void onGameEnd(Player winner) {}

	@Override
	public void onPieceAdded(GameStatus status, Snake board) {}

	@Override
	public void onSpecificError(Player currentPlayer, Exception e) {}

	@Override
	public void onError(Exception e) {}

}
