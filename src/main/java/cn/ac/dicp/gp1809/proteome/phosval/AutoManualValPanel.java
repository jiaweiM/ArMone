/* 
 ******************************************************************************
 * File: AutoManualValPanel.java * * * Created on 05-06-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.phosval;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.proteome.gui.PeptideListPagedRowGettor;
import cn.ac.dicp.gp1809.proteome.gui.PeptideListViewer;
import cn.ac.dicp.gp1809.proteome.gui.SubpeptideListViewerDialog;
import cn.ac.dicp.gp1809.proteome.gui.PeptideListPagedRowGettor.PeptideRowReader;
import cn.ac.dicp.gp1809.proteome.phosval.autoManual.AutoFilterTask;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumFilter;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.MinimumIntensityFilter;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.NeutralLossSpectrumFilter;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.RegionTopNIntensityFilter;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.TopNIntensityFilter;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.NeutralLossSettingPanel;
import cn.ac.dicp.gp1809.util.gui.UIutilities;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-06-2009, 08:47:54
 */
public class AutoManualValPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private final PeptideListPagedRowGettor getter;
	private final PeptideListViewer viewerFrame;

	private NeutralLossSettingPanel neutralLossSettingPanel0;
	private JCheckBox jCheckBoxNeu;
	private JCheckBox jCheckBoxMinIntens;
	private JPanel jPanel1;
	private JPanel jPanel0;
	private JPanel jPanel2;
	private JFormattedTextField jFormattedTextFieldContinue;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JCheckBox jCheckBoxTopNRegion;
	private JFormattedTextField jFormattedTextFieldIntens;
	private JFormattedTextField jFormattedTextFieldTopNRegion;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JFormattedTextField jFormattedTextFieldMassWindow;
	private JProgressBar jProgressBar0;
	private JButton jButtonStart;
	private JPanel jPanel3;
	private JLabel jLabel4;
	private JFormattedTextField jFormattedTextFieldFragToler;
	private ButtonGroup buttonGroup1;
	private JCheckBox jCheckBoxTopN;
	private JFormattedTextField jFormattedTextFieldTopNp;
	private JLabel jLabel5;
	private JCheckBox jCheckBoxby;
	private ButtonGroup buttonGroup2;
	private JCheckBox jCheckBoxcz;
	private JCheckBox jCheckBoxIsotope;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public AutoManualValPanel() {
		this(null, null);
	}

	public AutoManualValPanel(PeptideListPagedRowGettor getter) {
		this(null, getter);
	}

	public AutoManualValPanel(PeptideListViewer viewerFrame,
	        PeptideListPagedRowGettor getter) {
		this.getter = getter;
		this.viewerFrame = viewerFrame;
		initComponents();
	}

	private void initComponents() {
    	setLayout(new GroupLayout());
    	add(getNeutralLossSettingPanel0(), new Constraints(new Leading(364, 12, 12), new Leading(8, 12, 12)));
    	add(getJPanel3(), new Constraints(new Leading(5, 514, 12, 12), new Trailing(12, 29, 10, 301)));
    	add(getJProgressBar0(), new Constraints(new Leading(5, 515, 12, 12), new Trailing(47, 22, 10, 273)));
    	add(getJPanel2(), new Constraints(new Leading(5, 186, 186), new Leading(8, 84, 10, 10)));
    	add(getJPanel0(), new Constraints(new Leading(5, 346, 12, 12), new Leading(86, 10, 103)));
    	add(getJLabel4(), new Constraints(new Leading(11, 6, 6), new Leading(264, 78, 81)));
    	add(getJFormattedTextFieldFragToler(), new Constraints(new Leading(171, 40, 6, 6), new Leading(264, 78, 81)));
    	add(getJCheckBoxIsotope(), new Constraints(new Leading(246, 10, 10), new Leading(265, 78, 81)));
    	initButtonGroup1();
    	initButtonGroup2();
    	setSize(525, 359);
    }

	private JCheckBox getJCheckBoxIsotope() {
    	if (jCheckBoxIsotope == null) {
    		jCheckBoxIsotope = new JCheckBox();
    		jCheckBoxIsotope.setSelected(true);
    		jCheckBoxIsotope.setText("use monoisotpe mass");
    	}
    	return jCheckBoxIsotope;
    }

	private JCheckBox getJCheckBoxcz() {
		if (jCheckBoxcz == null) {
			jCheckBoxcz = new JCheckBox();
			jCheckBoxcz.setText("c & z");
		}
		return jCheckBoxcz;
	}

	private void initButtonGroup2() {
    	buttonGroup2 = new ButtonGroup();
    	buttonGroup2.add(getJCheckBoxby());
    	buttonGroup2.add(getJCheckBoxcz());
    }

	private JCheckBox getJCheckBoxby() {
    	if (jCheckBoxby == null) {
    		jCheckBoxby = new JCheckBox();
    		jCheckBoxby.setSelected(true);
    		jCheckBoxby.setText("b & y");
    	}
    	return jCheckBoxby;
    }

	private JLabel getJLabel5() {
		if (jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText("peaks");
		}
		return jLabel5;
	}

	private JFormattedTextField getJFormattedTextFieldTopNp() {
		if (jFormattedTextFieldTopNp == null) {
			jFormattedTextFieldTopNp = new JFormattedTextField(new Integer(50));
			jFormattedTextFieldTopNp
			        .setHorizontalAlignment(SwingConstants.CENTER);
			jFormattedTextFieldTopNp.setPreferredSize(new Dimension(40, 21));
		}
		return jFormattedTextFieldTopNp;
	}

	private JCheckBox getJCheckBoxTopN() {
    	if (jCheckBoxTopN == null) {
    		jCheckBoxTopN = new JCheckBox();
    		jCheckBoxTopN.setText("Top");
    	}
    	return jCheckBoxTopN;
    }

	private void initButtonGroup1() {
    	buttonGroup1 = new ButtonGroup();
    	buttonGroup1.add(getJCheckBoxMinIntens());
    	buttonGroup1.add(getJCheckBoxTopNRegion());
    	buttonGroup1.add(getJCheckBoxTopN());
    }

	private JFormattedTextField getJFormattedTextFieldFragToler() {
		if (jFormattedTextFieldFragToler == null) {
			jFormattedTextFieldFragToler = new JFormattedTextField(new Float(1));
			jFormattedTextFieldFragToler
			        .setHorizontalAlignment(SwingConstants.CENTER);
			jFormattedTextFieldFragToler
			        .setPreferredSize(new Dimension(30, 21));
		}
		return jFormattedTextFieldFragToler;
	}

	private JLabel getJLabel4() {
		if (jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("Fragment tolerance (+- Da)");
		}
		return jLabel4;
	}

	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jPanel3.add(getJButtonStart());
		}
		return jPanel3;
	}

	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setText("Start");
			jButtonStart.setMinimumSize(new Dimension(73, 25));
			jButtonStart.setMaximumSize(new Dimension(73, 25));
			jButtonStart.addActionListener(this);
		}
		return jButtonStart;
	}

	private JProgressBar getJProgressBar0() {
		if (jProgressBar0 == null) {
			jProgressBar0 = new JProgressBar();
			jProgressBar0.setMaximum(100);
		}
		return jProgressBar0;
	}

	private JFormattedTextField getJFormattedTextFieldMassWindow() {
		if (jFormattedTextFieldMassWindow == null) {
			jFormattedTextFieldMassWindow = new JFormattedTextField(
			        new Integer(100));
			jFormattedTextFieldMassWindow
			        .setHorizontalAlignment(SwingConstants.CENTER);
			jFormattedTextFieldMassWindow
			        .setPreferredSize(new Dimension(40, 21));
		}
		return jFormattedTextFieldMassWindow;
	}

	private JLabel getJLabel3() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("m/z");
		}
		return jLabel3;
	}

	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("peaks per");
		}
		return jLabel2;
	}

	private JFormattedTextField getJFormattedTextFieldTopNRegion() {
		if (jFormattedTextFieldTopNRegion == null) {
			jFormattedTextFieldTopNRegion = new JFormattedTextField(
			        new Integer(10));
			jFormattedTextFieldTopNRegion
			        .setHorizontalAlignment(SwingConstants.CENTER);
			jFormattedTextFieldTopNRegion
			        .setPreferredSize(new Dimension(40, 21));
		}
		return jFormattedTextFieldTopNRegion;
	}

	private JCheckBox getJCheckBoxTopNRegion() {
    	if (jCheckBoxTopNRegion == null) {
    		jCheckBoxTopNRegion = new JCheckBox();
    		jCheckBoxTopNRegion.setText("Top ");
    	}
    	return jCheckBoxTopNRegion;
    }

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("%");
		}
		return jLabel1;
	}

	private JFormattedTextField getJFormattedTextFieldIntens() {
		if (jFormattedTextFieldIntens == null) {
			jFormattedTextFieldIntens = new JFormattedTextField(new Float(10));
			jFormattedTextFieldIntens
			        .setHorizontalAlignment(SwingConstants.CENTER);
			jFormattedTextFieldIntens.setPreferredSize(new Dimension(30, 21));
		}
		return jFormattedTextFieldIntens;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Minimum number of continuous b or y type ions");
		}
		return jLabel0;
	}

	private JFormattedTextField getJFormattedTextFieldContinue() {
		if (jFormattedTextFieldContinue == null) {
			jFormattedTextFieldContinue = new JFormattedTextField(new Short(
			        (short) 3));
			jFormattedTextFieldContinue
			        .setHorizontalAlignment(SwingConstants.CENTER);
			jFormattedTextFieldContinue.setPreferredSize(new Dimension(30, 21));
		}
		return jFormattedTextFieldContinue;
	}

	private JPanel getJPanel2() {
    	if (jPanel2 == null) {
    		jPanel2 = new JPanel();
    		jPanel2.setBorder(BorderFactory.createTitledBorder(null, "Series", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12),
    				new Color(59, 59, 59)));
    		jPanel2.setLayout(new GroupLayout());
    		jPanel2.add(getJLabel0(), new Constraints(new Leading(0, 12, 12), new Leading(-2, 12, 12)));
    		jPanel2.add(getJFormattedTextFieldContinue(), new Constraints(new Trailing(12, 276, 276), new Leading(-3, 12, 12)));
    		jPanel2.add(getJCheckBoxby(), new Constraints(new Leading(-2, 10, 10), new Leading(18, 10, 10)));
    		jPanel2.add(getJCheckBoxcz(), new Constraints(new Leading(64, 6, 6), new Leading(18, 6, 6)));
    	}
    	return jPanel2;
    }

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.setBorder(BorderFactory.createTitledBorder(null, "Peak intensity", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif",
    				Font.BOLD, 12), new Color(59, 59, 59)));
    		jPanel0.setLayout(new GroupLayout());
    		jPanel0.add(getJCheckBoxNeu(), new Constraints(new Leading(0, 12, 12), new Leading(-3, 10, 10)));
    		jPanel0.add(getJPanel1(), new Constraints(new Bilateral(0, 0, 179), new Leading(24, 92, 10, 10)));
    	}
    	return jPanel0;
    }

	private JPanel getJPanel1() {
    	if (jPanel1 == null) {
    		jPanel1 = new JPanel();
    		jPanel1.setBorder(new LineBorder(Color.black, 1, false));
    		jPanel1.setLayout(new GroupLayout());
    		jPanel1.add(getJCheckBoxMinIntens(), new Constraints(new Leading(0, 12, 12), new Leading(5, 10, 10)));
    		jPanel1.add(getJFormattedTextFieldIntens(), new Constraints(new Leading(175, 40, 10, 10), new Leading(4, 12, 12)));
    		jPanel1.add(getJLabel1(), new Constraints(new Leading(220, 10, 10), new Leading(5, 12, 12)));
    		jPanel1.add(getJCheckBoxTopNRegion(), new Constraints(new Leading(0, 12, 12), new Leading(34, 12, 12)));
    		jPanel1.add(getJFormattedTextFieldTopNRegion(), new Constraints(new Leading(47, 12, 12), new Leading(33, 12, 12)));
    		jPanel1.add(getJLabel2(), new Constraints(new Leading(92, 10, 10), new Leading(34, 12, 12)));
    		jPanel1.add(getJLabel3(), new Constraints(new Leading(200, 10, 10), new Leading(35, 12, 12)));
    		jPanel1.add(getJFormattedTextFieldMassWindow(), new Constraints(new Leading(153, 10, 10), new Leading(32, 12, 12)));
    		jPanel1.add(getJCheckBoxTopN(), new Constraints(new Leading(0, 6, 6), new Leading(65, 6, 6)));
    		jPanel1.add(getJFormattedTextFieldTopNp(), new Constraints(new Leading(47, 6, 6), new Leading(64, 6, 6)));
    		jPanel1.add(getJLabel5(), new Constraints(new Leading(92, 55, 6, 6), new Leading(65, 6, 6)));
    	}
    	return jPanel1;
    }

	private JCheckBox getJCheckBoxMinIntens() {
    	if (jCheckBoxMinIntens == null) {
    		jCheckBoxMinIntens = new JCheckBox();
    		jCheckBoxMinIntens.setSelected(true);
    		jCheckBoxMinIntens.setText("Minimum intensity percent");
    	}
    	return jCheckBoxMinIntens;
    }

	private JCheckBox getJCheckBoxNeu() {
    	if (jCheckBoxNeu == null) {
    		jCheckBoxNeu = new JCheckBox();
    		jCheckBoxNeu.setSelected(true);
    		jCheckBoxNeu.setText("Exclude the neutral loss peaks");
    	}
    	return jCheckBoxNeu;
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

	/**
	 * The spectrum filter
	 * 
	 * @return
	 */
	private ISpectrumFilter getSpectrumFilter() {

		ISpectrumFilter neuFilter = null;

		if (this.getJCheckBoxNeu().isSelected()) {
			NeutralLossInfo[] infos = this.getNeutralLossSettingPanel0()
			        .getNeutralLossInfo();

			if (infos != null)
				neuFilter = new NeutralLossSpectrumFilter(infos, this
				        .getThreshold());
		}

		ISpectrumFilter intenseFilter = null;

		if (this.getJCheckBoxMinIntens().isSelected()) {

			double min_int = Double.parseDouble(this
			        .getJFormattedTextFieldIntens().getText()) / 100;

			intenseFilter = new MinimumIntensityFilter(min_int);
		} else if (this.getJCheckBoxTopN().isSelected()) {
			int topn = Integer.parseInt(this.getJFormattedTextFieldTopNp()
			        .getText());
			intenseFilter = new TopNIntensityFilter(topn);
		} else if (this.getJCheckBoxTopNRegion().isSelected()) {
			int topn = Integer.parseInt(this.getJFormattedTextFieldTopNRegion()
			        .getText());
			int window = Integer.parseInt(this
			        .getJFormattedTextFieldMassWindow().getText());
			intenseFilter = new RegionTopNIntensityFilter(topn, window);
		}

		return new MultiSpectrumFilter(neuFilter, intenseFilter);
	}

	/**
	 * The spectrum tolerance
	 * 
	 * @return
	 */
	private ISpectrumThreshold getThreshold() {
		double tol = Double.parseDouble(this.getJFormattedTextFieldFragToler()
		        .getText());

		SpectrumThreshold threshold = new SpectrumThreshold(tol, 0);

		return threshold;
	}

	private class MultiSpectrumFilter implements ISpectrumFilter {

		private ISpectrumFilter neuFilter, intensFilter;

		private MultiSpectrumFilter(ISpectrumFilter neuFilter,
		        ISpectrumFilter intensFilter) {
			this.neuFilter = neuFilter;
			this.intensFilter = intensFilter;
		}

		@Override
		public IMS2PeakList filter(IMS2PeakList peaklist) {

			IMS2PeakList filtered = peaklist;

			if (this.neuFilter != null) {
				filtered = this.neuFilter.filter(peaklist);
			}

			return this.intensFilter.filter(filtered);
		}

	}

	/**
	 * The type of ions
	 * 
	 * @return
	 */
	private int[] getIonTypes() {

		if (this.getJCheckBoxby().isSelected())
			return new int[] { Ion.TYPE_B, Ion.TYPE_Y };

		if (this.jCheckBoxcz.isSelected())
			return new int[] { Ion.TYPE_C, Ion.TYPE_Z };

		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();

		try {

			if (obj == this.getJButtonStart()) {

				if (this.getter != null) {

					int continu = Integer.parseInt(this
					        .getJFormattedTextFieldContinue().getText());

					final PeptideRowReader reader = this.getter
					        .getSelectedPeptideReader();

					final AutoFilterTask task = new AutoFilterTask(reader, this
					        .getSpectrumFilter(), this.getThreshold(), this
					        .getIonTypes(), this.getJCheckBoxIsotope()
					        .isSelected(), continu);

					new Thread() {

						@Override
						public void run() {

							try {
								getJButtonStart().setEnabled(false);

								while (task.hasNext()) {
									task.processNext();

									getJProgressBar0()
									        .setValue(
									                (int) (task
									                        .completedPercent() * 100));
								}

								if (viewerFrame != null)
									viewerFrame.repaint();

								UIutilities.getWindowForComponent(
								        AutoManualValPanel.this).dispose();

								PeptideListPagedRowGettor getter = reader
								        .getDeselectedSubset();
								if (getter != null) {
									new SubpeptideListViewerDialog(viewerFrame,
									        getter).setVisible(true);
								} else {
									JOptionPane.showMessageDialog(viewerFrame,
									        "All peptides pass the filter",
									        "Error", JOptionPane.ERROR_MESSAGE);
								}

							} finally {
								getJButtonStart().setEnabled(true);
							}
						}

					}.start();
				}

				return;
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex, "Error",
			        JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
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
				frame.setTitle("AutoManualValPanel");
				AutoManualValPanel content = new AutoManualValPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
