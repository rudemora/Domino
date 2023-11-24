package view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class PlayerNamesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private int humanPlayerCount;
	private List<String> names;
	
	public PlayerNamesTableModel() {
		this.humanPlayerCount = 1;
		this.names = new ArrayList<String>();
		names.add(null);
	}
	
	public void updatePlayerCount(int newCount) {
		if(newCount >= 1) {
			while(humanPlayerCount < newCount) {
				names.add("");
				humanPlayerCount++;
			}
			while(humanPlayerCount > newCount) {
				names.remove(humanPlayerCount - 1);
				humanPlayerCount--;
			}
			fireTableDataChanged();
		}
	}
	
	@Override
	public int getRowCount() {
		return humanPlayerCount;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		if(columnIndex == 0) {
			return "Player";
		}
		else if(columnIndex == 1) {
			return "Name";
		}
		return null;
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return rowIndex < getRowCount() && columnIndex == 1;
	}
	
	@Override
	public void setValueAt(Object obj, int rowIndex, int columnIndex) {
		names.set(rowIndex, obj.toString());
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 0 && rowIndex < getRowCount()) {
			return "Human Player " + (rowIndex + 1);
		}
		else if(columnIndex == 1 && rowIndex < getRowCount()) {
			return names.get(rowIndex);
		}
		return null;
	}
	
	public List<String> names() {
		return Collections.unmodifiableList(names);
	}
	
	public String getPlayerName(int player) {
		return names.get(player);
	}

}
