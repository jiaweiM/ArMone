/* 
 ******************************************************************************
 * File: BatchDrawSelectPanel.java * * * Created on 05-26-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.batchdraw;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria;
import cn.ac.dicp.gp1809.proteome.gui.ProteinCriPanel;
import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.IonsTypeSettingPanel;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.NeutralLossSettingPanel;
import cn.ac.dicp.gp1809.proteome.spectrum.gui.SpectrumThresholdSetPanelVer;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-26-2009, 19:58:14
 */
public class BatchDrawSelectPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private MyJFileChooser output;

	private JLabel jLabel0;
	private JTextField jTextField0;
	private JButton jButton0;
	private SpectrumThresholdSetPanelVer spectrumThresholdSetPanel0;
	private NeutralLossSettingPanel neutralLossSettingPanel0;
	private IonsTypeSettingPanel ionsTypeSettingPanel0;
	private JCheckBox jCheckBoxPDF;
	private JCheckBox jCheckBoxHTML;
	private ButtonGroup buttonGroup1;
	private JCheckBox jCheckBoxUniPep;
	private ButtonGroup buttonGroup2;
	private JCheckBox jCheckBoxAllPep;
	private JCheckBox jCheckBoxProFilter;
	private ProteinCriPanel proteinCriPanel0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public BatchDrawSelectPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJCheckBoxPDF(), new Constraints(new Leading(23, 10, 10), new Leading(80, 10, 10)));
		add(getJCheckBoxHTML(), new Constraints(new Leading(123, 10, 10), new Leading(80, 6, 6)));
		add(getJCheckBoxAllPep(), new Constraints(new Leading(348, 10, 10), new Leading(80, 6, 6)));
		add(getJCheckBoxUniPep(), new Constraints(new Leading(223, 10, 10), new Leading(80, 6, 6)));
		add(getSpectrumThresholdSetPanel0(), new Constraints(new Leading(22, 246, 10, 10), new Leading(279, 10, 10)));
		add(getIonsTypeSettingPanel0(), new Constraints(new Leading(310, 156, 10, 10), new Leading(259, 96, 10, 10)));
		add(getJCheckBoxProFilter(), new Constraints(new Leading(23, 6, 6), new Leading(115, 6, 6)));
		add(getProteinCriPanel0(), new Constraints(new Leading(17, 466, 10, 10), new Leading(145, 100, 6, 6)));
		add(getNeutralLossSettingPanel0(), new Constraints(new Leading(493, 6, 6), new Leading(21, 238, 6, 6)));
		add(getJLabel0(), new Constraints(new Leading(19, 10, 10), new Leading(36, 10, 10)));
		add(getJButton0(), new Constraints(new Leading(444, 6, 6), new Leading(32, 30, 10, 10)));
		add(getJTextField0(), new Constraints(new Leading(78, 350, 6, 6), new Leading(32, 6, 6)));
		initButtonGroup1();
		initButtonGroup2();
		setSize(660, 370);
	}

	private ProteinCriPanel getProteinCriPanel0() {
		if (proteinCriPanel0 == null) {
			proteinCriPanel0 = new ProteinCriPanel();
			proteinCriPanel0.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, null, null, null, null));
