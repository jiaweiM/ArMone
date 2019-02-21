/* 
 ******************************************************************************
 * File:SFOERMascotCriteria.java * * * Created on 2011-9-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.SFOERMascotCriteria;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.ICriteriaSetter;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import cn.ac.dicp.gp1809.util.gui.UIutilities;

//VS4E -- DO NOT REMOVE THIS LINE!
public class SFOERMascotCriteriaPanel extends JPanel implements
	ActionListener, ICriteriaSetter<IMascotPeptide> {

	private static final long serialVersionUID = 1L;

	private static final DecimalFormat DF2 = DecimalFormats.DF0_2;	
	private static final DecimalFormat DFE4 = DecimalFormats.DF_E4;
	
	private JTable jTableCriteria;
	private JScrollPane jScrollPane0;
	
	private JMenuItem jMenuItemExport;
	private MyJFileChooser chooser;
	
	private float[] ionScore;
	private float[] deltaIS;
	private float[] IS_MHT;
	private float[] IS_MIT;
	private double[] evalue;

	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public SFOERMascotCriteriaPanel() {
		initComponents();
	}

	private void initComponents() {
    	setBorder(BorderFactory.createTitledBorder(null, "Optimized filters by SFOER", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif",
    			Font.BOLD, 12), new Color(59, 59, 59)));
    	setMinimumSize(new Dimension(391, 145));
    	setPreferredSize(new Dimension(391, 145));
    	setLayout(new GroupLayout());
    	add(getJScrollPane0(), new Constraints(new Bilateral(0, 0, 25), new Bilateral(0, 0, 29, 406)));
    	setSize(585, 145);
    }

	private JScrollPane getJScrollPane0() {
    	if (jScrollPane0 == null) {
    		jScrollPane0 = new JScrollPane();
    		jScrollPane0.setViewportView(getJTableCriteria());
    	}
    	return jScrollPane0;
    }

	private JTable getJTableCriteria() {
    	if (jTableCriteria == null) {
    		jTableCriteria = new JTable();
    		JPopupMenu menu = UIutilities.setPopupMenu(jTableCriteria);
    		menu.add(getJMenuItemExport());
    		DefaultTableModel model = new DefaultTableModel(new String[] {"charge", "Ion score", "IS-MHT", "IS-MIT", "Evalue"}, 4);
    		jTableCriteria.setModel(model);
    		jTableCriteria.setValueAt(1, 0, 0);
    		jTableCriteria.setValueAt(2, 1, 0);
    		jTableCriteria.setValueAt(3, 2, 0);
    		jTableCriteria.setValueAt(">=4", 3, 0);
    		
    		this.showFilters();
    	}
    	return jTableCriteria;
    }
	
	private JMenuItem getJMenuItemExport() {
		if(jMenuItemExport == null) {
			jMenuItemExport = new JMenuItem("Export filters ...");
			jMenuItemExport.addActionListener(this);
		}
		
		return this.jMenuItemExport;
	}
	
	private MyJFileChooser getJFileChooser() {
		if(this.chooser ==null) {
			this.chooser = new MyJFileChooser();
			this.chooser.setFileFilter(new String[] {"txt"}, "plan text file (*.txt)");
		}
		
		return this.chooser;
	}
	
	/**
	 * Must be with lenght of 4
	 * 
	 * @param ionScore
	 * @param deltaIS
	 * @param IS_MHT
	 * @param IS_MIT
	 */
	public void setCriteira(float[] ionScore, float[] IS_MHT, float[] IS_MIT, double [] evalue) {
		
		if(ionScore != null && ionScore.length!=4)
			throw new IllegalArgumentException("The length of filters must equal to 4");
		
//		if(deltaIS != null && deltaIS.length!=4)
//			throw new IllegalArgumentException("The length of filters must equal to 4");
		
		if(IS_MHT != null && IS_MHT.length!=4)
			throw new IllegalArgumentException("The length of filters must equal to 4");
		
		if(IS_MIT != null && IS_MIT.length!=4)
			throw new IllegalArgumentException("The length of filters must equal to 4");
		
		if(evalue !=null && evalue.length!=4)
			throw new IllegalArgumentException("The length of filters must equal to 4");
	
		
		this.ionScore = ionScore;
//		this.deltaIS = deltaIS;
		this.IS_MHT = IS_MHT;
		this.IS_MIT = IS_MIT;
		this.evalue = evalue;

		this.showFilters();
	}
	
	private void showFilters() {
		
		float[] ionScore = this.ionScore == null ? new float[4] : this.ionScore;
//		float[] deltaIS = this.deltaIS == null ? new float[4] : this.deltaIS;
		float[] IS_MHT = this.IS_MHT == null ? new float[4] : this.IS_MHT;
		float[] IS_MIT = this.IS_MIT == null ? new float[4] : this.IS_MIT;
		double[] evalue = this.evalue ==null ? new double[4] : this.evalue;

		for(int i=0; i< 4; i++)
			this.getJTableCriteria().setValueAt(DF2.format(ionScore[i]), i, 1);
		
//		for(int i=0; i< 4; i++)
//			this.getJTableCriteria().setValueAt(DF3.format(deltaIS[i]), i, 2);
		
		for(int i=0; i< 4; i++)
			this.getJTableCriteria().setValueAt(DF2.format(IS_MHT[i]), i, 2);
		
		for(int i=0; i< 4; i++)
			this.getJTableCriteria().setValueAt(DF2.format(IS_MIT[i]), i, 3);
		
		for(int i=0; i< 4; i++)
			this.getJTableCriteria().setValueAt(DFE4.format(evalue[i]), i, 4);

	}
	
	/**
	 * Save the filters to file
	 * @throws FileNotFoundException 
	 */
	private void saveFilters(String path) throws FileNotFoundException {
		float[] ionScore = this.ionScore == null ? new float[4] : this.ionScore;
//		float[] deltaIS = this.deltaIS == null ? new float[4] : this.deltaIS;
		float[] IS_MHT = this.IS_MHT == null ? new float[4] : this.IS_MHT;
		float[] IS_MIT = this.IS_MIT == null ? new float[4] : this.IS_MIT;
		double[] evalue = this.evalue == null ? new double[4] : this.evalue;
		
		PrintWriter pw = new PrintWriter(path);
		pw.println("charge\tIonScore\tIS-MHT\tIS-MIT\tEvalue");
		for(int i=0; i< 4; i++) {
			pw.print(i==3 ? ">=4+":(i+1)+"+");
			pw.print("\t");
			pw.print(DF2.format(ionScore[i]));
			pw.print("\t");
//			pw.print(DF3.format(deltaIS[i]));
//			pw.print("\t");
			pw.print(DF2.format(IS_MHT[i]));
			pw.print("\t");
			pw.print(IS_MIT[i]);
			pw.print("\t");
			pw.print(DFE4.format(evalue[i]));
			pw.println();
		}
		pw.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.gui.ICriteriaSetter#getCriteria()
	 */
	@Override
    public IPeptideCriteria<IMascotPeptide> getCriteria() {
	    return new SFOERMascotCriteria(this.ionScore, this.IS_MHT, this.IS_MIT, this.evalue);
    }

	@Override
    public void actionPerformed(ActionEvent arg0) {
		Object obj = arg0.getSource();
		if(obj == this.getJMenuItemExport()) {
			int val = this.getJFileChooser().showSaveDialog(this);
			if(val == JFileChooser.APPROVE_OPTION) {
				String path = this.getJFileChooser().getSelectedFile().getPath();
				try {
	                this.saveFilters(path);
                } catch (FileNotFoundException e) {
	                throw new RuntimeException(e);
                }
			}
		}
		
		
		
    }
}
