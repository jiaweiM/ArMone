/* 
 ******************************************************************************
 * File:QModTabelPanel.java * * * Created on 2010-7-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.QModWriter;
import cn.ac.dicp.gp1809.proteome.quant.profile.IO.FeaturesPagedRowGetter;
import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.gui2.ModInfoPanel;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

//VS4E -- DO NOT REMOVE THIS LINE!
public class QModTableFrame extends JFrame implements ActionListener{

	private FeaturesPagedRowGetter lpgetter;
	private ModInfo[] mods;

	private static final long serialVersionUID = 1L;
	private JButton jButtonStart;
	private JButton jButtonClose;
	private JLabel jLabelOutput;
	private JTextField jTextFieldOutput;
	private JButton jButtonOutput;
	private MyJFileChooser outchooser;
	private MyJFileChooser refchooser; 
	private JCheckBox jCheckBoxNoMod;
	private JCheckBox jCheckBoxPro;
	private JTextField jTextFieldPro;
	private JButton jButtonPro;

	private RatioSelectPanel ratioSelectPanel0;
	private ModInfoPanel modInfoPanel0;
	private JProgressBar jProgressBar0;
	private Object [][] objs;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public QModTableFrame() {
		initComponents();
	}

	public QModTableFrame(FeaturesPagedRowGetter lpgetter) {
		this.lpgetter = lpgetter;
		this.mods = lpgetter.getMods();
		this.objs = lpgetter.getRatioModelInfo();
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new GroupLayout());
		add(getModInfoPanel0(), new Constraints(new Leading(0, 450, 10, 10), new Leading(0, 300, 12, 12)));
		add(getJLabelOutput(), new Constraints(new Leading(25, 10, 10), new Leading(573, 10, 10)));
		add(getJTextFieldOutput(), new Constraints(new Leading(125, 220, 12, 12), new Leading(565, 6, 6)));
		add(getJCheckBoxNoMod(), new Constraints(new Leading(25, 6, 6), new Leading(620, 6, 6)));
		add(getJButtonClose(), new Constraints(new Leading(250, 10, 10), new Leading(695, 6, 6)));
		add(getJButtonStart(), new Constraints(new Leading(100, 10, 10), new Leading(695, 6, 6)));
		add(getJCheckBoxPro(), new Constraints(new Leading(23, 8, 8), new Leading(528, 10, 10)));
		add(getJTextFieldPro(), new Constraints(new Leading(125, 220, 12, 12), new Leading(525, 12, 12)));
		add(getJButtonOutput(), new Constraints(new Leading(380, 10, 10), new Leading(565, 12, 12)));
		add(getJButtonPro(), new Constraints(new Leading(380, 10, 10), new Leading(525, 12, 12)));
		add(getRatioSelectPanel0(), new Constraints(new Bilateral(0, 0, 6, 6), new Leading(280, 220, 6, 6)));
		add(getJProgressBar0(), new Constraints(new Leading(25, 392, 6, 6), new Leading(660, 10, 10)));
		setSize(450, 740);
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

	private JProgressBar getJProgressBar0() {
		if (jProgressBar0 == null) {
			jProgressBar0 = new JProgressBar();
		}
		return jProgressBar0;
	}

	private JButton getJButtonPro() {
		if (jButtonPro == null) {
			jButtonPro = new JButton();
			jButtonPro.setText("...");
			jButtonPro.setEnabled(false);
			jButtonPro.addActionListener(this);
		}
		return jButtonPro;
	}

	private JTextField getJTextFieldPro() {
		if (jTextFieldPro == null) {
			jTextFieldPro = new JTextField();
			jTextFieldPro.setText("");
			jTextFieldPro.setEditable(false);
		}
		return jTextFieldPro;
	}

	private JCheckBox getJCheckBoxPro() {
		if (jCheckBoxPro == null) {
			jCheckBoxPro = new JCheckBox();
			jCheckBoxPro.setSelected(false);
			jCheckBoxPro.setText("Ref. result");
			jCheckBoxPro.addActionListener(this);
		}
		return jCheckBoxPro;
	}


	private ModInfoPanel getModInfoPanel0() {
		if (modInfoPanel0 == null) {
			if(this.mods==null)
				modInfoPanel0 = new ModInfoPanel();
			else
				modInfoPanel0 = new ModInfoPanel(mods);
		}
		return modInfoPanel0;
	}

	private JCheckBox getJCheckBoxNoMod() {
		if (jCheckBoxNoMod == null) {
			jCheckBoxNoMod = new JCheckBox();
			jCheckBoxNoMod.setText("Use no_mod peptide");
			jCheckBoxNoMod.setSelected(true);
		}
		return jCheckBoxNoMod;
	}

	private MyJFileChooser getOutchooser() {
		if (this.outchooser == null) {
			this.outchooser = new MyJFileChooser(lpgetter.getFile());
			this.outchooser.setFileFilter(new String[] { "xls" },
				"Mod site quantitation result file (*.xls)");
		}
		return outchooser;
	}

	private MyJFileChooser getRefchooser() {
		if (this.refchooser == null) {
			this.refchooser = new MyJFileChooser(lpgetter.getFile());
			this.refchooser.setFileFilter(new String[] { "pxml" },
				"Referenced protein quantitation result file (*.pxml)");
		}
		return refchooser;
	}

	private JButton getJButtonOutput() {
		if (jButtonOutput== null) {
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
				QModTableFrame frame = new QModTableFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("QModTableFrame");
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
		
		if(obj==this.jCheckBoxPro){
			if(this.jCheckBoxPro.isSelected()){
				this.jButtonPro.setEnabled(true);
				this.jTextFieldPro.setEditable(true);
			}else{
				this.jButtonPro.setEnabled(false);
				this.jTextFieldPro.setEditable(false);
			}
			return;
		}
		
		if(obj==this.jButtonPro){
			int value = this.getRefchooser().showOpenDialog(this);
			if (value == JFileChooser.APPROVE_OPTION){
				this.getJTextFieldPro().setText(
						this.getRefchooser().getSelectedFile().getAbsolutePath());
			}
			return;
		}

		if(obj==this.getJButtonOutput()){
			int value = this.getOutchooser().showOpenDialog(this);
			if (value == JFileChooser.APPROVE_OPTION)
				this.getJTextFieldOutput().setText(
				        this.getOutchooser().getSelectedFile().getAbsolutePath()+".xls");
			return;
		}
		
		if(obj==this.getJButtonStart()){

			String output = this.getJTextFieldOutput().getText();
			if(output == null || output.length() == 0) {
				JOptionPane.showMessageDialog(this, "The output path have not been set.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			ModInfo [] mods = this.modInfoPanel0.getMods();
			boolean noModPep = this.getJCheckBoxNoMod().isSelected();
			int [] select = this.ratioSelectPanel0.getSelect();
			double [] theoryRatio = this.ratioSelectPanel0.getTheRatio();
			double [] usedTheoryRatio = this.ratioSelectPanel0.getUsedTheRatio();
			String [] ratioNames = this.ratioSelectPanel0.getRatioNames();
			boolean isNormal = this.ratioSelectPanel0.isNormal();
			boolean relaProRatio = this.getJCheckBoxPro().isSelected();
			String proRatio = null;
			if(relaProRatio){
				
				proRatio = this.getJTextFieldPro().getText();
				if(proRatio == null || proRatio.length() == 0) {
					JOptionPane.showMessageDialog(this, "The reference result file have not been set.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			this.jButtonStart.setEnabled(false);
			try {
				QModTaskThread thread = new QModTaskThread(lpgetter, output, mods,
						noModPep, isNormal, relaProRatio, proRatio, ratioNames, 
						select, theoryRatio, usedTheoryRatio, jProgressBar0, this);
				
				thread.start();
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}

			return;
		}		
	}
	
	private class QModTaskThread extends Thread {
		
		private FeaturesPagedRowGetter getter;
		private String output;
		private ModInfo [] mods;
		private boolean noModPep;
		private boolean normal;
		
		private boolean relaProRatio;
		private String proRatio;
		
		private String [] ratioNames;
		private int [] outputids;
		private double [] theoryRatio;
		private double [] usedTheoryRatio;
		
		private JProgressBar jProgressBar0;
		private QModTableFrame frame;

		private QModTaskThread(FeaturesPagedRowGetter getter, String output, ModInfo [] mods,
				boolean noModPep, boolean normal, boolean relaProRatio, String proRatio, 
				String [] ratioNames, int [] outputids, double [] theoryRatio, double [] usedTheoryRatio,
				JProgressBar jProgressBar0, QModTableFrame frame){
			
			this.getter = getter;
			this.output = output;
			this.mods = mods;
			this.noModPep = noModPep;
			this.normal = normal;
			this.relaProRatio = relaProRatio;
			this.proRatio = proRatio;
			
			this.ratioNames = ratioNames;
			this.outputids = outputids;
			this.theoryRatio = theoryRatio;
			this.usedTheoryRatio = usedTheoryRatio;
			
			this.jProgressBar0 = jProgressBar0;
			this.frame = frame;
		}
		
		public void run(){

			jProgressBar0.setStringPainted(true);
			jProgressBar0.setString("Processing...");
			jProgressBar0.setIndeterminate(true);

			try {

				QModWriter writer = new QModWriter(output, relaProRatio, proRatio, getter, mods,
						noModPep, normal, ratioNames, outputids, theoryRatio, usedTheoryRatio);

				writer.write();
				writer.close();

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
