/* 
 ******************************************************************************
 * File: DecoyPeptideRemovalCriteria.java * * * Created on 06-07-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultDecoyPeptideFilter;
import javax.swing.GroupLayout.Alignment;

/**
 * Protein criteria to remove the decoy proteins
 * 
 * @author Xinning
 * @version 0.1, 06-07-2010, 14:35:13
 */
public class DecoyPeptideRemovalCriteria extends JPanel implements ICriteriaSetter<IPeptide> {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel0;

	@Override
	public IPeptideCriteria<IPeptide> getCriteria() {
		return new DefaultDecoyPeptideFilter();
	}

	public DecoyPeptideRemovalCriteria() {
		initComponents();
		this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(getJLabel0())));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(getJLabel0())));
		setLayout(groupLayout);
	}

	private void initComponents() {
		setMinimumSize(new Dimension(184, 30));
		setSize(184, 30);
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Remove decoy Peptides");
		}
		return jLabel0;
	}

}
