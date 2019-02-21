/* 
 ******************************************************************************
 * File: DbDecoyPanel.java * * * Created on 06-04-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.dbdecoy;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

/**
 * 
 * @author Xinning
 * @version 0.1, 06-04-2009, 20:48:05
 */
public class DbDecoyPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField jTextFieldOriginalDB;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JButton jButtonOriginDB;
	private JProgressBar jProgressBar0;
	private JButton jButtonGO;
	private JPanel jPanel0;
	private JTextField jTextFieldCombinedDB;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public DbDecoyPanel() {
		initComponents();
	}

	private void initComponents() {
    	setLayout(new GroupLayout());
    	add(getJLabel1(), new Constraints(new Leading(6, 83, 6, 6), new Leading(78, 6, 6)));
    	add(getJButtonOriginDB(), new Constraints(new Leading(448, 10, 10), new Leading(35, 28, 6, 6)));
    	add(getJProgressBar0(), new Constraints(new Bilateral(0, 0, 10), new Leading(119, 10, 10)));
    	add(getJPanel0(), new Constraints(new Bilateral(8, 9, 0), new Leading(150, 31, 10, 10)));
    	add(getJTextFieldCombinedDB(), new Constraints(new Leading(95, 340, 6, 6), new Leading(72, 25, 6, 6)));
    	add(getJLabel0(), new Constraints(new Leading(31, 10, 10), new Leading(42, 6, 6)));
    	add(getJTextFieldOriginalDB(), new Constraints(new Leading(94, 342, 6, 6), new Leading(37, 25, 10, 10)));
    	setSize(511, 191);
    }

	private JTextField getJTextFieldOriginalDB() {
    	if (jTextFieldOriginalDB == null) {
    		jTextFieldOriginalDB = new JTextField();
    	}
    	return jTextFieldOriginalDB;
    }

	private JTextField getJTextFieldCombinedDB() {
    	if (jTextFieldCombinedDB == null) {
    		jTextFieldCombinedDB = new JTextField();
    	}
    	return jTextFieldCombinedDB;
    }

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
    		jPanel0.add(getJButtonGO());
    	}
    	return jPanel0;
    }

	private JButton getJButtonGO() {
    	if (jButtonGO == null) {
    		jButtonGO = new JButton();
    		jButtonGO.setText("  Go!  ");
    	}
    	return jButtonGO;
    }

	private JProgressBar getJProgressBar0() {
    	if (jProgressBar0 == null) {
    		jProgressBar0 = new JProgressBar();
    	}
    	return jProgressBar0;
    }

	private JButton getJButtonOriginDB() {
    	if (jButtonOriginDB == null) {
    		jButtonOriginDB = new JButton();
    		jButtonOriginDB.setText("...");
    	}
    	return jButtonOriginDB;
    }

	private JLabel getJLabel1() {
    	if (jLabel1 == null) {
    		jLabel1 = new JLabel();
    		jLabel1.setText("Combined DB");
    	}
    	return jLabel1;
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("Origin DB");
    	}
    	return jLabel0;
    }

}
