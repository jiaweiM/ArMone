/* 
 ******************************************************************************
 * File: SequestApivaseChargedCriteriaPanel.java * * * Created on 06-30-2009
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
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.ISequestPhosphoPeptidePair;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.phosphorylation.DefaultSequestPhosPairChargedCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.phosphorylation.IPhosPeptidePairCriteria;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Criteria panel for filtering of sequest apivase peptides for different charge states
 * 
 * @author Xinning
 * @version 0.1, 06-30-2009, 14:52:33
 */
public class SequestApivaseChargedCriteriaPanel extends JPanel implements
        ICriteriaSetter<ISequestPhosphoPeptidePair> {

	private static final long serialVersionUID = 1L;
	private JFormattedTextField jFormattedTextFieldXcorr1;
	private JLabel jLabel2;
	private JFormattedTextField jFormattedTextFieldXcorr3;
	private JLabel jLabel1;
	private JFormattedTextField jFormattedTextFieldXcorr2;
	private JLabel jLabel0;
	private JLabel jLabel3;
	private JFormattedTextField jFormattedTextFieldDcn1;
	private JLabel jLabelXcorr4;
	private JFormattedTextField jFormattedTextFieldXcorr4;
	public SequestApivaseChargedCriteriaPanel() {
		initComponents();
	}

	private void initComponents() {
    	setBorder(BorderFactory.createTitledBorder(null, "SEQUEST filters (MS2/MS3 target-decoy strategy)", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
    			new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
    	setMinimumSize(new Dimension(535, 145));
    	setPreferredSize(new Dimension(535, 145));
    	setSize(585, 145);
    	javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(22)
    				.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addComponent(getJLabel0())
    						.addGap(16)
    						.addComponent(getJFormattedTextFieldXcorr1(), javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
    					.addGroup(groupLayout.createSequentialGroup()
    						.addComponent(getJLabelXcorr4())
    						.addGap(17)
    						.addComponent(getJFormattedTextFieldXcorr4(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
    				.addGap(24)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addComponent(getJLabel3())
    						.addGap(12)
    						.addComponent(getJFormattedTextFieldDcn1(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    					.addGroup(groupLayout.createSequentialGroup()
    						.addComponent(getJLabel1())
    						.addGap(13)
    						.addComponent(getJFormattedTextFieldXcorr2(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    						.addGap(27)
    						.addComponent(getJLabel2())
    						.addGap(17)
    						.addComponent(getJFormattedTextFieldXcorr3(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
    				.addGap(253))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(5)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(3)
    						.addComponent(getJLabel0()))
    					.addComponent(getJFormattedTextFieldXcorr1(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(3)
    						.addComponent(getJLabel1()))
    					.addComponent(getJFormattedTextFieldXcorr2(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(2)
    						.addComponent(getJLabel2(), javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
    					.addComponent(getJFormattedTextFieldXcorr3(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    				.addPreferredGap(ComponentPlacement.UNRELATED)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(3)
    						.addComponent(getJLabel3()))
    					.addComponent(getJFormattedTextFieldDcn1(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(2)
    						.addComponent(getJLabelXcorr4(), javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
    					.addComponent(getJFormattedTextFieldXcorr4(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    				.addGap(31))
    	);
    	setLayout(groupLayout);
    }

	private JFormattedTextField getJFormattedTextFieldXcorr4() {
    	if (jFormattedTextFieldXcorr4 == null) {
    		jFormattedTextFieldXcorr4 = new JFormattedTextField(new Float(0.00f));
    		jFormattedTextFieldXcorr4.setText("0.5");
    		jFormattedTextFieldXcorr4.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldXcorr4.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldXcorr4;
    }

	private JLabel getJLabelXcorr4() {
    	if (jLabelXcorr4 == null) {
    		jLabelXcorr4 = new JLabel();
    		jLabelXcorr4.setText("Xcorr's >4+");
    	}
    	return jLabelXcorr4;
    }

	private JFormattedTextField getJFormattedTextFieldDcn1() {
    	if (jFormattedTextFieldDcn1 == null) {
    		jFormattedTextFieldDcn1 = new JFormattedTextField(new Float(0.1f));
    		jFormattedTextFieldDcn1.setText("0.1");
    		jFormattedTextFieldDcn1.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldDcn1.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldDcn1;
    }

	private JLabel getJLabel3() {
    	if (jLabel3 == null) {
    		jLabel3 = new JLabel();
    		jLabel3.setText("DeltaCn'm 1+");
    	}
    	return jLabel3;
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("Xcorr's 1+");
    	}
    	return jLabel0;
    }

	private JFormattedTextField getJFormattedTextFieldXcorr2() {
    	if (jFormattedTextFieldXcorr2 == null) {
    		jFormattedTextFieldXcorr2 = new JFormattedTextField(new Float(0.00f));
    		jFormattedTextFieldXcorr2.setHorizontalAlignment(SwingConstants.LEFT);
    		jFormattedTextFieldXcorr2.setText("0.5");
    		jFormattedTextFieldXcorr2.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldXcorr2.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldXcorr2;
    }

	private JLabel getJLabel1() {
    	if (jLabel1 == null) {
    		jLabel1 = new JLabel();
    		jLabel1.setText("Xcorr's 2+");
    		jLabel1.setMinimumSize(new Dimension(45, 25));
    		jLabel1.setMaximumSize(new Dimension(45, 25));
    	}
    	return jLabel1;
    }

	private JFormattedTextField getJFormattedTextFieldXcorr3() {
    	if (jFormattedTextFieldXcorr3 == null) {
    		jFormattedTextFieldXcorr3 = new JFormattedTextField(new Float(0.00f));
    		jFormattedTextFieldXcorr3.setText("0.5");
    		jFormattedTextFieldXcorr3.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldXcorr3.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldXcorr3;
    }

	private JLabel getJLabel2() {
    	if (jLabel2 == null) {
    		jLabel2 = new JLabel();
    		jLabel2.setText("Xcorr's 3+");
    	}
    	return jLabel2;
    }

	private JFormattedTextField getJFormattedTextFieldXcorr1() {
    	if (jFormattedTextFieldXcorr1 == null) {
    		jFormattedTextFieldXcorr1 = new JFormattedTextField(new Float(0.00f));
    		jFormattedTextFieldXcorr1.setText("0.5");
    		jFormattedTextFieldXcorr1.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldXcorr1.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldXcorr1;
    }

	@Override
    public IPhosPeptidePairCriteria<ISequestPhosphoPeptidePair> getCriteria() {
		
		float[] xcorrs = new float[7];
		
		xcorrs[1] = Float.parseFloat(this.getJFormattedTextFieldXcorr1().getText());
		xcorrs[2] = Float.parseFloat(this.getJFormattedTextFieldXcorr2().getText());
		xcorrs[3] = Float.parseFloat(this.getJFormattedTextFieldXcorr3().getText());
		
		float xcorrb4 = Float.parseFloat(this.getJFormattedTextFieldXcorr4().getText());
		xcorrs[4] = xcorrb4;
		xcorrs[5] = xcorrb4;
		xcorrs[6] = xcorrb4;
		
		float deltaCns = Float.parseFloat(this.getJFormattedTextFieldDcn1().getText());
		
		return new DefaultSequestPhosPairChargedCriteria(xcorrs, deltaCns);
    }

}
