package ru.servermonitor.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import ru.servermonitor.actions.ConfigDataXML;
import ru.servermonitor.actions.CustActions;

public class CustTableOutput extends JTable {

	private static final long serialVersionUID = 6339511716278414247L;
	private MyTableModel customModel = null;
	private LabelCellRenderer labelRender = null;
	private String arrayDisks[] = null;
	private int currentRow = 0;
	private int currentColumn = 0;

	CustTableOutput(int row, int col) {

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setBorder(new MatteBorder(2, 2, 2, 2, new Color(128, 128, 128)));
		setRowHeight(15);
		setAutoCreateColumnsFromModel(true);
		setShowGrid(true);
		setShowHorizontalLines(true);
		setRowSelectionAllowed(true);
		setFillsViewportHeight(true);
		setAlignmentX(LEFT_ALIGNMENT);
		setAlignmentY(LEFT_ALIGNMENT);
		setFont(new Font(Font.SERIF, Font.BOLD, 12));
		setBackground(new Color(232, 232, 232));

		// Задаём весь стиль, модель таблицы
		setChangesTable(row, col);
	}

	public MyTableModel getCustomTableModel() {
		return customModel;
	}

	public boolean setChangesTable(int row, int col) {
		if(currentColumn != col || currentRow != row){
			customModel = new MyTableModel(row, col);
			setModel(customModel);
			getColumn("Свободное место, GB").setCellRenderer(new ProgressCellRender());

			changeSelection(0, 0, false, false);
			setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			JTableHeader tHead = null;
			tHead = getTableHeader();
			tHead.setResizingAllowed(false);
			tHead.setFont(new Font(Font.SERIF, Font.BOLD, 12));
			tHead.setEnabled(false);
			tHead.setAlignmentX(LEFT_ALIGNMENT);
			tHead.setAlignmentY(LEFT_ALIGNMENT);
			setTableHeader(tHead);

			labelRender = new LabelCellRenderer();
			TableColumn column = null;
			for (int i = 0; i < getColumnCount(); i++) {
			    column = getColumnModel().getColumn(i);
			    column.setResizable(false);

			    if(i == 0) {
			    	column.setMaxWidth(40);
			    	column.setCellRenderer(labelRender);
			    }

			    if(i == 2) {
			    	column.setMaxWidth(80);
			    	column.setCellRenderer(labelRender);
			    }
			    if(i == 3) {
			    	column.setCellRenderer(new CheckboxCellRenderer());
			    	column.setMaxWidth(200);
			    }
			}

			currentRow = row;
			currentColumn = col;
			return true;
		}
		return false;
	}

	@SuppressWarnings("serial")

	//----- REALIZE YOUR DATA MODEL FOR TABLE
	public class MyTableModel extends AbstractTableModel {

		private Object[] columnNames = {"Диск", "Свободное место, GB", "Всего, GB", "Отметить"};
		private Object[][] data;

		private int row = 0;
		private int col = 0;

		//------ CONSTRUCTOR
		MyTableModel(int row, int col) {
			this.row = row;
			this.col = col;
			data = new Object[row][col];
			resetData();
			refreshDataInTable();
		}

		public Object[][] getData() {
			return data;
		}

		public void refreshDataInTable() {
			int countRow = 0;
			for (File file: File.listRoots()) {
				setValueAt(String.valueOf(file.getAbsolutePath()), countRow, 0);
				setValueAt(String.valueOf(file.getFreeSpace() / (1 << 30)), countRow, 1);
				setValueAt(String.valueOf(file.getTotalSpace() / (1 << 30)), countRow, 2);

				String strBuf = file.getAbsolutePath().toString().replace("\\", "");
				if(ConfigDataXML.getCheckLogicalDisks().matches("(.*)" + strBuf + "(.*)"))
					setValueAt(Boolean.TRUE, countRow, 3);

				++countRow;
			}
		}

		public void resetData() {
			for (int i = 0; i < row; i++)
				for (int j = 0; j < col; j++)
					if(j == (col - 1)){
						//ConfigDataXML.getXMLCheckLogicalDisks();
						data[i][j] = Boolean.FALSE;
					}
					else
						data[i][j] = "None".toString();
		}


		//----- METHODS FOR OVERLOAD
		public void setValueAt(Object obj, int rowIndex, int columnIndex) {
			data[rowIndex][columnIndex] = obj;
			// Перерисовываем таблицу
			fireTableCellUpdated(rowIndex, columnIndex);
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col].toString();
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(columnIndex == 3)
				return true;
			else
				return false;
		}

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return data[0][columnIndex].getClass();
        }

	}


	class CheckboxCellRenderer extends JCheckBox implements TableCellRenderer {

		private static final long serialVersionUID = 6703872492730589499L;

	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

	    	setSelected((Boolean)value);
	    	customModel.setValueAt((Boolean)value, row, 3);
	    	setHorizontalAlignment(JCheckBox.CENTER);
	    	setVerticalAlignment(JCheckBox.CENTER);

	    	// Рисуем фон
	        if((Boolean)table.getValueAt(row, 3) == Boolean.TRUE)
	        	setBackground(new Color(176, 196, 222));
	        else
	        	setBackground(new Color(255, 255, 255));

	    	return this;
	    }
	}

	class LabelCellRenderer extends JLabel implements TableCellRenderer {

		private static final long serialVersionUID = 6703872492730589499L;

	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

	    	setText(value.toString());
	    	setOpaque(true);
	    	setForeground(new Color(119, 136, 153));
	    	setHorizontalAlignment(JLabel.CENTER);
	    	setVerticalAlignment(JLabel.CENTER);

	    	// Рисуем фон
	        if((Boolean)table.getValueAt(row, 3) == Boolean.TRUE)
	        	setBackground(new Color(176, 196, 222));
	        else
	        	setBackground(new Color(255, 255, 255));

	        customModel.fireTableRowsUpdated(row, row);

	    	return this;
	    }
	}

	public class ProgressCellRender extends JProgressBar implements TableCellRenderer {

		private static final long serialVersionUID = 7696432733953482969L;
		String message = null;

		// Сделать по-нормальному
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        	setRowSelectionAllowed(true);
        	setBorderPainted(false);
        	setMinimum(0);

        	if(CustActions.checkString(table.getValueAt(row, 2).toString())
        			&& CustActions.checkString(table.getValueAt(row, 1).toString())
        			&& Integer.parseInt(table.getValueAt(row, 2).toString()) > 0) {
            	setMaximum(Integer.parseInt(table.getValueAt(row, 2).toString()));
            	setValue(Integer.parseInt(table.getValueAt(row, 1).toString()));
        	} else {
        		setMaximum(1);
        		setValue(0);
        	}

        	setStringPainted(true);

            return this;
        }
    }
}