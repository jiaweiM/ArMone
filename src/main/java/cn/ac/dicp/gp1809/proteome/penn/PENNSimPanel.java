/* 
 ******************************************************************************
 * File: PENNMainPanel.java * * * Created on 08-08-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.penn;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SimSequestPeptideFormat;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.sim.SimCalculator;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.sim.SimCalculator.Instrument;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import javax.swing.GroupLayout.Alignment;

/**
 * 
 * @author Xinning
 * @version 0.1.1, 05-25-2010, 15:39:18
 */
public class PENNSimPanel extends JPanel implements ItemListener, ActionListener{

	private static final long serialVersionUID = 1L;
	
	private MyJFileChooser pplchooser;
	
	private JLabel jLabel0;
	private JTextField jTextFieldInput;
	private JButton jButtonInput;
	private JButton jButtonOutput;
	private JTextField jTextFieldOutput;
	private JLabel jLabel2;
	private JPanel jPanel0;
	private JButton jButtonStart;
	private JPanel jPanel1;
	private JProgressBar jProgressBar;

	private JLabel jLabel4;

	private JComboBox jComboBoxInstrument;

	private JLabel jLabel5;

	private JLabel jLabel6;

	private JLabel jLabel7;

	private JLabel jLabel8;

	private JLabel jLabel9;

	private JFormattedTextField jFormattedTextFieldIsoWidth;

	private JFormattedTextField jFormattedTextFieldResolution;

	private JFormattedTextField jFormattedTextFieldCID;

	private JFormattedTextField jFormattedTextFieldActime;

	private JFormattedTextField jFormattedTextFieldFTol;

	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public PENNSimPanel() {
		initComponents();
	}

