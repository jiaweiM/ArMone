/* 
 ******************************************************************************
 * File:AddModifPanel.java * * * Created on 2009-12-14
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.event.ActionEvent;
import java.util.HashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import cn.ac.dicp.gp1809.proteome.dbsearch.DefaultMod;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import javax.swing.GroupLayout.Alignment;

public class AddModifFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JButton jButton0;
	private JTextField jTextField0;
	private JTextField jTextField1;
	private JTextField jTextField2;
	private JButton jButton1;
	private JLabel jLabel3;
	private JTextField jTextField3;
	private JComboBox jComboBox0;
	private JLabel jLabel4;
	private JButton jButton2;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	
	public AddModifFrame() {
		initComponents();
	}

	private void initComponents() {
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(51)
					.addComponent(getJLabel0())
					.addGap(31)
					.addComponent(getJTextField0(), javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(51)
					.addComponent(getJLabel1())
					.addGap(27)
					.addComponent(getJTextField1(), javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(28)
					.addComponent(getJLabel2())
					.addGap(43)
					.addComponent(getJTextField2(), javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(51)
					.addComponent(getJLabel3())
					.addGap(35)
					.addComponent(getJTextField3(), javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(51)
					.addComponent(getJLabel4())
					.addGap(54)
					.addComponent(getJComboBox0(), javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(44)
					.addComponent(getJButton1()))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(82)
					.addComponent(getJButton0())
					.addGap(69)
					.addComponent(getJButton2()))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(37)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJLabel0())
						.addComponent(getJTextField0(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGap(4)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(4)
							.addComponent(getJLabel1()))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(2)
							.addComponent(getJTextField1(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(2)
							.addComponent(getJLabel2()))
						.addComponent(getJTextField2(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGap(6)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJLabel3())
						.addComponent(getJTextField3(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGap(6)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(4)
							.addComponent(getJLabel4()))
						.addComponent(getJComboBox0(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(getJButton1()))
					.addGap(28)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJButton0())
						.addComponent(getJButton2())))
		);
		getContentPane().setLayout(groupLayout);
		setSize(449, 280);
	}

	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("Return");
		}
		return jButton2;
	}

	private JLabel getJLabel4() {
		if (jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("Site");
		}
		return jLabel4;
	}

	private JComboBox getJComboBox0() {
		if (jComboBox0 == null) {
			jComboBox0 = new JComboBox();
			jComboBox0.setModel(new DefaultComboBoxModel(new Object[] { "A", "R", "N", "D", "C",
					"E", "Q", "G", "H", "I", "L", "K", "M", "F", "P", "S", "T", "W", "Y", "V", "N-term", "C-term"}));
			jComboBox0.setDoubleBuffered(false);
			jComboBox0.setBorder(null);
		}
		return jComboBox0;
	}

	private JTextField getJTextField3() {
		if (jTextField3 == null) {
			jTextField3 = new JTextField();
		}
		return jTextField3;
	}

	private JLabel getJLabel3() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("Specificity");
		}
		return jLabel3;
	}

	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Add New Specificity");
			jButton1.setOpaque(false);
		}
		return jButton1;
	}

	private JTextField getJTextField2() {
		if (jTextField2 == null) {
			jTextField2 = new JTextField();
		}
		return jTextField2;
	}

	private JTextField getJTextField1() {
		if (jTextField1 == null) {
			jTextField1 = new JTextField();
		}
		return jTextField1;
	}

	private JTextField getJTextField0() {
		if (jTextField0 == null) {
			jTextField0 = new JTextField();
		}
		return jTextField0;
	}

	private JButton getJButton0() {
		if (jButton0 == null) {
			jButton0 = new JButton();
			jButton0.setText("Add Modification");
		}
		return jButton0;
	}

	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Average");
		}
		return jLabel2;
	}

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Monoisotopic");
		}
		return jLabel1;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Title");
		}
		return jLabel0;
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
	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		HashSet <ModSite> sites = new HashSet <ModSite> ();
		HashSet <DefaultMod> mods = new HashSet <DefaultMod> ();
		
		String text = "";
		
		if(obj==getJButton0()){
			
			String title = jTextField0.getText();
			double mono = Double.parseDouble(jTextField1.getText());
			double avg = Double.parseDouble(jTextField2.getText());
			HashSet <ModSite> modSites = new HashSet <ModSite> ();
			modSites.addAll(sites);
			sites = new HashSet <ModSite> ();
			DefaultMod mod = new DefaultMod(title, mono, avg, modSites);
			mods.add(mod);
		}
		if(obj==getJButton1()){
			String str = jComboBox0.getSelectedItem().toString();
			if(str.length()==1){
				sites.add(ModSite.newInstance_aa(str.charAt(0)));
			}
			if(str.length()>1&&str.startsWith("C")){
				sites.add(ModSite.newInstance_PepCterm());
			}
			if(str.length()>1&&str.startsWith("N")){
				sites.add(ModSite.newInstance_PepNterm());
			}
			text += str;
			text += "; ";
		}
		if(obj==getJButton2()){
			if(mods.size()>0){
				
			}
		}
	}

	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				AddModifFrame frame = new AddModifFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
