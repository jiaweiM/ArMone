/* 
 ******************************************************************************
 * File: ProteinGroupSimpFrame.java * * * Created on 03-17-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.protein;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

/**
 * 
 * @author Xinning
 * @version 0.1, 03-17-2010, 23:09:52
 */
public class ProteinGroupSimpFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private ProteinGroupSimpPanel proteinGroupSimpPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public ProteinGroupSimpFrame() {
		initComponents();
	}

	private void initComponents() {
    	setTitle("Protein Group Simplifier");
    	setResizable(false);
    	setLayout(new GroupLayout());
    	add(getProteinGroupSimpPanel0(), new Constraints(new Bilateral(6, 6, 499), new Leading(6, 10, 6)));
    	setSize(511, 233);
    }

	private ProteinGroupSimpPanel getProteinGroupSimpPanel0() {
    	if (proteinGroupSimpPanel0 == null) {
    		proteinGroupSimpPanel0 = new ProteinGroupSimpPanel();
    	}
    	return proteinGroupSimpPanel0;
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
				ProteinGroupSimpFrame frame = new ProteinGroupSimpFrame();
				frame
				        .setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("ProteinGroupSimpFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
