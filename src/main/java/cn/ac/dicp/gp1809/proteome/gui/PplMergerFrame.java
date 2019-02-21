/* 
 ******************************************************************************
 * File: PplMergerFrame.java * * * Created on 05-04-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-04-2009, 20:50:55
 */
public class PplMergerFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private PplMergerPanel pplMergerPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PplMergerFrame() {
		initComponents();
	}

	private void initComponents() {
    	setTitle("peptide list merger");
    	setLayout(new GroupLayout());
    	add(getPplMergerPanel0(), new Constraints(new Bilateral(6, 6, 663), new Bilateral(6, 6, 10)));
    	setSize(675, 406);
    }

	private PplMergerPanel getPplMergerPanel0() {
    	if (pplMergerPanel0 == null) {
    		pplMergerPanel0 = new PplMergerPanel();
    	}
    	return pplMergerPanel0;
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
				PplMergerFrame frame = new PplMergerFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("PplMergerFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
