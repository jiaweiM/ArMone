/* 
 ******************************************************************************
 * File: GlycoMatchPanel.java * * * Created on Jan 19, 2016
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.gui;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;

import javax.swing.GroupLayout.Alignment;

/**
 *
 * @version Jan 19, 2016, 10:33:07 AM
 */
public class GlycoMatchPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton jButtonLoadGlycoMatch;

	GlycoMatchPanel() {
		initComponents();
	}

	private void initComponents() {
		setSize(270, 240);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(30)
					.addComponent(getJButtonLoadGlycoMatch(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(15)
					.addComponent(getJButtonLoadGlycoMatch(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
		);
		setLayout(groupLayout);
	}

	public JButton getJButtonLoadGlycoMatch() {
		if (jButtonLoadGlycoMatch == null) {
			jButtonLoadGlycoMatch = new JButton();
			jButtonLoadGlycoMatch.setText("<html><p align=\"center\">Load glycan structure</p></html>");
			jButtonLoadGlycoMatch.setBackground(Color.GREEN);
		}
		return jButtonLoadGlycoMatch;
	}

}
