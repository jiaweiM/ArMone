/* 
 ******************************************************************************
 * File: ProteinGroupSimpPanel.java * * * Created on 03-17-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.protein;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.NoredundantConstants;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.NoredundantReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.NoredundantWriter;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.protein.ProteinGroupSimplifierFactory.SimplifierType;
import cn.ac.dicp.gp1809.util.StringUtil;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

/**
 * 
 * @author Xinning
 * @version 0.1, 03-17-2010, 16:16:05
 */
public class ProteinGroupSimpPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	private MyJFileChooser noredundantchooser;
	
	private JButton jButton0;
	private JButton jButtonNoredundant;
	private JTextField jTextFieldNoredundant;
	private JTextField jTextFieldUnduplicated;
	private JButton jButtonUnduplicated;
	private JPanel jPanel0;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JComboBox jComboBox;

	private JLabel jLabel2;

	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public ProteinGroupSimpPanel() {
		initComponents();
		
		this.postInit();
	}

	private void initComponents() {
    	setLayout(new GroupLayout());
    	add(getJButton0(), new Constraints(new Leading(404, 10, 10), new Leading(163, 10, 10)));
    	add(getJPanel0(), new Constraints(new Bilateral(12, 12, 0), new Leading(0, 157, 12, 12)));
    	add(getJLabel2(), new Constraints(new Leading(20, 131, 10, 10), new Leading(169, 6, 6)));
    	setSize(499, 201);
    }

	private JLabel getJLabel2() {
    	if (jLabel2 == null) {
    		jLabel2 = new JLabel();
    	}
    	return jLabel2;
    }

	/**
	 * Post initial actions
	 */
	private void postInit() {
		this.getJComboBox().setModel(new DefaultComboBoxModel(ProteinGroupSimplifierFactory.getAllSupportedTypes()));
		this.getJButtonNoredundant().addActionListener(this);
		this.getJButton0().addActionListener(this);
		this.getJButtonUnduplicated().addActionListener(this);
	}
	
	/**
	 * The file chooser
	 * 
	 * @return
	 */
	private MyJFileChooser getFileChooser() {
		if(this.noredundantchooser == null) {
			this.noredundantchooser = new MyJFileChooser();
		}
		
		return this.noredundantchooser;
	}

	private JComboBox getJComboBox() {
    	if (jComboBox == null) {
    		jComboBox = new JComboBox();
    	}
    	return jComboBox;
    }

	private JLabel getJLabel1() {
    	if (jLabel1 == null) {
    		jLabel1 = new JLabel();
    		jLabel1.setText("Unduplicated");
    	}
    	return jLabel1;
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("Noredundant");
    	}
    	return jLabel0;
    }

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.setBorder(BorderFactory.createTitledBorder(null, "Parameters", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD,
    				12), new Color(59, 59, 59)));
    		jPanel0.setLayout(new GroupLayout());
    		jPanel0.add(getJButtonNoredundant(), new Constraints(new Leading(420, 31, 12, 12), new Leading(2, 10, 10)));
    		jPanel0.add(getJButtonUnduplicated(), new Constraints(new Leading(420, 31, 12, 12), new Leading(40, 12, 12)));
    		jPanel0.add(getJTextFieldNoredundant(), new Constraints(new Leading(83, 331, 10, 10), new Leading(2, 12, 12)));
    		jPanel0.add(getJTextFieldUnduplicated(), new Constraints(new Leading(82, 332, 12, 12), new Leading(40, 10, 10)));
    		jPanel0.add(getJLabel0(), new Constraints(new Leading(5, 78, 10, 10), new Leading(8, 12, 12)));
    		jPanel0.add(getJLabel1(), new Constraints(new Leading(5, 78, 12, 12), new Leading(46, 12, 12)));
    		jPanel0.add(getJComboBox(), new Constraints(new Leading(82, 286, 10, 10), new Leading(76, 12, 12)));
    	}
    	return jPanel0;
    }

	private JButton getJButtonUnduplicated() {
    	if (jButtonUnduplicated == null) {
    		jButtonUnduplicated = new JButton();
    		jButtonUnduplicated.setText("browse");
    	}
    	return jButtonUnduplicated;
    }

	private JTextField getJTextFieldUnduplicated() {
    	if (jTextFieldUnduplicated == null) {
    		jTextFieldUnduplicated = new JTextField();
    	}
    	return jTextFieldUnduplicated;
    }

	private JTextField getJTextFieldNoredundant() {
    	if (jTextFieldNoredundant == null) {
    		jTextFieldNoredundant = new JTextField();
    	}
    	return jTextFieldNoredundant;
    }

	private JButton getJButtonNoredundant() {
    	if (jButtonNoredundant == null) {
    		jButtonNoredundant = new JButton();
    		jButtonNoredundant.setText("browse");
    	}
    	return jButtonNoredundant;
    }

	private JButton getJButton0() {
    	if (jButton0 == null) {
    		jButton0 = new JButton();
    		jButton0.setText("    Start    ");
    		jButton0.addActionListener(this);
    	}
    	return jButton0;
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
	
	@Override
    public void actionPerformed(ActionEvent arg0) {
		
		Object obj = arg0.getSource();
		if(obj == this.getJButtonNoredundant()) {
			String[] filter = StringUtil.mergeStrArray(NoredundantConstants.PREV_USED_NORED_EXTENSION,NoredundantConstants.NORED_EXTENSION);
			this.getFileChooser().setFileFilter(filter, "Noredundant protein list file");
			int value = this.getFileChooser().showOpenDialog(this);
			if(value == JFileChooser.APPROVE_OPTION) {
				String name = this.getFileChooser().getSelectedFile().getAbsolutePath();
				String output = name.substring(0,name.lastIndexOf('.')+1)+NoredundantConstants.UNDUP_EXTENSION;
				this.getJTextFieldNoredundant().setText(name);
				this.getJTextFieldUnduplicated().setText(output);
			}
			
			return ;
		}
		
		if(obj == this.getJButtonUnduplicated()) {
			String[] filter = StringUtil.mergeStrArray(NoredundantConstants.PREV_USED_UNDUP_EXTENSION,NoredundantConstants.UNDUP_EXTENSION);
			this.getFileChooser().setFileFilter(filter, "Unduplicated protein list file");
			int value = this.getFileChooser().showOpenDialog(this);
			if(value == JFileChooser.APPROVE_OPTION) {
				String name = this.getFileChooser().getSelectedFile().getAbsolutePath();
				if(!name.toLowerCase().endsWith(NoredundantConstants.UNDUP_EXTENSION))
					name += "."+NoredundantConstants.UNDUP_EXTENSION;
				this.getJTextFieldUnduplicated().setText(name);
			}
			
			return ;
		}
		
		if(obj == this.getJButton0()) {
			String nored = this.getJTextFieldNoredundant().getText();
			String undup = this.getJTextFieldUnduplicated().getText();
			
			if(nored==null || nored.length()==0) {
				JOptionPane.showMessageDialog(this, "Select the file first");
				return;
			}
			
			if(undup==null || undup.length()==0) {
				JOptionPane.showMessageDialog(this, "Select the file first");
				return;
			}
			
			SimplifierType type = (SimplifierType)this.jComboBox.getSelectedItem();
			IProteinGroupSimplifier simplifier = ProteinGroupSimplifierFactory.getSimplifier(type);
			NoredundantReader reader = null;
			NoredundantWriter writer = null;
			try {
				this.getJLabel2().setText("Processing ... ");
				this.getJButton0().setEnabled(false);
				
				reader = new NoredundantReader(nored);
				writer = new NoredundantWriter(undup, reader.getProteinFormat());
				
				Protein pro;
				while((pro = reader.getProtein())!=null) {
					pro.simplify(simplifier);
					writer.write(pro);
				}
				
				this.getJLabel2().setText("Finish.");
			
			}catch(Exception e) {
				this.getJLabel2().setText("Error");
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}finally {
				if(reader != null)
					reader.close();
				if(writer != null)
					writer.close();
				
				this.getJButton0().setEnabled(true);
			}
			
			return ;
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
				frame.setTitle("ProteinGroupSimpPanel");
				ProteinGroupSimpPanel content = new ProteinGroupSimpPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
