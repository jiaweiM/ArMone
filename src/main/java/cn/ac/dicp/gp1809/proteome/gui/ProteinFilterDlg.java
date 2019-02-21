/* 
 ******************************************************************************
 * File: ProteinFilterDlg.java * * * Created on 06-07-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
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

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

/**
 * 
 * @author Xinning
 * @version 0.1, 06-07-2010, 17:06:28
 */
public class ProteinFilterDlg extends JDialog{

	private static final long serialVersionUID = 1L;
	
	private ProteinFilterPanel proteinFilterPanel0;

	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public ProteinFilterDlg() {
		initComponents();
	}

	public ProteinFilterDlg(Frame parent) {
		super(parent);
		initComponents();
	}

	private void initComponents() {
    	setTitle("Protein infering");
    	setFont(new Font("Dialog", Font.PLAIN, 12));
    	setBackground(new Color(204, 232, 207));
    	setResizable(false);
    	setForeground(Color.black);
    	setLayout(new GroupLayout());
    	add(getProteinFilterPanel0(), new Constraints(new Leading(6, 476, 6, 6), new Bilateral(6, 6, 10)));
    	setSize(488, 259);
    }

	private ProteinFilterPanel getProteinFilterPanel0() {
    	if (proteinFilterPanel0 == null) {
    		proteinFilterPanel0 = new ProteinFilterPanel();
    	}
    	return proteinFilterPanel0;
    }
}
