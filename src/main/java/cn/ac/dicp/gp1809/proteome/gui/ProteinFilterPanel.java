/* 
 ******************************************************************************
 * File: ProteinIntegrPanel.java * * * Created on 05-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.NoredundantConstants;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.protein.DecoyProteinRemovalCriteria;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.protein.PeptideCountProteinCriteria;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.protein.PeptideHitsProteinCriteria;
import cn.ac.dicp.gp1809.util.StringUtil;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import javax.swing.GroupLayout.Alignment;

/**
 * 
 * @author Xinning
 * @version 0.2, 03-25-2010, 22:52:19
 */
public class ProteinFilterPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private MyJFileChooser output;
	
	private JButton jButtonInput;
	private JLabel jLabel0;
	private JTextField jTextFieldInput;
	private JLabel jLabel1;
	private JButton jButtonOutput;
	private JTextField jTextFieldOutput;
	private JButton jButtonStart;
	private JPanel jPanel0;
	private JCheckBox jCheckBoxUniCount;
	private JCheckBox jCheckBoxPepCount;
	private JCheckBox jCheckBoxRemoveDecoy;
	private PeptideCountProteinCriteria peptideCountProteinCriteria0;
	private PeptideHitsProteinCriteria peptideHitsProteinCriteria0;
	private DecoyProteinRemovalCriteria decoyProteinRemovalCriteria0;
	private JProgressBar jProgressBar0;
	
	public ProteinFilterPanel() {
		initComponents();
	}
	
	
	/**
	 * @return the dbchooser
	 */
	private MyJFileChooser getFileChooser() {
		if (this.output == null) {
			this.output = new MyJFileChooser();
			String[] exts = StringUtil.mergeStrArray(NoredundantConstants.PREV_USED_NORED_EXTENSION,NoredundantConstants.NORED_EXTENSION);
			String[] undupexts = StringUtil.mergeStrArray(NoredundantConstants.PREV_USED_UNDUP_EXTENSION,NoredundantConstants.UNDUP_EXTENSION);
			String[] allexts = StringUtil.mergeStrArray(exts, undupexts);
			
			this.output.setFileFilter(allexts,
			        "Tab delimated txt file (*."+NoredundantConstants.NORED_EXTENSION+", *."+NoredundantConstants.UNDUP_EXTENSION+")");
		}
		return output;
	}
	

	private void initComponents() {
    	setSize(476, 233);
    	GroupLayout groupLayout = new GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(23)
    				.addComponent(getJLabel0())
    				.addGap(21)
    				.addComponent(getJTextFieldInput(), GroupLayout.PREFERRED_SIZE, 356, GroupLayout.PREFERRED_SIZE)
    				.addGap(6)
    				.addComponent(getJButtonInput()))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(21)
    				.addComponent(getJLabel1(), GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
    				.addGap(4)
    				.addComponent(getJTextFieldOutput(), GroupLayout.PREFERRED_SIZE, 356, GroupLayout.PREFERRED_SIZE)
    				.addGap(6)
    				.addComponent(getJButtonOutput()))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(37)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addComponent(getJCheckBoxUniCount())
    					.addComponent(getJCheckBoxRemoveDecoy()))
    				.addGap(2)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addComponent(getPeptideCountProteinCriteria0(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    					.addComponent(getDecoyProteinRemovalCriteria0(), GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE))
    				.addGap(20)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(19)
    						.addComponent(getPeptideHitsProteinCriteria0(), GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE))
    					.addComponent(getJCheckBoxPepCount(), GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(4)
    				.addComponent(getJProgressBar0(), GroupLayout.PREFERRED_SIZE, 472, GroupLayout.PREFERRED_SIZE))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJPanel0(), GroupLayout.PREFERRED_SIZE, 462, GroupLayout.PREFERRED_SIZE))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(18)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(6)
    						.addComponent(getJLabel0()))
    					.addComponent(getJTextFieldInput(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    					.addComponent(getJButtonInput()))
    				.addGap(10)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(8)
    						.addComponent(getJLabel1()))
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(2)
    						.addComponent(getJTextFieldOutput(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addComponent(getJButtonOutput()))
    				.addGap(12)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(4)
    						.addComponent(getJCheckBoxUniCount())
    						.addGap(12)
    						.addComponent(getJCheckBoxRemoveDecoy()))
    					.addComponent(getPeptideCountProteinCriteria0(), GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(28)
    						.addComponent(getDecoyProteinRemovalCriteria0(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addComponent(getPeptideHitsProteinCriteria0(), GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(6)
    						.addComponent(getJCheckBoxPepCount())))
    				.addGap(3)
    				.addComponent(getJProgressBar0(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    				.addGap(9)
    				.addComponent(getJPanel0(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    	);
    	setLayout(groupLayout);
    }

	private JProgressBar getJProgressBar0() {
    	if (jProgressBar0 == null) {
    		jProgressBar0 = new JProgressBar();
    	}
    	return jProgressBar0;
    }

	private DecoyProteinRemovalCriteria getDecoyProteinRemovalCriteria0() {
    	if (decoyProteinRemovalCriteria0 == null) {
    		decoyProteinRemovalCriteria0 = new DecoyProteinRemovalCriteria();
    		decoyProteinRemovalCriteria0.setMinimumSize(new Dimension(184, 30));
    	}
    	return decoyProteinRemovalCriteria0;
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

	private JTextField getJTextFieldOutput() {
    	if (jTextFieldOutput == null) {
    		jTextFieldOutput = new JTextField();
    	}
    	return jTextFieldOutput;
    }

	private JButton getJButtonOutput() {
    	if (jButtonOutput == null) {
    		jButtonOutput = new JButton();
    		jButtonOutput.setText("...");
    		jButtonOutput.addActionListener(this);
    	}
    	return jButtonOutput;
    }

	private JLabel getJLabel1() {
    	if (jLabel1 == null) {
    		jLabel1 = new JLabel();
    		jLabel1.setText("Output");
    	}
    	return jLabel1;
    }

	private JTextField getJTextFieldInput() {
    	if (jTextFieldInput == null) {
    		jTextFieldInput = new JTextField();
    	}
    	return jTextFieldInput;
    }

	private JLabel getJLabel0() {
    	if (jLabel0 == null) {
    		jLabel0 = new JLabel();
    		jLabel0.setText("Input");
    	}
    	return jLabel0;
    }

	private JButton getJButtonInput() {
    	if (jButtonInput == null) {
    		jButtonInput = new JButton();
    		jButtonInput.setText("...");
    		jButtonInput.addActionListener(this);
    	}
    	return jButtonInput;
    }
	
	/**
	 * The selected output file
	 * 
	 * @return
	 */
	public String getOutput() {
		return this.getJTextFieldOutput().getText();
	}
	
	/**
	 * The input file
	 * @return
	 */
	public String getInput() {
		return this.getJTextFieldInput().getText();
	}
	

	@Override
    public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		try {
			
			if(obj == this.getJButtonInput()) {

				int value = this.getFileChooser().showSaveDialog(this);
				if (value == JFileChooser.APPROVE_OPTION) {
					String file = this.getFileChooser().getSelectedFile().getAbsolutePath();
					this.getJTextFieldInput().setText(file);
					int idx = file.lastIndexOf('.');
					String basename = file.substring(0,idx);
					String ext = file.substring(idx+1);
					String newname = basename + "_filered."+ext;
					
					this.getJTextFieldOutput().setText(newname);
				}
				return;
			
			}
			
			if(obj == this.getJButtonOutput()) {

				int value = this.getFileChooser().showOpenDialog(this);
				if (value == JFileChooser.APPROVE_OPTION) {
					String file = this.getFileChooser().getSelectedFile().getAbsolutePath();
					String lowfile = file.toLowerCase();
					
					if(!lowfile.endsWith(NoredundantConstants.NORED_EXTENSION) && !lowfile.endsWith(NoredundantConstants.UNDUP_EXTENSION)) {
						file += "."+NoredundantConstants.NORED_EXTENSION;
					}
					this.getJTextFieldOutput().setText(file);
				}
				return;
			
			}
			
			if(obj == this.getJButtonStart()) {
					
					String input = this.getInput();
					
					if(input == null || input.length() == 0) {
						JOptionPane.showMessageDialog(this, "The input path is null.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					String output = this.getOutput();
					
					if(output == null || output.length() == 0) {
						JOptionPane.showMessageDialog(this, "The output is null.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					ArrayList<IProteinCriteria> procriteria = new ArrayList<IProteinCriteria>();
					
					if(this.getJCheckBoxRemoveDecoy().isSelected()) {
						procriteria.add(this.getDecoyProteinRemovalCriteria0().getProteinFilter());
					}
					
					
					if(this.getJCheckBoxUniCount().isSelected()) {
						procriteria.add(this.getPeptideCountProteinCriteria0().getProteinFilter());
					}
					
					if(this.getJCheckBoxPepCount().isSelected()) {
						procriteria.add(this.getPeptideHitsProteinCriteria0().getProteinFilter());
					}
					
					
					final ProteinFilterTask task = new ProteinFilterTask(input, output, 
							procriteria.toArray(new IProteinCriteria[procriteria.size()]));
					
					new Thread() {

						@Override
						public void run() {

							try {
								getJButtonStart().setEnabled(false);
								getJProgressBar0().setIndeterminate(true);
								
								while (task.hasNext()) {
									task.processNext();
								}
								
								task.dispose();
								
								getJProgressBar0().setIndeterminate(false);
								

							} catch(Exception e) {
								JOptionPane.showMessageDialog(ProteinFilterPanel.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
								e.printStackTrace();
							}
							finally {
								getJButtonStart().setEnabled(true);
							}
						}
					}.start();
				
				return;
			}
			
			
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
    }

}
