/* 
 ******************************************************************************
 * File:ModInfoFrame.java * * * Created on 2011-9-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
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

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IFilteredPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.ModInfoExcelWriter;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

//VS4E -- DO NOT REMOVE THIS LINE!
public class ModInfoFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private ModInfoPanel modInfoPanel0;
	private JLabel jLabelOutput;
	private JTextField jTextFieldOutput;
	private JButton jButtonOutput;
	private JButton jButtonStart;
	
	private VariModRowGetter getter;
	private MyJFileChooser xlsChooser;
	private File file;
	private IFilteredPeptideListReader reader;
	private JButton jButtonClose;
	
	private JProgressBar jProgressBar0;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	
	public ModInfoFrame() {
		initComponents();
	}
	
	public ModInfoFrame(PeptideListPagedRowGetter2 getter) {
		this.getter = new VariModRowGetter(getter.getSearchParameter().getVariableInfo());
		this.reader = getter.getSelectedPeptideReader();
		initComponents();
	}
	
	public ModInfoFrame(PeptideListPagedRowGetter2 getter, File file) {
		this.getter = new VariModRowGetter(getter.getSearchParameter().getVariableInfo());
		this.reader = getter.getSelectedPeptideReader();
		this.file = file;
		initComponents();
	}
	
	public ModInfoFrame(VariModRowGetter getter, IFilteredPeptideListReader reader) {
		this.getter = getter;
		this.reader = reader;
		initComponents();
	}

	private void initComponents() {
		setFont(new Font("Dialog", Font.PLAIN, 12));
		setBackground(new Color(204, 232, 207));
		setForeground(Color.black);
		setLayout(new GroupLayout());
		add(getModInfoPanel0(), new Constraints(new Bilateral(0, 0, 6, 6), new Bilateral(0, 180, 6, 6)));
		add(getJLabelOutput(), new Constraints(new Leading(23, 10, 10), new Leading(303, 6, 6)));
		add(getJButtonOutput(), new Constraints(new Leading(340, 10, 10), new Leading(300, 26, 6, 6)));
		add(getJTextFieldOutput(), new Constraints(new Leading(86, 221, 10, 10), new Leading(300, 26, 6, 6)));
		add(getJButtonStart(), new Constraints(new Leading(107, 10, 10), new Leading(400, 6, 6)));
		add(getJButtonClose(), new Constraints(new Leading(240, 10, 10), new Leading(400, 6, 6)));
		add(getJProgressBar0(), new Constraints(new Leading(23, 360, 12, 12), new Leading(360, 10, 10)));
		setSize(410, 450);
	}

	private JProgressBar getJProgressBar0() {
		if (jProgressBar0 == null) {
			jProgressBar0 = new JProgressBar(0, 100);			
		}
		return jProgressBar0;
	}

	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText("Close");
			jButtonClose.addActionListener(this);
		}
		return jButtonClose;
	}

	private MyJFileChooser getXlsChooser() {
		if (this.xlsChooser == null) {
			if(file!=null)
				this.xlsChooser = new MyJFileChooser(file);
			else
				this.xlsChooser = new MyJFileChooser();
			
			this.xlsChooser.setFileFilter(new String [] { "xls" },
			        "excel file (*.xls)");
		}
		return xlsChooser;
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

	private JTextField getJTextFieldOutput() {
		if (jTextFieldOutput == null) {
			jTextFieldOutput = new JTextField();
			jTextFieldOutput.setText("");
		}
		return jTextFieldOutput;
	}

	private JLabel getJLabelOutput() {
		if (jLabelOutput == null) {
			jLabelOutput = new JLabel();
			jLabelOutput.setText("Output");
		}
		return jLabelOutput;
	}

	private ModInfoPanel getModInfoPanel0() {
		if (modInfoPanel0 == null) {
			if(getter==null)
				modInfoPanel0 = new ModInfoPanel();
			else
				modInfoPanel0 = new ModInfoPanel(getter);
		}
		return modInfoPanel0;
	}
	
	public char getSymbol(){
		return modInfoPanel0.getSymbol();
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
				ModInfoFrame dialog = new ModInfoFrame();
				dialog.setDefaultCloseOperation(ModInfoFrame.DISPOSE_ON_CLOSE);
				dialog.setTitle("ModInfoDialog");
				dialog.setLocationRelativeTo(null);
				dialog.getContentPane().setPreferredSize(dialog.getSize());
				dialog.pack();
				dialog.setVisible(true);
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
		
		if(obj==this.getJButtonOutput()){
			int value = this.getXlsChooser().showOpenDialog(this);
			if(value==MyJFileChooser.APPROVE_OPTION){
				
				String file = this.getXlsChooser().getSelectedFile().getAbsolutePath();
				
				if(!file.toLowerCase().endsWith("xls"))
					file += ".xls";
				
				this.getJTextFieldOutput().setText(file);
			}
			return;
		}

		if(obj==this.getJButtonStart()){

			char symbol = modInfoPanel0.getSymbol();
			
			if(symbol=='\u0000'){
				JOptionPane.showMessageDialog(this, "Variable mod have not been selected.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			String name = modInfoPanel0.getModName();
			String sites = modInfoPanel0.getSites();
			
			String file = this.getJTextFieldOutput().getText();
			
			if(file==null || file.length()==0){
				JOptionPane.showMessageDialog(this, "The output path have not been set.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			this.jButtonStart.setEnabled(false);
			
			try{
				
				ModWriterThread thread = new ModWriterThread(file, reader, name, symbol, sites, jProgressBar0, this);
				thread.start();
				
//				ModInfoExcelWriter writer = new ModInfoExcelWriter(file, reader, name, symbol, sites);
//				writer.write();
					
			}catch(Exception e1){
				JOptionPane.showMessageDialog(this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}

			return;
		}
		
		if(obj==this.getJButtonClose()){
			this.dispose();
			return;
		}
	}
	
	private class ModWriterThread extends Thread {
		
		private String file;
		private IFilteredPeptideListReader reader;
		private String name;
		private char symbol;
		private String sites;
		private JProgressBar jProgressBar0;
		private ModInfoFrame frame;

		ModWriterThread(String file, IFilteredPeptideListReader reader,
				String name, char symbol, String sites, JProgressBar jProgressBar0, ModInfoFrame frame){
			
			this.file = file;
			this.reader = reader;
			this.name = name;
			this.symbol = symbol;
			this.sites = sites;
			this.jProgressBar0 = jProgressBar0;
			this.frame = frame;
		}
		
		public void run(){

			jProgressBar0.setStringPainted(true);
			jProgressBar0.setString("Processing...");
			jProgressBar0.setIndeterminate(true);
			
			ModInfoExcelWriter writer = null;

			try {
				
				writer
					= new ModInfoExcelWriter(file, reader, name, symbol, sites);
				
				writer.write();		
				
			} catch(Exception e) {
				
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				
			} finally {
			
				frame.dispose();
			}
			
			jProgressBar0.setString("Complete");
			try {
				sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			frame.dispose();
		}
		
	}

}
