/* 
 ******************************************************************************
 * File: MainGui.java * * * Created on 05-03-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.phosval;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import cn.ac.dicp.gp1809.proteome.penn.PENNFrm;
import cn.ac.dicp.gp1809.proteome.quant.gui.QStatOutFrame;
import cn.ac.dicp.gp1809.proteome.quant.spcounter.SPCounterMainPanel;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.APIVASEII.gui.APVFrame;
import cn.ac.dicp.gp1809.proteome.APIVASEII.gui.DtaRemoverFrame;
import cn.ac.dicp.gp1809.proteome.IO.proteome.protein.ProteinGroupSimpFrame;
import cn.ac.dicp.gp1809.proteome.gui.BatchPplCreatorFrame;
import cn.ac.dicp.gp1809.proteome.gui.PeptideLoaderFrame;
import cn.ac.dicp.gp1809.proteome.gui.PplMergerFrame;
import cn.ac.dicp.gp1809.proteome.proteometools.dbdecoy.DBDecoy;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.DtaFormatConversionFrame;
import cn.ac.dicp.gp1809.proteome.spectrum.spselector.SpSelectFrame;
import cn.ac.dicp.gp1809.util.fragInfo.FragMainFrame;
import cn.ac.dicp.gp1809.util.gui.AboutDlg;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-03-2009, 18:13:01
 */
public class MainGui extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	private static final String version_info= "[Software Information]\n"+
	"Name\t: ArMone\n"+
	"Version\t:1.01 beta2 (02-20-2011)\n"+
	"Author\t: Xinning Jiang(vext@163.com)\n"+
	"\t\r Kai Cheng(cksakuraever@msn.com)\n"+
	"Contact\t: Prof. Minliang Ye (mingliang@dicp.ac.cn)\n" +
	"\t  Prof. Hanfa Zou (hanfazou@dicp.ac.cn)\n"+
	"Address\t:Dalian Institute of Chemical Physics\n"+
	"\t 457 zhongshan Road, Dalian 116023, China\n"+
	"Homepage\t: http://bioanalysis.dicp.ac.cn/proteomics/software/ArMone.html\n"+
	"Citations\t:\n" +
    "\"1. Jiang, X. N.; Ye, M. L.; Cheng, K.; Zou, H. F., ArMone: A Software " +
      "Suite Specially Designed for Processing and Analysis of Phosphoproteome Data " +
      "J. Proteome Res. 2010, 9, (5), 2743-2751\"\n" +
    "\"2. Jiang, X. N.; Ye, M. L.; Han, G. H.; Zou, H. F., Classification Filtering Strategy " +
      "to Improve the Coverage and Sensitivity of Phosphoproteome Analysis Anal. Chem. 2010, 82, 6168-6175\"\n" +
    "\"3. Jiang, X. N.; Dong, X. L.; Ye, M. L.; Zou, H. F., Instance Based Algorithm for Posterior Probability Calculation " +
      "by Target-Decoy Strategy to Improve Protein Identifications Anal. Chem. 2008, 80, (23), 9326-9335\"\n" +
    "\"4. Jiang, X. N.; Han, G. H.; Feng, S.; Jiang, X. G.; Ye, M. L.; Yao, X. B.; Zou, H. F., Automatic validation of phosphopeptide " +
    "identifications by the MS2/MS3 target-decoy search strategy J. Proteome Res. 2008, 7, (4), 1640-1649\"\n" +
    "\"5. Jiang, X. N.; Jiang, X. G.; Han, G. H.; Ye, M. L.; Zou, H. F., Optimization of filtering criterion for SEQUEST database searching " +
    "to improve proteome coverage in shotgun proteomics Bmc Bioinfor. 2007, 8:323\""+
	
	
	"\n\n[Introduction]\n"+
	"    Even though a few of proteome pipelines have been developed to facilitate the data " +
	"processing of proteome researches, these pipelines commonly neither provide well " +
	"support for the processing of phosphoproteome data set because of the unique features " +
	"for the identification of phosphorylated peptides, such as the poor fragment in MS2 and " +
	"the multiple possible phosphorylation site localizations on a single peptide, nor report " +
	"the phosphoproteome data set with sufficient information. To address these problems, " +
	"we presented a software suite named Phosval for the generation of phosphopeptide identification " +
	"and phosphosite localizations with high reliability and high sensitivity and for the conveniences " +
	"of preparing phosphoproteome data set following the proteome guidelines " +
	"(http://www.mcponline.org/misc/ParisReport_Final.dtl). Easy for use batch-filtering and manual " +
	"validation modules are also provided by Phosval for the further distinguishing of false positive " +
	"identifications in the high confidence data set on the fly. It is a stand-alone application with " +
	"friendly graphic user interface supporting multiple operating systems and multiple database search " +
	"engines. As Phosval is originally developed for the phosphoproteome researches, it is more powerful and " +
	"easy for use while the processing of phosphoproteome data set."+
	
	
	"\n\n[Licence]\n" +
	"All right reserved by Hanfa Zou & Mingliang Ye at Dalian Institute of Chemical Physics in China.\n" +
	"Free for academic and non commercial usage.\n" +
	"Commercial users please contact Prof. Hanfa Zou or Prof. Mingliang Ye and get the licence.";
	
	
	private JMenuItem jMenuItemAbout;
	private JMenu jMenu0;
	private JMenuBar jMenuBar0;
	private JButton jButtonPeakformatConv;
	private JButton jButtonPeaklistRemove;
	private JButton jButtonApv;
	private JButton jButtonManualVal;
	private JButton jButtonSiteStatistic;
	private JButton jButtonPplCreation;
	private JButton jButtonProInfering;
	private JButton jButtonAutoFilter;
	private JButton jButtonDrawer;
	private JLabel jLabel0;
	private JButton jButtonpplmerge;

	private JLabel jLabel1;

	private JMenuItem jMenuItemDbDecoy;

	private JMenu jMenuTools;
	
	private JMenu jMenuQuan;

	private JButton jButtonPENN;

	private JMenuItem JMenuItemSPCounter;

