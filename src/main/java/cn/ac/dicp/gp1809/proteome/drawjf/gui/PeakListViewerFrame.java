/* 
 ******************************************************************************
 * File: PeakListViewerFrame.java * * * Created on 04-14-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.gui;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.drawjf.SpectrumMatchDatasetConstructor;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;

/**
 * The frame of peak list viewer
 * 
 * @author Xinning
 * @version 0.1, 04-14-2009, 19:17:46
 */
public class PeakListViewerFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private boolean isMS3Selectable;
	private PeakListViewerPanel peakListViewerPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public PeakListViewerFrame() {
		initComponents();
	}
	
	public PeakListViewerFrame(boolean isMS3Selectable) {
		this.isMS3Selectable = isMS3Selectable;
		initComponents();
	}
	
	public PeakListViewerFrame(SpectrumMatchDatasetConstructor constructor, IPeptide peptide,
	        IMS2PeakList[] peaklists) {
		this(false, constructor, peptide, peaklists);
	}
	
	public PeakListViewerFrame(boolean isMS3Selectable, SpectrumMatchDatasetConstructor constructor, IPeptide peptide,
	        IMS2PeakList[] peaklists) {
		this.isMS3Selectable = isMS3Selectable;
		this.draw(constructor, peptide, peaklists);
		initComponents();
	}

	private void initComponents() {
    	setTitle("Spectrum");
    	setLayout(new GroupLayout());
    	add(getPeakListViewerPanel0(), new Constraints(new Bilateral(6, 6, 773), new Bilateral(6, 6, 10, 477)));
    	setSize(785, 489);
    }

	private PeakListViewerPanel getPeakListViewerPanel0() {
    	if (peakListViewerPanel0 == null) {
    		peakListViewerPanel0 = new PeakListViewerPanel(this.isMS3Selectable);
    		peakListViewerPanel0.setPreferredSize(new Dimension(797, 452));
    	}
    	return peakListViewerPanel0;
    }

	public void draw(SpectrumMatchDatasetConstructor constructor, IPeptide peptide,
	        IMS2PeakList[] peaklists){
		this.getPeakListViewerPanel0().drawMatchedSpectrum(constructor, peptide, peaklists);
	}
	
	/**
	 * Draw the spectrum for the scan
	 * 
	 * @param dataset
	 */
	public void drawMatchedSpectrum(
	        SpectrumMatchDatasetConstructor constructor, IPeptide peptide,
	        IMS2PeakList[] peaklists) {
		this.getPeakListViewerPanel0().drawMatchedSpectrum(constructor,
		        peptide, peaklists);
	}
}
