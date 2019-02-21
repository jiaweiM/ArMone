/* 
 ******************************************************************************
 * File: DtaOutCompressorUI.java * * * Created on 07-04-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.zipdata;

import javax.swing.JPanel;

import java.awt.Frame;
import javax.swing.JDialog;
import java.awt.Rectangle;
import javax.swing.JLabel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.WindowConstants;

import java.awt.Font;
import java.io.File;

import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

import cn.ac.dicp.gp1809.util.StringUtil;
import cn.ac.dicp.gp1809.util.gui.UIutilities;
import java.awt.ComponentOrientation;
import javax.swing.JScrollPane;

/**
 * GUI for ZippedDtaOut
 * 
 * @author Xinning
 * @version 0.1, 07-04-2008, 10:13:05
 */
public class DtaOutCompressorUI extends JFrame implements ActionListener, ItemListener{

	private static final long serialVersionUID = 1L;
	
	//zip unzip or zip_del ?
	private int action_type;
	
	private JPanel jContentPane = null;
	private JLabel jLabel = null;
	private JTextArea jTextArea = null;
	private JButton jButtonAdd = null;
	private JRadioButton jRadioButtonZip = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel11 = null;
	private JLabel jLabel111 = null;
	private JRadioButton jRadioButtonZipDel = null;
	private JRadioButton jRadioButtonUnzip = null;
	private JButton jButtonStart = null;

	/**
     * This method initializes jTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getJTextArea() {
    	if (jTextArea == null) {
    		jTextArea = new JTextArea();
    		jTextArea.setLineWrap(true);
    	}
    	return jTextArea;
    }

	/**
     * This method initializes jButtonAdd	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButtonAdd() {
    	if (jButtonAdd == null) {
    		jButtonAdd = new JButton();
    		jButtonAdd.setBounds(new Rectangle(461, 44, 44, 18));
    		jButtonAdd.setFont(new Font("Courier New", Font.BOLD, 12));
    		jButtonAdd.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    		jButtonAdd.setHorizontalTextPosition(SwingConstants.RIGHT);
    		jButtonAdd.setText("+");
    		jButtonAdd.addActionListener(this);
    	}
    	return jButtonAdd;
    }

	/**
     * This method initializes jRadioButtonZip	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getJRadioButtonZip() {
    	if (jRadioButtonZip == null) {
    		jRadioButtonZip = new JRadioButton();
    		jRadioButtonZip.setBounds(new Rectangle(75, 113, 21, 21));
    		jRadioButtonZip.addItemListener(this);
    	}
    	return jRadioButtonZip;
    }

	/**
     * This method initializes jRadioButtonZipDel	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getJRadioButtonZipDel() {
    	if (jRadioButtonZipDel == null) {
    		jRadioButtonZipDel = new JRadioButton();
    		jRadioButtonZipDel.setBounds(new Rectangle(191, 115, 21, 21));
    		jRadioButtonZipDel.addItemListener(this);
    	}
    	return jRadioButtonZipDel;
    }

	/**
     * This method initializes jRadioButtonUnzip	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getJRadioButtonUnzip() {
    	if (jRadioButtonUnzip == null) {
    		jRadioButtonUnzip = new JRadioButton();
    		jRadioButtonUnzip.setBounds(new Rectangle(356, 114, 21, 21));
    		jRadioButtonUnzip.addItemListener(this);
    	}
    	return jRadioButtonUnzip;
    }
    
    /*
     * Group the radio buttons
     */
    private ButtonGroup getButtonGroup(){
    	ButtonGroup group = new ButtonGroup();
    	group.add(this.getJRadioButtonUnzip());
    	group.add(this.getJRadioButtonZip());
    	group.add(this.getJRadioButtonZipDel());
    	this.getJRadioButtonZipDel().setSelected(true);
    	return group;
    }

