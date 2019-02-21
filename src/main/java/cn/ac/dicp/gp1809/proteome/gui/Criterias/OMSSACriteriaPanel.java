/* 
 ******************************************************************************
 * File: OMSSACriteriaPanel.java * * * Created on 04-09-2009
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
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.IOMSSAPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultOMSSACriteria;
import javax.swing.GroupLayout.Alignment;

/**
 * Criteria panel for filtering of OMSSA peptides
 * 
 * @author Xinning
 * @version 0.1, 04-09-2009, 21:16:45
 */
public class OMSSACriteriaPanel extends JPanel implements
        ICriteriaSetter<IOMSSAPeptide> {

	private static final long serialVersionUID = 1L;
	private JFormattedTextField jFormattedTextFieldEvalue;
	private JCheckBox jCheckBoxEvalue;
	private JCheckBox jCheckBoxPvalue;
	private JFormattedTextField jFormattedPvalue;
	public OMSSACriteriaPanel() {
		initComponents();
	}

	private void initComponents() {
    	setBorder(BorderFactory.createTitledBorder(null, "OMSSA filters", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12),
    			new Color(59, 59, 59)));
    	setMinimumSize(new Dimension(391, 145));
    	setPreferredSize(new Dimension(391, 145));
    	setSize(585, 145);
    	javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addComponent(getJCheckBoxEvalue())
    				.addGap(14)
    				.addComponent(getJFormattedTextFieldEvalue(), javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
    				.addGap(76)
    				.addComponent(getJCheckBoxPvalue())
    				.addGap(18)
    				.addComponent(getJFormattedPvalue(), javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(9)
    				.addComponent(getJCheckBoxEvalue()))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJFormattedTextFieldEvalue(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(9)
    				.addComponent(getJCheckBoxPvalue()))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJFormattedPvalue(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	setLayout(groupLayout);
    }

	private JFormattedTextField getJFormattedPvalue() {
    	if (jFormattedPvalue == null) {
    		jFormattedPvalue = new JFormattedTextField(new Float(0.1f));
    		jFormattedPvalue.setText("0.1");
    		jFormattedPvalue.setMinimumSize(new Dimension(40, 25));
    		jFormattedPvalue.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedPvalue;
    }

	private JCheckBox getJCheckBoxPvalue() {
    	if (jCheckBoxPvalue == null) {
    		jCheckBoxPvalue = new JCheckBox();
    		jCheckBoxPvalue.setText("P-value");
    	}
    	return jCheckBoxPvalue;
    }

	private JCheckBox getJCheckBoxEvalue() {
    	if (jCheckBoxEvalue == null) {
    		jCheckBoxEvalue = new JCheckBox();
    		jCheckBoxEvalue.setSelected(true);
    		jCheckBoxEvalue.setText("E-value");
    	}
    	return jCheckBoxEvalue;
    }

	private JFormattedTextField getJFormattedTextFieldEvalue() {
    	if (jFormattedTextFieldEvalue == null) {
    		jFormattedTextFieldEvalue = new JFormattedTextField(new Float(0.1f));
    		jFormattedTextFieldEvalue.setText("0.1");
    		jFormattedTextFieldEvalue.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldEvalue.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldEvalue;
    }

	@Override
    public IPeptideCriteria<IOMSSAPeptide> getCriteria() {
		
		double evalue = Double.MAX_VALUE;
		double pvalue = Double.MAX_VALUE;
		
		if(this.getJCheckBoxEvalue().isSelected()) {
			evalue = Double.parseDouble(this.getJFormattedTextFieldEvalue().getText());
		}
		
		if(this.getJCheckBoxPvalue().isSelected()) {
			pvalue = Double.parseDouble(this.getJFormattedPvalue().getText());
		}
		
	    return new DefaultOMSSACriteria(evalue, pvalue);
    }

}