	private void initComponents() {
    	setSize(516, 298);
    	GroupLayout groupLayout = new GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(12)
    				.addComponent(getJPanel0(), GroupLayout.PREFERRED_SIZE, 492, GroupLayout.PREFERRED_SIZE))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(9)
    				.addComponent(getJLabel0())
    				.addGap(9)
    				.addComponent(getJTextFieldInput(), GroupLayout.PREFERRED_SIZE, 386, GroupLayout.PREFERRED_SIZE)
    				.addGap(11)
    				.addComponent(getJButtonInput()))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(9)
    				.addComponent(getJLabel2(), GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
    				.addGap(8)
    				.addComponent(getJTextFieldOutput(), GroupLayout.PREFERRED_SIZE, 388, GroupLayout.PREFERRED_SIZE)
    				.addGap(10)
    				.addComponent(getJButtonOutput()))
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(6)
    				.addComponent(getJProgressBar(), GroupLayout.PREFERRED_SIZE, 498, GroupLayout.PREFERRED_SIZE))
    			.addComponent(getJPanel1(), GroupLayout.PREFERRED_SIZE, 510, GroupLayout.PREFERRED_SIZE)
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(9)
    				.addComponent(getJPanel0(), GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
    				.addGap(7)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(5)
    						.addComponent(getJLabel0()))
    					.addComponent(getJTextFieldInput(), GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
    					.addComponent(getJButtonInput()))
    				.addGap(10)
    				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(6)
    						.addComponent(getJLabel2()))
    					.addComponent(getJTextFieldOutput(), GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGap(2)
    						.addComponent(getJButtonOutput())))
    				.addGap(21)
    				.addComponent(getJProgressBar(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    				.addGap(6)
    				.addComponent(getJPanel1(), GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
    	);
    	setLayout(groupLayout);
    }

	private JFormattedTextField getJFormattedTextFieldFTol() {
    	if (jFormattedTextFieldFTol == null) {
    		jFormattedTextFieldFTol = new JFormattedTextField();
    	}
    	return jFormattedTextFieldFTol;
    }

	private JFormattedTextField getJFormattedTextFieldActime() {
    	if (jFormattedTextFieldActime == null) {
    		jFormattedTextFieldActime = new JFormattedTextField();
    	}
    	return jFormattedTextFieldActime;
    }

	private JFormattedTextField getJFormattedTextFieldCID() {
    	if (jFormattedTextFieldCID == null) {
    		jFormattedTextFieldCID = new JFormattedTextField();
    	}
    	return jFormattedTextFieldCID;
    }

	private JFormattedTextField getJFormattedTextFieldResolution() {
    	if (jFormattedTextFieldResolution == null) {
    		jFormattedTextFieldResolution = new JFormattedTextField();
    	}
    	return jFormattedTextFieldResolution;
    }

	private JFormattedTextField getJFormattedTextFieldIsoWidth() {
    	if (jFormattedTextFieldIsoWidth == null) {
    		jFormattedTextFieldIsoWidth = new JFormattedTextField();
    	}
    	return jFormattedTextFieldIsoWidth;
    }

	private JLabel getJLabel9() {
    	if (jLabel9 == null) {
    		jLabel9 = new JLabel();
    		jLabel9.setText("Fragment tolerance (+-)");
    	}
    	return jLabel9;
    }

	private JLabel getJLabel8() {
    	if (jLabel8 == null) {
    		jLabel8 = new JLabel();
    		jLabel8.setText("Activation time (s)");
    	}
    	return jLabel8;
    }

	private JLabel getJLabel7() {
    	if (jLabel7 == null) {
    		jLabel7 = new JLabel();
    		jLabel7.setText("Collistion energy (%)");
    	}
    	return jLabel7;
    }

	private JLabel getJLabel6() {
    	if (jLabel6 == null) {
    		jLabel6 = new JLabel();
    		jLabel6.setText("Resolution");
    	}
    	return jLabel6;
    }

	private JLabel getJLabel5() {
    	if (jLabel5 == null) {
    		jLabel5 = new JLabel();
    		jLabel5.setText("Isolation width (Da)");
    	}
    	return jLabel5;
    }

	private JComboBox getJComboBoxInstrument() {
    	if (jComboBoxInstrument == null) {
    		jComboBoxInstrument = new JComboBox();
    		
    		jComboBoxInstrument.addItem("LCQ");
    		jComboBoxInstrument.addItem("LTQ");
    		jComboBoxInstrument.addItem("LTQ-Orbitrap");
    		jComboBoxInstrument.addItem("LTQ-FT");
    		
    		jComboBoxInstrument.addItemListener(this);
    		jComboBoxInstrument.setSelectedItem("LTQ");
    	}
    	return jComboBoxInstrument;
    }

	private JLabel getJLabel4() {
    	if (jLabel4 == null) {
    		jLabel4 = new JLabel();
    		jLabel4.setText("Instrument");
    	}
    	return jLabel4;
    }

	private MyJFileChooser getJFileChooserPpl() {
		if(this.pplchooser == null) {
			this.pplchooser = new MyJFileChooser();
			this.pplchooser.setFileFilter(new String[] {"ppl"}, "The peptide list file (*.ppl)");
		}
		return this.pplchooser;
	}
	
	private JProgressBar getJProgressBar() {
    	if (jProgressBar == null) {
    		jProgressBar = new JProgressBar();
    	}
    	return jProgressBar;
    }

	private JPanel getJPanel1() {
    	if (jPanel1 == null) {
    		jPanel1 = new JPanel();
    		jPanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
    		jPanel1.add(getJButtonStart());
    	}
    	return jPanel1;
    }

	private JButton getJButtonStart() {
    	if (jButtonStart == null) {
    		jButtonStart = new JButton();
    		jButtonStart.setText("Start");
    		jButtonStart.addActionListener(this);
    	}
    	return jButtonStart;
    }

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.setBorder(BorderFactory.createTitledBorder(null, "Parameters for calculation of Sim score", TitledBorder.LEADING, TitledBorder.ABOVE_TOP,
    				new Font("SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
    		GroupLayout gl_jPanel0 = new GroupLayout(jPanel0);
    		gl_jPanel0.setHorizontalGroup(
    			gl_jPanel0.createParallelGroup(Alignment.LEADING)
    				.addGroup(gl_jPanel0.createSequentialGroup()
    					.addGap(2)
    					.addComponent(getJLabel4())
    					.addGap(15)
    					.addComponent(getJComboBoxInstrument(), GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
    					.addGap(53)
    					.addComponent(getJLabel7(), GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
    					.addGap(18)
    					.addComponent(getJFormattedTextFieldCID(), GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))
    				.addGroup(gl_jPanel0.createSequentialGroup()
    					.addComponent(getJLabel5(), GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
    					.addGap(7)
    					.addComponent(getJFormattedTextFieldIsoWidth(), GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
    					.addGap(53)
    					.addComponent(getJLabel8(), GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
    					.addGap(18)
    					.addComponent(getJFormattedTextFieldActime(), GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))
    				.addGroup(gl_jPanel0.createSequentialGroup()
    					.addComponent(getJLabel6(), GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
    					.addGap(54)
    					.addComponent(getJFormattedTextFieldResolution(), GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
    					.addGap(53)
    					.addComponent(getJLabel9(), GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
    					.addGap(6)
    					.addComponent(getJFormattedTextFieldFTol(), GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))
    		);
    		gl_jPanel0.setVerticalGroup(
    			gl_jPanel0.createParallelGroup(Alignment.LEADING)
    				.addGroup(gl_jPanel0.createSequentialGroup()
    					.addGroup(gl_jPanel0.createParallelGroup(Alignment.LEADING)
    						.addGroup(gl_jPanel0.createSequentialGroup()
    							.addGap(5)
    							.addComponent(getJLabel4()))
    						.addComponent(getJComboBoxInstrument(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    						.addGroup(gl_jPanel0.createSequentialGroup()
    							.addGap(5)
    							.addComponent(getJLabel7()))
    						.addComponent(getJFormattedTextFieldCID(), GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
    					.addGap(3)
    					.addGroup(gl_jPanel0.createParallelGroup(Alignment.LEADING)
    						.addGroup(gl_jPanel0.createSequentialGroup()
    							.addGap(3)
    							.addComponent(getJLabel5()))
    						.addComponent(getJFormattedTextFieldIsoWidth(), GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
    						.addGroup(gl_jPanel0.createSequentialGroup()
    							.addGap(2)
    							.addComponent(getJLabel8()))
    						.addComponent(getJFormattedTextFieldActime(), GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
    					.addGap(6)
    					.addGroup(gl_jPanel0.createParallelGroup(Alignment.LEADING)
    						.addGroup(gl_jPanel0.createSequentialGroup()
    							.addGap(3)
    							.addComponent(getJLabel6()))
    						.addComponent(getJFormattedTextFieldResolution(), GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
    						.addGroup(gl_jPanel0.createSequentialGroup()
    							.addGap(3)
    							.addComponent(getJLabel9()))
    						.addComponent(getJFormattedTextFieldFTol(), GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)))
    		);
    		jPanel0.setLayout(gl_jPanel0);
    	}
    	return jPanel0;
    }

	private JLabel getJLabel2() {
    	if (jLabel2 == null) {
    		jLabel2 = new JLabel();
    		jLabel2.setText("Output");
    	}
    	return jLabel2;
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
    		jButtonOutput.setText("<<");
    		jButtonOutput.addActionListener(this);
    	}
    	return jButtonOutput;
    }

	private JButton getJButtonInput() {
    	if (jButtonInput == null) {
    		jButtonInput = new JButton();
    		jButtonInput.setText("<<");
    		jButtonInput.addActionListener(this);
    	}
    	return jButtonInput;
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
    		jLabel0.setText("Input ppl");
    	}
    	return jLabel0;
    }

	@Override
    public void itemStateChanged(ItemEvent e) {
		
		Object obj = e.getSource();

		if(obj == this.getJComboBoxInstrument()) {
			Instrument instr = SimCalculator.INST_LTQ;
			switch(this.getJComboBoxInstrument().getSelectedIndex() + 1){
			case SimCalculator.LCQ : instr = SimCalculator.INST_LCQ;break;
			case SimCalculator.ORBITRAP : instr = SimCalculator.INST_ORBITRAP; break;
			case SimCalculator.LTQFT : instr = SimCalculator.INST_LTQFT;break;
			default: ;//Default is LTQ
			}
			
			this.showDefaultInstrument(instr);
			
			return ;
		}

	
    }
	
	private void showDefaultInstrument(Instrument instr){
		this.getJFormattedTextFieldCID().setValue(instr.getCollisionEnergy());
		this.getJFormattedTextFieldIsoWidth().setText(String.valueOf(instr.getIsolationWidth()));
		this.getJFormattedTextFieldActime().setText(String.valueOf(instr.getReactionTime()));
		this.getJFormattedTextFieldResolution().setText(String.valueOf(instr.getResolution()));
		this.getJFormattedTextFieldFTol().setText(String.valueOf(instr.getFrag_tolerance()));
	}
	
	private Instrument getModifiedInstrument() throws IllegalArgumentException{
		
			short instrument = (short) (this.getJComboBoxInstrument().getSelectedIndex()-1);
			float startMass = 50f;
			float endMass = 2000f;
			float collisionEnergy = Float.parseFloat(this.getJFormattedTextFieldCID().getText());
			float reactionTime = Float.parseFloat(this.getJFormattedTextFieldActime().getText());
			float isolationWidth = Float.parseFloat(this.getJFormattedTextFieldIsoWidth().getText());
			float resolution = Float.parseFloat(this.getJFormattedTextFieldResolution().getText());
			float frag_tolerance = Float.parseFloat(this.getJFormattedTextFieldFTol().getText());
			
			return new Instrument(instrument, collisionEnergy, reactionTime,startMass, 
					endMass, isolationWidth, resolution,  frag_tolerance);

	}

	@Override
    public void actionPerformed(ActionEvent e) {
		
		Object obj = e.getSource();
		
		
		if(obj == this.getJButtonInput()) {
			int val = this.getJFileChooserPpl().showOpenDialog(this);
			if(val == JFileChooser.APPROVE_OPTION) {
				String name = this.getJFileChooserPpl().getSelectedFile().getAbsolutePath();
				this.getJTextFieldInput().setText(name);
				this.getJTextFieldOutput().setText(name.substring(0,name.length()-3)+"sim.ppl");
			}
			return ;
		}
		
		if(obj == this.getJButtonOutput()) {
			int val = this.getJFileChooserPpl().showSaveDialog(this);
			if(val == JFileChooser.APPROVE_OPTION) {
				String name = this.getJFileChooserPpl().getSelectedFile().getAbsolutePath();
				if(!name.toLowerCase().endsWith(".sim.ppl"))
					name += ".sim.ppl";
				this.getJTextFieldOutput().setText(name);
			}
			return ;
		}
		
		if(obj == this.getJButtonStart()) {
			
			try {
				final String input = this.getJTextFieldInput().getText();
				if(input.length() < 3)
					throw new NullPointerException("Please select input first");
				
				final String output = this.getJTextFieldOutput().getText();
				if(output.length() < 3)
					throw new NullPointerException("Please select output first");

				
					new Thread(){
						@Override
						public void run() {
							try {
							getJButtonStart().setEnabled(false);
							getJProgressBar().setString("Processing, please wait ...");
							
							
							PeptideListReader reader = new PeptideListReader(input);
							
							if(reader.getPeptideType() != PeptideType.SEQUEST) {
								throw new IllegalArgumentException("Currently can only process SEQUEST results");
							}
							
							getJProgressBar().setMaximum(reader.getNumberofPeptides());
							
							SimCalculator calor = new SimCalculator(getModifiedInstrument());
							MwCalculator mwcalor = new MwCalculator(reader.getSearchParameter()
							        .getStaticInfo(), reader.getSearchParameter().getVariableInfo());
							
							PeptideListWriter writer = new PeptideListWriter(output, SimSequestPeptideFormat.newInstance(),
									reader.getSearchParameter(), reader.getDecoyJudger(), reader.getProNameAccesser());
							
							IPeptide peptide;
							while((peptide = reader.getPeptide())!=null) {
								IMS2PeakList [] lists = reader.getPeakLists();
								float sim = calor.getSim(peptide.getSequence(), mwcalor, peptide.getCharge(), lists[0]);
								peptide.setSim(sim);
								writer.write(peptide, reader.getPeakLists());
								
								getJProgressBar().setValue(reader.getCurtPeptideIndex());
							}
							
							reader.close();
							writer.close();
							getJProgressBar().setString("Completed!");
							
                            } catch (Exception ie) {
                            	JOptionPane.showMessageDialog(PENNSimPanel.this, ie, "Error", JOptionPane.ERROR_MESSAGE);
                            	ie.printStackTrace();
                            } finally {
    							getJButtonStart().setEnabled(true);
                            }
						}
					}.start();
				
			}catch(Exception ex) {
				JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}

			
			return ;
		}
		
    }
	
	public static void main(String[] args) {
		
	}
}
