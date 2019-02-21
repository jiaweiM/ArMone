/* 
 ******************************************************************************
 * File: CruxCriteriaPanel.java * * * Created on 04-09-2009
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

import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultCruxCriteria;
import javax.swing.GroupLayout.Alignment;

/**
 * Criteria panel for filtering of X!Tandem peptides
 * 
 * @author Xinning
 * @version 0.1, 04-09-2009, 21:16:45
 */
public class CruxCriteriaPanel extends JPanel implements
        ICriteriaSetter<ICruxPeptide> {

	private static final long serialVersionUID = 1L;
	private JFormattedTextField jFormattedTextFieldPvalue;
	private JCheckBox jCheckBoxEvalue;
	private JCheckBox jCheckBoxQvalue;
	private JFormattedTextField jFormattedTextFieldQvalue;
	private JCheckBox jCheckBoxPercolatorScore;
	private JFormattedTextField jFormattedTextFieldPercolatorScore;

	public CruxCriteriaPanel() {
		initComponents();
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(null, "Crux filters",
		        TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font(
		                "SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
		setMinimumSize(new Dimension(391, 145));
		setPreferredSize(new Dimension(391, 145));
		setSize(585, 145);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(getJCheckBoxEvalue())
					.addGap(14)
					.addComponent(getJFormattedTextFieldPvalue(), javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(76)
					.addComponent(getJCheckBoxQvalue())
					.addGap(12)
					.addComponent(getJFormattedTextFieldQvalue(), javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(68)
					.addComponent(getJCheckBoxPercolatorScore(), javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(6)
					.addComponent(getJFormattedTextFieldPercolatorScore(), javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(9)
					.addComponent(getJCheckBoxEvalue()))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addComponent(getJFormattedTextFieldPvalue(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(9)
					.addComponent(getJCheckBoxQvalue()))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addComponent(getJFormattedTextFieldQvalue(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(9)
					.addComponent(getJCheckBoxPercolatorScore()))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addComponent(getJFormattedTextFieldPercolatorScore(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
		);
		setLayout(groupLayout);
	}

	private JFormattedTextField getJFormattedTextFieldPercolatorScore() {
		if (jFormattedTextFieldPercolatorScore == null) {
			jFormattedTextFieldPercolatorScore = new JFormattedTextField(
			        new Float(0.1f));
			jFormattedTextFieldPercolatorScore.setMinimumSize(new Dimension(40,
			        25));
			jFormattedTextFieldPercolatorScore.setPreferredSize(new Dimension(
			        40, 25));
			jFormattedTextFieldPercolatorScore.setAutoscrolls(true);
		}
		return jFormattedTextFieldPercolatorScore;
	}

	private JCheckBox getJCheckBoxPercolatorScore() {
		if (jCheckBoxPercolatorScore == null) {
			jCheckBoxPercolatorScore = new JCheckBox();
			jCheckBoxPercolatorScore.setText("percolator score");
		}
		return jCheckBoxPercolatorScore;
	}

	private JFormattedTextField getJFormattedTextFieldQvalue() {
		if (jFormattedTextFieldQvalue == null) {
			jFormattedTextFieldQvalue = new JFormattedTextField(new Float(0.1));
			jFormattedTextFieldQvalue.setText("0.01");
			jFormattedTextFieldQvalue.setMinimumSize(new Dimension(40, 25));
			jFormattedTextFieldQvalue.setPreferredSize(new Dimension(40, 25));
			jFormattedTextFieldQvalue.setAutoscrolls(true);
		}
		return jFormattedTextFieldQvalue;
	}

	private JCheckBox getJCheckBoxQvalue() {
		if (jCheckBoxQvalue == null) {
			jCheckBoxQvalue = new JCheckBox();
			jCheckBoxQvalue.setText("q-value");
		}
		return jCheckBoxQvalue;
	}

	private JCheckBox getJCheckBoxEvalue() {
		if (jCheckBoxEvalue == null) {
			jCheckBoxEvalue = new JCheckBox();
			jCheckBoxEvalue.setSelected(true);
			jCheckBoxEvalue.setText("P-value");
		}
		return jCheckBoxEvalue;
	}

	private JFormattedTextField getJFormattedTextFieldPvalue() {
		if (jFormattedTextFieldPvalue == null) {
			jFormattedTextFieldPvalue = new JFormattedTextField(new Float(0.1f));
			jFormattedTextFieldPvalue.setText("0.1");
			jFormattedTextFieldPvalue.setMinimumSize(new Dimension(40, 25));
			jFormattedTextFieldPvalue.setPreferredSize(new Dimension(40, 25));
		}
		return jFormattedTextFieldPvalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.gui.Criterias.ICriteriaSetter#getCriteria()
	 */
	@Override
	public IPeptideCriteria<ICruxPeptide> getCriteria() {

		float pvalue = -Float.MAX_VALUE;
		float qvalue = 1f;
		float percolatorScore = -Float.MAX_VALUE;

		if (this.getJCheckBoxEvalue().isSelected()) {
			pvalue = Float.parseFloat(this.getJFormattedTextFieldPvalue()
			        .getText());
		}

		if (this.getJCheckBoxQvalue().isSelected()) {
			qvalue = Float.parseFloat(this.getJFormattedTextFieldQvalue()
			        .getText());
		}

		if (this.getJCheckBoxPercolatorScore().isSelected()) {
			percolatorScore = Float.parseFloat(this
			        .getJFormattedTextFieldPercolatorScore().getText());
		}

		return new DefaultCruxCriteria(pvalue, percolatorScore, qvalue);
	}

}
