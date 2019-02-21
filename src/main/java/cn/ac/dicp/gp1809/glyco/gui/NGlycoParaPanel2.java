/* 
 ******************************************************************************
 * File: NGlycoParaPanel2.java * * * Created on 2014-2-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.gui;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import javax.swing.*;
import java.awt.*;

/**
 * @author ck
 */
public class NGlycoParaPanel2 extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField jTextFieldInten;
	private JLabel jLabelInten;
	private JLabel jLabelIntenPer;
	private JLabel jLabelMZ;
	private JTextField jTextFieldMZ;
	private JLabel jLabelPPM;
	private JLabel jLabelRT;
	private JTextField jTextFieldRT;
	private JLabel jLabelRtMin;
	private JLabel jLabelTopn;
	private JTextField jTextFieldTopn;
	private JLabel jLabelTopnSturc;

	private JLabel jLabelDeLabel;
	private JRadioButton jRadioButton0;
	private JRadioButton jRadioButton1;
	private ButtonGroup group;
	private boolean label;
	private boolean oGlyco;
	private JCheckBox jCheckBoxOGlyco1;
	private JCheckBox jCheckBoxOGlyco2;
	private JCheckBox jCheckBoxOGlyco3;	
	private JTextField jTextFieldOGlyco1;
	private JTextField jTextFieldOGlyco2;
	private JTextField jTextFieldOGlyco3;

	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";

	public NGlycoParaPanel2() {
		initComponents();
	}
	
	public NGlycoParaPanel2(boolean noPepMatch) {
		initComponents();
		if(noPepMatch){
			this.getJLabelRtMin().setEnabled(false);
			this.getJTextFieldRT().setEnabled(false);
			this.getJLabelRT().setEnabled(false);
		}
	}
	
	public NGlycoParaPanel2(boolean label, boolean oGlyco) {
		this.label = label;
		this.oGlyco = oGlyco;
		initComponents();
	}

	private void initComponents() {
		
		setLayout(new GroupLayout());
		add(getJLabelInten(), new Constraints(new Leading(36, 10, 10), new Leading(20, 10, 10)));
		add(getJLabelMZ(), new Constraints(new Leading(36, 10, 10), new Leading(65, 10, 10)));
		add(getJLabelRT(), new Constraints(new Leading(36, 10, 10), new Leading(155, 10, 10)));
		add(getJLabelTopn(), new Constraints(new Leading(36, 10, 10), new Leading(110, 10, 10)));
		add(getJTextFieldInten(), new Constraints(new Leading(222, 36, 10, 10), new Leading(20, 10, 10)));
		add(getJTextFieldMZ(), new Constraints(new Leading(222, 36, 10, 10), new Leading(65, 10, 10)));
		add(getJTextFieldRT(), new Constraints(new Leading(222, 36, 10, 10), new Leading(155, 10, 10)));
		add(getJTextFieldTopn(), new Constraints(new Leading(222, 36, 10, 10), new Leading(110, 10, 10)));
		add(getJLabelIntenPer(), new Constraints(new Leading(272, 10, 10), new Leading(20, 10, 10)));
		add(getJLabelPPM(), new Constraints(new Leading(272, 10, 10), new Leading(65, 10, 10)));
		add(getJLabelRtMin(), new Constraints(new Leading(272, 10, 10), new Leading(155, 10, 10)));
		add(getJLabelTopnStruc(), new Constraints(new Leading(272, 10, 10), new Leading(110, 10, 10)));
		add(getJLabelDeLabel(), new Constraints(new Leading(36, 10, 10), new Leading(195, 10, 10)));
		add(getJRadioButton0(), new Constraints(new Leading(215, 10, 10), new Leading(195, 10, 10)));
		add(getJRadioButton1(), new Constraints(new Leading(290, 10, 10), new Leading(195, 10, 10)));
//		add(getJCheckBoxOGlyco1(), new Constraints(new Leading(35, 10, 10), new Leading(195, 10, 10)));
//		add(getJCheckBoxOGlyco2(), new Constraints(new Leading(155, 10, 10), new Leading(195, 10, 10)));
//		add(getJCheckBoxOGlyco3(), new Constraints(new Leading(275, 10, 10), new Leading(195, 10, 10)));
//		add(getJTextFieldOGlyco1(), new Constraints(new Leading(60, 70, 10, 10), new Leading(195, 25, 10, 10)));
//		add(getJTextFieldOGlyco2(), new Constraints(new Leading(180, 70, 10, 10), new Leading(195, 25, 10, 10)));
//		add(getJTextFieldOGlyco3(), new Constraints(new Leading(300, 70, 10, 10), new Leading(195, 25, 10, 10)));
		getGroup();
		setSize(460, 240);
	}

	private JTextField getJTextFieldOGlyco3() {
		if (jTextFieldOGlyco3 == null) {
			jTextFieldOGlyco3 = new JTextField();
			jTextFieldOGlyco3.setText("");
			if(oGlyco)
				jTextFieldOGlyco3.setEnabled(true);
			else
				jTextFieldOGlyco3.setEnabled(false);
		}
		return jTextFieldOGlyco3;
	}

	private JTextField getJTextFieldOGlyco2() {
		if (jTextFieldOGlyco2 == null) {
			jTextFieldOGlyco2 = new JTextField();
			jTextFieldOGlyco2.setText("365.1322");
			if(oGlyco)
				jTextFieldOGlyco2.setEnabled(true);
			else
				jTextFieldOGlyco2.setEnabled(false);
		}
		return jTextFieldOGlyco2;
	}

	private JTextField getJTextFieldOGlyco1() {
		if (jTextFieldOGlyco1 == null) {
			jTextFieldOGlyco1 = new JTextField();
			jTextFieldOGlyco1.setText("203.0794");
			if(oGlyco)
				jTextFieldOGlyco1.setEnabled(true);
			else
				jTextFieldOGlyco1.setEnabled(false);
		}
		return jTextFieldOGlyco1;
	}
	
	private JCheckBox getJCheckBoxOGlyco3() {
		if (jCheckBoxOGlyco3 == null) {
			jCheckBoxOGlyco3 = new JCheckBox();
			if(oGlyco)
				jCheckBoxOGlyco3.setEnabled(true);
			else
				jCheckBoxOGlyco3.setEnabled(false);
		}
		return jCheckBoxOGlyco3;
	}

	private JCheckBox getJCheckBoxOGlyco2() {
		if (jCheckBoxOGlyco2 == null) {
			jCheckBoxOGlyco2 = new JCheckBox();
			if(oGlyco)
				jCheckBoxOGlyco2.setEnabled(true);
			else
				jCheckBoxOGlyco2.setEnabled(false);
		}
		return jCheckBoxOGlyco2;
	}

	private JCheckBox getJCheckBoxOGlyco1() {
		if (jCheckBoxOGlyco1 == null) {
			jCheckBoxOGlyco1 = new JCheckBox();
			if(oGlyco)
				jCheckBoxOGlyco1.setEnabled(true);
			else
				jCheckBoxOGlyco1.setEnabled(false);
		}
		return jCheckBoxOGlyco1;
	}
	
	private ButtonGroup getGroup(){
		if (group == null) {
			group = new ButtonGroup();
			group.add(getJRadioButton0());
			group.add(getJRadioButton1());
		}
		return group;
	}

	private JRadioButton getJRadioButton1() {
		if (jRadioButton1 == null) {
			jRadioButton1 = new JRadioButton();
			jRadioButton1.setText("No");
			if(!label)
				jRadioButton1.setEnabled(false);
			else
				jRadioButton1.setEnabled(true);
		}
		return jRadioButton1;
	}

	private JRadioButton getJRadioButton0() {
		if (jRadioButton0 == null) {
			jRadioButton0 = new JRadioButton();
			jRadioButton0.setSelected(true);
			jRadioButton0.setText("Yes");
			if(!label)
				jRadioButton0.setEnabled(false);
			else
				jRadioButton0.setEnabled(true);
		}
		return jRadioButton0;
	}

	private JLabel getJLabelDeLabel() {
		if (jLabelDeLabel == null) {
			jLabelDeLabel = new JLabel();
			jLabelDeLabel.setText("De_Glyco_peptide Labeled?");
			if(!label)
				jLabelDeLabel.setEnabled(false);
			else
				jLabelDeLabel.setEnabled(true);
		}
		return jLabelDeLabel;
	}
	
	private JLabel getJLabelTopn() {
		if (jLabelTopn == null) {
			jLabelTopn = new JLabel();
			jLabelTopn.setText("Match top ");
		}
		return jLabelTopn;
	}

	private JTextField getJTextFieldTopn() {
		if (jTextFieldTopn == null) {
			jTextFieldTopn = new JTextField();
			jTextFieldTopn.setText("3");
		}
		return jTextFieldTopn;
	}

	private JLabel getJLabelTopnStruc() {
		if (jLabelTopnSturc == null) {
			jLabelTopnSturc = new JLabel();
			jLabelTopnSturc.setText("structures");
		}
		return jLabelTopnSturc;
	}

	private JLabel getJLabelRtMin() {
		if (jLabelRtMin == null) {
			jLabelRtMin = new JLabel();
			jLabelRtMin.setText("min");
		}
		return jLabelRtMin;
	}

	private JTextField getJTextFieldRT() {
		if (jTextFieldRT == null) {
			jTextFieldRT = new JTextField();
			jTextFieldRT.setText("30");
		}
		return jTextFieldRT;
	}

	private JLabel getJLabelRT() {
		if (jLabelRT == null) {
			jLabelRT = new JLabel();
			jLabelRT.setText("Retention Time Tolerance");
		}
		return jLabelRT;
	}

	private JLabel getJLabelPPM() {
		if (jLabelPPM == null) {
			jLabelPPM = new JLabel();
			jLabelPPM.setText("ppm");
		}
		return jLabelPPM;
	}

	private JTextField getJTextFieldMZ() {
		if (jTextFieldMZ == null) {
			jTextFieldMZ = new JTextField();
			jTextFieldMZ.setText("50");
		}
		return jTextFieldMZ;
	}

	private JLabel getJLabelMZ() {
		if (jLabelMZ == null) {
			jLabelMZ = new JLabel();
			jLabelMZ.setText("MZ Tolerance");
		}
		return jLabelMZ;
	}

	private JLabel getJLabelIntenPer() {
		if (jLabelIntenPer == null) {
			jLabelIntenPer = new JLabel();
			jLabelIntenPer.setText("%");
		}
		return jLabelIntenPer;
	}

	private JLabel getJLabelInten() {
		if (jLabelInten == null) {
			jLabelInten = new JLabel();
			jLabelInten.setText("Intensity Threshold");
		}
		return jLabelInten;
	}

	private JTextField getJTextFieldInten() {
		if (jTextFieldInten == null) {
			jTextFieldInten = new JTextField();
			jTextFieldInten.setText("0.1");
		}
		return jTextFieldInten;
	}
	
	public GlycoJudgeParameter getPara(){
		
		float inten = Float.parseFloat(this.getJTextFieldInten().getText())/100f;
		float mz = Float.parseFloat(this.getJTextFieldMZ().getText());
		float rt = Float.parseFloat(this.getJTextFieldRT().getText());
		boolean label = this.getJRadioButton0().isSelected();
		int topn = Integer.parseInt(this.getJTextFieldTopn().getText());
		
		GlycoJudgeParameter para = new GlycoJudgeParameter(inten, mz, 0.1f, 500f, 0.3f, rt, topn);
		para.setDeGlycoLabel(label);
		return para;
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
				frame.setTitle("GlycoParaPanel");
				NGlycoParaPanel content = new NGlycoParaPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
