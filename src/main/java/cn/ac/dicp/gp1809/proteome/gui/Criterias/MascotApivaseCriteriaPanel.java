/* 
 ******************************************************************************
 * File: MascotApivaseCriteriaPanel.java * * * Created on 04-09-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IMascotPhosphoPeptidePair;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.phosphorylation.DefaultMascotPhosPairCriteria;
import javax.swing.GroupLayout.Alignment;

/**
 * Criteria panel for filtering of IMascotPhosphoPeptidePair peptides
 * 
 * @author Xinning
 * @version 0.1, 04-09-2009, 21:16:45
 */
public class MascotApivaseCriteriaPanel extends JPanel implements
        ICriteriaSetter<IMascotPhosphoPeptidePair> {

	private static final long serialVersionUID = 1L;
	private JFormattedTextField jFormattedTextFieldIonscore;
	private JLabel jLabel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public MascotApivaseCriteriaPanel() {
		initComponents();
	}

	private void initComponents() {
    	setBorder(BorderFactory.createTitledBorder(null, "Mascot filters (MS2/MS3 target-decoy strategy)", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font(
    			"SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
    	setMinimumSize(new Dimension(391, 145));
    	setPreferredSize(new Dimension(391, 145));
    	setSize(585, 145);
    	javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJLabel0())
    				.addGap(16)
    				.addComponent(getJFormattedTextFieldIonscore(), javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(9)
    				.addComponent(getJLabel0()))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJFormattedTextFieldIonscore(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	setLayout(groupLayout);
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("Ion score'");
    	}
    	return jLabel0;
    }

	private JFormattedTextField getJFormattedTextFieldIonscore() {
    	if (jFormattedTextFieldIonscore == null) {
    		jFormattedTextFieldIonscore = new JFormattedTextField();
    		jFormattedTextFieldIonscore.setText("40");
    		jFormattedTextFieldIonscore.setMinimumSize(new Dimension(40, 25));
    		jFormattedTextFieldIonscore.setPreferredSize(new Dimension(40, 25));
    	}
    	return jFormattedTextFieldIonscore;
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
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("SequestCriteriaPanel");
				MascotApivaseCriteriaPanel content = new MascotApivaseCriteriaPanel();
				content.setPreferredSize(content.getSize());
				frame.getContentPane().add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.gui.ICriteriaSetter#getCriteria()
	 */
	@Override
    public IPeptideCriteria<IMascotPhosphoPeptidePair> getCriteria() {
	    return new DefaultMascotPhosPairCriteria(Float.parseFloat(this.jFormattedTextFieldIonscore.getText()));
    }
}