	/**
     * This method initializes jButtonStart	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButtonStart() {
    	if (jButtonStart == null) {
    		jButtonStart = new JButton();
    		jButtonStart.setBounds(new Rectangle(146, 150, 180, 22));
    		jButtonStart.setText("Start process");
    		jButtonStart.addActionListener(this);
    	}
    	return jButtonStart;
    }

	/**
     * This method initializes jScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPane() {
    	if (jScrollPane == null) {
    		jScrollPane = new JScrollPane();
    		jScrollPane.setBounds(new Rectangle(100, 13, 359, 86));
    		jScrollPane.setViewportView(getJTextArea());
    	}
    	return jScrollPane;
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new DtaOutCompressorUI();
	}

	/**
	 * @param owner
	 */
	public DtaOutCompressorUI() {
		super();
		initialize(null);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	/**
	 * @param owner
	 */
	public DtaOutCompressorUI(Frame owner) {
		super();
		initialize(owner);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize(Frame owner) {
		this.setSize(516, 214);
		this.setTitle("DtaOutCompressor By Xinning Jiang");
		this.setContentPane(getJContentPane());
		this.setLocation(UIutilities.getProperLocation(owner, this));
		this.setResizable(false);
		this.setVisible(true);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel111 = new JLabel();
			jLabel111.setBounds(new Rectangle(386, 111, 43, 26));
			jLabel111.setText("Unzip");
			jLabel11 = new JLabel();
			jLabel11.setBounds(new Rectangle(222, 113, 84, 25));
			jLabel11.setText("Zip and Delete");
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(104, 110, 36, 26));
			jLabel1.setText("Zip");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(4, 43, 91, 25));
			jLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			jLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
			jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabel.setText("Select files:");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabel, null);
			jContentPane.add(getJButtonAdd(), null);
			jContentPane.add(getJRadioButtonZip(), null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(jLabel11, null);
			jContentPane.add(jLabel111, null);
			jContentPane.add(getJRadioButtonZipDel(), null);
			jContentPane.add(getJRadioButtonUnzip(), null);
			jContentPane.add(getJButtonStart(), null);
			jContentPane.add(getJScrollPane(), null);
			
			this.getButtonGroup();
		}
		return jContentPane;
	}
	
	private File curtDir;
	private JFileChooser chooser;

	private JScrollPane jScrollPane = null;
	private JFileChooser getJFileChooser(int action_type){
		if(chooser ==null){
			this.chooser = new JFileChooser();
			this.chooser.setMultiSelectionEnabled(true);
		}
		
		if(curtDir !=null){
			this.chooser.setCurrentDirectory(curtDir);
		}
		
		if(action_type==ZippedDtaOutUltility.ACTION_ZIP||action_type==ZippedDtaOutUltility.ACTION_ZIP_DEL){
			this.chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			this.chooser.setFileFilter(new FileFilter(){

				@Override
                public boolean accept(File f) {
					
					if(f.isDirectory())
						return true;
					
	                return false;
                }

				@Override
                public String getDescription() {
					
	                return "Sequest search directory";
                }
				
			});
		}
		else if(action_type == ZippedDtaOutUltility.ACTION_UNZIP){
			this.chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			this.chooser.setFileFilter(new FileFilter(){

				@Override
                public boolean accept(File f) {
					
					if(f.isDirectory()||f.getName().toLowerCase().endsWith(".zip"))
						return true;
					
	                return false;
                }

				@Override
                public String getDescription() {
					
	                return "Zipped search directory (*.zip)";
                }
				
			});
		}
		
		return chooser;
		
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if(obj == this.jButtonStart){
			
			final String[] filenames = StringUtil.split(this.jTextArea.getText(),';');
			if(filenames.length!=0){
				
					new Thread(){
						@Override
						public void run(){
							jButtonStart.setEnabled(false);
							jButtonStart.setText("Processing ...");
							try{
								parseActionType();
								
								if(action_type == ZippedDtaOutUltility.ACTION_ZIP_DEL){
									for(String name : filenames){
										ZippedDtaOutUltility.zipAnddel(name);
									}
								}else if(action_type == ZippedDtaOutUltility.ACTION_ZIP){
									for(String name : filenames){
										ZippedDtaOutUltility.zip(name);
									}
								}else if(action_type == ZippedDtaOutUltility.ACTION_UNZIP){
									for(String name : filenames){
										ZippedDtaOutUltility.unzip(name);
									}
								}
								
								jButtonStart.setEnabled(true);
								jButtonStart.setText("Start process");
							}catch(Exception e){
								JOptionPane.showMessageDialog(DtaOutCompressorUI.this, e.getMessage());
								e.printStackTrace();
							}
							finally{
								jButtonStart.setEnabled(true);
								jButtonStart.setText("Start process");
							}

						}
					}.start();
			}
			
			
		}
		
		if(obj == this.jButtonAdd){
			int type = this.parseActionType();
			JFileChooser chooser = this.getJFileChooser(type);
			int act = chooser.showOpenDialog(this);
			if(act == JFileChooser.APPROVE_OPTION){
				File[] files = chooser.getSelectedFiles();
				for(File file : files){
					this.jTextArea.append(file.getAbsolutePath()+";");
				}
			}
		}
    }
	
	private int parseActionType(){
		if(this.jRadioButtonUnzip.isSelected()){
			this.action_type = ZippedDtaOutUltility.ACTION_UNZIP;
		}
		else if(this.jRadioButtonZip.isSelected()){
			this.action_type = ZippedDtaOutUltility.ACTION_ZIP;
		}
		else if(this.jRadioButtonZipDel.isSelected()){
			this.action_type = ZippedDtaOutUltility.ACTION_ZIP_DEL;
		}
		else{
			throw new NullPointerException("UnExpected selection index.");
		}
		
		return this.action_type;
	}

	@Override
    public void itemStateChanged(ItemEvent e) {
		
		Object obj = e.getSource();
		if(obj == this.jRadioButtonUnzip || obj == this.jRadioButtonZip || obj == this.jRadioButtonZipDel){
			this.jTextArea.setText("");
		}
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
