/*
 ******************************************************************************
 * File: DeltaMZCriteriaPanel.java * * * Created on 09-13-2010
 *
 * Copyright (c) 2010 Xinning Jiang (vext@163.com)
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
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultDeltaMZCriteria;
import javax.swing.GroupLayout.Alignment;

/**
 * Criteria panel for filtering the peptides by delta M/Z
 * 
 * @author Xinning
 * @version 0.1, 09-13-2010, 16:53:33
 */
public class DeltaMZCriteriaPanel extends JPanel implements
        ICriteriaSetter<IPeptide> {

	private static final long serialVersionUID = 1L;
	private JFormattedTextField jFormattedTextFieldIonscore;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public DeltaMZCriteriaPanel() {
		initComponents();
	}

	private void initComponents() {
    	setBorder(BorderFactory.createTitledBorder(null, "Setting delta m/z filter", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD,
    			12), new Color(59, 59, 59)));
    	setMinimumSize(new Dimension(391, 145));
    	setPreferredSize(new Dimension(391, 145));
    	setSize(585, 145);
    	javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJLabel0())
    				.addGap(18)
    				.addComponent(getJFormattedTextFieldIonscore(), javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
    				.addGap(2)
    				.addComponent(getJLabel1(), javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(9)
    				.addComponent(getJLabel0()))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(4)
    				.addComponent(getJFormattedTextFieldIonscore(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(7)
    				.addComponent(getJLabel1()))
    	);
    	setLayout(groupLayout);
    }

	private JLabel getJLabel1() {
    	if (jLabel1 == null) {
    		jLabel1 = new JLabel();
    		jLabel1.setText("ppm");
    	}
    	return jLabel1;
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("M/Z <=");
    	}
    	return jLabel0;
    }

	private JFormattedTextField getJFormattedTextFieldIonscore() {
    	if (jFormattedTextFieldIonscore == null) {
    		jFormattedTextFieldIonscore = new JFormattedTextField();
    		jFormattedTextFieldIonscore.setHorizontalAlignment(SwingConstants.TRAILING);
    		jFormattedTextFieldIonscore.setText("5");
    		jFormattedTextFieldIonscore.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldIonscore.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldIonscore;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.gui.ICriteriaSetter#getCriteria()
	 */
	@Override
    public IPeptideCriteria<IPeptide> getCriteria() {
	    return new DefaultDeltaMZCriteria(Double.parseDouble(this.jFormattedTextFieldIonscore.getText()));
    }
}
