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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.ga.sequest.Optimizer;
import cn.ac.dicp.gp1809.ga.sequest.SequestPplDataForInput;
import cn.ac.dicp.gp1809.ga.sequest.Optimizer.OptimizedFilter;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultDecoyPeptideFilter;
import cn.ac.dicp.gp1809.proteome.drawjf.SpectrumMatchDatasetConstructor;
import cn.ac.dicp.gp1809.proteome.drawjf.gui.PeakListViewerFrame;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.CriteriaFrame;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.ISFOERListener;
import cn.ac.dicp.gp1809.proteome.gui.PeptideListPagedRowGettor.PeptideRowReader;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;

/**
 * The viewer for peptides in peptide list file
 * 
 * @author Xinning
 * @version 0.1.1, 09-02-2009, 21:20:18
 */
public class PeptideListViewerPanel extends JPanel implements ItemListener {

	private static final long serialVersionUID = 1L;
	private PeptideListPagedRowGettor getter;
	private PeptideSelectionListListener selectionListener;
	private SpectrumMatchDatasetConstructor constructor;
	private DefaultDecoyPeptideFilter deocyFilter = new DefaultDecoyPeptideFilter();
	private IPeptideCriteria usedFilter;

	private PeptideListTablePanel peptideListTable1;
	private CriteriaFrame criteriaFrame0;
	private PeakListViewerFrame peakListViewerFrame0;
	private JCheckBox jCheckBoxFilter;
	private JCheckBox jCheckBoxSpectrum;
	private JPanel jPanel0;
	private JCheckBox jCheckBoxRmvDeocy;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PeptideListViewerPanel() {
		this(null);
	}

