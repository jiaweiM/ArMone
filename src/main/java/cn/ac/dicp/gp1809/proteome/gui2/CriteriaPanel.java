/* 
 ******************************************************************************
 * File:CriteriaPanel.java * * * Created on 2011-8-31
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.ga.mascot.MascotOptimizer;
import cn.ac.dicp.gp1809.ga.mascot.MascotPplDataForInput;
import cn.ac.dicp.gp1809.ga.mascot.MascotOptimizer.MascotOptimizedFilter;
import cn.ac.dicp.gp1809.ga.sequest.Optimizer;
import cn.ac.dicp.gp1809.ga.sequest.SequestPplDataForInput;
import cn.ac.dicp.gp1809.ga.sequest.Optimizer.OptimizedFilter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultDeltaMZCriteria;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.CruxCriteriaPanel;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.DeltaMZCriteriaPanel;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.ICriteriaSetter;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.ISFOERListener;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.MascotApivaseCriteriaPanel;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.MascotCriteriaPanel3;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.OMSSACriteriaPanel;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.ProbabilityCriteriaPanel;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.SFOERActionPanel;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.SFOERSequestCriteriaPanel;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.SequestApivaseCriteriaPanel;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.SequestCriteriaPanel;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.XTandemCriteriaPanel;
import cn.ac.dicp.gp1809.proteome.gui2.PeptideListPagedRowGetter2.PeptideRowReader;
import cn.ac.dicp.gp1809.proteome.gui2.util.SFOERMascotCriteriaPanel;
import javax.swing.GroupLayout.Alignment;

public class CriteriaPanel extends JPanel implements ItemListener, ActionListener {

	private static final long serialVersionUID = 1L;

	private ICriteriaSetter currentCriteriaPanel;

	private JPanel jPanel0;

	private SequestCriteriaPanel sequestCriteriaPanel0;
	private ProbabilityCriteriaPanel probabilityCriteriaPanel0;
	private DeltaMZCriteriaPanel deltaMZCriteriaPanel0;
	private OMSSACriteriaPanel OMSSACriteriaPanel0;
	private MascotCriteriaPanel3 mascotCriteriaPanel0;
	private XTandemCriteriaPanel xtandemCriteriaPanel0;
	private CruxCriteriaPanel cruxCriteriaPanel0;
	private MascotApivaseCriteriaPanel mascotApivaseCriteriaPanel0;
	private SequestApivaseCriteriaPanel sequestApivaseCriteriaPanel0;
	private SFOERSequestCriteriaPanel sFOERSequestCriteriaPanel;
	private SFOERMascotCriteriaPanel sFOERMascotCriteriaPanel;
	private SFOERActionPanel spanel;
	private JCheckBox jCheckBoxDeltaMz;

	private cn.ac.dicp.gp1809.proteome.gui2.SFOERActionPanel sFOERActionPanel1;

	private JLabel jLabel0;

	private JTextField jTextField0;

	private JLabel jLabelPPM;
	
	private PeptideListPagedRowGetter2 getter;

	private JButton jButton0;

	private JButton jButtonRemove;

	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public CriteriaPanel() {
		initComponents();
	}

	public CriteriaPanel(PeptideListPagedRowGetter2 getter) {
		initComponents();
		this.getter = getter;
		this.selectCriteriaPanel(getter.getPeptideType());
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(null, "Filter", TitledBorder.LEADING, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 15), new Color(59, 59,
				59)));
		setSize(645, 331);
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(14)
					.addComponent(getSFOERActionPanel1(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(67)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJCheckBoxDeltaMz(), javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(5)
							.addComponent(getJLabel0())
							.addGap(9)
							.addComponent(getJTextFieldMzFilter(), javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(2)
							.addComponent(getJLabelPPM()))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getJButton0())
							.addGap(24)
							.addComponent(getJButtonRemove()))))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(3)
					.addComponent(getJPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(10)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getSFOERActionPanel1(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(10)
							.addComponent(getJCheckBoxDeltaMz())
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(4)
									.addComponent(getJLabel0()))
								.addComponent(getJTextFieldMzFilter(), javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(4)
									.addComponent(getJLabelPPM())))
							.addGap(12)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(getJButton0())
								.addComponent(getJButtonRemove()))))
					.addGap(27)
					.addComponent(getJPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
		);
		setLayout(groupLayout);
	}

	JButton getJButtonRemove() {
		if (jButtonRemove == null) {
			jButtonRemove = new JButton();
			jButtonRemove.setText("Remove filter");
			jButtonRemove.addActionListener(this);
		}
		return jButtonRemove;
	}

	JButton getJButton0() {
		if (jButton0 == null) {
			jButton0 = new JButton();
			jButton0.setText("Apply filter");
			jButton0.addActionListener(this);
		}
		return jButton0;
	}

	private JLabel getJLabelPPM() {
		if (jLabelPPM == null) {
			jLabelPPM = new JLabel();
			jLabelPPM.setText("PPM");
		}
		return jLabelPPM;
	}

	private JTextField getJTextFieldMzFilter() {
		if (jTextField0 == null) {
			jTextField0 = new JTextField();
			jTextField0.setText("10");
		}
		return jTextField0;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Delta M/Z <");
		}
		return jLabel0;
	}

	private cn.ac.dicp.gp1809.proteome.gui2.SFOERActionPanel getSFOERActionPanel1() {
		if (sFOERActionPanel1 == null) {
			sFOERActionPanel1 = new cn.ac.dicp.gp1809.proteome.gui2.SFOERActionPanel();
			sFOERActionPanel1.setBorder(BorderFactory.createTitledBorder(null, "SFOER", TitledBorder.LEADING, TitledBorder.CENTER, new Font("SansSerif",
					Font.BOLD, 12), new Color(59, 59, 59)));
			sFOERActionPanel1.getJButton0().addActionListener(this);
			sFOERActionPanel1.getJButtonDispose().addActionListener(this);
		}
		return sFOERActionPanel1;
	}

	protected JCheckBox getJCheckBoxDeltaMz() {
		if (jCheckBoxDeltaMz == null) {
			jCheckBoxDeltaMz = new JCheckBox();
			jCheckBoxDeltaMz.setText("Use Delta M/Z filter");
		}
		return jCheckBoxDeltaMz;
	}

	private JPanel getJPanel0() {
		if (jPanel0 == null) {
			jPanel0 = new JPanel();
			jPanel0.setLayout(new GroupLayout());
			jPanel0.add(getSequestCriteriaPanel0(), new Constraints(new Leading(0, 610, 6, 6), new Leading(52, 112, 10, 10)));
			this.currentCriteriaPanel = getSequestCriteriaPanel0();
		}
		return jPanel0;
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
	 * Select the criteria panel for the peptide type
	 * 
	 * @param type
	 */
	public void selectCriteriaPanel(PeptideType type) {

		JPanel panel = null;

		switch (type) {
		case SEQUEST:
			panel = this.getSequestCriteriaPanel0();
			this.sFOERActionPanel1.setEnabled(true);
			break;
		case MASCOT:
			panel = this.getMascotCriteriaPanel0();
			this.sFOERActionPanel1.setEnabled(true);
			break;
		case OMSSA:
			panel = this.getOMSSACriteriaPanel0();
			this.sFOERActionPanel1.setEnabled(false);
			break;
		case CRUX:
			panel = this.getCruxCriteriaPanel0();
			this.sFOERActionPanel1.setEnabled(false);
			break;
		case XTANDEM:
			panel = this.getXTandemCriteriaPanel0();
			this.sFOERActionPanel1.setEnabled(false);
			break;
		case APIVASE_MASCOT:
			panel = this.getmascotApivaseCriteriaPanel0();
			this.sFOERActionPanel1.setEnabled(false);
			break;
		case APIVASE_SEQUEST:
			panel = this.getsequestApivaseCriteriaPanel0();
			this.sFOERActionPanel1.setEnabled(false);
			break;
		default:
			throw new IllegalArgumentException("Unsupportted peptide type: "
			        + type);
		}

		if (this.currentCriteriaPanel != null)
			this.getJPanel0().remove((JPanel) this.currentCriteriaPanel);

		this.getJPanel0().add(
		        panel,
		        new Constraints(new Bilateral(0, 0, 585),
		                new Leading(0, 0, 145)));
		this.updateUI();
		panel.updateUI();

		this.currentCriteriaPanel = (ICriteriaSetter) panel;
	}

	/**
	 * Select the SFOER criteria panel
	 * 
	 * @param xcorrs
	 * @param dcns
	 * @param sps
	 * @param rsps
	 */
	public void selectSequestCriteriaPanel(float[] xcorrs, float[] dcns, float[] sps, short[] rsps) {
		
		SFOERSequestCriteriaPanel panel = this.getSFOERSequestCriteriaPanel0();
		panel.setCriteira(xcorrs, dcns, sps, rsps);
		
		if (this.currentCriteriaPanel != null)
			this.getJPanel0().remove((JPanel) this.currentCriteriaPanel);

		this.getJPanel0().add(
		        panel,
		        new Constraints(new Bilateral(0, 0, 585),
		                new Leading(0, 0, 145)));
		this.updateUI();
		panel.updateUI();

		this.currentCriteriaPanel = panel;
	}
	
	/**
	 * Select the SFOER criteria panel
	 * 
	 * @param xcorrs
	 * @param dcns
	 * @param sps
	 * @param rsps
	 */
	public void selectMascotCriteriaPanel(float[] ionScore, float[] IS_MHT, float[] IS_MIT, double [] evalue) {
		
		SFOERMascotCriteriaPanel panel = this.getSFOERMascotCriteriaPanel0();
		panel.setCriteira(ionScore, IS_MHT, IS_MIT, evalue);
		
		if (this.currentCriteriaPanel != null)
			this.getJPanel0().remove((JPanel) this.currentCriteriaPanel);

		this.getJPanel0().add(
		        panel,
		        new Constraints(new Bilateral(0, 0, 585),
		                new Leading(0, 0, 145)));
		this.updateUI();
		panel.updateUI();

		this.currentCriteriaPanel = panel;
	}

	private SFOERSequestCriteriaPanel getSFOERSequestCriteriaPanel0() {
		if (this.sFOERSequestCriteriaPanel == null) {
			sFOERSequestCriteriaPanel = new SFOERSequestCriteriaPanel();
		}
		return sFOERSequestCriteriaPanel;
	}
	
	private SFOERMascotCriteriaPanel getSFOERMascotCriteriaPanel0() {
		if (this.sFOERMascotCriteriaPanel == null) {
			sFOERMascotCriteriaPanel = new SFOERMascotCriteriaPanel();
		}
		return sFOERMascotCriteriaPanel;
	}
	
	private ProbabilityCriteriaPanel getProbabilityCriteriaPanel0() {
		if (this.probabilityCriteriaPanel0 == null) {
			probabilityCriteriaPanel0 = new ProbabilityCriteriaPanel();
		}
		return probabilityCriteriaPanel0;
	}

	private SequestCriteriaPanel getSequestCriteriaPanel0() {
		if (sequestCriteriaPanel0 == null) {
			sequestCriteriaPanel0 = new SequestCriteriaPanel();
		}
		return sequestCriteriaPanel0;
	}

	private MascotCriteriaPanel3 getMascotCriteriaPanel0() {
		if (mascotCriteriaPanel0 == null) {
			mascotCriteriaPanel0 = new MascotCriteriaPanel3();
			mascotCriteriaPanel0.getJButtonSet().addActionListener(this);
		}
		return mascotCriteriaPanel0;
	}

	private OMSSACriteriaPanel getOMSSACriteriaPanel0() {
		if (this.OMSSACriteriaPanel0 == null) {
			this.OMSSACriteriaPanel0 = new OMSSACriteriaPanel();
		}
		return this.OMSSACriteriaPanel0;
	}

	private XTandemCriteriaPanel getXTandemCriteriaPanel0() {
		if (this.xtandemCriteriaPanel0 == null) {
			this.xtandemCriteriaPanel0 = new XTandemCriteriaPanel();
		}
		return this.xtandemCriteriaPanel0;
	}

	private CruxCriteriaPanel getCruxCriteriaPanel0() {
		if (this.cruxCriteriaPanel0 == null) {
			this.cruxCriteriaPanel0 = new CruxCriteriaPanel();
		}
		return this.cruxCriteriaPanel0;
	}

	private MascotApivaseCriteriaPanel getmascotApivaseCriteriaPanel0() {
		if (this.mascotApivaseCriteriaPanel0 == null) {
			this.mascotApivaseCriteriaPanel0 = new MascotApivaseCriteriaPanel();
		}
		return this.mascotApivaseCriteriaPanel0;
	}

	private SequestApivaseCriteriaPanel getsequestApivaseCriteriaPanel0() {
		if (this.sequestApivaseCriteriaPanel0 == null) {
			this.sequestApivaseCriteriaPanel0 = new SequestApivaseCriteriaPanel();
		}
		return this.sequestApivaseCriteriaPanel0;
	}

	/**
	 * Return the current showing and set criteria in the criteria panel
	 * 
	 * @return
	 */
	protected IPeptideCriteria getCriteria() {
		return this.currentCriteriaPanel.getCriteria();
	}

	protected DefaultDeltaMZCriteria getMzFilter() {
		String text = this.getJTextFieldMzFilter().getText();
		double ppm = Double.parseDouble(text);
		DefaultDeltaMZCriteria mzfilter = new DefaultDeltaMZCriteria(ppm);
		return mzfilter;
	}
	/**
	 * Add the action of the filter button
	 * 
	 * @param listener
	 */
	protected void addFilterActionListener(ActionListener listener) {
		this.getJButton0().addActionListener(listener);
	}

	protected void addMzFilterActionListener(ActionListener listener) {
		this.getJCheckBoxDeltaMz().addActionListener(listener);
	}
	
	public void addSFOERActionListener(ISFOERListener listener) {
		if(listener != null) {
			spanel = new SFOERActionPanel(listener);
			add(spanel, new Constraints(new Leading(371, 10, 10), new Leading(8, 12, 12)));
			
			this.updateUI();
		}
	}

	/**
	 * Add the action of the filter button
	 * 
	 * @param listener
	 */
	protected void removeFilterActionListener(ActionListener listener) {
		this.getJButtonRemove().addActionListener(listener);
	}
	
	/**
	 * @param actionListener
	 */
	public void addSetPvalueActionListener(ActionListener actionListener) {
		// TODO Auto-generated method stub
		if(this.getJButtonSetPvalue()!=null)
			this.getJButtonSetPvalue().addActionListener(actionListener);
	}
	
	JButton getJButtonSetPvalue(){
		if(mascotCriteriaPanel0!=null)
			return this.mascotCriteriaPanel0.getJButtonSet();
		
		return null;
	}
	
	public float getMascotPvalue(){
		return this.getMascotCriteriaPanel0().getPvalue();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getSource();

		if (obj == this.getJCheckBoxDeltaMz()) {
			
			return ;
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		Object obj = e.getSource();

		if (obj == this.getSFOERActionPanel1().getJButton0()) {
			
			float fdr = this.sFOERActionPanel1.getFDR();
			short type = this.sFOERActionPanel1.getOptimizeType();

			if(this.getter.getPeptideType()==PeptideType.SEQUEST){

				try {
					
					SequestPplDataForInput input = new SequestPplDataForInput();

					System.out.println("Optimizing 1+ peptides ...");
					PeptideRowReader reader = this.getter.getSelectedAllPeptideReader();
					float[][] peptides = input.getPeptide(reader, (short)1);
					reader.close();
					
//					Optimizer optimizer = new Optimizer(peptides);
					Optimizer optimizer = new Optimizer(peptides, fdr, false, false, type);
					OptimizedFilter ofiler1 = optimizer.optimize();
					
					System.out.println("Optimizing 2+ peptides ...");
					reader = this.getter.getSelectedAllPeptideReader();
					peptides = input.getPeptide(reader, (short)2);
					reader.close();
					
					optimizer = new Optimizer(peptides, fdr, false, false, type);
					OptimizedFilter ofiler2 = optimizer.optimize();
					
					System.out.println("Optimizing 3+ peptides ...");
					reader = this.getter.getSelectedAllPeptideReader();
					peptides = input.getPeptide(reader, (short)3);
					reader.close();
					
					optimizer = new Optimizer(peptides, fdr, false, false, type);
					OptimizedFilter ofiler3 = optimizer.optimize();
					
					System.out.println("Optimizing >=4+ peptides ...");
					
					reader = this.getter.getSelectedAllPeptideReader();
					peptides = input.getPeptideWithHigherCharge(reader, (short)4);
					reader.close();
					
					optimizer = new Optimizer(peptides, fdr, false, false, type);
					OptimizedFilter ofiler4 = optimizer.optimize();
			
					float[] xcorrs = new float[4];
					float[] dcns = new float[4];
					float[] sps = new float[4];
					short[] rsps = new short[4];

					if(ofiler1 != null) {
						
						xcorrs[0] = ofiler1.getXcorr();
						dcns[0] = ofiler1.getDcn();
						sps[0] = ofiler1.getSp();
						rsps[0] = ofiler1.getRsp();

					}
					
					if(ofiler2 != null) {
						xcorrs[1] = ofiler2.getXcorr();
						dcns[1] = ofiler2.getDcn();
						sps[1] = ofiler2.getSp();
						rsps[1] = ofiler2.getRsp();
					}

					
					if(ofiler3 != null) {
						xcorrs[2] = ofiler3.getXcorr();
						dcns[2] = ofiler3.getDcn();
						sps[2] = ofiler3.getSp();
						rsps[2] = ofiler3.getRsp();
					}

					
					if(ofiler4 != null) {
						xcorrs[3] = ofiler4.getXcorr();
						dcns[3] = ofiler4.getDcn();
						sps[3] = ofiler4.getSp();
						rsps[3] = ofiler4.getRsp();
					}

					this.selectSequestCriteriaPanel(xcorrs, dcns, sps, rsps);
					
				}catch(Exception ex) {
					throw new RuntimeException (ex);
				}

			
			}else if(this.getter.getPeptideType()==PeptideType.MASCOT){


				try {
					
					MascotPplDataForInput input = new MascotPplDataForInput();

					System.out.println("Optimizing 1+ peptides ...");
					PeptideRowReader reader = this.getter.getSelectedAllPeptideReader();
					float[][] peptides = input.getPeptide(reader, (short)1);
					reader.close();
					
					MascotOptimizer optimizer = new MascotOptimizer(peptides, fdr, type);
					MascotOptimizedFilter ofiler1 = optimizer.optimize();
					
					System.out.println("Optimizing 2+ peptides ...");
					reader = this.getter.getSelectedAllPeptideReader();
					peptides = input.getPeptide(reader, (short)2);
					reader.close();
					
					optimizer = new MascotOptimizer(peptides, fdr, type);
					MascotOptimizedFilter ofiler2 = optimizer.optimize();
					
					System.out.println("Optimizing 3+ peptides ...");
					reader = this.getter.getSelectedAllPeptideReader();
					peptides = input.getPeptide(reader, (short)3);
					reader.close();
					
					optimizer = new MascotOptimizer(peptides, fdr, type);
					MascotOptimizedFilter ofiler3 = optimizer.optimize();
					
					System.out.println("Optimizing >=4+ peptides ...");
					
					reader = this.getter.getSelectedAllPeptideReader();
					peptides = input.getPeptideWithHigherCharge(reader, (short)4);
					reader.close();
					
					optimizer = new MascotOptimizer(peptides, fdr, type);
					MascotOptimizedFilter ofiler4 = optimizer.optimize();

					float[] ionScore = new float[4];
//					float[] deltaIS = new float[4];
					float[] IS_MHT = new float[4];
					float[] IS_MIT = new float[4];
					double[] evalue = new double[4];

					if(ofiler1 != null) {
						
						ionScore[0] = ofiler1.getIonScore();
//						deltaIS[0] = ofiler1.getDeltaIS();
						IS_MHT[0] = ofiler1.getMht();
						IS_MIT[0] = ofiler1.getMit();
						evalue[0] = ofiler1.getEvalue();
					}
					
					if(ofiler2 != null) {
						ionScore[1] = ofiler2.getIonScore();
//						deltaIS[1] = ofiler2.getDeltaIS();
						IS_MHT[1] = ofiler2.getMht();
						IS_MIT[1] = ofiler2.getMit();
						evalue[1] = ofiler2.getEvalue();
					}

					
					if(ofiler3 != null) {
						ionScore[2] = ofiler3.getIonScore();
//						deltaIS[2] = ofiler3.getDeltaIS();
						IS_MHT[2] = ofiler3.getMht();
						IS_MIT[2] = ofiler3.getMit();
						evalue[2] = ofiler3.getEvalue();
					}

					
					if(ofiler4 != null) {
						ionScore[3] = ofiler4.getIonScore();
//						deltaIS[3] = ofiler4.getDeltaIS();
						IS_MHT[3] = ofiler4.getMht();
						IS_MIT[3] = ofiler4.getMit();
						evalue[3] = ofiler4.getEvalue();
					}

					this.selectMascotCriteriaPanel(ionScore, IS_MHT, IS_MIT, evalue);
					
				}catch(Exception ex) {
					throw new RuntimeException (ex);
				}

			}
			
			return ;
		}

		if (obj == this.getSFOERActionPanel1().getJButtonDispose()) {
			this.selectCriteriaPanel(getter.getPeptideType());
			return ;
		}
	}

	
}
