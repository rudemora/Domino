package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import logic.GameObserver;
import logic.GameStatus;
import logic.Piece;
import logic.Player;
import logic.Snake;

public class BoardPane extends JScrollPane implements GameObserver {
	
	private static final long serialVersionUID = 1L;

	private class BoardComponent extends JComponent {
		
		private static final long serialVersionUID = 1L;
		
		private static final int PIECE_SQUARE_WIDTH = 20;
		private static final int PIECE_SEPARATION = 8;
		
		private enum DrawingDirection {
			RIGHT, DOWN, LEFT
		}
		
		private Snake board;
		
		private void update(Snake board) {
			this.board = board;
		}
		
		public BoardComponent() {
			this.board = null;
		}
		
		@Override
		public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;
			Dimension preferredSize = getPreferredSize();
			FontMetrics fm = g.getFontMetrics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			g.setColor(Color.BLACK);
			
			DrawingDirection direction = DrawingDirection.RIGHT;
			int x = 0;
			int y = 0;
			for(Piece p : board.pieces()) {
				int initialX = x;
				int initialY = y;
				
				g.drawRect(x, y, PIECE_SQUARE_WIDTH, PIECE_SQUARE_WIDTH);
				
				String leftValue = String.valueOf(p.getLeft());
				String rightValue = String.valueOf(p.getRight());
				
				if(direction == DrawingDirection.LEFT) {
					g.drawString(rightValue, x + PIECE_SQUARE_WIDTH / 2 - fm.stringWidth(rightValue) / 2 , y + PIECE_SQUARE_WIDTH / 2 - fm.getHeight() / 2 + fm.getAscent());
				}
				else {
					g.drawString(leftValue, x + PIECE_SQUARE_WIDTH / 2 - fm.stringWidth(leftValue) / 2 , y + PIECE_SQUARE_WIDTH / 2 - fm.getHeight() / 2 + fm.getAscent());
				}
				
				if(direction == DrawingDirection.DOWN) {
					y += PIECE_SQUARE_WIDTH;
				}
				else {
					x += PIECE_SQUARE_WIDTH;
				}
				
				g.drawRect(x, y, PIECE_SQUARE_WIDTH, PIECE_SQUARE_WIDTH);
				
				if(direction == DrawingDirection.LEFT) {
					g.drawString(leftValue, x + PIECE_SQUARE_WIDTH / 2 - fm.stringWidth(leftValue) / 2 , y + PIECE_SQUARE_WIDTH / 2 - fm.getHeight() / 2 + fm.getAscent());
				}
				else {
					g.drawString(rightValue, x + PIECE_SQUARE_WIDTH / 2 - fm.stringWidth(rightValue) / 2 , y + PIECE_SQUARE_WIDTH / 2 - fm.getHeight() / 2 + fm.getAscent());
				}
				
				x = initialX;
				y = initialY;
				
				if(direction == DrawingDirection.DOWN) {
					if(x < preferredSize.width / 2) {
						direction = DrawingDirection.RIGHT;
					}
					else {
						direction = DrawingDirection.LEFT;
						x -= PIECE_SQUARE_WIDTH;
					}
					y += 2 * PIECE_SQUARE_WIDTH + PIECE_SEPARATION;
				}
				else if(direction == DrawingDirection.RIGHT) {
					if(x + 6 * PIECE_SQUARE_WIDTH + PIECE_SEPARATION <= preferredSize.width) {
						x += 2 * PIECE_SQUARE_WIDTH + PIECE_SEPARATION;
					}
					else {
						x += PIECE_SQUARE_WIDTH;
						y += PIECE_SQUARE_WIDTH + PIECE_SEPARATION;
						direction = DrawingDirection.DOWN;
					}
				}
				else if(direction == DrawingDirection.LEFT) {
					if(x - 2 * PIECE_SQUARE_WIDTH - PIECE_SEPARATION >= 0) {
						x -= 2 * PIECE_SQUARE_WIDTH + PIECE_SEPARATION;
					}
					else {
						y += PIECE_SQUARE_WIDTH + PIECE_SEPARATION;
						direction = DrawingDirection.DOWN;
					}
				}
			}
		}
		
		@Override
		public Dimension getPreferredSize() {
			if(board == null) {
				return new Dimension(0, 0);
			}
			Dimension parentDimension = getParent().getSize();
			int piecesPerRow = (parentDimension.width - 2 * PIECE_SQUARE_WIDTH + PIECE_SEPARATION) / (2 * PIECE_SQUARE_WIDTH + PIECE_SEPARATION);
			int rowCount = (int) (Math.ceil((board.pieces().size() + 1) / (double) piecesPerRow));
			int bridgeCount = rowCount - 1;
			int height = rowCount * PIECE_SQUARE_WIDTH + bridgeCount * 2 * (PIECE_SQUARE_WIDTH + PIECE_SEPARATION) + 1;
			return new Dimension(piecesPerRow * PIECE_SQUARE_WIDTH * 2 + (piecesPerRow - 1) * PIECE_SEPARATION + 1, height);
		}
			
	}
	
	private JPanel boardPanel;
	private JLabel waitingLabel;
	private BoardComponent boardComponent;

	private void initGUI() {
		boardPanel.setLayout(new GridBagLayout());
		boardPanel.add(waitingLabel);
		boardPanel.add(boardComponent);
		setViewportView(boardPanel);
	}
	
	public BoardPane() {
		this.boardPanel = new JPanel();
		this.waitingLabel = new JLabel("Waiting for the game to start...");
		this.boardComponent = new BoardComponent();
		initGUI();
	}

	@Override
	public void onPieceAdded(GameStatus status, Snake board) {
		boardComponent.update(board);
		boardPanel.revalidate();
		revalidate();
		repaint();
	}

	@Override
	public void onRoundStart(GameStatus status, Snake board) {
		waitingLabel.setVisible(false);
		boardComponent.update(board);
		boardPanel.revalidate();
		boardPanel.setVisible(true);
		revalidate();
		repaint();
	}

	@Override
	public void onTurnChange(GameStatus status, Snake board) {}
	
	@Override
	public void onRoundEnd(GameStatus status, Snake board, List<Player> players, Player winner) {}

	@Override
	public void onGameEnd(Player winner) {
		boardPanel.removeAll();
		if(winner == null) {
			boardPanel.add(new JLabel("The game was aborted!"));
		}
		else {
			boardPanel.add(new JLabel("The game is finished! (Winner: " + winner.name() + ")"));
		}
		boardPanel.revalidate();
		revalidate();
		repaint();
	}

	@Override
	public void onSpecificError(Player currentPlayer, Exception e) {}

	@Override
	public void onError(Exception e) {}

}
