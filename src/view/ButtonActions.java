package view;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.Controller;

public class ButtonActions {

	private static File chooseJSONFile(Component parent) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
		int result = fileChooser.showOpenDialog(parent);
		if(result == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		return null;
	}
	
	public static void newGame(Frame parent) {
		new NewGameDialog(parent);
	}
	
	public static void joinOnlineGame(Frame parent) {
		new JoinGameDialog(parent);
	}
	
	public static boolean loadGame(Component parent, Controller controller) {
		File file = chooseJSONFile(parent);
		if(file != null) {
			try {
				controller.loadGame(file);
				return true;
			}
			catch(Exception ex) {
				JOptionPane.showMessageDialog(parent, "Failed to open file!" + System.lineSeparator() + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}
	
	public static void saveGame(Component parent, Controller controller) {
		File file = chooseJSONFile(parent);
		if(file != null) {
			try {
				controller.saveGame(file);
			}
			catch(Exception ex) {
				JOptionPane.showMessageDialog(parent, "Failed to open file!" + System.lineSeparator() + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public static void closeGame(Component parent) {
		int selection = JOptionPane.showConfirmDialog(parent, "Do you want to exit the game?", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(selection == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}
	
}
