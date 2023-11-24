package view;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

public class ViewMenu extends JMenu {

	private static final long serialVersionUID = 1L;
	
	private PlayerPanel playerPanel;
	
	private void initGUI() {
		JCheckBoxMenuItem pieceHintsItem = new JCheckBoxMenuItem("Hints", true);
		pieceHintsItem.addActionListener((ActionEvent e) -> {
			playerPanel.piecePanel().toggleHints();
		});
		add(pieceHintsItem);
	}
	
	public ViewMenu(PlayerPanel playerPanel) {
		super("View");
		this.playerPanel = playerPanel;
		initGUI();
	}
}
