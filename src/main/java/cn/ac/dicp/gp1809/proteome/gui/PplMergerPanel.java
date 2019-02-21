/* 
 ******************************************************************************
 * File: PplMergerPanel.java * * * Created on 05-04-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PplMergeTask;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import cn.ac.dicp.gp1809.util.gui.ProgressControllerDialog;
import cn.ac.dicp.gp1809.util.gui.UIutilities;
import cn.ac.dicp.gp1809.util.ioUtil.SimpleFilenameChecker;
import cn.ac.dicp.gp1809.util.progress.ControlableTaskProgress;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-04-2009, 20:36:59
 */
public class PplMergerPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private MyJFileChooser pplchooser;

	private JList jListInputs;
	private JScrollPane jScrollPane0;
	private JPanel jPanel0;
	private JPanel jPanel1;
	private JButton jButtonOutput;
	private JTextField jTextFieldOutput;
	private JButton jButton2;
	private JPanel jPanel2;
	private JButton jButtonAddInput;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public PplMergerPanel() {
		initComponents();
	}

	private void initComponents() {
    	setLayout(new GroupLayout());
    	add(getJPanel2(), new Constraints(new Bilateral(6, 7, 83), new Trailing(6, 32, 10, 345)));
    	add(getJPanel1(), new Constraints(new Bilateral(6, 7, 650), new Trailing(50, 79, 10, 254)));
    	add(getJPanel0(), new Constraints(new Bilateral(6, 6, 0), new Bilateral(6, 141, 10)));
    	setSize(663, 383);
    }

	/**
	 * @return the outputchooser
	 */
	private MyJFileChooser getPplchooser() {
		if (this.pplchooser == null) {
			this.pplchooser = new MyJFileChooser();
			this.pplchooser.setFileFilter(new String[] { "ppl" },
			        "Peptide list file (*.ppl)");
			this.pplchooser.setMultiSelectionEnabled(true);
		}
		return pplchooser;
	}

	private JButton getJButtonAddInput() {
		if (jButtonAddInput == null) {
			jButtonAddInput = new JButton();
			jButtonAddInput.setText("   Add   ");
			jButtonAddInput.addActionListener(this);
		}
		return jButtonAddInput;
	}

	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jPanel2.add(getJButton2());
		}
		return jPanel2;
	}

	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("   start   ");
			jButton2.addActionListener(this);
		}
		return jButton2;
	}

	private JTextField getJTextFieldOutput() {
		if (jTextFieldOutput == null) {
			jTextFieldOutput = new JTextField();
		}
		return jTextFieldOutput;
	}

	private JButton getJButtonOutput() {
		if (jButtonOutput == null) {
			jButtonOutput = new JButton();
			jButtonOutput.setText("<<");
			jButtonOutput.addActionListener(this);
		}
		return jButtonOutput;
	}

	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1
			        .setBorder(BorderFactory.createTitledBorder(null, "Output",
			                TitledBorder.LEADING, TitledBorder.ABOVE_TOP,
			                new Font("SansSerif", Font.BOLD, 12), new Color(59,
			                        59, 59)));
			jPanel1.setLayout(new GroupLayout());
			jPanel1.add(getJButtonOutput(), new Constraints(new Trailing(0, 49,
			        24, 569), new Leading(0, 6, 6)));
			jPanel1.add(getJTextFieldOutput(), new Constraints(new Bilateral(0,
			        65, 557), new Leading(0, 6, 6)));
		}
		return jPanel1;
	}

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.setBorder(BorderFactory.createTitledBorder(null, "Ppl for merge", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif",
    				Font.BOLD, 12), new Color(59, 59, 59)));
    		jPanel0.setLayout(new GroupLayout());
    		jPanel0.add(getJScrollPane0(), new Constraints(new Bilateral(-1, 0, 25), new Bilateral(-1, 37, 10, 154)));
    		jPanel0.add(getJButtonAddInput(), new Constraints(new Trailing(6, 6, 6), new Trailing(1, 68, 177)));
    	}
    	return jPanel0;
    }

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getJListInputs());
		}
		return jScrollPane0;
	}

	private JList getJListInputs() {
		if (jListInputs == null) {
			jListInputs = new JList();
			DefaultListModel listModel = new DefaultListModel();
			jListInputs.setModel(listModel);

			JPopupMenu menu = UIutilities.setPopupMenu(jListInputs);
			JMenuItem mitem = new JMenuItem("Remove file");
			mitem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int[] inds = jListInputs.getSelectedIndices();
					if(inds.length > 0) {
						DefaultListModel model = ((DefaultListModel)jListInputs.getModel());
						for (int i= inds.length-1; i>=0; i--) {
							model.remove(inds[i]);
						}
					}
				}

			});
			menu.add(mitem);

			mitem = new JMenuItem("Clear files");
			mitem.addActionListener(new ActionListener() {

				@Override
                public void actionPerformed(ActionEvent e) {
					DefaultListModel model = ((DefaultListModel)jListInputs.getModel());
					model.removeAllElements();
                }
				
			});
			menu.add(mitem);
		}
		return jListInputs;
	}

	/**
	 * Add the file to the list
	 * 
	 * @param path
	 * @return
	 */
	private boolean addFiletoList(String path) {

		DefaultListModel model = ((DefaultListModel) this.getJListInputs()
		        .getModel());

		int size = model.getSize();

		for (int i = 0; i < size; i++) {
			if (path.equals(model.get(i))) {
				return false;
			}
		}
		model.addElement(path);

		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {

			Object obj = e.getSource();

			if (obj == this.getJButtonAddInput()) {
				int ap = this.getPplchooser().showOpenDialog(this);
				if (ap == JFileChooser.APPROVE_OPTION) {
					
					File[] files = this.getPplchooser().getSelectedFiles();
			        
					for(File file : files)
					this.addFiletoList(file.getAbsolutePath());
				}

				return;
			}

			if (obj == this.getJButtonOutput()) {
				int ap = this.getPplchooser().showSaveDialog(this);
				if (ap == JFileChooser.APPROVE_OPTION) {
					this.getJTextFieldOutput().setText(SimpleFilenameChecker.check(this.getPplchooser().getSelectedFile()
			                .getAbsolutePath(), "ppl"));

				}

				return;
			}


			if (obj == this.getJButton2()) {
				DefaultListModel model = (DefaultListModel) this
				        .getJListInputs().getModel();
				int size = model.getSize();

				if (size <= 1) {
					JOptionPane
					        .showMessageDialog(
					                this,
					                "More than one peptide list file should be selected",
					                "Error", JOptionPane.ERROR_MESSAGE);
				} else {

					String output = this.getJTextFieldOutput().getText();
					if (output.length() == 0) {
						JOptionPane.showMessageDialog(this,
						        "Select the output first!", "Error",
						        JOptionPane.ERROR_MESSAGE);

						return;
					}

					String[] files = new String[size];

					for (int i = 0; i < size; i++) {
						files[i] = (String) model.get(i);
					}

					ITask task = new PplMergeTask(output, files, false);

					ControlableTaskProgress progress = new ControlableTaskProgress(
					        new ITask[] { task });
					progress.begin();
					ProgressControllerDialog dlg = new ProgressControllerDialog(this, progress, false);
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
			        JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

}
