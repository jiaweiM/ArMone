/* 
 ******************************************************************************
 * File: SequestCriteriaPanel.java * * * Created on 04-09-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultMascotCriteria;
import javax.swing.GroupLayout.Alignment;

/**
 * Criteria panel for filtering of sequest peptides
 * 
 * @author Xinning
 * @version 0.1, 04-09-2009, 21:16:45
 */
public class MascotCriteriaPanel2 extends JPanel implements
        ICriteriaSetter<IMascotPeptide> {

	private static final long serialVersionUID = 1L;
	private JFormattedTextField jFormattedTextFieldIonscore;
	private JLabel jLabelIS;
	private JLabel jLabelEValue;
	private JTextField jTextFieldEValue;
	private JLabel jLabelDeltaIS;
	private JTextField jTextFieldDeltaIS;
	private JLabel jLabelMHT;
	private JTextField jTextFieldMHT;
	private JLabel jLabelMIT;
	private JTextField jTextFieldMIT;
	private JLabel jLabelPValue;
	private JTextField jTextFieldPValue;
	private JPanel jPanelPValue;
	private JButton jButtonSet;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public MascotCriteriaPanel2() {
		initComponents();
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(null, "Mascot filters", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12),
				new Color(59, 59, 59)));
		setMinimumSize(new Dimension(391, 145));
		setPreferredSize(new Dimension(569, 145));
		setSize(585, 145);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getJLabelIS())
							.addGap(18)
							.addComponent(getJFormattedTextFieldIonscore(), javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(40)
							.addComponent(getJLabelEValue())
							.addGap(31)
							.addComponent(getJTextFieldEValue(), javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(55)
							.addComponent(getJLabelDeltaIS())
							.addGap(20)
							.addComponent(getJTextFieldDeltaIS(), javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getJLabelMHT())
							.addGap(12)
							.addComponent(getJTextFieldMHT(), javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(getJLabelMIT())
							.addGap(9)
							.addComponent(getJTextFieldMIT(), javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(41)
							.addComponent(getJPanelPValue(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(3)
							.addComponent(getJLabelIS()))
						.addComponent(getJFormattedTextFieldIonscore(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(3)
							.addComponent(getJLabelEValue()))
						.addComponent(getJTextFieldEValue(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(3)
							.addComponent(getJLabelDeltaIS()))
						.addComponent(getJTextFieldDeltaIS(), javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGap(12)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(17)
							.addComponent(getJLabelMHT()))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(14)
							.addComponent(getJTextFieldMHT(), javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(17)
							.addComponent(getJLabelMIT()))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(14)
							.addComponent(getJTextFieldMIT(), javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addComponent(getJPanelPValue(), javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
		);
		setLayout(groupLayout);
	}

	public JButton getJButtonSet() {
		if (jButtonSet == null) {
			jButtonSet = new JButton();
			jButtonSet.setText("set");
		}
		return jButtonSet;
	}
	
	public float getPvalue(){
		String s = this.getJTextFieldPValue().getText();
		return Float.parseFloat(s);
	}

	private JPanel getJPanelPValue() {
		if (jPanelPValue == null) {
			jPanelPValue = new JPanel();
			jPanelPValue.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, null, null, null, null));
			javax.swing.GroupLayout gl_jPanelPValue = new javax.swing.GroupLayout(jPanelPValue);
			gl_jPanelPValue.setHorizontalGroup(
				gl_jPanelPValue.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_jPanelPValue.createSequentialGroup()
						.addGap(23)
						.addComponent(getJLabelPValue())
						.addGap(6)
						.addComponent(getJTextFieldPValue(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(23)
						.addComponent(getJButtonSet()))
			);
			gl_jPanelPValue.setVerticalGroup(
				gl_jPanelPValue.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_jPanelPValue.createSequentialGroup()
						.addGap(15)
						.addComponent(getJLabelPValue()))
					.addGroup(gl_jPanelPValue.createSequentialGroup()
						.addGap(10)
						.addComponent(getJTextFieldPValue(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_jPanelPValue.createSequentialGroup()
						.addGap(9)
						.addComponent(getJButtonSet()))
			);
			jPanelPValue.setLayout(gl_jPanelPValue);
		}
		return jPanelPValue;
	}

	private JTextField getJTextFieldPValue() {
		if (jTextFieldPValue == null) {
			jTextFieldPValue = new JTextField();
			jTextFieldPValue.setText("0.05");
			jTextFieldMIT.setMinimumSize(new Dimension(40, 25));
			jTextFieldMIT.setPreferredSize(new Dimension(40, 25));
		}
		return jTextFieldPValue;
	}

	private JLabel getJLabelPValue() {
		if (jLabelPValue == null) {
			jLabelPValue = new JLabel();
			jLabelPValue.setText("P value = ");
		}
		return jLabelPValue;
	}

	private JTextField getJTextFieldMIT() {
		if (jTextFieldMIT == null) {
			jTextFieldMIT = new JTextField();
			jTextFieldMIT.setText("0");
			jTextFieldMIT.setMinimumSize(new Dimension(40, 25));
			jTextFieldMIT.setPreferredSize(new Dimension(40, 25));
		}
		return jTextFieldMIT;
	}

	private JLabel getJLabelMIT() {
		if (jLabelMIT == null) {
			jLabelMIT = new JLabel();
			jLabelMIT.setText("Ion score - MIT >");
		}
		return jLabelMIT;
	}

	private JTextField getJTextFieldMHT() {
		if (jTextFieldMHT == null) {
			jTextFieldMHT = new JTextField();
			jTextFieldMHT.setText("0");
			jTextFieldMHT.setMinimumSize(new Dimension(40, 25));
			jTextFieldMHT.setPreferredSize(new Dimension(40, 25));
		}
		return jTextFieldMHT;
	}

	private JLabel getJLabelMHT() {
		if (jLabelMHT == null) {
			jLabelMHT = new JLabel();
			jLabelMHT.setText("Ion score - MHT >");
		}
		return jLabelMHT;
	}

	private JTextField getJTextFieldDeltaIS() {
		if (jTextFieldDeltaIS == null) {
			jTextFieldDeltaIS = new JTextField();
			jTextFieldDeltaIS.setText("0.1");
			jTextFieldDeltaIS.setMinimumSize(new Dimension(40, 25));
			jTextFieldDeltaIS.setPreferredSize(new Dimension(40, 25));
		}
		return jTextFieldDeltaIS;
	}

	private JLabel getJLabelDeltaIS() {
		if (jLabelDeltaIS == null) {
			jLabelDeltaIS = new JLabel();
			jLabelDeltaIS.setText("Delta ion score");
		}
		return jLabelDeltaIS;
	}

	private JTextField getJTextFieldEValue() {
		if (jTextFieldEValue == null) {
			jTextFieldEValue = new JTextField();
			jTextFieldEValue.setText("0.05");
			jTextFieldEValue.setMinimumSize(new Dimension(40, 25));
			jTextFieldEValue.setPreferredSize(new Dimension(40, 25));
		}
		return jTextFieldEValue;
	}

	private JLabel getJLabelEValue() {
		if (jLabelEValue == null) {
			jLabelEValue = new JLabel();
			jLabelEValue.setText("Pep Expect");
		}
		return jLabelEValue;
	}

	private JLabel getJLabelIS() {
		if (jLabelIS == null) {
			jLabelIS = new JLabel();
			jLabelIS.setText("Ion score");
		}
		return jLabelIS;
	}

	private JFormattedTextField getJFormattedTextFieldIonscore() {
		if (jFormattedTextFieldIonscore == null) {
			jFormattedTextFieldIonscore = new JFormattedTextField(new Float(0.1f));
			jFormattedTextFieldIonscore.setText("20");
			jFormattedTextFieldIonscore.setMinimumSize(new Dimension(40, 25));
			jFormattedTextFieldIonscore.setPreferredSize(new Dimension(40, 25));
		}
		return jFormattedTextFieldIonscore;
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
	 * Main entry of the class. Note: This class is only created so that you can
	 * easily preview the result at runtime. It is not expected to be managed by
	 * the designer. You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("SequestCriteriaPanel");
				MascotCriteriaPanel2 content = new MascotCriteriaPanel2();
				content.setPreferredSize(content.getSize());
				frame.getContentPane().add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.gui.ICriteriaSetter#getCriteria()
	 */
	@Override
    public IPeptideCriteria<IMascotPeptide> getCriteria() {
		float ionscore = Float.parseFloat(this.jFormattedTextFieldIonscore.getText());
		double evalue = Double.parseDouble(this.getJTextFieldEValue().getText());
		float deltaIS = Float.parseFloat(this.getJTextFieldDeltaIS().getText());
		float mht = Float.parseFloat(this.getJTextFieldMHT().getText());
		float mit = Float.parseFloat(this.getJTextFieldMIT().getText());
	    return new DefaultMascotCriteria(ionscore, evalue, deltaIS, mht, mit);
    }
}
