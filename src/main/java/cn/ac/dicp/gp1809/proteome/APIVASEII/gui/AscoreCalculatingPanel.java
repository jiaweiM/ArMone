/* 
 ******************************************************************************
 * File: AscoreCalculatingPanel.java * * * Created on 06-14-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation.AscoreCalculationTask;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.IonsTypeSettingPanel;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

/**
 * Ascore calculating panel
 * 
 * @author Xinning
 * @version 0.1, 06-14-2009, 15:51:45
 */
public class AscoreCalculatingPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private MyJFileChooser output;
	private MyJFileChooser input;
	private boolean externalppl = true;
	private IPeptideListReader reader;

	private JLabel jLabel0;
	private JFormattedTextField jFormattedTextFieldTol;
	private JTextField jTextFieldOutput;
	private JButton jButtonOutput;
	private JTextField jTextFieldInput;
	private JButton jButtonInput;
	private JLabel jLabel1;
	private JCheckBox jCheckBoxRemove;
	private JProgressBar jProgressBar0;
	private JButton jButtonStart;

	private IonsTypeSettingPanel ionsTypeSettingPanel0;

	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public AscoreCalculatingPanel() {
		initComponents();
	}

	public AscoreCalculatingPanel(IPeptideListReader reader) {
		initComponents();

		if (reader != null) {
			this.reader = reader;
			this.externalppl = false;

			this.jTextFieldInput.setVisible(false);
			this.jButtonInput.setVisible(false);
		}
	}

	private void initComponents() {
    	setLayout(new GroupLayout());
    	add(getJButtonInput(), new Constraints(new Leading(500, 10, 10), new Leading(62, 6, 6)));
    	add(getJButtonOutput(), new Constraints(new Leading(500, 6, 6), new Leading(21, 6, 6)));
    	add(getJTextFieldOutput(), new Constraints(new Bilateral(16, 99, 474), new Leading(23, 6, 6)));
    	add(getJTextFieldInput(), new Constraints(new Bilateral(16, 98, 475), new Leading(64, 6, 6)));
    	add(getJButtonStart(), new Constraints(new Leading(216, 153, 10, 10), new Leading(223, 10, 10)));
    	add(getJProgressBar0(), new Constraints(new Leading(19, 550, 6, 6), new Leading(195, 22, 6, 6)));
    	add(getIonsTypeSettingPanel0(), new Constraints(new Leading(350, 144, 10, 10), new Leading(95, 94, 6, 6)));
    	add(getJLabel0(), new Constraints(new Leading(21, 10, 10), new Leading(115, 10, 10)));
    	add(getJFormattedTextFieldTol(), new Constraints(new Leading(163, 41, 6, 6), new Leading(111, 26, 6, 6)));
    	add(getJLabel1(), new Constraints(new Leading(212, 10, 10), new Leading(114, 6, 6)));
    	add(getJCheckBoxRemove(), new Constraints(new Leading(19, 6, 6), new Leading(151, 6, 6)));
    	setSize(589, 263);
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

	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setText("Start");
			jButtonStart.addActionListener(this);
		}
		return jButtonStart;
	}

	private JProgressBar getJProgressBar0() {
		if (jProgressBar0 == null) {
			jProgressBar0 = new JProgressBar();
		}
		return jProgressBar0;
	}

	private JCheckBox getJCheckBoxRemove() {
		if (jCheckBoxRemove == null) {
			jCheckBoxRemove = new JCheckBox();
			jCheckBoxRemove.setSelected(true);
			jCheckBoxRemove.setText("Remove the neutral loss peak");
			jCheckBoxRemove.setEnabled(false);
		}
		return jCheckBoxRemove;
	}

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Da");
		}
		return jLabel1;
	}

	private JFormattedTextField getJFormattedTextFieldTol() {
		if (jFormattedTextFieldTol == null) {
			jFormattedTextFieldTol = new JFormattedTextField(new Float(0.8));
			jFormattedTextFieldTol
			        .setHorizontalAlignment(SwingConstants.CENTER);
		}
		return jFormattedTextFieldTol;
	}

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("MS/MS tolerance (+-)");
    	}
    	return jLabel0;
    }

	private JButton getJButtonInput() {
		if (jButtonInput == null) {
			jButtonInput = new JButton();
			jButtonInput.setText("Input");
			jButtonInput.setMinimumSize(new Dimension(64, 28));
			jButtonInput.setPreferredSize(new Dimension(64, 28));
		}
		return jButtonInput;
	}

	private JTextField getJTextFieldInput() {
		if (jTextFieldInput == null) {
			jTextFieldInput = new JTextField();
			jTextFieldInput.setPreferredSize(new Dimension(400, 25));
		}
		return jTextFieldInput;
	}

	private JButton getJButtonOutput() {
		if (jButtonOutput == null) {
			jButtonOutput = new JButton();
			jButtonOutput.setText("Output");
			jButtonOutput.setMinimumSize(new Dimension(64, 28));
			jButtonOutput.setPreferredSize(new Dimension(64, 28));
			jButtonOutput.addActionListener(this);
		}
		return jButtonOutput;
	}

	private JTextField getJTextFieldOutput() {
		if (jTextFieldOutput == null) {
			jTextFieldOutput = new JTextField();
			jTextFieldOutput.setMinimumSize(new Dimension(100, 25));
			jTextFieldOutput.setPreferredSize(new Dimension(400, 25));
			jTextFieldOutput.addActionListener(this);
		}
		return jTextFieldOutput;
	}

	/**
	 * @return the output file chooser
	 */
	private MyJFileChooser getOutputChooser() {
		if (this.output == null) {
			this.output = new MyJFileChooser();
			this.output.setFileFilter(new String[] { "ppl" },
			        "peptide list (*.ppl)");
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

		if (obj == this.getJButtonStart()) {

			try {

				final boolean ext = this.externalppl;
				final IPeptideListReader reader = ext ? new PeptideListReader(
				        this.getJTextFieldInput().getText()) : this.reader;

				new Thread() {

					@Override
					public void run() {
						try {

							getJButtonStart().setEnabled(false);

							String output = getJTextFieldOutput().getText();
							int[] types = getIonsTypeSettingPanel0()
							        .getIonTypes();
							ISpectrumThreshold threshold = new SpectrumThreshold(
							        Double
							                .parseDouble(getJFormattedTextFieldTol()
							                        .getText()), 0);
							AscoreCalculationTask convertor = new AscoreCalculationTask(
							        reader, output, false, types, threshold);

							getJProgressBar0().setMaximum(100);
							
							
							while (convertor.hasNext()) {
								convertor.processNext();
								float per = convertor.completedPercent();
								int value = (int)(per*100);
								getJProgressBar0().setValue(value);
							}

							convertor.dispose();
							
							if (ext)
								reader.close();

							getJProgressBar0().setValue(100);
						} catch (Exception e) {
							throw new RuntimeException(e);
						} finally {
							getJButtonStart().setEnabled(true);
						}
					}

				}.start();

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, ex, "Error",
				        JOptionPane.ERROR_MESSAGE);
			}
		}

		if (obj == this.getJButtonInput()) {

			if (this.getInputChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				this.getJTextFieldInput().setText(
				        this.getInputChooser().getSelectedFile()
				                .getAbsolutePath());
			}

			return;

		}

		if (obj == this.getJButtonOutput()) {

			if (this.getOutputChooser().showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				this.getJTextFieldOutput().setText(
				        this.getOutputChooser().getSelectedFile()
				                .getAbsolutePath());
			}

			return;
		}
	}

}
