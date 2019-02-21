/* 
 ******************************************************************************
 * File: ProcessingDlg.java * * * Created on 04-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * No ti
 * 
 * @author Xinning
 * @version 0.1, 04-12-2009, 20:35:05
 */
public class ProcessingDlg extends JDialog {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel0;

	public ProcessingDlg() {
		initComponents();
	}

	public ProcessingDlg(Component parent) {
		super(UIutilities.getFrameForComponent(parent), true);
		initComponents();
		this.setUndecorated(true);
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}

	private void initComponents() {
    	add(getJLabel0(), BorderLayout.CENTER);
    	setSize(393, 78);
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setHorizontalAlignment(SwingConstants.CENTER);
    		jLabel0.setText("Processing ...");
    	}
    	return jLabel0;
    }

}
