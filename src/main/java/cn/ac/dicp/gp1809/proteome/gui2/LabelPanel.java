/* 
 ******************************************************************************
 * File:LabelPanel.java * * * Created on 2011-8-4
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

//VS4E -- DO NOT REMOVE THIS LINE!
public class LabelPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JButton jButtonLoad;
	private JButton jButtonRepeat;
	private JButton jButtonMerge;
	
	private MyJFileChooser resultChooser;
	private JButton jButtonTurnOver;
	private static Color yellow = new Color(255, 255, 128);
	
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public LabelPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJButtonLoad(), new Constraints(new Leading(10, 80, 10, 10), new Leading(15, 80, 10, 10)));
		add(getJButtonRepeat(), new Constraints(new Leading(10, 80, 10, 10), new Leading(135, 80, 10, 10)));
		add(getJButtonMerge(), new Constraints(new Leading(130, 80, 10, 10), new Leading(15, 80, 10, 10)));
		add(getJButtonTurnOver(), new Constraints(new Leading(130, 80, 10, 10), new Leading(135, 80, 10, 10)));
		setSize(360, 270);
	}

	protected JButton getJButtonTurnOver() {
		if (jButtonTurnOver == null) {
			jButtonTurnOver = new JButton();
			jButtonTurnOver.setText("<html><p align=\"center\">Sixplex turnover analysis</p></html>");
			jButtonTurnOver.setBackground(yellow);
		}
		return jButtonTurnOver;
	}

	protected JButton getJButtonMerge() {
		if (jButtonMerge == null) {
			jButtonMerge = new JButton();
			jButtonMerge.setText("<html><p align=\"center\">Gradient result merge</p></html>");
			jButtonMerge.setBackground(yellow);
		}
		return jButtonMerge;
	}

	protected JButton getJButtonRepeat() {
		if (jButtonRepeat == null) {
			jButtonRepeat = new JButton();
			jButtonRepeat.setText("<html><p align=\"center\">Repeat result analysis</p></html>");
			jButtonRepeat.setBackground(yellow);
		}
		return jButtonRepeat;
	}

	protected JButton getJButtonLoad() {
		if (jButtonLoad == null) {
			jButtonLoad = new JButton();
			jButtonLoad.setText("<html><p align=\"center\">Open quantitative result</p></html>");
			jButtonLoad.setBackground(yellow);
		}
		return jButtonLoad;
	}

	private MyJFileChooser getResultChooser(){
		if( this.resultChooser==null){
			this.resultChooser = new MyJFileChooser();
			this.resultChooser.setFileFilter(new String[] { "pxml" },
	        	" Peptide XML file (*.pxml)");
		}
		return resultChooser;
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
				frame.setTitle("LabelPanel");
				LabelPanel content = new LabelPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
