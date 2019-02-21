/* 
 ******************************************************************************
 * File: ProcessingDlgNew.java * * * Created on 2011-10-24
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

//VS4E -- DO NOT REMOVE THIS LINE!
public class ProcessingDlgNew extends JDialog {

	private static final long serialVersionUID = 1L;
	private JProgressBar jProgressBar0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public ProcessingDlgNew() {
		initComponents();
	}
	
	public ProcessingDlgNew(Component parent) {
		super(UIutilities.getFrameForComponent(parent), true);
		System.out.println("1");
		initComponents();
		System.out.println("2");
		this.setUndecorated(true);
		System.out.println("3");
		this.setLocationRelativeTo(parent);
		System.out.println("4");
//		this.setVisible(true);
		System.out.println("5");
	}

	private void initComponents() {
		setFont(new Font("Dialog", Font.PLAIN, 12));
		setBackground(new Color(204, 232, 207));
		setForeground(Color.black);
		setLayout(new GroupLayout());
		add(getJProgressBar0(), new Constraints(new Bilateral(30, 30, 10, 10), new Bilateral(30, 30, 10, 10)));
		setSize(400, 80);
	}

	private JProgressBar getJProgressBar0() {
		if (jProgressBar0 == null) {
			jProgressBar0 = new JProgressBar();
			jProgressBar0.setStringPainted(true);
			jProgressBar0.setString("Loading...");
			jProgressBar0.setIndeterminate(true);
		}
		return jProgressBar0;
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
				ProcessingDlgNew dialog = new ProcessingDlgNew();
				dialog
						.setDefaultCloseOperation(ProcessingDlgNew.DISPOSE_ON_CLOSE);
				dialog.setTitle("ProcessingDlgNew");
				dialog.setLocationRelativeTo(null);
				dialog.getContentPane().setPreferredSize(dialog.getSize());
				dialog.pack();
				dialog.setVisible(true);
			}
		});
	}

}
