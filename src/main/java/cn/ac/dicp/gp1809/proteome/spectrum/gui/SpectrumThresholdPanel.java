/* 
 ******************************************************************************
 * File: SpectrumThresholdPanel.java * * * Created on 04-23-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;

/**
 * 
 * @author Xinning
 * @version 0.1, 04-23-2009, 15:09:11
 */
public class SpectrumThresholdPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private DecimalFormat DF0 = new DecimalFormat("0");
	private ISpectrumThreshold threshold;

	private JFormattedTextField jFormattedTextFieldIntens;
	private JFormattedTextField jFormattedTextFieldToler;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JButton jButton0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public SpectrumThresholdPanel() {
		threshold = SpectrumThreshold.PERCENT_10_INTENSE_THRESHOLD;
		initComponents();

		this.showThreshold();
	}

	private void initComponents() {
    	setMinimumSize(new Dimension(561, 28));
    	setPreferredSize(new Dimension(561, 28));
    	setLayout(new GroupLayout());
    	add(getJFormattedTextFieldToler(), new Constraints(new Leading(183, 6, 6), new Leading(3, 6, 6)));
    	add(getJLabel0(), new Constraints(new Leading(480, 6, 6), new Leading(6, 6, 6)));
    	add(getJLabel1(), new Constraints(new Leading(16, 6, 6), new Leading(6, 6, 6)));
    	add(getJLabel2(), new Constraints(new Leading(254, 6, 6), new Leading(6, 6, 6)));
    	add(getJFormattedTextFieldIntens(), new Constraints(new Leading(424, 6, 6), new Leading(3, 6, 6)));
    	add(getJButton0(), new Constraints(new Leading(509, 6, 6), new Leading(0, 26, 6, 6)));
    	setSize(561, 28);
    }

	private JButton getJButton0() {
		if (jButton0 == null) {
			jButton0 = new JButton();
			jButton0.setText("Set");
			jButton0.setMinimumSize(new Dimension(46, 25));
			jButton0.setMaximumSize(new Dimension(46, 25));
			jButton0.addActionListener(this);
		}
		return jButton0;
	}

	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Minimum intensity percentage");
		}
		return jLabel2;
	}

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Fragement tolerance (+- Da)");
		}
		return jLabel1;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("%");
		}
		return jLabel0;
	}

	private JFormattedTextField getJFormattedTextFieldToler() {
		if (jFormattedTextFieldToler == null) {
			jFormattedTextFieldToler = new JFormattedTextField(new Float(0));
			jFormattedTextFieldToler.setHorizontalAlignment(SwingConstants.CENTER);
			jFormattedTextFieldToler.setMinimumSize(new Dimension(30, 21));
			jFormattedTextFieldToler.setPreferredSize(new Dimension(50, 21));
		}
		return jFormattedTextFieldToler;
	}

	private JFormattedTextField getJFormattedTextFieldIntens() {
		if (jFormattedTextFieldIntens == null) {
			jFormattedTextFieldIntens = new JFormattedTextField(new Integer(0));
			jFormattedTextFieldIntens.setHorizontalAlignment(SwingConstants.CENTER);
			jFormattedTextFieldIntens.setMinimumSize(new Dimension(30, 21));
			jFormattedTextFieldIntens.setPreferredSize(new Dimension(50, 21));
		}
		return jFormattedTextFieldIntens;
	}

	/**
	 * Show the current threshold in the panel
	 */
	private void showThreshold() {
		jFormattedTextFieldIntens.setText(DF0.format(this.threshold
		        .getInstensityThreshold() * 100));
		jFormattedTextFieldToler.setText(String.valueOf(this.threshold
		        .getMassTolerance()));
	}

	/**
	 * Add listener when the button of set threshold is pressed
	 * 
	 * @param listener
	 */
	public void addSetThresholdListener(ActionListener listener) {
		ActionListener[] listeners = this.getJButton0().getActionListeners();
		for(ActionListener l : listeners)
			this.getJButton0().removeActionListener(l);
		
		this.getJButton0().addActionListener(listener);
		
		//process the listener as a queue
		for(int i=listeners.length-1; i>=0; i--)
			this.getJButton0().addActionListener(listeners[i]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			double intens = Double.parseDouble(this.getJFormattedTextFieldIntens()
			        .getText())/100d;
			double thres = Double.parseDouble(this.getJFormattedTextFieldToler()
			        .getText());

			SpectrumThreshold threshold = new SpectrumThreshold(thres, intens);
			this.threshold = threshold;
		} catch (Exception ex) {
			this.showThreshold();
			ex.printStackTrace();
		}
	}

	/**
	 * Get the threshold describe by this panel
	 * 
	 * @return
	 */
	public ISpectrumThreshold getThreshold() {
		return this.threshold;
	}
}
