/* 
 ******************************************************************************
 * File: CriteriaPanel.java * * * Created on 04-09-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import javax.swing.GroupLayout.Alignment;

/**
 * The criteria panels
 * 
 * @author Xinning
 * @version 0.1, 04-09-2009, 21:09:29
 */
public class CriteriaPanel extends JPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	private ICriteriaSetter currentCriteriaPanel;

	private JComboBox jComboBox0;
	private JButton jButtonApplyFilter;
	private JPanel jPanel0;

	private SequestCriteriaPanel sequestCriteriaPanel0;
	private ProbabilityCriteriaPanel probabilityCriteriaPanel0;
	private DeltaMZCriteriaPanel deltaMZCriteriaPanel0;
	private OMSSACriteriaPanel OMSSACriteriaPanel0;
	private MascotCriteriaPanel2 mascotCriteriaPanel0;
	private XTandemCriteriaPanel xtandemCriteriaPanel0;
	private CruxCriteriaPanel cruxCriteriaPanel0;
	private MascotApivaseCriteriaPanel mascotApivaseCriteriaPanel0;
	private SequestApivaseCriteriaPanel sequestApivaseCriteriaPanel0;

	
	private SFOERSequestCriteriaPanel sFOERSequestCriteriaPanel;
	
	private JCheckBox jCheckBoxUseProb;
	
	private SFOERActionPanel spanel;

	private JCheckBox jCheckBoxDeltaMz;

	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public CriteriaPanel() {
		this(PeptideType.SEQUEST, true);
	}

	public CriteriaPanel(PeptideType type) {
		this(type, true);
	}

	/**
	 * Shown and can only shown the specific peptide type criteria panel
	 * 
	 * @param type
	 * @param peptypeChangable
	 */
	public CriteriaPanel(PeptideType type, boolean peptypeChangable) {
		initComponents();
		this.selectCriteriaPanel(type);
		this.setPeptideTypeSelectable(peptypeChangable);
	}

	private void initComponents() {
    	setSize(618, 261);
    	javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(16)
    				.addComponent(getJCheckBoxUseProb())
    				.addGap(44)
    				.addComponent(getJCheckBoxDeltaMz(), javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(12)
    				.addComponent(getJComboBox0(), javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
    				.addGap(24)
    				.addComponent(getJButtonApplyFilter()))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(14)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addComponent(getJCheckBoxUseProb())
    					.addComponent(getJCheckBoxDeltaMz()))
    				.addGap(18)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addComponent(getJComboBox0(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    					.addComponent(getJButtonApplyFilter()))
    				.addGap(25)
    				.addComponent(getJPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
    	);
    	setLayout(groupLayout);
    }

	private JCheckBox getJCheckBoxDeltaMz() {
    	if (jCheckBoxDeltaMz == null) {
    		jCheckBoxDeltaMz = new JCheckBox();
    		jCheckBoxDeltaMz.setText("Use Delta M/Z filter");
    		jCheckBoxDeltaMz.addItemListener(this);
    	}
    	return jCheckBoxDeltaMz;
    }

	private JCheckBox getJCheckBoxUseProb() {
    	if (jCheckBoxUseProb == null) {
    		jCheckBoxUseProb = new JCheckBox();
    		jCheckBoxUseProb.setText("Use probability filter");
    		jCheckBoxUseProb.addItemListener(this);
    	}
    	return jCheckBoxUseProb;
    }

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		javax.swing.GroupLayout gl_jPanel0 = new javax.swing.GroupLayout(jPanel0);
    		gl_jPanel0.setHorizontalGroup(
    			gl_jPanel0.createParallelGroup(Alignment.LEADING)
    				.addComponent(getSequestCriteriaPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE)
    		);
    		gl_jPanel0.setVerticalGroup(
    			gl_jPanel0.createParallelGroup(Alignment.LEADING)
    				.addComponent(getSequestCriteriaPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
    		);
    		jPanel0.setLayout(gl_jPanel0);
    	}
    	return jPanel0;
    }

	private JButton getJButtonApplyFilter() {
		if (jButtonApplyFilter == null) {
			jButtonApplyFilter = new JButton();
			jButtonApplyFilter.setText("Apply filter");
			jButtonApplyFilter.setVisible(false);

		}
		return jButtonApplyFilter;
	}

	private JComboBox getJComboBox0() {
		if (jComboBox0 == null) {
			jComboBox0 = new JComboBox();
			jComboBox0.setModel(new DefaultComboBoxModel(new Object[] {
			        PeptideType.SEQUEST, PeptideType.MASCOT,
			        PeptideType.XTANDEM, PeptideType.OMSSA, PeptideType.CRUX,
			        PeptideType.APIVASE_SEQUEST, PeptideType.APIVASE_MASCOT }));
			jComboBox0.addItemListener(this);
		}
		return jComboBox0;
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
	 * Set the peptide type combobox selectable
	 * 
	 * @param selectable
	 */
	public void setPeptideTypeSelectable(boolean selectable) {
		this.getJComboBox0().setEnabled(selectable);
	}

	/**
	 * Select the criteria panel for the peptide type
	 * 
	 * @param type
	 */
	public void selectCriteriaPanel(PeptideType type) {

		if (this.jComboBox0.getSelectedItem() != type) {
			this.jComboBox0.setSelectedItem(type);
		}

		JPanel panel = null;

		switch (type) {
		case SEQUEST:
			panel = this.getSequestCriteriaPanel0();
			break;
		case MASCOT:
			panel = this.getMascotCriteriaPanel0();
			break;
		case OMSSA:
			panel = this.getOMSSACriteriaPanel0();
			break;
		case CRUX:
			panel = this.getCruxCriteriaPanel0();
			break;
		case XTANDEM:
			panel = this.getXTandemCriteriaPanel0();
			break;
		case APIVASE_MASCOT:
			panel = this.getmascotApivaseCriteriaPanel0();
			break;
		case APIVASE_SEQUEST:
			panel = this.getsequestApivaseCriteriaPanel0();
			break;
		default:
			throw new IllegalArgumentException("Unsupportted peptide type: "
			        + type);
		}

		if (this.currentCriteriaPanel != null)
			this.getJPanel0().remove((JPanel) this.currentCriteriaPanel);

		this.getJPanel0().add(
		        panel,
		        new Constraints(new Bilateral(0, 0, 585),
		                new Leading(0, 0, 145)));
		this.updateUI();
		panel.updateUI();

		this.currentCriteriaPanel = (ICriteriaSetter) panel;
	}
	
	
	/**
	 * Select the SFOER criteria panel
	 * 
	 * @param xcorrs
	 * @param dcns
	 * @param sps
	 * @param rsps
	 */
	public void selectCriteriaPanel(float[] xcorrs, float[] dcns, float[] sps, short[] rsps, float[] deltaMSppms) {
		
		SFOERSequestCriteriaPanel panel = this.getSFOERCriteriaPanel0();
		panel.setCriteira(xcorrs, dcns, sps, rsps);
		
		if (this.currentCriteriaPanel != null)
			this.getJPanel0().remove((JPanel) this.currentCriteriaPanel);

		this.getJPanel0().add(
		        panel,
		        new Constraints(new Bilateral(0, 0, 585),
		                new Leading(0, 0, 145)));
		this.updateUI();
		panel.updateUI();

		this.currentCriteriaPanel = panel;
	}
	
	
	private SFOERSequestCriteriaPanel getSFOERCriteriaPanel0() {
		if (this.sFOERSequestCriteriaPanel == null) {
			sFOERSequestCriteriaPanel = new SFOERSequestCriteriaPanel();
		}
		return sFOERSequestCriteriaPanel;
	}
	
	/**
	 * Select the probability setting panel
	 */
	private void selectProbabilityPanel() {
		if (this.currentCriteriaPanel != null)
			this.getJPanel0().remove((JPanel) this.currentCriteriaPanel);

		this.getJPanel0().add(
		        this.getProbabilityCriteriaPanel0(),
		        new Constraints(new Bilateral(0, 0, 585),
		                new Leading(0, 0, 145)));
		this.updateUI();
		this.getProbabilityCriteriaPanel0().updateUI();

		this.currentCriteriaPanel = this.getProbabilityCriteriaPanel0();
	}
	
	

	private ProbabilityCriteriaPanel getProbabilityCriteriaPanel0() {
		if (this.probabilityCriteriaPanel0 == null) {
			probabilityCriteriaPanel0 = new ProbabilityCriteriaPanel();
		}
		return probabilityCriteriaPanel0;
	}
	
	
	/**
	 * Select the probability setting panel
	 */
	private void selectDeltaMzPanel() {
		if (this.currentCriteriaPanel != null)
			this.getJPanel0().remove((JPanel) this.currentCriteriaPanel);

		this.getJPanel0().add(
		        this.getDeltaMzCriteriaPanel0(),
		        new Constraints(new Bilateral(0, 0, 585),
		                new Leading(0, 0, 145)));
		this.updateUI();
		this.getDeltaMzCriteriaPanel0().updateUI();

		this.currentCriteriaPanel = this.getDeltaMzCriteriaPanel0();
	}
	
	

	private DeltaMZCriteriaPanel getDeltaMzCriteriaPanel0() {
		if (this.deltaMZCriteriaPanel0 == null) {
			deltaMZCriteriaPanel0 = new DeltaMZCriteriaPanel();
		}
		return deltaMZCriteriaPanel0;
	}
	
	private SequestCriteriaPanel getSequestCriteriaPanel0() {
		if (sequestCriteriaPanel0 == null) {
			sequestCriteriaPanel0 = new SequestCriteriaPanel();
		}
		return sequestCriteriaPanel0;
	}

	private MascotCriteriaPanel2 getMascotCriteriaPanel0() {
		if (mascotCriteriaPanel0 == null) {
			mascotCriteriaPanel0 = new MascotCriteriaPanel2();
		}
		return mascotCriteriaPanel0;
	}

	private OMSSACriteriaPanel getOMSSACriteriaPanel0() {
		if (this.OMSSACriteriaPanel0 == null) {
			this.OMSSACriteriaPanel0 = new OMSSACriteriaPanel();
		}
		return this.OMSSACriteriaPanel0;
	}

	private XTandemCriteriaPanel getXTandemCriteriaPanel0() {
		if (this.xtandemCriteriaPanel0 == null) {
			this.xtandemCriteriaPanel0 = new XTandemCriteriaPanel();
		}
		return this.xtandemCriteriaPanel0;
	}

	private CruxCriteriaPanel getCruxCriteriaPanel0() {
		if (this.cruxCriteriaPanel0 == null) {
			this.cruxCriteriaPanel0 = new CruxCriteriaPanel();
		}
		return this.cruxCriteriaPanel0;
	}

	private MascotApivaseCriteriaPanel getmascotApivaseCriteriaPanel0() {
		if (this.mascotApivaseCriteriaPanel0 == null) {
			this.mascotApivaseCriteriaPanel0 = new MascotApivaseCriteriaPanel();
		}
		return this.mascotApivaseCriteriaPanel0;
	}

	private SequestApivaseCriteriaPanel getsequestApivaseCriteriaPanel0() {
		if (this.sequestApivaseCriteriaPanel0 == null) {
			this.sequestApivaseCriteriaPanel0 = new SequestApivaseCriteriaPanel();
		}
		return this.sequestApivaseCriteriaPanel0;
	}

	/**
	 * Return the current showing and set criteria in the criteria panel
	 * 
	 * @return
	 */
	public IPeptideCriteria getCriteria() {
		return this.currentCriteriaPanel.getCriteria();
	}

	/**
	 * Add the action of the filter button
	 * 
	 * @param listener
	 */
	public void addFilterActionListener(ActionListener listener) {
		this.getJButtonApplyFilter().addActionListener(listener);
		this.getJButtonApplyFilter().setVisible(true);
	}
	
	public void addSFOERActionListener(ISFOERListener listener) {
		if(listener != null) {
			spanel = new SFOERActionPanel(listener);
			add(spanel, new Constraints(new Leading(371, 10, 10), new Leading(8, 12, 12)));
			
			this.updateUI();
		}
	}

	/**
	 * Add the action of the filter button
	 * 
	 * @param listener
	 */
	public void removeFilterActionListener(ActionListener listener) {
		this.getJButtonApplyFilter().removeActionListener(listener);

		ActionListener[] listeners = this.getJButtonApplyFilter().getActionListeners();
		if (listeners == null || listeners.length == 0) {
			//No action for this button, set invisible
			this.getJButtonApplyFilter().setVisible(false);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getSource();
		if (obj == this.getJComboBox0()) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				PeptideType type = (PeptideType) getJComboBox0()
				        .getSelectedItem();
				this.selectCriteriaPanel(type);
			}

			return;
		}

		if (obj == this.getJCheckBoxUseProb()) {
			
			if(this.getJCheckBoxUseProb().isSelected()) {
				this.getJCheckBoxDeltaMz().setSelected(false);
				this.selectProbabilityPanel();
			}
			else {
				PeptideType type = (PeptideType) getJComboBox0().getSelectedItem();
				this.selectCriteriaPanel(type);
			}
			
			return ;
		}
		
		if (obj == this.getJCheckBoxDeltaMz()) {
			
			if(this.getJCheckBoxDeltaMz().isSelected()) {
				this.getJCheckBoxUseProb().setSelected(false);
				this.selectDeltaMzPanel();
			}
			else {
				PeptideType type = (PeptideType) getJComboBox0().getSelectedItem();
				this.selectCriteriaPanel(type);
			}
			
			return ;
		}
	}
}
