/* 
 ******************************************************************************
 * File: BatchDrawFrame.java * * * Created on 07-21-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.BatchDrawHtmlWriter;
import cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.IBatchDrawWriter;
import cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.branch.BatchDrawPDFWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.IonsTypeSettingPanel;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.NeutralLossSettingPanel;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.SpectrumThresholdSetPanelVer;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

/**
 * 
 * @author Xinning
 * @version 0.1, 07-21-2009, 16:30:58
 */
public class BatchDrawFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private MyJFileChooser output;
	private MyJFileChooser input;
	private boolean externalppl = true;
	private IPeptideListReader reader;

	private JTextField jTextFieldInput;
	private JButton jButtonInput;
	private JLabel jLabelInput;
	private NeutralLossSettingPanel neutralLossSettingPanel0;
	private JCheckBox jCheckBoxHtml;
	private IonsTypeSettingPanel ionsTypeSettingPanel0;
	private JProgressBar jProgressBar0;
	private JCheckBox jCheckBoxPDF;
	private JButton jButtonStart;
	private ButtonGroup buttonGroup1;
	private JButton jButtonOutput;
	private JTextField jTextFieldOutputt;
	private JLabel jLabelOutput;

	private SpectrumThresholdSetPanelVer spectrumThresholdSetPanelVer0;

	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public BatchDrawFrame() {
		initComponents();
	}

	/**
	 * Call from other frame
	 * 
	 * @param reader
	 */
	public BatchDrawFrame(IPeptideListReader reader) {
		initComponents();

		if (reader != null) {
			this.reader = reader;
			this.externalppl = false;

			this.jLabelInput.setVisible(false);
			this.jTextFieldInput.setVisible(false);
			this.jButtonInput.setVisible(false);
		}

	}

	private void initComponents() {
		setTitle("Batch draw");
		setResizable(false);
		setLayout(new GroupLayout());
		add(getNeutralLossSettingPanel0(), new Constraints(new Leading(464, 12, 12), new Leading(12, 238, 12, 12)));
		add(getJProgressBar0(), new Constraints(new Bilateral(0, 0, 10), new Leading(259, 10, 10)));
		add(getIonsTypeSettingPanel0(), new Constraints(new Leading(296, 156, 6, 6), new Leading(106, 147, 6, 6)));
		add(getJTextFieldInput(), new Constraints(new Leading(61, 316, 6, 6), new Leading(54, 10, 10)));
		add(getJTextFieldOutputt(), new Constraints(new Leading(61, 316, 6, 6), new Leading(21, 6, 6)));
		add(getJButtonInput(), new Constraints(new Leading(384, 55, 10, 10), new Leading(54, 10, 10)));
		add(getJButtonOutput(), new Constraints(new Leading(384, 54, 6, 6), new Leading(21, 6, 6)));
		add(getJLabelOutput(), new Constraints(new Leading(15, 10, 10), new Leading(24, 6, 6)));
		add(getJLabelInput(), new Constraints(new Leading(24, 6, 6), new Leading(57, 6, 6)));
		add(getJCheckBoxPDF(), new Constraints(new Leading(184, 10, 10), new Leading(84, 6, 6)));
		add(getJCheckBoxHtml(), new Constraints(new Leading(64, 10, 10), new Leading(84, 6, 6)));
		add(getJButtonStart(), new Constraints(new Leading(17, 261, 12, 12), new Leading(192, 45, 10, 10)));
		add(getSpectrumThresholdSetPanelVer0(), new Constraints(new Leading(19, 10, 10), new Leading(122, 10, 10)));
		initButtonGroup1();
		setSize(632, 288);
	}

	private SpectrumThresholdSetPanelVer getSpectrumThresholdSetPanelVer0() {
		if (spectrumThresholdSetPanelVer0 == null) {
			spectrumThresholdSetPanelVer0 = new SpectrumThresholdSetPanelVer();
		}
		return spectrumThresholdSetPanelVer0;
	}

	private JLabel getJLabelOutput() {
		if (jLabelOutput == null) {
			jLabelOutput = new JLabel();
			jLabelOutput.setText("Output");
		}
		return jLabelOutput;
	}

	private JTextField getJTextFieldOutputt() {
		if (jTextFieldOutputt == null) {
			jTextFieldOutputt = new JTextField();
			jTextFieldOutputt.setPreferredSize(new Dimension(12, 24));
			jTextFieldOutputt.setAutoscrolls(true);
		}
		return jTextFieldOutputt;
	}

	private JButton getJButtonOutput() {
		if (jButtonOutput == null) {
			jButtonOutput = new JButton();
			jButtonOutput.setText("...");
			jButtonOutput.setMinimumSize(new Dimension(73, 24));
			jButtonOutput.setPreferredSize(new Dimension(73, 24));
			jButtonOutput.addActionListener(this);
		}
		return jButtonOutput;
	}

	private void initButtonGroup1() {
		buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(getJCheckBoxHtml());
		buttonGroup1.add(getJCheckBoxPDF());
	}

	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setText("Start");
			jButtonStart.addActionListener(this);
		}
		return jButtonStart;
	}

	private JCheckBox getJCheckBoxPDF() {
		if (jCheckBoxPDF == null) {
			jCheckBoxPDF = new JCheckBox();
			jCheckBoxPDF.setSelected(false);
			jCheckBoxPDF.setText("PDF");
		}
		return jCheckBoxPDF;
	}

	private JProgressBar getJProgressBar0() {
		if (jProgressBar0 == null) {
			jProgressBar0 = new JProgressBar();
		}
		return jProgressBar0;
	}

	private IonsTypeSettingPanel getIonsTypeSettingPanel0() {
		if (ionsTypeSettingPanel0 == null) {
			ionsTypeSettingPanel0 = new IonsTypeSettingPanel();
			ionsTypeSettingPanel0.setBorder(BorderFactory.createTitledBorder(
			        null, "Type of ions", TitledBorder.LEADING,
			        TitledBorder.ABOVE_TOP, new Font("Dialog", Font.BOLD, 12),
			        new Color(51, 51, 51)));
		}
		return ionsTypeSettingPanel0;
	}

	private JCheckBox getJCheckBoxHtml() {
		if (jCheckBoxHtml == null) {
			jCheckBoxHtml = new JCheckBox();
			jCheckBoxHtml.setSelected(true);
			jCheckBoxHtml.setText("Html");
		}
		return jCheckBoxHtml;
	}

	private NeutralLossSettingPanel getNeutralLossSettingPanel0() {
		if (neutralLossSettingPanel0 == null) {
			neutralLossSettingPanel0 = new NeutralLossSettingPanel();
			neutralLossSettingPanel0.setBorder(BorderFactory
			        .createTitledBorder(null, "Neutral loss peaks",
			                TitledBorder.LEADING, TitledBorder.ABOVE_TOP,
			                new Font("Dialog", Font.BOLD, 12), new Color(51,
			                        51, 51)));
		}
		return neutralLossSettingPanel0;
	}

	private JLabel getJLabelInput() {
		if (jLabelInput == null) {
			jLabelInput = new JLabel();
			jLabelInput.setText("Input");
		}
		return jLabelInput;
	}

	private JButton getJButtonInput() {
		if (jButtonInput == null) {
			jButtonInput = new JButton();
			jButtonInput.setText("...");
			jButtonInput.setMinimumSize(new Dimension(37, 24));
			jButtonInput.setPreferredSize(new Dimension(62, 24));
			jButtonInput.setMaximumSize(new Dimension(37, 24));
			jButtonInput.addActionListener(this);
		}
		return jButtonInput;
	}

	private JTextField getJTextFieldInput() {
		if (jTextFieldInput == null) {
			jTextFieldInput = new JTextField();
			jTextFieldInput.setPreferredSize(new Dimension(12, 24));
			jTextFieldInput.setAutoscrolls(true);
		}
		return jTextFieldInput;
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
				BatchDrawFrame frame = new BatchDrawFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("BatchDrawFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	/**
	 * @return the output file chooser
	 */
	private MyJFileChooser getOutputChooser() {
		if (this.output == null) {
			this.output = new MyJFileChooser();
		}
		return output;
	}

	/**
	 * @return the input file chooser
	 */
	private MyJFileChooser getInputChooser() {
		if (this.input == null) {
			this.input = new MyJFileChooser();
			this.input.setFileFilter(new String[] { "ppl" },
			        "peptide list (*.ppl)");
		}
		return input;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Object obj = e.getSource();

		if (obj == this.getJButtonInput()) {

			if (this.getInputChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				this.getJTextFieldInput().setText(
				        this.getInputChooser().getSelectedFile()
				                .getAbsolutePath());
			}

			return;

		}

		if (obj == this.getJButtonOutput()) {

			boolean html = this.getJCheckBoxHtml().isSelected();

			if (html)
				this.getOutputChooser().setFileFilter(
				        new String[] { "html", "htm" }, "HTML (*.htm, *.html)");
			else
				this.getOutputChooser().setFileFilter(new String[] { "pdf" },
				        "PDF (*.pdf)");

			if (this.getOutputChooser().showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				this.getJTextFieldOutputt().setText(
				        this.getOutputChooser().getSelectedFile()
				                .getAbsolutePath());
			}

			return;
		}

		if (obj == this.getJButtonStart()) {

			try {

				final boolean ext = this.externalppl;
				final IPeptideListReader reader = ext ? new PeptideListReader(
				        this.getJTextFieldInput().getText()) : this.reader;

				final int total = reader.getNumberofPeptides();

				new Thread() {

					@Override
					public void run() {
						try {
							
							getJButtonStart().setEnabled(false);
							
							IBatchDrawWriter writer = null;

							if (getJCheckBoxHtml().isSelected()) {
								writer = new BatchDrawHtmlWriter(
								        getJTextFieldOutputt().getText(),
								        reader.getSearchParameter(), reader
								                .getPeptideType());
							} else if(getJCheckBoxPDF().isSelected()){
								writer = new BatchDrawPDFWriter(
								        getJTextFieldOutputt().getText(),
								        reader.getSearchParameter(), reader
								                .getPeptideType());
							}

							NeutralLossInfo[] infos = getNeutralLossSettingPanel0()
							        .getNeutralLossInfo();
							int[] types = getIonsTypeSettingPanel0()
							        .getIonTypes();
							ISpectrumThreshold threshold = getSpectrumThresholdSetPanelVer0()
							        .getThreshold();

							getJProgressBar0().setMaximum(total);
							IPeptide pep;
							while ((pep = reader.getPeptide()) != null) {
								writer.write(pep, reader.getPeakLists(), types,
								        threshold, infos);

								getJProgressBar0().setValue(
								        reader.getCurtPeptideIndex());
							}

							writer.close();
							if (ext)
								reader.close();

							getJProgressBar0().setValue(total);
							getJProgressBar0().setString("Finish!");
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						finally {
							getJButtonStart().setEnabled(true);
						}
					}

				}.start();

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, ex, "Error",
				        JOptionPane.ERROR_MESSAGE);
			}
		}

	}

}
