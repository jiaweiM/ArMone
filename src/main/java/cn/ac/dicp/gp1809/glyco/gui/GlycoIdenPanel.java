/* 
 ******************************************************************************
 * File: GlycoIdenPanel.java * * * Created on Jan 19, 2016
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
import javax.swing.GroupLayout;

/**
 *
 * @version Jan 19, 2016, 10:28:06 AM
 */
public class GlycoIdenPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton jButtonGlycoStrucIden;
	private JButton jButtonLoadGlycoStruc;

	GlycoIdenPanel() {
		initComponents();
	}

	private void initComponents() {
		setSize(354, 252);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(30)
					.addComponent(getJButtonGlycoStrucIden(), GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
					.addGap(35)
					.addComponent(getJButtonLoadGlycoStruc(), GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
					.addGap(20))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(15)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(getJButtonLoadGlycoStruc(), Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(getJButtonGlycoStrucIden(), Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
					.addGap(134))
		);
		setLayout(groupLayout);
	}

	public JButton getJButtonGlycoStrucIden() {
		if (jButtonGlycoStrucIden == null) {
			jButtonGlycoStrucIden = new JButton();
			jButtonGlycoStrucIden.setText("<html><p align=\"center\">Glycan structure analysis</p></html>");
			jButtonGlycoStrucIden.setBackground(Color.pink);
		}
		return jButtonGlycoStrucIden;
	}

	public JButton getJButtonLoadGlycoStruc() {
		if (jButtonLoadGlycoStruc == null) {
			jButtonLoadGlycoStruc = new JButton();
			jButtonLoadGlycoStruc.setText("<html><p align=\"center\">Load glycan structure</p></html>");
			jButtonLoadGlycoStruc.setBackground(Color.pink);
		}
		return jButtonLoadGlycoStruc;
	}

}
