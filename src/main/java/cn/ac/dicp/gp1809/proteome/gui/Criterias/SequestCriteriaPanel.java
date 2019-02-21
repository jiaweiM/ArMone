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
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultSequestCriteria;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Criteria panel for filtering of sequest peptides
 * 
 * @author Xinning
 * @version 0.1, 04-09-2009, 21:16:45
 */
public class SequestCriteriaPanel extends JPanel implements
        ICriteriaSetter<ISequestPeptide> {

	private static final long serialVersionUID = 1L;
	private JFormattedTextField jFormattedTextFieldXcorr1;
	private JLabel jLabel2;
	private JFormattedTextField jFormattedTextFieldXcorr3;
	private JLabel jLabel1;
	private JFormattedTextField jFormattedTextFieldXcorr2;
	private JLabel jLabel0;
	private JLabel jLabel3;
	private JFormattedTextField jFormattedTextFieldDcn1;
	private JLabel jLabel4;
	private JFormattedTextField jFormattedTextFieldDcn2;
	private JLabel jLabel5;
	private JFormattedTextField jFormattedTextFieldDcn3;
	private JLabel jLabelXcorr4;
	private JLabel jLabelDcn4;
	private JFormattedTextField jFormattedTextFieldXcorr4;
	private JFormattedTextField jFormattedTextFieldDcn4;
	public SequestCriteriaPanel() {
		initComponents();
	}

	private void initComponents() {
    	setBorder(BorderFactory.createTitledBorder(null, "SEQUEST filters", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
    			new Color(51, 51, 51)));
    	setMinimumSize(new Dimension(535, 145));
    	setPreferredSize(new Dimension(535, 145));
    	setSize(575, 145);
    	javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addContainerGap()
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addComponent(getJLabel0())
    						.addPreferredGap(ComponentPlacement.RELATED)
    						.addComponent(getJFormattedTextFieldXcorr1(), javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
    						.addGap(18)
    						.addComponent(getJLabel1())
    						.addPreferredGap(ComponentPlacement.UNRELATED)
    						.addComponent(getJFormattedTextFieldXcorr2(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    						.addGap(18)
    						.addComponent(getJLabel2())
    						.addPreferredGap(ComponentPlacement.UNRELATED)
    						.addComponent(getJFormattedTextFieldXcorr3(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    						.addGap(18)
    						.addComponent(getJLabelXcorr4())
    						.addPreferredGap(ComponentPlacement.UNRELATED)
    						.addComponent(getJFormattedTextFieldXcorr4(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    					.addGroup(groupLayout.createSequentialGroup()
    						.addComponent(getJLabel3())
    						.addPreferredGap(ComponentPlacement.RELATED)
    						.addComponent(getJFormattedTextFieldDcn1(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    						.addPreferredGap(ComponentPlacement.UNRELATED)
    						.addComponent(getJLabel4(), javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
    						.addPreferredGap(ComponentPlacement.UNRELATED)
    						.addComponent(getJFormattedTextFieldDcn2(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    						.addPreferredGap(ComponentPlacement.UNRELATED)
    						.addComponent(getJLabel5())
    						.addPreferredGap(ComponentPlacement.UNRELATED)
    						.addComponent(getJFormattedTextFieldDcn3(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    						.addPreferredGap(ComponentPlacement.UNRELATED)
    						.addComponent(getJLabelDcn4())
    						.addGap(12)
    						.addComponent(getJFormattedTextFieldDcn4(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
    				.addGap(35))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(5)
    						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    							.addComponent(getJLabel0())
    							.addComponent(getJFormattedTextFieldXcorr1(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    							.addComponent(getJLabel1())
    							.addComponent(getJFormattedTextFieldXcorr2(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    							.addComponent(getJLabel2(), javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
    							.addComponent(getJFormattedTextFieldXcorr3(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    							.addComponent(getJLabelXcorr4(), javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
    					.addComponent(getJFormattedTextFieldXcorr4(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    				.addGap(18)
    				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    					.addComponent(getJLabel3())
    					.addComponent(getJFormattedTextFieldDcn1(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    					.addComponent(getJLabel4())
    					.addComponent(getJLabel5())
    					.addComponent(getJFormattedTextFieldDcn3(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    					.addComponent(getJLabelDcn4())
    					.addComponent(getJFormattedTextFieldDcn4(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    					.addComponent(getJFormattedTextFieldDcn2(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
    	);
    	setLayout(groupLayout);
    }

	private JFormattedTextField getJFormattedTextFieldDcn4() {
    	if (jFormattedTextFieldDcn4 == null) {
    		jFormattedTextFieldDcn4 = new JFormattedTextField(new Float(0.0f));
    		jFormattedTextFieldDcn4.setText("0.1");
    		jFormattedTextFieldDcn4.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldDcn4.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldDcn4;
    }

	private JFormattedTextField getJFormattedTextFieldXcorr4() {
    	if (jFormattedTextFieldXcorr4 == null) {
    		jFormattedTextFieldXcorr4 = new JFormattedTextField(new Float(0.0f));
    		jFormattedTextFieldXcorr4.setText("3.75");
    		jFormattedTextFieldXcorr4.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldXcorr4.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldXcorr4;
    }

	private JLabel getJLabelDcn4() {
    	if (jLabelDcn4 == null) {
    		jLabelDcn4 = new JLabel();
    		jLabelDcn4.setText("DeltaCn >4+");
    	}
    	return jLabelDcn4;
    }

	private JLabel getJLabelXcorr4() {
    	if (jLabelXcorr4 == null) {
    		jLabelXcorr4 = new JLabel();
    		jLabelXcorr4.setText("Xcorr >4+");
    	}
    	return jLabelXcorr4;
    }

	private JFormattedTextField getJFormattedTextFieldDcn3() {
    	if (jFormattedTextFieldDcn3 == null) {
    		jFormattedTextFieldDcn3 = new JFormattedTextField(new Float(0.1f));
    		jFormattedTextFieldDcn3.setText("0.1");
    		jFormattedTextFieldDcn3.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldDcn3.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldDcn3;
    }

	private JLabel getJLabel5() {
    	if (jLabel5 == null) {
    		jLabel5 = new JLabel();
    		jLabel5.setText("DeltaCn 3+");
    	}
    	return jLabel5;
    }

	private JFormattedTextField getJFormattedTextFieldDcn2() {
    	if (jFormattedTextFieldDcn2 == null) {
    		jFormattedTextFieldDcn2 = new JFormattedTextField(new Float(0.1f));
    		jFormattedTextFieldDcn2.setText("0.1");
    		jFormattedTextFieldDcn2.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldDcn2.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldDcn2;
    }

	private JLabel getJLabel4() {
    	if (jLabel4 == null) {
    		jLabel4 = new JLabel();
    		jLabel4.setText("DeltaCn 2+");
    		jLabel4.setMinimumSize(new Dimension(40, 25));
    		jLabel4.setMaximumSize(new Dimension(40, 25));
    	}
    	return jLabel4;
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
    		jLabel3.setText("DeltaCn 1+");
    	}
    	return jLabel3;
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("Xcorr 1+");
    	}
    	return jLabel0;
    }

	private JFormattedTextField getJFormattedTextFieldXcorr2() {
    	if (jFormattedTextFieldXcorr2 == null) {
    		jFormattedTextFieldXcorr2 = new JFormattedTextField(new Float(0.1f));
    		jFormattedTextFieldXcorr2.setHorizontalAlignment(SwingConstants.LEFT);
    		jFormattedTextFieldXcorr2.setText("2.2");
    		jFormattedTextFieldXcorr2.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldXcorr2.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldXcorr2;
    }

	private JLabel getJLabel1() {
    	if (jLabel1 == null) {
    		jLabel1 = new JLabel();
    		jLabel1.setText("Xcorr 2+");
    		jLabel1.setMinimumSize(new Dimension(45, 25));
    		jLabel1.setMaximumSize(new Dimension(45, 25));
    	}
    	return jLabel1;
    }

	private JFormattedTextField getJFormattedTextFieldXcorr3() {
    	if (jFormattedTextFieldXcorr3 == null) {
    		jFormattedTextFieldXcorr3 = new JFormattedTextField(new Float(0.1f));
    		jFormattedTextFieldXcorr3.setText("3.75");
    		jFormattedTextFieldXcorr3.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldXcorr3.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldXcorr3;
    }

	private JLabel getJLabel2() {
    	if (jLabel2 == null) {
    		jLabel2 = new JLabel();
    		jLabel2.setText("Xcorr 3+");
    	}
    	return jLabel2;
    }

	private JFormattedTextField getJFormattedTextFieldXcorr1() {
    	if (jFormattedTextFieldXcorr1 == null) {
    		jFormattedTextFieldXcorr1 = new JFormattedTextField(new Float(0.0f));
    		jFormattedTextFieldXcorr1.setText("1.9");
    		jFormattedTextFieldXcorr1.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldXcorr1.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldXcorr1;
    }

	@Override
    public IPeptideCriteria<ISequestPeptide> getCriteria() {
		
		float[] xcorrs = new float[7];
		float[] deltaCns = new float[7];
		
		xcorrs[1] = Float.parseFloat(this.getJFormattedTextFieldXcorr1().getText());
		xcorrs[2] = Float.parseFloat(this.getJFormattedTextFieldXcorr2().getText());
		xcorrs[3] = Float.parseFloat(this.getJFormattedTextFieldXcorr3().getText());
		
		float xcorrb4 = Float.parseFloat(this.getJFormattedTextFieldXcorr4().getText());
		xcorrs[4] = xcorrb4;
		xcorrs[5] = xcorrb4;
		xcorrs[6] = xcorrb4;
		
		deltaCns[1] = Float.parseFloat(this.getJFormattedTextFieldDcn1().getText());
		deltaCns[2] = Float.parseFloat(this.getJFormattedTextFieldDcn2().getText());
		deltaCns[3] = Float.parseFloat(this.getJFormattedTextFieldDcn3().getText());
		
		float dcnb4 = Float.parseFloat(this.getJFormattedTextFieldDcn4().getText());
		deltaCns[4] = dcnb4;
		deltaCns[5] = dcnb4;
		deltaCns[6] = dcnb4;
		
		return new DefaultSequestCriteria(xcorrs, deltaCns);
    }

}
