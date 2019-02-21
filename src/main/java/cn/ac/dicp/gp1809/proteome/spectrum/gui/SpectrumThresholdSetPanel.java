/* 
 ******************************************************************************
 * File: SpectrumThresholdSetPanel.java * * * Created on 05-26-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.gui;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-26-2009, 21:21:51
 */
public class SpectrumThresholdSetPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel0;
	private JFormattedTextField jFormattedTextFieldTol;
	private JLabel jLabel1;
	private JFormattedTextField jFormattedTextFieldIntens;
	private JLabel jLabel2;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public SpectrumThresholdSetPanel() {
		initComponents();
	}

	private void initComponents() {
    	setLayout(new GroupLayout());
    	add(getJLabel0(), new Constraints(new Leading(11, 10, 10), new Leading(8, 10, 10)));
    	add(getJFormattedTextFieldTol(), new Constraints(new Leading(178, 38, 10, 10), new Leading(6, 22, 6, 6)));
    	add(getJLabel1(), new Constraints(new Leading(242, 165, 6, 6), new Leading(8, 6, 6)));
    	add(getJFormattedTextFieldIntens(), new Constraints(new Leading(413, 38, 6, 6), new Leading(6, 22, 6, 6)));
    	add(getJLabel2(), new Constraints(new Leading(457, 6, 6), new Leading(8, 6, 6)));
    	setSize(485, 36);
    }

	private JLabel getJLabel2() {
    	if (jLabel2 == null) {
    		jLabel2 = new JLabel();
    		jLabel2.setText("%");
    	}
    	return jLabel2;
    }

	private JFormattedTextField getJFormattedTextFieldIntens() {
    	if (jFormattedTextFieldIntens == null) {
    		jFormattedTextFieldIntens = new JFormattedTextField(new Float(10));
    		jFormattedTextFieldIntens.setHorizontalAlignment(SwingConstants.CENTER);
    	}
    	return jFormattedTextFieldIntens;
    }

	private JLabel getJLabel1() {
    	if (jLabel1 == null) {
    		jLabel1 = new JLabel();
    		jLabel1.setText("Minimum intensity percentage");
    	}
    	return jLabel1;
    }

	private JFormattedTextField getJFormattedTextFieldTol() {
    	if (jFormattedTextFieldTol == null) {
    		jFormattedTextFieldTol = new JFormattedTextField(new Float(1));
    		jFormattedTextFieldTol.setHorizontalAlignment(SwingConstants.CENTER);
    	}
    	return jFormattedTextFieldTol;
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("Fragement tolerance (+- Da)");
    	}
    	return jLabel0;
    }
	
	public SpectrumThreshold getThreshold() {
		try {
			double intens = Double.parseDouble(this.getJFormattedTextFieldIntens()
			        .getText())/100d;
			double thres = Double.parseDouble(this.getJFormattedTextFieldTol()
			        .getText());

			SpectrumThreshold threshold = new SpectrumThreshold(thres, intens);
			
			return threshold;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
