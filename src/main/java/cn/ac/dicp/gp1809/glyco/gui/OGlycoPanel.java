/* 
 ******************************************************************************
 * File: OGlycoPanel.java * * * Created on 2013-12-2
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.GroupLayout.Alignment;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.GroupLayout;
import java.awt.SystemColor;

public class OGlycoPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton btnDeglyco;
	private JButton btnGlycoform;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";

	public OGlycoPanel() {
		initComponents();
	}

	private void initComponents() {
		setSize(201, 253);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addGap(35)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(getBtnGlycoform(), Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(getBtnDeglyco(), Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGap(144)));
		groupLayout
				.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup().addGap(30)
								.addComponent(getBtnDeglyco(), GroupLayout.PREFERRED_SIZE, 62,
										GroupLayout.PREFERRED_SIZE)
								.addGap(29).addComponent(getBtnGlycoform(), GroupLayout.PREFERRED_SIZE, 76,
										GroupLayout.PREFERRED_SIZE)
								.addGap(73)));
		groupLayout.linkSize(SwingConstants.VERTICAL, new Component[] { getBtnGlycoform(), getBtnDeglyco() });
		groupLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] { getBtnGlycoform(), getBtnDeglyco() });
		setLayout(groupLayout);
	}

	public JButton getBtnGlycoform() {
		if (btnGlycoform == null) {
			btnGlycoform = new JButton();
			btnGlycoform.setText("<html><p align=\"center\">O-glycoform<br>determination<p><html>");
			btnGlycoform.addActionListener(this);
			btnGlycoform.setBackground(Color.CYAN);
		}
		return btnGlycoform;
	}

	public JButton getBtnDeglyco() {
		if (btnDeglyco == null) {
			btnDeglyco = new JButton();
			btnDeglyco.setText("<html><p align=\"center\"><i>In silico</i><br>deglycosylation<html>");
			btnDeglyco.addActionListener(this);
			btnDeglyco.setBackground(SystemColor.textHighlight);
		}
		return btnDeglyco;
	}

	private static void installLnF() {
		try {
			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
			if (lnfClassname == null)
				lnfClassname = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lnfClassname);
		} catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL + " on this platform:" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("OGlycoPanel");
				OGlycoPanel content = new OGlycoPanel();
				content.setPreferredSize(content.getSize());
				frame.getContentPane().add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		Object obj = arg0.getSource();

		if (obj == this.getBtnDeglyco()) {
			return;
		}

		if (obj == this.getBtnGlycoform()) {
			return;
		}
	}

}
