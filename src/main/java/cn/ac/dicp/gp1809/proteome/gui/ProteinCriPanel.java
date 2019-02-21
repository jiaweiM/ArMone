/* 
 ******************************************************************************
 * File:ProteinCriPanel.java * * * Created on 2010-12-17
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.protein.DecoyProteinRemovalCriteria;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.protein.PeptideCountProteinCriteria;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.protein.PeptideHitsProteinCriteria;

//VS4E -- DO NOT REMOVE THIS LINE!
public class ProteinCriPanel extends JPanel {

	private JCheckBox jCheckBoxUniCount;
	private JCheckBox jCheckBoxPepCount;
	private JCheckBox jCheckBoxRemoveDecoy;
	private PeptideCountProteinCriteria peptideCountProteinCriteria0;
	private PeptideHitsProteinCriteria peptideHitsProteinCriteria0;
	private DecoyProteinRemovalCriteria decoyProteinRemovalCriteria0;
	
	private static final long serialVersionUID = 1L;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public ProteinCriPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getPeptideCountProteinCriteria0(), new Constraints(new Leading(57, 10, 10), new Leading(20, 30, 73, 73)));
		add(getPeptideHitsProteinCriteria0(), new Constraints(new Leading(280, 10, 10), new Leading(20, 30, 73, 73)));
		add(getDecoyProteinRemovalCriteria0(), new Constraints(new Leading(57, 10, 10), new Leading(60, 73, 73)));
		add(getJCheckBoxUniCount(), new Constraints(new Leading(37, 10, 10), new Leading(25, 73, 73)));
		add(getJCheckBoxRemoveDecoy(), new Constraints(new Leading(37, 10, 10), new Leading(65, 73, 73)));
		add(getJCheckBoxPepCount(), new Constraints(new Leading(261, 20, 10, 10), new Leading(25, 48, 48)));
		setSize(470, 100);
	}

	private DecoyProteinRemovalCriteria getDecoyProteinRemovalCriteria0() {
    	if (decoyProteinRemovalCriteria0 == null) {
    		decoyProteinRemovalCriteria0 = new DecoyProteinRemovalCriteria();
    		decoyProteinRemovalCriteria0.setMinimumSize(new Dimension(184, 30));
    	}
    	return decoyProteinRemovalCriteria0;
    }

	private PeptideHitsProteinCriteria getPeptideHitsProteinCriteria0() {
    	if (peptideHitsProteinCriteria0 == null) {
    		peptideHitsProteinCriteria0 = new PeptideHitsProteinCriteria();
    		peptideHitsProteinCriteria0.setMinimumSize(new Dimension(184, 30));
    	}
    	return peptideHitsProteinCriteria0;
    }

	private PeptideCountProteinCriteria getPeptideCountProteinCriteria0() {
    	if (peptideCountProteinCriteria0 == null) {
    		peptideCountProteinCriteria0 = new PeptideCountProteinCriteria();
    		peptideCountProteinCriteria0.setMinimumSize(new Dimension(184, 30));
    	}
    	return peptideCountProteinCriteria0;
    }

	private JCheckBox getJCheckBoxRemoveDecoy() {
		if (jCheckBoxRemoveDecoy == null) {
			jCheckBoxRemoveDecoy = new JCheckBox();
			jCheckBoxRemoveDecoy.setSelected(true);
		}
		return jCheckBoxRemoveDecoy;
	}

	private JCheckBox getJCheckBoxPepCount() {
    	if (jCheckBoxPepCount == null) {
    		jCheckBoxPepCount = new JCheckBox();
    	}
    	return jCheckBoxPepCount;
    }

	private JCheckBox getJCheckBoxUniCount() {
		if (jCheckBoxUniCount == null) {
			jCheckBoxUniCount = new JCheckBox();
		}
		return jCheckBoxUniCount;
	}
	
	public void switchOn(){
		this.setEnabled(true);
		this.getJCheckBoxPepCount().setEnabled(true);
		this.getJCheckBoxRemoveDecoy().setEnabled(true);
		this.getJCheckBoxUniCount().setEnabled(true);
		this.getPeptideCountProteinCriteria0().switchOn();
		this.getPeptideHitsProteinCriteria0().switchOn();
		this.getDecoyProteinRemovalCriteria0().switchOn();
	}
	
	public void switchOff(){
		this.setEnabled(false);
		this.getJCheckBoxPepCount().setEnabled(false);
		this.getJCheckBoxRemoveDecoy().setEnabled(false);
		this.getJCheckBoxUniCount().setEnabled(false);
		this.getPeptideCountProteinCriteria0().switchOff();
		this.getPeptideHitsProteinCriteria0().switchOff();
		this.getDecoyProteinRemovalCriteria0().switchOff();
	}

	public ArrayList<IProteinCriteria> getProCriteria(){
		
		ArrayList<IProteinCriteria> procriteria = new ArrayList<IProteinCriteria>();
		
		if(this.getJCheckBoxRemoveDecoy().isSelected()) {
			procriteria.add(this.getDecoyProteinRemovalCriteria0().getProteinFilter());
		}
		
		
		if(this.getJCheckBoxUniCount().isSelected()) {
			procriteria.add(this.getPeptideCountProteinCriteria0().getProteinFilter());
		}
		
		if(this.getJCheckBoxPepCount().isSelected()) {
			procriteria.add(this.getPeptideHitsProteinCriteria0().getProteinFilter());
		}
		
		return procriteria;
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
				frame.setTitle("ProteinCriPanel");
				ProteinCriPanel content = new ProteinCriPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
