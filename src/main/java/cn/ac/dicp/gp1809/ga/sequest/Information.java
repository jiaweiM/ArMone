/*
 * *****************************************************************************
 * File: Information.java * * * Created on 11-24-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.sequest;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import cn.ac.dicp.gp1809.util.gui.UIutilities;

/**
 * The copy right panel
 * 
 * @author Xinning
 * @version 0.2, 11-24-2008, 14:01:32
 */
class Information extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JTextArea jTextArea = null;

	/**
	 * This is the default constructor
	 */
	public Information() {
		this(null);
	}
	
	/**
	 * Call this frame from a parent component
	 * @param parent
	 */
	public Information(JFrame parent){
		super(parent,true);
		initialize();
		this.setLocation(UIutilities.getProperLocation(parent, this));
		this.setVisible(true);
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(500, 480);
		this.setContentPane(getJContentPane());
		this.setTitle("About SFOER");
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			
			JScrollPane scroll = getJScrollPane();
			scroll.setSize(new Dimension(493, 475));
			jContentPane.add(scroll);
		}
		return jContentPane;
	}

	private JScrollPane jscrollpane = null;
	private JScrollPane getJScrollPane(){
		if(jscrollpane==null){
			this.jscrollpane = new JScrollPane();
			jscrollpane.setViewportView(getJTextArea());
		}
		return this.jscrollpane;
	}
	
	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setEditable(false);
			jTextArea.setText("\n[Software Information]\n"+
					"Name\t: SFOER v2\n"+
					"Version\t:2.3.2 (8-7-2009)\n"+
					"Author\t: Xinning Jiang(vext@163.com)\n"+
					"Contact\t: Prof. Hanfa Zou (hanfazou@dicp.ac.cn)\n" +
					"       \t  Prof. Mingliang Ye(mingliang@dicp.ac.cn)\n"+
					"Address\t:Dalian Institute of Chemical Physics\n"+
					"\t 457 zhongshan Road, Dalian 116023, China\n"+
					"Homepage\t: http://bioanalysis.dicp.ac.cn/proteomics/sofware/SFOER.html\n" +
					"Citation\t: Jiang, X.N.; Jiang, X.G.; Han, G.H.; Ye, M.L.; Zou, H.F., Optimization\r\n" +
					"\tof filtering criterion for SEQUEST database searching to improve\r\n" +
					"\tproteome coverage in shotgun proteomic. BMC Bioinformatics,\n" +
					"\t2007 8:323.\n\n"+
					
					
					"[Introduction]\n" +
					"SFOER (Sequest Filter Optimizer by genetic algorithm) is used for the optimization of\n" +
					"filtering criteria for the peptide identifications assigned by SEQUEST. It takes Xcorr,\n" +
					"DeltaCn, Sp and Rsp as weight and Number of peptide identified at specified \n" +
					"false-discovery rate (FDR) as fitness. After the optimization, the outputed criteria are the\n" +
					"optimized cirteria which can result in most peptide identification at current FDR. And the\n" +
					"FDR of the indentification is calculated using the decoy database searching approach.\n" +
					
					"\n[Licence]\n" +
					"This software is distributed under GNU general publication licence (GPL).");
		}
		return jTextArea;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
