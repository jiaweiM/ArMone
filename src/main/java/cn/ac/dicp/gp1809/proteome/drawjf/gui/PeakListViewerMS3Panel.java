/* 
 ******************************************************************************
 * File: PeakListViewerPanel.java * * * Created on 04-14-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.drawjf.JFreeChartPanel;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair;
import cn.ac.dicp.gp1809.proteome.drawjf.SpectrumMatchDatasetConstructor;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.NeutralLossSettingPanel;

/**
 * The MS3 spectrum viewer panel
 * 
 * @author Xinning
 * @version 0.1, 06-09-2009, 22:26:15
 */
public class PeakListViewerMS3Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	private SpectrumMatchDatasetConstructor constructor;
	private IPhosPeptidePair peptide;
	private IMS2PeakList[] peaklists;
	private ISpectrumThreshold threshold;
	private int[] types;
	private boolean isMono;

	private JFreeChartPanel jFreeChartPanel0;
	private JPanel jPanelNoSpectrum;
	private JLabel jLabelNoSpectrum;
	private NeutralLossSettingPanel neutralLossSettingPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public PeakListViewerMS3Panel() {
		initComponents();
	}

	private void initComponents() {
		setPreferredSize(new Dimension(797, 452));
		setLayout(new GroupLayout());
		add(getJFreeChartPanel0(), new Constraints(new Bilateral(198, 10, 100),
		        new Bilateral(6, 6, 200, 401)));
		add(getJPanelNoSpectrum(), new Constraints(new Bilateral(198, 10, 100),
		        new Bilateral(6, 6, 401, 401)));
		add(getNeutralLossSettingPanel0(), new Constraints(new Leading(6, 182,
		        69, 710), new Leading(6, 6, 6)));
		setSize(797, 413);
	}

	private NeutralLossSettingPanel getNeutralLossSettingPanel0() {
		if (neutralLossSettingPanel0 == null) {
			neutralLossSettingPanel0 = new NeutralLossSettingPanel();
			neutralLossSettingPanel0.addSelectionListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					reDrawMatchedSpectrum();
				}
			});
		}
		return neutralLossSettingPanel0;
	}

	private JFreeChartPanel getJFreeChartPanel0() {
		if (jFreeChartPanel0 == null) {
			jFreeChartPanel0 = new JFreeChartPanel();
			jFreeChartPanel0.setBorder(new LineBorder(Color.black, 1, false));
			jFreeChartPanel0.setVisible(false);
		}
		return jFreeChartPanel0;
	}

	private JPanel getJPanelNoSpectrum() {
		if (jPanelNoSpectrum == null) {
			jPanelNoSpectrum = new JPanel();
			jPanelNoSpectrum.setBorder(new LineBorder(Color.black, 1, false));
			jPanelNoSpectrum.setLayout(new BorderLayout());
			jPanelNoSpectrum.add(getJLabelNoSpectrum(), BorderLayout.CENTER);
		}
		return jPanelNoSpectrum;
	}

	private JLabel getJLabelNoSpectrum() {
		if (jLabelNoSpectrum == null) {
			jLabelNoSpectrum = new JLabel();
			jLabelNoSpectrum.setHorizontalAlignment(SwingConstants.CENTER);
			jLabelNoSpectrum
			        .setText("<html><em>No spectrum or not a peptide pair</em></html>");
		}
		return jLabelNoSpectrum;
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

		this.constructor = constructor;
		this.peptide = peptide;
		this.peaklists = peaklists;
		this.types = types;
		this.threshold = threshold;
		this.isMono = ismono;

		if (constructor != null && peptide != null && peaklists != null
		        && peaklists.length > 1) {
			this.getJPanelNoSpectrum().setVisible(false);
			this.getJFreeChartPanel0().setVisible(true);

			this.getJFreeChartPanel0().drawChart(
			        JFChartDrawer.createXYBarChart(constructor.construct(
			        		peaklists[1], peptide, peptide.getNeutralLossPeptideSequence(), this
			                        .getNeutralLossSettingPanel0()
			                        .getNeutralLossInfo(), types, ismono,
			                threshold)));

		} else {
			this.getJPanelNoSpectrum().setVisible(true);
			this.getJFreeChartPanel0().setVisible(false);
		}
	}

	/**
	 * Redraw for the previous information. Currently only for matched spectrum
	 */
	void reDrawMatchedSpectrum() {
		this.drawMatchedSpectrum(constructor, peptide, peaklists, this.types,
		        this.isMono, this.threshold);
	}
}
