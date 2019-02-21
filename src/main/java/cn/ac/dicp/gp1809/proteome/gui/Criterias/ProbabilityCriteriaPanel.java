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

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultProbabilityCriteria;
import javax.swing.GroupLayout.Alignment;

/**
 * Criteria panel for filtering of sequest peptides
 * 
 * @author Xinning
 * @version 0.1, 08-08-2009, 12:28:11
 */
public class ProbabilityCriteriaPanel extends JPanel implements
        ICriteriaSetter<IPeptide> {

	private static final long serialVersionUID = 1L;
	private JFormattedTextField jFormattedTextFieldIonscore;
	private JLabel jLabel0;
	public ProbabilityCriteriaPanel() {
		initComponents();
	}

	private void initComponents() {
    	setBorder(BorderFactory.createTitledBorder(null, "Filter by probability", TitledBorder.LEADING, TitledBorder.ABOVE_TOP,
    			new Font("SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
    	setMinimumSize(new Dimension(391, 145));
    	setPreferredSize(new Dimension(391, 145));
    	setSize(585, 145);
    	javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJLabel0())
    				.addGap(16)
    				.addComponent(getJFormattedTextFieldIonscore(), javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(9)
    				.addComponent(getJLabel0()))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJFormattedTextFieldIonscore(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	setLayout(groupLayout);
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("Mininum probability");
    	}
    	return jLabel0;
    }

	private JFormattedTextField getJFormattedTextFieldIonscore() {
		if (jFormattedTextFieldIonscore == null) {
			jFormattedTextFieldIonscore = new JFormattedTextField(new Float(0.9f));
			jFormattedTextFieldIonscore.setMinimumSize(new Dimension(40, 25));
			jFormattedTextFieldIonscore.setPreferredSize(new Dimension(40, 25));
		}
		return jFormattedTextFieldIonscore;
	}

	@Override
    public IPeptideCriteria<IPeptide> getCriteria() {
	    return new DefaultProbabilityCriteria(Float.parseFloat(this.jFormattedTextFieldIonscore.getText()));
    }
}
