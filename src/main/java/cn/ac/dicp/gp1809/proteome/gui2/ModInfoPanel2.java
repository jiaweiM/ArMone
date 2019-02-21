/* 
 ******************************************************************************
 * File:ModInfoPanel2.java * * * Created on 2011-10-12
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultVariModPepFilter;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NoVairModFilter;

//VS4E -- DO NOT REMOVE THIS LINE!
public class ModInfoPanel2 extends JPanel implements TableModelListener, ActionListener {

	private JTable jTable0;
	private JScrollPane jScrollPane0;
	private JLabel jLabel0;
	private VariModRowGetter getter;
	private static final long serialVersionUID = 1L;
	private JButton jButtonSelect;
	private JButton jButtonDispose;
	private JCheckBox jCheckBoxAll;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public ModInfoPanel2() {
		initComponents();
	}
	
	public ModInfoPanel2(AminoacidModification aaf) {
		this.getter = new VariModRowGetter(aaf);
		initComponents();
	}
	
	public ModInfoPanel2(VariModRowGetter getter) {
		this.getter = getter;
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJLabel0(), new Constraints(new Leading(21, 10, 10), new Leading(20, 10, 10)));
		add(getJScrollPane0(), new Constraints(new Bilateral(20, 20, 370), new Leading(50, 180, 10, 10)));
		add(getJButtonSelect(), new Constraints(new Leading(150, 10, 10), new Leading(240, 88, 242)));
		add(getJButtonDispose(), new Constraints(new Leading(280, 10, 10), new Leading(240, 88, 242)));
		add(getJCheckBoxAll(), new Constraints(new Leading(21, 10, 10), new Leading(240, 10, 10)));
		setSize(410, 280);
	}

	private JCheckBox getJCheckBoxAll() {
		if (jCheckBoxAll == null) {
			jCheckBoxAll = new JCheckBox();
			jCheckBoxAll.setSelected(true);
			jCheckBoxAll.setText("Select all");
			jCheckBoxAll.addActionListener(this);
		}
		return jCheckBoxAll;
	}

	protected JButton getJButtonDispose() {
		if (jButtonDispose == null) {
			jButtonDispose = new JButton();
			jButtonDispose.setText("Dispose");
			jButtonDispose.addActionListener(this);
		}
		return jButtonDispose;
	}

	protected JButton getJButtonSelect() {
		if (jButtonSelect == null) {
			jButtonSelect = new JButton();
			jButtonSelect.setText("Select");
		}
		return jButtonSelect;
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
    		if(getter==null){
    			
    			jTable0 = new JTable();
    			jTable0.setModel(new DefaultTableModel(new String[][]{{}, {}, }, VariModRowGetter.getTitle()));
    			jTable0.getModel().addTableModelListener(this);
    			
    		}else{
    			
    			jTable0 = new JTable();
    			ModModel mm = new ModModel(getter);
    			mm.addTableModelListener(this);
    			jTable0.setModel(mm);
    		}  		
    	}
    	return jTable0;
    }
	
	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("<html>Variable modification information: </html>");
    	}
    	return jLabel0;
    }
	
	protected void addSelectListener(ActionListener listener){
		this.getJButtonSelect().addActionListener(listener);
	}
	
	protected void addDisposeListener(ActionListener listener){
		this.getJButtonDispose().addActionListener(listener);
	}
	
	protected ArrayList <IPeptideCriteria> getModFilters(){
		ArrayList <IPeptideCriteria> filterlist = new ArrayList <IPeptideCriteria>();
		TableModel model = jTable0.getModel();
		int rowcount = model.getRowCount();
		for(int i=0;i<rowcount;i++){
			boolean select = (Boolean) model.getValueAt(i, 0);
			String name = (String) model.getValueAt(i, 1);
			char symbol = (Character) model.getValueAt(i, 3);
			String site = ((ModSite) model.getValueAt(i, 4)).toString();
			
			if(select){
				
				if(name.equals("N-Glyco")){
					
					NGlycoPepCriteria filter = new NGlycoPepCriteria(true);
					filterlist.add(filter);
					
				}else{
					DefaultVariModPepFilter filter = new DefaultVariModPepFilter(name, symbol, site);
					filterlist.add(filter);
				}
			}
		}
		if(filterlist.size()==0){
			filterlist.add(new NoVairModFilter());
		}
		return filterlist;
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
				frame.setTitle("ModInfoPanel2");
				ModInfoPanel2 content = new ModInfoPanel2();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub
		int row = e.getFirstRow();
		int column = e.getColumn();
		AbstractTableModel model = (AbstractTableModel) e.getSource();
		Object obj = model.getValueAt(row, column);

		if(obj.getClass() == Boolean.class){
			Boolean boo = (Boolean) obj;
			if(!boo){
				this.jCheckBoxAll.setSelected(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		Object obj = e.getSource();
		
		if(obj==this.jButtonDispose){
			
			TableModel model = jTable0.getModel();
			int rowcount = model.getRowCount();
			for(int i=0;i<rowcount;i++){
				model.setValueAt(Boolean.TRUE, i, 0);
			}
			this.jCheckBoxAll.setSelected(true);
			return;
		}
		
		if(obj==this.jCheckBoxAll){
			if(this.jCheckBoxAll.isSelected()){
				TableModel model = jTable0.getModel();
				int rowcount = model.getRowCount();
				for(int i=0;i<rowcount;i++){
					model.setValueAt(Boolean.TRUE, i, 0);
				}
			}else{
				TableModel model = jTable0.getModel();
				int rowcount = model.getRowCount();
				for(int i=0;i<rowcount;i++){
					model.setValueAt(Boolean.FALSE, i, 0);
				}
			}
			return;
		}
		
	}

	private class ModModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private String [] names;
		private Object [][] objs;
		
		public ModModel(VariModRowGetter rowGetter){
			this.names = rowGetter.getColumnNames();
			this.objs = rowGetter.getObject4InfoPanel2();
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
		
		@Override
		public Class getColumnClass(int c) {
			if(objs.length>0){
				return getValueAt(0, c).getClass();
			}
			else
				return String.class;
	    }
		
		public boolean isCellEditable(int row, int col) {
			Class c = this.getColumnClass(col);
			if(c==Boolean.class){
				return true;
			}
			return false;
		}
		
		public void setValueAt(Object value, int row, int col) {   
	        objs[row][col] = value;   
	        fireTableCellUpdated(row, col);   
	    }
		
		
	}

	
}
