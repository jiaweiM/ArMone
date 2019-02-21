/* 
 ******************************************************************************
 * File: PhosStatisticPanel.java * * * Created on 05-04-2009
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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.xml.stream.XMLStreamException;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IStatisticTask;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.Kinase;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.KinaseSiteStatisTask;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.KinaseXMLReader;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.PhosSiteStatisticTask;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.BioException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.aasequence.SequenceGenerationException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.proteome.gui.PeptideListPagedRowGettor;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-04-2009, 20:12:08
 */
public class PhosStatisticPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private final PeptideListPagedRowGettor getter;
	private MyJFileChooser dbchooser;
	private MyJFileChooser output;
	
	private IStatisticTask task;
	private String kinaseFile = "PhosphoMotif/PhosphoMotif.xml";

	private JButton jButtonSelectDb;
	private JTextField jTextFieldDatabase;
	private JPanel jPanel0;
	private JButton jButtonGlobalInfo;
	private JButton jButtonExport;
	private JPanel jPanel1;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JFormattedTextField jFormattedTextFieldMS2Sym;
	private JFormattedTextField jFormattedTextFieldMS3Sym;
	private JPanel jPanel2;
	private JTextField jTextFieldSites;
	private JLabel jLabel2;
	private JTextField jTextFieldSSites;
	private JLabel jLabel3;
	private JTextField jTextFieldTSites;
	private JLabel jLabel4;
	private JTextField jTextFieldYSites;
	private JLabel jLabel5;
	private JTextField jTextFieldPeps;
	private JLabel jLabel6;
	private JTextField jTextFieldSinglePeps;
	private JTextField jTextFieldDoublePeps;
	private JLabel jLabel8;
	private JTextField jTextFieldTriplePeps;
	private JLabel jLabel9;
	private JLabel jLabel7;
	private JTextField jTextFieldOtherPeps;
	private JCheckBox kinaseStatBox;
	private JPanel kinaInfoPanel;
	private JLabel jLabel11;
	private JTextField jTextFieldPepProp;
	private JLabel jLabel10;
	private JTextField jTextFieldMoDes;
	private JLabel jLabel12;
	private JTextField jTextFieldSitePro;
	private JRadioButton jRadioButtonPhosPep;
	private JRadioButton jRadioButtonNoPhos;
	private ButtonGroup jPhosButtonGroup;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PhosStatisticPanel() {
		this(null);
	}

	public PhosStatisticPanel(PeptideListPagedRowGettor getter) {
		this.getter = getter;
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJPanel1(), new Constraints(new Bilateral(6, 6, 249), new Trailing(7, 28, 10, 10)));
		add(getJPanel0(), new Constraints(new Bilateral(10, 11, 81), new Leading(8, 10, 10)));
		add(getKinaseStatBox(), new Constraints(new Leading(27, 10, 10), new Leading(195, 10, 10)));
		add(getJPanel2(), new Constraints(new Bilateral(10, 11, 511), new Leading(313, 80, 10, 10)));
		add(getJButtonGlobalInfo(), new Constraints(new Leading(91, 10, 10), new Leading(399, 47, 47)));
		add(getJButtonExport(), new Constraints(new Leading(303, 10, 10), new Leading(399, 19, 47)));
		add(getKinaInfoPanel(), new Constraints(new Leading(130, 381, 6, 6), new Leading(189, 116, 10, 10)));
		add(getJRadioButtonPhosPep(), new Constraints(new Leading(27, 6, 6), new Leading(238, 10, 10)));
		add(getJRadioButtonNoPhos(), new Constraints(new Leading(27, 6, 6), new Leading(277, 19, 47)));
		getPhosButtonGroup();
		setSize(535, 476);
	}

	private ButtonGroup getPhosButtonGroup(){
		if (jPhosButtonGroup == null) {
			jPhosButtonGroup = new ButtonGroup();
			jPhosButtonGroup.add(jRadioButtonNoPhos);
			jPhosButtonGroup.add(jRadioButtonPhosPep);
		}
		return jPhosButtonGroup;
	}
	
	private JRadioButton getJRadioButtonNoPhos() {
		if (jRadioButtonNoPhos == null) {
			jRadioButtonNoPhos = new JRadioButton();
			jRadioButtonNoPhos.setText("No Phos");
		}
		return jRadioButtonNoPhos;
	}

	private JRadioButton getJRadioButtonPhosPep() {
		if (jRadioButtonPhosPep == null) {
			jRadioButtonPhosPep = new JRadioButton();
			jRadioButtonPhosPep.setSelected(true);
			jRadioButtonPhosPep.setText("Phos Peptide");
		}
		return jRadioButtonPhosPep;
	}

	private JTextField getJTextFieldSitePro() {
		if (jTextFieldSitePro == null) {
			jTextFieldSitePro = new JTextField();
			jTextFieldSitePro.setEditable(false);
		}
		return jTextFieldSitePro;
	}

	private JLabel getJLabel12() {
		if (jLabel12 == null) {
			jLabel12 = new JLabel();
			jLabel12.setText("Sites Proportion");
		}
		return jLabel12;
	}

	private JTextField getJTextFieldMoDes() {
		if (jTextFieldMoDes == null) {
			jTextFieldMoDes = new JTextField();
		}
		return jTextFieldMoDes;
	}

	private JLabel getJLabel10() {
		if (jLabel10 == null) {
			jLabel10 = new JLabel();
			jLabel10.setText("Motif Description");
		}
		return jLabel10;
	}

	private JTextField getJTextFieldPepProp() {
		if (jTextFieldPepProp == null) {
			jTextFieldPepProp = new JTextField();
			jTextFieldPepProp.setEditable(false);
		}
		return jTextFieldPepProp;
	}

	private JLabel getJLabel11() {
		if (jLabel11 == null) {
			jLabel11 = new JLabel();
			jLabel11.setText("Peptide Proportion");
		}
		return jLabel11;
	}

	private JPanel getKinaInfoPanel() {
		if (kinaInfoPanel == null) {
			kinaInfoPanel = new JPanel();
			kinaInfoPanel.setBorder(BorderFactory.createTitledBorder(null, "Kinase Infomation", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif",
					Font.BOLD, 12), new Color(59, 59, 59)));
			kinaInfoPanel.setLayout(new GroupLayout());
			kinaInfoPanel.add(getJLabel10(), new Constraints(new Leading(4, 10, 10), new Leading(6, 42, 42)));
			kinaInfoPanel.add(getJLabel11(), new Constraints(new Leading(4, 6, 6), new Leading(44, 10, 10)));
			kinaInfoPanel.add(getJTextFieldPepProp(), new Constraints(new Leading(114, 68, 6, 6), new Leading(38, 6, 6)));
			kinaInfoPanel.add(getJLabel12(), new Constraints(new Leading(190, 10, 10), new Leading(44, 6, 6)));
			kinaInfoPanel.add(getJTextFieldMoDes(), new Constraints(new Leading(113, 242, 6, 6), new Leading(0, 6, 6)));
			kinaInfoPanel.add(getJTextFieldSitePro(), new Constraints(new Bilateral(284, 6, 12), new Leading(36, 6, 6)));
			kinaInfoPanel.setEnabled(false);
		}
		return kinaInfoPanel;
	}

	private JCheckBox getKinaseStatBox() {
		if (kinaseStatBox == null) {
			kinaseStatBox = new JCheckBox();
			kinaseStatBox.setText("Kinase Stat");
			kinaseStatBox.addActionListener(this);
		}
		return kinaseStatBox;
	}

	private JTextField getJTextFieldOtherPeps() {
		if (jTextFieldOtherPeps == null) {
			jTextFieldOtherPeps = new JTextField();
			jTextFieldOtherPeps.setEditable(false);
		}
		return jTextFieldOtherPeps;
	}

	private JLabel getJLabel7() {
		if (jLabel7 == null) {
			jLabel7 = new JLabel();
			jLabel7.setText("Double phosphopeptides");
		}
		return jLabel7;
	}

	private JLabel getJLabel9() {
		if (jLabel9 == null) {
			jLabel9 = new JLabel();
			jLabel9.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabel9.setText("Others");
		}
		return jLabel9;
	}

	private JTextField getJTextFieldTriplePeps() {
		if (jTextFieldTriplePeps == null) {
			jTextFieldTriplePeps = new JTextField();
			jTextFieldTriplePeps.setEditable(false);
		}
		return jTextFieldTriplePeps;
	}

	private JLabel getJLabel8() {
		if (jLabel8 == null) {
			jLabel8 = new JLabel();
			jLabel8.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabel8.setText("Triple phosphopeptides");
		}
		return jLabel8;
	}

	private JTextField getJTextFieldDoublePeps() {
		if (jTextFieldDoublePeps == null) {
			jTextFieldDoublePeps = new JTextField();
			jTextFieldDoublePeps.setEditable(false);
		}
		return jTextFieldDoublePeps;
	}

	private JTextField getJTextFieldSinglePeps() {
		if (jTextFieldSinglePeps == null) {
			jTextFieldSinglePeps = new JTextField();
			jTextFieldSinglePeps.setEditable(false);
		}
		return jTextFieldSinglePeps;
	}

	private JLabel getJLabel6() {
		if (jLabel6 == null) {
			jLabel6 = new JLabel();
			jLabel6.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabel6.setText("Single phosphorylated peptides");
		}
		return jLabel6;
	}

	private JTextField getJTextFieldPeps() {
		if (jTextFieldPeps == null) {
			jTextFieldPeps = new JTextField();
			jTextFieldPeps.setEditable(false);
		}
		return jTextFieldPeps;
	}

	private JLabel getJLabel5() {
		if (jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText("Number of distinct phosphopeptides");
		}
		return jLabel5;
	}

	private JTextField getJTextFieldYSites() {
		if (jTextFieldYSites == null) {
			jTextFieldYSites = new JTextField();
			jTextFieldYSites.setEditable(false);
		}
		return jTextFieldYSites;
	}

	private JLabel getJLabel4() {
		if (jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText(", Y");
		}
		return jLabel4;
	}

	private JTextField getJTextFieldTSites() {
		if (jTextFieldTSites == null) {
			jTextFieldTSites = new JTextField();
			jTextFieldTSites.setEditable(false);
		}
		return jTextFieldTSites;
	}

	private JLabel getJLabel3() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText(", T");
		}
		return jLabel3;
	}

	private JTextField getJTextFieldSSites() {
		if (jTextFieldSSites == null) {
			jTextFieldSSites = new JTextField();
			jTextFieldSSites.setEditable(false);
		}
		return jTextFieldSSites;
	}

	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText(": S");
		}
		return jLabel2;
	}

	private JTextField getJTextFieldSites() {
		if (jTextFieldSites == null) {
			jTextFieldSites = new JTextField();
			jTextFieldSites.setEditable(false);
		}
		return jTextFieldSites;
	}

	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(new GroupLayout());
			jPanel2.add(getJLabel1(), new Constraints(new Leading(6, 6, 6), new Leading(48, 6, 6)));
			jPanel2.add(getJFormattedTextFieldMS2Sym(), new Constraints(new Leading(305, 28, 6, 6), new Leading(45, 24, 6, 6)));
			jPanel2.add(getJFormattedTextFieldMS3Sym(), new Constraints(new Leading(345, 28, 6, 6), new Leading(45, 24, 6, 6)));
			jPanel2.add(getJButtonSelectDb(), new Constraints(new Leading(6, 6, 6), new Leading(8, 22, 6, 6)));
			jPanel2.add(getJTextFieldDatabase(), new Constraints(new Leading(133, 372, 6, 6), new Leading(8, 28, 6, 6)));
		}
		return jPanel2;
	}

	private JFormattedTextField getJFormattedTextFieldMS3Sym() {
    	if (jFormattedTextFieldMS3Sym == null) {
    		jFormattedTextFieldMS3Sym = new JFormattedTextField();
    		jFormattedTextFieldMS3Sym.setHorizontalAlignment(SwingConstants.CENTER);
    		jFormattedTextFieldMS3Sym.setText("n");
    	}
    	return jFormattedTextFieldMS3Sym;
    }

	private JFormattedTextField getJFormattedTextFieldMS2Sym() {
    	if (jFormattedTextFieldMS2Sym == null) {
    		jFormattedTextFieldMS2Sym = new JFormattedTextField();
    		jFormattedTextFieldMS2Sym.setHorizontalAlignment(SwingConstants.CENTER);
    		jFormattedTextFieldMS2Sym.setText("p");
    	}
    	return jFormattedTextFieldMS2Sym;
    }

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1
			        .setText("Set the phosphorylation symbol in peptide sequence");
		}
		return jLabel1;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Number of distinct phosphorylation sites");
		}
		return jLabel0;
	}

	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jPanel1.add(getJButtonGlobalInfo());
			jPanel1.add(getJButtonExport());
		}
		return jPanel1;
	}

	private JButton getJButtonExport() {
		if (jButtonExport == null) {
			jButtonExport = new JButton();
			jButtonExport.setText("    Export the site details    ");
			jButtonExport.addActionListener(this);
		}
		return jButtonExport;
	}

	private JButton getJButtonGlobalInfo() {
		if (jButtonGlobalInfo == null) {
			jButtonGlobalInfo = new JButton();
			jButtonGlobalInfo.setText("Show global site information");
			jButtonGlobalInfo.addActionListener(this);
		}
		return jButtonGlobalInfo;
	}

	private JPanel getJPanel0() {
		if (jPanel0 == null) {
			jPanel0 = new JPanel();
			jPanel0.setBorder(BorderFactory
			        .createTitledBorder(null, "Site info",
			                TitledBorder.LEADING, TitledBorder.ABOVE_TOP,
			                new Font("SansSerif", Font.BOLD, 12), new Color(59,
			                        59, 59)));
			jPanel0.setLayout(new GroupLayout());
			jPanel0.add(getJLabel0(), new Constraints(new Leading(0, 6, 6),
			        new Leading(0, 6, 6)));
			jPanel0.add(getJTextFieldSites(), new Constraints(new Leading(227,
			        46, 10, 10), new Leading(-3, 24, 6, 6)));
			jPanel0.add(getJLabel2(), new Constraints(new Leading(281, 10, 10),
			        new Leading(0, 6, 6)));
			jPanel0.add(getJTextFieldSSites(), new Constraints(new Leading(301,
			        46, 6, 6), new Leading(-3, 24, 6, 6)));
			jPanel0.add(getJLabel3(), new Constraints(new Leading(353, 6, 6),
			        new Leading(0, 6, 6)));
			jPanel0.add(getJTextFieldTSites(), new Constraints(new Leading(367,
			        46, 10, 10), new Leading(-3, 24, 6, 6)));
			jPanel0.add(getJLabel4(), new Constraints(new Leading(417, 6, 6),
			        new Leading(0, 6, 6)));
			jPanel0.add(getJTextFieldYSites(), new Constraints(new Leading(436,
			        46, 6, 6), new Leading(-3, 24, 6, 6)));
			jPanel0.add(getJLabel5(), new Constraints(
			        new Leading(0, 204, 6, 6), new Leading(30, 6, 6)));
			jPanel0.add(getJTextFieldPeps(), new Constraints(new Leading(213,
			        46, 10, 10), new Leading(27, 24, 6, 6)));
			jPanel0.add(getJTextFieldSinglePeps(), new Constraints(new Leading(
			        212, 46, 6, 6), new Leading(57, 24, 6, 6)));
			jPanel0.add(getJTextFieldDoublePeps(), new Constraints(new Leading(
			        429, 46, 10, 10), new Leading(60, 24, 6, 6)));
			jPanel0.add(getJLabel6(), new Constraints(
			        new Leading(24, 176, 6, 6), new Leading(60, 6, 6)));
			jPanel0.add(getJLabel8(), new Constraints(
			        new Leading(26, 172, 6, 6), new Leading(87, 6, 6)));
			jPanel0.add(getJTextFieldTriplePeps(), new Constraints(new Leading(
			        213, 46, 6, 6), new Leading(84, 24, 6, 6)));
			jPanel0.add(getJLabel9(), new Constraints(new Leading(281, 140, 10,
			        10), new Leading(90, 6, 6)));
			jPanel0.add(getJLabel7(), new Constraints(new Leading(281, 6, 6),
			        new Leading(63, 6, 6)));
			jPanel0.add(getJTextFieldOtherPeps(), new Constraints(new Leading(
			        429, 46, 6, 6), new Leading(87, 24, 6, 6)));
		}
		return jPanel0;
	}

	private JTextField getJTextFieldDatabase() {
		if (jTextFieldDatabase == null) {
			jTextFieldDatabase = new JTextField();
		}
		return jTextFieldDatabase;
	}

	private JButton getJButtonSelectDb() {
		if (jButtonSelectDb == null) {
			jButtonSelectDb = new JButton();
			jButtonSelectDb.setText("Set the database");
			jButtonSelectDb.addActionListener(this);
		}
		return jButtonSelectDb;
	}

	/**
	 * The phosphorylation symbol
	 * 
	 * @return
	 */
	private char[] getPhosSymbols() {

		String s1 = this.getJFormattedTextFieldMS2Sym().getText();
		String s2 = this.getJFormattedTextFieldMS3Sym().getText();

		if (s1.length() == 0 && s2.length() == 0) {
			throw new NullPointerException(
			        "Set the symbol of phosphorylation first");
		}

		if (s1.length() == 0) {
			return new char[] { s2.charAt(0) };
		}

		if (s2.length() == 0)
			return new char[] { s1.charAt(0) };

		return new char[] { s1.charAt(0), s2.charAt(0) };
	}

	/**
	 * @return the dbchooser
	 */
	private MyJFileChooser getDbchooser() {
		if (this.dbchooser == null) {
			this.dbchooser = new MyJFileChooser();
			this.dbchooser.setFileFilter(new String[] { "fasta" },
			        "Fasta database (*.fasta)");
		}
		return dbchooser;
	}
	
	/**
	 * @return the dbchooser
	 */
	private MyJFileChooser getOutputChooser() {
		if (this.output == null) {
			this.output = new MyJFileChooser();
			this.output.setFileFilter(new String[] { "csv" },
			        "csv (*.csv)");
		}
		return output;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();

		try {
			
			if(obj == this.getKinaseStatBox()){
				if(kinaseStatBox.isSelected()){
					kinaInfoPanel.setEnabled(true);
				}else{
					kinaInfoPanel.setEnabled(false);
				}
			}
			
			if (obj == this.getJButtonSelectDb()) {
				int value = this.getDbchooser().showOpenDialog(this);
				if (value == JFileChooser.APPROVE_OPTION)
					this.getJTextFieldDatabase().setText(
					        this.dbchooser.getSelectedFile().getAbsolutePath());
				return;
			}

			if (obj == this.getJButtonGlobalInfo()) {
				
				if(this.task == null){
					if(kinaseStatBox.isSelected()){
						this.getKinaseStatTask();
						this.getJTextFieldPepProp().setText(task.getKinasePepProp());
						this.getJTextFieldSitePro().setText(task.getKinaseSitesProp());
					}else{
						this.getStatisticTask();
					}
				}
				
				/*
				 * 
				 */
				HashMap<String, Integer> map = task.getSiteMap();

				Integer ssite = map.get("S");
				Integer tsite = map.get("T");
				Integer ysite = map.get("Y");

				int ss = ssite == null ? 0 : ssite;
				int ts = tsite == null ? 0 : tsite;
				int ys = ysite == null ? 0 : ysite;

				this.getJTextFieldSites().setText(String.valueOf(ss + ts + ys));
				this.getJTextFieldSSites().setText(String.valueOf(ss));
				this.getJTextFieldTSites().setText(String.valueOf(ts));
				this.getJTextFieldYSites().setText(String.valueOf(ys));

				int[] nvsc = task.getNumDistinctPepsVsSiteCount();

				int other = 0;

				for (int i = 4; i < nvsc.length; i++) {
					other += nvsc[i];
				}

				this.getJTextFieldPeps().setText(
				        String.valueOf(nvsc[1] + nvsc[2] + nvsc[3] + other));
				this.getJTextFieldSinglePeps().setText(String.valueOf(nvsc[1]));
				this.getJTextFieldDoublePeps().setText(String.valueOf(nvsc[2]));
				this.getJTextFieldTriplePeps().setText(String.valueOf(nvsc[3]));
				this.getJTextFieldOtherPeps().setText(String.valueOf(other));

				return;
			}
			
			
			if(obj == this.getJButtonExport()) {
				int ap = this.getOutputChooser().showSaveDialog(this);
				if(ap == JFileChooser.APPROVE_OPTION) {
					String output = this.getOutputChooser().getSelectedFile().getAbsolutePath();
					
					if(this.task == null)
						this.getStatisticTask();
					
					this.task.printDetails(output);
				}
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex, "Error",
			        JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}

	}

	private void getStatisticTask() throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, BioException,
	        SequenceGenerationException, FastaDataBaseException, IOException,
	        PeptideParsingException {
		
		char symbols[] = this.getPhosSymbols();

		IPeptideListReader reader = this.getter.getSelectedPeptideReader();

		int num = reader.getNumberofPeptides();
		IPeptide[] peps = new IPeptide[num];

		for (int i = 0; i < num; i++) {
			peps[i] = reader.getPeptide();
		}

		String database = this.getJTextFieldDatabase().getText();

		if (database.length() == 0) {
			throw new NullPointerException("Select the database first.");
		}

		PhosSiteStatisticTask task = new PhosSiteStatisticTask(database,
		        symbols, peps, new DefaultDecoyRefJudger());
		task.process();

		this.task = task;
	}

	private void getKinaseStatTask() throws PeptideParsingException, XMLStreamException, ModsReadingException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, FastaDataBaseException, IOException, BioException, SequenceGenerationException{
		
		char symbols[] = this.getPhosSymbols();

		IPeptideListReader reader = this.getter.getSelectedPeptideReader();

		int num = reader.getNumberofPeptides();
		IPeptide[] peps = new IPeptide[num];

		for (int i = 0; i < num; i++) {
			peps[i] = reader.getPeptide();
		}

		String database = this.getJTextFieldDatabase().getText();

		if (database.length() == 0) {
			throw new NullPointerException("Select the database first.");
		}

		String kinaInput = this.getJTextFieldMoDes().getText();
		if (kinaInput.length() == 0) {
			throw new NullPointerException("Input a motif description first.");
		}
		
		KinaseXMLReader kinaseReader = new KinaseXMLReader(kinaseFile, true);
		Kinase kinase = null;
		
		HashMap <String, Kinase> kinaseMap = kinaseReader.getKinaseNameMap();
		
		if(kinaseMap.containsKey(kinaInput)){
			kinase = kinaseMap.get(kinaInput);
		}else{
			int i = 0;
			Kinase k = null;
			Set <String> kinaseSet = kinaseMap.keySet();
			for(String str:kinaseSet){
				if(str.startsWith(kinaInput)){
					i++;
					k = kinaseMap.get(str);
				}
			}
			if(i==1){
				kinase = k;
			}
			else if(i==0){
				throw new NullPointerException("Motif description is not in the list.");
			}
			else if(i>1){
				throw new NullPointerException("More than one kinase are fit for this description.");
			}
		}
		
		boolean phos = this.getJRadioButtonPhosPep().isSelected();
		
		KinaseSiteStatisTask task = new KinaseSiteStatisTask(database, new DefaultDecoyRefJudger(), kinase,
		        peps, symbols, phos);
		task.process();

		this.task = task;
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
				frame.setTitle("PhosStatisticPanel");
				PhosStatisticPanel content = new PhosStatisticPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
