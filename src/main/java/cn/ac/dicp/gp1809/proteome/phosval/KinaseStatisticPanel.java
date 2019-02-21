/* 
 ******************************************************************************
Motif DescriptionMotif DescriptionMotif Description * File:KinaseStatisticPanel.java * * * Created on 2009-12-24
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.phosval;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

//VS4E -- DO NOT REMOVE THIS LINE!
public class KinaseStatisticPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel motifDes;
	private JTextField jTextField0;
	private JLabel proportion;
	private JTextField jTextField1;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public KinaseStatisticPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getMotifDes(), new Constraints(new Leading(12, 10, 10), new Leading(11, 10, 10)));
		add(getProportion(), new Constraints(new Leading(12, 6, 6), new Leading(41, 6, 6)));
		add(getJTextField0(), new Constraints(new Leading(126, 201, 6, 6), new Leading(9, 6, 6)));
		add(getJTextField1(), new Constraints(new Leading(95, 71, 10, 10), new Leading(39, 6, 6)));
		setSize(338, 72);
	}

	private JTextField getJTextField1() {
		if (jTextField1 == null) {
			jTextField1 = new JTextField();
		}
		return jTextField1;
	}

	private JLabel getProportion() {
		if (proportion == null) {
			proportion = new JLabel();
			proportion.setText("Proportion");
		}
		return proportion;
	}

	private JTextField getJTextField0() {
		if (jTextField0 == null) {
			jTextField0 = new JTextField();
		}
		return jTextField0;
	}

	private JLabel getMotifDes() {
		if (motifDes == null) {
			motifDes = new JLabel();
			motifDes.setText("Motif Description");
		}
		return motifDes;
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
				frame.setTitle("KinaseStatisticPanel");
				KinaseStatisticPanel content = new KinaseStatisticPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
