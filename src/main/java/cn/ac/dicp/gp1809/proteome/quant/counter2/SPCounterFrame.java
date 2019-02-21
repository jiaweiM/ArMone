/* 
 ******************************************************************************
 * File:SPCounterFrame.java * * * Created on 2010-9-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.counter2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.gui2.util.JFileSelectPanel;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import javax.swing.GroupLayout.Alignment;

//VS4E -- DO NOT REMOVE THIS LINE!
public class SPCounterFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JFileSelectPanel panel1;
	private JFileSelectPanel panel2;
	private JProgressBar jProgressBar0;
	private JButton jButtonStart;
	private JButton jButtonClose;
	private JLabel jLabel0;
	private JTextField jTextField0;
	private JButton jButton0;
	private MyJFileChooser chooser;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public SPCounterFrame() {
		initComponents();
	}

	private void initComponents() {
		setTitle("SPCounter");
		javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(getJPanel1(), javax.swing.GroupLayout.PREFERRED_SIZE, 622, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addComponent(getJPanel2(), javax.swing.GroupLayout.PREFERRED_SIZE, 622, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(17)
					.addComponent(getJLabel0())
					.addGap(35)
					.addComponent(getJTextField0(), javax.swing.GroupLayout.PREFERRED_SIZE, 446, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(39)
					.addComponent(getJButton0()))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(12)
					.addComponent(getJProgressBar0(), javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(200)
					.addComponent(getJButtonStart())
					.addGap(77)
					.addComponent(getJButtonClose()))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(getJPanel1(), javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addComponent(getJPanel2(), javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(6)
							.addComponent(getJLabel0()))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(1)
							.addComponent(getJTextField0(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addComponent(getJButton0()))
					.addGap(18)
					.addComponent(getJProgressBar0(), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(22)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getJButtonStart())
						.addComponent(getJButtonClose())))
		);
		getContentPane().setLayout(groupLayout);
		setSize(640, 520);
	}
	
	private MyJFileChooser getOutchooser() {
		if (this.chooser == null) {
			this.chooser = new MyJFileChooser();
			this.chooser.setFileFilter(new String[] { "xls" },
			        " Quantitation result file (*.xls)");
		}
		return chooser;
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
			jTextField0.setText("");
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

	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setText("Start");
			jButtonStart.addActionListener(this);
		}
		return jButtonStart;
	}
	
	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText("Close");
			jButtonClose.addActionListener(this);
		}
		return jButtonClose;
	}

	private JProgressBar getJProgressBar0() {
		if (jProgressBar0 == null) {
			jProgressBar0 = new JProgressBar();
		}
		return jProgressBar0;
	}

	private JFileSelectPanel getJPanel1() {
		if (panel1 == null) {
			panel1 = new JFileSelectPanel("Sample 1", "xls", "protein identification result");
		}
		return panel1;
	}
	
	private JFileSelectPanel getJPanel2() {
		if (panel2 == null) {
			panel2 = new JFileSelectPanel("Sample 2", "xls", "protein identification result");
		}
		return panel2;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object obj = e.getSource();
		
		if(obj == this.jButtonClose){
			this.dispose();
			return;
		}
		
		if(obj==this.jButton0){
			
			int value = this.getOutchooser().showOpenDialog(this);
			if (value == JFileChooser.APPROVE_OPTION){
				this.getJTextField0().setText(this.getOutchooser().getSelectedFile().getAbsolutePath()+".xls");
			}
			
			return;
		}
		
		if(obj == this.jButtonStart){
			
			File [] f1 = this.panel1.getFiles();
			File [] f2 = this.panel2.getFiles();
			
			String output = this.getJTextField0().getText();

			if(f1==null || f1.length==0){
				JOptionPane.showMessageDialog(null,"The input files are null.","Error",JOptionPane.ERROR_MESSAGE);
				throw new NullPointerException("The input files are null.");
			}
			
			if(f2==null || f2.length==0){
				JOptionPane.showMessageDialog(null,"The input files are null.","Error",JOptionPane.ERROR_MESSAGE);
				throw new NullPointerException("The input files are null.");
			}
			
			if(output == null || output.length() == 0) {
				JOptionPane.showMessageDialog(null,"The output path is null.","Error",JOptionPane.ERROR_MESSAGE);
				throw new NullPointerException("The output path is null.");
			}
			
			WriteThread thread = new WriteThread(f1, f2, output, jProgressBar0, this);
			
			thread.start();
			
			return;
		}
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

	private class WriteThread extends Thread{
		
		private File [] f1;
		private File [] f2;
		private String output;
		private JProgressBar bar;
		private JFrame frame;
		
		private WriteThread(File [] f1, File [] f2, String output, 
				JProgressBar bar, JFrame frame){
			
			this.f1 = f1;
			this.f2 = f2;
			this.output = output;
			this.bar = bar;
			this.frame = frame;
		}
		
		public void run(){
			
			bar.setStringPainted(true);
			bar.setString("Processing...");
			bar.setIndeterminate(true);

			frame.setEnabled(false);
			
			try {
				
				SCComparator2 sc2 = new SCComparator2(f1, f2, output);
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			
			bar.setString("Complete");
			
			try {
				sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			frame.dispose();
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
			@Override
			public void run() {
				SPCounterFrame frame = new SPCounterFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("SPCounterFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
