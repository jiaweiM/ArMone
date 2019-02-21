/* 
 ******************************************************************************
 * File: PeptideListViewer.java * * * Created on 04-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import cn.ac.dicp.gp1809.proteome.APIVASEII.gui.AscoreCalculatingFrame;
import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPepxmlWriter;
import cn.ac.dicp.gp1809.proteome.IO.pepxml.PepxmlWriterFactory;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.PhosPepCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.protein.GlycoProteinFilter;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.protein.PhosProteinFilter;
import cn.ac.dicp.gp1809.proteome.gui2.FastaExpFrame;
import cn.ac.dicp.gp1809.proteome.phosval.AutoManualValDlg;
import cn.ac.dicp.gp1809.proteome.phosval.BatchDrawDlg;
import cn.ac.dicp.gp1809.proteome.phosval.PhosPeptideListViewerPanel;
import cn.ac.dicp.gp1809.util.gui.AboutDlg;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

/**
 * 
 * @author Xinning
 * @version 0.1, 04-12-2009, 19:29:08
 */
public class PeptideListViewer extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private final PeptideListPagedRowGettor getter;
	private MyJFileChooser pplChooser,csvChooser, pepxmlChooser;
	private int peptidePTMType = 0;
	
	private JMenuItem jMenuItemExport;
	private JMenu jMenu0;
	private JMenuBar jMenuBar0;
	private JPanel peptideListViewerPanel0;
	private JMenuItem jMenuItemBatchDraw;
	private JMenuItem jMenuItemProIntegr;
	private JMenuItem jMenuItemToCsv;
	private JMenu jMenu1;
	private JMenuItem jMenuItemLocal;
	private JMenuItem jMenuItemPepxml;
	private JMenuItem jMenuItemAutoFilter;
	private JMenuItem jMenuItemExpFasta;
	private JMenuItem jMenuItemInfo;
	private JMenu jMenuView;
	private File currentFile;
	
	private JMenuItem jMenuItemSilac;
	private JMenu jMenuQuan;
	private JMenuItem jMenuItemDimethyl;
	private JMenuItem jMenuItemUDF;
	private JMenuItem jMenuItemICPL;
	private JMenuItem jMenuItemLFree;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PeptideListViewer() {
		this(null);
	}

	public PeptideListViewer(PeptideListPagedRowGettor getter) {
		this(getter, 0);
	}
	
	public void setCurrentFile(File currentFile){
		this.currentFile = currentFile;
	}
	
	public PeptideListViewer(PeptideListPagedRowGettor getter, int peptidePTMType) {
		this.getter = getter;
		this.peptidePTMType = peptidePTMType;
		
		initComponents();
		
		this.addWindowListener(new MyWindowAdapter());
		
		this.setTitle("Peptide Viewer");
		this.setLocationRelativeTo(null);
	}
	
	public PeptideListViewer(String pplfile, PeptideListPagedRowGettor getter, int peptidePTMType) {
		this.getter = getter;
		this.peptidePTMType = peptidePTMType;
		
		initComponents();
		
		this.addWindowListener(new MyWindowAdapter());
		
		this.setTitle("Peptide Viewer - \""+pplfile+"\"");
		this.setLocationRelativeTo(null);
	}
	
	private class MyWindowAdapter extends WindowAdapter {
		
		MyWindowAdapter(){
			
		}
		
		@Override
		public void windowClosing(WindowEvent e) {
			if(peptideListViewerPanel0 instanceof PeptideListViewerPanel)
				((PeptideListViewerPanel) peptideListViewerPanel0).forceClean();
			else if(peptideListViewerPanel0 instanceof PhosPeptideListViewerPanel)
				((PhosPeptideListViewerPanel) peptideListViewerPanel0).forceClean();
			
			if(getter != null)
				getter.closeList();
		}
	}

	private JMenu getJMenuView() {
    	if (jMenuView == null) {
    		jMenuView = new JMenu();
    		jMenuView.setText("View");
    		jMenuView.add(getJMenuItemInfo());
    	}
    	return jMenuView;
    }
	
	private JMenuItem getJMenuItemInfo() {
    	if (jMenuItemInfo == null) {
    		jMenuItemInfo = new JMenuItem();
    		jMenuItemInfo.setText("File info");
    		jMenuItemInfo.addActionListener(this);
    		jMenuItemInfo.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_V, InputEvent.ALT_DOWN_MASK));
    	}
    	return jMenuItemInfo;
    }
	
	private void initComponents() {
		setTitle("Peptide Viewer");
		setLayout(new GroupLayout());
		add(getPeptideListViewerPanel0(), new Constraints(new Bilateral(6, 6, 874), new Bilateral(6, 8, 10)));
		setJMenuBar(getJMenuBar0());
		setSize(886, 611);
	}

	private JMenuItem getJMenuItemICPL() {
		if (jMenuItemICPL == null) {
			jMenuItemICPL = new JMenuItem();
			jMenuItemICPL.setText("ICPL");
			jMenuItemICPL.addActionListener(this);
		}
		return jMenuItemICPL;
	}

	private JMenuItem getJMenuItemUDF() {
		if (jMenuItemUDF == null) {
			jMenuItemUDF = new JMenuItem();
			jMenuItemUDF.setText("User-Defined");
			jMenuItemUDF.addActionListener(this);
		}
		return jMenuItemUDF;
	}

	private JMenuItem getJMenuItemDimethyl() {
		if (jMenuItemDimethyl == null) {
			jMenuItemDimethyl = new JMenuItem();
			jMenuItemDimethyl.setText("Dimethyl");
			jMenuItemDimethyl.addActionListener(this);
		}
		return jMenuItemDimethyl;
	}

	private JMenu getJMenuQuan() {
		if (jMenuQuan == null) {
			jMenuQuan = new JMenu();
			jMenuQuan.setText("Quantitation");
			jMenuQuan.add(getJMenuItemSilac());
			jMenuQuan.add(getJMenuItemDimethyl());
			jMenuQuan.add(getJMenuItemICPL());
			jMenuQuan.add(getJMenuItemUDF());
			jMenuQuan.add(getJMenuItemLFree());
		}
		return jMenuQuan;
	}

	private JMenuItem getJMenuItemSilac() {
		if (jMenuItemSilac == null) {
			jMenuItemSilac = new JMenuItem();
			jMenuItemSilac.setText("SILAC");
			jMenuItemSilac.addActionListener(this);
		}
		return jMenuItemSilac;
	}
	
	private JMenuItem getJMenuItemLFree() {
		if (jMenuItemLFree == null) {
			jMenuItemLFree = new JMenuItem();
			jMenuItemLFree.setText("Label_Free");
			jMenuItemLFree.addActionListener(this);
		}
		return jMenuItemLFree;
	}

	private JMenuItem getJMenuItemExpFasta() {
		if (jMenuItemExpFasta == null) {
			jMenuItemExpFasta = new JMenuItem();
			jMenuItemExpFasta.setText("Export Fasta");
			jMenuItemExpFasta.addActionListener(this);
			jMenuItemExpFasta.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK));
		}
		return jMenuItemExpFasta;
	}

	private JMenuItem getJMenuItemPepxml() {
    	if (jMenuItemPepxml == null) {
    		jMenuItemPepxml = new JMenuItem();
    		jMenuItemPepxml.setText("Export Pepxml");
    		jMenuItemPepxml.addActionListener(this);
    		jMenuItemPepxml.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK));
    	}
    	return jMenuItemPepxml;
    }

	private JMenuItem getJMenuItemLocal() {
    	if (jMenuItemLocal == null) {
    		jMenuItemLocal = new JMenuItem();
    		jMenuItemLocal.setText("PhosphoSite localization");
    		jMenuItemLocal.addActionListener(this);
    	}
    	return jMenuItemLocal;
    }

	private JMenu getJMenu1() {
    	if (jMenu1 == null) {
    		jMenu1 = new JMenu();
    		jMenu1.setText("Export");
    		jMenu1.add(getJMenuItemToCsv());
    		jMenu1.add(getJMenuItemExport());
    	}
    	return jMenu1;
    }

	private JMenuItem getJMenuItemToCsv() {
    	if (jMenuItemToCsv == null) {
    		jMenuItemToCsv = new JMenuItem();
    		jMenuItemToCsv.setText("To csv");
    		jMenuItemToCsv.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK));
    		jMenuItemToCsv.addActionListener(this);
    	}
    	return jMenuItemToCsv;
    }

	private JMenuItem getJMenuItemProIntegr() {
    	if (jMenuItemProIntegr == null) {
    		jMenuItemProIntegr = new JMenuItem();
    		jMenuItemProIntegr.setText("Protein integration");
			jMenuItemProIntegr.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK));
			jMenuItemProIntegr.addActionListener(this);
    	}
    	return jMenuItemProIntegr;
    }

	private JMenuItem getJMenuItemBatchDraw() {
    	if (jMenuItemBatchDraw == null) {
    		jMenuItemBatchDraw = new JMenuItem();
    		jMenuItemBatchDraw.setText("Batch draw");
    		jMenuItemBatchDraw.addActionListener(this);
    		jMenuItemBatchDraw.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_B, InputEvent.ALT_DOWN_MASK));
    	}
    	return jMenuItemBatchDraw;
    }

	private JPanel getPeptideListViewerPanel0() {
    	if (peptideListViewerPanel0 == null) {
    		
    		switch (peptidePTMType){
    			case PeptideLoader.peptide:
    				peptideListViewerPanel0 = new PeptideListViewerPanel(this.getter);
    				break;
    				
    			case PeptideLoader.phosPeptide:
    				peptideListViewerPanel0 = new PhosPeptideListViewerPanel(this.getter);
    				break;
    			
    		}
    	}
    	return peptideListViewerPanel0;
    }

	private JMenuItem getJMenuItemAutoFilter() {
    	if (jMenuItemAutoFilter == null) {
    		jMenuItemAutoFilter = new JMenuItem();
    		jMenuItemAutoFilter.setText("Auto filtering");
    		jMenuItemAutoFilter.addActionListener(this);
    		jMenuItemAutoFilter.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK));
    	}
    	return jMenuItemAutoFilter;
    }

	/**
	 * @return the peptide list file chooser
	 */
	private MyJFileChooser getPplChooser() {
		if (this.pplChooser == null) {
			this.pplChooser = new MyJFileChooser(currentFile);
			this.pplChooser.setFileFilter(new String[] { "ppl" },
			        "Peptide list file (*.ppl)");
		}
		return pplChooser;
	}
	
	/**
	 * @return the peptide list file chooser
	 */
	private MyJFileChooser getCsvChooser() {
		if (this.csvChooser == null) {
			this.csvChooser = new MyJFileChooser(currentFile);
			this.csvChooser.setFileFilter(new String[] { "csv" },
			        "comma delimated file (*.csv)");
		}
		return csvChooser;
	}
	
	/**
	 * @return the pepxml chooser
	 */
	private MyJFileChooser getPepxmlChooser() {
		if (this.pepxmlChooser == null) {
			this.pepxmlChooser = new MyJFileChooser(currentFile);
			this.pepxmlChooser.setFileFilter(new String[] { "xml" },
			        "PepXML (*.xml)");
		}
		return pepxmlChooser;
	}

	private JMenuBar getJMenuBar0() {
		if (jMenuBar0 == null) {
			jMenuBar0 = new JMenuBar();
			jMenuBar0.add(getJMenu0());
			jMenuBar0.add(getJMenuQuan());
			jMenuBar0.add(getJMenuView());
		}
		return jMenuBar0;
	}

	private JMenu getJMenu0() {
		if (jMenu0 == null) {
			jMenu0 = new JMenu();
			jMenu0.setText("Actions");
			jMenu0.add(getJMenu1());
			jMenu0.add(getJMenuItemProIntegr());
			jMenu0.add(getJMenuItemLocal());
			jMenu0.add(getJMenuItemAutoFilter());
			jMenu0.add(getJMenuItemBatchDraw());
			jMenu0.add(getJMenuItemPepxml());
			jMenu0.add(getJMenuItemExpFasta());
		}
		return jMenu0;
	}

	private JMenuItem getJMenuItemExport() {
    	if (jMenuItemExport == null) {
    		jMenuItemExport = new JMenuItem();
    		jMenuItemExport.setText("To Peptide list");
    		jMenuItemExport.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_E, InputEvent.ALT_DOWN_MASK));
    		jMenuItemExport.addActionListener(this);
    	}
    	return jMenuItemExport;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();

		if (obj == this.getJMenuItemExport()) {

			int value = this.getPplChooser().showSaveDialog(this);
			if (value == JFileChooser.APPROVE_OPTION) {
				final String path = this.getPplChooser().getSelectedFile()
				        .getAbsolutePath();

				final Thread thread = new Thread() {

					@Override
					public void run() {

						try {
							getter.exportDisplayToPpl(path);
						} catch (Exception e) {
							JOptionPane.showMessageDialog(
							        PeptideListViewer.this, e);
							e.printStackTrace();
						}
					}

				};

				thread.start();

				/*
				final ProcessingDlg dlg = new ProcessingDlg(this);

				final Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {

						if (!thread.isAlive()) {
							dlg.dispose();
							timer.cancel();
						}

					}

				}, 0, 500);
				*/
			}
			
			return ;
		}
		
		
		if (obj == this.getJMenuItemToCsv()) {

			int value = this.getCsvChooser().showSaveDialog(this);
			if (value == JFileChooser.APPROVE_OPTION) {
				final String path = this.getCsvChooser().getSelectedFile()
				        .getAbsolutePath();

				final Thread thread = new Thread() {

					@Override
					public void run() {

						try {
							getter.exportDisplayToCsv(path);
						} catch (Exception e) {
							JOptionPane.showMessageDialog(
							        PeptideListViewer.this, e);
							e.printStackTrace();
						}
					}

				};

				thread.start();

				/*
				final ProcessingDlg dlg = new ProcessingDlg(this);

				final Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {

						if (!thread.isAlive()) {
							dlg.dispose();
							timer.cancel();
						}

					}

				}, 0, 500);
				*/
			}
			
			return ;
		}
		
		if (obj == this.getJMenuItemPepxml()) {

			int value = this.getPepxmlChooser().showSaveDialog(this);
			if (value == JFileChooser.APPROVE_OPTION) {
				final String path = this.getPepxmlChooser().getSelectedFile()
				        .getAbsolutePath();

				final Thread thread = new Thread() {

					@Override
					public void run() {

						try {
							
							IPeptideListReader reader = getter.getSelectedPeptideReader();
							
							IPepxmlWriter writer = PepxmlWriterFactory.createWriter(reader.getPeptideType(), 
									path, reader.getSearchParameter(), reader.getDecoyJudger());
							
							IPeptide pep;
							
							while((pep = reader.getPeptide())!=null) {
								writer.write(pep, null);
							}
							
							writer.close();
							
						} catch (Exception e) {
							JOptionPane.showMessageDialog(
							        PeptideListViewer.this, e);
							e.printStackTrace();
						}
					}

				};

				thread.start();
			}
			
			return ;
		}
		
		
		if(obj == this.getJMenuItemAutoFilter()) {
			
			AutoManualValDlg frame = new AutoManualValDlg(this, this.getter);
			frame.pack();
			frame.setLocationRelativeTo(this);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			return ;
		}
		
		if(obj == this.getJMenuItemBatchDraw()) {
			
//			BatchDrawFrame frame = new BatchDrawFrame(this.getter.getSelectedPeptideReader());
			BatchDrawDlg frame = new BatchDrawDlg(null,this.getter.getSelectedPeptideReader());
			frame.pack();
			frame.setLocationRelativeTo(this);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			
			return ;
		}

		if(obj == this.getJMenuItemProIntegr()) {
			
			ArrayList <IPeptideCriteria> pepFilters = new ArrayList <IPeptideCriteria>();
			ArrayList <IProteinCriteria> proFilters = new ArrayList <IProteinCriteria>();
			IPeptideCriteria pepFilter = null;
			
			if(peptideListViewerPanel0 instanceof PeptideListViewerPanel){
				pepFilter = 
					((PeptideListViewerPanel) peptideListViewerPanel0).getUsedFilter();
			}
				
			else if(peptideListViewerPanel0 instanceof PhosPeptideListViewerPanel){
				pepFilter = 
					((PhosPeptideListViewerPanel) peptideListViewerPanel0).getFilter();
				IPeptideCriteria phos = 
					((PhosPeptideListViewerPanel) peptideListViewerPanel0).getPTMFilter();
				if(phos!=null){
					IProteinCriteria phosPro = new PhosProteinFilter((PhosPepCriteria) phos);
					proFilters.add(phosPro);
				}
			}
			
			if(pepFilter!=null)
				pepFilters.add(pepFilter);
			
//			at first output all the peptides then add the filter for peptide and protein
//			ProteinIntegrDlg dlg = new ProteinIntegrDlg(this.getter.getSelectedPeptideReader(), currentFile);
			ProteinIntegrDlg dlg = new ProteinIntegrDlg(this.getter.getSelectedAllPeptideReader(), currentFile);
			
			dlg.setPepCriList(pepFilters);
			if(proFilters.size()>0)
				dlg.setProCriList(proFilters);
			
			dlg.getContentPane().setPreferredSize(dlg.getSize());
			dlg.pack();
			dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dlg.setLocationRelativeTo(this);
			dlg.setVisible(true);
			return ;
		}
		
		if(obj == this.getJMenuItemLocal()) {
			AscoreCalculatingFrame frame = new AscoreCalculatingFrame(this.getter != null ? this.getter.getSelectedPeptideReader(): null);
			frame.getContentPane().setPreferredSize(frame.getSize());
			frame.pack();
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setLocationRelativeTo(this);
			frame.setVisible(true);
			return;
		}
