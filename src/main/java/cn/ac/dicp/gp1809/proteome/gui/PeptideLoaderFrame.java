/* 
 ******************************************************************************
 * File: PeptideLoaderPanel.java * * * Created on 04-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.gui.Criterias.CriteriaPanel;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

/**
 * 
 * @author Xinning
 * @version 0.1.1, 09-14-2010, 16:47:47
 */
public class PeptideLoaderFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private MyJFileChooser pplChooser;
	private int peptidePTMType;

	private JTextField jTextFieldOpen;
	private JButton jButtonOpen;
	private JPanel jPanel1;
	private CriteriaPanel criteriaPanel0;
	private JPanel jPanel0;
	private JSpinner jSpinnerTopN;
	private JLabel jLabel0;
	private JButton jButtonLoad;
	private JPanel jPanel2;
	private JCheckBox jCheckBoxUseFilters;
	private File currentFile;
	
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public PeptideLoaderFrame() {
		this(null);
	}
	
	public PeptideLoaderFrame(Container parent) {
		this(parent, 0);
	}
	
	public PeptideLoaderFrame(Container parent, int peptidePTMType) {
		this.peptidePTMType = peptidePTMType;
		initComponents();
		setLocationRelativeTo(parent);
	}
	
	private void initComponents() {
    	setTitle("Load peptides ...");
    	setResizable(false);
    	setLayout(new GroupLayout());
    	add(getJPanel2(), new Constraints(new Bilateral(287, 300, 78), new Trailing(6, 393, 393)));
    	add(getJPanel1(), new Constraints(new Bilateral(6, 6, 650), new Leading(14, 78, 10, 10)));
    	add(getJPanel0(), new Constraints(new Bilateral(6, 6, 734), new Bilateral(101, 50, 10, 345)));
    	setSize(770, 570);
    }

	private JCheckBox getJCheckBoxUseFilters() {
    	if (jCheckBoxUseFilters == null) {
    		jCheckBoxUseFilters = new JCheckBox();
    		jCheckBoxUseFilters.setSelected(true);
    		jCheckBoxUseFilters.setText("Use filters");
    	}
    	return jCheckBoxUseFilters;
    }

	/**
	 * @return the peptide list file chooser
	 */
	private MyJFileChooser getPplChooser() {
		if (this.pplChooser == null) {
			this.pplChooser = new MyJFileChooser();
			this.pplChooser.setFileFilter(new String[] { "ppl" },
			        "Peptide list file (*.ppl)");
		}
		return pplChooser;
	}

	private JPanel getJPanel2() {
    	if (jPanel2 == null) {
    		jPanel2 = new JPanel();
    		jPanel2.add(getJButtonLoad());
    	}
    	return jPanel2;
    }

	private JButton getJButtonLoad() {
		if (jButtonLoad == null) {
			jButtonLoad = new JButton();
			jButtonLoad.setText("Load ...");
			jButtonLoad.addActionListener(this);
		}
		return jButtonLoad;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Display n top matched peptides");
		}
		return jLabel0;
	}

	private JSpinner getJSpinnerTopN() {
		if (jSpinnerTopN == null) {
			jSpinnerTopN = new JSpinner();
			SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 25, 1);
			jSpinnerTopN.setModel(model);
		}
		return jSpinnerTopN;
	}

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.setBorder(BorderFactory.createTitledBorder("Parameters"));
    		jPanel0.setLayout(new GroupLayout());
    		jPanel0.add(getJSpinnerTopN(), new Constraints(new Leading(217, 109, 10, 10), new Trailing(5, 25, 10, 202)));
    		jPanel0.add(getJLabel0(), new Constraints(new Leading(10, 178, 6, 6), new Trailing(8, 165, 300)));
    		jPanel0.add(getJCheckBoxUseFilters(), new Constraints(new Leading(10, 6, 6), new Leading(3, 10, 10)));
    		jPanel0.add(getCriteriaPanel0(), new Constraints(new Bilateral(6, 8, 692), new Bilateral(33, 50, 10, 216)));
    	}
    	return jPanel0;
    }

	private CriteriaPanel getCriteriaPanel0() {
    	if (criteriaPanel0 == null) {
    		criteriaPanel0 = new CriteriaPanel();
    		criteriaPanel0.setBorder(new LineBorder(Color.black, 1, false));
    	}
    	return criteriaPanel0;
    }

	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setBorder(BorderFactory
			        .createTitledBorder("Select a peptide list file (ppl)"));
			jPanel1.setLayout(new GroupLayout());
			jPanel1.add(getJTextFieldOpen(), new Constraints(new Bilateral(98,
			        15, 509), new Leading(0, 6, 6)));
			jPanel1.add(getJButtonOpen(), new Constraints(new Leading(22, 33,
			        33), new Leading(0, 6, 6)));
		}
		return jPanel1;
	}

	private JButton getJButtonOpen() {
		if (jButtonOpen == null) {
			jButtonOpen = new JButton();
			jButtonOpen.setText("Open");
			jButtonOpen.addActionListener(this);
		}
		return jButtonOpen;
	}

	private JTextField getJTextFieldOpen() {
		if (jTextFieldOpen == null) {
			jTextFieldOpen = new JTextField();
		}
		return jTextFieldOpen;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Object obj = e.getSource();
		if (obj == this.getJButtonOpen()) {
			int value = this.getPplChooser().showOpenDialog(this);
			if (value == JFileChooser.APPROVE_OPTION){
				File file = this.getPplChooser().getSelectedFile();
				this.getJTextFieldOpen().setText(file.getAbsolutePath());
				this.currentFile = file.getParentFile();
			}
			return;
		}

		if (obj == this.jButtonLoad) {
			String pplfile = this.jTextFieldOpen.getText();

			if (pplfile.length() == 0)
				JOptionPane.showMessageDialog(this,
				        "Select the peptide list file first!");
			else {
				try {
					int topN = (Integer) this.getJSpinnerTopN().getValue();
					IPeptideCriteria criteria = this.getJCheckBoxUseFilters()
					        .isSelected() ? this.getCriteriaPanel0().getCriteria()
					        : null;
					
					PeptideListPagedRowGettor getter = PeptideLoader.load(pplfile, criteria,
					        topN);
					this.dispose();
					
					PeptideListViewer viewer = null;
					
					viewer = new PeptideListViewer(pplfile, getter, this.peptidePTMType);
					viewer.setLocationRelativeTo(this);
					viewer.setDefaultCloseOperation(this.getDefaultCloseOperation());
					viewer.setVisible(true);
					viewer.setCurrentFile(currentFile);
					
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, ex);
					ex.printStackTrace();
				}
			}
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
	 * Main entry of the class. Note: This class is only created so that you can
	 * easily preview the result at runtime. It is not expected to be managed by
	 * the designer. You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PeptideLoaderFrame frame = new PeptideLoaderFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
