/* 
 ******************************************************************************
 * File: PepListInfoFrame.java * * * Created on 04-14-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

/**
 * 
 * @author Xinning
 * @version 0.1, 04-14-2009, 16:26:38
 */
public class PepListInfoFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private PeptideListPagedRowGettor getter;

	private PepListInfoPanel pepListInfoPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PepListInfoFrame() {
		initComponents();
		setResizable(false);
	}

	public PepListInfoFrame(PeptideListPagedRowGettor getter) {
		this.getter = getter;
		initComponents();
		setResizable(false);
	}

	private void initComponents() {
    	setTitle("Peptides information");
    	setResizable(false);
    	setLayout(new GroupLayout());
    	add(getPepListInfoPanel0(), new Constraints(new Bilateral(6, 6, 497), new Bilateral(6, 6, 10, 135)));
    	setSize(519, 160);
    }

	private PepListInfoPanel getPepListInfoPanel0() {
		if (pepListInfoPanel0 == null) {
			pepListInfoPanel0 = new PepListInfoPanel();
			pepListInfoPanel0.setMinimumSize(new Dimension(659, 122));
			pepListInfoPanel0.setPreferredSize(new Dimension(659, 122));
			pepListInfoPanel0.setLayout(new FlowLayout(FlowLayout.LEADING));

			if (this.getter != null)
				pepListInfoPanel0.loadPeplistInfo(this.getter.getPeptideInfo());
		}
		return pepListInfoPanel0;
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
	 * Reload the peptide information
	 * 
	 */
	public void reLoadPeptideInfo() {
		if (this.getter != null) {
			this.getPepListInfoPanel0().loadPeplistInfo(
			        this.getter.getPeptideInfo());
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
				PepListInfoFrame frame = new PepListInfoFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("PepListInfoFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
