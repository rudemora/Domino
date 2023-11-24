package view;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import controller.Controller;

public class FileMenu extends JMenu {
	
	private static final long serialVersionUID = 1L;
	
	private Controller controller;
	
	private void initGUI() {
		JMenuItem newGameItem = new JMenuItem("New game");
		newGameItem.addActionListener((ActionEvent e) -> ButtonActions.newGame((Frame) SwingUtilities.getRoot(this)));
		add(newGameItem);
		
		JMenuItem loadItem = new JMenuItem("Load game");
		loadItem.addActionListener((ActionEvent e) -> ButtonActions.loadGame(SwingUtilities.getRoot(this), controller));
		loadItem.setEnabled(controller.allowsLoadingAndSaving());
		add(loadItem);
		
		JMenuItem saveItem = new JMenuItem("Save game");
		saveItem.addActionListener((ActionEvent e) -> ButtonActions.saveGame(SwingUtilities.getRoot(this), controller));
		saveItem.setEnabled(controller.allowsLoadingAndSaving());
		add(saveItem);
		
		JMenuItem closeItem = new JMenuItem("Close");
		closeItem.addActionListener((ActionEvent e) -> ButtonActions.closeGame(SwingUtilities.getRoot(this)));
		add(closeItem);
	}
	
	public FileMenu(Controller controller) {
		super("File");
		this.controller = controller;
		initGUI();
	}

}
