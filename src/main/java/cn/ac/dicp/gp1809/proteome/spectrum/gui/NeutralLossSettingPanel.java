/* 
 ******************************************************************************
 * File: NeutralLossSetting.java * * * Created on 04-15-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;

/**
 * The neutral loss setting panel
 * 
 * @author Xinning
 * @version 0.1, 04-15-2009, 14:49:38
 */
public class NeutralLossSettingPanel extends JPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	private NeutralLossInfo[] losses;

	private JCheckBox jCheckBoxMH;
	private JCheckBox jCheckBoxH2O;
	private JCheckBox jCheckBoxH2ONH3;
	private JCheckBox jCheckBoxH3PO4;
	private JCheckBox jCheckBoxH3PO4H2O;
	private JCheckBox jCheckBox2H3PO4;
	private JCheckBox jCheckBoxSpc1;
	private JCheckBox jCheckBoxSpc2;

	private JFormattedTextField jFormattedTextFieldSpc1;

	private JFormattedTextField jFormattedTextFieldSpc2;

	public NeutralLossSettingPanel() {
		initComponents();
		
		this.refreshNeutralLoss();
	}

	private void initComponents() {
    	setBorder(BorderFactory.createTitledBorder(null, "Neutral loss peaks", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("Dialog", Font.BOLD, 12),
    			new Color(51, 51, 51)));
    	setLayout(new GroupLayout());
    	add(getJCheckBoxMH(), new Constraints(new Leading(0, 6, 6), new Leading(0, 6, 6)));
    	add(getJCheckBoxH2O(), new Constraints(new Leading(0, 6, 6), new Leading(24, 6, 6)));
    	add(getJCheckBoxH2ONH3(), new Constraints(new Leading(0, 6, 6), new Leading(48, 6, 6)));
    	add(getJCheckBoxH3PO4(), new Constraints(new Leading(0, 6, 6), new Leading(72, 6, 6)));
    	add(getJCheckBoxH3PO4H2O(), new Constraints(new Leading(0, 6, 6), new Leading(96, 6, 6)));
    	add(getJCheckBox2H3PO4(), new Constraints(new Leading(0, 6, 6), new Leading(120, 6, 6)));
    	add(getJCheckBoxSpc1(), new Constraints(new Leading(0, 6, 6), new Leading(144, 6, 6)));
    	add(getJCheckBoxSpc2(), new Constraints(new Leading(0, 6, 6), new Leading(168, 6, 6)));
    	add(getJFormattedTextFieldSpc1(), new Constraints(new Leading(59, 12, 12), new Leading(141, 12, 12)));
    	add(getJFormattedTextFieldSpc2(), new Constraints(new Leading(59, 12, 12), new Leading(168, 12, 12)));
    	setSize(156, 238);
    }

	private JFormattedTextField getJFormattedTextFieldSpc2() {
    	if (jFormattedTextFieldSpc2 == null) {
    		jFormattedTextFieldSpc2 = new JFormattedTextField(new DecimalFormat("0.##"));
    		jFormattedTextFieldSpc2.setMinimumSize(new Dimension(4, 21));
    		jFormattedTextFieldSpc2.setPreferredSize(new Dimension(56, 21));
    		jFormattedTextFieldSpc2.setOpaque(true);
    		jFormattedTextFieldSpc2.setAutoscrolls(true);
    	}
    	return jFormattedTextFieldSpc2;
    }

	private JFormattedTextField getJFormattedTextFieldSpc1() {
    	if (jFormattedTextFieldSpc1 == null) {
    		jFormattedTextFieldSpc1 = new JFormattedTextField(new DecimalFormat("0.##"));
    		jFormattedTextFieldSpc1.setMinimumSize(new Dimension(4, 21));
    		jFormattedTextFieldSpc1.setPreferredSize(new Dimension(56, 21));
    		jFormattedTextFieldSpc1.setAutoscrolls(true);
    	}
    	return jFormattedTextFieldSpc1;
    }

	private JCheckBox getJCheckBoxSpc2() {
    	if (jCheckBoxSpc2 == null) {
    		jCheckBoxSpc2 = new JCheckBox();
    		jCheckBoxSpc2.setText("MH   - ");
    		jCheckBoxSpc2.addItemListener(this);
    	}
    	return jCheckBoxSpc2;
    }

	private JCheckBox getJCheckBoxSpc1() {
    	if (jCheckBoxSpc1 == null) {
    		jCheckBoxSpc1 = new JCheckBox();
    		jCheckBoxSpc1.setText("MH   -");
    		jCheckBoxSpc1.addItemListener(this);
    	}
    	return jCheckBoxSpc1;
    }

	private JCheckBox getJCheckBox2H3PO4() {
		if (jCheckBox2H3PO4 == null) {
			jCheckBox2H3PO4 = new JCheckBox();
			jCheckBox2H3PO4.setText("MH - 2H3PO4");
			jCheckBox2H3PO4.addItemListener(this);
		}
		return jCheckBox2H3PO4;
	}

	private JCheckBox getJCheckBoxH3PO4H2O() {
		if (jCheckBoxH3PO4H2O == null) {
			jCheckBoxH3PO4H2O = new JCheckBox();
			jCheckBoxH3PO4H2O.setText("MH - H3PO4 -H2O");
			jCheckBoxH3PO4H2O.addItemListener(this);
		}
		return jCheckBoxH3PO4H2O;
	}

	private JCheckBox getJCheckBoxH3PO4() {
		if (jCheckBoxH3PO4 == null) {
			jCheckBoxH3PO4 = new JCheckBox();
			jCheckBoxH3PO4.setText("MH - H3PO4");
			jCheckBoxH3PO4.addItemListener(this);
		}
		return jCheckBoxH3PO4;
	}

	private JCheckBox getJCheckBoxH2ONH3() {
		if (jCheckBoxH2ONH3 == null) {
			jCheckBoxH2ONH3 = new JCheckBox();
			jCheckBoxH2ONH3.setText("MH - 2H2O");
			jCheckBoxH2ONH3.addItemListener(this);
		}
		return jCheckBoxH2ONH3;
	}

	private JCheckBox getJCheckBoxH2O() {
		if (jCheckBoxH2O == null) {
			jCheckBoxH2O = new JCheckBox();
			jCheckBoxH2O.setText("MH - H2O");
			jCheckBoxH2O.addItemListener(this);
		}
		return jCheckBoxH2O;
	}

	private JCheckBox getJCheckBoxMH() {
    	if (jCheckBoxMH == null) {
    		jCheckBoxMH = new JCheckBox();
    		jCheckBoxMH.setSelected(true);
    		jCheckBoxMH.setText("MH");
    		jCheckBoxMH.addItemListener(this);
    	}
    	return jCheckBoxMH;
    }

	/**
	 * Return the neutral loss information need to be shown
	 * 
	 * @return
	 */
	public NeutralLossInfo[] getNeutralLossInfo() {
		return this.losses;
	}

	/**
	 * Refresh the losses
	 */
	private void refreshNeutralLoss() {

		ArrayList<NeutralLossInfo> list = new ArrayList<NeutralLossInfo>();
		if (this.getJCheckBoxMH().isSelected()) {
			list.add(NeutralLossInfo.MH);
		}

		if (this.getJCheckBoxH3PO4().isSelected()) {
			list.add(NeutralLossInfo.MH_H3PO4);
		}

		if (this.getJCheckBoxH3PO4H2O().isSelected()) {
			list.add(NeutralLossInfo.MH_H3PO4_H2O);
		}

		if (this.getJCheckBoxH2O().isSelected()) {
			list.add(NeutralLossInfo.MH_H2O);
		}

		if (this.getJCheckBoxH2ONH3().isSelected()) {
			list.add(NeutralLossInfo.MH_2H2O);
		}

		if (this.getJCheckBox2H3PO4().isSelected()) {
			list.add(NeutralLossInfo.MH_2H3PO4);
		}

		if (this.getJCheckBoxSpc1().isSelected()) {
			String txt = this.getJFormattedTextFieldSpc1().getText();
			double loss = Double.parseDouble(txt);
			list.add(new NeutralLossInfo(loss, "[MH-"+txt+"]"));
		}

		if (this.getJCheckBoxSpc2().isSelected()) {
			String txt = this.getJFormattedTextFieldSpc2().getText();
			double loss = Double.parseDouble(txt);
			list.add(new NeutralLossInfo(loss, "[MH-"+txt+"]"));
		}

		this.losses = list.size() == 0 ? null : list
		        .toArray(new NeutralLossInfo[list.size()]);
	}

	/**
	 * Add the listener when some terms are selected or disselected.
	 * 
	 * @param listener
	 */
	public void addSelectionListener(ItemListener listener) {
		
		/*
		 * The listener is in a stack, make sure the refresh executed first
		 */
		this.reMoveSelectionListener1(this);
		
		this.addSelectionListener1(listener);
		this.addSelectionListener1(this);
	}
	
	

	
	/**
	 * Rmove the listener
	 * 
	 * @param listener
	 */
	private void reMoveSelectionListener1(ItemListener listener) {
		this.getJCheckBoxMH().removeItemListener(listener);
		this.getJCheckBoxH3PO4().removeItemListener(listener);
		this.getJCheckBoxH3PO4H2O().removeItemListener(listener);
		this.getJCheckBoxH2O().removeItemListener(listener);
		this.getJCheckBoxH2ONH3().removeItemListener(listener);
		this.getJCheckBox2H3PO4().removeItemListener(listener);
		this.getJCheckBoxSpc1().removeItemListener(listener);
		this.getJCheckBoxSpc2().removeItemListener(listener);
	}
	
	/**
	 * Add the listener when some terms are selected or disselected.
	 * 
	 * @param listener
	 */
	private void addSelectionListener1(ItemListener listener) {
		
		this.getJCheckBoxMH().addItemListener(listener);
		this.getJCheckBoxH3PO4().addItemListener(listener);
		this.getJCheckBoxH3PO4H2O().addItemListener(listener);
		this.getJCheckBoxH2O().addItemListener(listener);
		this.getJCheckBoxH2ONH3().addItemListener(listener);
		this.getJCheckBox2H3PO4().addItemListener(listener);
		this.getJCheckBoxSpc1().addItemListener(listener);
		this.getJCheckBoxSpc2().addItemListener(listener);
	}
	

	@Override
	public void itemStateChanged(ItemEvent e) {
		
		Object obj = e.getSource();
		
		if(obj == this.getJCheckBoxSpc1()) {
			
			if(this.getJCheckBoxSpc1().isSelected()) {
				if(this.getJFormattedTextFieldSpc1().getText().length()==0) {
					JOptionPane.showMessageDialog(this, "Please set the neutral loss value first.", "Error", JOptionPane.ERROR_MESSAGE);
					this.getJCheckBoxSpc1().setSelected(false);
					return ;
				}
			}
		}
		
		if(obj == this.getJCheckBoxSpc2()) {
			
			if(this.getJCheckBoxSpc2().isSelected()) {
				if(this.getJFormattedTextFieldSpc2().getText().length()==0) {
					JOptionPane.showMessageDialog(this, "Please set the neutral loss value first.", "Error", JOptionPane.ERROR_MESSAGE);
					this.getJCheckBoxSpc2().setSelected(false);
					return ;
				}
			}
		}
		
		/*
		 * All the selection of disselection will cause the refresh of neutral
		 * loss
		 */
		this.refreshNeutralLoss();
	}
}
