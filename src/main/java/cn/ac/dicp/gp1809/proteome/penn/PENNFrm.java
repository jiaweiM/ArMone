/* 
 ******************************************************************************
 * File: PENNFrm.java * * * Created on 08-08-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.penn;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import javax.swing.GroupLayout.Alignment;

/**
 * 
 * @author Xinning
 * @version 0.1, 08-08-2009, 17:45:37
 */
public class PENNFrm extends JFrame {

	private static final long serialVersionUID = 1L;
	private PENNMainPanel pENNMainPanel0;
	private PENNSimPanel pENNSimPanel0;
	private JTabbedPane jTabbedPane0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PENNFrm() {
		initComponents();
	}
	private void initComponents() {
    	setTitle("PENN in ArMone");
    	setResizable(false);
    	javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(getContentPane());
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJTabbedPane0(), javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJTabbedPane0(), javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	getContentPane().setLayout(groupLayout);
    	setSize(536, 363);
    }
	private JTabbedPane getJTabbedPane0() {
    	if (jTabbedPane0 == null) {
    		jTabbedPane0 = new JTabbedPane();
    		jTabbedPane0.addTab("PENN", getPENNMainPanel0());
    		jTabbedPane0.addTab("Sim Score", getPENNSimPanel0());
    	}
    	return jTabbedPane0;
    }
	private PENNMainPanel getPENNMainPanel0() {
    	if (pENNMainPanel0 == null) {
    		pENNMainPanel0 = new PENNMainPanel();
    	}
    	return pENNMainPanel0;
    }
	
	private PENNSimPanel getPENNSimPanel0() {
    	if (pENNSimPanel0 == null) {
    		pENNSimPanel0 = new PENNSimPanel();
    	}
    	return pENNSimPanel0;
    }
	
	public static void main(String[] args) {
		new PENNFrm().setVisible(true);
	}

}
