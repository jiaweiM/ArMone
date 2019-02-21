/* 
 ******************************************************************************
 * File: RatioStatPanel.java * * * Created on 2011-9-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import cn.ac.dicp.gp1809.proteome.quant.profile.IO.FeaturesPagedRowGetter;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;
import org.jfree.chart.JFreeChart;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.drawjf.JFreeChartPanel;

//VS4E -- DO NOT REMOVE THIS LINE!
public class RatioStatPanel extends JPanel implements ItemListener{

	private JFreeChartPanel jFreeChartPanel0;
	private FeaturesPagedRowGetter getter;
	private static final long serialVersionUID = 1L;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public RatioStatPanel() {
		initComponents();
	}
	
	public RatioStatPanel(FeaturesPagedRowGetter getter) {
		this.getter = getter;
		initComponents();
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(null, "Log2 Ratio Distribution", TitledBorder.LEADING, TitledBorder.CENTER, new Font("Dialog", Font.BOLD, 12),
				new Color(51, 51, 51)));
		setLayout(new GroupLayout());
		add(getChartPanel(), new Constraints(new Leading(0, 535, 10, 10), new Leading(0, 320, 10, 10)));
		setSize(535, 340);
	}

	private JFreeChartPanel getChartPanel() {
		if (jFreeChartPanel0 == null) {
			jFreeChartPanel0 = new JFreeChartPanel();
			RatioStatDataset dataset = getter.getRatioDistribution();
			JFreeChart chart = JFChartDrawer.createScatterChart(dataset);
			jFreeChartPanel0.drawChart(chart);
			jFreeChartPanel0.setSize(535, 340);
		}
		return jFreeChartPanel0;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		Object obj = e.getSource();

	}
	
}
