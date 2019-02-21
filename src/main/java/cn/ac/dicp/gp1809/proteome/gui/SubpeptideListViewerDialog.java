/* 
 ******************************************************************************
 * File: SubpeptideListViewerDialog.java * * * Created on 05-22-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-22-2009, 20:53:04
 */
public class SubpeptideListViewerDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private PeptideListPagedRowGettor getter;
	
	private SubPeptideListViewerPanel subPeptideListViewerPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public SubpeptideListViewerDialog() {
		initComponents();
	}

	public SubpeptideListViewerDialog(JFrame parent, PeptideListPagedRowGettor getter) {
		super(parent, true);
		this.getter = getter;
		
		initComponents();
	}

	private void initComponents() {
    	setTitle("Peptide viewer");
    	setFont(new Font("Dialog", Font.PLAIN, 12));
    	setBackground(new Color(204, 232, 207));
    	setForeground(Color.black);
    	setLayout(new GroupLayout());
    	add(getSubPeptideListViewerPanel0(), new Constraints(new Bilateral(6, 6, 872), new Bilateral(6, 6, 10, 506)));
    	setSize(884, 518);
    }

	private SubPeptideListViewerPanel getSubPeptideListViewerPanel0() {
    	if (subPeptideListViewerPanel0 == null) {
    		subPeptideListViewerPanel0 = new SubPeptideListViewerPanel(getter);
    		subPeptideListViewerPanel0.setMinimumSize(new Dimension(862, 500));
    		subPeptideListViewerPanel0.setPreferredSize(new Dimension(862, 515));
    	}
    	return subPeptideListViewerPanel0;
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
				SubpeptideListViewerDialog dialog = new SubpeptideListViewerDialog();
				dialog
				        .setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				dialog.setTitle("SubpeptideListViewerDialog");
				dialog.setLocationRelativeTo(null);
				dialog.getContentPane().setPreferredSize(dialog.getSize());
				dialog.pack();
				dialog.setVisible(true);
			}
		});
	}

}
