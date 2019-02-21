/* 
 ******************************************************************************
 * File:InfoPanel.java * * * Created on 2011-9-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.gui.PeptideStatInfo;
import cn.ac.dicp.gp1809.proteome.gui.PeptideStatPanel;

//VS4E -- DO NOT REMOVE THIS LINE!
public class InfoPanel extends JPanel implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;
	private JPanel currentPanel;
	private PPMPanel ppmPanel;
	private PeptideStatPanel peptideStatPanel0;
	private ModInfoPanel2 modInfoPanel;
	private PeptideListPagedRowGetter2 getter;
	private JCheckBox jCheckBoxStatInfo;
	private JCheckBox jCheckBoxMassDevi;
	private JCheckBox jCheckBoxVariMod;
	private ButtonGroup buttonGroup1;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public InfoPanel() {
		initComponents();
	}
	
	public InfoPanel(PeptideListPagedRowGetter2 getter) {
		this.getter = getter;
		initComponents();
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(null, "Peptide information", TitledBorder.LEADING, TitledBorder.TOP, new Font("Dialog", Font.BOLD, 15),
				new Color(51, 51, 51)));
		setLayout(new GroupLayout());
		add(getPeptideStatPanel0(), new Constraints(new Leading(0, 409, 6, 6), new Leading(0, 6, 6)));
		add(getJCheckBoxStatInfo(), new Constraints(new Leading(427, 6, 6), new Leading(35, 10, 10)));
		add(getJCheckBoxMassDevi(), new Constraints(new Leading(427, 6, 6), new Leading(85, 10, 10)));
		add(getJCheckBoxVariMod(), new Constraints(new Leading(427, 6, 6), new Leading(135, 10, 10)));
		initButtonGroup1();
		getPPMStatPanel0();
		getModInfoPanel();
		setSize(635, 340);
	}

	private JCheckBox getJCheckBoxVariMod() {
		if (jCheckBoxVariMod == null) {
			jCheckBoxVariMod = new JCheckBox();
			jCheckBoxVariMod.setText("Variable mod");
			jCheckBoxVariMod.addActionListener(this);
		}
		return jCheckBoxVariMod;
	}

	private void initButtonGroup1() {
		buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(getJCheckBoxStatInfo());
		buttonGroup1.add(getJCheckBoxMassDevi());
		buttonGroup1.add(getJCheckBoxVariMod());
	}

	private JCheckBox getJCheckBoxMassDevi() {
		if (jCheckBoxMassDevi == null) {
			jCheckBoxMassDevi = new JCheckBox();
			jCheckBoxMassDevi.setText("Mass deviation");
			jCheckBoxMassDevi.addActionListener(this);
		}
		return jCheckBoxMassDevi;
	}

	private JCheckBox getJCheckBoxStatInfo() {
		if (jCheckBoxStatInfo == null) {
			jCheckBoxStatInfo = new JCheckBox();
			jCheckBoxStatInfo.setSelected(true);
			jCheckBoxStatInfo.setText("Peptide matches");
			jCheckBoxStatInfo.addActionListener(this);
		}
		return jCheckBoxStatInfo;
	}

	private PeptideStatPanel getPeptideStatPanel0() {
		if (peptideStatPanel0 == null) {
			if(getter==null)
				peptideStatPanel0 = new PeptideStatPanel();
			else{
				peptideStatPanel0 = new PeptideStatPanel(getter.getPeptideStatInfo());
			}
		}
		currentPanel = peptideStatPanel0;
		return peptideStatPanel0;
	}
	
	private PPMPanel getPPMStatPanel0() {
		if (ppmPanel == null) {
			if(getter==null)
				ppmPanel = new PPMPanel();
			else{
				ppmPanel = new PPMPanel(getter.getPPMDataset());
			}
		}
		return ppmPanel;
	}
	
	private ModInfoPanel2 getModInfoPanel() {
		if (modInfoPanel == null) {
			if(getter==null){
				modInfoPanel = new ModInfoPanel2();
			}else{
				modInfoPanel = new ModInfoPanel2(getter.getSearchParameter().getVariableInfo());
			}
		}
		return modInfoPanel;
	}
	
	protected void addSelectListener(ActionListener listener){
		this.modInfoPanel.getJButtonSelect().addActionListener(listener);
	}
	
	protected void addDisposeListener(ActionListener listener){
		this.modInfoPanel.getJButtonDispose().addActionListener(listener);
	}
	
	protected ArrayList <IPeptideCriteria> getModFilters(){
		return this.modInfoPanel.getModFilters();
	}
	
	private JPanel getCurrentPanel() {
		if (currentPanel == null) {
			currentPanel = new JPanel();
		}
		return currentPanel;
	}

	public void setStatPanel(PeptideStatInfo pepInfo){	
		peptideStatPanel0.loadPeptideStatInfo(pepInfo);
		peptideStatPanel0.updateUI();
		this.updateUI();
	}
	
	public void setPPMPanel(PPMDataset dataset){
		ppmPanel.loadPPMInfo(dataset);
		ppmPanel.updateUI();
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
				frame.setTitle("InfoPanel");
				InfoPanel content = new InfoPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
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

		if(obj==this.jCheckBoxStatInfo){
			
			if(this.jCheckBoxStatInfo.isSelected()){
				if(this.currentPanel!=null){
					this.remove(currentPanel);
					add(peptideStatPanel0, new Constraints(new Leading(0, 409, 6, 6), new Leading(0, 6, 6)));
					this.updateUI();
					currentPanel = this.peptideStatPanel0;
				}
			}
			return;
		}
		
		if(obj==this.jCheckBoxMassDevi){
			if(this.jCheckBoxMassDevi.isSelected()){
				if(this.currentPanel!=null){
					this.remove(currentPanel);
					add(ppmPanel, new Constraints(new Leading(0, 409, 6, 6), new Leading(0, 6, 6)));
					this.updateUI();
					currentPanel = this.ppmPanel;
				}
			}
		}
		
		if(obj==this.jCheckBoxVariMod){
			if(this.jCheckBoxVariMod.isSelected()){
				if(this.currentPanel!=null){
					this.remove(currentPanel);
					add(getModInfoPanel(), new Constraints(new Leading(0, 409, 6, 6), new Leading(0, 280, 6, 6)));
					this.updateUI();
					currentPanel = this.modInfoPanel;
				}
			}
		}
		
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
