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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.drawjf.JFreeChartPanel;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair;
import cn.ac.dicp.gp1809.proteome.drawjf.SpectrumMatchDatasetConstructor;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.IonsTypeSettingPanel;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.NeutralLossSettingPanel;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.SpectrumThresholdPanel;

/**
 * 
 * @author Xinning
 * @version 0.1, 04-14-2009, 10:50:40
 */
public class PeakListViewerPanel extends JPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	private SpectrumMatchDatasetConstructor constructor;
	private IPeptide peptide;
	private IMS2PeakList[] peaklists;
	private boolean isMono = true;

	private PeakListViewerMS3Frame peakListViewerMS3Frame;

	private boolean ms3Selectable;
	
	private JFreeChartPanel jFreeChartPanel0;
	private JPanel jPanelNoSpectrum;
	private JLabel jLabelNoSpectrum;
	private NeutralLossSettingPanel neutralLossSettingPanel0;
	private JCheckBox jCheckBoxShowMS3;
	private SpectrumThresholdPanel spectrumThresholdPanel0;
	private IonsTypeSettingPanel ionsTypeSettingPanel0;
	private JCheckBox jCheckBoxIsMono;

	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public PeakListViewerPanel() {
		initComponents();
	}
	
	public PeakListViewerPanel(SpectrumMatchDatasetConstructor constructor, IPeptide peptide,
	        IMS2PeakList[] peaklists) {
		this.constructor = constructor;
		this.peptide = peptide;
		this.peaklists = peaklists;
		initComponents();
	}
	
	public PeakListViewerPanel(boolean ms3Selectable) {
		this.ms3Selectable = ms3Selectable;
		initComponents();
	}

	private void initComponents() {
		setPreferredSize(new Dimension(797, 452));
		setLayout(new GroupLayout());
		add(getJFreeChartPanel0(), new Constraints(new Bilateral(198, 10, 100),
		        new Bilateral(6, 45, 200, 401)));
		add(getSpectrumThresholdPanel0(), new Constraints(new Leading(198, 12,
		        12), new Trailing(7, 10, 442)));
		add(getJCheckBoxShowMS3(), new Constraints(new Leading(13, 6, 6),
		        new Leading(446, 10, 10)));
		add(getIonsTypeSettingPanel0(), new Constraints(new Leading(6, 156, 69,
		        710), new Leading(259, 135, 6, 6)));
		add(getNeutralLossSettingPanel0(), new Constraints(new Leading(6, 69,
		        710), new Leading(6, 6, 6)));
		add(getJPanelNoSpectrum(), new Constraints(new Bilateral(180, 10, 53),
		        new Bilateral(6, 45, 401, 401)));
		add(getJCheckBoxIsMono(), new Constraints(new Leading(11, 10, 10),
		        new Leading(410, 6, 6)));
		setSize(773, 477);
	}

	private JCheckBox getJCheckBoxIsMono() {
		if (jCheckBoxIsMono == null) {
			jCheckBoxIsMono = new JCheckBox();
			jCheckBoxIsMono.setSelected(true);
			jCheckBoxIsMono.setText("Use monoisotope mass");
			jCheckBoxIsMono.addItemListener(this);
		}
		return jCheckBoxIsMono;
	}

	private IonsTypeSettingPanel getIonsTypeSettingPanel0() {
		if (ionsTypeSettingPanel0 == null) {
			ionsTypeSettingPanel0 = new IonsTypeSettingPanel();

			ionsTypeSettingPanel0.addSelectionListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					reDrawMatchedSpectrum();
				}

			});
		}
		return ionsTypeSettingPanel0;
	}

	private SpectrumThresholdPanel getSpectrumThresholdPanel0() {
		if (spectrumThresholdPanel0 == null) {
			spectrumThresholdPanel0 = new SpectrumThresholdPanel();

			spectrumThresholdPanel0
			        .addSetThresholdListener(new ActionListener() {

				        @Override
				        public void actionPerformed(ActionEvent e) {
					        reDrawMatchedSpectrum();
				        }

			        });
		}
		return spectrumThresholdPanel0;
	}

	private JCheckBox getJCheckBoxShowMS3() {
		if (jCheckBoxShowMS3 == null) {
			jCheckBoxShowMS3 = new JCheckBox();
			jCheckBoxShowMS3.setText("Show MS/MS/MS spectrum");
			jCheckBoxShowMS3.addItemListener(this);
			
			if(!this.ms3Selectable) {
				jCheckBoxShowMS3.setEnabled(false);
			}
		}
		return jCheckBoxShowMS3;
	}

	private NeutralLossSettingPanel getNeutralLossSettingPanel0() {
		if (neutralLossSettingPanel0 == null) {
			neutralLossSettingPanel0 = new NeutralLossSettingPanel();
			neutralLossSettingPanel0.addSelectionListener(new ItemListener() {

				@Override
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
			jLabelNoSpectrum.setText("<html><em>No spectrum</em></html>");
		}
		return jLabelNoSpectrum;
	}

	private PeakListViewerMS3Frame getPeakListViewerMS3Frame() {
		if (peakListViewerMS3Frame == null) {
			peakListViewerMS3Frame = new PeakListViewerMS3Frame();
			peakListViewerMS3Frame.setAlwaysOnTop(true);
			peakListViewerMS3Frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			peakListViewerMS3Frame.getContentPane().setPreferredSize(peakListViewerMS3Frame.getSize());
			peakListViewerMS3Frame.pack();
		}

		return peakListViewerMS3Frame;
	}

	/**
	 * Draw the spectrum for the scan, if the peak list getter existed, use it
	 * first
	 * 
	 * @param dataset
	 */
	public void drawMatchedSpectrum(
	        SpectrumMatchDatasetConstructor constructor, IPeptide peptide,
	        IMS2PeakList[] peaklists) {

		this.constructor = constructor;
		this.peptide = peptide;
		this.peaklists = peaklists;

		if (constructor != null && peptide != null && peaklists != null
		        && peaklists.length != 0) {
			this.getJPanelNoSpectrum().setVisible(false);
			this.getJFreeChartPanel0().setVisible(true);

			this.getJFreeChartPanel0().drawChart(
			        JFChartDrawer.createXYBarChart(constructor.construct(
			                this.peaklists[0], peptide, this
			                        .getNeutralLossSettingPanel0()
			                        .getNeutralLossInfo(), this
			                        .getIonsTypeSettingPanel0().getIonTypes(),
			                this.isMono, this.getSpectrumThresholdPanel0()
			                        .getThreshold())));

			if (this.getJCheckBoxShowMS3().isSelected()) {
				this.getPeakListViewerMS3Frame().drawMatchedSpectrum(
				        constructor, (IPhosPeptidePair) peptide, peaklists,
				        this.getIonsTypeSettingPanel0().getIonTypes(),
				        this.isMono,
				        this.getSpectrumThresholdPanel0().getThreshold());
			}
		} else {
			this.getJPanelNoSpectrum().setVisible(true);
			this.getJFreeChartPanel0().setVisible(false);

			if (this.getJCheckBoxShowMS3().isSelected())
				this.getPeakListViewerMS3Frame().drawMatchedSpectrum(null,
				        null, null, null, true, null);
		}
	}

	/**
	 * Redraw for the previous information. Currently only for matched spectrum
	 */
	public void reDrawMatchedSpectrum() {
		this.drawMatchedSpectrum(constructor, peptide, peaklists);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getSource();

		if (obj == this.getJCheckBoxShowMS3()) {
			if (this.getJCheckBoxShowMS3().isSelected()) {
				this.getPeakListViewerMS3Frame().setVisible(true);
			} else {
				this.getPeakListViewerMS3Frame().setVisible(false);
			}

			return;
		}

		if (obj == this.getJCheckBoxIsMono()) {
			if (this.getJCheckBoxIsMono().isSelected()) {
				this.isMono = true;
			} else {
				this.isMono = false;
			}

			this.reDrawMatchedSpectrum();
			return;
		}
	}
}
