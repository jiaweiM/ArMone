/* 
 ******************************************************************************
 * File: SequestCriteriaPanel.java * * * Created on 04-09-2009
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

import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.SFOERSequestCriteria;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import cn.ac.dicp.gp1809.util.gui.UIutilities;
import javax.swing.GroupLayout.Alignment;

/**
 * Criteria panel for filtering of sequest peptides
 * 
 * @author Xinning
 * @version 0.2.1, 07-28-2010, 19:33:57
 */
public class SFOERSequestCriteriaPanel extends JPanel implements
        ActionListener, ICriteriaSetter<ISequestPeptide> {

	private static final long serialVersionUID = 1L;
	
	private static final DecimalFormat DF1 = DecimalFormats.DF0_1;
	private static final DecimalFormat DF2 = DecimalFormats.DF0_2;
	private static final DecimalFormat DF3 = DecimalFormats.DF0_3;
	
	private JTable jTableCriteria;
	private JScrollPane jScrollPane0;
	
	private JMenuItem jMenuItemExport;
	private MyJFileChooser chooser;
	
	private float[] xcorrs;
	private float[] dcns;
	private float[] sps;
	private short[] rsps;
	private float[] deltaMSppms;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public SFOERSequestCriteriaPanel() {
		initComponents();
	}

	private void initComponents() {
    	setBorder(BorderFactory.createTitledBorder(null, "Optimized filters by SFOER", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif",
    			Font.BOLD, 12), new Color(59, 59, 59)));
    	setMinimumSize(new Dimension(391, 145));
    	setPreferredSize(new Dimension(391, 145));
    	setSize(585, 145);
    	javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addComponent(getJScrollPane0(), javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addComponent(getJScrollPane0(), javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
    	);
    	setLayout(groupLayout);
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
    		DefaultTableModel model = new DefaultTableModel(new String[] {"charge", "Xcorr", "DeltaCn", "Sp", "Rsp"}, 4);
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
	 * @param xcorrs
	 * @param dcns
	 * @param sps
	 * @param rsps
	 */
	public void setCriteira(float[] xcorrs, float[] dcns, float[] sps, short[] rsps) {
		
		if(xcorrs != null && xcorrs.length!=4)
			throw new IllegalArgumentException("The length of filters must equal to 4");
		
		if(dcns != null && dcns.length!=4)
			throw new IllegalArgumentException("The length of filters must equal to 4");
		
		if(sps != null && sps.length!=4)
			throw new IllegalArgumentException("The length of filters must equal to 4");
		
		if(rsps != null && rsps.length!=4)
			throw new IllegalArgumentException("The length of filters must equal to 4");
	
		
		this.xcorrs = xcorrs;
		this.dcns = dcns;
		this.sps = sps;
		this.rsps = rsps;

		this.showFilters();
	}
	
	private void showFilters() {
		
		float[] xcorrs = this.xcorrs == null ? new float[4] : this.xcorrs;
		float[] dcns = this.dcns == null ? new float[4] : this.dcns;
		float[] sps = this.sps == null ? new float[4] : this.sps;
		short[] rsps = this.rsps == null ? new short[4] : this.rsps;

		for(int i=0; i< 4; i++)
			this.getJTableCriteria().setValueAt(DF3.format(xcorrs[i]), i, 1);
		
		for(int i=0; i< 4; i++)
			this.getJTableCriteria().setValueAt(DF3.format(dcns[i]), i, 2);
		
		for(int i=0; i< 4; i++)
			this.getJTableCriteria().setValueAt(DF2.format(sps[i]), i, 3);
		
		for(int i=0; i< 4; i++)
			this.getJTableCriteria().setValueAt(rsps[i], i, 4);

	}
	
	/**
	 * Save the filters to file
	 * @throws FileNotFoundException 
	 */
	private void saveFilters(String path) throws FileNotFoundException {
		float[] xcorrs = this.xcorrs == null ? new float[4] : this.xcorrs;
		float[] dcns = this.dcns == null ? new float[4] : this.dcns;
		float[] sps = this.sps == null ? new float[4] : this.sps;
		short[] rsps = this.rsps == null ? new short[4] : this.rsps;

		PrintWriter pw = new PrintWriter(path);
		pw.println("charge\tXcorr\tDeltaCn\tSp\tRsp");
		for(int i=0; i< 4; i++) {
			pw.print(i==3 ? ">=4+":(i+1)+"+");
			pw.print("\t");
			pw.print(DF3.format(xcorrs[i]));
			pw.print("\t");
			pw.print(DF3.format(dcns[i]));
			pw.print("\t");
			pw.print(DF2.format(sps[i]));
			pw.print("\t");
			pw.print(rsps[i]);
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
    public IPeptideCriteria<ISequestPeptide> getCriteria() {
	    return new SFOERSequestCriteria(this.xcorrs, this.dcns, this.sps, this.rsps);
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
