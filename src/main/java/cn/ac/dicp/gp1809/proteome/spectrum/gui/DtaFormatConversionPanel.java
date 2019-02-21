/* 
 ******************************************************************************
 * File: DtaFormatConversionPanel.java * * * Created on 05-04-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaFormatConverter;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-04-2009, 20:59:20
 */
public class DtaFormatConversionPanel extends JPanel implements ItemListener,
        ActionListener {

	private static final long serialVersionUID = 1L;
	private MyJFileChooser filechooserfrom;

	private JPanel jPanel0;
	private JButton jButtonConvert;
	private JPanel jPanel1;
	private JComboBox jComboBoxFrom;
	private JLabel jLabel0;
	private JButton jButtonFrom;
	private JLabel jLabel1;
	private JComboBox jComboBoxTo;
	private JTextField jTextFieldFrom;
	private JTextField jTextFieldTo;
	private JButton jButtonTo;
	private JProgressBar jProgressBar0;
	private JLabel jLabel2;
	private JLabel jLabelAction;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public DtaFormatConversionPanel() {
		this.initalOthers();
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJPanel0(), new Constraints(new Bilateral(6, 6, 0), new Leading(
		        6, 10, 10)));
		add(getJLabel2(), new Constraints(new Leading(10, 421, 10, 10),
		        new Leading(129, 79, 79)));
		add(getJLabelAction(), new Constraints(new Leading(10, 412, 10, 10),
		        new Leading(129, 6, 6)));
		add(getJProgressBar0(), new Constraints(new Bilateral(6, 7, 492),
		        new Leading(153, 48, 48)));
		add(getJPanel1(), new Constraints(new Leading(6, 492, 6, 6),
		        new Trailing(12, 10, 184)));
		setSize(505, 220);
	}

	private JLabel getJLabelAction() {
		if (jLabelAction == null) {
			jLabelAction = new JLabel();
		}
		return jLabelAction;
	}

	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
		}
		return jLabel2;
	}

	private void initalOthers() {
		this.getFilechooserfrom();
	}

	/**
	 * @return the filechooser
	 */
	private MyJFileChooser getFilechooserfrom() {
		if (this.filechooserfrom == null) {
			this.filechooserfrom = new MyJFileChooser();
		}
		return filechooserfrom;
	}


	private JProgressBar getJProgressBar0() {
		if (jProgressBar0 == null) {
			jProgressBar0 = new JProgressBar();
		}
		return jProgressBar0;
	}

	private JButton getJButtonTo() {
		if (jButtonTo == null) {
			jButtonTo = new JButton();
			jButtonTo.setText("jButton2");
			jButtonTo.addActionListener(this);
		}
		return jButtonTo;
	}

	private JTextField getJTextFieldTo() {
		if (jTextFieldTo == null) {
			jTextFieldTo = new JTextField();
		}
		return jTextFieldTo;
	}

	private JComboBox getJComboBoxTo() {
		if (jComboBoxTo == null) {
			jComboBoxTo = new JComboBox();
			jComboBoxTo.setModel(new DefaultComboBoxModel(new DtaType[] {
			        DtaType.MGF, DtaType.DTA, DtaType.MS2 }));
		}
		return jComboBoxTo;
	}

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("To");
		}
		return jLabel1;
	}

	private JTextField getJTextFieldFrom() {
		if (jTextFieldFrom == null) {
			jTextFieldFrom = new JTextField();
		}
		return jTextFieldFrom;
	}

	private JButton getJButtonFrom() {
		if (jButtonFrom == null) {
			jButtonFrom = new JButton();
			jButtonFrom.setText("...");
			jButtonFrom.addActionListener(this);
		}
		return jButtonFrom;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("From");
		}
		return jLabel0;
	}

	private JComboBox getJComboBoxFrom() {
		if (jComboBoxFrom == null) {
			jComboBoxFrom = new JComboBox();
			jComboBoxFrom.setModel(new DefaultComboBoxModel(new DtaType[] {
			        DtaType.DTA, DtaType.MGF, DtaType.MS2 }));
			
			jComboBoxFrom.setSelectedItem(DtaType.DTA);
			this.select(this.getFilechooserfrom(), DtaType.DTA);
		}
		return jComboBoxFrom;
	}

	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jPanel1.add(getJButtonConvert());
		}
		return jPanel1;
	}

	private JButton getJButtonConvert() {
		if (jButtonConvert == null) {
			jButtonConvert = new JButton();
			jButtonConvert.setText("Convert");
			jButtonConvert.addActionListener(this);
		}
		return jButtonConvert;
	}

	private JPanel getJPanel0() {
		if (jPanel0 == null) {
			jPanel0 = new JPanel();
			jPanel0.setBorder(BorderFactory
			        .createTitledBorder(null, "Conversion",
			                TitledBorder.LEADING, TitledBorder.ABOVE_TOP,
			                new Font("SansSerif", Font.BOLD, 12), new Color(59,
			                        59, 59)));
			jPanel0.setLayout(new GroupLayout());
			jPanel0.add(getJComboBoxFrom(), new Constraints(new Leading(33, 81,
			        10, 10), new Leading(0, 6, 6)));
			jPanel0.add(getJLabel0(), new Constraints(new Leading(0, 6, 6),
			        new Leading(5, 6, 6)));
			jPanel0.add(getJButtonFrom(), new Constraints(new Trailing(0, 144,
			        383), new Leading(-1, 6, 6)));
			jPanel0.add(getJTextFieldFrom(), new Constraints(new Bilateral(120,
			        43, 12), new Leading(-1, 6, 6)));
			jPanel0.add(getJLabel1(), new Constraints(new Leading(0, 29, 6, 6),
			        new Leading(41, 6, 6)));
			jPanel0.add(getJComboBoxTo(), new Constraints(new Leading(33, 81,
			        6, 6), new Leading(36, 6, 6)));
			jPanel0.add(getJButtonTo(), new Constraints(new Trailing(0, 37,
			        144, 425), new Leading(35, 6, 6)));
			jPanel0.add(getJTextFieldTo(), new Constraints(new Bilateral(120,
			        43, 12), new Leading(35, 6, 6)));
		}
		return jPanel0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
	}

	/**
	 * Set the file filter for specific type
	 * 
	 * @param chooser
	 * @param type
	 */
	private void select(MyJFileChooser chooser, DtaType type) {
		switch (type) {
		case DTA:
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setFileFilter(null, "Dta directory");
			break;
		case MGF:
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setFileFilter(new String[] { "mgf" },
			        "Matrix generic format (*.mgf)");
			break;
		case MS2:
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setFileFilter(new String[] { "ms2" }, "MS2 format (*.ms2)");
			break;
		default:
			throw new RuntimeException("Unknown DtaType!");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Object obj = e.getSource();

		if (obj == this.getJButtonFrom()) {
			
			DtaType type = (DtaType)this.getJComboBoxFrom().getSelectedItem();	
			this.select(this.getFilechooserfrom(), type);
			
			int ap = this.getFilechooserfrom().showOpenDialog(this);
			if (ap == JFileChooser.APPROVE_OPTION) {
				this.jTextFieldFrom.setText(this.getFilechooserfrom()
				        .getSelectedFile().getAbsolutePath());
			}

			return;
		}

		if (obj == this.getJButtonTo()) {
			
			DtaType type = (DtaType)this.getJComboBoxTo().getSelectedItem();	
			this.select(this.getFilechooserfrom(), type);
			
			int ap = this.getFilechooserfrom().showSaveDialog(this);
			if (ap == JFileChooser.APPROVE_OPTION) {
				this.jTextFieldTo.setText(this.getFilechooserfrom()
				        .getSelectedFile().getAbsolutePath());
			}

			return;
		}

		if (obj == this.getJButtonConvert()) {
			String from = this.getJTextFieldFrom().getText();
			String to = this.getJTextFieldTo().getText();

			DtaType typefrom = (DtaType) this.getJComboBoxFrom()
			        .getSelectedItem();
			DtaType typeto = (DtaType) this.getJComboBoxTo().getSelectedItem();

			if (from.length() == 0 || to.length() == 0) {
				JOptionPane.showMessageDialog(this,
				        "The dta file name is null", "Error",
				        JOptionPane.ERROR_MESSAGE);
			} else {

				try {
					new MyThread(this.getJProgressBar0(), this
					        .getJLabelAction(), from, to, typefrom, typeto)
					        .start();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, ex.getMessage(),
					        "Error", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}

			}

			return;
		}
	}

	/**
	 * The inner thread
	 * 
	 * @author Xinning
	 * @version 0.1, 05-20-2009, 15:49:32
	 */
	private class MyThread extends Thread {

		private String from, to;
		private DtaType fromType, toType;
		private JProgressBar bar;
		private JLabel label;

		private MyThread(JProgressBar bar, JLabel label, String from,
		        String to, DtaType fromType, DtaType toType) {
			this.bar = bar;
			this.label = label;
			this.from = from;
			this.to = to;
			this.fromType = fromType;
			this.toType = toType;
		}

		@Override
		public void run() {
			this.label.setText("Converting ...");
			bar.setIndeterminate(true);

			try {
				DtaFormatConverter.main(new String[] { fromType.getType_name(),
				        toType.getType_name(), from, to });
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				bar.setIndeterminate(false);
				this.label.setText("Error while converting");
			}

			this.label.setText("Finished");
		}
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
				frame.setTitle("DtaFormatConversionPanel");
				DtaFormatConversionPanel content = new DtaFormatConversionPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