//			proteinCriPanel0.setEnabled(false);
			proteinCriPanel0.switchOff();
		}
		return proteinCriPanel0;
	}
	
	private JCheckBox getJCheckBoxProFilter() {
		if (jCheckBoxProFilter == null) {
			jCheckBoxProFilter = new JCheckBox();
			jCheckBoxProFilter.setText("Protein Filter");
			jCheckBoxProFilter.addActionListener(this);
		}
		return jCheckBoxProFilter;
	}

	private JCheckBox getJCheckBoxAllPep() {
		if (jCheckBoxAllPep == null) {
			jCheckBoxAllPep = new JCheckBox();
			jCheckBoxAllPep.setText("All Peptide");
		}
		return jCheckBoxAllPep;
	}

	private void initButtonGroup2() {
		buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(getJCheckBoxUniPep());
		buttonGroup2.add(getJCheckBoxAllPep());
	}

	private JCheckBox getJCheckBoxUniPep() {
		if (jCheckBoxUniPep == null) {
			jCheckBoxUniPep = new JCheckBox();
			jCheckBoxUniPep.setSelected(true);
			jCheckBoxUniPep.setText("Unique Peptide");
		}
		return jCheckBoxUniPep;
	}

	private void initButtonGroup1() {
		buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(getJCheckBoxPDF());
		buttonGroup1.add(getJCheckBoxHTML());
	}

	private JCheckBox getJCheckBoxHTML() {
		if (jCheckBoxHTML == null) {
			jCheckBoxHTML = new JCheckBox();
			jCheckBoxHTML.setText("HTML");
		}
		return jCheckBoxHTML;
	}

	private JCheckBox getJCheckBoxPDF() {
		if (jCheckBoxPDF == null) {
			jCheckBoxPDF = new JCheckBox();
			jCheckBoxPDF.setSelected(true);
			jCheckBoxPDF.setText("PDF");
		}
		return jCheckBoxPDF;
	}

	private IonsTypeSettingPanel getIonsTypeSettingPanel0() {
		if (ionsTypeSettingPanel0 == null) {
			ionsTypeSettingPanel0 = new IonsTypeSettingPanel();
			ionsTypeSettingPanel0.setBorder(BorderFactory.createTitledBorder(null, "Type of ions", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("Dialog",
					Font.BOLD, 12), new Color(51, 51, 51)));
		}
		return ionsTypeSettingPanel0;
	}

	private NeutralLossSettingPanel getNeutralLossSettingPanel0() {
    	if (neutralLossSettingPanel0 == null) {
    		neutralLossSettingPanel0 = new NeutralLossSettingPanel();
    		neutralLossSettingPanel0.setBorder(BorderFactory.createTitledBorder(null, "Neutral loss peaks", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font(
    				"Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
    	}
    	return neutralLossSettingPanel0;
    }

	private SpectrumThresholdSetPanelVer getSpectrumThresholdSetPanel0() {
		if (spectrumThresholdSetPanel0 == null) {
			spectrumThresholdSetPanel0 = new SpectrumThresholdSetPanelVer();
			spectrumThresholdSetPanel0.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, null, null));
		}
		return spectrumThresholdSetPanel0;
	}

	private JButton getJButton0() {
    	if (jButton0 == null) {
    		jButton0 = new JButton();
    		jButton0.setText("...");
    		jButton0.addActionListener(this);
    	}
    	return jButton0;
    }

	private JTextField getJTextField0() {
    	if (jTextField0 == null) {
    		jTextField0 = new JTextField();
    	}
    	return jTextField0;
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("Output");
    	}
    	return jLabel0;
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
	 * The output path of the drawing
	 * 
	 * @return
	 */
	public String getOutput() {
		return this.getJTextField0().getText();
	}

	/**
	 * The type of the output
	 * 
	 * @see IBatchDrawWriter.HTML & IBatchDrawWriter.PDF
	 * @return
	 */
	public int getOutputType() {
		if(this.jCheckBoxPDF.isSelected())
			return 0;
		else
			return 1;
	}
	
	public boolean unipep(){
		if(this.jCheckBoxUniPep.isSelected())
			return true;
		else
			return false;
	}
	
	/**
	 * The spectrum threshold
	 * 
	 * @return
	 */
	public SpectrumThreshold getSpectrumThreshold() {
		return this.getSpectrumThresholdSetPanel0().getThreshold();
	}
	
	/**
	 * The neutral loss info
	 * 
	 * @return
	 */
	public NeutralLossInfo[] getNeutralLossInfo() {
		return this.getNeutralLossSettingPanel0().getNeutralLossInfo();
	}
	
	public int [] getIonsType(){
		return this.ionsTypeSettingPanel0.getIonTypes();
	}

	public ArrayList<IProteinCriteria> getProCriteria(){
		if(this.getJCheckBoxProFilter().isSelected()){
			return this.getProteinCriPanel0().getProCriteria();
		}else{
			return null;
		}
	}
	
	public boolean useProFilter(){
		return this.getJCheckBoxProFilter().isSelected();
	}

	@Override
    public void actionPerformed(ActionEvent e) {
		
		Object obj = e.getSource();
		
		if(obj == this.getJCheckBoxProFilter()){
			
			if(this.getJCheckBoxProFilter().isSelected()){
				this.getProteinCriPanel0().switchOn();
			}else{
				this.getProteinCriPanel0().switchOff();
			}
			
			this.repaint();
			this.updateUI();
			return;
		}
		
		if(obj == this.getJButton0()) {
			
			int type = this.getOutputType();
			if (type == IBatchDrawWriter.PDF) {
				this.getOutputChooser().setFileFilter(new String[] { "pdf" },
					"PDF (*.pdf)");
			}else{
				this.getOutputChooser().setFileFilter(
						new String[] { "html", "htm" }, "HTML (*.htm, *.html)");
			}
			
			if (this.getOutputChooser().showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				this.getJTextField0().setText(
				        this.getOutputChooser().getSelectedFile()
				                .getAbsolutePath());
			}
			return ;
		}

    }

}
