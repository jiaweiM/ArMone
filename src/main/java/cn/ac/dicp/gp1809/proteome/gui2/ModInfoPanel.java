/* 
 ******************************************************************************
 * File:ModInfoPanel.java * * * Created on 2011-9-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

//VS4E -- DO NOT REMOVE THIS LINE!
public class ModInfoPanel extends JPanel implements TableModelListener {

	private static final long serialVersionUID = 1L;
	private JTable jTable0;
	private JScrollPane jScrollPane0;
	private JLabel jLabel0;
	private VariModRowGetter getter;
	private JTextField jTextFieldName;
	private JTextField jTextFieldMass;
	private JTextField jTextFieldSymbol;
	private JTextField jTextFieldSite;

	public ModInfoPanel() {
		initComponents();
	}
	
	public ModInfoPanel(AminoacidModification aaf) {
		this.getter = new VariModRowGetter(aaf);
		initComponents();
	}
	
	public ModInfoPanel(ModInfo[] mods) {
		this.getter = new VariModRowGetter(mods);
		initComponents();
	}
	
	public ModInfoPanel(VariModRowGetter getter) {
		this.getter = getter;
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJLabel0(), new Constraints(new Leading(21, 10, 10), new Leading(20, 10, 10)));
		add(getJScrollPane0(), new Constraints(new Bilateral(20, 20, 370), new Bilateral(50, 50, 150, 10)));
		add(getJTextFieldName(), new Constraints(new Leading(20, 115, 10, 10), new Trailing(8, 10, 10)));
		add(getJTextFieldMass(), new Constraints(new Leading(155, 75, 10, 10), new Trailing(8, 10, 10)));
		add(getJTextFieldSymbol(), new Constraints(new Leading(250, 30, 10, 10), new Trailing(8, 10, 10)));
		add(getJTextFieldSite(), new Constraints(new Leading(300, 70, 10, 10), new Trailing(8, 10, 10)));
		setSize(410, 280);
	}

	private JTextField getJTextFieldSite() {
		if (jTextFieldSite == null) {
			jTextFieldSite = new JTextField();
			jTextFieldSite.setText("");
			jTextFieldSite.setEditable(false);
		}
		return jTextFieldSite;
	}

	private JTextField getJTextFieldSymbol() {
		if (jTextFieldSymbol == null) {
			jTextFieldSymbol = new JTextField();
			jTextFieldSymbol.setText("");
			jTextFieldSymbol.setEditable(false);
		}
		return jTextFieldSymbol;
	}

	private JTextField getJTextFieldMass() {
		if (jTextFieldMass == null) {
			jTextFieldMass = new JTextField();
			jTextFieldMass.setText("");
			jTextFieldMass.setEditable(false);
		}
		return jTextFieldMass;
	}

	private JTextField getJTextFieldName() {
		if (jTextFieldName == null) {
			jTextFieldName = new JTextField();
			jTextFieldName.setText("");
			jTextFieldName.setEditable(false);
		}
		return jTextFieldName;
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
	
	public String getModName(){
		return this.jTextFieldName.getText();
	}

	public char getSymbol(){

		char symbol = '\u0000';
		String symstr = this.jTextFieldSymbol.getText();
		if(symstr!=null && symstr.length()>0){
			symbol = symstr.charAt(0);
		}
		return symbol;
	}
	
	/**
	 * Only modifications on an aminoacid are added, modifications on peptide terms are not considered.
	 * @return
	 */
	public String getSites(){
		
		String sss = this.jTextFieldSite.getText();
		if(sss!=null && sss.length()>0){
			String [] strs = sss.split(";");
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<strs.length;i++){
				if(strs[i].length()==1){
					sb.append(strs[i]);
				}
			}
			return sb.toString();
		}else{
			return null;
		}
	}
	
	public ModInfo [] getMods(){
		
		ArrayList <ModInfo> infolist = new ArrayList <ModInfo>();
		TableModel model = jTable0.getModel();
		int rowcount = model.getRowCount();
		for(int i=0;i<rowcount;i++){
			boolean select = (Boolean) model.getValueAt(i, 0);
			String name = (String) model.getValueAt(i, 1);
			double mass = (Double) model.getValueAt(i, 2);
			char symbol = (Character) model.getValueAt(i, 3);
			ModSite site = (ModSite) model.getValueAt(i, 4);
			
			if(select){
				ModInfo info = new ModInfo(name, mass, symbol, site);
				infolist.add(info);
			}
		}
		return infolist.toArray(new ModInfo[infolist.size()]);
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
			if(boo){
				String prename = this.jTextFieldName.getText();
				String presym = this.jTextFieldSymbol.getText();
				String premodsite = this.jTextFieldSite.getText();
				
				String symbol = String.valueOf(model.getValueAt(row, 3));
				String site = String.valueOf(model.getValueAt(row, 4));
				String name = String.valueOf(model.getValueAt(row, 1));
				if(presym.length()>0){
					if(presym.equals(symbol)){
						
						if(prename.equals("N-Glyco") || name.equals("N-Glyco")){
							
							int rowCount = model.getRowCount();
							for(int i=0;i<rowCount;i++){
								if(i!=row){
									model.setValueAt(Boolean.FALSE, i, 0);
								}
							}
							
							this.jTextFieldName.setText(String.valueOf(model.getValueAt(row, 1)));
							this.jTextFieldMass.setText(String.valueOf(model.getValueAt(row, 2)));
							this.jTextFieldSymbol.setText(String.valueOf(model.getValueAt(row, 3)));
							this.jTextFieldSite.setText(String.valueOf(model.getValueAt(row, 4)));
							
						}else{
							premodsite += ";"+site;
							this.jTextFieldSite.setText(premodsite);
						}

					}else{
						
						int rowCount = model.getRowCount();
						for(int i=0;i<rowCount;i++){
							if(i!=row){
								model.setValueAt(Boolean.FALSE, i, 0);
							}
						}
						
						this.jTextFieldName.setText(String.valueOf(model.getValueAt(row, 1)));
						this.jTextFieldMass.setText(String.valueOf(model.getValueAt(row, 2)));
						this.jTextFieldSymbol.setText(String.valueOf(model.getValueAt(row, 3)));
						this.jTextFieldSite.setText(String.valueOf(model.getValueAt(row, 4)));
					}
				}else{
					this.jTextFieldName.setText(String.valueOf(model.getValueAt(row, 1)));
					this.jTextFieldMass.setText(String.valueOf(model.getValueAt(row, 2)));
					this.jTextFieldSymbol.setText(String.valueOf(model.getValueAt(row, 3)));
					this.jTextFieldSite.setText(String.valueOf(model.getValueAt(row, 4)));
				}
				
			}else{
				
				int rowCount = model.getRowCount();
				String name = "";
				String mass = "";
				String symbol = "";
				String modsite = "";
				
				for(int i=0;i<rowCount;i++){
					Boolean select = (Boolean) model.getValueAt(i, 0);
					if(select){
						name = String.valueOf(model.getValueAt(i, 1));
						mass = String.valueOf(model.getValueAt(i, 2));
						symbol = String.valueOf(model.getValueAt(i, 3));
						modsite += String.valueOf(model.getValueAt(i, 4))+";";
					}
				}
				
				this.jTextFieldName.setText(name);
				this.jTextFieldMass.setText(mass);
				this.jTextFieldSymbol.setText(symbol);
				if(modsite.length()==0){
					this.jTextFieldSite.setText(modsite);
				}else{
					this.jTextFieldSite.setText(modsite.substring(0, modsite.length()-1));
				}
			}
		}
	}
	
	private class ModModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private String [] names;
		private Object [][] objs;
		
		public ModModel(VariModRowGetter rowGetter){
			this.names = rowGetter.getColumnNames();
			this.objs = rowGetter.getObject4Table();
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
		public Class <?> getColumnClass(int c) {
			if(objs.length>0){
				return getValueAt(0, c).getClass();
			}
			else
				return String.class;
	    }
		
		public boolean isCellEditable(int row, int col) {
			Class <?> c = this.getColumnClass(col);
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
