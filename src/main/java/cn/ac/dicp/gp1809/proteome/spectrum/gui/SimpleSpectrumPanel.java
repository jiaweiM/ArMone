/* 
 ******************************************************************************
 * File: SimpleSpectrumPanel.java * * * Created on 2012-12-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.gui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.jfree.chart.JFreeChart;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.drawjf.JFreeChartPanel;
import cn.ac.dicp.gp1809.proteome.drawjf.SpectrumDataset;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import javax.swing.GroupLayout.Alignment;

public class SimpleSpectrumPanel extends JPanel {
	
	private JFreeChartPanel jFreeChartPanel0;
	private double intenthres = 0.1;
	private static final long serialVersionUID = 1L;

	public SimpleSpectrumPanel() {
		initComponents();
	}
	
	public SimpleSpectrumPanel(IPeak [] peaks) {
		initComponents();
	}
	
	public SimpleSpectrumPanel(MS2PeakList peaks) {
		SpectrumDataset dataset = new SpectrumDataset(peaks);
		JFreeChart chart = JFChartDrawer.createXYBarChart(dataset);
		this.getJFreeChartPanel0().drawChart(chart);
		
		add(getJFreeChartPanel0());
		this.repaint();
		this.updateUI();
		initComponents();
	}
	
	private void initComponents() {
		setSize(320, 240);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGap(0, 320, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGap(0, 240, Short.MAX_VALUE)
		);
		setLayout(groupLayout);
	}

	private JFreeChartPanel getJFreeChartPanel0() {
		if (jFreeChartPanel0 == null) {
			jFreeChartPanel0 = new JFreeChartPanel();
			jFreeChartPanel0.setBorder(BorderFactory.createCompoundBorder(null, null));
			jFreeChartPanel0.setMinimumSize(new Dimension(560, 360));
			jFreeChartPanel0.setPreferredSize(new Dimension(560, 360));
		}
		return jFreeChartPanel0;
	}
	
	public void draw(MS2PeakList peaks){
		
		SpectrumDataset dataset = new SpectrumDataset(peaks, intenthres);
		JFreeChart chart = JFChartDrawer.createXYBarChartNoLegend(dataset);
		this.getJFreeChartPanel0().drawChart(chart);
		
		this.add(getJFreeChartPanel0());
		this.repaint();
		this.updateUI();
	}
	
}
