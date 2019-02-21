/* 
 ******************************************************************************
 * File: PeptideStatFrame.java * * * Created on 06-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

/**
 * 
 * @author Xinning
 * @version 0.1, 06-19-2009, 16:32:24
 */
public class PeptideStatFrame extends JDialog {

	private static final long serialVersionUID = 1L;
	private PeptideStatPanel peptideStatPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PeptideStatFrame() {
		initComponents();
	}

	public PeptideStatFrame(Frame parent) {
		super(parent);
		initComponents();
	}

	private void initComponents() {
    	setTitle("Statistic");
    	setFont(new Font("Dialog", Font.PLAIN, 12));
    	setBackground(new Color(204, 232, 207));
    	setForeground(Color.black);
    	setLayout(new GroupLayout());
    	add(getPeptideStatPanel0(), new Constraints(new Bilateral(6, 6, 409), new Bilateral(6, 6, 10, 260)));
    	setSize(421, 272);
    }

	private PeptideStatPanel getPeptideStatPanel0() {
    	if (peptideStatPanel0 == null) {
    		peptideStatPanel0 = new PeptideStatPanel();
    	}
    	return peptideStatPanel0;
    }
	
	/**
	 * The peptide statistic info
	 * 
	 * @param info
	 */
	public void loadPeptideStatInfo(PeptideStatInfo info) {
		this.getPeptideStatPanel0().loadPeptideStatInfo(info);
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
				PeptideStatFrame dialog = new PeptideStatFrame();
				dialog
				        .setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				dialog.setTitle("PeptideStatFrame");
				dialog.setLocationRelativeTo(null);
				dialog.getContentPane().setPreferredSize(dialog.getSize());
				dialog.pack();
				dialog.setVisible(true);
			}
		});
	}

}
