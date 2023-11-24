package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.Controller;
import logic.GameObserver;
import logic.GameStatus;
import logic.Player;
import logic.Snake;

public class PlayerPanel extends JPanel implements GameObserver {
	
	private static final long serialVersionUID = 1L;
	
	private Controller controller;
	private JLabel playerLabel;
	private JLabel scoreLabel;
	private JPanel infoPanel;
	private JPanel movementPanel;
	private PiecePanel piecePanel;
	private MovementMenu movementMenu;
	
	private void update(GameStatus status) {
		playerLabel.setText(status.currentPlayer().name());
		scoreLabel.setText(status.gameMode().globalScoreName() + ": " + status.currentPlayer().globalScore());
		piecePanel.setVisible(controller.usesThisController(status.currentPlayer()));
		movementPanel.setVisible(controller.usesThisController(status.currentPlayer()));
	}
	
	private void initGUI(Controller controller) {
		this.playerLabel = new JLabel();
		this.scoreLabel = new JLabel();
		this.infoPanel = new JPanel();
		this.movementPanel = new JPanel();
		this.movementMenu = new MovementMenu(controller);
		this.piecePanel = new PiecePanel(controller);
		
		JPanel auxiliarPanel = new JPanel();
		auxiliarPanel.setLayout(new GridBagLayout());
		auxiliarPanel.add(piecePanel);
		
		controller.addObserver(movementMenu);
		controller.addObserver(piecePanel);
				
		setLayout(new BorderLayout());
		infoPanel.setLayout(new GridLayout(0, 1, 0, 0));
		movementPanel.setLayout(new GridBagLayout());
		
		infoPanel.add(playerLabel);
		infoPanel.add(scoreLabel);
		movementPanel.add(movementMenu);
				
		movementPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		
		infoPanel.setPreferredSize(movementPanel.getPreferredSize());
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		
		add(infoPanel, BorderLayout.WEST);
		add(auxiliarPanel, BorderLayout.CENTER);
		add(movementPanel, BorderLayout.EAST);
		
		setVisible(false);
	}
	
	public PlayerPanel(Controller controller) {
		this.controller = controller;
		initGUI(controller);
	}
	
	public PiecePanel piecePanel() {
		return piecePanel;
	}

	@Override
	public void onTurnChange(GameStatus status, Snake board) {
		update(status);
	}

	@Override
	public void onPieceAdded(GameStatus status, Snake board) {}

	@Override
	public void onRoundStart(GameStatus status, Snake board) {
		update(status);
		setVisible(true);
	}

	@Override
	public void onRoundEnd(GameStatus status, Snake board, List<Player> players, Player winner) {}

	@Override
	public void onGameEnd(Player winner) {
		setVisible(false);
	}

	@Override
	public void onSpecificError(Player currentPlayer, Exception e) {}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(0, 60);
	}

	@Override
	public void onError(Exception e) {}
	
}
