/* 
 ******************************************************************************
 * File: XTandemCriteriaPanel.java * * * Created on 04-09-2009
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

import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.IXTandemPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultXTandemCriteria;
import javax.swing.GroupLayout.Alignment;

/**
 * Criteria panel for filtering of X!Tandem peptides
 * 
 * @author Xinning
 * @version 0.1, 04-09-2009, 21:16:45
 */
public class XTandemCriteriaPanel extends JPanel implements ICriteriaSetter<IXTandemPeptide> {

	private static final long serialVersionUID = 1L;
	private JFormattedTextField jFormattedTextFieldEvalue;
	private JCheckBox jCheckBoxEvalue;
	private JCheckBox jCheckBoxHyperScore;
	private JFormattedTextField jFormattedTextFieldHyperScore;

	public XTandemCriteriaPanel() {
		initComponents();
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(null, "X!Tandem filters", TitledBorder.LEADING,
				TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
		setMinimumSize(new Dimension(391, 145));
		setPreferredSize(new Dimension(391, 145));
		setSize(585, 145);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addComponent(getJCheckBoxEvalue()).addGap(14)
						.addComponent(getJFormattedTextFieldEvalue(), javax.swing.GroupLayout.PREFERRED_SIZE, 43,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(76).addComponent(getJCheckBoxHyperScore()).addGap(12)
						.addComponent(getJFormattedTextFieldHyperScore(), javax.swing.GroupLayout.PREFERRED_SIZE, 43,
								javax.swing.GroupLayout.PREFERRED_SIZE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addGap(9).addComponent(getJCheckBoxEvalue()))
				.addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(getJFormattedTextFieldEvalue(),
						javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup().addGap(9).addComponent(getJCheckBoxHyperScore()))
				.addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(getJFormattedTextFieldHyperScore(),
						javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.PREFERRED_SIZE)));
		setLayout(groupLayout);
	}

	private JFormattedTextField getJFormattedTextFieldHyperScore() {
		if (jFormattedTextFieldHyperScore == null) {
			jFormattedTextFieldHyperScore = new JFormattedTextField(new Float(0.1f));
			jFormattedTextFieldHyperScore.setText("0.1");
			jFormattedTextFieldHyperScore.setMinimumSize(new Dimension(40, 25));
			jFormattedTextFieldHyperScore.setPreferredSize(new Dimension(40, 25));
		}
		return jFormattedTextFieldHyperScore;
	}

	private JCheckBox getJCheckBoxHyperScore() {
		if (jCheckBoxHyperScore == null) {
			jCheckBoxHyperScore = new JCheckBox();
			jCheckBoxHyperScore.setText("HyperScore");
		}
		return jCheckBoxHyperScore;
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
	public IPeptideCriteria<IXTandemPeptide> getCriteria() {

		double evalue = Double.MAX_VALUE;
		float hyperScore = -Float.MAX_VALUE;

		if (this.getJCheckBoxEvalue().isSelected()) {
			evalue = Double.parseDouble(this.getJFormattedTextFieldEvalue().getText());
		}

		if (this.getJCheckBoxHyperScore().isSelected()) {
			hyperScore = Float.parseFloat(this.getJFormattedTextFieldHyperScore().getText());
		}

		return new DefaultXTandemCriteria(evalue, hyperScore);
	}

}
