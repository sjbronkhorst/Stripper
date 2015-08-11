package linalg;

import serialize.ProfileMatrix;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class VisualProfileMatrix extends AbstractTableModel{
	
	private static final long serialVersionUID = 1L;
	private ProfileMatrix mat;
	private boolean[] status;
	
	public VisualProfileMatrix(String name, ProfileMatrix pmat, boolean[] status){
		this.mat = pmat;
		this.status = status;
		if(pmat.size() != status.length)
			throw new MatrixDimensionException("incorrect status vector dimension");
		JFrame frame = new JFrame(name);
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize().width,768);
		JTable t;		
		frame.add(new JScrollPane(t = new JTable(this)));
		t.setDefaultRenderer(Object.class, new StatusRenderer());
		t.setColumnSelectionAllowed(true);
		t.setRowHeight(30);
		frame.setVisible(true);
	}
	
	
	public int getColumnCount() {
		return status.length;
	}

	public int getRowCount() {
		return status.length;
	}

	public String getColumnName(int column) {
		return "dof"+column;
	}
	
	public Object getValueAt(int row, int col) {
		if(col <= row && col >= mat.profile(row))
			return mat.getValue(row, col);
		else if(row >= mat.profile(col) && row < col)
			return mat.getValue(col, row);
		return "";
	}

	class StatusRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;
		
		JLabel tf = new JLabel();
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
			if(status[row] || status[column])
				setForeground(Color.RED);
			else
				setForeground(Color.BLUE);
			if(row == column)
				setFont(getFont().deriveFont(Font.BOLD));
			else
				setFont(getFont().deriveFont(Font.PLAIN));
			return this;
			
		}
		
	}
	
}
