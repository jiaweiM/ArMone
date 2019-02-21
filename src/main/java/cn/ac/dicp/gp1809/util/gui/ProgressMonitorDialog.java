/* 
 ******************************************************************************
 * File: ProgressMonitorDialog.java * * * Created on 04-09-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.gui;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

/**
 * 
 * @author Xinning
 * @version 0.1, 04-09-2009, 20:19:33
 */
public class ProgressMonitorDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JProgressBar jProgressBar;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	private Component owner;

	public ProgressMonitorDialog(){
		this(null);
	}
	
	ProgressMonitorDialog(Component owner) {
		super(UIutilities.getWindowForComponent(owner));
		this.owner = owner;
		initComponents();
	}

	private void initComponents() {
    	setUndecorated(true);
    	setLayout(new GroupLayout());
    	add(getJProgressBar(), new Constraints(new Bilateral(0, 0, 10), new Bilateral(0, 0, 14)));
    	
    	int width = this.owner != null ? this.getParent().getSize().width : 600;
    	setSize((int)(width*0.8), 18);
    	this.setVisible(true);
    }

	/**
	 * The progress bar
	 * 
	 * @return
	 */
	JProgressBar getJProgressBar() {
    	if (jProgressBar == null) {
    		jProgressBar = new JProgressBar();
    	}
    	return jProgressBar;
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
				ProgressMonitorDialog dialog = new ProgressMonitorDialog();
				dialog
				        .setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				dialog.setTitle("ProgressMonitorDialog");
				dialog.setLocationRelativeTo(null);
				dialog.getContentPane().setPreferredSize(dialog.getSize());
				dialog.pack();
				dialog.setVisible(true);
			}
		});
	}

}
