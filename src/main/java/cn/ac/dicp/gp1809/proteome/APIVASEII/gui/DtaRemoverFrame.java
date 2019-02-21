/* 
 ******************************************************************************
 * File: DtaRemoverFrame.java * * * Created on 05-04-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-04-2009, 20:03:44
 */
public class DtaRemoverFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private DtaRemoverPanel dtaRemoverPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public DtaRemoverFrame() {
		initComponents();
	}

	private void initComponents() {
    	setTitle("MS2/MS3 DTA preprocess");
    	setLayout(new GroupLayout());
    	add(getDtaRemoverPanel0(), new Constraints(new Bilateral(6, 6, 827), new Bilateral(6, 6, 10, 517)));
    	setSize(839, 529);
    }

	private DtaRemoverPanel getDtaRemoverPanel0() {
    	if (dtaRemoverPanel0 == null) {
    		dtaRemoverPanel0 = new DtaRemoverPanel();
    	}
    	return dtaRemoverPanel0;
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
				DtaRemoverFrame frame = new DtaRemoverFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("DtaRemoverFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
