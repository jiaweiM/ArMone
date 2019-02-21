/* 
 ******************************************************************************
 * File: LInfoSixPanel.java * * * Created on 2013-3-28
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;


//VS4E -- DO NOT REMOVE THIS LINE!
public class LInfoSixPanel extends JPanel {
	
	private JLabel jLabelName;

	private static final long serialVersionUID = 1L;

	private JLabel jLabel0;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JLabel jLabel5;

	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	
	public LInfoSixPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJLabelName(), new Constraints(new Leading(23, 10, 10), new Leading(16, 10, 10)));
		add(getJLabel0(), new Constraints(new Leading(40, 138, 10, 10), new Leading(45, 10, 10)));
		add(getJLabel1(), new Constraints(new Leading(250, 138, 10, 10), new Leading(45, 10, 10)));
		add(getJLabel2(), new Constraints(new Leading(40, 138, 10, 10), new Leading(80, 10, 10)));
		add(getJLabel3(), new Constraints(new Leading(250, 138, 10, 10), new Leading(80, 10, 10)));
		add(getJLabel4(), new Constraints(new Leading(40, 138, 10, 10), new Leading(115, 10, 10)));
		add(getJLabel5(), new Constraints(new Leading(250, 138, 10, 10), new Leading(115, 10, 10)));
		setSize(460, 155);
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Label 1 : CH3-K0");
		}
		return jLabel0;
	}
	
	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Label 2 : CD2H-K0");
		}
		return jLabel1;
	}
	
	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Label 3 : 13CD3-K0");
		}
		return jLabel2;
	}
	
	private JLabel getJLabel3() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("Label 4 : CH3-K4");
		}
		return jLabel3;
	}
	
	private JLabel getJLabel4() {
		if (jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("Label 5 : CD2H-K4");
		}
		return jLabel4;
	}
	
	private JLabel getJLabel5() {
		if (jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText("Label 6 : 13CD3-K4");
		}
		return jLabel5;
	}

	private JLabel getJLabelName() {
		if (jLabelName == null) {
			jLabelName = new JLabel();
			jLabelName.setText("Six-plex");
		}
		return jLabelName;
	}
	
	public LabelType getLabelType(){
		return LabelType.SixLabel;
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
				frame.setTitle("LInfoSixPanel");
				LInfoSixPanel content = new LInfoSixPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
	
}
