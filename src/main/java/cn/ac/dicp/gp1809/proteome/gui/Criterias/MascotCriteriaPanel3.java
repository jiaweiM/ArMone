/* 
 ******************************************************************************
 * File: MascotCriteriaPanel3.java * * * Created on 2011-11-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultMascotCriteria;
import javax.swing.GroupLayout.Alignment;

public class MascotCriteriaPanel3 extends JPanel implements
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

	private JCheckBox jCheckBox0;
	private JCheckBox jCheckBox1;
	private JCheckBox jCheckBox2;
	private JCheckBox jCheckBox3;
	private JCheckBox jCheckBox4;

	public MascotCriteriaPanel3() {
		initComponents();
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(null, "Mascot filters", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12),
				new Color(59, 59, 59)));
		setSize(574, 145);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(9)
							.addComponent(getJCheckBox0()))
						.addComponent(getJLabelIS())
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(4)
							.addComponent(getJFormattedTextFieldIonscore(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
					.addGap(14)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(19)
							.addComponent(getJCheckBox1()))
						.addComponent(getJLabelMHT())
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(9)
							.addComponent(getJTextFieldMHT(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
					.addGap(21)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(15)
							.addComponent(getJCheckBox2())
							.addGap(57)
							.addComponent(getJCheckBox3()))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(70)
							.addComponent(getJLabelEValue(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addComponent(getJLabelMIT(), javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getJTextFieldMIT(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(30)
							.addComponent(getJTextFieldEValue(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(15)
							.addComponent(getJCheckBox4()))
						.addComponent(getJLabelDeltaIS())
						.addComponent(getJTextFieldDeltaIS(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(getJPanelPValue(), javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(13))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(getJCheckBox0())
					.addGap(16)
					.addComponent(getJLabelIS())
					.addGap(23)
					.addComponent(getJFormattedTextFieldIonscore(), javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(getJCheckBox1())
					.addGap(10)
					.addComponent(getJLabelMHT())
					.addGap(11)
					.addComponent(getJTextFieldMHT(), javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJCheckBox2())
						.addComponent(getJCheckBox3()))
					.addGap(10)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(3)
							.addComponent(getJLabelEValue()))
						.addComponent(getJLabelMIT()))
					.addGap(11)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJTextFieldMIT(), javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(getJTextFieldEValue(), javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(getJCheckBox4())
					.addGap(20)
					.addComponent(getJLabelDeltaIS())
					.addGap(19)
					.addComponent(getJTextFieldDeltaIS(), javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addComponent(getJPanelPValue(), javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
		);
		setLayout(groupLayout);
	}

	private JCheckBox getJCheckBox0() {
		if (jCheckBox0 == null) {
			jCheckBox0 = new JCheckBox();
			jCheckBox0.setText("");
		}
		return jCheckBox0;
	}
	
	private JCheckBox getJCheckBox1() {
		if (jCheckBox1 == null) {
			jCheckBox1 = new JCheckBox();
			jCheckBox1.setText("");
		}
		return jCheckBox1;
	}
	
	private JCheckBox getJCheckBox2() {
		if (jCheckBox2 == null) {
			jCheckBox2 = new JCheckBox();
			jCheckBox2.setText("");
		}
		return jCheckBox2;
	}
	
	private JCheckBox getJCheckBox3() {
		if (jCheckBox3 == null) {
			jCheckBox3 = new JCheckBox();
			jCheckBox3.setText("");
		}
		return jCheckBox3;
	}
	
	private JCheckBox getJCheckBox4() {
		if (jCheckBox4 == null) {
			jCheckBox4 = new JCheckBox();
			jCheckBox4.setText("");
		}
		return jCheckBox4;
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
						.addGap(10)
						.addComponent(getJLabelPValue())
						.addGap(7)
						.addComponent(getJTextFieldPValue(), javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_jPanelPValue.createSequentialGroup()
						.addGap(36)
						.addComponent(getJButtonSet()))
			);
			gl_jPanelPValue.setVerticalGroup(
				gl_jPanelPValue.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_jPanelPValue.createSequentialGroup()
						.addGap(16)
						.addGroup(gl_jPanelPValue.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_jPanelPValue.createSequentialGroup()
								.addGap(2)
								.addComponent(getJLabelPValue()))
							.addComponent(getJTextFieldPValue(), javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
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
			jLabelMIT.setText("<html><body>Ion score - <br>MIT ></body><html>");
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
			jLabelMHT.setText("<html><body>Ion score - <br>MHT ></body><html>");
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

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.gui.Criterias.ICriteriaSetter#getCriteria()
	 */
	@Override
	public IPeptideCriteria<IMascotPeptide> getCriteria() {
		// TODO Auto-generated method stub
		boolean [] use = new boolean [5];
		use[0] = this.jCheckBox0.isSelected();
		use[1] = this.jCheckBox1.isSelected();
		use[2] = this.jCheckBox2.isSelected();
		use[3] = this.jCheckBox3.isSelected();
		use[4] = this.jCheckBox4.isSelected();
		
		float ionscore = Float.parseFloat(this.jFormattedTextFieldIonscore.getText());
		double evalue = Double.parseDouble(this.getJTextFieldEValue().getText());
		float deltaIS = Float.parseFloat(this.getJTextFieldDeltaIS().getText());
		float mht = Float.parseFloat(this.getJTextFieldMHT().getText());
		float mit = Float.parseFloat(this.getJTextFieldMIT().getText());
		
		return new DefaultMascotCriteria(ionscore, evalue, deltaIS, mht, mit, use);
	}

}
