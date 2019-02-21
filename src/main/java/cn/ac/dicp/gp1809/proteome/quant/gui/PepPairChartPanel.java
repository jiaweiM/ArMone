/* 
 ******************************************************************************
 * File: PepPairChartPanel.java * * * Created on 2011-9-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.drawjf.JFreeChartPanel;
import cn.ac.dicp.gp1809.glyco.drawjf.GlycoSpecMatchDataset;
import cn.ac.dicp.gp1809.glyco.drawjf.GlycoStrucDrawer;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;

//VS4E -- DO NOT REMOVE THIS LINE!
public class PepPairChartPanel extends JPanel {
	
	private JFreeChartPanel jFreeChartPanel0;
	private PepFeaturesDataset dataset;
	private GlycoStrucDrawer drawer;
	private JLabel jLabel1;
	private boolean bar = false;

	private static final long serialVersionUID = 1L;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public PepPairChartPanel() {
		
		initComponents();
/*		try {
			this.drawer = new GlycoStrucDrawer();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
	}
	
	public PepPairChartPanel(PepFeaturesDataset dataset) {
		
		this.dataset = dataset;
		dataset.selectType(PepFeaturesDataset.beforeFilter);
		this.draw(dataset, bar);
		initComponents();
/*		try {
			this.drawer = new GlycoStrucDrawer();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(null, null, TitledBorder.LEADING, TitledBorder.CENTER, new Font("Dialog", Font.BOLD, 12),
				new Color(51, 51, 51)));
		setLayout(new GroupLayout());
		add(getJFreeChartPanel0(), new Constraints(new Bilateral(0, 0, 0), new Bilateral(0, 0, 0)));
		setSize(560, 340);
	}
	
	private JLabel getJLabel1() {
		
    	if (jLabel1 == null) {
    		jLabel1 = new JLabel();
   	}
    	return jLabel1;
    }

	public void draw(PepFeaturesDataset dataset, boolean bar){
		
		this.remove(getJLabel1());
		if(bar)
			this.getJFreeChartPanel0().drawChart(JFChartDrawer.createXYBarChart(dataset));
		else
			this.getJFreeChartPanel0().drawChart(JFChartDrawer.createLineChart(dataset));
		
		this.add(getJFreeChartPanel0(), new Constraints(new Bilateral(0, 0, 0), new Bilateral(0, 0, 0)));
		this.repaint();
		this.updateUI();
	}
	
	public void draw(GlycoSpecMatchDataset glycoDataset){
		this.remove(getJLabel1());
		this.getJFreeChartPanel0().drawChart(JFChartDrawer.createXYBarChart(glycoDataset));
		this.add(getJFreeChartPanel0(), new Constraints(new Bilateral(0, 0, 0), new Bilateral(0, 0, 0)));
		this.repaint();
		this.updateUI();
	}
	
	public void draw(GlycoTree tree){
		
		Image image = this.drawer.draw(tree).getScaledInstance(450, 360, Image.SCALE_SMOOTH);
		this.getJLabel1().setIcon(new ImageIcon(image));
		this.remove(getJFreeChartPanel0());
		this.add(getJLabel1(), new Constraints(new Bilateral(0, 0, 0), new Bilateral(0, 0, 0)));
		this.repaint();
		this.updateUI();
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
}
