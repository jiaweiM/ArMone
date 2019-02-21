/* 
 ******************************************************************************
 * File: PeakListViewerMS3Frame.java * * * Created on 06-09-2009
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

import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair;
import cn.ac.dicp.gp1809.proteome.drawjf.SpectrumMatchDatasetConstructor;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;

/**
 * 
 * @author Xinning
 * @version 0.1, 06-09-2009, 22:52:56
 */
public class PeakListViewerMS3Frame extends JFrame {

	private static final long serialVersionUID = 1L;
	private PeakListViewerMS3Panel peakListViewerMS3Panel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public PeakListViewerMS3Frame() {
		initComponents();
	}

	private void initComponents() {
		setTitle("MS3 Spectrum viewer");
		setLayout(new GroupLayout());
		add(getPeakListViewerMS3Panel0(), new Constraints(new Bilateral(6, 6,
		        797), new Bilateral(6, 6, 10, 413)));
		setSize(809, 425);
	}

	private PeakListViewerMS3Panel getPeakListViewerMS3Panel0() {
		if (peakListViewerMS3Panel0 == null) {
			peakListViewerMS3Panel0 = new PeakListViewerMS3Panel();
			peakListViewerMS3Panel0.setPreferredSize(new Dimension(797, 452));
		}
		return peakListViewerMS3Panel0;
	}

	/**
	 * Draw the spectrum for the scan, if the peak list getter existed, use it
	 * first
	 * 
	 * @param dataset
	 */
	void drawMatchedSpectrum(SpectrumMatchDatasetConstructor constructor,
	        IPhosPeptidePair peptide, IMS2PeakList[] peaklists, int[] types,
	        boolean ismono, ISpectrumThreshold threshold) {
		this.getPeakListViewerMS3Panel0().drawMatchedSpectrum(constructor,
		        peptide, peaklists, types, ismono, threshold);
	}

	/**
	 * Redraw for the previous information. Currently only for matched spectrum
	 */
	void reDrawMatchedSpectrum() {
		this.getPeakListViewerMS3Panel0().reDrawMatchedSpectrum();
	}

}
