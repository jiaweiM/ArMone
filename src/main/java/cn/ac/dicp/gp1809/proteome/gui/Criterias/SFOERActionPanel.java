/* 
 ******************************************************************************
 * File: SFOERActionPanel.java * * * Created on 02-26-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import javax.swing.GroupLayout.Alignment;

/**
 * 
 * @author Xinning
 * @version 0.1.1, 09-14-2010, 15:46:38
 */
public class SFOERActionPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	private ISFOERListener listener;
	
	private JFormattedTextField jFormattedTextField0;
	private JCheckBox jCheckBox0;
	private JButton jButtonAction;
	private JLabel jLabel1;

	private JCheckBox jCheckBox2;

	private JLabel jLabel2;

	private ButtonGroup buttonGroup1;

	private JRadioButton jRadioButtonFDR;

	private JRadioButton jRadioButtonFPR;

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
    	setSize(223, 86);
    	GroupLayout groupLayout = new GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(14)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addComponent(getJRadioButtonFDR())
    						.addGap(12)
    						.addComponent(getJRadioButtonFPR(), GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
    						.addGap(18)
    						.addComponent(getJLabel2(), GroupLayout.PREFERRED_SIZE, 11, GroupLayout.PREFERRED_SIZE)
    						.addGap(1)
    						.addComponent(getJFormattedTextField0(), GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
    						.addGap(4)
    						.addComponent(getJLabel1(), GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    							.addComponent(getJCheckBox0())
    							.addComponent(getJCheckBox2(), GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE))
    						.addGap(12)
    						.addComponent(getJButton0(), GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE))))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(9)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(3)
    						.addComponent(getJRadioButtonFDR()))
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(3)
    						.addComponent(getJRadioButtonFPR()))
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(3)
    						.addComponent(getJLabel2()))
    					.addComponent(getJFormattedTextField0(), GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(3)
    						.addComponent(getJLabel1())))
    				.addGap(11)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addComponent(getJCheckBox0())
    						.addGap(2)
    						.addComponent(getJCheckBox2()))
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(4)
    						.addComponent(getJButton0()))))
    	);
    	setLayout(groupLayout);
    }

	private JRadioButton getJRadioButtonFPR() {
    	if (jRadioButtonFPR == null) {
    		jRadioButtonFPR = new JRadioButton();
    		jRadioButtonFPR.setText("FPR");
    	}
    	return jRadioButtonFPR;
    }

	private JRadioButton getJRadioButtonFDR() {
    	if (jRadioButtonFDR == null) {
    		jRadioButtonFDR = new JRadioButton();
    		jRadioButtonFDR.setSelected(true);
    		jRadioButtonFDR.setText("FDR");
    	}
    	return jRadioButtonFDR;
    }

	private void initButtonGroup1() {
    	buttonGroup1 = new ButtonGroup();
    	buttonGroup1.add(getJRadioButtonFDR());
    	buttonGroup1.add(getJRadioButtonFPR());
    }

	private JLabel getJLabel2() {
    	if (jLabel2 == null) {
    		jLabel2 = new JLabel();
    		jLabel2.setText("<");
    	}
    	return jLabel2;
    }

	private JCheckBox getJCheckBox2() {
    	if (jCheckBox2 == null) {
    		jCheckBox2 = new JCheckBox();
    		jCheckBox2.setText("  Use Delta M/Z");
    	}
    	return jCheckBox2;
    }

	private JLabel getJLabel1() {
    	if (jLabel1 == null) {
    		jLabel1 = new JLabel();
    		jLabel1.setText("%");
    	}
    	return jLabel1;
    }

	private JButton getJButton0() {
    	if (jButtonAction == null) {
    		jButtonAction = new JButton();
    		jButtonAction.setText("optimize");
    	}
    	return jButtonAction;
    }

	private JCheckBox getJCheckBox0() {
    	if (jCheckBox0 == null) {
    		jCheckBox0 = new JCheckBox();
    		jCheckBox0.setSelected(true);
    		jCheckBox0.setText("  Use Sp & Rsp");
    	}
    	return jCheckBox0;
    }

	private JFormattedTextField getJFormattedTextField0() {
    	if (jFormattedTextField0 == null) {
    		jFormattedTextField0 = new JFormattedTextField();
    		jFormattedTextField0.setHorizontalAlignment(SwingConstants.RIGHT);
    		jFormattedTextField0.setText("1");
    	}
    	return jFormattedTextField0;
    }

	@Override
    public void actionPerformed(ActionEvent arg0) {
		
		Object obj = arg0.getSource();
		if(obj == this.getJButton0()) {
			if(this.listener != null) {
				listener.setMaxFDR(Double.parseDouble(this.getJFormattedTextField0().getText())/100);
				listener.setUseSpRsp(this.getJCheckBox0().isSelected());
				listener.setUseDeltaMZ(this.getJCheckBox2().isSelected());
				listener.setUseFDR(this.getJRadioButtonFDR().isSelected());
			}
		}
		
    }
}
