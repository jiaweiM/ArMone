/* 
 ******************************************************************************
 * File: PepPairViewPanel.java * * * Created on 2011-9-22
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cn.ac.dicp.gp1809.proteome.quant.profile.IO.FeaturesPagedRowGetter;
import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.glyco.Quan.label.GlycoLPRowGetter;
import cn.ac.dicp.gp1809.glyco.drawjf.GlycoSpecMatchDataset;
import cn.ac.dicp.gp1809.glyco.drawjf.MatchSpecDrawFrame;
import cn.ac.dicp.gp1809.glyco.gui.NGlycoExportFrame;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.beans.gui.SelectablePagedTable;

//VS4E -- DO NOT REMOVE THIS LINE!
public class PepPairViewPanel extends JPanel implements ActionListener, ListSelectionListener {

	private FeaturesPagedRowGetter getter;
	private SelectablePagedTable selectablePagedTable;
	
	private static final long serialVersionUID = 1L;
	private RatioStatPanel ratioStatPanel0;
	private PepPairChartPanel pepPairChartPanel0;
	private JPanel jPanelOutput;
	private JPanel jPanelGlycoChart;
	private JButton jButtonClose;
	private JButton jButtonOutput;
	private JComboBox <Object> jComboBox0;
	private JLabel jLabelTitle;
	private boolean glyco;
	private JRadioButton jRadioButton0;
	private JRadioButton jRadioButton1;
	private JRadioButton jRadioButton2;
	private ButtonGroup buttonGroup1;
	private FeaturesObject feasObject;

	public PepPairViewPanel() {
		initComponents();
	}
	
	public PepPairViewPanel(FeaturesPagedRowGetter getter) {
		this.getter = getter;
		this.glyco = getter.isGlyco();
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new GroupLayout());
		add(addJLabelTitle(), new Constraints(new Bilateral(0, 0, 280), new Leading(0, 20, 6, 6)));
		add(getSelectablePagedTable(), new Constraints(new Bilateral(0, 0, 280), new Leading(20, 400, 6, 6)));
		add(getRatioStatPanel0(), new Constraints(new Leading(0, 560, 6, 6), new Leading(418, 350, 6, 6)));
		add(getPepPairChartPanel0(), new Constraints(new Leading(565, 535, 12, 12), new Leading(418, 350, 6, 6)));
		add(getOutputPanel(), new Constraints(new Leading(1137, 200, 6, 6), new Leading(435, 180, 10, 10)));
//		add(getGlycoChartChange(), new Constraints(new Leading(1137, 200, 6, 6), new Leading(630, 140, 10, 10)));
//		getGlycoChartChange();
		initButtonGroup1();
		setSize(1200, 800);
	}

	private void initButtonGroup1() {
		buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(getJRadioButton0());
		buttonGroup1.add(getJRadioButton1());
		buttonGroup1.add(getJRadioButton2());
	}
	
	private JRadioButton getJRadioButton2() {
		if (jRadioButton2 == null) {
			jRadioButton2 = new JRadioButton();
			jRadioButton2.setText("Glycan Structure");
			jRadioButton2.addActionListener(this);
			jRadioButton2.setEnabled(glyco);
		}
		return jRadioButton2;
	}

	private JRadioButton getJRadioButton1() {
		if (jRadioButton1 == null) {
			jRadioButton1 = new JRadioButton();
			jRadioButton1.setText("HCD Match Spectrum");
			jRadioButton1.addActionListener(this);
			jRadioButton1.setEnabled(glyco);
		}
		return jRadioButton1;
	}

	private JRadioButton getJRadioButton0() {
		if (jRadioButton0 == null) {
			jRadioButton0 = new JRadioButton();
			jRadioButton0.setSelected(true);
			jRadioButton0.setText("MS1 peak");
			jRadioButton0.addActionListener(this);
			jRadioButton0.setEnabled(glyco);
		}
		return jRadioButton0;
	}

	private JPanel getGlycoChartChange() {
		
		if (jPanelGlycoChart == null) {
			jPanelGlycoChart = new JPanel();
			jPanelGlycoChart.setBorder(BorderFactory.createTitledBorder(null, null, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("Dialog",
					Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelGlycoChart.setLayout(new GroupLayout());
			jPanelGlycoChart.add(getJRadioButton0(), new Constraints(new Leading(18, 6, 6), new Leading(15, 6, 6)));
			jPanelGlycoChart.add(getJRadioButton1(), new Constraints(new Leading(18, 6, 6), new Leading(50, 6, 6)));
			jPanelGlycoChart.add(getJRadioButton2(), new Constraints(new Leading(18, 6, 6), new Leading(85, 6, 6)));
			jPanelGlycoChart.setEnabled(glyco);
		}
		return jPanelGlycoChart;
	}

	private JPanel getOutputPanel() {
		if (jPanelOutput == null) {
			jPanelOutput = new JPanel();
			jPanelOutput.setBorder(BorderFactory.createTitledBorder(null, null, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("Dialog",
					Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelOutput.setLayout(new GroupLayout());
			jPanelOutput.add(getJButtonClose(), new Constraints(new Leading(10, 10, 10), new Leading(120, 10, 10)));
			jPanelOutput.add(getJButtonOutput(), new Constraints(new Leading(10, 12, 12), new Leading(70, 10, 10)));
			jPanelOutput.add(getJComboBox0(), new Constraints(new Leading(10, 150, 6, 6), new Leading(20, 10, 10)));
		}
		return jPanelOutput;
	}

	private JLabel addJLabelTitle() {
		if (jLabelTitle == null) {
			jLabelTitle = new JLabel("    "+getter.getFileName());
			Font myFont = new Font("Serif", Font.BOLD, 12);
			jLabelTitle.setFont(myFont);
		}
		return jLabelTitle;
	}

	private JComboBox <Object> getJComboBox0() {
		if (jComboBox0 == null) {
			jComboBox0 = new JComboBox <Object> ();
			
			if(glyco)
				jComboBox0.setModel(new DefaultComboBoxModel(new Object[] { "GlycoQuant result", "Glyco Spectra&Structrue" }));
			else
				jComboBox0.setModel(new DefaultComboBoxModel(new Object[] { "Quant result", "ModQuant result" }));
			
			jComboBox0.setDoubleBuffered(false);
			jComboBox0.setBorder(null);
		}
		return jComboBox0;
	}

	private JButton getJButtonOutput() {
		if (jButtonOutput == null) {
			jButtonOutput = new JButton();
			jButtonOutput.setText("Output");
			jButtonOutput.addActionListener(this);
		}
		return jButtonOutput;
	}

	public JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText("Close");
		}
		return jButtonClose;
	}

	private PepPairChartPanel getPepPairChartPanel0() {
		if (pepPairChartPanel0 == null) {
			pepPairChartPanel0 = new PepPairChartPanel();
			pepPairChartPanel0.setBorder(BorderFactory.createTitledBorder(null, null, TitledBorder.LEADING, TitledBorder.CENTER, new Font("Dialog",
					Font.BOLD, 12), new Color(51, 51, 51)));
		}
		return pepPairChartPanel0;
	}

	private RatioStatPanel getRatioStatPanel0() {
		if (ratioStatPanel0 == null) {
			if(getter==null)
				ratioStatPanel0 = new RatioStatPanel();
			else
				ratioStatPanel0 = new RatioStatPanel(getter);
			
			ratioStatPanel0.setBorder(BorderFactory.createTitledBorder(null, null, TitledBorder.LEADING, TitledBorder.CENTER, new Font(
					"Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
		}
		return ratioStatPanel0;
	}

	private SelectablePagedTable getSelectablePagedTable() {
		if (selectablePagedTable == null) {
			selectablePagedTable = new SelectablePagedTable(getter);
			selectablePagedTable.setBorder(BorderFactory.createCompoundBorder(null, null));
			selectablePagedTable.setMinimumSize(new Dimension(300, 200));
			selectablePagedTable.setPreferredSize(new Dimension(300, 200));
			selectablePagedTable.addListSelectionListener(this);
		}
		return selectablePagedTable;
	}
	
	public FeaturesPagedRowGetter getFeaturesGetter(){
		return this.getter;
	}

	public boolean isGlyco(){
		return glyco;
	}
	
	public void dispose(){
		this.getter = null;
		System.gc();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
		ListSelectionModel model = (ListSelectionModel) e.getSource();
		int first = e.getFirstIndex();
		int last = e.getLastIndex();
		
		if(model.isSelectedIndex(first)){
			
			this.feasObject = getter.getRow(first);
			
			if(this.glyco){
				
				if(this.getJRadioButton0().isSelected()){
					
					PepFeaturesDataset dataset = new PepFeaturesDataset(feasObject);
					dataset.selectType(0);
					pepPairChartPanel0.draw(dataset, true);
					this.repaint();
					this.updateUI();
					
				}else if(this.getJRadioButton1().isSelected()){
					
					IGlycoPeptide glyPep = (IGlycoPeptide) feasObject.getPeitdePair().getPeptide();
					NGlycoSSM ssm = glyPep.getDeleStructure();

					GlycoSpecMatchDataset dataset = new GlycoSpecMatchDataset(ssm.getScanNum());
					dataset.createDataset(ssm);
					
					pepPairChartPanel0.draw(dataset);
					this.repaint();
					this.updateUI();
					
				}else{
					
					IGlycoPeptide glyPep = (IGlycoPeptide) feasObject.getPeitdePair().getPeptide();
					NGlycoSSM ssm = glyPep.getDeleStructure();
					pepPairChartPanel0.draw(ssm.getGlycoTree());
					this.repaint();
					this.updateUI();
				}
				
			}else{
				
				if(this.getJRadioButton0().isSelected()){
					
					PepFeaturesDataset dataset = new PepFeaturesDataset(feasObject);
					dataset.selectType(0);
					pepPairChartPanel0.draw(dataset, true);
					this.repaint();
					this.updateUI();
					
				}
			}
			
		}else if(model.isSelectedIndex(last)){
			
			this.feasObject = getter.getRow(last);
			
			if(this.glyco){
				
				if(this.getJRadioButton0().isSelected()){

					PepFeaturesDataset dataset = new PepFeaturesDataset(feasObject);
					dataset.selectType(0);
					pepPairChartPanel0.draw(dataset, true);
					this.repaint();
					this.updateUI();
				
				}else if(this.getJRadioButton1().isSelected()){
					
					IGlycoPeptide glyPep = (IGlycoPeptide) feasObject.getPeitdePair().getPeptide();
					NGlycoSSM ssm = glyPep.getDeleStructure();

					GlycoSpecMatchDataset dataset = new GlycoSpecMatchDataset(ssm.getScanNum());
					dataset.createDataset(ssm);
					
					pepPairChartPanel0.draw(dataset);
					this.repaint();
					this.updateUI();
					
				}else{

					IGlycoPeptide glyPep = (IGlycoPeptide) feasObject.getPeitdePair().getPeptide();
					NGlycoSSM ssm = glyPep.getDeleStructure();
					pepPairChartPanel0.draw(ssm.getGlycoTree());
					this.repaint();
					this.updateUI();
				}
			}else{
				
				if(this.getJRadioButton0().isSelected()){
					
					PepFeaturesDataset dataset = new PepFeaturesDataset(feasObject);
					dataset.selectType(0);
					pepPairChartPanel0.draw(dataset, true);
					this.repaint();
					this.updateUI();
					
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object obj = e.getSource();
		
		if(obj==this.getJRadioButton0() || obj==this.getJRadioButton1() || obj==this.getJRadioButton2()){
			
			if(this.getJRadioButton0().isSelected()){
				
				if(feasObject!=null){
					
					PepFeaturesDataset dataset = new PepFeaturesDataset(feasObject);
					dataset.selectType(0);
					pepPairChartPanel0.draw(dataset, true);
					this.repaint();
					this.updateUI();
				}

			}else if(this.getJRadioButton1().isSelected()){
				
				if(feasObject!=null){
					
					IGlycoPeptide glyPep = (IGlycoPeptide) feasObject.getPeitdePair().getPeptide();
					NGlycoSSM ssm = glyPep.getDeleStructure();

					GlycoSpecMatchDataset dataset = new GlycoSpecMatchDataset(ssm.getScanNum());
					dataset.createDataset(ssm);
					
					pepPairChartPanel0.draw(dataset);
					this.repaint();
					this.updateUI();
					
				}
				
			}else if(this.getJRadioButton2().isSelected()){
				
				if(feasObject!=null){
					
					IGlycoPeptide glyPep = (IGlycoPeptide) feasObject.getPeitdePair().getPeptide();
					NGlycoSSM ssm = glyPep.getDeleStructure();
					pepPairChartPanel0.draw(ssm.getGlycoTree());
					this.repaint();
					this.updateUI();
				}
			}
			
			return;
		}
		
/*		if(obj==this.getJRadioButton1()){
			
			if(this.getJRadioButton0().isSelected()){
				
				if(feasObject!=null){
					
					PepFeaturesDataset dataset = new PepFeaturesDataset(feasObject);
					dataset.selectType(0);
					pepPairChartPanel0.draw(dataset, true);
					this.repaint();
					this.updateUI();
				}

			}else{
				
				if(feasObject!=null){
					
					IGlycoPeptide glyPep = (IGlycoPeptide) feasObject.getFeatures().getPeptide();
					NGlycoSSM ssm = glyPep.getDeleStructure();

					GlycoSpecMatchDataset dataset = new GlycoSpecMatchDataset(ssm.getScanNum());
					dataset.createDataset(ssm);
					
					pepPairChartPanel0.draw(dataset);
					this.repaint();
					this.updateUI();
					
				}
			}
			return;
		}
*/		
		if(obj==this.getJButtonOutput()){
			
			int idx = this.getJComboBox0().getSelectedIndex();
			
			if(idx==0){
				
				if(glyco){
					
					JFrame frame = new NGlycoExportFrame((GlycoLPRowGetter) getter);
					frame.getContentPane().setPreferredSize(frame.getSize());
					frame.pack();
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setLocationRelativeTo(this);
					frame.setVisible(true);
					frame.setAlwaysOnTop(true);
					
				}else{
					
					JFrame frame = new QResOutFrame(getter);
					frame.getContentPane().setPreferredSize(frame.getSize());
					frame.pack();
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setLocationRelativeTo(this);
					frame.setVisible(true);
					frame.setAlwaysOnTop(true);
				}

				return;
				
			}else if(idx==1){
				
				if(glyco){
					
					JFrame frame = new MatchSpecDrawFrame((GlycoLPRowGetter) getter);
					frame.getContentPane().setPreferredSize(frame.getSize());
					frame.pack();
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setLocationRelativeTo(this);
					frame.setVisible(true);
					frame.setAlwaysOnTop(true);
					
				}else{
					
					JFrame frame = new QModTableFrame(getter);
					frame.getContentPane().setPreferredSize(frame.getSize());
					frame.pack();
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setLocationRelativeTo(this);
					frame.setVisible(true);
					frame.setAlwaysOnTop(true);
				}
			}
		}
	}

	

}
