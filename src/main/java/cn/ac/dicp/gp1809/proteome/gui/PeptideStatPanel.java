/* 
 ******************************************************************************
 * File: PeptideStatPanel.java * * * Created on 06-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

/**
 * 
 * @author Xinning
 * @version 0.1, 06-19-2009, 10:50:22
 */
public class PeptideStatPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable jTable0;
	private JScrollPane jScrollPane0;
	private JLabel jLabel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PeptideStatPanel() {
		initComponents();
	}
	
	public PeptideStatPanel(PeptideStatInfo info) {
		initComponents();
		this.loadPeptideStatInfo(info);
	}

	private void initComponents() {
    	setLayout(new GroupLayout());
    	add(getJLabel0(), new Constraints(new Leading(21, 10, 10), new Leading(20, 6, 6)));
    	add(getJScrollPane0(), new Constraints(new Bilateral(19, 20, 370), new Bilateral(50, 18, 10, 192)));
    	setSize(410, 260);
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("<html>Peptides statistic information: </html>");
    	}
    	return jLabel0;
    }

	private JScrollPane getJScrollPane0() {
    	if (jScrollPane0 == null) {
    		jScrollPane0 = new JScrollPane();
    		jScrollPane0.setViewportView(getJTable0());
    	}
    	return jScrollPane0;
    }

	private JTable getJTable0() {
    	if (jTable0 == null) {
    		jTable0 = new JTable();
    		jTable0.setModel(new DefaultTableModel(new String[0][0], PeptideStatInfo.getTableColNames()));
    	}
    	return jTable0;
    }
	
	/**
	 * The peptide statistic info
	 * 
	 * @param info
	 */
	public void loadPeptideStatInfo(PeptideStatInfo info) {
//		DefaultTableModel model = new DefaultTableModel(info.getString4Table(), PeptideStatInfo.getTableColNames());
		PepInfoModel model = new PepInfoModel(info);
		model.fireTableDataChanged();
		this.jTable0.setModel(model);
	}
	
	private class PepInfoModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;
		private String [] names;
		private Object [][] objs;
		
		private PepInfoModel(PeptideStatInfo info){
			this.names = info.getTableColNames();
			this.objs = info.getString4Table();
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return names.length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return objs.length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			return objs[rowIndex][columnIndex];
		}
		
		public String getColumnName(int col) {   
	        return names[col];   
	    } 
		
		public boolean isCellEditable(int row, int col) {
			return false;
		}
		
		public void setValueAt(Object value, int row, int col) {   
	        objs[row][col] = value;   
	        fireTableCellUpdated(row, col);   
	    }
		
	}
	
}
