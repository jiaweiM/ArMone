/* 
 ******************************************************************************
 * File: GlycoStrucIdenFrame.java * * * Created on 2012-5-17
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.Iden.GlycoIdenPagedRowGetter;
import cn.ac.dicp.gp1809.glyco.Iden.GlycoIdenTask;
import cn.ac.dicp.gp1809.glyco.Iden.GlycoIdenXMLReader;
import cn.ac.dicp.gp1809.proteome.gui2.MainGui2;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import javax.swing.GroupLayout.Alignment;

public class NGlycoStrucIdenFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private NGlycoParaPanel glycoParaPanel0;
	private JLabel jLabelIn;
	private JTextField jTextFieldIn;
	private JButton jButtonIn;
	private JLabel jLabelOut;
	private JTextField jTextFieldOut;
	private JButton jButtonOut;
	private JProgressBar jProgressBar0;
	private JButton jButtonStart;
	private JButton jButtonClose;
	
	private MyJFileChooser peakChooser;
	private MyJFileChooser outChooser;
	private File file;
	private MainGui2 maingui;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public NGlycoStrucIdenFrame() {
		initComponents();
	}
	
	public NGlycoStrucIdenFrame(MainGui2 maingui) {
		initComponents();
		this.maingui = maingui;
	}

	private void initComponents() {
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(40)
					.addComponent(getJLabelIn())
					.addGap(23)
					.addComponent(getJTextFieldIn(), javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(40)
					.addComponent(getJButtonIn()))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(40)
					.addComponent(getJLabelOut())
					.addGap(14)
					.addComponent(getJTextFieldOut(), javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(40)
					.addComponent(getJButtonOut()))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(40)
					.addComponent(getJProgressBar0(), javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(102)
					.addComponent(getJButtonStart())
					.addGap(104)
					.addComponent(getJButtonClose()))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(61)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(6)
							.addComponent(getJLabelIn()))
						.addComponent(getJTextFieldIn(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(getJButtonIn()))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addComponent(getJLabelOut()))
						.addComponent(getJTextFieldOut(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(getJButtonOut()))
					.addGap(27)
					.addComponent(getJProgressBar0(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJButtonStart())
						.addComponent(getJButtonClose())))
		);
		getContentPane().setLayout(groupLayout);
		setSize(495, 287);
	}

	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText("close");
			jButtonClose.addActionListener(this);
		}
		return jButtonClose;
	}

	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setText("start");
			jButtonStart.addActionListener(this);
		}
		return jButtonStart;
	}

	private JProgressBar getJProgressBar0() {
		if (jProgressBar0 == null) {
			jProgressBar0 = new JProgressBar();
		}
		return jProgressBar0;
	}

	private JButton getJButtonOut() {
		if (jButtonOut == null) {
			jButtonOut = new JButton();
			jButtonOut.setText("...");
			jButtonOut.addActionListener(this);
		}
		return jButtonOut;
	}

	private JTextField getJTextFieldOut() {
		if (jTextFieldOut == null) {
			jTextFieldOut = new JTextField();
			jTextFieldOut.setAutoscrolls(true);
		}
		return jTextFieldOut;
	}

	private JLabel getJLabelOut() {
		if (jLabelOut == null) {
			jLabelOut = new JLabel();
			jLabelOut.setText("Output");
		}
		return jLabelOut;
	}

	private JButton getJButtonIn() {
		if (jButtonIn == null) {
			jButtonIn = new JButton();
			jButtonIn.setText("...");
			jButtonIn.addActionListener(this);
		}
		return jButtonIn;
	}

	private JTextField getJTextFieldIn() {
		if (jTextFieldIn == null) {
			jTextFieldIn = new JTextField();
			jTextFieldIn.setAutoscrolls(true);
		}
		return jTextFieldIn;
	}

	private JLabel getJLabelIn() {
		if (jLabelIn == null) {
			jLabelIn = new JLabel();
			jLabelIn.setText("Input");
		}
		return jLabelIn;
	}

	private NGlycoParaPanel getGlycoParaPanel0() {
		if (glycoParaPanel0 == null) {
			glycoParaPanel0 = new NGlycoParaPanel(true);
			glycoParaPanel0.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		}
		return glycoParaPanel0;
	}
	
	private MyJFileChooser getPeakchooser() {
		if (this.peakChooser == null) {
			this.peakChooser = new MyJFileChooser();
			this.peakChooser.setFileFilter(new String[] { "mzxml" },
					" MzXML file (*.mzxml)");
		}
		return peakChooser;
	}
	
	private MyJFileChooser getOutchooser() {
		if (this.outChooser == null) {
			this.outChooser = new MyJFileChooser(file);
			this.outChooser.setFileFilter(new String[] { "pxml" },
			        "Glycan structure result file (*.pxml)");
		}
		return outChooser;
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
	 * Main entry of the class.
	 * Note: This class is only created so that you can easily preview the result at runtime.
	 * It is not expected to be managed by the designer.
	 * You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				NGlycoStrucIdenFrame frame = new NGlycoStrucIdenFrame();
				frame
						.setDefaultCloseOperation(NGlycoStrucIdenFrame.EXIT_ON_CLOSE);
				frame.setTitle("GlycoStrucIdenFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if(obj==this.getJButtonClose()){
			this.dispose();
			return;
		}
		
		if(obj==this.getJButtonIn()){
			
			int value = this.getPeakchooser().showOpenDialog(this);
			if (value == JFileChooser.APPROVE_OPTION){
				
				File peakfile = this.getPeakchooser().getSelectedFile();
				this.getJTextFieldIn().setText(peakfile.getAbsolutePath());
				this.getJTextFieldOut().setText(peakfile.getAbsolutePath().substring(0, 
						peakfile.getAbsolutePath().length()-5)+"pxml");
				
				this.file = peakfile.getParentFile();
			}
			
			return;
		}
		
		if(obj==this.getJButtonOut()){
			
			int value = this.getOutchooser().showOpenDialog(this);
			if (value == JFileChooser.APPROVE_OPTION){
				this.getJTextFieldOut().setText(
				        this.getOutchooser().getSelectedFile().getAbsolutePath()+".pxml");
			}
			
			return;
		}
		
		if(obj==this.getJButtonStart()){
			
			String in = this.getJTextFieldIn().getText();
			
			if(in==null || in.length()==0){
				JOptionPane.showMessageDialog(null,"The input files are null.","Error",JOptionPane.ERROR_MESSAGE);
				throw new NullPointerException("The input files are null.");
			}
			
			String out = this.getJTextFieldOut().getText();
			
			if(out==null || out.length()==0){
				JOptionPane.showMessageDialog(null,"The output files are null.","Error",JOptionPane.ERROR_MESSAGE);
				throw new NullPointerException("The output files are null.");
			}
			
			GlycanStrucIdenThread thread = new GlycanStrucIdenThread(in, out, GlycoJudgeParameter.defaultParameter(), 
					this.jProgressBar0, this);
			thread.start();
			
			return;
		}
	}

	private class GlycanStrucIdenThread extends Thread{
		
		private String in;
		private String out;
		private GlycoJudgeParameter para;
		private JProgressBar jProgressBar0;
		private NGlycoStrucIdenFrame frame;
		
		GlycanStrucIdenThread(String in, String out, GlycoJudgeParameter para,
				JProgressBar jProgressBar0, NGlycoStrucIdenFrame frame){
			
			this.in = in;
			this.out = out;
			this.para = para;
			this.jProgressBar0 = jProgressBar0;
			this.frame = frame;
		}
		
		public void run(){

			frame.getJButtonStart().setEnabled(false);
			frame.getJTextFieldIn().setEditable(false);
			frame.getJTextFieldOut().setEditable(false);
			frame.getJButtonIn().setEnabled(false);
			frame.getJButtonOut().setEnabled(false);
			
			jProgressBar0.setStringPainted(true);
			jProgressBar0.setString("Processing...");
			jProgressBar0.setIndeterminate(true);
			
			NGlycoStrucViewPanel panel = null;
			
			try {
				
				GlycoIdenTask task = new GlycoIdenTask(in, out, para);
				while(task.hasNext()){
					task.processNext();
				}
				
				task.dispose();
				
				GlycoIdenXMLReader reader = new GlycoIdenXMLReader(out);
				GlycoIdenPagedRowGetter getter = new GlycoIdenPagedRowGetter(reader);
				panel = new NGlycoStrucViewPanel(getter);
				
			} catch (XMLStreamException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				
			}finally {
			
				frame.dispose();
			}
			
			jProgressBar0.setString("Complete");
			frame.getJButtonStart().setEnabled(true);
			frame.getJTextFieldIn().setEditable(true);
			frame.getJTextFieldOut().setEditable(true);
			frame.getJButtonIn().setEnabled(true);
			frame.getJButtonOut().setEnabled(true);
			
			try {
				sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			frame.dispose();
			maingui.addTabbedPane(panel);
			panel.getJButtonClose().addActionListener(maingui);
			maingui.addToCloseList(panel.getJButtonClose());
		}
	}
}
