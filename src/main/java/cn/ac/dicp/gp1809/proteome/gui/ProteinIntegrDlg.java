/* 
 ******************************************************************************
 * File: ProteinIntegrDlg.java * * * Created on 05-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IFilteredPeptideListReader;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.DecoyPeptideRemovalCriteria;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.protein.PeptideCountProteinCriteria;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.protein.PeptideHitsProteinCriteria;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-27-2009, 09:10:30
 */
public class ProteinIntegrDlg extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private MyJFileChooser output;
	private IFilteredPeptideListReader reader;
	
	private JButton jButtonOutput;
	private JLabel jLabel0;
	private JTextField jTextFieldOutput;
	private JProgressBar jProgressBar0;
	private JButton jButtonStart;
	private JPanel jPanel0;
	private JCheckBox jCheckBoxUniCount;
	private JCheckBox jCheckBoxPepCount;
	private JCheckBox jCheckBoxRemoveDecoy;
	private PeptideCountProteinCriteria peptideCountProteinCriteria0;
	private PeptideHitsProteinCriteria peptideHitsProteinCriteria0;
	private DecoyPeptideRemovalCriteria decoyPeptideRemovalCriteria0;
	private ArrayList <IProteinCriteria> proCriList;
	private ArrayList <IPeptideCriteria> pepCriList;
	private File currentFile;
	private JButton jButtonClose;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public ProteinIntegrDlg() {
		initComponents();
	}
	
	public ProteinIntegrDlg(IFilteredPeptideListReader reader) {
		this.reader = reader;
		initComponents();
	}
	
	public ProteinIntegrDlg(IFilteredPeptideListReader reader, File currentFile) {
		this.currentFile = currentFile;
		this.reader = reader;
		initComponents();
	}

	/**
	 * @return the dbchooser
	 */
	private MyJFileChooser getOutputChooser() {
		if (this.output == null) {
			this.output = new MyJFileChooser(currentFile);
			this.output.setFileFilter(new String[] {"xls"},
			        "Excel file (*.xls)");
		}
		return output;
	}

	private void initComponents() {
		setFont(new Font("Dialog", Font.PLAIN, 12));
		setBackground(new Color(204, 232, 207));
		setForeground(Color.black);
		setLayout(new GroupLayout());
		add(getJPanel0(), new Constraints(new Leading(6, 462, 6, 6), new Trailing(6, 149, 149)));
		add(getPeptideCountProteinCriteria0(), new Constraints(new Leading(57, 6, 6), new Leading(67, 30, 10, 10)));
		add(getJCheckBoxUniCount(), new Constraints(new Leading(37, 6, 6), new Leading(72, 73, 73)));
		add(getJCheckBoxRemoveDecoy(), new Constraints(new Leading(37, 6, 6), new Leading(115, 73, 73)));
		add(getJCheckBoxPepCount(), new Constraints(new Leading(253, 137, 6, 6), new Leading(72, 73, 73)));
		add(getPeptideHitsProteinCriteria0(), new Constraints(new Leading(281, 184, 10, 10), new Leading(67, 30, 10, 10)));
		add(getDecoyPeptideRemovalCriteria0(), new Constraints(new Leading(59, 184, 10, 10), new Leading(110, 10, 10)));
		add(getJButtonOutput(), new Constraints(new Leading(445, 10, 10), new Leading(18, 18, 18)));
		add(getJTextFieldOutput(), new Constraints(new Leading(88, 331, 10, 10), new Leading(18, 73, 73)));
		add(getJLabel0(), new Constraints(new Leading(34, 6, 6), new Leading(21, 10, 10)));
		add(getJProgressBar0(), new Constraints(new Leading(37, 435, 6, 6), new Leading(150, 48, 48)));
		add(getJButtonStart(), new Constraints(new Leading(150, 10, 10), new Leading(187, 18, 18)));
		add(getJButtonClose(), new Constraints(new Leading(290, 10, 10), new Leading(187, 18, 18)));
		setSize(500, 240);
	}

	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText("Close");
			jButtonClose.addActionListener(this);
		}
		return jButtonClose;
	}

	private DecoyPeptideRemovalCriteria getDecoyPeptideRemovalCriteria0() {
    	if (decoyPeptideRemovalCriteria0 == null) {
    		decoyPeptideRemovalCriteria0 = new DecoyPeptideRemovalCriteria();
    		decoyPeptideRemovalCriteria0.setMinimumSize(new Dimension(184, 30));
    	}
    	return decoyPeptideRemovalCriteria0;
    }

	private PeptideHitsProteinCriteria getPeptideHitsProteinCriteria0() {
    	if (peptideHitsProteinCriteria0 == null) {
    		peptideHitsProteinCriteria0 = new PeptideHitsProteinCriteria();
    		peptideHitsProteinCriteria0.setMinimumSize(new Dimension(184, 30));
    	}
    	return peptideHitsProteinCriteria0;
    }

	private PeptideCountProteinCriteria getPeptideCountProteinCriteria0() {
    	if (peptideCountProteinCriteria0 == null) {
    		peptideCountProteinCriteria0 = new PeptideCountProteinCriteria();
    		peptideCountProteinCriteria0.setMinimumSize(new Dimension(184, 30));
    	}
    	return peptideCountProteinCriteria0;
    }

	private JCheckBox getJCheckBoxRemoveDecoy() {
    	if (jCheckBoxRemoveDecoy == null) {
    		jCheckBoxRemoveDecoy = new JCheckBox();
    	}
    	return jCheckBoxRemoveDecoy;
    }

	private JCheckBox getJCheckBoxPepCount() {
		if (jCheckBoxPepCount == null) {
			jCheckBoxPepCount = new JCheckBox();
		}
		return jCheckBoxPepCount;
	}

	private JCheckBox getJCheckBoxUniCount() {
    	if (jCheckBoxUniCount == null) {
    		jCheckBoxUniCount = new JCheckBox();
    	}
    	return jCheckBoxUniCount;
    }

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
    		jPanel0.add(getJButtonStart());
    	}
    	return jPanel0;
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

	private JTextField getJTextFieldOutput() {
    	if (jTextFieldOutput == null) {
    		jTextFieldOutput = new JTextField();
    	}
    	return jTextFieldOutput;
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("Output");
    	}
    	return jLabel0;
    }

	private JButton getJButtonOutput() {
    	if (jButtonOutput == null) {
    		jButtonOutput = new JButton();
    		jButtonOutput.setText("...");
    		jButtonOutput.addActionListener(this);
    	}
    	return jButtonOutput;
    }
	
	/**
	 * The selected output file
	 * 
	 * @return
	 */
	public String getOutput() {
		return this.getJTextFieldOutput().getText();
	}

	public void setProCriList(ArrayList <IProteinCriteria> proCriList){
		this.proCriList = proCriList;
	}

	public void setPepCriList(ArrayList <IPeptideCriteria> pepCriList){
		this.pepCriList = pepCriList;
	}

	@Override
    public void actionPerformed(ActionEvent e) {
		
		Object obj = e.getSource();
		
		if(obj == this.getJButtonOutput()) {

			int value = this.getOutputChooser().showOpenDialog(this);
			if (value == JFileChooser.APPROVE_OPTION) {
				
				String file = this.getOutputChooser().getSelectedFile().getAbsolutePath();
				
				if(!file.toLowerCase().endsWith("xls"))
					file += ".xls";
				this.getJTextFieldOutput().setText(file);
			}
			return;
		
		}
		
		if(obj == this.getJButtonClose()){
			
			this.dispose();
			return;
		}
		
		try {
			
			if(obj == this.getJButtonStart()) {
				
				if(this.reader != null) {
					
					String output = this.getOutput();
					
					if(output == null || output.length() == 0) {
						JOptionPane.showMessageDialog(this, "The output path have not been set.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					ArrayList<IPeptideCriteria> pepcriteria = new ArrayList<IPeptideCriteria>();
					ArrayList<IProteinCriteria> procriteria = new ArrayList<IProteinCriteria>();
					
					if(this.getJCheckBoxRemoveDecoy().isSelected()) {
						pepcriteria.add(this.getDecoyPeptideRemovalCriteria0().getCriteria());
					}
					
					if(this.pepCriList!=null){
						pepcriteria.addAll(pepCriList);
					}
					
					if(this.getJCheckBoxUniCount().isSelected()) {
						procriteria.add(this.getPeptideCountProteinCriteria0().getProteinFilter());
					}
					
					if(this.getJCheckBoxPepCount().isSelected()) {
						procriteria.add(this.getPeptideHitsProteinCriteria0().getProteinFilter());
					}
					
					if(this.proCriList!=null){
						procriteria.addAll(proCriList);
					}
					
					final ProteinInferTask task = new ProteinInferTask(output, this.reader.getProNameAccesser(), this.reader,
							pepcriteria.toArray(new IPeptideCriteria[pepcriteria.size()]), 
							procriteria.toArray(new IProteinCriteria[procriteria.size()]));
					
					new Thread() {

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

							} catch(Exception e) {
								JOptionPane.showMessageDialog(ProteinIntegrDlg.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
								e.printStackTrace();
							}
							finally {
								getJButtonStart().setEnabled(true);
								dispose();
							}
						}
					}.start();
				}

				return;
			}
			
			
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
    }

}
