/* 
 ******************************************************************************
 * File:QStatPanel.java * * * Created on 2010-5-24
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.gui;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelStatInfo;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import javax.swing.GroupLayout.Alignment;

public class QStatPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel jLabelStatInfoLabel;
	private JTable jTable0;
	private JScrollPane jScrollPane1;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public QStatPanel() {
		initComponents();
	}

	private void initComponents() {
		setSize(362, 235);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(23)
					.addComponent(getJLabelStatInfoLabel()))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(20)
					.addComponent(getJScrollPane1(), javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(29)
					.addComponent(getJLabelStatInfoLabel())
					.addGap(20)
					.addComponent(getJScrollPane1(), javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
		);
		setLayout(groupLayout);
	}

	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJTable0());
		}
		return jScrollPane1;
	}

	private JTable getJTable0() {
		if (jTable0 == null) {
			jTable0 = new JTable();
			jTable0.setModel(new DefaultTableModel(new String[0][0], LabelStatInfo.getTableColNames()));
		}
		return jTable0;
	}

	private JLabel getJLabelStatInfoLabel() {
		if (jLabelStatInfoLabel == null) {
			jLabelStatInfoLabel = new JLabel();
			jLabelStatInfoLabel.setText("Peptide Quantitive Statistic Infomation");
		}
		return jLabelStatInfoLabel;
	}
	
	public void loadQuanInfo(){
		
	}

	private static void installLnF() {
		try {
			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
			if (lnfClassname == null)
				lnfClassname = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lnfClassname);
		} catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL
					+ " on this platform:" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("QStatPanel");
				QStatPanel content = new QStatPanel();
				content.setPreferredSize(content.getSize());
				frame.getContentPane().add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
