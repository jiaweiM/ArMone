/* 
 ******************************************************************************
 * File: BatchDrawDlg.java * * * Created on 05-26-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.phosval;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.BatchDrawHtmlWriter;
import cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.BatchDrawSelectPanel;
import cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.IBatchDrawWriter;
import cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.branch.BatchDrawPDFWriter;
import cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.branch.BatchDrawTask;
import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-26-2009, 20:16:49
 */
public class BatchDrawDlg extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private IPeptideListReader reader;

	private BatchDrawSelectPanel batchDrawSelectPanel0;
	private JProgressBar jProgressBar0;
	private JButton jButtonStart;
	private JButton jButtonClose;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public BatchDrawDlg() {
		initComponents();
	}

	public BatchDrawDlg(Frame parent, IPeptideListReader reader) {
		super(parent, true);
		this.reader = reader;

		initComponents();
	}

	private void initComponents() {
		setTitle("Batch spectrum drawer");
		setFont(new Font("Dialog", Font.PLAIN, 12));
		setBackground(new Color(204, 232, 207));
		setResizable(false);
		setForeground(Color.black);
		setLayout(new GroupLayout());
		add(getBatchDrawSelectPanel0(), new Constraints(new Bilateral(0, 0, 660), new Bilateral(0, 100, 380, 380)));
		add(getJButtonStart(), new Constraints(new Leading(250, 10, 10), new Leading(425, 10, 10)));
		add(getJButtonClose(), new Constraints(new Leading(350, 10, 10), new Leading(425, 10, 10)));
		add(getJProgressBar0(), new Constraints(new Bilateral(20, 20, 10, 10), new Leading(390, 10, 10)));
		setSize(660, 480);
	}
	
	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText("Close");
			jButtonClose.addActionListener(this);
		}
		return jButtonClose;
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
			jProgressBar0.setMaximum(100);
		}
		return jProgressBar0;
	}

	private BatchDrawSelectPanel getBatchDrawSelectPanel0() {
		if (batchDrawSelectPanel0 == null) {
			batchDrawSelectPanel0 = new BatchDrawSelectPanel();
		}
		return batchDrawSelectPanel0;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Object obj = e.getSource();

		if(obj == this.getJButtonClose()){
			
			this.dispose();
			return;
		}
		
		try {

			if (obj == this.getJButtonStart()) {

				String path = this.getBatchDrawSelectPanel0().getOutput();
				int [] iontypes = this.getBatchDrawSelectPanel0().getIonsType();
				NeutralLossInfo [] lossinfo = this.getBatchDrawSelectPanel0().getNeutralLossInfo();
				SpectrumThreshold thres = this.getBatchDrawSelectPanel0().getSpectrumThreshold();
				boolean useUniPep = this.getBatchDrawSelectPanel0().unipep();
				
				if (path == null || path.length() == 0) {
					throw new NullPointerException("Select the output first");
				}

				if (this.reader == null)
					throw new NullPointerException(
					        "The peptide list reader is null");

				IBatchDrawWriter writer = null;
				int type = this.getBatchDrawSelectPanel0().getOutputType();
				if (type == IBatchDrawWriter.PDF) {
					writer = new BatchDrawPDFWriter(path, reader
					        .getSearchParameter(), reader.getPeptideType());
				}else{
					writer = new BatchDrawHtmlWriter(path, reader
					        .getSearchParameter(), reader.getPeptideType());
				}

				final BatchDrawTask task = new BatchDrawTask(writer, reader, iontypes, 
						useUniPep, thres, lossinfo);

				boolean useProFilter = this.getBatchDrawSelectPanel0().useProFilter();
				
				if(useProFilter){
					task.setUseProFilter(true);
					task.setProFilter(this.getBatchDrawSelectPanel0().getProCriteria());
				}

				new Thread() {

					@Override
					public void run() {

						try {
							BatchDrawDlg.this
							        .setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
							getJButtonStart().setEnabled(false);

							jProgressBar0.setString("Processing...");
							jProgressBar0.setStringPainted(true);
							
							while (task.hasNext()) {
								task.processNext();
								getJProgressBar0().setValue(
								        (int) (task.completedPercent() * 100));
							}

							jProgressBar0.setValue(100);
							jProgressBar0.setString("Finish!");
							jProgressBar0.setStringPainted(true);
							
							task.dispose();

						} finally {
							getJButtonStart().setEnabled(true);
							BatchDrawDlg.this
							        .setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
							System.gc();
						}
					}
				}.start();

				return;
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex, "Error",
			        JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}

	}

}
