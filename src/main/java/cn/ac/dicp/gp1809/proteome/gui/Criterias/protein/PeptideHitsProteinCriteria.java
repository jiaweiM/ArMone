/* 
 ******************************************************************************
 * File: PeptideHitsProteinCriteria.java * * * Created on 06-07-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias.protein;

import java.awt.Dimension;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.protein.ProteinPeptideHitCountFilter;

/**
 * Protein criteria setter of peptide hit count
 * 
 * @author Xinning
 * @version 0.1, 06-07-2010, 14:35:13
 */
public class PeptideHitsProteinCriteria extends JPanel implements
        IProteinFilterSetter {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel0;
	private JFormattedTextField jFormattedTextField0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.gui.Criterias.protein.IProteinFilterSetter#getProteinFilter()
	 */
	@Override
	public IProteinCriteria getProteinFilter() {
		int pepcount = Integer.parseInt(this.getJFormattedTextField0().getText());
		return new ProteinPeptideHitCountFilter(pepcount);
	}

	public PeptideHitsProteinCriteria() {
		initComponents();
    	this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));
	}

	private void initComponents() {
//    	setMinimumSize(new Dimension(184, 30));
    	setLayout(new GroupLayout());
    	add(getJLabel0(), new Constraints(new Leading(6, 6, 6), new Leading(6, 6, 6)));
    	add(getJFormattedTextField0(), new Constraints(new Leading(122, 28, 6, 6), new Leading(0, 6, 6)));
    	setSize(160, 30);
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("Peptide hit count >=");
    	}
    	return jLabel0;
    }

	private JFormattedTextField getJFormattedTextField0() {
    	if (jFormattedTextField0 == null) {
    		jFormattedTextField0 = new JFormattedTextField(Integer.class);
    		jFormattedTextField0.setHorizontalAlignment(SwingConstants.CENTER);
    		jFormattedTextField0.setText("2");
    	}
    	return jFormattedTextField0;
    }

	public void switchOn(){
		this.jFormattedTextField0.setEnabled(true);
		this.jLabel0.setEnabled(true);
	}
	
	public void switchOff(){
		this.jFormattedTextField0.setEnabled(false);
		this.jLabel0.setEnabled(false);
	}
}
