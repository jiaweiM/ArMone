/* 
 ******************************************************************************
 * File:LInfoUDFPanel.java * * * Created on 2010-7-26
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * @author ck
 *
 * @version 2010-7-26, 21:10:24
 */

//VS4E -- DO NOT REMOVE THIS LINE!
public class LInfoUDFPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private ModSite [] modList;
	
	private JLabel jLabelName;
	private JCheckBox jCheckBox0;
	private JCheckBox jCheckBox1;
	private JCheckBox jCheckBox2;
	private JLabel jLabelS1;
	private JLabel jLabelM1;
	private JLabel jLabelS2;
	private JLabel jLabelM2;
	private JComboBox jComboBoxS11;
	private JTextField jTextFieldM11;
	private JComboBox jComboBoxS12;
	private JTextField jTextFieldM12;
	private JComboBox jComboBoxS21;
	private JTextField jTextFieldM21;
	private JComboBox jComboBoxS22;
	private JTextField jTextFieldM22;
	private JComboBox jComboBoxS31;
	private JTextField jTextFieldM31;
	private JComboBox jComboBoxS32;
	private JTextField jTextFieldM32;

	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	
	public LInfoUDFPanel() {
		this.getAllModSite();
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJLabelName(), new Constraints(new Leading(27, 10, 10), new Leading(16, 10, 10)));
		add(getJCheckBox0(), new Constraints(new Leading(23, 8, 8), new Leading(54, 10, 10)));
		add(getJCheckBox1(), new Constraints(new Leading(23, 8, 8), new Leading(89, 10, 10)));
		add(getJCheckBox2(), new Constraints(new Leading(23, 8, 8), new Leading(124, 10, 10)));
		add(getJLabelS1(), new Constraints(new Leading(180, 10, 10), new Leading(32, 12, 12)));
		add(getJLabelM1(), new Constraints(new Leading(250, 10, 10), new Leading(32, 12, 12)));
		add(getJLabelS2(), new Constraints(new Leading(320, 10, 10), new Leading(32, 12, 12)));
		add(getJLabelM2(), new Constraints(new Leading(390, 10, 10), new Leading(32, 12, 12)));
		add(getJComboBoxS11(), new Constraints(new Leading(165, 55, 10, 10), new Leading(55, 24, 10, 10)));
		add(getJTextFieldM11(), new Constraints(new Leading(239, 50, 12, 12), new Leading(57, 24, 10, 10)));
		add(getJComboBoxS12(), new Constraints(new Leading(306, 55, 10, 10), new Leading(55, 24, 10, 10)));
		add(getJTextFieldM12(), new Constraints(new Leading(384, 50, 12, 12), new Leading(57, 24, 10, 10)));
		add(getJComboBoxS21(), new Constraints(new Leading(165, 55, 10, 10), new Leading(90, 24, 10, 10)));
		add(getJTextFieldM21(), new Constraints(new Leading(239, 50, 12, 12), new Leading(92, 24, 10, 10)));
		add(getJComboBoxS22(), new Constraints(new Leading(306, 55, 10, 10), new Leading(90, 24, 10, 10)));
		add(getJTextFieldM22(), new Constraints(new Leading(384, 50, 12, 12), new Leading(92, 24, 10, 10)));
		add(getJComboBoxS31(), new Constraints(new Leading(165, 55, 10, 10), new Leading(125, 24, 10, 10)));
		add(getJTextFieldM31(), new Constraints(new Leading(239, 50, 12, 12), new Leading(127, 24, 10, 10)));
		add(getJComboBoxS32(), new Constraints(new Leading(306, 55, 10, 10), new Leading(125, 24, 10, 10)));
		add(getJTextFieldM32(), new Constraints(new Leading(384, 50, 12, 12), new Leading(127, 24, 10, 10)));
		setSize(460, 155);
	}

	private JTextField getJTextFieldM32() {
		if (jTextFieldM32 == null) {
			jTextFieldM32 = new JTextField();
		}
		return jTextFieldM32;
	}

	private JComboBox getJComboBoxS32() {
		if (jComboBoxS32 == null) {
			jComboBoxS32 = new JComboBox();
			jComboBoxS32.setModel(new DefaultComboBoxModel(modList));
			jComboBoxS32.setDoubleBuffered(false);
			jComboBoxS32.setBorder(null);
		}
		return jComboBoxS32;
	}
	
	private JTextField getJTextFieldM31() {
		if (jTextFieldM31 == null) {
			jTextFieldM31 = new JTextField();
		}
		return jTextFieldM31;
	}

	private JComboBox getJComboBoxS31() {
		if (jComboBoxS31 == null) {
			jComboBoxS31 = new JComboBox();
			jComboBoxS31.setModel(new DefaultComboBoxModel(modList));
			jComboBoxS31.setDoubleBuffered(false);
			jComboBoxS31.setBorder(null);
		}
		return jComboBoxS31;
	}
	
	private JTextField getJTextFieldM22() {
		if (jTextFieldM22 == null) {
			jTextFieldM22 = new JTextField();
		}
		return jTextFieldM22;
	}

	private JComboBox getJComboBoxS22() {
		if (jComboBoxS22 == null) {
			jComboBoxS22 = new JComboBox();
			jComboBoxS22.setModel(new DefaultComboBoxModel(modList));
			jComboBoxS22.setDoubleBuffered(false);
			jComboBoxS22.setBorder(null);
		}
		return jComboBoxS22;
	}
	
	private JTextField getJTextFieldM21() {
		if (jTextFieldM21 == null) {
			jTextFieldM21 = new JTextField();
		}
		return jTextFieldM21;
	}

	private JComboBox getJComboBoxS21() {
		if (jComboBoxS21 == null) {
			jComboBoxS21 = new JComboBox();
			jComboBoxS21.setModel(new DefaultComboBoxModel(modList));
			jComboBoxS21.setDoubleBuffered(false);
			jComboBoxS21.setBorder(null);
		}
		return jComboBoxS21;
	}
	
	private JTextField getJTextFieldM12() {
		if (jTextFieldM12 == null) {
			jTextFieldM12 = new JTextField();
		}
		return jTextFieldM12;
	}

	private JComboBox getJComboBoxS12() {
		if (jComboBoxS12 == null) {
			jComboBoxS12 = new JComboBox();
			jComboBoxS12.setModel(new DefaultComboBoxModel(modList));
			jComboBoxS12.setDoubleBuffered(false);
			jComboBoxS12.setBorder(null);
		}
		return jComboBoxS12;
	}

	private JTextField getJTextFieldM11() {
		if (jTextFieldM11 == null) {
			jTextFieldM11 = new JTextField();
		}
		return jTextFieldM11;
	}

	private JComboBox getJComboBoxS11() {
		if (jComboBoxS11 == null) {
			jComboBoxS11 = new JComboBox();
			jComboBoxS11.setModel(new DefaultComboBoxModel(modList));
			jComboBoxS11.setDoubleBuffered(false);
			jComboBoxS11.setBorder(null);
		}
		return jComboBoxS11;
	}

	private JLabel getJLabelM2() {
		if (jLabelM2 == null) {
			jLabelM2 = new JLabel();
			jLabelM2.setText("Mass");
		}
		return jLabelM2;
	}

	private JLabel getJLabelS2() {
		if (jLabelS2 == null) {
			jLabelS2 = new JLabel();
			jLabelS2.setText("Site");
		}
		return jLabelS2;
	}

	private JLabel getJLabelM1() {
		if (jLabelM1 == null) {
			jLabelM1 = new JLabel();
			jLabelM1.setText("Mass");
		}
		return jLabelM1;
	}

	private JLabel getJLabelS1() {
		if (jLabelS1 == null) {
			jLabelS1 = new JLabel();
			jLabelS1.setText("Site");
		}
		return jLabelS1;
	}

	private JCheckBox getJCheckBox2() {
		if (jCheckBox2 == null) {
			jCheckBox2 = new JCheckBox();
			jCheckBox2.setText("   3. User-Defined-3");
		}
		return jCheckBox2;
	}

	private JCheckBox getJCheckBox1() {
		if (jCheckBox1 == null) {
			jCheckBox1 = new JCheckBox();
			jCheckBox1.setText("   2. User-Defined-2");
		}
		return jCheckBox1;
	}

	private JCheckBox getJCheckBox0() {
		if (jCheckBox0 == null) {
			jCheckBox0 = new JCheckBox();
			jCheckBox0.setText("   1. User-Defined-1");
		}
		return jCheckBox0;
	}

	private JLabel getJLabelName() {
		if (jLabelName == null) {
			jLabelName = new JLabel();
			jLabelName.setText("User-Defined");
		}
		return jLabelName;
	}

	private void getAllModSite(){
		ArrayList <ModSite> modList = new ArrayList <ModSite>();
		modList.add(null);
		modList.add(ModSite.newInstance_PepNterm());
		modList.add(ModSite.newInstance_PepCterm());
		modList.add(ModSite.newInstance_aa('A'));
		modList.add(ModSite.newInstance_aa('C'));
		modList.add(ModSite.newInstance_aa('D'));
		modList.add(ModSite.newInstance_aa('E'));
		modList.add(ModSite.newInstance_aa('F'));
		modList.add(ModSite.newInstance_aa('G'));
		modList.add(ModSite.newInstance_aa('H'));
		modList.add(ModSite.newInstance_aa('I'));
		modList.add(ModSite.newInstance_aa('K'));
		modList.add(ModSite.newInstance_aa('L'));
		modList.add(ModSite.newInstance_aa('M'));
		modList.add(ModSite.newInstance_aa('N'));
		modList.add(ModSite.newInstance_aa('P'));
		modList.add(ModSite.newInstance_aa('Q'));
		modList.add(ModSite.newInstance_aa('R'));
		modList.add(ModSite.newInstance_aa('S'));
		modList.add(ModSite.newInstance_aa('T'));
		modList.add(ModSite.newInstance_aa('V'));
		modList.add(ModSite.newInstance_aa('W'));
		modList.add(ModSite.newInstance_aa('Y'));
		modList.add(ModSite.newInstance_aa('B'));
		modList.add(ModSite.newInstance_aa('X'));
		modList.add(ModSite.newInstance_aa('Z'));
		
		this.modList = modList.toArray(new ModSite [modList.size()]);
	}
	
	public LabelType getLabelType(){
		LabelType type = LabelType.User_Defined;
		ArrayList <Short> intList = new ArrayList <Short>();
		
		ArrayList <LabelInfo[]> infoList = new ArrayList <LabelInfo[]>();
		if(getJCheckBox0().isSelected()){
			intList.add((short)1);
			ArrayList <LabelInfo> iList = new ArrayList <LabelInfo>();
			Object obj1 = getJComboBoxS11().getSelectedItem();
			if(obj1!=null){
				ModSite site = (ModSite)obj1;
				String m = getJTextFieldM11().getText();
				if(m!=null && m.length()>0){
					double ms = Double.parseDouble(m);
					LabelInfo info = new LabelInfo(site, ms, "User_Defined_1");
					iList.add(info);
				}
			}
			Object obj2 = getJComboBoxS12().getSelectedItem();
			if(obj2!=null){
				ModSite site = (ModSite)obj2;
				String m = getJTextFieldM12().getText();
				if(m!=null && m.length()>0){
					double ms = Double.parseDouble(m);
					LabelInfo info = new LabelInfo(site, ms, "User_Defined_1");
					iList.add(info);
				}
			}
			infoList.add(iList.toArray(new LabelInfo[iList.size()]));
		}
		if(getJCheckBox1().isSelected()){
			intList.add((short)2);
			ArrayList <LabelInfo> iList = new ArrayList <LabelInfo>();
			Object obj1 = getJComboBoxS21().getSelectedItem();
			if(obj1!=null){
				ModSite site = (ModSite)obj1;
				String m = getJTextFieldM21().getText();
				if(m!=null && m.length()>0){
					double ms = Double.parseDouble(m);
					LabelInfo info = new LabelInfo(site, ms, "User_Defined_2");
					iList.add(info);
				}
			}
			Object obj2 = getJComboBoxS22().getSelectedItem();
			if(obj2!=null){
				ModSite site = (ModSite)obj2;
				String m = getJTextFieldM22().getText();
				if(m!=null && m.length()>0){
					double ms = Double.parseDouble(m);
					LabelInfo info = new LabelInfo(site, ms, "User_Defined_2");
					iList.add(info);
				}
			}
			infoList.add(iList.toArray(new LabelInfo[iList.size()]));
		}
		if(getJCheckBox2().isSelected()){
			intList.add((short)3);
			ArrayList <LabelInfo> iList = new ArrayList <LabelInfo>();
			Object obj1 = getJComboBoxS31().getSelectedItem();
			if(obj1!=null){
				ModSite site = (ModSite)obj1;
				String m = getJTextFieldM31().getText();
				if(m!=null && m.length()>0){
					double ms = Double.parseDouble(m);
					LabelInfo info = new LabelInfo(site, ms, "User_Defined_3");
					iList.add(info);
				}
			}
			Object obj2 = getJComboBoxS32().getSelectedItem();
			if(obj2!=null){
				ModSite site = (ModSite)obj2;
				String m = getJTextFieldM32().getText();
				if(m!=null && m.length()>0){
					double ms = Double.parseDouble(m);
					LabelInfo info = new LabelInfo(site, ms, "User_Defined_3");
					iList.add(info);
				}
			}
			infoList.add(iList.toArray(new LabelInfo[iList.size()]));
		}
		LabelInfo[][] infos = infoList.toArray(new LabelInfo[infoList.size()][]);
		short [] used = new short[intList.size()];
		for(int i=0;i<used.length;i++){
			used[i] = intList.get(i);
		}
		type.setInfo(infos);
		type.setUsed(used);
		return type;
	}
	
	public short [] getUsed(){
		return getLabelType().getUsed();
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
				frame.setTitle("LInfoUDFPanel");
				LInfoUDFPanel content = new LInfoUDFPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
