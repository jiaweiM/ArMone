/* 
 ******************************************************************************
 * File: PhosPeptideListViewer.java * * * Created on 05-21-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.phosval;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-21-2009, 19:34:18
 */
public class PhosPeptideListViewer extends JFrame {

	private static final long serialVersionUID = 1L;
	private PhosPeptideListViewerPanel phosPeptideListViewerPanel0;
	private JMenuItem jMenuItem0;
	private JMenu jMenu0;
	private JMenuBar jMenuBar0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public PhosPeptideListViewer() {
		initComponents();
	}

	private void initComponents() {
    	setLayout(new GroupLayout());
    	add(getPhosPeptideListViewerPanel0(), new Constraints(new Bilateral(6, 6, 874), new Bilateral(6, 6, 10)));
    	setJMenuBar(getJMenuBar0());
    	setSize(886, 584);
    }

	private JMenuBar getJMenuBar0() {
    	if (jMenuBar0 == null) {
    		jMenuBar0 = new JMenuBar();
    		jMenuBar0.add(getJMenu0());
    	}
    	return jMenuBar0;
    }

	private JMenu getJMenu0() {
    	if (jMenu0 == null) {
    		jMenu0 = new JMenu();
    		jMenu0.setText("jMenu0");
    		jMenu0.add(getJMenuItem0());
    	}
    	return jMenu0;
    }

	private JMenuItem getJMenuItem0() {
    	if (jMenuItem0 == null) {
    		jMenuItem0 = new JMenuItem();
    		jMenuItem0.setText("jMenuItem0");
    	}
    	return jMenuItem0;
    }

	private PhosPeptideListViewerPanel getPhosPeptideListViewerPanel0() {
    	if (phosPeptideListViewerPanel0 == null) {
    		phosPeptideListViewerPanel0 = new PhosPeptideListViewerPanel();
    	}
    	return phosPeptideListViewerPanel0;
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
				PhosPeptideListViewer frame = new PhosPeptideListViewer();
				frame
				        .setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("PhosPeptideListViewer");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
