/*
 * *****************************************************************************
 * File: DBDecoy.java * * * Created on 03-24-2008
 * 
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.dbdecoy;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;
import javax.swing.JProgressBar;
import javax.swing.JComboBox;

import cn.ac.dicp.gp1809.util.gui.UIutilities;

import java.awt.Font;
import java.awt.Rectangle;

/**
 * Frame UI for creation of composite database containing both target and decoy sequences.
 * 
 * @author Xinning
 * @version 0.1.1, 04-05-2008, 15:44:15
 */
public class DBDecoy extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3108361021210875115L;

	private static final Pattern DBFILE = Pattern.compile(".+\\p{Punct}fasta",Pattern.CASE_INSENSITIVE);

	private JPanel jContentPane = null;
	private JTextField jTextFieldInput = null;
	private JTextField jTextFieldOutput = null;
	private JButton jButtonInput = null;
	private JButton jButtonReverse = null;
	private JLabel jLabelInput = null;
	private JLabel jLabelOutput = null;
	
	
	private String inputfilename,outputfilename, decoyfilename;
	public JProgressBar jProgressBar = null;

	public JLabel jLabel = null;

	private JComboBox jComboBox = null;

	/**
	 * This method initializes jTextFieldInput	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldInput() {
		if (jTextFieldInput == null) {
			jTextFieldInput = new JTextField();
			jTextFieldInput.setBounds(new Rectangle(107,37,303,21));
			jTextFieldInput.setEditable(false);
		}
		return jTextFieldInput;
	}

	/**
	 * This method initializes jTextFieldOutput	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldOutput() {
		if (jTextFieldOutput == null) {
			jTextFieldOutput = new JTextField();
			jTextFieldOutput.setBounds(new Rectangle(108,75,301,21));
			jTextFieldOutput.setEditable(false);
		}
		return jTextFieldOutput;
	}

	/**
	 * This method initializes jButtonInput	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonInput() {
		if (jButtonInput == null) {
			jButtonInput = new JButton();
			jButtonInput.setBounds(new Rectangle(412,38,21,17));
			jButtonInput.setText("...");
			jButtonInput.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFont(new Font("Dialog", Font.PLAIN, 12));
					chooser.addChoosableFileFilter(new FileFilter(){
						@Override
						public boolean accept(File file){
							if(file.isDirectory()||DBFILE.matcher(file.getName()).matches())
								return true;
							else 
								return false;
						}
						
						@Override
						public String getDescription(){
							return "DataBase File Format(.fasta)";
						}
					});
					int action = chooser.showOpenDialog(DBDecoy.this);
					if(action == JFileChooser.APPROVE_OPTION){
						String dir = chooser.getCurrentDirectory().getAbsolutePath();
						File choosedfile = chooser.getSelectedFile().getAbsoluteFile();
						inputfilename = choosedfile.getAbsolutePath();
						decoyfilename = dir+"\\Decoy_"+choosedfile.getName();
						outputfilename = dir+"\\Final_"+choosedfile.getName();
						jTextFieldInput.setText(inputfilename);
						jTextFieldOutput.setText(outputfilename);
					}
				}
			});
		}
		return jButtonInput;
	}

	/**
	 * This method initializes jButtonStart	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonReverse() {
		if (jButtonReverse == null) {
			jButtonReverse = new JButton();
			jButtonReverse.setBounds(new Rectangle(318,114,88,20));
			jButtonReverse.setText("Go!");
			jButtonReverse.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jLabel.setText("");

					new Thread(new Runnable(){
						public void run() {
							DecoyFasta decoy;
							
							int index = jComboBox.getSelectedIndex();
							if(index==0)
								decoy = new ReverseFasta(inputfilename,decoyfilename,DBDecoy.this);
							else if(index == 1)
								decoy = new RandomDB(inputfilename,decoyfilename,DBDecoy.this);
							else 
								decoy = new RandomPepLev(inputfilename,decoyfilename,DBDecoy.this);
							
							try {
								decoy.makeDecoy();
								Merger.merge(inputfilename, decoyfilename, outputfilename);
							} catch (IOException e) {
								System.out.println(e);
								jLabel.setText("Reading & Writing Error, please check out the fasta file");
								throw new RuntimeException("IOException");
							}
						}
					}).start();
				}
			});
			
			
		}
		return jButtonReverse;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setBounds(new Rectangle(0,164,479,25));
		}
		return jProgressBar;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
			jComboBox.setBounds(new Rectangle(103,116,166,18));
			jComboBox.addItem("Reverse");
//			jComboBox.addItem("KR Specific Random");
//			jComboBox.addItem("Peptdie Level Random");
		}
		return jComboBox;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new DBDecoy();
	}

	/**
	 * This is the default constructor
	 */
	public DBDecoy() {
		this(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Call this frame from a parent component
	 * @param parent
	 */
	public DBDecoy(JFrame parent){
		super();
		initialize();
		this.setLocation(UIutilities.getProperLocation(parent, this));
		this.setVisible(true);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(485, 221);
		this.setContentPane(getJContentPane());
		this.setTitle("DBDecoy");
		this.setResizable(false);
	}
	
	

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(2,142,180,21));
			jLabel.setText("");
			jLabelOutput = new JLabel();
			jLabelOutput.setBounds(new Rectangle(17, 76, 82, 21));
			jLabelOutput.setText("Decoyed DB");
			jLabelInput = new JLabel();
			jLabelInput.setBounds(new Rectangle(26, 37, 73, 21));
			jLabelInput.setText("Origin DB");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJTextFieldInput(), null);
			jContentPane.add(getJTextFieldOutput(), null);
			jContentPane.add(getJButtonInput(), null);
			jContentPane.add(getJButtonReverse(), null);
			jContentPane.add(jLabelInput, null);
			jContentPane.add(jLabelOutput, null);
			jContentPane.add(getJProgressBar(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(getJComboBox(), null);
		}
		return jContentPane;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
