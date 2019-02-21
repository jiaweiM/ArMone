/* 
 ******************************************************************************
 * File: APVPanel.java * * * Created on 03-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import cn.ac.dicp.gp1809.util.gui.ProgressControllerDialog;
import cn.ac.dicp.gp1809.util.gui.UIutilities;
import cn.ac.dicp.gp1809.util.progress.ControlableTaskProgress;
import cn.ac.dicp.gp1809.util.progress.ITaskDetails;

/**
 * 
 * @author Xinning
 * @version 0.1, 03-10-2009, 20:53:50
 */
public class APVPanel extends JPanel implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;
	//The file choosers
	private MyJFileChooser pplchooser, mzDataChooser;

	private JList jListTasks;
	private JScrollPane jScrollPane0;
	private JButton jButtonAddTask;
	private JButton jButtonMS2;
	private JTextField jTextFieldMS2;
	private JPanel jPanel2;
	private JButton jButtonMS3;
	private JTextField jTextFieldMS3;
	private JPanel jPanel3;
	private JPanel jPanel4;
	private JButton jButtonStart;
	private JButton jButtonClose;
	private JPanel jPanel5;
	private JButton jButtonMzData;
	private JTextField jTextFieldMzData;
	private JPanel jPanel6;
	private JPanel jPanel1;
	private JLabel jLabel1;
	private JLabel jLabel0;
	private JFormattedTextField jFormattedTextFieldMinIntens;
	private JFormattedTextField jFormattedTextFieldTolerance;
	private JPanel jPanel7;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JSpinner jSpinner0;
	private JLabel jLabel4;
	private JTextField jTextFieldOutput;
	private JButton jButtonOutput;
	private JPanel jPanel0;
	private JCheckBox jCheckBoxMzData;
	private JCheckBox jCheckBoxMzXML;
	private ButtonGroup buttonGroup1;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public APVPanel() {
		initComponents();
		initialOthers();
	}

	private void initComponents() {
		setLayout(new GroupLayout());
		add(getJPanel5(), new Constraints(new Bilateral(329, 6, 492), new Bilateral(6, 7, 10)));
		add(getJSpinner0(), new Constraints(new Leading(111, 59, 10, 10), new Leading(511, 22, 10, 10)));
		add(getJButtonAddTask(), new Constraints(new Leading(209, 10, 10), new Leading(508, 6, 6)));
		add(getJLabel4(), new Constraints(new Leading(17, 10, 10), new Leading(512, 6, 6)));
		add(getJPanel7(), new Constraints(new Leading(9, 304, 261, 327), new Leading(404, 98, 6, 6)));
		add(getJPanel4(), new Constraints(new Leading(6, 311, 272, 338), new Leading(6, 392, 6, 6)));
		initButtonGroup1();
		setSize(827, 560);
	}

	protected JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText(" Close ");
		}
		return jButtonClose;
	}

	private void initButtonGroup1() {
		buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(getJCheckBoxMzData());
		buttonGroup1.add(getJCheckBoxMzXML());
	}

	private JCheckBox getJCheckBoxMzXML() {
    	if (jCheckBoxMzXML == null) {
    		jCheckBoxMzXML = new JCheckBox();
    		jCheckBoxMzXML.setText("MzXML");
    		jCheckBoxMzXML.addItemListener(this);
    	}
    	return jCheckBoxMzXML;
    }

	private JCheckBox getJCheckBoxMzData() {
    	if (jCheckBoxMzData == null) {
    		jCheckBoxMzData = new JCheckBox();
    		jCheckBoxMzData.setSelected(true);
    		jCheckBoxMzData.setText("MzData");
    	}
    	return jCheckBoxMzData;
    }

	private JPanel getJPanel0() {
    	if (jPanel0 == null) {
    		jPanel0 = new JPanel();
    		jPanel0.setBorder(BorderFactory.createTitledBorder(null, "Select output", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif",
    				Font.BOLD, 12), new Color(59, 59, 59)));
    		jPanel0.setLayout(new GroupLayout());
    		jPanel0.add(getJTextFieldOutput(), new Constraints(new Leading(0, 213, 6, 6), new Leading(0, 24, 6, 6)));
    		jPanel0.add(getJButtonOutput(), new Constraints(new Trailing(0, 33, 24, 225), new Leading(0, 24, 6, 6)));
    	}
    	return jPanel0;
    }

	private JButton getJButtonOutput() {
    	if (jButtonOutput == null) {
    		jButtonOutput = new JButton();
    		jButtonOutput.setText("jButton0");
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

	private JLabel getJLabel4() {
    	if (jLabel4 == null) {
    		jLabel4 = new JLabel();
    		jLabel4.setText("MS/MS/MS count");
    	}
    	return jLabel4;
    }

	private JSpinner getJSpinner0() {
    	if (jSpinner0 == null) {
    		jSpinner0 = new JSpinner();
    		SpinnerNumberModel model = new SpinnerNumberModel(3, 1, 10, 1);
    		jSpinner0.setModel(model);
    	}
    	return jSpinner0;
    }

	private JLabel getJLabel3() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("Da");
		}
		return jLabel3;
	}

	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("+-");
		}
		return jLabel2;
	}

	private JPanel getJPanel7() {
		if (jPanel7 == null) {
			jPanel7 = new JPanel();
			jPanel7.setBorder(BorderFactory
			        .createTitledBorder(null, "Neutral loss peak",
			                TitledBorder.LEADING, TitledBorder.ABOVE_TOP,
			                new Font("SansSerif", Font.BOLD, 12), new Color(59,
			                        59, 59)));
			jPanel7.setLayout(new GroupLayout());
			jPanel7.add(getJLabel1(), new Constraints(
			        new Leading(6, 207, 6, 6), new Leading(27, 6, 6)));
			jPanel7.add(getJLabel0(), new Constraints(new Leading(6, 6, 6),
			        new Leading(3, 6, 6)));
			jPanel7.add(getJLabel2(), new Constraints(new Leading(171, 10, 10),
			        new Leading(0, 6, 6)));
			jPanel7.add(getJFormattedTextFieldTolerance(), new Constraints(
			        new Leading(182, 36, 10, 10), new Leading(-3, 6, 6)));
			jPanel7.add(getJLabel3(), new Constraints(new Leading(224, 6, 6),
			        new Leading(0, 6, 6)));
			jPanel7.add(getJFormattedTextFieldMinIntens(), new Constraints(
			        new Leading(215, 41, 10, 10), new Leading(24, 6, 6)));
		}
		return jPanel7;
	}

	private JFormattedTextField getJFormattedTextFieldTolerance() {
		if (jFormattedTextFieldTolerance == null) {
			jFormattedTextFieldTolerance = new JFormattedTextField(
			        new Double(0.8));
			jFormattedTextFieldTolerance
			        .setHorizontalAlignment(SwingConstants.CENTER);
			jFormattedTextFieldTolerance.setMinimumSize(new Dimension(12, 24));
			jFormattedTextFieldTolerance
			        .setPreferredSize(new Dimension(12, 24));
		}
		return jFormattedTextFieldTolerance;
	}

	private JFormattedTextField getJFormattedTextFieldMinIntens() {
		if (jFormattedTextFieldMinIntens == null) {
			jFormattedTextFieldMinIntens = new JFormattedTextField(new Double(
			        0.5));
			jFormattedTextFieldMinIntens
			        .setHorizontalAlignment(SwingConstants.CENTER);
			jFormattedTextFieldMinIntens.setMinimumSize(new Dimension(12, 24));
			jFormattedTextFieldMinIntens
			        .setPreferredSize(new Dimension(12, 24));
		}
		return jFormattedTextFieldMinIntens;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("MS/MS mass tolerance");
		}
		return jLabel0;
	}

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Minimum neutral loss peak intensity");
		}
		return jLabel1;
	}

	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
			jPanel1.add(getJButtonStart());
			jPanel1.add(getJButtonClose());
		}
		return jPanel1;
	}

	/**
	 * Initial specific components
	 */
	private void initialOthers() {
		this.getPplchooser();
		this.getMzDataChooser();
	}

	/**
	 * @return the dbchooser
	 */
	private MyJFileChooser getPplchooser() {
		if (this.pplchooser == null) {
			this.pplchooser = new MyJFileChooser();
			this.pplchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			this.pplchooser.setFileFilter(new String[] {"ppl"}, "peptide list file (*.ppl)");
		}
		return pplchooser;
	}

	/**
	 * @return the peaklist chooser
	 */
	private MyJFileChooser getMzDataChooser() {
		if (this.mzDataChooser == null) {
			this.mzDataChooser = new MyJFileChooser();
			this.mzDataChooser.setFileFilter(new String[] { "xml", "mzxml", "mzdata" },
			        "MzData, Mzxml file (*.xml, *.mzxml, *.mzdata)");
		}
		return mzDataChooser;
	}

	private JPanel getJPanel6() {
    	if (jPanel6 == null) {
    		jPanel6 = new JPanel();
    		jPanel6.setBorder(BorderFactory.createTitledBorder(null, "Select MzData file", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif",
    				Font.BOLD, 12), new Color(59, 59, 59)));
    		jPanel6.setLayout(new GroupLayout());
    		jPanel6.add(getJTextFieldMzData(), new Constraints(new Leading(0, 213, 6, 6), new Leading(0, 24, 6, 6)));
    		jPanel6.add(getJButtonMzData(), new Constraints(new Leading(219, 33, 6, 6), new Leading(0, 24, 6, 6)));
    		jPanel6.add(getJCheckBoxMzData(), new Constraints(new Leading(0, 6, 6), new Trailing(6, 36, 36)));
    		jPanel6.add(getJCheckBoxMzXML(), new Constraints(new Leading(80, 6, 6), new Trailing(6, 36, 36)));
    	}
    	return jPanel6;
    }

	private JTextField getJTextFieldMzData() {
		if (jTextFieldMzData == null) {
			jTextFieldMzData = new JTextField();
		}
		return jTextFieldMzData;
	}

	private JButton getJButtonMzData() {
		if (jButtonMzData == null) {
			jButtonMzData = new JButton();
			jButtonMzData.setText("Select MzData");
			jButtonMzData.addActionListener(this);
		}
		return jButtonMzData;
	}

	private JPanel getJPanel5() {
		if (jPanel5 == null) {
			jPanel5 = new JPanel();
			jPanel5.setBorder(BorderFactory.createTitledBorder(null, "Tasks", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12),
					new Color(59, 59, 59)));
			jPanel5.setLayout(new GroupLayout());
			jPanel5.add(getJPanel1(), new Constraints(new Bilateral(73, 73, 75), new Trailing(0, 31, 86, 195)));
			jPanel5.add(getJScrollPane0(), new Constraints(new Bilateral(13, 12, 22), new Bilateral(13, 40, 10, 329)));
		}
		return jPanel5;
	}

	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setText("  Start  ");
			jButtonStart.addActionListener(this);
		}
		return jButtonStart;
	}

	private JPanel getJPanel4() {
    	if (jPanel4 == null) {
    		jPanel4 = new JPanel();
    		jPanel4.setBorder(BorderFactory.createTitledBorder(null, "Edit a task", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD,
    				12), new Color(59, 59, 59)));
    		jPanel4.setLayout(new GroupLayout());
    		jPanel4.add(getJPanel2(), new Constraints(new Leading(0, 283, 6, 6), new Leading(0, 74, 10, 10)));
    		jPanel4.add(getJPanel3(), new Constraints(new Leading(0, 283, 6, 6), new Leading(76, 72, 10, 10)));
    		jPanel4.add(getJPanel0(), new Constraints(new Leading(0, 282, 6, 6), new Trailing(6, 73, 237, 237)));
    		jPanel4.add(getJPanel6(), new Constraints(new Leading(0, 282, 6, 6), new Bilateral(154, 85, 76)));
    	}
    	return jPanel4;
    }

	private JPanel getJPanel3() {
    	if (jPanel3 == null) {
    		jPanel3 = new JPanel();
    		jPanel3.setBorder(BorderFactory.createTitledBorder(null, "Select MS3", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD,
    				12), new Color(59, 59, 59)));
    		jPanel3.setLayout(new GroupLayout());
    		jPanel3.add(getJTextFieldMS3(), new Constraints(new Bilateral(0, 41, 12), new Leading(0, 6, 6)));
    		jPanel3.add(getJButtonMS3(), new Constraints(new Trailing(3, 32, 59, 59), new Leading(0, 24, 6, 6)));
    	}
    	return jPanel3;
    }

	private JTextField getJTextFieldMS3() {
		if (jTextFieldMS3 == null) {
			jTextFieldMS3 = new JTextField();
			jTextFieldMS3.setMinimumSize(new Dimension(12, 24));
			jTextFieldMS3.setPreferredSize(new Dimension(12, 24));
		}
		return jTextFieldMS3;
	}

	private JButton getJButtonMS3() {
    	if (jButtonMS3 == null) {
    		jButtonMS3 = new JButton();
    		jButtonMS3.setText("Select MS3");
    		jButtonMS3.addActionListener(this);
    	}
    	return jButtonMS3;
    }

	private JPanel getJPanel2() {
    	if (jPanel2 == null) {
    		jPanel2 = new JPanel();
    		jPanel2.setBorder(BorderFactory.createTitledBorder(null, "Select MS2", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD,
    				12), new Color(59, 59, 59)));
    		jPanel2.setLayout(new GroupLayout());
    		jPanel2.add(getJTextFieldMS2(), new Constraints(new Leading(0, 214, 10, 10), new Leading(0, 6, 6)));
    		jPanel2.add(getJButtonMS2(), new Constraints(new Leading(220, 32, 6, 6), new Leading(0, 24, 6, 6)));
    	}
    	return jPanel2;
    }

	private JTextField getJTextFieldMS2() {
		if (jTextFieldMS2 == null) {
			jTextFieldMS2 = new JTextField();
			jTextFieldMS2.setMinimumSize(new Dimension(12, 24));
			jTextFieldMS2.setPreferredSize(new Dimension(12, 24));
		}
		return jTextFieldMS2;
	}

	private JButton getJButtonMS2() {
    	if (jButtonMS2 == null) {
    		jButtonMS2 = new JButton();
    		jButtonMS2.setText("Select MS2");
    		jButtonMS2.addActionListener(this);
    	}
    	return jButtonMS2;
    }

	private JButton getJButtonAddTask() {
		if (jButtonAddTask == null) {
			jButtonAddTask = new JButton();
			jButtonAddTask.setText("Add a task");
			jButtonAddTask.addActionListener(this);
		}
		return jButtonAddTask;
	}

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getJListTasks());
		}
		return jScrollPane0;
	}

	private JList getJListTasks() {
		if (jListTasks == null) {
			jListTasks = new JList();
			jListTasks.setBackground(new Color(238, 238, 238));
			DefaultListModel listModel = new DefaultListModel();
			jListTasks.setModel(listModel);
			
			JPopupMenu menu = UIutilities.setPopupMenu(jListTasks);
			JMenuItem mitem = new JMenuItem("Remove task");
			mitem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int[] inds = jListTasks.getSelectedIndices();
					if (inds.length > 0) {
						DefaultListModel model = ((DefaultListModel) jListTasks
						        .getModel());
						for (int i = inds.length - 1; i >= 0; i--) {
							model.remove(inds[i]);
						}
					}
				}

			});
			menu.add(mitem);

			mitem = new JMenuItem("Clear tasks");
			mitem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					DefaultListModel model = ((DefaultListModel) jListTasks
					        .getModel());
					model.removeAllElements();

				}

			});
			menu.add(mitem);

		}
		return jListTasks;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {

			Object obj = e.getSource();
			if (obj == this.getJButtonMS2()) {
				int value = this.pplchooser.showOpenDialog(this);
				if (value == JFileChooser.APPROVE_OPTION)
					this.getJTextFieldMS2()
					        .setText(
					                this.pplchooser.getSelectedFile()
					                        .getAbsolutePath());
				return;
			}

			if (obj == this.getJButtonMS3()) {
				int value = this.pplchooser.showOpenDialog(this);
				if (value == JFileChooser.APPROVE_OPTION)
					this.getJTextFieldMS3()
					        .setText(
					                this.pplchooser.getSelectedFile()
					                        .getAbsolutePath());
				return;
			}
			
			if (obj == this.getJButtonOutput()) {
				this.pplchooser.setSelectedFile(new File("Result.apv.ppl"));
				int value = this.pplchooser.showSaveDialog(this);
				if (value == JFileChooser.APPROVE_OPTION)
					this.getJTextFieldOutput()
					        .setText(
					                this.pplchooser.getSelectedFile()
					                        .getAbsolutePath());
				
				this.pplchooser.setSelectedFile(null);
				return;
			}

			if (obj == this.getJButtonMzData()) {
				int value = this.mzDataChooser.showOpenDialog(this);
				if (value == JFileChooser.APPROVE_OPTION) {
					String name = this.mzDataChooser.getSelectedFile()
					        .getAbsolutePath();
					this.getJTextFieldMzData().setText(name);
				}
				return;
			}

			if (obj == this.getJButtonAddTask()) {

				String ms2 = this.getJTextFieldMS2().getText();
				String ms3 = this.getJTextFieldMS3().getText();
				String mzdata = this.getJTextFieldMzData().getText();
				String output = this.getJTextFieldOutput().getText();
				SpectrumThreshold threshold = new SpectrumThreshold(Double
				        .parseDouble(this.getJFormattedTextFieldTolerance()
				                .getText()), Double.parseDouble(this
				        .getJFormattedTextFieldMinIntens().getText()));
				int msnCount = ((Number)this.getJSpinner0().getValue()).intValue();
				
				
				//The type
				DtaType type;
				
				if(this.jCheckBoxMzData.isSelected()) {
					type = DtaType.MZDATA;
				}
				else if(this.jCheckBoxMzXML.isSelected()) {
					type = DtaType.MZXML;
					msnCount = 1;
				}
				else {
					type = null;
				}
				
				APIVASETaskDetails details = new APIVASETaskDetails(ms2,
				        ms3, mzdata, type, output, threshold, msnCount);

				((DefaultListModel) this.jListTasks.getModel())
				        .addElement(details);

				return;
			}

			if (obj == this.getJButtonStart()) {

				final Object[] taskObjs = this.jListTasks.getSelectedValues();

				if (taskObjs == null || taskObjs.length == 0)
					JOptionPane.showMessageDialog(this, "No task.", "Error",
					        JOptionPane.ERROR_MESSAGE);
				else {
					try {
						int size = taskObjs.length;
						ITaskDetails[] tasks = new ITaskDetails[size];
						for (int i = 0; i < size; i++) {
							tasks[i] = (ITaskDetails) taskObjs[i];
						}

						ControlableTaskProgress progress = new ControlableTaskProgress(
						        tasks);
						progress.begin();
						new ProgressControllerDialog(this, progress, false);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(this, ex.getMessage(),
						        "Error", JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
				}

				return;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
			        JOptionPane.OK_OPTION);
		}
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {

		Object obj = e.getSource();

		try {

			if (obj == this.getJCheckBoxMzXML()) {
				
				if(this.getJCheckBoxMzXML().isSelected()) {
					int apv = JOptionPane.showConfirmDialog(this,
					        "Currently, the MzXML can be used only when the mass "
					                + "\ncircle is set as follow: "
					                + "\n\tMS1|MS2|MS3|MS2|MS3 ...", "Important",
					        JOptionPane.YES_NO_OPTION);

					if (apv == JOptionPane.NO_OPTION) {
						this.getJCheckBoxMzData().setSelected(true);
					}
				}
				
				return ;
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex, "Error",
			        JOptionPane.ERROR_MESSAGE);
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
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("BatchPplCreatorFrame");
				APVPanel content = new APVPanel();
				content.setPreferredSize(content.getSize());
				frame.add(content, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
