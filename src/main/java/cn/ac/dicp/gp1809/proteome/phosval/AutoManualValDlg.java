/* 
 ******************************************************************************
 * File: AutoManualValDlg.java * * * Created on 05-06-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.phosval;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

import cn.ac.dicp.gp1809.proteome.gui.PeptideListPagedRowGettor;
import cn.ac.dicp.gp1809.proteome.gui.PeptideListViewer;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-06-2009, 19:13:52
 */
public class AutoManualValDlg extends JDialog {

	private static final long serialVersionUID = 1L;
	private PeptideListViewer viewerFrame;
	private PeptideListPagedRowGettor getter;

	private AutoManualValPanel autoManualValPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public AutoManualValDlg() {
		initComponents();
	}

	public AutoManualValDlg(PeptideListViewer viewerFrame,
	        PeptideListPagedRowGettor getter) {
		super(viewerFrame, true);
		
		this.viewerFrame = viewerFrame;
		this.getter = getter;

		initComponents();
	}

	private void initComponents() {
    	setTitle("Auto filtering");
    	setFont(new Font("Dialog", Font.PLAIN, 12));
    	setBackground(new Color(204, 232, 207));
    	setForeground(Color.black);
    	setLayout(new GroupLayout());
    	add(getAutoManualValPanel0(), new Constraints(new Bilateral(7, 6, 525), new Bilateral(7, 6, 10)));
    	setSize(538, 374);
    }

	private AutoManualValPanel getAutoManualValPanel0() {
		if (autoManualValPanel0 == null) {
			autoManualValPanel0 = new AutoManualValPanel(this.viewerFrame,
			        this.getter);
		}
		return autoManualValPanel0;
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
	 * Main entry of the class. Note: This class is only created so that you can
	 * easily preview the result at runtime. It is not expected to be managed by
	 * the designer. You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				AutoManualValDlg frame = new AutoManualValDlg();
				frame
				        .setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
