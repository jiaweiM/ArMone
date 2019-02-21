/* 
 ******************************************************************************
 * File:LPairViewerFrame.java * * * Created on 2010-7-8
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.gui;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.drawjf.JFreeChartPanel;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import org.dyno.visual.swing.layouts.GroupLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//VS4E -- DO NOT REMOVE THIS LINE!
public class LPairViewerFrame extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JFreeChartPanel jFreeChartPanel0;
	private MyJFileChooser outChooser;
	private PepFeaturesDataset dataset;
	private JMenuItem jMenuItemExPeak;
	private JMenu jMenuAction;
	private JMenuBar jMenuBar0;
	private JMenuItem jMenuItemBar;
	private JMenu jMenuFigure;
	private JMenuItem jMenuItemLine;
	private JMenuItem jMenuItemSmoothLine;
	private ButtonGroup chartGroup;
	private boolean filter = false;
	private boolean bar = true;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public LPairViewerFrame() {
		initComponents();
	}

	public LPairViewerFrame(PepFeaturesDataset dataset) {
		this.dataset = dataset;
		dataset.selectType(PepFeaturesDataset.beforeFilter);
		this.draw(dataset, bar);
		initComponents();
	}
	
//	add(getJFreeChartPanel0(), new Constraints(new Bilateral(0, 0, 0), new Bilateral(0, 0, 0)));
	
	private void initComponents() {
		setLayout(new GroupLayout());
		setJMenuBar(getJMenuBar0());
//		add(getJFreeChartPanel0(), new Constraints(new Bilateral(0, 0, 0), new Bilateral(0, 0, 0)));
		initChartGroup();
		setSize(640, 250);
	}
	
	private ButtonGroup initChartGroup() {
		if (chartGroup == null) {
			chartGroup = new ButtonGroup();
			chartGroup.add(getJMenuItemBar());
			chartGroup.add(getJMenuItemLine());
			chartGroup.add(getJMenuItemSmoothLine());
		}
		return chartGroup;
	}
	
	private JMenuItem getJMenuItemSmoothLine() {
		if (jMenuItemSmoothLine == null) {
			jMenuItemSmoothLine = new JMenuItem();
			jMenuItemSmoothLine.setText("Smooth Line");
			jMenuItemSmoothLine.addActionListener(this);
		}
		return jMenuItemSmoothLine;
	}

	private JMenuItem getJMenuItemLine() {
		if (jMenuItemLine == null) {
			jMenuItemLine = new JMenuItem();
			jMenuItemLine.setText("Line Chart");
			jMenuItemLine.addActionListener(this);
		}
		return jMenuItemLine;
	}

	private JMenu getJMenuFigure() {
		if (jMenuFigure == null) {
			jMenuFigure = new JMenu();
			jMenuFigure.setText("Chart Type");
			jMenuFigure.add(getJMenuItemBar());
			jMenuFigure.add(getJMenuItemLine());
			jMenuFigure.add(getJMenuItemSmoothLine());
		}
		return jMenuFigure;
	}

	private JMenuItem getJMenuItemBar() {
		if (jMenuItemBar == null) {
			jMenuItemBar = new JMenuItem();
			jMenuItemBar.setText("Bar Chart");
			jMenuItemBar.addActionListener(this);
		}
		return jMenuItemBar;
	}

	private JMenuBar getJMenuBar0() {
		if (jMenuBar0 == null) {
			jMenuBar0 = new JMenuBar();
			jMenuBar0.add(getJMenuAction());
			jMenuBar0.add(getJMenuFigure());
		}
		return jMenuBar0;
	}

	private JMenu getJMenuAction() {
		if (jMenuAction == null) {
			jMenuAction = new JMenu();
			jMenuAction.setText("Action");
			jMenuAction.add(getJMenuItemExPeak());
		}
		return jMenuAction;
	}

	private JMenuItem getJMenuItemExPeak() {
		if (jMenuItemExPeak == null) {
			jMenuItemExPeak = new JMenuItem();
			jMenuItemExPeak.setText("Export Peak Info");
			jMenuItemExPeak.addActionListener(this);
		}
		return jMenuItemExPeak;
	}

	private MyJFileChooser getOutchooser() {
		if (this.outChooser == null) {
			this.outChooser = new MyJFileChooser();
			this.outChooser.setFileFilter(new String[] { "txt" },
			        "Label Quantitation result file (*.txt)");
		}
		return outChooser;
	}

	private JFreeChartPanel getJFreeChartPanel0() {
		if (jFreeChartPanel0 == null) {
			jFreeChartPanel0 = new JFreeChartPanel();
			jFreeChartPanel0.setBorder(BorderFactory.createCompoundBorder(null, null));
			jFreeChartPanel0.setMinimumSize(new Dimension(630, 360));
			jFreeChartPanel0.setPreferredSize(new Dimension(630, 360));
		}
		return jFreeChartPanel0;
	}

	public void draw(PepFeaturesDataset dataset, boolean bar){
		if(bar)
			this.getJFreeChartPanel0().drawChart(JFChartDrawer.createXYBarChart(dataset));
		else
			this.getJFreeChartPanel0().drawChart(JFChartDrawer.createLineChart(dataset));
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
				LPairViewerFrame frame = new LPairViewerFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("LPairViewerFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		Object obj = e.getSource();
		
		if(obj==this.getJMenuItemExPeak()){
			int value = this.getOutchooser().showOpenDialog(this);
			if (value == JFileChooser.APPROVE_OPTION){
				String out =  this.getOutchooser().getSelectedFile().getAbsolutePath()+".txt";
				try {
					PrintWriter pw = new PrintWriter(new FileWriter(out));
					FeaturesObject pairobj = this.dataset.getFeatures();
					PeptidePair pair = pairobj.getPeitdePair();
					LabelFeatures feas = pair.getFeatures();
					
					String seq = pair.getSequence();
					pw.write("Sequence:\t"+seq+"\n");

					String refs = pair.getRefs();
					pw.write("Reference:\t"+refs+"\n\n");

					String [] pairNames = feas.getFeatureNames();
					
					int [] scanlist = feas.getScanList();
					double [][] intenlist = feas.getIntenList();
					
					for(int i=0;i<pairNames.length;i++){
						pw.write(pairNames[i]+"\n");
						for(int j=0;j<scanlist.length;j++){
							pw.write(scanlist[j]+"\t"+intenlist[j][i]+"\n");
						}
						pw.write("\n");
					}

					pw.close();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return;
		}
		
		if(obj == this.getJMenuItemBar()){
			if(filter){
				dataset.selectType(PepFeaturesDataset.beforeFilter);
				filter = false;
			}
			if(!bar){
				this.bar = true;
				this.draw(dataset, bar);
			}
			return;
		}
		
		if(obj == this.getJMenuItemLine()){
			if(filter){
				dataset.selectType(PepFeaturesDataset.beforeFilter);
				filter = false;
			}
			if(bar){
				this.bar = false;
				this.draw(dataset, bar);
			}else{
				this.draw(dataset, bar);
			}
			return;
		}
		
		if(obj == this.getJMenuItemSmoothLine()){
			if(!filter){
				dataset.selectType(PepFeaturesDataset.afterFilter);
				filter = true;
			}
			if(bar){
				this.bar = false;
				this.draw(dataset, bar);
			}else{
				this.draw(dataset, bar);
			}
			return;
			
		}
		
	}

}
