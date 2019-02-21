/* 
 ******************************************************************************
 * File: IonsTypeSettingPanel.java * * * Created on 04-15-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.util.arrayutil.IntArrayList;

/**
 * The match ion type setting panel
 * 
 * @author Xinning
 * @version 0.1, 05-30-2009, 16:18:33
 */
public class IonsTypeSettingPanel extends JPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	private int[] types;

	private JCheckBox jCheckBoxb;
	private JCheckBox jCheckBoxy;
	private JCheckBox jCheckBoxc;
	private JCheckBox jCheckBoxz;
	public IonsTypeSettingPanel() {
		initComponents();
		
		this.refreshTypes();
	}

	private void initComponents() {
    	setBorder(BorderFactory.createTitledBorder(null, "Type of ions", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("Dialog", Font.BOLD, 12),
    			new Color(51, 51, 51)));
    	setLayout(new GroupLayout());
    	add(getJCheckBoxb(), new Constraints(new Leading(0, 6, 6), new Leading(0, 6, 6)));
    	add(getJCheckBoxy(), new Constraints(new Leading(76, 10, 10), new Leading(0, 6, 6)));
    	add(getJCheckBoxc(), new Constraints(new Leading(-1, 6, 6), new Leading(28, 6, 6)));
    	add(getJCheckBoxz(), new Constraints(new Leading(76, 6, 6), new Leading(28, 6, 6)));
    	setSize(156, 147);
    }

	private JCheckBox getJCheckBoxz() {
    	if (jCheckBoxz == null) {
    		jCheckBoxz = new JCheckBox();
    		jCheckBoxz.setText("z");
    		jCheckBoxz.addItemListener(this);
    	}
    	return jCheckBoxz;
    }

	private JCheckBox getJCheckBoxc() {
    	if (jCheckBoxc == null) {
    		jCheckBoxc = new JCheckBox();
    		jCheckBoxc.setText("c");
    		jCheckBoxc.addItemListener(this);
    	}
    	return jCheckBoxc;
    }

	private JCheckBox getJCheckBoxy() {
    	if (jCheckBoxy == null) {
    		jCheckBoxy = new JCheckBox();
    		jCheckBoxy.setSelected(true);
    		jCheckBoxy.setText("y");
    		jCheckBoxy.addItemListener(this);
    	}
    	return jCheckBoxy;
    }

	private JCheckBox getJCheckBoxb() {
    	if (jCheckBoxb == null) {
    		jCheckBoxb = new JCheckBox();
    		jCheckBoxb.setSelected(true);
    		jCheckBoxb.setText("b");
    		jCheckBoxb.addItemListener(this);
    	}
    	return jCheckBoxb;
    }

	/**
	 * Return types of the ions need to be shown
	 * 
	 * @return
	 */
	public int[] getIonTypes() {
		return this.types;
	}

	/**
	 * Refresh the losses
	 */
	private void refreshTypes() {

		IntArrayList list = new IntArrayList();
		if (this.getJCheckBoxb().isSelected()) {
			list.add(Ion.TYPE_B);
		}

		if (this.getJCheckBoxz().isSelected()) {
			list.add(Ion.TYPE_Z);
		}

		if (this.getJCheckBoxy().isSelected()) {
			list.add(Ion.TYPE_Y);
		}

		if (this.getJCheckBoxc().isSelected()) {
			list.add(Ion.TYPE_C);
		}

		this.types = list.size() == 0 ? null : list
		        .toArray();
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
		this.getJCheckBoxb().removeItemListener(listener);
		this.getJCheckBoxz().removeItemListener(listener);
		this.getJCheckBoxy().removeItemListener(listener);
		this.getJCheckBoxc().removeItemListener(listener);
	}
	
	/**
	 * Add the listener when some terms are selected or disselected.
	 * 
	 * @param listener
	 */
	private void addSelectionListener1(ItemListener listener) {
		
		this.getJCheckBoxb().addItemListener(listener);
		this.getJCheckBoxz().addItemListener(listener);
		this.getJCheckBoxy().addItemListener(listener);
		this.getJCheckBoxc().addItemListener(listener);
	}
	

	@Override
	public void itemStateChanged(ItemEvent e) {
		/*
		 * All the selection of disselection will cause the refresh of neutral
		 * loss
		 */
		this.refreshTypes();
	}
}
