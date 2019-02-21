/* 
 ******************************************************************************
 * File: ChartPanel.java * * * Created on 04-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.drawjf;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import java.awt.BorderLayout;

/**
 * Panel contains the jfree chart
 * 
 * @author Xinning
 * @version 0.1, 04-13-2009, 11:59:13
 */
public class JFreeChartPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private ChartPanel chartPanel;

	public JFreeChartPanel() {
		initComponents();
	}

	private void initComponents() {
		setSize(630, 240);
		setLayout(new BorderLayout(0, 0));
		add(getChartPanel());
	}

	private ChartPanel getChartPanel() {
		if (chartPanel == null) {
			chartPanel = new ChartPanel(null);
		}
		return chartPanel;
	}

	/**
	 * Draw the new data set in the chart panel
	 * 
	 * @param dataset
	 */
	public void drawChart(JFreeChart chart) {
		this.chartPanel.setChart(chart);
	}
}
