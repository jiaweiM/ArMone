/* 
 ******************************************************************************
 * File:PennPanel.java * * * Created on 2011-8-3
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
import javax.swing.WindowConstants;

import cn.ac.dicp.gp1809.proteome.penn.PENNFrm;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

//VS4E -- DO NOT REMOVE THIS LINE!
public class PennPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JButton jButtonPenn;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PennPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJButtonPenn(), new Constraints(new Leading(49, 163, 10, 10), new Leading(54, 61, 10, 10)));
		setSize(450, 270);
	}

	private JButton getJButtonPenn() {
		if (jButtonPenn == null) {
			jButtonPenn = new JButton();
			jButtonPenn.setText("<html><p align=\"center\">Peptide probability calculate (PENN)</p></html>");
			jButtonPenn.addMouseListener(new MouseAdapter() {
				
				public void mouseClicked(MouseEvent event) {
					jButtonPennMouseMouseClicked(event);
				}
			});		
		}
		return jButtonPenn;
	}

	/**
	 * @param event
	 */
	protected void jButtonPennMouseMouseClicked(MouseEvent event) {
		// TODO Auto-generated method stub
		JFrame frame = new PENNFrm();
		frame.getContentPane().setPreferredSize(frame.getSize());
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(this);
		frame.setVisible(true);
		return ;
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
				frame.setTitle("PennPanel");
				PennPanel content = new PennPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
