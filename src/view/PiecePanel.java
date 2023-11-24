package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import controller.Controller;
import logic.GameObserver;
import logic.GameStatus;
import logic.Piece;
import logic.Player;
import logic.Snake;
import logic.movements.PlaceMovement;

public class PiecePanel extends JPanel implements GameObserver {

	private static final long serialVersionUID = 1L;

	private class PieceComponent extends JComponent {
		
		private static final long serialVersionUID = 1L;
		
		private static final Color PLAYABLE_COLOR = new Color(0, 180, 0);
		private static final Color NON_PLAYABLE_COLOR = new Color(200, 0, 0);
		
		private Piece piece;
		private boolean playable;
		
		public PieceComponent(Piece piece, boolean playable) {
			this.piece = piece;
			this.playable = playable;
		}
		
		@Override
		public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;
			FontMetrics fm = g.getFontMetrics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			
			if(hintsEnabled) {
				g.setColor(playable ? PLAYABLE_COLOR : NON_PLAYABLE_COLOR);
			}
			else {
				g.setColor(Color.BLACK);
			}
			
			int x = 0;
			int y = 0;
			g.drawRect(x, y, PIECE_SQUARE_WIDTH, PIECE_SQUARE_WIDTH);
			
			String leftValue = String.valueOf(piece.getLeft());
			g.drawString(leftValue, x + PIECE_SQUARE_WIDTH / 2 - fm.stringWidth(leftValue) / 2 , y + PIECE_SQUARE_WIDTH / 2 - fm.getHeight() / 2 + fm.getAscent());
			
			y += PIECE_SQUARE_WIDTH;
			
			g.drawRect(x, y, PIECE_SQUARE_WIDTH, PIECE_SQUARE_WIDTH);
			
			String rightValue = String.valueOf(piece.getRight());
			g.drawString(rightValue, x + PIECE_SQUARE_WIDTH / 2 - fm.stringWidth(rightValue) / 2 , y + PIECE_SQUARE_WIDTH / 2 - fm.getHeight() / 2 + fm.getAscent());
		}
		
		public Dimension getPreferredSize() { 
			return new Dimension(PIECE_SQUARE_WIDTH + 1, 2 * PIECE_SQUARE_WIDTH + 1);
		}
		
		public void onClick(MouseEvent e) {
			Integer pos = pieceComponents.indexOf(this);
			
			if(e.getButton() == MouseEvent.BUTTON1) {	// Left click
				controller.execute(new PlaceMovement(pos, 'L'));
			}
			else if(e.getButton() == MouseEvent.BUTTON3) {	// Right click
				controller.execute(new PlaceMovement(pos, 'R'));
			}
			
		}
		
	}
	
	private static final int PIECE_SQUARE_WIDTH = 20;
	
	private Controller controller;
	private List<PieceComponent> pieceComponents;
	private boolean hintsEnabled;
	
	private void update(GameStatus status, Snake board) {
		pieceComponents.clear();
		if(status.currentPlayer().dependsOnUser()) {
			List<Integer> playablePieces = board.playablePieces(status.currentPlayer().hand());
			List<Piece> pieces = status.currentPlayer().hand().pieces();
			pieceComponents.clear();
			int i = 0;
			for(int j = 0; j < pieces.size(); j++) {
				if(i < playablePieces.size() && playablePieces.get(i) == j) {
					pieceComponents.add(new PieceComponent(pieces.get(j), true));
					i++;
				}
				else {
					pieceComponents.add(new PieceComponent(pieces.get(j), false));
				}
			}
		}
		resetGUI();
	}
	
	private void initGUI() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Component component = getComponentAt(e.getPoint());
				int index = pieceComponents.indexOf(component);
				if(index != -1) {
					pieceComponents.get(index).onClick(e);
				}
			}
		});
				
		resetGUI();
	}
	
	private void resetGUI() {
		removeAll();
		for(PieceComponent p : pieceComponents) {
			add(p);
		}
		revalidate();
		repaint();
	}
	
	public PiecePanel(Controller controller) {
		this.controller = controller;
		this.pieceComponents = new LinkedList<PieceComponent>();
		this.hintsEnabled = true;
		initGUI();
	}
	
	public void toggleHints() {
		hintsEnabled = !hintsEnabled;
		resetGUI();
	}
	
	@Override
	public void onTurnChange(GameStatus status, Snake board) {
		update(status, board);
	}
	
	@Override
	public void onRoundStart(GameStatus status, Snake board) {
		update(status, board);
	}

	@Override
	public void onPieceAdded(GameStatus status, Snake board) {
		update(status, board);
	}
	
	@Override
	public void onRoundEnd(GameStatus status, Snake board, List<Player> players, Player winner) {}

	@Override
	public void onGameEnd(Player winner) {}
	
	@Override
	public void onSpecificError(Player currentPlayer, Exception e) {}

	@Override
	public void onError(Exception e) {}

}
