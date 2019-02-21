/* 
 ******************************************************************************
 * File: PagedTable.java * * * Created on 04-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.beans.gui;


import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import javax.swing.JFormattedTextField;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Trailing;

/**
 * The table which can limit the number of rows in a page
 * 
 * @author Xinning
 * @version 0.1, 04-10-2009, 15:28:14
 */
public class SelectablePagedTable extends JPanel implements KeyListener {

	private static final long serialVersionUID = 1L;
	private IPagedTableRowGettor<?> rowGettor;

	private SelectPagePanel selectPagePanel0;
	private JFormattedTextField jFormattedTextFieldMaxRecords;
	private JLabel jLabel0;
	private PagedTable pagedTable0;
	private JScrollPane jScrollPane0;
	public SelectablePagedTable() {
		initComponents();
	}

	public SelectablePagedTable(IPagedTableRowGettor<?> rowGettor) {
		this.rowGettor = rowGettor;
		initComponents();
	}

	private void initComponents() {
    	setMinimumSize(new Dimension(300, 200));
    	setPreferredSize(new Dimension(300, 200));
    	setLayout(new GroupLayout());
    	add(getSelectPagePanel0(), new Constraints(new Bilateral(220, 220, 161), new Trailing(4, 28, 79, 456)));
    	add(getJLabel0(), new Constraints(new Trailing(86, 387, 382), new Trailing(8, 79, 456)));
    	add(getJFormattedTextFieldMaxRecords(), new Constraints(new Trailing(39, 35, 387, 382), new Trailing(4, 23, 79, 456)));
    	add(getJScrollPane0(), new Constraints(new Bilateral(0, 0, 25), new Bilateral(0, 33, 29)));
    	setSize(687, 416);
    }

	private JScrollPane getJScrollPane0() {
    	if (jScrollPane0 == null) {
    		jScrollPane0 = new JScrollPane();
    		jScrollPane0.setViewportView(getPagedTable0());
    	}
    	return jScrollPane0;
    }

	private PagedTable getPagedTable0() {
    	if (pagedTable0 == null) {
    		pagedTable0 = new PagedTable(this.rowGettor);
    	}
    	return pagedTable0;
    }

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Records per page");
			jLabel0.setToolTipText("Max number of records per page");
		}
		return jLabel0;
	}

	private JFormattedTextField getJFormattedTextFieldMaxRecords() {
		if (jFormattedTextFieldMaxRecords == null) {
			jFormattedTextFieldMaxRecords = new JFormattedTextField(
			        NumberFormat.getIntegerInstance());
			jFormattedTextFieldMaxRecords
			        .setToolTipText("Max number of records per page");

			jFormattedTextFieldMaxRecords.addKeyListener(this);
			
			if (this.rowGettor != null) {
				jFormattedTextFieldMaxRecords.setText(String
				        .valueOf(this.rowGettor.getMaxRecordsperPage()));
			}
		}
		return jFormattedTextFieldMaxRecords;
	}

	private SelectPagePanel getSelectPagePanel0() {
		if (selectPagePanel0 == null) {
			selectPagePanel0 = new SelectPagePanel(this.getPagedTable0());
		}
		return selectPagePanel0;
	}

	/**
	 * Select the specific page
	 * 
	 * @param pageIdx
	 */
	public void selectPage(int pageIdx) {
		this.getSelectPagePanel0().select(pageIdx);
	}
	
	/**
	 * If the settings are changed in the row getter, use this method to update
	 * the table. The page number will be changed to 1.
	 */
	public void refresh() {
		this.selectPage(0);
	}
	
	public void reLoad(IPagedTableRowGettor<?> rowGettor){
		this.rowGettor = rowGettor;
		initComponents();
	}
	
	/**
	 * Add list selection listener
	 * 
	 * @param listener
	 */
	public void addListSelectionListener(ListSelectionListener listener) {
		this.getPagedTable0().addListSelectionListener(listener);
	}
	
	
	/**
	 * Remove list selection listener
	 * 
	 * @param listener
	 */
	public void removeListSelectionListener(ListSelectionListener listener) {
		this.getPagedTable0().removeListSelectionListener(listener);
	}

	public Object getObject(){
		int idx = this.getPagedTable0().getSelectedRow();
		return getObject(idx);
	}
	
	public Object getObject(int idx){
		if(idx>=0){
			Object obj = this.rowGettor.getRow(idx);
			return obj;
		}else
			return null;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {

		char typed = e.getKeyChar();
		Object obj = e.getSource();
		
		if (obj == this.jFormattedTextFieldMaxRecords) {
			if (typed == '\n') {
				this.jFormattedTextFieldMaxRecords.requestFocus(false);

				int max = Integer.parseInt(this.jFormattedTextFieldMaxRecords
				        .getText());
				if (max != this.rowGettor.getMaxRecordsperPage()) {
					this.rowGettor.setMaxRecordsperPage(max);
					this.selectPagePanel0.select(0);
				}
			}
		}

	}
}
