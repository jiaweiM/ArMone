/* 
 ******************************************************************************
 * File: PeptideListViewer.java * * * Created on 04-09-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.drawjf.SpectrumMatchDatasetConstructor;
import cn.ac.dicp.gp1809.proteome.drawjf.gui.PeakListViewerFrame;

/**
 * Show the sub set of the peptides in the peptide list file. Commonly the sub
 * set of peptides are generated from the
 * 
 * @author Xinning
 * @version 0.1, 04-09-2009, 20:57:09
 */
public class SubPeptideListViewerPanel extends JPanel implements ItemListener {

	private static final long serialVersionUID = 1L;
	private PeptideListPagedRowGettor getter;
	private PeptideSelectionListListener selectionListener;
	private SpectrumMatchDatasetConstructor constructor;

	private PeptideListTablePanel peptideListTable1;
	private PeakListViewerFrame peakListViewerFrame0;
	private JCheckBox jCheckBoxSpectrum;
	private JPanel jPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public SubPeptideListViewerPanel() {
		this(null);
	}

	public SubPeptideListViewerPanel(PeptideListPagedRowGettor getter) {
		this.getter = getter;

		if (getter != null) {
			this.constructor = new SpectrumMatchDatasetConstructor(getter
			        .getSearchParameter());
		}

		initComponents();
	}

	private void initComponents() {
    	setMinimumSize(new Dimension(862, 500));
    	setPreferredSize(new Dimension(862, 515));
    	setLayout(new GroupLayout());
    	add(getPeptideListTable1(), new Constraints(new Bilateral(0, 3, 859), new Bilateral(0, 28, 10, 481)));
    	add(getJPanel0(), new Constraints(new Leading(0, 862, 10, 10), new Trailing(0, 27, 10, 10)));
    	setSize(872, 506);
    }

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.setLayout(new GroupLayout());
    		jPanel0.add(getJCheckBoxSpectrum(), new Constraints(new Leading(6, 6, 6), new Leading(6, 6, 6)));
    	}
    	return jPanel0;
    }

	private JCheckBox getJCheckBoxSpectrum() {
    	if (jCheckBoxSpectrum == null) {
    		jCheckBoxSpectrum = new JCheckBox();
    		jCheckBoxSpectrum.setText("Show spectrum match infomation");
    		jCheckBoxSpectrum.addItemListener(this);
    	}
    	return jCheckBoxSpectrum;
    }

	private PeakListViewerFrame getPeakListViewerPanel0() {
		if (peakListViewerFrame0 == null) {
			peakListViewerFrame0 = new PeakListViewerFrame();
			peakListViewerFrame0.setAlwaysOnTop(true);
			peakListViewerFrame0
			        .setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			peakListViewerFrame0.setLocationRelativeTo(this);
		}
		return peakListViewerFrame0;
	}

	private PeptideSelectionListListener getPeptideSelectionListListener() {
		if (this.selectionListener == null) {
			this.selectionListener = new PeptideSelectionListListener(
			        this.getter, this.constructor, this
			                .getPeakListViewerPanel0());
		}
		return this.selectionListener;
	}


	private PeptideListTablePanel getPeptideListTable1() {
		if (peptideListTable1 == null) {
			peptideListTable1 = new PeptideListTablePanel(this.getter);
			peptideListTable1.setMinimumSize(new Dimension(300, 100));
			peptideListTable1.setPreferredSize(new Dimension(300, 100));
		}
		return peptideListTable1;
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
	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getSource();

		if (obj == this.getJCheckBoxSpectrum()) {

			if (this.getJCheckBoxSpectrum().isSelected()) {
				this.getPeakListViewerPanel0().setVisible(true);
				if (this.getter != null)
					this.getPeptideListTable1().addPeptideSelectionListener(
					        this.getPeptideSelectionListListener());
			} else {
				this.getPeakListViewerPanel0().setVisible(false);
				if (this.getter != null)
					this.getPeptideListTable1().removePeptideSelectionListener(
					        this.getPeptideSelectionListListener());
			}

			return;
		}
	}

	/**
	 * Action when a peptide in the list was selected
	 * 
	 * @author Xinning
	 * @version 0.1, 04-13-2009, 20:31:47
	 */
	private class PeptideSelectionListListener implements ListSelectionListener {

		private PeptideListPagedRowGettor getter;
		private PeakListViewerFrame frame;
		private SpectrumMatchDatasetConstructor constructor;

		public PeptideSelectionListListener(PeptideListPagedRowGettor getter,
		        SpectrumMatchDatasetConstructor constructor,
		        PeakListViewerFrame frame) {
			this.getter = getter;
			this.constructor = constructor;
			this.frame = frame;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {

			if (e.getValueIsAdjusting()) {
				return;
			}

			if (getter != null) {
				ListSelectionModel model = (ListSelectionModel) e.getSource();

				if (model.getSelectionMode() != ListSelectionModel.SINGLE_SELECTION) {
					throw new IllegalArgumentException(
					        "Can only parse the single selection.");
				}

				int first = e.getFirstIndex();
				int last = e.getLastIndex();
				if (model.isSelectedIndex(first)) {

					PeptideRowObject peprow = getter.getRowandSpectra(first);

					this.frame.drawMatchedSpectrum(this.constructor, peprow
					        .getPeptide(), peprow.getPeakLists());
				} else {
					PeptideRowObject peprow = getter.getRowandSpectra(last);
					this.frame.drawMatchedSpectrum(this.constructor, peprow
					        .getPeptide(), peprow.getPeakLists());
				}
			}
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
				frame.setTitle("PeptideListViewer");

				PeptideListPagedRowGettor gettor = null;

				try {
					gettor = new PeptideListPagedRowGettor(
					        "D:\\try2\\f001460.dat.ppl");
				} catch (FileDamageException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				SubPeptideListViewerPanel content = new SubPeptideListViewerPanel(
				        gettor);
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