//	private JMenuItem JMenuItemModifList;

	private JMenuItem jMenuItemKinase;

	private JMenuItem jMenuItemSpSelector;

	private JMenuItem jMenuItemLoadQuan;
	
	private MyJFileChooser xmlchooser;
	
//	private MyJFileChooser txtchooser;

	private JMenu jMenuLabelFree;

	private JMenu jMenuLabel;

	private JMenuItem jMenuItemQStat;

	private JMenuItem jMenuItemSimplifier;

	private JMenuItem jMenuItemSeqCal;

	private JMenuItem jMenuItemGlycoQuant;

	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public MainGui() {
		initComponents();
	}

	private void initComponents() {
		setTitle("ArMone 1.01");
		setLayout(new GroupLayout());
		add(getJButtonPeakformatConv(), new Constraints(new Leading(53, 119, 10, 10), new Leading(94, 53, 10, 10)));
		add(getJButtonPeaklistRemove(), new Constraints(new Leading(52, 119, 10, 10), new Leading(242, 53, 10, 10)));
		add(getJButtonPplCreation(), new Constraints(new Leading(174, 119, 10, 10), new Leading(144, 53, 6, 6)));
		add(getJButtonProInfering(), new Constraints(new Leading(305, 10, 10), new Leading(96, 53, 10, 10)));
		add(getJButtonAutoFilter(), new Constraints(new Leading(438, 119, 10, 10), new Leading(73, 53, 10, 10)));
		add(getJButtonDrawer(), new Constraints(new Leading(438, 119, 10, 10), new Leading(215, 53, 10, 10)));
		add(getJButtonSiteStatistic(), new Constraints(new Leading(438, 119, 10, 10), new Leading(286, 53, 10, 10)));
		add(getJButtonpplmerge(), new Constraints(new Leading(174, 119, 6, 6), new Leading(207, 53, 6, 6)));
		add(getJLabel0(), new Constraints(new Leading(442, 181, 10, 10), new Leading(12, 43, 6, 6)));
		add(getJButtonApv(), new Constraints(new Leading(305, 119, 6, 6), new Leading(173, 53, 10, 10)));
		add(getJButtonPENN(), new Constraints(new Leading(305, 119, 6, 6), new Leading(251, 53, 10, 10)));
		add(getJButtonManualVal(), new Constraints(new Leading(438, 119, 6, 6), new Leading(142, 53, 6, 6)));
		add(getJLabel1(), new Constraints(new Leading(9, 105, 10, 10), new Leading(341, 35, 10, 10)));
		setJMenuBar(getJMenuBar0());
		setSize(631, 408);
	}

	private JMenuItem getJMenuItemGlycoQuant() {
		if (jMenuItemGlycoQuant == null) {
			jMenuItemGlycoQuant = new JMenuItem();
			jMenuItemGlycoQuant.setText("Load Glyco Quan file");
			jMenuItemGlycoQuant.addActionListener(this);
		}
		return jMenuItemGlycoQuant;
	}

	private JMenuItem getJMenuItemSeqCal() {
		if (jMenuItemSeqCal == null) {
			jMenuItemSeqCal = new JMenuItem();
			jMenuItemSeqCal.setText("SeqCalculator");
			jMenuItemSeqCal.addActionListener(this);
		}
		return jMenuItemSeqCal;
	}

	private JMenuItem getJMenuItemSimplifier() {
    	if (jMenuItemSimplifier == null) {
    		jMenuItemSimplifier = new JMenuItem();
    		jMenuItemSimplifier.setText("ProteinGroupSimplify");
    		jMenuItemSimplifier.addActionListener(this);
    	}
    	return jMenuItemSimplifier;
    }
	
	private JMenuItem getJMenuItemQStat() {
		if (jMenuItemQStat == null) {
			jMenuItemQStat = new JMenuItem();
			jMenuItemQStat.setText("Repeat Quan Result Stat");
			jMenuItemQStat.addActionListener(this);
		}
		return jMenuItemQStat;
	}

	private JMenu getJMenuLabel() {
		if (jMenuLabel == null) {
			jMenuLabel = new JMenu();
			jMenuLabel.setText("ModQuant");
			jMenuLabel.add(getJMenuItemLoadQuan());
			jMenuLabel.add(getJMenuItemGlycoQuant());
			jMenuLabel.add(getJMenuItemQStat());
		}
		return jMenuLabel;
	}

	private JMenu getJMenuLabelFree() {
		if (jMenuLabelFree == null) {
			jMenuLabelFree = new JMenu();
			jMenuLabelFree.setText("Label Free");
			jMenuLabelFree.add(getJMenuItemSPCounter());
		}
		return jMenuLabelFree;
	}

	private MyJFileChooser getXmlFilechooser() {
		if (xmlchooser == null) {
			xmlchooser = new MyJFileChooser();
			xmlchooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			xmlchooser.setFileFilter(new String[] { "pxml" },
			        " Quantitation result file (*.pxml) or Directory");
		}
		return xmlchooser;
	}
	
	private JMenuItem getJMenuItemLoadQuan() {
		if (jMenuItemLoadQuan == null) {
			jMenuItemLoadQuan = new JMenuItem();
			jMenuItemLoadQuan.setText("Load Quan file");
			jMenuItemLoadQuan.addActionListener(this);
		}
		return jMenuItemLoadQuan;
	}

	private JMenuItem getJMenuItemSpSelector() {
		if (jMenuItemSpSelector == null) {
			jMenuItemSpSelector = new JMenuItem();
			jMenuItemSpSelector.setText("SpSelector");
			jMenuItemSpSelector.addActionListener(this);
		}
		return jMenuItemSpSelector;
	}

	private JMenuItem getJMenuItemKinase() {
		if (jMenuItemKinase == null) {
			jMenuItemKinase = new JMenuItem();
			jMenuItemKinase.setText("KinaseStat");
			jMenuItemKinase.addActionListener(this);
		}
		return jMenuItemKinase;
	}

	/*	private JMenuItem getJMenuItemModifList() {
		if (JMenuItemModifList == null) {
			JMenuItemModifList = new JMenuItem();
			JMenuItemModifList.setText("UserModifList");
			JMenuItemModifList.addActionListener(this);
			
		}
		return JMenuItemModifList;
	}
*/
	private JMenuItem getJMenuItemSPCounter() {
		if (JMenuItemSPCounter == null) {
			JMenuItemSPCounter = new JMenuItem();
			JMenuItemSPCounter.setText("SPCounter");
			JMenuItemSPCounter.addActionListener(this);
		}
		return JMenuItemSPCounter;
	}

	private JButton getJButtonPENN() {
    	if (jButtonPENN == null) {
    		jButtonPENN = new JButton();
    		jButtonPENN.setFont(new Font("SansSerif", Font.PLAIN, 11));
    		jButtonPENN.setText("<html><p color=\"#0099FF\" align=\"center\"><b>Peptide probability (PENN)</b></p></html>");
    		jButtonPENN.setAutoscrolls(true);
    		jButtonPENN.addActionListener(this);
    	}
    	return jButtonPENN;
    }

	private JMenu getJMenuTools() {
		if (jMenuTools == null) {
			jMenuTools = new JMenu();
			jMenuTools.setText("Tools");
			jMenuTools.add(getJMenuItemDbDecoy());
			jMenuTools.add(getJMenuItemKinase());
			jMenuTools.add(getJMenuItemSpSelector());
			jMenuTools.add(getJMenuItemSimplifier());
			jMenuTools.add(getJMenuItemSeqCal());
		}
		return jMenuTools;
	}

	private JMenuItem getJMenuItemDbDecoy() {
    	if (jMenuItemDbDecoy == null) {
    		jMenuItemDbDecoy = new JMenuItem();
    		jMenuItemDbDecoy.setText("DBDecoy");
    		jMenuItemDbDecoy.addActionListener(this);
    	}
    	return jMenuItemDbDecoy;
    }

	private JLabel getJLabel1() {
    	if (jLabel1 == null) {
    		jLabel1 = new JLabel();
//    		jLabel1.setIcon(new ImageIcon(getClass().getResource("/resources/beta_2.png")));
    	}
    	return jLabel1;
    }

	private JButton getJButtonpplmerge(){
		if(jButtonpplmerge==null){
			jButtonpplmerge = new JButton();
			jButtonpplmerge.setFont(new Font("SansSerif", Font.PLAIN, 11));
			jButtonpplmerge.setText("<html><p color=\"#0099FF\" align=\"center\"><b>Peptide list file merge</b></p></html>");
			jButtonpplmerge.setAutoscrolls(true);
			jButtonpplmerge.addActionListener(this);
		}
		return jButtonpplmerge;
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setFont(new Font("SansSerif", Font.PLAIN, 30));
//    		jLabel0.setIcon(new ImageIcon(getClass().getResource("/resources/ArMone_1.png")));
    	}
    	return jLabel0;
    }

	private JButton getJButtonDrawer(){
		if(jButtonDrawer==null){
			jButtonDrawer = new JButton();
			jButtonDrawer.setFont(new Font("SansSerif", Font.PLAIN, 11));
			jButtonDrawer.setText("<html><p color=\"#0099FF\" align=\"center\"><b>Batch spectra drawer</b></p></html>");
			jButtonDrawer.setAutoscrolls(true);
			jButtonDrawer.addActionListener(this);
		}
		return jButtonDrawer;
    }

	private JButton getJButtonAutoFilter(){
		if(jButtonAutoFilter==null){
			jButtonAutoFilter = new JButton();
			jButtonAutoFilter.setFont(new Font("SansSerif", Font.PLAIN, 11));
			jButtonAutoFilter.setText("<html><p color=\"#0099FF\" align=\"center\"><b>Automatic filtering</b></p></html>");
			jButtonAutoFilter.setAutoscrolls(true);
			jButtonAutoFilter.addActionListener(this);
		}
    return jButtonAutoFilter;
    }

	private JButton getJButtonProInfering(){
		if(jButtonProInfering==null){
			jButtonProInfering = new JButton();
			jButtonProInfering.setFont(new Font("SansSerif", Font.PLAIN, 11));
			jButtonProInfering.setText("<html><p color=\"#0099FF\" align=\"center\"><b>Protein Inferring</b></p></html>");
			jButtonProInfering.setAutoscrolls(true);
			jButtonProInfering.addActionListener(this);
		}
    return jButtonProInfering;
    }

	private JButton getJButtonPplCreation() {
    	if (jButtonPplCreation == null) {
    		jButtonPplCreation = new JButton();
    		jButtonPplCreation.setFont(new Font("SansSerif", Font.PLAIN, 11));
    		jButtonPplCreation.setText("<html><p color=\"#0099FF\" align=\"center\"><b>Peptide list file creation</b></p></html>");
    		jButtonPplCreation.setAutoscrolls(true);
    		jButtonPplCreation.addActionListener(this);
    	}
    	return jButtonPplCreation;
    }

	private JButton getJButtonSiteStatistic() {
    	if (jButtonSiteStatistic == null) {
    		jButtonSiteStatistic = new JButton();
    		jButtonSiteStatistic.setFont(new Font("SansSerif", Font.PLAIN, 11));
    		jButtonSiteStatistic.setText("<html><p color=\"#0099FF\" align=\"center\"><b>Phospho site statistic</b></p></html>");
    		jButtonSiteStatistic.setAutoscrolls(true);
    		jButtonSiteStatistic.addActionListener(this);
    	}
    	return jButtonSiteStatistic;
    }

	private JButton getJButtonManualVal(){
		if(jButtonManualVal==null){
			jButtonManualVal = new JButton();
			jButtonManualVal.setFont(new Font("SansSerif", Font.PLAIN, 11));
			jButtonManualVal.setText("<html><p color=\"#0099FF\" align=\"center\"><b>Glyco Peptide Analysis</b></p></html>");
			jButtonManualVal.setAutoscrolls(true);
			jButtonManualVal.addActionListener(this);
		}
    return jButtonManualVal;
    }

	private JButton getJButtonApv() {
    	if (jButtonApv == null) {
    		jButtonApv = new JButton();
    		jButtonApv.setFont(new Font("SansSerif", Font.PLAIN, 11));
    		jButtonApv.setText("<html><p color=\"#0099FF\" align=\"center\"><b>MS2/MS3 strategy (APIVASE)</b></p></html>");
    		jButtonApv.setAutoscrolls(true);
    		jButtonApv.addActionListener(this);
    	}
    	return jButtonApv;
    }

	private JButton getJButtonPeaklistRemove() {
    	if (jButtonPeaklistRemove == null) {
    		jButtonPeaklistRemove = new JButton();
    		jButtonPeaklistRemove.setFont(new Font("SansSerif", Font.PLAIN, 11));
    		jButtonPeaklistRemove.setText("<html><p color=\"#0099FF\" align=\"center\"><b>MS2/MS3 DTA preprocess</b></p></html>");
    		jButtonPeaklistRemove.setAutoscrolls(true);
    		jButtonPeaklistRemove.addActionListener(this);
    	}
    	return jButtonPeaklistRemove;
    }

	private JButton getJButtonPeakformatConv(){
		if(jButtonPeakformatConv==null){
			jButtonPeakformatConv = new JButton();
			jButtonPeakformatConv.setFont(new Font("SansSerif", Font.PLAIN, 11));
			jButtonPeakformatConv.setText("<html><p color=\"#0099FF\" align=\"center\"><b>Peak list format conversion</b></p></html>");
			jButtonPeakformatConv.setAutoscrolls(true);
			jButtonPeakformatConv.addActionListener(this);
		}
    return jButtonPeakformatConv;
    }

	private JMenuBar getJMenuBar0() {
		if (jMenuBar0 == null) {
			jMenuBar0 = new JMenuBar();
			jMenuBar0.add(getJMenuTools());
			jMenuBar0.add(getJMenuQuan());
			jMenuBar0.add(getJMenu0());
		}
		return jMenuBar0;
	}

	private JMenu getJMenuQuan() {
		if (jMenuQuan == null) {
			jMenuQuan = new JMenu();
			jMenuQuan.setText("Quantitation");
			jMenuQuan.add(getJMenuLabel());
			jMenuQuan.add(getJMenuLabelFree());
		}
		return jMenuQuan;
	}

	private JMenu getJMenu0() {
    	if (jMenu0 == null) {
    		jMenu0 = new JMenu();
    		jMenu0.setText("Help");
    		jMenu0.add(getJMenuItemAbout());
    	}
    	return jMenu0;
    }

	private JMenuItem getJMenuItemAbout() {
    	if (jMenuItemAbout == null) {
    		jMenuItemAbout = new JMenuItem();
    		jMenuItemAbout.setText("About ArMone");
    		jMenuItemAbout.addActionListener(this);
    	}
    	return jMenuItemAbout;
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

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
    public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if(obj == this.getJButtonApv()) {
			JFrame frame = new APVFrame();
			frame.getContentPane().setPreferredSize(frame.getSize());
			frame.pack();
			frame.setLocationRelativeTo(this);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
			return ;
		}
		
		if(obj == this.getJButtonPplCreation()) {
			JFrame frame = new BatchPplCreatorFrame();
			frame.getContentPane().setPreferredSize(frame.getSize());
			frame.pack();
			frame.setLocationRelativeTo(this);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
			return ;
		}
		
		if(obj == this.getJButtonPeakformatConv()) {
			JFrame frame = new DtaFormatConversionFrame();
			frame.pack();
			frame.setLocationRelativeTo(this);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
			return ;
		}
		
		if(obj == this.getJButtonPeaklistRemove()) {
			JFrame frame = new DtaRemoverFrame();
			frame.getContentPane().setPreferredSize(frame.getSize());
			frame.pack();
			frame.setLocationRelativeTo(this);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
			return ;
		}
		
		if(obj == this.getJButtonpplmerge()) {
			JFrame frame = new PplMergerFrame();
			frame.setLocationRelativeTo(this);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
			return ;
		}
		
		if(obj == this.getJButtonProInfering()) {
			JFrame frame = new PeptideLoaderFrame(this);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
			return ;
		}
		
		if(obj == this.getJButtonPENN()) {
			JFrame frame = new PENNFrm();
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setLocationRelativeTo(this);
			frame.setVisible(true);
			return ;
		}
		
		
		if(obj == this.getJButtonAutoFilter()) {
			JFrame frame = new PeptideLoaderFrame(this);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
			return ;
		}
		
		if(obj == this.getJButtonManualVal()) {
			JFrame frame = new PeptideLoaderFrame(this, 2);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
			return ;
		}
		
		if(obj == this.getJButtonSiteStatistic()) {
			JFrame frame = new PeptideLoaderFrame(this, 1);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
			return ;
		}
		
		if(obj == this.getJButtonDrawer()) {
			JFrame frame = new PeptideLoaderFrame(this);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
			return ;
		}
		
		if(obj == this.getJMenuItemAbout()) {
			
			AboutDlg dlg = new AboutDlg(this);
			dlg.setAboutInformation(version_info);
			dlg.setVisible(true);
			
			return ;
		}
		
		if(obj == this.getJMenuItemDbDecoy()) {
			DBDecoy dbdecoy = new DBDecoy();
			dbdecoy.setLocationRelativeTo(this);
			dbdecoy.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			dbdecoy.setVisible(true);
			return ;
		}
		
		if(obj == this.getJMenuItemSPCounter()){
			SPCounterMainPanel spCounter = new SPCounterMainPanel();
			spCounter.setLocationRelativeTo(this);
			spCounter.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			spCounter.setVisible(true);
			return ;
		}
		
		if(obj == this.getJMenuItemKinase()){
			KinaseSitesPanel kinase = new KinaseSitesPanel();
			kinase.setLocationRelativeTo(this);
			kinase.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			kinase.setVisible(true);
			return ;
		}
		
		if(obj == this.getJMenuItemSpSelector()){
			SpSelectFrame spframe = new SpSelectFrame();
			spframe.setLocationRelativeTo(this);
			spframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			spframe.setVisible(true);
			return ;
		}
		
		if(obj == this.getJMenuItemLoadQuan()){
			String file = null;
			int value = this.getXmlFilechooser().showOpenDialog(this);
			if (value == JFileChooser.APPROVE_OPTION)
				file = this.getXmlFilechooser().getSelectedFile().getAbsolutePath();
			
//			LPairLoader loader;

			// If catch org.dom4j.DocumentException, the GUI will disappear....
//			try {
//				loader = new LPairLoader(file);
//				loader.load(this);

//			} catch (IOException e2) {
				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			}
			
			return ;
		}
	
		if(obj == this.getJMenuItemQStat()){
			QStatOutFrame qsFrame = new QStatOutFrame();
			qsFrame.setLocationRelativeTo(this);
			qsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			qsFrame.setVisible(true);
			return ;
		}
		
		if(obj == this.getJMenuItemSimplifier()) {
			ProteinGroupSimpFrame frame = new ProteinGroupSimpFrame();
			frame.setLocationRelativeTo(this);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
			return ;
		}

		if(obj == this.getJMenuItemSeqCal()){
			JFrame frame = new FragMainFrame();
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
			frame.setLocationRelativeTo(null);
			return ;
		}
		
	/*	if(obj == this.getJMenuItemModifList()) {
			ModifListPanel modifFrame = null;
			try {
				modifFrame = new ModifListPanel();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ModsReadingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (XMLStreamException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			modifFrame.setLocationRelativeTo(this);
			modifFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			modifFrame.setVisible(true);
			return ;
		}
	*/	
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
				MainGui frame = new MainGui();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
