/* 
 ******************************************************************************
 * File:PhosPanel.java * * * Created on 2011-8-3
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.GroupLayout.Alignment;

public class PhosPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JButton jButtonGen;
	private JButton jButtonApi;
	private JButton jButtonKin;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PhosPanel() {
		initComponents();
	}

	private void initComponents() {
		setBorder(null);
		setSize(220, 270);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(40)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJButtonGen(), javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(getJButtonApi(), javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(getJButtonKin(), javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(30)
					.addComponent(getJButtonGen(), javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(15)
					.addComponent(getJButtonApi(), javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(15)
					.addComponent(getJButtonKin(), javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
		);
		setLayout(groupLayout);
	}

	private JButton getJButtonKin() {
		if (jButtonKin == null) {
			jButtonKin = new JButton();
			jButtonKin.setText("<html><p aligh=\"center\">Kinase statistics</p></html>");
			jButtonKin.setOpaque(true);
			jButtonKin.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButtonKinMouseMouseClicked(event);
				}
			});
		}
		return jButtonKin;
	}

	private JButton getJButtonApi() {
		if (jButtonApi == null) {
			jButtonApi = new JButton();
			jButtonApi.setText("<html><p aligh=\"center\">MS2/MS3 strategy (APIVASE)</p></html>");
			jButtonApi.setOpaque(true);
			jButtonApi.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButtonApiMouseMouseClicked(event);
				}
			});
		}
		return jButtonApi;
	}

	private JButton getJButtonGen() {
		if (jButtonGen == null) {
			jButtonGen = new JButton();
			jButtonGen.setText("<html><p aligh=\"center\">General strategy</p></html>");
			jButtonGen.setOpaque(true);
			jButtonGen.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButtonGenMouseMouseClicked(event);
				}
			});
		}
		return jButtonGen;
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
				frame.setTitle("PhosPanel");
				PhosPanel content = new PhosPanel();
				content.setPreferredSize(content.getSize());
				frame.getContentPane().add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	private void jButtonGenMouseMouseClicked(MouseEvent event) {
	}

	private void jButtonApiMouseMouseClicked(MouseEvent event) {
	}

	private void jButtonKinMouseMouseClicked(MouseEvent event) {
	}
}
