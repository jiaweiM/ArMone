/* 
 ******************************************************************************
 * File: GlycoJugParaFrame.java * * * Created on 2011-4-7
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;

//VS4E -- DO NOT REMOVE THIS LINE!
public class NGlycoJugParaFrame extends JFrame implements ActionListener {

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
	private JButton jButtonOK;
	private JButton jButtonCancel;
	
	private GlycoJudgeParameter para;
	private JLabel jLabelDeLabel;
	private JRadioButton jRadioButton0;
	private JRadioButton jRadioButton1;
	private ButtonGroup group;
	private boolean label;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	
	public NGlycoJugParaFrame() {
		initComponents();
		this.para = this.getParaFromFrame();
	}
	
	public NGlycoJugParaFrame(boolean label) {
		this.label = label;
		initComponents();
		this.para = this.getParaFromFrame();
	}

	private void initComponents() {
		setTitle("Parameter");
		setLayout(new GroupLayout());
		add(getJLabelInten(), new Constraints(new Leading(36, 10, 10), new Leading(50, 10, 10)));
		add(getJLabelMZ(), new Constraints(new Leading(36, 10, 10), new Leading(100, 10, 10)));
		add(getJLabelRT(), new Constraints(new Leading(36, 10, 10), new Leading(150, 10, 10)));
		add(getJTextFieldInten(), new Constraints(new Leading(222, 36, 10, 10), new Leading(50, 10, 10)));
		add(getJTextFieldMZ(), new Constraints(new Leading(222, 36, 10, 10), new Leading(100, 10, 10)));
		add(getJTextFieldRT(), new Constraints(new Leading(222, 36, 10, 10), new Leading(150, 10, 10)));
		add(getJLabelIntenPer(), new Constraints(new Leading(272, 10, 10), new Leading(50, 10, 10)));
		add(getJLabelPPM(), new Constraints(new Leading(272, 10, 10), new Leading(100, 10, 10)));
		add(getJLabelRtMin(), new Constraints(new Leading(272, 10, 10), new Leading(150, 10, 10)));
		add(getJButtonOK(), new Constraints(new Leading(90, 10, 10), new Leading(250, 10, 10)));
		add(getJButtonCancel(), new Constraints(new Leading(200, 10, 10), new Leading(250, 10, 10)));
		add(getJLabelDeLabel(), new Constraints(new Leading(36, 10, 10), new Leading(203, 10, 10)));
		add(getJRadioButton0(), new Constraints(new Leading(215, 10, 10), new Leading(200, 10, 10)));
		add(getJRadioButton1(), new Constraints(new Leading(290, 10, 10), new Leading(200, 10, 10)));
		getGroup();
		setSize(380, 330);
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

	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText("cancel");
			jButtonCancel.addActionListener(this);
		}
		return jButtonCancel;
	}

	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText("OK");
			jButtonOK.addActionListener(this);
		}
		return jButtonOK;
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
			jTextFieldRT.setText("10");
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
			jTextFieldMZ.setText("20");
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
	
	private GlycoJudgeParameter getParaFromFrame(){
		
		float inten = Float.parseFloat(this.getJTextFieldInten().getText())/100f;
		float mz = Float.parseFloat(this.getJTextFieldMZ().getText());
		float rt = Float.parseFloat(this.getJTextFieldRT().getText());
		boolean label = this.getJRadioButton0().isSelected();
		
		GlycoJudgeParameter para = new GlycoJudgeParameter(inten, mz, 0.1f, 500f, 0.3f, rt, 3);
		para.setDeGlycoLabel(label);
		return para;
	}
	
	public GlycoJudgeParameter getPara(){
		return this.para;
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
				NGlycoJugParaFrame frame = new NGlycoJugParaFrame(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("GlycoJugParaFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		Object obj = e.getSource();
		
		if(obj==this.getJButtonOK()){
			this.para = this.getParaFromFrame();
//			this.dispose();
			return;
		}
		
		if(obj==this.getJButtonCancel()){
/*			
			this.getJTextFieldInten().setText("0.1");
			this.getJTextFieldMZ().setText("20");
			this.getJTextFieldRT().setText("10");
			this.getJRadioButton0().setSelected(true);
			this.para = this.getParaFromFrame();
*/			
			this.dispose();
			return;
		}
	}

}
