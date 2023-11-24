package view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.InetAddress;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.Client;

public class JoinGameDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private Frame parentFrame;
	
	private void initGUI() {
		
		setLayout(new BorderLayout());
		
		JPanel serverInfoPanel = new JPanel();
		serverInfoPanel.setLayout(new GridLayout(0, 2, 10, 10));
		
		JLabel playerNameLabel = new JLabel("Player name:");
		
		JTextField playerNameField = new JTextField();
		
		serverInfoPanel.add(playerNameLabel);
		serverInfoPanel.add(playerNameField);
		
		JLabel addressLabel = new JLabel("IP Address:");
		
		JTextField addressField = new JTextField();
		
		serverInfoPanel.add(addressLabel);
		serverInfoPanel.add(addressField);

		serverInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JButton connectButton = new JButton("Connect");
		connectButton.addActionListener((ActionEvent e) -> {
			try {
				Client c = new Client(playerNameField.getText(), InetAddress.getByName(addressField.getText()));
				MainWindow mainWindow = new MainWindow(c);
				c.startListening();
				parentFrame.dispose();
				dispose();
				mainWindow.setVisible(true);
			}
			catch(Exception ex) {
				System.out.println(ex);
				JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		connectButton.setFocusable(false);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener((ActionEvent e) -> {
			dispose();
		});
		cancelButton.setFocusable(false);
		cancelButton.setPreferredSize(connectButton.getPreferredSize());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(connectButton);
		buttonPanel.add(cancelButton);
		
		add(serverInfoPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		pack();
		setResizable(false);
		setModal(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	public JoinGameDialog(Frame parentFrame) {
		super(parentFrame, "Join Game");
		this.parentFrame = parentFrame;
		initGUI();
	}
	
}
