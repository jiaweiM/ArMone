/* 
 ******************************************************************************
 * File: SequestCriteriaPanel.java * * * Created on 04-09-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.ISequestPhosphoPeptidePair;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.phosphorylation.DefaultSequestPhosPairCriteria;
import javax.swing.GroupLayout.Alignment;

/**
 * Criteria panel for filtering of sequest peptides
 * 
 * @author Xinning
 * @version 0.1, 04-09-2009, 21:16:45
 */
public class SequestApivaseCriteriaPanel extends JPanel implements
        ICriteriaSetter<ISequestPhosphoPeptidePair> {

	private static final long serialVersionUID = 1L;
	private JFormattedTextField jFormattedTextFieldXcorr;
	private JLabel jLabel1;
	private JLabel jLabel0;
	private JPanel jPanel0;
	private JPanel jPanel1;
	private JFormattedTextField jFormattedTextFieldDcn;
	public SequestApivaseCriteriaPanel() {
		initComponents();
	}

	private void initComponents() {
    	setBorder(BorderFactory.createTitledBorder(null, "SEQUEST filters (MS2/MS3 target-decoy strategy)", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font(
    			"SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
    	setMinimumSize(new Dimension(535, 145));
    	setPreferredSize(new Dimension(535, 145));
    	setSize(585, 145);
    	javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJLabel0())
    				.addGap(14)
    				.addComponent(getJFormattedTextFieldXcorr(), javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
    				.addGap(78)
    				.addComponent(getJPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(191)
    				.addComponent(getJPanel1(), javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(9)
    						.addComponent(getJLabel0()))
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(6)
    						.addComponent(getJFormattedTextFieldXcorr(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    					.addComponent(getJPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
    				.addGap(2)
    				.addComponent(getJPanel1(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	setLayout(groupLayout);
    }

	private JFormattedTextField getJFormattedTextFieldDcn() {
    	if (jFormattedTextFieldDcn == null) {
    		jFormattedTextFieldDcn = new JFormattedTextField(new DecimalFormat("0.####"));
    		jFormattedTextFieldDcn.setText("0.1");
    		jFormattedTextFieldDcn.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldDcn.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldDcn;
    }

	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
		}
		return jPanel1;
	}

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.add(getJLabel1());
    		jPanel0.add(getJFormattedTextFieldDcn());
    	}
    	return jPanel0;
    }

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Xcorr's");
		}
		return jLabel0;
	}


	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("DeltaCn'm");
			jLabel1.setMinimumSize(new Dimension(45, 25));
			jLabel1.setMaximumSize(new Dimension(45, 25));
		}
		return jLabel1;
	}

	private JFormattedTextField getJFormattedTextFieldXcorr() {
		if (jFormattedTextFieldXcorr == null) {
			jFormattedTextFieldXcorr = new JFormattedTextField(new DecimalFormat("0.######"));
			jFormattedTextFieldXcorr.setText("0.6");
			jFormattedTextFieldXcorr.setMinimumSize(new Dimension(40, 25));
			jFormattedTextFieldXcorr.setPreferredSize(new Dimension(40, 25));
			jFormattedTextFieldXcorr.setAutoscrolls(true);
		}
		return jFormattedTextFieldXcorr;
	}

	@Override
	public IPeptideCriteria<ISequestPhosphoPeptidePair> getCriteria() {
		return new DefaultSequestPhosPairCriteria(Float.parseFloat(this
		        .getJFormattedTextFieldXcorr().getText()), Float.parseFloat(this
				        .getJFormattedTextFieldDcn().getText()));
	}

}
