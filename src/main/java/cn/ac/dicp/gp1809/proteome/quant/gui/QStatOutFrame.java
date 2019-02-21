/* 
 ******************************************************************************
 * File:QStatOutFrame.java * * * Created on 2010-9-6
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.quant.profile.ResultStat;
import cn.ac.dicp.gp1809.proteome.gui2.util.JFileSelectPanel;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

//VS4E -- DO NOT REMOVE THIS LINE!
public class QStatOutFrame extends JFrame implements ActionListener, ItemListener{

	private static final long serialVersionUID = 1L;
	private JFileSelectPanel jFileSelectPanel0;
	private JLabel jLabelOutput;
	private JTextField jTextFieldOutput;
	private JButton jButtonOutput;
	private JCheckBox jCheckBoxQuan;
	private JCheckBox jCheckBoxMQuan;
	private JButton jButtonStart;
	private ButtonGroup quanGroup;
	private JButton jButtonClose;
	private MyJFileChooser databaseChooser;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public QStatOutFrame() {
		initComponents();
		initButtonGroup();
	}

	private void initComponents() {
		
		setLayout(new GroupLayout());
		add(getJLabelOutput(), new Constraints(new Leading(25, 10, 10), new Leading(268, 10, 10)));
		add(getJTextFieldOutput(), new Constraints(new Leading(85, 270, 10, 10), new Leading(265, 6, 6)));
		add(getJButtonOutput(), new Constraints(new Leading(380, 10, 10), new Leading(265, 10, 10)));
		add(getJButtonClose(), new Constraints(new Leading(300, 10, 10), new Leading(350, 10, 10)));
		add(getJFileSelectPanel0(), new Constraints(new Bilateral(10, 10, 10, 10), new Leading(0, 240, 10, 10)));
		add(getJButtonStart(), new Constraints(new Leading(200, 10, 10), new Leading(350, 12, 12)));
		add(getJCheckBoxMQuan(), new Constraints(new Leading(25, 10, 10), new Leading(370, 10, 10)));
		add(getJCheckBoxQuan(), new Constraints(new Leading(25, 10, 10), new Leading(330, 10, 10)));
		initQuanGroup();
		setSize(450, 420);
	}
	
	private JFileSelectPanel getJFileSelectPanel0() {
		if (jFileSelectPanel0 == null) {
			jFileSelectPanel0 = new JFileSelectPanel("Quantitative result file", "xml", "Quantitation result");
		}
		return jFileSelectPanel0;
	}

	private MyJFileChooser getDatabaseChooser() {
		if (databaseChooser == null) {
			databaseChooser = new MyJFileChooser();
			databaseChooser.setFileFilter(new String[] { "fasta" },
				"database file (*.fasta)");
		}
		return databaseChooser;
	}
	
	
	private void initQuanGroup() {
		quanGroup = new ButtonGroup();
		quanGroup.add(getJCheckBoxQuan());
		quanGroup.add(getJCheckBoxMQuan());
	}

	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText("Close");
			jButtonClose.addActionListener(this);
		}
		return jButtonClose;
	}

	private void initButtonGroup() {
		if (quanGroup == null) {
			quanGroup = new ButtonGroup();
			quanGroup.add(getJCheckBoxQuan());
			quanGroup.add(getJCheckBoxMQuan());
		}
	}
	
	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setText("Start");
			jButtonStart.addActionListener(this);
		}
		return jButtonStart;
	}

	private JCheckBox getJCheckBoxMQuan() {
		if (jCheckBoxMQuan == null) {
			jCheckBoxMQuan = new JCheckBox();
			jCheckBoxMQuan.setText("Mod Quan Result");
		}
		return jCheckBoxMQuan;
	}

	private JCheckBox getJCheckBoxQuan() {
		if (jCheckBoxQuan == null) {
			jCheckBoxQuan = new JCheckBox();
			jCheckBoxQuan.setSelected(true);
			jCheckBoxQuan.setText("Quan Result");
			jCheckBoxQuan.addItemListener(this);
		}
		return jCheckBoxQuan;
	}

	private JButton getJButtonOutput() {
		if (jButtonOutput == null) {
			jButtonOutput = new JButton();
			jButtonOutput.setText("...");
			jButtonOutput.addActionListener(this);
		}
		return jButtonOutput;
	}

	private JTextField getJTextFieldOutput() {
		if (jTextFieldOutput == null) {
			jTextFieldOutput = new JTextField();
		}
		return jTextFieldOutput;
	}

	private JLabel getJLabelOutput() {
		if (jLabelOutput == null) {
			jLabelOutput = new JLabel();
			jLabelOutput.setText("Output");
		}
		return jLabelOutput;
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
				QStatOutFrame frame = new QStatOutFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("QStatOutFrame");
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
		
		if(obj == this.getJButtonStart()){

			String out = this.getJTextFieldOutput().getText();
			if(out==null || out.length()==0)
				throw new NullPointerException("The input path is null.");
			
			ResultStat stat = null;
			
			
			return;
		}
		
		if(obj == this.getJButtonClose()){
			this.dispose();
		}
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		Object obj = e.getSource();
		if(obj==this.getJCheckBoxQuan()){
			
			return;
		}
	}

}