	public PeptideListViewerPanel(PeptideListPagedRowGettor getter) {
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

	private JCheckBox getJCheckBoxRmvDeocy() {
    	if (jCheckBoxRmvDeocy == null) {
    		jCheckBoxRmvDeocy = new JCheckBox();
    		jCheckBoxRmvDeocy.setText("Remove decoy peptides");
    		jCheckBoxRmvDeocy.addItemListener(this);
    	}
    	return jCheckBoxRmvDeocy;
    }

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.setLayout(new GroupLayout());
    		jPanel0.add(getJCheckBoxFilter(), new Constraints(new Leading(6, 6, 6), new Leading(6, 6, 6)));
    		jPanel0.add(getJCheckBoxSpectrum(), new Constraints(new Leading(316, 10, 10), new Leading(6, 6, 6)));
    		jPanel0.add(getJCheckBoxRmvDeocy(), new Constraints(new Leading(604, 10, 10), new Leading(6, 6, 6)));
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

	private JCheckBox getJCheckBoxFilter() {
    	if (jCheckBoxFilter == null) {
    		jCheckBoxFilter = new JCheckBox();
    		jCheckBoxFilter.setText("Filter");
    		jCheckBoxFilter.addItemListener(this);
    	}
    	return jCheckBoxFilter;
    }

	private PeakListViewerFrame getPeakListViewerPanel0() {
		if (peakListViewerFrame0 == null) {
			peakListViewerFrame0 = new PeakListViewerFrame(this.getter != null
			        && this.getter.getPeptideType().isPeptidePair());
			peakListViewerFrame0.pack();
			peakListViewerFrame0.setAlwaysOnTop(true);
//			peakListViewerFrame0
//			        .setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			peakListViewerFrame0.setLocationRelativeTo(this);
		}
		return peakListViewerFrame0;
	}

	private PeakListViewerFrame getPeakListViewerPanel0(SpectrumMatchDatasetConstructor constructor, IPeptide peptide,
	        IMS2PeakList[] peaklists) {
//		if (peakListViewerFrame0 == null) {
			peakListViewerFrame0 = new PeakListViewerFrame(this.getter != null
			        && this.getter.getPeptideType().isPeptidePair(), constructor, peptide, peaklists);
			peakListViewerFrame0.pack();
			peakListViewerFrame0.setAlwaysOnTop(true);
//			peakListViewerFrame0
//			        .setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			peakListViewerFrame0.setLocationRelativeTo(this);
//		}
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

	/*
	 * if(this.getter != null) {
	 * criteriaPanel0.selectCriteriaPanel(this.getter.getPeptideType());
	 * criteriaPanel0.addFilterActionListener(new TableFilterListener(this
	 * .getPeptideListTable1(), criteriaPanel0)); }
	 */
	private CriteriaFrame getCriteriaFrame0() {
		if (criteriaFrame0 == null) {
			criteriaFrame0 = new CriteriaFrame();

			if (this.getter != null) {
				criteriaFrame0
				        .selectCriteriaPanel(this.getter.getPeptideType());
				criteriaFrame0.setPeptideTypeSelectable(false);
				criteriaFrame0.addFilterActionListener(new TableFilterListener(
				        this.getPeptideListTable1(), criteriaFrame0));
				
				/*
				 * Current can only sequest
				 */
				if(this.getter.getPeptideType() == PeptideType.SEQUEST) {
					criteriaFrame0.addSFOERActionListener(new SOFERListener(this.getter, this.criteriaFrame0));
				}
				
			}
//			criteriaFrame0.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			criteriaFrame0.setLocationRelativeTo(this);
			criteriaFrame0.setAlwaysOnTop(true);
		}
		return criteriaFrame0;
	}

	private PeptideListTablePanel getPeptideListTable1() {
		if (peptideListTable1 == null) {
			peptideListTable1 = new PeptideListTablePanel(this.getter);
			peptideListTable1.setMinimumSize(new Dimension(300, 100));
			peptideListTable1.setPreferredSize(new Dimension(300, 100));
		}
		return peptideListTable1;
	}

	/**
	 * Filter the peptides in the table
	 * 
	 * @param filter
	 */
	public void addFilter(IPeptideCriteria filter) {
		this.getPeptideListTable1().addFilter(filter);
	}
	
	/**
	 * Remove a filter
	 * 
	 * @param filter
	 */
	public void removeFilter(IPeptideCriteria filter) {
		this.getPeptideListTable1().removeFilter(filter);
	}
	
	public IPeptideCriteria getFilter(){
		return this.getCriteriaFrame0().getCriteria();
	}
	
	public IPeptideCriteria getUsedFilter(){
		return usedFilter;
	}
	
	public void setUsedFilter(IPeptideCriteria filter){
		this.usedFilter = filter;
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

				PeptideRowObject object = this.getPeptideListTable1().getRowObject();
				if(object==null){
					this.getPeakListViewerPanel0().setVisible(true);
				}else{
					IPeptide pep = object.getPeptide();
					IMS2PeakList [] peaks = object.getPeakLists();
					this.getPeakListViewerPanel0(constructor, pep, peaks).setVisible(true);
				}
				
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

		if (obj == this.getJCheckBoxFilter()) {
			this.getCriteriaFrame0().setVisible(
			        this.getJCheckBoxFilter().isSelected());

			return;
		}
		
		if(obj == this.getJCheckBoxRmvDeocy()) {
			
			if(this.getJCheckBoxRmvDeocy().isSelected()) {
				this.peptideListTable1.addFilter(this.deocyFilter);
			}
			else {
				this.peptideListTable1.removeFilter(this.deocyFilter);
			}
			
			return ;
		}
	}
	
	/**
	 * Clean all the related frames and windows when close
	 */
	public void forceClean() {
		if(this.peptideListTable1 != null)
			this.peptideListTable1.forceClean();
		
		if(this.criteriaFrame0 != null)
			this.criteriaFrame0.dispose();
		
		if(this.peakListViewerFrame0 != null)
			this.peakListViewerFrame0.dispose();
	}

	/**
	 * The filter listener for the set filter button
	 * 
	 * 
	 * @author Xinning
	 * @version 0.1, 04-11-2009, 22:18:51
	 */
	private class TableFilterListener implements ActionListener {

		private PeptideListTablePanel table;
		private CriteriaFrame criteriaFrame;
		private IPeptideCriteria preFilter;

		private TableFilterListener(PeptideListTablePanel table,
		        CriteriaFrame criteriaFrame) {
			this.table = table;
			this.criteriaFrame = criteriaFrame;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (this.preFilter != null)
				this.table.removeFilter(preFilter);

			this.preFilter = this.criteriaFrame.getCriteria();
			this.table.addFilter(this.preFilter);
			setUsedFilter(preFilter);
		}

	}
	
	
	
	/**
	 * The filter listener for the set filter button
	 * 
	 * 
	 * @author Xinning
	 * @version 0.1, 04-11-2009, 22:18:51
	 */
	private class SOFERListener implements ISFOERListener {

		private PeptideListPagedRowGettor gettor;
		private CriteriaFrame criteriaFrame;
		
		private boolean useFDR = true;
		private double fdr = 0.01;
		private boolean usd_sprsp = true;
		private boolean use_deltaMS = false;

		private SOFERListener(PeptideListPagedRowGettor gettor,
		        CriteriaFrame criteriaFrame) {
			this.gettor = gettor;
			this.criteriaFrame = criteriaFrame;
		}

		/*
		 * (non-Javadoc)
		 * @see cn.ac.dicp.gp1809.proteome.gui.Criterias.ISFOERListener#setMaxFDR(double)
		 */
		public void setMaxFDR(double fdr) {
			this.fdr = fdr;
		}
		
		/*
		 * (non-Javadoc)
		 * @see cn.ac.dicp.gp1809.proteome.gui.Criterias.ISFOERListener#setUseSp(boolean)
		 */
		public void setUseSpRsp(boolean use) {
			this.usd_sprsp = use;
		}
		
		
		public void setUseDeltaMZ(boolean use) {
			this.use_deltaMS = use;
		}
		
		public void setUseFDR(boolean use) {
			this.useFDR = use;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(this.gettor.getPeptideType() == PeptideType.SEQUEST) {
				try {
					SequestPplDataForInput input = new SequestPplDataForInput();
					
					
					System.out.println("Optimizing 1+ peptides ...");
					PeptideRowReader reader = this.gettor.getSelectedAllPeptideReader();
					float[][] peptides = input.getPeptide(reader, (short)1);
					reader.close();
					
//					Optimizer optimizer = new Optimizer(peptides);
					Optimizer optimizer = new Optimizer(peptides, fdr, !this.usd_sprsp, use_deltaMS, (short) 0);
					OptimizedFilter ofiler1 = optimizer.optimize();
					
					System.out.println("Optimizing 2+ peptides ...");
					reader = this.gettor.getSelectedAllPeptideReader();
					peptides = input.getPeptide(reader, (short)2);
					reader.close();
					
					optimizer = new Optimizer(peptides, fdr, !this.usd_sprsp, use_deltaMS, (short) 0);
					OptimizedFilter ofiler2 = optimizer.optimize();
					
					System.out.println("Optimizing 3+ peptides ...");
					reader = this.gettor.getSelectedAllPeptideReader();
					peptides = input.getPeptide(reader, (short)3);
					reader.close();
					
					optimizer = new Optimizer(peptides, fdr, !this.usd_sprsp, use_deltaMS, (short) 0);
					OptimizedFilter ofiler3 = optimizer.optimize();
					
					System.out.println("Optimizing >=4+ peptides ...");
					
					reader = this.gettor.getSelectedAllPeptideReader();
					peptides = input.getPeptideWithHigherCharge(reader, (short)4);
					reader.close();
					
					optimizer = new Optimizer(peptides, fdr, !this.usd_sprsp, use_deltaMS, (short) 0);
					OptimizedFilter ofiler4 = optimizer.optimize();
					
					
					
					float[] xcorrs = new float[4];
					float[] dcns = new float[4];
					float[] sps = new float[4];
					short[] rsps = new short[4];
					float[] deltaMSs = new float[4];
					
					if(ofiler1 != null) {
						xcorrs[0] = ofiler1.getXcorr();
						dcns[0] = ofiler1.getDcn();
						sps[0] = ofiler1.getSp();
						rsps[0] = ofiler1.getRsp();
						deltaMSs[0] = ofiler1.getDeltaMSppm();
						
						if(!this.usd_sprsp) {
							sps[0] = 0;
							rsps[0] = 255;
						}
						
						if(!this.use_deltaMS) {
							deltaMSs[0] = 10000;
						}
					}
					
					if(ofiler2 != null) {
						xcorrs[1] = ofiler2.getXcorr();
						dcns[1] = ofiler2.getDcn();
						sps[1] = ofiler2.getSp();
						rsps[1] = ofiler2.getRsp();
						deltaMSs[1] = ofiler2.getDeltaMSppm();
						
						if(!this.usd_sprsp) {
							sps[1] = 0;
							rsps[1] = 255;
						}
						
						if(!this.use_deltaMS) {
							deltaMSs[1] = 10000;
						}
					}

					
					if(ofiler3 != null) {
						xcorrs[2] = ofiler3.getXcorr();
						dcns[2] = ofiler3.getDcn();
						sps[2] = ofiler3.getSp();
						rsps[2] = ofiler3.getRsp();
						deltaMSs[2] = ofiler3.getDeltaMSppm();
						
						if(!this.usd_sprsp) {
							sps[2] = 0;
							rsps[2] = 255;
						}
						
						if(!this.use_deltaMS) {
							deltaMSs[2] = 10000;
						}
					}

					
					if(ofiler4 != null) {
						xcorrs[3] = ofiler4.getXcorr();
						dcns[3] = ofiler4.getDcn();
						sps[3] = ofiler4.getSp();
						rsps[3] = ofiler4.getRsp();
						deltaMSs[3] = ofiler4.getDeltaMSppm();
						
						if(!this.usd_sprsp) {
							sps[3] = 0;
							rsps[3] = 255;
						}
						
						if(!this.use_deltaMS) {
							deltaMSs[3] = 10000;
						}
					}

					
					this.criteriaFrame.selectCriteriaPanel(xcorrs, dcns, sps, rsps, deltaMSs);
				}catch(Exception ex) {
					throw new RuntimeException (ex);
				}

			}
			

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
					        "F:\\data\\wxl\\100810_SILAC_IGF_phosphopeptide_c0.ppl");
				} catch (FileDamageException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				PeptideListViewerPanel content = new PeptideListViewerPanel(
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
