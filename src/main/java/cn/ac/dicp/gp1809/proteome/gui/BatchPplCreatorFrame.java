/* 
 ******************************************************************************
 * File: BatchPplCreatorFrame2.java * * * Created on 06-04-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import javax.swing.JFrame;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

/**
 * 
 * @author Xinning
 * @version 0.1, 06-04-2009, 22:06:54
 */
public class BatchPplCreatorFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private BatchPplCreatorPanel batchPplCreatorPanel20;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public BatchPplCreatorFrame() {
		initComponents();
	}

	private void initComponents() {
    	setTitle("Peptide list creator");
    	setLayout(new GroupLayout());
    	add(getBatchPplCreatorPanel20(), new Constraints(new Bilateral(6, 6, 1088), new Bilateral(6, 6, 10)));
    	setSize(1100, 615);
    }

	private BatchPplCreatorPanel getBatchPplCreatorPanel20() {
    	if (batchPplCreatorPanel20 == null) {
    		batchPplCreatorPanel20 = new BatchPplCreatorPanel();
    	}
    	return batchPplCreatorPanel20;
    }

}