/*		
		if(obj == this.getJMenuItemSilac()) {
			LPairCreateFrame qFrame = new LPairCreateFrame(LabelType.SILAC, this.getter.getSelectedPeptideReader(), false);
			qFrame.getContentPane().setPreferredSize(qFrame.getSize());
			qFrame.pack();
			qFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			qFrame.setLocationRelativeTo(this);
			qFrame.setVisible(true);
			qFrame.setCurrentFile(currentFile);
			
			return ;
		}
		
		if(obj == this.getJMenuItemDimethyl()) {
			LPairCreateFrame qFrame = new LPairCreateFrame(LabelType.Dimethyl, this.getter.getSelectedPeptideReader(), false);
			qFrame.getContentPane().setPreferredSize(qFrame.getSize());
			qFrame.pack();
			qFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			qFrame.setLocationRelativeTo(this);
			qFrame.setVisible(true);
			qFrame.setCurrentFile(currentFile);
			
			return ;
		}
		
		if(obj == this.getJMenuItemICPL()) {
			LPairCreateFrame qFrame = new LPairCreateFrame(LabelType.ICPL, this.getter.getSelectedPeptideReader(), false);
			qFrame.getContentPane().setPreferredSize(qFrame.getSize());
			qFrame.pack();
			qFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			qFrame.setLocationRelativeTo(this);
			qFrame.setVisible(true);
			qFrame.setCurrentFile(currentFile);
			
			return ;
		}
		
		if(obj == this.getJMenuItemUDF()) {
			LPairCreateFrame qFrame = new LPairCreateFrame(LabelType.User_Defined, this.getter.getSelectedPeptideReader(), false);
			qFrame.getContentPane().setPreferredSize(qFrame.getSize());
			qFrame.pack();
			qFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			qFrame.setLocationRelativeTo(this);
			qFrame.setVisible(true);
			qFrame.setCurrentFile(currentFile);
			
			return ;
		}
		
		if(obj == this.getJMenuItemLFree()) {
			LFreeCreateFrame qFrame = new LFreeCreateFrame(this.getter.getSelectedPeptideReader());
			qFrame.getContentPane().setPreferredSize(qFrame.getSize());
			qFrame.pack();
			qFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			qFrame.setLocationRelativeTo(this);
			qFrame.setVisible(true);
			qFrame.setCurrentFile(currentFile);
			
			return ;
		}
*/		
		if(obj == this.getJMenuItemExpFasta()) {
			FastaExpFrame fExpPanel = new FastaExpFrame(this.getter.getSelectedPeptideReader());
			fExpPanel.getContentPane().setPreferredSize(fExpPanel.getSize());
			fExpPanel.pack();
			fExpPanel.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			fExpPanel.setLocationRelativeTo(this);
			fExpPanel.setVisible(true);
			
			return ;
		}
		
		if(obj == this.getJMenuItemInfo()) {
			AboutDlg dlg = new AboutDlg(this);
			dlg.setTitle("File info");
			dlg.setSize(550, 260);
			dlg.setAboutInformation(this.getter.getPplInfo());
			dlg.setVisible(true);
			return ;
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
				PeptideListViewer frame = new PeptideListViewer();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("PeptideListViewer");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
