/* 
 ******************************************************************************
 * File: CriteriaFrame.java * * * Created on 04-14-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * The frame of the crieria panel
 * 
 * @author Xinning
 * @version 0.1, 04-14-2009, 20:05:53
 */
public class CriteriaFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private CriteriaPanel criteriaPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public CriteriaFrame() {
		initComponents();
	}

	private void initComponents() {
    	setTitle("Set filtering criteria");
    	setResizable(false);
    	setLayout(new GroupLayout());
    	add(getCriteriaPanel0(), new Constraints(new Bilateral(6, 6, 618), new Bilateral(22, 10, 10)));
    	setSize(630, 315);
    }

	private CriteriaPanel getCriteriaPanel0() {
    	if (criteriaPanel0 == null) {
    		criteriaPanel0 = new CriteriaPanel();
    		criteriaPanel0.setPreferredSize(new Dimension(618, 222));
    	}
    	return criteriaPanel0;
    }

	/**
	 * Set the peptide type combobox selectable
	 * 
	 * @param selectable
	 */
	public void setPeptideTypeSelectable(boolean selectable) {
		this.getCriteriaPanel0().setPeptideTypeSelectable(selectable);
	}
	

	/**
	 * Select the criteria panel for the peptide type
	 * 
	 * @param type
	 */
	public void selectCriteriaPanel(PeptideType type) {
		this.getCriteriaPanel0().selectCriteriaPanel(type);
	}
	
	/**
	 * Select the SFOER criteria panel
	 * 
	 * @param xcorrs
	 * @param dcns
	 * @param sps
	 * @param rsps
	 */
	public void selectCriteriaPanel(float[] xcorrs, float[] dcns, float[] sps, short[] rsps, float[] deltaMSppms) {
		this.getCriteriaPanel0().selectCriteriaPanel(xcorrs, dcns, sps, rsps, deltaMSppms);
	}
	
	/**
	 * Return the current showing and set criteria in the criteria panel
	 * 
	 * @return
	 */
	public IPeptideCriteria getCriteria() {
		return this.getCriteriaPanel0().getCriteria();
	}
	
	/**
	 * Add the action of the filter button
	 * 
	 * @param listener
	 */
	public void addFilterActionListener(ActionListener listener) {
		this.getCriteriaPanel0().addFilterActionListener(listener);
	}
	
	/**
	 * Add the action of SFOER button
	 * 
	 * @param listener
	 */
	public void addSFOERActionListener(ISFOERListener listener) {
		this.getCriteriaPanel0().addSFOERActionListener(listener);
	}
	
	
	/**
	 * Add the action of the filter button
	 * 
	 * @param listener
	 */
	public void removeFilterActionListener(ActionListener listener) {
		this.getCriteriaPanel0().removeFilterActionListener(listener);
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
				CriteriaFrame frame = new CriteriaFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("CriteriaFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
