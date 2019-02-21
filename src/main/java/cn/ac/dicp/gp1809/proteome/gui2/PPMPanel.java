/* 
 ******************************************************************************
 * File:PPMPanel.java * * * Created on 2011-9-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.jfree.chart.JFreeChart;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.drawjf.JFreeChartPanel;

//VS4E -- DO NOT REMOVE THIS LINE!
public class PPMPanel extends JPanel {

	private PPMDataset dataset;
	private JFreeChartPanel jFreeChartPanel0;
	
	private static final long serialVersionUID = 1L;
	private static final String PREFERRED_LOOK_AND_FEEL = null;

	public PPMPanel() {
		initComponents();
	}
	
	public PPMPanel(PPMDataset dataset) {
		this.dataset = dataset;
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getChartPanel(), new Constraints(new Leading(0, 410, 10, 10), new Leading(0, 260, 10, 10)));
		setSize(410, 260);
	}
	
	private JFreeChartPanel getChartPanel() {
		if (jFreeChartPanel0 == null) {
			jFreeChartPanel0 = new JFreeChartPanel();
			JFreeChart chart = JFChartDrawer.createScatterChart(dataset);
			jFreeChartPanel0.drawChart(chart);
			jFreeChartPanel0.setSize(410, 260);
		}
		return jFreeChartPanel0;
	}

	protected void loadPPMInfo(PPMDataset dataset){
		this.dataset = dataset;
		if(jFreeChartPanel0!=null){
			this.remove(jFreeChartPanel0);
		}
		JFreeChart chart = JFChartDrawer.createScatterChart(dataset);
		jFreeChartPanel0.drawChart(chart);
		add(getChartPanel(), new Constraints(new Leading(0, 410, 10, 10), new Leading(0, 260, 10, 10)));
		this.repaint();
		this.updateUI();
	}
	
	private static void installLnF() {
		try {
			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
			if (lnfClassname == null)
				lnfClassname = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lnfClassname);
		} catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL
					+ " on this platform:" + e.getMessage());
		}
	}

	/**
	 * Main entry of the class.
	 * Note: This class is only created so that you can easily preview the result at runtime.
	 * It is not expected to be managed by the designer.
	 * You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("PPMPanel");
				PPMPanel content = new PPMPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
