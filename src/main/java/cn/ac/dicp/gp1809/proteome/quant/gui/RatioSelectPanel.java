/* 
 ******************************************************************************
 * File: RatioSelectPanel.java * * * Created on 2011-10-26
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.gui;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import cn.ac.dicp.gp1809.util.DecimalFormats;
import javax.swing.GroupLayout.Alignment;

public class RatioSelectPanel extends JPanel implements TableModelListener {

	private JLabel jLabel0;
	private JTable jTable0;
	private JScrollPane jScrollPane0;
	private Object [][] objs;
	private boolean change = false;
	
	private static final long serialVersionUID = 1L;
	private JCheckBox jCheckBox0;

	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";

	public RatioSelectPanel() {
		initComponents();
	}
	
	public RatioSelectPanel(Object [][] objs) {
		this.objs = objs;
		initComponents();
	}

	private void initComponents() {
		setSize(410, 480);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(21)
					.addComponent(getJLabel0()))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(20)
					.addComponent(getJScrollPane0(), javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(21)
					.addComponent(getJCheckBox0()))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(20)
					.addComponent(getJLabel0())
					.addGap(12)
					.addComponent(getJScrollPane0(), javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(getJCheckBox0()))
		);
		setLayout(groupLayout);
	}

	protected JCheckBox getJCheckBox0() {
		if (jCheckBox0 == null) {
			jCheckBox0 = new JCheckBox();
			jCheckBox0.setText("Normalize");
			jCheckBox0.setSelected(true);
		}
		return jCheckBox0;
	}

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("<html>Output ratios & Sample volume correction</html>");
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
    		if(objs==null){
    			
    			jTable0 = new JTable();
    			jTable0.setModel(new DefaultTableModel(new String[][]{{}, {}, }, 
    					new String[]{"Select", "Output", "Theoretical ratio"}));
    			
    			jTable0.getColumnModel().getColumn(0).setPreferredWidth(70);
    			jTable0.getColumnModel().getColumn(1).setPreferredWidth(70);
    			jTable0.getColumnModel().getColumn(2).setPreferredWidth(160);
    			
    			jTable0.getModel().addTableModelListener(this);
    			
    		}else{
    			
    			jTable0 = new JTable();
    			RatioSelectModel mm = new RatioSelectModel(objs);
    			mm.addTableModelListener(this);
    			jTable0.setModel(mm);
    			jTable0.getColumnModel().getColumn(0).setPreferredWidth(70);
    			jTable0.getColumnModel().getColumn(1).setPreferredWidth(70);
    			jTable0.getColumnModel().getColumn(2).setPreferredWidth(160);
    			
    			DefaultTableCellRenderer render = new DefaultTableCellRenderer();
    			render.setHorizontalAlignment(SwingConstants.CENTER);
    			jTable0.getColumn("Theoretical ratio").setCellRenderer(render);
    		}  		
    	}
    	return jTable0;
    }

	public int [] getSelect(){
		ArrayList <Integer> list = new ArrayList <Integer>();
		TableModel model = this.jTable0.getModel();
		int rowcount = model.getRowCount();
		for(int i=0;i<rowcount;i++){
			boolean select = (Boolean) model.getValueAt(i, 0);
			if(select){
				list.add(i);
			}
		}
		int [] select = new int[list.size()];
		for(int i=0;i<select.length;i++){
			select[i] = list.get(i);
		}
		return select;
	}
	
	public double [] getTheRatio(){
		ArrayList <Double> list = new ArrayList <Double>();
		TableModel model = this.jTable0.getModel();
		int rowcount = model.getRowCount();
		for(int i=0;i<rowcount;i++){
			if(i%2==0){
				list.add((Double) model.getValueAt(i, 2));
			}
		}
		double [] ratio = new double[list.size()];
		for(int i=0;i<ratio.length;i++){
			ratio[i] = list.get(i);
		}
		return ratio;
	}
	
	public double [] getUsedTheRatio(){
		ArrayList <Double> list = new ArrayList <Double>();
		TableModel model = this.jTable0.getModel();
		int rowcount = model.getRowCount();
		for(int i=0;i<rowcount;i++){
			boolean select = (Boolean) model.getValueAt(i, 0);
			if(select){
				list.add((Double) model.getValueAt(i, 2));
			}
		}
		double [] ratio = new double[list.size()];
		for(int i=0;i<ratio.length;i++){
			ratio[i] = list.get(i);
		}
		return ratio;
	}
	
	public String [] getRatioNames(){
		
		ArrayList <String> list = new ArrayList <String>();
		TableModel model = this.jTable0.getModel();
		int rowcount = model.getRowCount();
		for(int i=0;i<rowcount;i++){
			boolean select = (Boolean) model.getValueAt(i, 0);
			if(select){
				list.add((String) model.getValueAt(i, 1));
			}
		}
	
		return list.toArray(new String[list.size()]);
	}
	
	public boolean isNormal(){
		return this.jCheckBox0.isSelected();
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
				frame.setTitle("RatioSelectPanel");
				RatioSelectPanel content = new RatioSelectPanel();
				content.setPreferredSize(content.getSize());
				frame.getContentPane().add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
	
	public class RatioSelectModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;
		private String [] names;
		private Object [][] objs;
		
		private RatioSelectModel(Object [][] objs){
			this.names = new String[]{"Select", "Output", "Theoretical ratio"};
			this.objs = objs;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			return names.length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			return objs.length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return objs[rowIndex][columnIndex];
		}
		
		public String getColumnName(int col) {   
	        return names[col];   
	    } 
		
		@Override
		public Class <?> getColumnClass(int c) {
			if(objs.length>0){
				return getValueAt(0, c).getClass();
			}
			else
				return String.class;
	    }
		
		public boolean isCellEditable(int row, int col) {
			if(col==1)
				return false;
			
			return true;
		}
		
		public void setValueAt(Object value, int row, int col) {   
	        objs[row][col] = value;   
	        fireTableCellUpdated(row, col);   
	    }

	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		
		if(change){
			change = false;
			return;
		}
		
		DecimalFormat df3 = DecimalFormats.DF0_3;
		
		int firstRow = e.getFirstRow();
		int column = e.getColumn();
		AbstractTableModel model = (AbstractTableModel) e.getSource();
		
		if(e.getType() == TableModelEvent.UPDATE){
			
			Object obj = model.getValueAt(firstRow, column);

			if(column==2){
				
				if(obj!=null){
					
					double ratio = (Double) obj;

					double dr = 0;
					if(ratio!=0)
						dr = 1.0/ratio;
					
					double r1 = Double.parseDouble(df3.format(dr));
					
					this.change = true;
					
					if(firstRow%2==0){
						model.setValueAt(r1, firstRow+1, column);
					}else{
						model.setValueAt(r1, firstRow-1, column);
					}
				}
			}
		}
	}

}
