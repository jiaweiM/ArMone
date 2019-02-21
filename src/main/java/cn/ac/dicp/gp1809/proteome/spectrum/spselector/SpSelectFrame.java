/* 
 ******************************************************************************
 * File:SpSelectFrame.java * * * Created on 2010-6-7
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.spselector;

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

import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

//VS4E -- DO NOT REMOVE THIS LINE!
public class SpSelectFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel jLabelMZs;
	private JLabel jLabelPeakFile;
	private JLabel jLabelOutput;
	private JTextField jTextFieldMZs;
	private JTextField jTextFieldPeakfile;
	private JTextField jTextFieldOutput;
	private JButton jButtonMZs;
	private JButton jButtonPeakfile;
	private JButton jButtonOutput;
	private JButton jButtonStart;
	private MyJFileChooser output;
	private MyJFileChooser peakfile;
	private MyJFileChooser mzlist;
	private JLabel jLabelMzTole;
	private JTextField jTextFieldMzTole;
	private JLabel jLabel0;
	private JTextField jTextFieldIntenThres;
	private JLabel jLabelDa;
	private JLabel jLabelPer;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public SpSelectFrame() {
		initComponents();
	}

	private void initComponents() {
		setTitle("SPSelector");
		setLayout(new GroupLayout());
		add(getJLabelMZs(), new Constraints(new Leading(30, 10, 10), new Leading(36, 10, 10)));
		add(getJLabelPeakFile(), new Constraints(new Leading(30, 10, 10), new Leading(72, 10, 10)));
		add(getJLabelOutput(), new Constraints(new Leading(30, 10, 10), new Leading(108, 10, 10)));
		add(getJTextFieldMZs(), new Constraints(new Leading(110, 120, 10, 10), new Leading(36, 10, 10)));
		add(getJTextFieldPeakfile(), new Constraints(new Leading(110, 120, 10, 10), new Leading(72, 10, 10)));
		add(getJTextFieldOutput(), new Constraints(new Leading(110, 120, 10, 10), new Leading(108, 10, 10)));
		add(getJButtonMZs(), new Constraints(new Leading(260, 10, 10), new Leading(36, 10, 10)));
		add(getJButtonPeakfile(), new Constraints(new Leading(260, 10, 10), new Leading(72, 10, 10)));
		add(getJButtonOutput(), new Constraints(new Leading(260, 10, 10), new Leading(108, 10, 10)));
		add(getJLabelMzTole(), new Constraints(new Leading(30, 6, 6), new Leading(156, 6, 6)));
		add(getJTextFieldMzTole(), new Constraints(new Leading(114, 32, 10, 10), new Leading(151, 6, 6)));
		add(getJLabel0(), new Constraints(new Leading(30, 6, 6), new Leading(195, 10, 10)));
		add(getJTextFieldIntenThres(), new Constraints(new Leading(152, 36, 6, 6), new Leading(192, 6, 6)));
		add(getJLabelDa(), new Constraints(new Leading(156, 10, 10), new Leading(157, 6, 6)));
		add(getJLabelPer(), new Constraints(new Leading(197, 10, 10), new Leading(198, 6, 6)));
		add(getJButtonStart(), new Constraints(new Leading(241, 10, 10), new Leading(170, 10, 10)));
		setSize(320, 286);
	}

	private JLabel getJLabelPer() {
		if (jLabelPer == null) {
			jLabelPer = new JLabel();
			jLabelPer.setText("%");
		}
		return jLabelPer;
	}

	private JLabel getJLabelDa() {
		if (jLabelDa == null) {
			jLabelDa = new JLabel();
			jLabelDa.setText("Da");
		}
		return jLabelDa;
	}

	private JTextField getJTextFieldIntenThres() {
		if (jTextFieldIntenThres == null) {
			jTextFieldIntenThres = new JTextField();
			jTextFieldIntenThres.setText("20");
		}
		return jTextFieldIntenThres;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Intensity Threshold");
		}
		return jLabel0;
	}

	private JTextField getJTextFieldMzTole() {
		if (jTextFieldMzTole == null) {
			jTextFieldMzTole = new JTextField();
			jTextFieldMzTole.setText("1");
		}
		return jTextFieldMzTole;
	}

	private JLabel getJLabelMzTole() {
		if (jLabelMzTole == null) {
			jLabelMzTole = new JLabel();
			jLabelMzTole.setText("MZ Tolerance");
		}
		return jLabelMzTole;
	}

	private MyJFileChooser getOutputChooser() {
		if (this.output == null) {
			this.output = new MyJFileChooser();
			this.output.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		return output;
	}
	
	private MyJFileChooser getMZlistChooser() {
		if (this.mzlist == null) {
			this.mzlist = new MyJFileChooser();
			this.mzlist.setFileFilter(new String[] { "txt" },
			        "Tab delimated txt file (*.txt)");
		}
		return mzlist;
	}
	
	private MyJFileChooser getPeakfileChooser() {
		if (this.peakfile == null) {
			this.peakfile = new MyJFileChooser();
			this.peakfile.setFileFilter(new String[] { "mzxml" },
					" MzXML file (*.mzxml)");
		}
		return peakfile;
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

	private JButton getJButtonPeakfile() {
		if (jButtonPeakfile == null) {
			jButtonPeakfile = new JButton();
			jButtonPeakfile.setText("...");
			jButtonPeakfile.addActionListener(this);
		}
		return jButtonPeakfile;
	}

	private JButton getJButtonMZs() {
		if (jButtonMZs == null) {
			jButtonMZs = new JButton();
			jButtonMZs.setText("...");
			jButtonMZs.addActionListener(this);
		}
		return jButtonMZs;
	}

	private JTextField getJTextFieldOutput() {
		if (jTextFieldOutput == null) {
			jTextFieldOutput = new JTextField();
		}
		return jTextFieldOutput;
	}

	private JTextField getJTextFieldPeakfile() {
		if (jTextFieldPeakfile == null) {
			jTextFieldPeakfile = new JTextField();
		}
		return jTextFieldPeakfile;
	}

	private JTextField getJTextFieldMZs() {
		if (jTextFieldMZs == null) {
			jTextFieldMZs = new JTextField();
		}
		return jTextFieldMZs;
	}

	private JLabel getJLabelOutput() {
		if (jLabelOutput == null) {
			jLabelOutput = new JLabel();
			jLabelOutput.setText("Output");
		}
		return jLabelOutput;
	}

	private JLabel getJLabelPeakFile() {
		if (jLabelPeakFile == null) {
			jLabelPeakFile = new JLabel();
			jLabelPeakFile.setText("Peak File");
		}
		return jLabelPeakFile;
	}

	private JLabel getJLabelMZs() {
		if (jLabelMZs == null) {
			jLabelMZs = new JLabel();
			jLabelMZs.setText("MZ List");
		}
		return jLabelMZs;
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
				SpSelectFrame frame = new SpSelectFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("SpSelectFrame");
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
		
		try{
			if(obj==this.getJButtonMZs()){
				int value = this.getMZlistChooser().showOpenDialog(this);
				if (value == JFileChooser.APPROVE_OPTION)
					this.getJTextFieldMZs().setText(
					        this.getMZlistChooser().getSelectedFile().getAbsolutePath());
				return;
			}			
			if(obj==this.getJButtonOutput()){
				int value = this.getOutputChooser().showOpenDialog(this);
				if (value == JFileChooser.APPROVE_OPTION)
					this.getJTextFieldOutput().setText(
					        this.getOutputChooser().getSelectedFile().getAbsolutePath());
				return;
			}
			if(obj==this.getJButtonPeakfile()){
				int value = this.getPeakfileChooser().showOpenDialog(this);
				if (value == JFileChooser.APPROVE_OPTION)
					this.getJTextFieldPeakfile().setText(
					        this.getPeakfileChooser().getSelectedFile().getAbsolutePath());
				return;
			}
			if(obj==this.getJButtonStart()){
				
				this.getJButtonStart().setEnabled(false);
				String outStr = this.getJTextFieldOutput().getText();
				if(outStr == null || outStr.length() == 0) {
					throw new NullPointerException("The output path is null.");
				}
				
				String mzStr = this.getJTextFieldMZs().getText();
				if(mzStr == null || mzStr.length() == 0) {
					throw new NullPointerException("The MZ List file path is null.");
				}
				
				String peakStr = this.getJTextFieldPeakfile().getText();
				if(peakStr == null || peakStr.length() == 0) {
					throw new NullPointerException("The peak file path is null.");
				}
				
				double mzTole = Double.parseDouble(this.getJTextFieldMzTole().getText());
				double intenThres = Double.parseDouble(this.getJTextFieldIntenThres().getText())/100.0;
				
				SpSelector selector = new SpSelector(mzStr, peakStr, outStr, intenThres, mzTole);				
				selector.write();
				this.getJButtonStart().setEnabled(true);
			}
			
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

}
