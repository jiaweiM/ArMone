/* 
 ******************************************************************************
 * File: MatchSpecDrawFrame.java * * * Created on 2011-12-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.drawjf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.glyco.Quan.label.GlycoLPRowGetter;
import cn.ac.dicp.gp1809.glyco.Quan.label.GlycoPeptideLabelPair;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

//VS4E -- DO NOT REMOVE THIS LINE!
public class MatchSpecDrawFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton jButtonStart;
	private JButton jButtonClose;
	private JProgressBar jProgressBar0;
	private JLabel jLabel0;
	private JTextField jTextField0;
	private JButton jButton0;
	private MyJFileChooser output;
	
	private JCheckBox jCheckBoxPdf;
	private JCheckBox jCheckBoxHtml;
	private ButtonGroup buttonGroup1;
	
	private GlycoLPRowGetter lpgetter;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public MatchSpecDrawFrame() {
		initComponents();
	}
	
	public MatchSpecDrawFrame(GlycoLPRowGetter lpgetter) {
		this.lpgetter = lpgetter;
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJLabel0(), new Constraints(new Leading(19, 10, 10), new Leading(36, 10, 10)));
		add(getJTextField0(), new Constraints(new Leading(78, 300, 6, 6), new Leading(32, 6, 6)));
		add(getJProgressBar0(), new Constraints(new Bilateral(20, 20, 10, 10), new Leading(170, 10, 10)));
		add(getJButton0(), new Constraints(new Leading(419, 10, 10), new Leading(32, 6, 6)));
		add(getJButtonClose(), new Constraints(new Leading(260, 10, 10), new Leading(220, 10, 10)));
		add(getJButtonStart(), new Constraints(new Leading(155, 10, 10), new Leading(220, 10, 10)));
		add(getJCheckBoxPdf(), new Constraints(new Leading(20, 6, 6), new Leading(110, 10, 10)));
		add(getJCheckBoxHtml(), new Constraints(new Leading(160, 10, 10), new Leading(110, 6, 6)));
		initButtonGroup1();
		setSize(480, 300);
	}

	private void initButtonGroup1() {
		buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(getJCheckBoxPdf());
		buttonGroup1.add(getJCheckBoxHtml());
	}

	private JCheckBox getJCheckBoxHtml() {
		if (jCheckBoxHtml == null) {
			jCheckBoxHtml = new JCheckBox();
			jCheckBoxHtml.setText("HTML");
			jCheckBoxHtml.addActionListener(this);
		}
		return jCheckBoxHtml;
	}

	private JCheckBox getJCheckBoxPdf() {
		if (jCheckBoxPdf == null) {
			jCheckBoxPdf = new JCheckBox();
			jCheckBoxPdf.setText("PDF");
			jCheckBoxPdf.setSelected(true);
			jCheckBoxPdf.addActionListener(this);
		}
		return jCheckBoxPdf;
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
	
	private JProgressBar getJProgressBar0() {
		if (jProgressBar0 == null) {
			jProgressBar0 = new JProgressBar();
			jProgressBar0.setMaximum(100);
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

	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setText("Start");
			jButtonStart.addActionListener(this);
		}
		return jButtonStart;
	}
	
	private MyJFileChooser getOutputChooser() {
		if (this.output == null) {
			this.output = new MyJFileChooser(lpgetter.getFile());
			this.output.setFileFilter(new String[] { "pdf" },
				"PDF (*.pdf)");
		}
		return output;
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
				MatchSpecDrawFrame frame = new MatchSpecDrawFrame();
				frame
						.setDefaultCloseOperation(MatchSpecDrawFrame.EXIT_ON_CLOSE);
				frame.setTitle("MatchSpecDrawFrame");
				frame.getContentPane().setPreferredSize(frame.getSize());
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

		if(obj==this.getJButtonClose()){
			this.dispose();
			return;
		}
		
		if(obj == this.getJButton0()) {

			boolean ispdf = this.getJCheckBoxPdf().isSelected();
			
			if (ispdf) {
				
				this.getOutputChooser().setFileFilter(new String[] { "pdf" },
					"PDF (*.pdf)");
				
				if (this.getOutputChooser().showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					this.getJTextField0().setText(
					        this.getOutputChooser().getSelectedFile()
					                .getAbsolutePath()+".pdf");
				}
				
			}else{
				
				this.getOutputChooser().setFileFilter(
						new String[] { "html", "htm" }, "HTML (*.htm, *.html)");
				
				if (this.getOutputChooser().showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					this.getJTextField0().setText(
					        this.getOutputChooser().getSelectedFile()
					                .getAbsolutePath()+".html");
				}
			}
			
			
			return ;
		}
		
		if(obj==this.getJCheckBoxPdf()){
			
			if(this.getJCheckBoxPdf().isSelected()){
				
				String out = this.getJTextField0().getText();
				if(out.length()>0){
					
					if(out.endsWith(".html")){
						
						out = out.substring(0, out.length()-5);
						out += ".pdf";
						this.getJTextField0().setText(out);
						
					}else if(out.endsWith(".pdf")){
						
					}else{
						
						out += ".pdf";
						this.getJTextField0().setText(out);
					}
				}
				
			}else{
				
				String out = this.getJTextField0().getText();
				if(out.length()>0){
					
					if(out.endsWith(".html")){
	
					}else if(out.endsWith(".pdf")){
						
						out = out.substring(0, out.length()-4);
						out += ".html";
						this.getJTextField0().setText(out);
						
					}else{
						
						out += ".html";
						this.getJTextField0().setText(out);
					}
				}
			}
			
			this.repaint();
			return;
		}
		
		if(obj==this.getJCheckBoxHtml()){

			if(this.getJCheckBoxPdf().isSelected()){
				
				String out = this.getJTextField0().getText();
				if(out.length()>0){
					
					if(out.endsWith(".html")){
						
						out = out.substring(0, out.length()-5);
						out += ".pdf";
						this.getJTextField0().setText(out);
						
					}else if(out.endsWith(".pdf")){
						
					}else{
						
						out += ".pdf";
						this.getJTextField0().setText(out);
					}
				}
				
			}else{
				
				String out = this.getJTextField0().getText();
				if(out.length()>0){
					
					if(out.endsWith(".html")){
	
					}else if(out.endsWith(".pdf")){
						
						out = out.substring(0, out.length()-4);
						out += ".html";
						this.getJTextField0().setText(out);
						
					}else{
						
						out += ".html";
						this.getJTextField0().setText(out);
					}
				}
			}
			
			this.repaint();
			return;
		
		}
		
		if(obj==this.getJButtonStart()){
			
			String output = this.getJTextField0().getText();
			if(output==null || output.length()==0){
				JOptionPane.showMessageDialog(this, "The output is null.", "Error", JOptionPane.ERROR_MESSAGE);
				throw new NullPointerException("The output is null.");
			}
			
			this.jButtonStart.setEnabled(false);
			
			boolean pdf = this.getJCheckBoxPdf().isSelected();
			JProgressBar bar = this.getJProgressBar0();
			
			try {
				
				WriterThread thread = new WriterThread(lpgetter, output, pdf, bar, this);
				
				thread.start();
				
			}catch (Exception e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}

			return;
		}
		
	}

	private class WriterThread extends Thread {
		
		private String output;
		private boolean pdf;
		private JProgressBar bar;
		private JFrame frame;
		private GlycoLPRowGetter lpgetter;
		
		private WriterThread(GlycoLPRowGetter lpgetter, String output, boolean pdf, 
				JProgressBar bar, JFrame frame){
			
			this.lpgetter = lpgetter;
			this.output = output;
			this.pdf = pdf;
			this.bar = bar;
			this.frame = frame;
		}
		
		public void run(){

			jProgressBar0.setStringPainted(true);
			jProgressBar0.setString("Processing...");
			jProgressBar0.setIndeterminate(true);

			try {
				
				if(pdf){
					
					MatchSpecPdfWriter writer = new MatchSpecPdfWriter(output);
					PeptidePair[] pairs = lpgetter.getAllSelectedFeatures();
					for(int i=0;i<pairs.length;i++){
						
						GlycoPeptideLabelPair glycopair = (GlycoPeptideLabelPair) pairs[i];
						IGlycoPeptide peptide = (IGlycoPeptide) glycopair.getPeptide();
						NGlycoSSM ssm = glycopair.getSSM();
						writer.write(ssm, peptide);
					}
					
					writer.close();
					
				}else{
					
					MatchSpecHtmlWriter writer = new MatchSpecHtmlWriter(output);
					PeptidePair [] pairs = lpgetter.getAllSelectedFeatures();
					for(int i=0;i<pairs.length;i++){
						
						GlycoPeptideLabelPair glycopair = (GlycoPeptideLabelPair) pairs[i];
						IGlycoPeptide peptide = (IGlycoPeptide) pairs[i].getPeptide();
						NGlycoSSM ssm = glycopair.getSSM();
						writer.write(ssm, peptide);
					}
					
					writer.close();
				}

			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
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
