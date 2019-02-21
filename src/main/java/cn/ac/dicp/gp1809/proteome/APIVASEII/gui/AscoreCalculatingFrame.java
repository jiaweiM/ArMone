/* 
 ******************************************************************************
 * File: AscoreCalculatingFrame.java * * * Created on 06-14-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.gui;

import javax.swing.JFrame;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;

/**
 * The calculating of ascore for all the peptides from the reader.
 * 
 * @author Xinning
 * @version 0.1, 06-14-2009, 15:17:52
 */
public class AscoreCalculatingFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private IPeptideListReader reader;
	
	private AscoreCalculatingPanel ascoreCalculatingPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public AscoreCalculatingFrame() {
		initComponents();
	}
	
	public AscoreCalculatingFrame(IPeptideListReader reader) {
		this.reader = reader;
		
		initComponents();
	}
	
	private void initComponents() {
    	setTitle("Phosphorylation site localization");
    	setLayout(new GroupLayout());
    	add(getAscoreCalculatingPanel0(), new Constraints(new Bilateral(6, 6, 589), new Bilateral(6, 6, 10)));
    	setSize(601, 275);
    	setResizable(false);
    }
	private AscoreCalculatingPanel getAscoreCalculatingPanel0() {
    	if (ascoreCalculatingPanel0 == null) {
    		ascoreCalculatingPanel0 = new AscoreCalculatingPanel(reader);
    	}
    	return ascoreCalculatingPanel0;
    }

}
