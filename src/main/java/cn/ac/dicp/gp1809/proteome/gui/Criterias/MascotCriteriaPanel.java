/* 
 ******************************************************************************
 * File: MascotCriteriaPanel.java * * * Created on 2011-9-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import javax.swing.GroupLayout.Alignment;

public class MascotCriteriaPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable jTable0;
	private JScrollPane jScrollPane0;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public MascotCriteriaPanel() {
		initComponents();
	}

	private void initComponents() {
		setSize(585, 145);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(getJScrollPane0(), javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(getJScrollPane0(), javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
		);
		setLayout(groupLayout);
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
			MascotCriModel model = new MascotCriModel();
			jTable0.setModel(model);
//			jTable0.getColumn("").setCellEditor(cellEditor)
			for(int i=0;i<5;i++){
				TableCellEditor cellEditor = new DefaultCellEditor(new JCheckBox());
				cellEditor.getTableCellEditorComponent(jTable0, Boolean.TRUE, true, 1, i);
			}
		}
		return jTable0;
	}

	private static void installLnF() {
		try {
			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
			if (lnfClassname == null)
				lnfClassname = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lnfClassname);
		} catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL
					+ " on this platform:" + e.getMessage());
		}
	}

	/**
	 * Main entry of the class.
	 * Note: This class is only created so that you can easily preview the result at runtime.
	 * It is not expected to be managed by the designer.
	 * You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("MascotCriteriaPanel");
				MascotCriteriaPanel content = new MascotCriteriaPanel();
				content.setPreferredSize(content.getSize());
				frame.getContentPane().add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
	
	private class MascotCriModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;
		private String [] names;
		private Object [][] objs;
		
		private MascotCriModel(){
			this.names = new String []{"Ion score", "Expect", "Delta ion score",
					"Ion score-MIT", "Ion score-MHT"};
			
			objs = new Object [][]{{new Double(0), new Double(0), new Double(0), 
				new Double(0), new Double(0)}, {new Boolean(true), new Boolean(true), 
					new Boolean(true), new Boolean(true), new Boolean(true), }};
/*			
			this.objs = new Object [2][];
			objs [0] = new Double [5];
//			Arrays.fill(objs[0], new Double(0));
			objs [1] = new Boolean [5];
			Arrays.fill(objs[1], Boolean.TRUE);
*/			
		}

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public int getRowCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return objs[rowIndex][columnIndex];
		}
		
		public String getColumnName(int col) {   
	        return names[col];   
	    } 
		
		@Override
//		public Class getColumnClass(int c) {   
//	        return getValueAt(0, c).getClass();
//	    }
		
		public boolean isCellEditable(int row, int col) {
			return true;
		}
		
		public void setValueAt(Object value, int row, int col) {   
	        objs[row][col] = value;   
	        fireTableCellUpdated(row, col);   
	    }
	}
	
	private class EachRowEditor implements TableCellEditor {

		private Hashtable editors; 
		private TableCellEditor editor, defaultEditor; 
		private JTable table; 

		private EachRowEditor(JTable table){
			this.table = table; 
			editors = new Hashtable(); 
			defaultEditor = new DefaultCellEditor(new JTextField()); 
		}
		
		public void setEditorAt(int row, TableCellEditor editor) { 
			editors.put(new Integer(row),editor); 
		} 

		public Component getTableCellEditorComponent(JTable table, 
			Object value, boolean isSelected, int row, int column) { 
			//editor = (TableCellEditor)editors.get(new Integer(row)); 
			//if (editor == null) { 
			// editor = defaultEditor; 
			//} 
			return editor.getTableCellEditorComponent(table, 
			value, isSelected, row, column); 
		} 

		public Object getCellEditorValue() { 
			return editor.getCellEditorValue(); 
		} 
		
		public boolean stopCellEditing() { 
			return editor.stopCellEditing(); 
		} 
		
		public void cancelCellEditing() { 
			editor.cancelCellEditing(); 
		} 
		
		public boolean isCellEditable(EventObject anEvent) { 
			selectEditor((MouseEvent)anEvent); 
			return editor.isCellEditable(anEvent); 
		} 
		
		public void addCellEditorListener(CellEditorListener l) { 
			editor.addCellEditorListener(l); 
		} 
		
		public void removeCellEditorListener(CellEditorListener l) { 
			editor.removeCellEditorListener(l); 
		} 
			
		public boolean shouldSelectCell(EventObject anEvent) { 
			selectEditor((MouseEvent)anEvent); 
			return editor.shouldSelectCell(anEvent); 
		} 

		protected void selectEditor(MouseEvent e) { 
			int row = 0; 
			if (e == null) { 
			} else { 
			row = table.rowAtPoint(e.getPoint()); 
			} 
			editor = (TableCellEditor)editors.get(new Integer(row)); 
			if (editor == null) { 
			editor = defaultEditor;	
			}
		}
	}
}
