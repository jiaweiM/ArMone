/* 
 ******************************************************************************
 * File:FastaExpPanel.java * * * Created on 2010-6-18
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IFilteredPeptideListReader;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaCreator;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

//VS4E -- DO NOT REMOVE THIS LINE!
public class FastaExpFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel jLabelOutput;
	private JTextField jTextFieldOutput;
	private JButton jButtonOutput;
	private JButton jButtonStart;
	private MyJFileChooser jFileChooserOut;
	private MyJFileChooser jFileChooserFasta;
	private IFilteredPeptideListReader reader;
	private JLabel jLabelDatabase;
	private JTextField jTextFieldDatabase;
	private JButton jButtonDatabase;
	private JButton jButtonClose;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public FastaExpFrame() {
		initComponents();
	}

	public FastaExpFrame(IFilteredPeptideListReader reader) {
		this.reader = reader;
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJLabelOutput(), new Constraints(new Leading(28, 12, 12), new Leading(36, 12, 12)));
		add(getJButtonStart(), new Constraints(new Leading(135, 6, 6), new Leading(137, 10, 10)));
		add(getJButtonOutput(), new Constraints(new Leading(332, 28, 10, 10), new Leading(32, 25, 6, 6)));
		add(getJButtonDatabase(), new Constraints(new Leading(332, 28, 6, 6), new Leading(80, 25, 6, 6)));
		add(getJLabelDatabase(), new Constraints(new Leading(28, 6, 6), new Leading(83, 6, 6)));
		add(getJTextFieldOutput(), new Constraints(new Leading(92, 217, 10, 10), new Leading(32, 12, 12)));
		add(getJTextFieldDatabase(), new Constraints(new Leading(92, 216, 6, 6), new Leading(83, 10, 10)));
		add(getJButtonClose(), new Constraints(new Leading(229, 10, 10), new Leading(137, 6, 6)));
		setSize(400, 200);
	}

	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText("Close");
			jButtonClose.addActionListener(this);
		}
		return jButtonClose;
	}

	private JButton getJButtonDatabase() {
		if (jButtonDatabase == null) {
			jButtonDatabase = new JButton();
			jButtonDatabase.setText("...");
			jButtonDatabase.addActionListener(this);
		}
		return jButtonDatabase;
	}

	private JTextField getJTextFieldDatabase() {
		if (jTextFieldDatabase == null) {
			jTextFieldDatabase = new JTextField();
		}
		return jTextFieldDatabase;
	}

	private JLabel getJLabelDatabase() {
		if (jLabelDatabase == null) {
			jLabelDatabase = new JLabel();
			jLabelDatabase.setText("Database");
		}
		return jLabelDatabase;
	}
	
	private MyJFileChooser getFastaChooser() {
		if (this.jFileChooserFasta == null) {
			this.jFileChooserFasta = new MyJFileChooser();
			this.jFileChooserFasta.setFileFilter(new String[] { "fasta" },
			        "fasta file (*.fasta)");
		}
		return jFileChooserFasta;
	}

	private MyJFileChooser getOutputChooser() {
		if (this.jFileChooserOut == null) {
			this.jFileChooserOut = new MyJFileChooser();
			this.jFileChooserOut.setFileFilter(new String[] { "fasta" },
			        "fasta file (*.fasta)");
		}
		return jFileChooserOut;
	}
	
	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setText("Start");
			jButtonStart.addActionListener(this);
		}
		return jButtonStart;
	}

	private JButton getJButtonOutput() {
		if (jButtonOutput == null) {
			jButtonOutput = new JButton();
			jButtonOutput.setText("...");
			jButtonOutput.addActionListener(this);
		}
		return jButtonOutput;
	}

	private JLabel getJLabelOutput() {
		if (jLabelOutput == null) {
			jLabelOutput = new JLabel();
			jLabelOutput.setText("Output");
		}
		return jLabelOutput;
	}
	
	private JTextField getJTextFieldOutput() {
		if (jTextFieldOutput == null) {
			jTextFieldOutput = new JTextField();
		}
		return jTextFieldOutput;
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
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("FastaExpPanel");
				FastaExpFrame content = new FastaExpFrame();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object obj = e.getSource();

		IDecoyReferenceJudger judger = reader.getDecoyJudger();

		try{
			
			if(obj==this.getJButtonClose()){
				this.dispose();
				return;
			}
			
			if(obj==this.getJButtonOutput()){
				int value = this.getOutputChooser().showOpenDialog(this);
				if (value == JFileChooser.APPROVE_OPTION)
					this.getJTextFieldOutput().setText(
					        this.getOutputChooser().getSelectedFile().getAbsolutePath()+".fasta");
				return;
			}
			
			if(obj==this.getJButtonDatabase()){
				int value = this.getFastaChooser().showOpenDialog(this);
				if (value == JFileChooser.APPROVE_OPTION)
					this.getJTextFieldDatabase().setText(
					        this.getFastaChooser().getSelectedFile().getAbsolutePath());
				return;
			}
			
			if(obj==this.getJButtonStart()){
				
				getJButtonStart().setEnabled(false);
				String fasta = this.getJTextFieldDatabase().getText();
				if(fasta == null || fasta.length() == 0) {
					JOptionPane.showMessageDialog(this, "The database is null.", "Error", JOptionPane.ERROR_MESSAGE);
					throw new NullPointerException("The database is null.");
				}
				
				String out = this.getJTextFieldOutput().getText();
				if(out == null || out.length() == 0) {
					JOptionPane.showMessageDialog(this, "The output path is null.", "Error", JOptionPane.ERROR_MESSAGE);
					throw new NullPointerException("The output path is null.");
				}
				
				FastaCreator creator = new FastaCreator(fasta, out, judger);
				creator.getRefs(reader);
				getJButtonStart().setEnabled(true);
				
			}
			
		}catch(Exception ex) {
			
			JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			
		}finally{
			
			getJButtonStart().setEnabled(true);
		}
	}

}
