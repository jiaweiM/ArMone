/* 
 ******************************************************************************
 * File: CriteriaPanel2.java * * * Created on 08-08-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import javax.swing.GroupLayout.Alignment;

/**
 * 
 * @author Xinning
 * @version 0.1, 08-08-2009, 19:01:42
 */
public class CriteriaPanel2 extends JPanel {

	private static final long serialVersionUID = 1L;
	private SequestCriteriaPanel sequestCriteriaPanel0;
	private JPanel jPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public CriteriaPanel2() {
		initComponents();
	}

	private void initComponents() {
    	setSize(599, 220);
    	javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 587, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(63)
    				.addComponent(getJPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	setLayout(groupLayout);
    }

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		javax.swing.GroupLayout gl_jPanel0 = new javax.swing.GroupLayout(jPanel0);
    		gl_jPanel0.setHorizontalGroup(
    			gl_jPanel0.createParallelGroup(Alignment.LEADING)
    				.addComponent(getSequestCriteriaPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)
    		);
    		gl_jPanel0.setVerticalGroup(
    			gl_jPanel0.createParallelGroup(Alignment.LEADING)
    				.addComponent(getSequestCriteriaPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    		);
    		jPanel0.setLayout(gl_jPanel0);
    	}
    	return jPanel0;
    }

	private SequestCriteriaPanel getSequestCriteriaPanel0() {
    	if (sequestCriteriaPanel0 == null) {
    		sequestCriteriaPanel0 = new SequestCriteriaPanel();
    		sequestCriteriaPanel0.setBorder(BorderFactory.createTitledBorder(null, "SEQUEST filters", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font(
    				"Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
    		sequestCriteriaPanel0.setMinimumSize(new Dimension(535, 145));
    		sequestCriteriaPanel0.setPreferredSize(new Dimension(535, 145));
    	}
    	return sequestCriteriaPanel0;
    }

}
