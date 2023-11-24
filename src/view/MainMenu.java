package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.Controller;
import controller.LocalController;

public class MainMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static final String BOARD_IMAGE_PATH = "resources/board_image.png";
	private static final String LOGO_IMAGE_PATH = "resources/logo.png";
		
	private ImageIcon loadImage(String path) {
		try {
			return new ImageIcon(ImageIO.read(new File(path)));
		}
		catch(IOException ioe) {
			JOptionPane.showMessageDialog(this, "Couldn't load image resources!", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	private static JButton generateButton(String name, ActionListener l) {
		JButton newButton = new JButton(name);
		newButton.addActionListener(l);
		newButton.setFocusable(false);
		newButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		return newButton;
	}
	
	private void initGUI() {
		
		setPreferredSize(new Dimension(800, 600));
		setLayout(new GridBagLayout());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		JButton newGameButton = generateButton("New Game", (ActionEvent e) -> ButtonActions.newGame(this));
		
		JButton joinOnlineGameButton = generateButton("Join Online Game", (ActionEvent e) -> ButtonActions.joinOnlineGame(this));
		
		JButton loadGameButton = generateButton("Load Game", (ActionEvent e) -> {
			Controller c = new LocalController();
			MainWindow mainWindow = new MainWindow(c);
			if(ButtonActions.loadGame(this, c)) {
				dispose();
				mainWindow.setVisible(true);
			}
		});
		
		JButton quitButton = generateButton("Quit", (ActionEvent e) -> ButtonActions.closeGame(this));
		
		JLabel boardImage = new JLabel(loadImage(BOARD_IMAGE_PATH));
		boardImage.setAlignmentX(CENTER_ALIGNMENT);
		
		JLabel logoImage = new JLabel(loadImage(LOGO_IMAGE_PATH));
		logoImage.setAlignmentX(CENTER_ALIGNMENT);
		
		JPanel logoPanel = new JPanel();
		logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.X_AXIS));
		logoPanel.add(boardImage);
		logoPanel.add(Box.createHorizontalStrut(26));
		logoPanel.add(logoImage);
		
		mainPanel.add(logoPanel);
		mainPanel.add(Box.createVerticalStrut(30));
		mainPanel.add(newGameButton);
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(loadGameButton);
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(joinOnlineGameButton);
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(quitButton);
		
		add(mainPanel);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();		
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public MainMenu() {
		super("Domino");
		initGUI();
	}
	
}
