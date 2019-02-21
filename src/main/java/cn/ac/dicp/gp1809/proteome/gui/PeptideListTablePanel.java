/* 
 ******************************************************************************
 * File: PeptideListTable.java * * * Created on 04-09-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionListener;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.util.beans.gui.SelectablePagedTable;

/**
 * The peptide list table panel
 * 
 * @author Xinning
 * @version 0.1, 04-09-2009, 20:59:54
 */
public class PeptideListTablePanel extends JPanel implements ItemListener {

	private static final long serialVersionUID = 1L;
	private PeptideListPagedRowGettor getter;
	private SelectablePagedTable selectablePagedTable0;
	private PeptideStatFrame peptideStatFrame;
	private JPanel jPanel0;
	private JCheckBox jCheckBoxShowInfo;
	private JCheckBox jCheckBoxSelectAll;
	private JCheckBox jCheckBoxDisselectAll;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PeptideListTablePanel() {
		initComponents();
	}

	public PeptideListTablePanel(PeptideListPagedRowGettor getter) {
		this.getter = getter;
		initComponents();
	}

	private void initComponents() {
    	setBorder(BorderFactory.createCompoundBorder(null, null));
    	setMinimumSize(new Dimension(300, 100));
    	setPreferredSize(new Dimension(300, 100));
    	setLayout(new GroupLayout());
    	add(getSelectablePagedTable0(), new Constraints(new Bilateral(6, 6, 781), new Bilateral(0, 47, 80)));
    	add(getJPanel0(), new Constraints(new Bilateral(0, 10, 612), new Trailing(2, 33, 133, 133)));
    	setSize(821, 456);
    }

	private JCheckBox getJCheckBoxDisselectAll() {
		if (jCheckBoxDisselectAll == null) {
			jCheckBoxDisselectAll = new JCheckBox();
			jCheckBoxDisselectAll.setText("Disselect all displayed peptides");
			jCheckBoxDisselectAll.addItemListener(this);
		}
		return jCheckBoxDisselectAll;
	}

	private JCheckBox getJCheckBoxSelectAll() {
		if (jCheckBoxSelectAll == null) {
			jCheckBoxSelectAll = new JCheckBox();
			jCheckBoxSelectAll.setText("Select all displayed peptides");
			jCheckBoxSelectAll.addItemListener(this);
		}
		return jCheckBoxSelectAll;
	}

	private JCheckBox getJCheckBoxShowInfo() {
    	if (jCheckBoxShowInfo == null) {
    		jCheckBoxShowInfo = new JCheckBox();
    		jCheckBoxShowInfo.setText("Show displayed peptide information");
    		jCheckBoxShowInfo.addItemListener(this);
    	}
    	return jCheckBoxShowInfo;
    }

	private PeptideStatFrame getPeptideStatFrame() {
		if (peptideStatFrame == null) {
			peptideStatFrame = new PeptideStatFrame();
//			peptideStatFrame
//			        .setDefaultCloseOperation(PepListInfoFrame.DO_NOTHING_ON_CLOSE);
			peptideStatFrame.getContentPane().setPreferredSize(
			        peptideStatFrame.getSize());
			peptideStatFrame.setLocationRelativeTo(this);
			peptideStatFrame.setAlwaysOnTop(true);
		}
		return peptideStatFrame;
	}

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.setLayout(new GroupLayout());
    		jPanel0.add(getJCheckBoxShowInfo(), new Constraints(new Leading(6, 6, 6), new Leading(11, 6, 6)));
    		jPanel0.add(getJCheckBoxSelectAll(), new Constraints(new Leading(316, 10, 10), new Leading(11, 6, 6)));
    		jPanel0.add(getJCheckBoxDisselectAll(), new Constraints(new Leading(604, 10, 10), new Leading(11, 6, 6)));
    	}
    	return jPanel0;
    }

	private SelectablePagedTable getSelectablePagedTable0() {
		if (selectablePagedTable0 == null) {
			selectablePagedTable0 = new SelectablePagedTable(this.getter);
			selectablePagedTable0.setBorder(new LineBorder(Color.lightGray, 1,
			        false));
			selectablePagedTable0.setMinimumSize(new Dimension(200, 80));
			selectablePagedTable0.setPreferredSize(new Dimension(200, 80));
		}
		return selectablePagedTable0;
	}

	public PeptideRowObject getRowObject(){
		return (PeptideRowObject) this.getSelectablePagedTable0().getObject();
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
	 * Filter the peptides in the table
	 * 
	 * @param filter
	 */
	public void addFilter(IPeptideCriteria filter) {
		if (this.getter != null) {
			this.getter.addFilter(filter);
			this.selectablePagedTable0.refresh();
		}
	}

	/**
	 * Remove a filter
	 * 
	 * @param filter
	 */
	public void removeFilter(IPeptideCriteria filter) {
		if (this.getter != null) {
			this.getter.removeFilter(filter);
			this.selectablePagedTable0.refresh();
		}
	}

	/**
	 * Add a listener when a peptide is selected
	 * 
	 * @param listener
	 */
	public void addPeptideSelectionListener(ListSelectionListener listener) {
		this.getSelectablePagedTable0().addListSelectionListener(listener);
	}

	/**
	 * remove the peptide selection listener
	 * 
	 * @param listener
	 */
	public void removePeptideSelectionListener(ListSelectionListener listener) {
		this.getSelectablePagedTable0().removeListSelectionListener(listener);
	}
	
	
	/**
	 * Clean all the related frames and windows when close
	 */
	void forceClean() {
		if(this.peptideStatFrame != null) {
			this.peptideStatFrame.dispose();
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getSource();

		if (obj == this.getJCheckBoxShowInfo()) {
			if (this.getJCheckBoxShowInfo().isSelected()) {
				if (this.getter != null)
					this.getPeptideStatFrame().loadPeptideStatInfo(
					        this.getter.getPeptideStatInfo());
				this.getPeptideStatFrame().setVisible(true);
			} else {
				this.getPeptideStatFrame().setVisible(false);
			}

			return;
		}

		if (obj == this.getJCheckBoxSelectAll()) {
			if (this.getJCheckBoxSelectAll().isSelected()) {

				int option = JOptionPane
				        .showConfirmDialog(
				                this,
				                "This will select all peptides and miss all the manual validation information.\nContinue?",
				                "Select all", JOptionPane.WARNING_MESSAGE);

				if (option == JOptionPane.YES_OPTION) {
					if (this.getter != null)
						this.getter.selectAllDisplayedPeptides();
				}

				this.getJCheckBoxSelectAll().setSelected(false);
				this.getSelectablePagedTable0().repaint();
			}

			return;
		}

		if (obj == this.getJCheckBoxDisselectAll()) {
			if (this.getJCheckBoxDisselectAll().isSelected()) {

				int option = JOptionPane
				        .showConfirmDialog(
				                this,
				                "This will disselect all peptides and miss all the manual validation information.\nContinue?",
				                "Disselect all", JOptionPane.WARNING_MESSAGE);

				if (option == JOptionPane.YES_OPTION) {
					if (this.getter != null)
						this.getter.disSelectAllDisplayedPeptides();
				}

				this.getJCheckBoxDisselectAll().setSelected(false);
				this.getSelectablePagedTable0().repaint();
			}

			return;
		}
	}

	/**
	 * Main entry of the class. Note: This class is only created so that you can
	 * easily preview the result at runtime. It is not expected to be managed by
	 * the designer. You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("PeptideListTable");
				PeptideListTablePanel content = new PeptideListTablePanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
