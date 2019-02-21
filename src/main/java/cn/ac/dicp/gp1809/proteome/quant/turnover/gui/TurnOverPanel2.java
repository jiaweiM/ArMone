/* 
 ******************************************************************************
 * File: MutilCompPanel2.java * * * Created on 2011-11-14
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.turnover.gui;

import cn.ac.dicp.gp1809.proteome.quant.gui.RatioSelectPanel;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import org.dyno.visual.swing.layouts.*;
import org.dyno.visual.swing.layouts.GroupLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//VS4E -- DO NOT REMOVE THIS LINE!
public class TurnOverPanel2 extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private RatioSelectPanel ratioSelectPanel0;
	private JButton jButtonClose;
	private JButton jButtonStart;
	private JButton jButtonMod;
	private JButton jButtonPre;
	private JProgressBar jProgressBar0;
	private JLabel jLabelOutput;
	private JTextField jTextFieldOutput;
	private JButton jButtonOutput;
	private Object [][] objs;
	private MyJFileChooser outChooser;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	
	public TurnOverPanel2() {
		initComponents();
	}
	
	public TurnOverPanel2(Object [][] objs) {
		this.objs = objs;
		initComponents();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJButtonClose(), new Constraints(new Trailing(30, 10, 10), new Leading(330, 12, 12)));
		add(getJButtonStart(), new Constraints(new Trailing(135, 10, 10), new Leading(330, 12, 12)));
		add(getJButtonMod(), new Constraints(new Trailing(230, 10, 10), new Leading(330, 12, 12)));
		add(getJButtonPre(), new Constraints(new Trailing(360, 10, 10), new Leading(330, 12, 12)));
		add(getRatioSelectPanel0(), new Constraints(new Bilateral(0, 0, 270), new Leading(0, 220, 12, 12)));
		add(getJProgressBar0(), new Constraints(new Bilateral(20, 20, 10, 10), new Leading(290, 10, 10)));
		add(getJLabelOutput(), new Constraints(new Leading(25, 10, 10), new Leading(240, 10, 10)));
		add(getJButtonOutput(), new Constraints(new Leading(450, 10, 10), new Leading(238, 10, 10)));
		add(getJTextFieldOutput(), new Constraints(new Leading(85, 335, 10, 10), new Leading(240, 25, 10, 10)));
		setSize(540, 390);
	}

	public String getOutput(){
		return this.getJTextFieldOutput().getText();
	}
	
	private MyJFileChooser getOutchooser() {
		if (this.outChooser == null) {
			this.outChooser = new MyJFileChooser();
			this.outChooser.setFileFilter(new String[] { "xls" },
			        " Average quantitation result (*.xls)");
		}
		return outChooser;
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

	public JProgressBar getJProgressBar0() {
		if (jProgressBar0 == null) {
			jProgressBar0 = new JProgressBar();
		}
		return jProgressBar0;
	}

	public JButton getJButtonPre() {
		if (jButtonPre == null) {
			jButtonPre = new JButton();
			jButtonPre.setText("Previous");
		}
		return jButtonPre;
	}

	public JButton getJButtonMod() {
		if (jButtonMod == null) {
			jButtonMod = new JButton();
			jButtonMod.setText("ModQuant");
		}
		return jButtonMod;
	}

	public JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setText("Start");
		}
		return jButtonStart;
	}

	public JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText("Close");
		}
		return jButtonClose;
	}

	private RatioSelectPanel getRatioSelectPanel0() {
		if (ratioSelectPanel0 == null) {
			if(objs==null)
				ratioSelectPanel0 = new RatioSelectPanel();
			else
				ratioSelectPanel0 = new RatioSelectPanel(objs);
		}
		return ratioSelectPanel0;
	}
	
	public String [] getRatioNames(){
		return this.ratioSelectPanel0.getRatioNames();
	}
	
	public boolean isNormal(){
		return this.ratioSelectPanel0.isNormal();
	}

	public int [] getSelectRatio(){
		return this.ratioSelectPanel0.getSelect();
	}
	
	public double [] getTheoryRatios(){
		return this.ratioSelectPanel0.getTheRatio();
	}
	
	public double [] getUsedTheoryRatios(){
		return this.ratioSelectPanel0.getUsedTheRatio();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		Object obj = e.getSource();
		
		if(obj==this.getJButtonOutput()){
			
			int value = this.getOutchooser().showOpenDialog(this);
			if (value == JFileChooser.APPROVE_OPTION){
				this.getJTextFieldOutput().setText(this.getOutchooser().getSelectedFile().getAbsolutePath()+".xls");
			}
			
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
				frame.setTitle("MutilCompPanel2");
				TurnOverPanel2 content = new TurnOverPanel2();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
