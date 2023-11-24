package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import controller.Controller;
import logic.GameObserver;
import logic.GameStatus;
import logic.Player;
import logic.Snake;

public class MainWindow extends JFrame implements GameObserver {

	private static final long serialVersionUID = 1L;
	
	private Controller controller;
	private BoardPane boardPane;
	private PlayerPanel playerPanel;
	
	private void initGUI() {
		
		setPreferredSize(new Dimension(800, 600));
		setLayout(new BorderLayout());
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(new FileMenu(controller));
		
		this.boardPane = new BoardPane();
		controller.addObserver(boardPane);
		
		add(boardPane, BorderLayout.CENTER);
		
		this.playerPanel = new PlayerPanel(controller);
		controller.addObserver(playerPanel);
		
		add(playerPanel, BorderLayout.SOUTH);
		
		menuBar.add(new ViewMenu(playerPanel));
		
		setJMenuBar(menuBar);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();		
		setLocationRelativeTo(null);
	}
	
	public MainWindow(Controller controller) {
		super("Domino");
		this.controller = controller;
		controller.addObserver(this);
		initGUI();
	}

	@Override
	public void onTurnChange(GameStatus status, Snake board) {}
	
	@Override
	public void onRoundStart(GameStatus status, Snake board) {}

	@Override
	public void onRoundEnd(GameStatus status, Snake board, List<Player> players, Player winner) {
		JOptionPane.showMessageDialog(this, winner.name() + " has won the round!", "Round End", JOptionPane.INFORMATION_MESSAGE);		
	}

	@Override
	public void onGameEnd(Player winner) {
		if(winner != null) {
			JOptionPane.showMessageDialog(this, winner.name() + " has won the game!", "Game End", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void onPieceAdded(GameStatus status, Snake board) {}

	@Override
	public void onSpecificError(Player currentPlayer, Exception e) {
		if(controller.usesThisController(currentPlayer)) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void onError(Exception e) {
		JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if(controller != null) {
			controller.removeObserver(this);
			controller.removeObserver(boardPane);
			controller.removeObserver(playerPanel);
		}
	}
	
}
