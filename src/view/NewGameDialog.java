package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

import controller.Client;
import controller.LocalController;
import controller.Server;
import logic.Game;
import logic.gamemodes.GameMode;

public class NewGameDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private Frame parentFrame;
	
	private void initGUI() {
		
		setLayout(new BorderLayout());
		
		JPanel configPanel = new JPanel();
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
		
		JTable playerNamesTable = new JTable(1, 2);
		PlayerNamesTableModel playerNamesTableModel = new PlayerNamesTableModel();
		playerNamesTable.setModel(playerNamesTableModel);
		playerNamesTable.setPreferredScrollableViewportSize(new Dimension(playerNamesTable.getColumnModel().getTotalColumnWidth(), playerNamesTable.getRowHeight() * 4));
		
		JCheckBox onlineCheckBox = new JCheckBox("Online (create a server)");
		
		JSpinner AIPlayerSpinner = new JSpinner();
		SpinnerNumberModel AIPlayerModel = new SpinnerNumberModel(1, 1, 3, 1);
		AIPlayerSpinner.setModel(AIPlayerModel);
		((JSpinner.NumberEditor) AIPlayerSpinner.getEditor()).getTextField().setEditable(false);
		
		JSpinner humanPlayerSpinner = new JSpinner();
		humanPlayerSpinner.setModel(new SpinnerNumberModel(1, 1, 4, 1));
		humanPlayerSpinner.addChangeListener((ChangeEvent e) -> {
			int value = (int) humanPlayerSpinner.getValue();
			AIPlayerModel.setMinimum(value == 1 ? 1 : 0);
			AIPlayerModel.setMaximum(4 - value);
			AIPlayerModel.setValue(AIPlayerModel.getMinimum());
			if(!onlineCheckBox.isSelected()) {
				playerNamesTableModel.updatePlayerCount(value);
			}
		});
		((JSpinner.NumberEditor) humanPlayerSpinner.getEditor()).getTextField().setEditable(false);

		JComboBox<GameMode> gameModeBox = new JComboBox<>(Game.MODES);
		
		JPanel spinnerPanel = new JPanel();
		spinnerPanel.setLayout(new GridLayout(3, 2, 10, 10));
		spinnerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		spinnerPanel.add(new JLabel("Human Players:"));
		spinnerPanel.add(humanPlayerSpinner);
		spinnerPanel.add(new JLabel("AI Players:"));
		spinnerPanel.add(AIPlayerSpinner);
		spinnerPanel.add(new JLabel("Game mode:"));
		spinnerPanel.add(gameModeBox);
		
		configPanel.add(spinnerPanel);
				
		Map<String, JPanel> gameModeMap = new HashMap<>();
		Map<String, ArrayList<JTextField>> parameterMap = new HashMap<>();
		for(GameMode mode : Game.MODES) {
			String name = mode.toString();
			JPanel panel = new JPanel();
			parameterMap.put(name, new ArrayList<JTextField>());
			panel.setLayout(new GridLayout(0, 2, 10, 10));
			panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			for(String param : mode.parameters()) {
				panel.add(new JLabel(param + ":"));
				JTextField textField = new JTextField();
				textField.setHorizontalAlignment(SwingConstants.RIGHT);
				panel.add(textField);
				parameterMap.get(name).add(textField);
			}
			configPanel.add(panel);
			panel.setVisible(false);
			gameModeMap.put(name, panel);
		}
		
		gameModeMap.get(gameModeBox.getItemAt(0).toString()).setVisible(true);
		gameModeBox.setEditable(false);
		gameModeBox.addActionListener((ActionEvent e) -> {
			for(GameMode mode : Game.MODES) {
				gameModeMap.get(mode.toString()).setVisible(mode == (GameMode) gameModeBox.getSelectedItem());
			}
		});
		
		JScrollPane tablePanel = new JScrollPane(playerNamesTable);

		tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		onlineCheckBox.setSelected(false);
		onlineCheckBox.setAlignmentX(CENTER_ALIGNMENT);
		onlineCheckBox.addChangeListener((ChangeEvent e) -> {
			playerNamesTableModel.updatePlayerCount(onlineCheckBox.isSelected() ? 1 : (int) humanPlayerSpinner.getValue());
		});
		
		configPanel.add(onlineCheckBox);
		configPanel.add(tablePanel);
		
		add(configPanel, BorderLayout.NORTH);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener((ActionEvent e) -> dispose());
		cancelButton.setFocusable(false);
		
		JButton startGameButton = new JButton("Start Game");
		startGameButton.addActionListener((ActionEvent e) -> {
			int humanPlayerCount = (int) humanPlayerSpinner.getValue();
			int AIPlayerCount = (int) AIPlayerSpinner.getValue();
			
			GameMode gameMode = (GameMode) gameModeBox.getSelectedItem();
			String[] parameters = new String[gameMode.parameters().length];
			
			ArrayList<JTextField> parameterList = parameterMap.get(gameMode.toString());
			for(int i = 0; i < parameters.length; i++) {
				parameters[i] = parameterList.get(i).getText();
			}
			
			try {
				gameMode.initialize(parameters);
				
				if(onlineCheckBox.isSelected()) {
					try {
						// Initialize the server thread
						Server s = new Server();
						new Thread(() -> s.reset(humanPlayerCount, AIPlayerCount, gameMode)).start();

						// Initialize the client thread
						Client c = new Client(playerNamesTableModel.getPlayerName(0), s.address());
						MainWindow mainWindow = new MainWindow(c);
						c.startListening();
						mainWindow.setVisible(true);
					}
					catch(Exception ex) {
						JOptionPane.showMessageDialog(this, ex.getMessage(), "Online Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else {
					LocalController lc = new LocalController();
					MainWindow mainWindow = new MainWindow(lc);
					mainWindow.setVisible(true);
					lc.reset(playerNamesTableModel.names(), AIPlayerCount, gameMode);
				}
				
				dispose();
				parentFrame.dispose();
			}
			catch(IllegalArgumentException iae) {
				JOptionPane.showMessageDialog(this, iae.getMessage(), "Game Mode Error", JOptionPane.ERROR_MESSAGE);
			}
			
		});
		startGameButton.setFocusable(false);
		
		cancelButton.setPreferredSize(startGameButton.getPreferredSize());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(cancelButton);
		buttonPanel.add(startGameButton);
		
		add(buttonPanel, BorderLayout.SOUTH);
		
		pack();
		setResizable(false);
		setModal(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	public NewGameDialog(Frame parentFrame) {
		super(parentFrame, "New game");
		this.parentFrame = parentFrame;
		initGUI();
	}
	
}
