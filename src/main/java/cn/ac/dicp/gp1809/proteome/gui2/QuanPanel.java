/* 
 ******************************************************************************
 * File:QuanPanel.java * * * Created on 2011-8-3
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

//VS4E -- DO NOT REMOVE THIS LINE!
public class QuanPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel jPanel0;
	private JButton jButtonLabel;
	private JButton jButtonLFree;
	private JButton jButtonSPCount;
	private LabelPanel labelPanel0;
	private LFreePanel lfreePanel0;
	private static Color yellow = new Color(255, 255, 128);

	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public QuanPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJPanel0(), new Constraints(new Leading(240, 10, 10), new Bilateral(0, 0, 0)));
		add(getJButtonLabel(), new Constraints(new Leading(45, 120, 10, 10), new Leading(30, 60, 10, 10)));
//		add(getJButtonLFree(), new Constraints(new Leading(45, 120, 10, 10), new Leading(100, 60, 10, 10)));
		add(getJButtonSPC(), new Constraints(new Leading(45, 120, 10, 10), new Leading(150, 60, 10, 10)));		
		getLFreePanel0();
		setSize(600, 270);
	}
	
	private LabelPanel getLabelPanel0() {
		if (labelPanel0 == null) {
			labelPanel0 = new LabelPanel();
		}
		return labelPanel0;
	}

	private LFreePanel getLFreePanel0() {
		if (lfreePanel0 == null) {
			lfreePanel0 = new LFreePanel();
		}
		return lfreePanel0;
	}

	private JButton getJButtonLabel() {
		if (jButtonLabel == null) {
			jButtonLabel = new JButton();
			jButtonLabel.setText("<html><p align=\"center\">Isotope label quantitation<p><html>");
			jButtonLabel.setBackground(yellow);
			jButtonLabel.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButtonLabelMouseMouseClicked(event);
				}
			});
		}
		return jButtonLabel;
	}

	private JButton getJButtonLFree() {
		if (jButtonLFree == null) {
			jButtonLFree = new JButton();
			jButtonLFree.setText("<html><p align=\"center\">Label free quantitation<p><html>");
			jButtonLFree.addMouseListener(new MouseAdapter() {
	
				public void mouseClicked(MouseEvent event) {
					jButtonLFreeMouseMouseClicked(event);
				}
			});
		}
		return jButtonLFree;
	}

	protected JButton getJButtonSPC() {
		if (jButtonSPCount == null) {
			jButtonSPCount = new JButton();
			jButtonSPCount.setText("<html><p align=\"center\">Spectral count<p><html>");
			Color gray = new Color(150, 150, 150);
			jButtonSPCount.setBackground(gray);
		}
		return jButtonSPCount;
	}
	
	protected JButton getJButtonLabelMerge() {
		return this.labelPanel0.getJButtonMerge();
	}

	protected JButton getJButtonLabelRepeat() {
		return this.labelPanel0.getJButtonRepeat();
	}

	protected JButton getJButtonLabelTurnover() {
		return this.labelPanel0.getJButtonTurnOver();
	}

	protected JButton getJButtonLabelLoad() {
		return this.labelPanel0.getJButtonLoad();
	}

	protected JButton getJButtonLabelFreeGenerate() {
		return this.lfreePanel0.getJButtonGenerate();
	}
	
	protected JButton getJButtonLabelFreeLoad() {
		return this.lfreePanel0.getJButtonLoad();
	}
	
	protected JButton getJButtonLabelFreeRepeat() {
		return this.lfreePanel0.getJButtonRepeat();
	}
	
	protected JButton getJButtonLabelFreeTurnover() {
		return this.lfreePanel0.getJButtonTurnOver();
	}

	private JPanel getJPanel0() {
		if (jPanel0 == null) {
			jPanel0 = new JPanel();
			jPanel0.setLayout(new GroupLayout());
			jPanel0.add(getLabelPanel0(), new Constraints(new Bilateral(0, 12, 12), new Bilateral(0, 12, 12)));
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

	/**
	 * Main entry of the class.
	 * Note: This class is only created so that you can easily preview the result at runtime.
	 * It is not expected to be managed by the designer.
	 * You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("QuanPanel");
				QuanPanel content = new QuanPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
	
	private void jButtonLabelMouseMouseClicked(MouseEvent event) {
		this.jPanel0.removeAll();
		this.jPanel0.add(getLabelPanel0(), new Constraints(new Bilateral(0, 12, 12), new Bilateral(0, 12, 12)));
		this.jPanel0.updateUI();
	}

	private void jButtonLFreeMouseMouseClicked(MouseEvent event) {
		this.jPanel0.removeAll();
		this.jPanel0.add(getLFreePanel0(), new Constraints(new Bilateral(0, 12, 12), new Bilateral(0, 12, 12)));
		this.jPanel0.updateUI();
	}

}
