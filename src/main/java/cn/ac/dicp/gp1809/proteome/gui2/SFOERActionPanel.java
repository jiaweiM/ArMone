/* 
 ******************************************************************************
 * File:SFOERActionPanel.java * * * Created on 2011-8-31
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import cn.ac.dicp.gp1809.proteome.gui.Criterias.ISFOERListener;
import javax.swing.GroupLayout.Alignment;

public class SFOERActionPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private ISFOERListener listener;
	
	private JFormattedTextField jFormattedTextField0;

	private JButton jButtonAction;
	private JLabel jLabel1;

	private JRadioButton jRadioButton2FP;

	private JRadioButton jRadioButtonFP;
	
	private JRadioButton jRadioButtonFPTP;

	private ButtonGroup buttonGroup1;

	private JLabel jLabel0;

	private JButton jButtonDispose;

	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public SFOERActionPanel() {
		initComponents();
	}
	
	public SFOERActionPanel(ISFOERListener s) {
		this.listener = s;
		initComponents();
		
		this.jButtonAction.addActionListener(s);
		this.jButtonAction.addActionListener(this);
	}

	private void initComponents() {
		initButtonGroup1();
		setSize(287, 86);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(11)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJRadioButton2FP())
						.addComponent(getJRadioButtonFP())
						.addComponent(getJRadioButtonFPTP()))
					.addGap(12)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJLabel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(11)
							.addComponent(getJFormattedTextField0(), javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
					.addGap(5)
					.addComponent(getJLabel1(), javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(9)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJButton0(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(getJButtonDispose(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(9)
					.addComponent(getJRadioButton2FP())
					.addGap(6)
					.addComponent(getJRadioButtonFP())
					.addGap(6)
					.addComponent(getJRadioButtonFPTP()))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(32)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(1)
							.addComponent(getJLabel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addComponent(getJFormattedTextField0(), javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(33)
					.addComponent(getJLabel1()))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addComponent(getJButton0())
					.addGap(9)
					.addComponent(getJButtonDispose()))
		);
		setLayout(groupLayout);
	}

	protected JButton getJButtonDispose() {
		if (jButtonDispose == null) {
			jButtonDispose = new JButton();
			jButtonDispose.setText("dispose");
		}
		return jButtonDispose;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("<");
		}
		return jLabel0;
	}

	private void initButtonGroup1() {
		buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(getJRadioButton2FP());
		buttonGroup1.add(getJRadioButtonFP());
		buttonGroup1.add(getJRadioButtonFPTP());
	}

	private JRadioButton getJRadioButton2FP() {
		if (jRadioButton2FP == null) {
			jRadioButton2FP = new JRadioButton();
			jRadioButton2FP.setSelected(true);
			jRadioButton2FP.setText("2*FP/(TP+FP)");
		}
		return jRadioButton2FP;
	}

	private JRadioButton getJRadioButtonFP() {
		if (jRadioButtonFP == null) {
			jRadioButtonFP = new JRadioButton();
			jRadioButtonFP.setText("FP/(TP+FP)");
		}
		return jRadioButtonFP;
	}

	private JRadioButton getJRadioButtonFPTP() {
		if (jRadioButtonFPTP == null) {
			jRadioButtonFPTP = new JRadioButton();
			jRadioButtonFPTP.setText("FP/TP");
		}
		return jRadioButtonFPTP;
	}

	private JLabel getJLabel1() {
    	if (jLabel1 == null) {
    		jLabel1 = new JLabel();
    		jLabel1.setText("%");
    	}
    	return jLabel1;
    }

	protected JButton getJButton0() {
    	if (jButtonAction == null) {
    		jButtonAction = new JButton();
    		jButtonAction.setText("optimize");
    	}
    	return jButtonAction;
    }

	private JFormattedTextField getJFormattedTextField0() {
    	if (jFormattedTextField0 == null) {
    		jFormattedTextField0 = new JFormattedTextField();
    		jFormattedTextField0.setHorizontalAlignment(SwingConstants.RIGHT);
    		jFormattedTextField0.setText("1.0");
    	}
    	return jFormattedTextField0;
    }

	public float getFDR(){
		String ss = this.getJFormattedTextField0().getText();
		return Float.parseFloat(ss)/100.0f;
	}
	
	public short getOptimizeType(){
		if(this.getJRadioButton2FP().isSelected()){
			return 0;
		}else if(this.getJRadioButtonFP().isSelected()){
			return 1;
		}else if(this.getJRadioButtonFPTP().isSelected()){
			return 2;
		}else{
			return 0;
		}
	}
	
	@Override
    public void actionPerformed(ActionEvent arg0) {
		
		Object obj = arg0.getSource();
		if(obj == this.getJButton0()) {
			if(this.listener != null) {
				listener.setMaxFDR(Double.parseDouble(this.getJFormattedTextField0().getText())/100);
			}
		}
		
    }
}
