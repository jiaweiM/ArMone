/* 
 ******************************************************************************
 * File:PTMPanel.java * * * Created on 2011-8-3
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

public class PTMPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel jPanel0;
	private JButton jButtonPhos;
	private JButton jButtonGlyco;
	private PhosPanel phosPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PTMPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJPanel0(), new Constraints(new Trailing(0, 220, 10, 10), new Bilateral(0, 0, 0)));
		add(getJButtonPhos(), new Constraints(new Leading(45, 120, 10, 10), new Leading(45, 60, 10, 10)));
		add(getJButtonGlyco(), new Constraints(new Leading(45, 120, 10, 10), new Leading(150, 60, 10, 10)));
		setSize(450, 270);
	}

	private PhosPanel getPhosPanel0() {
		if (phosPanel0 == null) {
			phosPanel0 = new PhosPanel();
		}
		return phosPanel0;
	}

	private JButton getJButtonGlyco() {
		if (jButtonGlyco == null) {
			jButtonGlyco = new JButton();
			jButtonGlyco.setText("<html><p align=\"center\">Glycosylation analysis<p><html>");
			jButtonGlyco.setBackground(Color.yellow);
			jButtonGlyco.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
//					jButtonGlycoMouseMouseClicked(event);
				}
			});
		}
		return jButtonGlyco;
	}

	private JButton getJButtonPhos() {
		if (jButtonPhos == null) {
			jButtonPhos = new JButton();
			jButtonPhos.setText("<html><p align=\"center\">Phosphorylation analysis<p><html>");
			jButtonPhos.setBackground(Color.pink);
			jButtonPhos.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButtonPhosMouseMouseClicked(event);
				}
			});
		}
		return jButtonPhos;
	}

	private JPanel getJPanel0() {
		if (jPanel0 == null) {
			jPanel0 = new JPanel();
			jPanel0.setLayout(new GroupLayout());
			jPanel0.add(getPhosPanel0(), new Constraints(new Leading(0, 220, 12, 12), new Leading(0, 270, 12, 12)));
		}
		return jPanel0;
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
				frame.setTitle("PTMPanel");
				PTMPanel content = new PTMPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	private void jButtonPhosMouseMouseClicked(MouseEvent event) {
		this.jPanel0.removeAll();
		this.jPanel0.add(getPhosPanel0(), new Constraints(new Leading(0, 220, 12, 12), new Leading(0, 270, 12, 12)));
		this.jPanel0.updateUI();
	}


}
