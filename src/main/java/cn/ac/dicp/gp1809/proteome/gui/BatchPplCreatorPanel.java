/*
 ******************************************************************************
 * File: BatchPplCreatorPanel.java * * * Created on 03-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotDBPattern;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotDBPatterns;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.sequest.zipdata.ZippedDtaOutUltility;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DecoyDBHelper;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.GlobalDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.util.Parameters;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import cn.ac.dicp.gp1809.util.gui.ProgressControllerDialog;
import cn.ac.dicp.gp1809.util.gui.UIutilities;
import cn.ac.dicp.gp1809.util.progress.ControlableTaskProgress;
import cn.ac.dicp.gp1809.util.progress.ITaskDetails;
import org.dyno.visual.swing.layouts.*;
import org.dyno.visual.swing.layouts.GroupLayout;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

/**
 * @author Xinning
 * @version 0.1, 03-10-2009, 20:53:50
 */
public class BatchPplCreatorPanel extends JPanel implements ActionListener, ItemListener
{
    private static final long serialVersionUID = 1L;
    private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
    // The file choosers
    private MyJFileChooser dbchooser, filechooser, outputchooser, peakListChooser;
    private Parameters pplcrParam;
    private JButton jButtonAddTask;
    private JComboBox jComboBoxType;
    private JPanel jPanelAlgorithms;
    private JButton jButtondb;
    private JTextField jTextFielddb;
    private JPanel jPanelDatabase;
    private JButton jButtonSource;
    private JTextField jTextFieldSource;
    private JPanel jPanelSource;
    private JComboBox jComboBoxMascotPattern;
    private JPanel jPanelMascotAcc;
    private JButton jButtonStart;
    private JPanel jPanel5;
    private JButton jButtonOutput;
    private JTextField jTextFieldOutput;
    private JPanel jPanelOutput;
    private JSpinner jSpinnerTopN;
    private JPanel jPanelPeaklist;
    private JLabel jLabel0;
    private JButton jButtonPeakList;
    private JTextField jTextFieldPeakList;
    private JPanel jPanel0;
    private JPanel jPanel1;
    private JList jListTasks;
    private JScrollPane jScrollPane0;
    private JCheckBox jCheckBoxMzXML;
    private ButtonGroup buttonGroup1;
    private JCheckBox jCheckBoxMzData;
    private JCheckBox jCheckBoxEmbPeaks;
    private JPanel jPanel2;
    private JCheckBox jCheckBoxDecoy;
    private JLabel jLabel1;
    private JTextField jTextFieldDecoy;
    private JCheckBox jCheckBoxDecoySym;
    private JPanel jPanelSearchParams;
    private JButton jButtonSearchParams;
    private JTextField jTextFieldSearchParams;
    private File currentFile;
    private JCheckBox jCheckBoxMgf;
    private JButton jButtonClose;

    public BatchPplCreatorPanel()
    {
        this.setName("Peptide List Creation");
        initComponents();
        initialOthers();
    }

    private static void installLnF()
    {
        try {
            String lnfClassname = PREFERRED_LOOK_AND_FEEL;
            if (lnfClassname == null)
                lnfClassname = UIManager.getCrossPlatformLookAndFeelClassName();
            UIManager.setLookAndFeel(lnfClassname);
        } catch (Exception e) {
            System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL + " on this platform:" + e.getMessage());
        }
    }

    /**
     * Main entry of the class. Note: This class is only created so that you can easily preview the result at runtime.
     * It is not expected to be managed by the designer. You can modify it as you like.
     */
    public static void main(String[] args)
    {
        installLnF();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setTitle("BatchPplCreatorFrame");
            BatchPplCreatorPanel content = new BatchPplCreatorPanel();
            content.setPreferredSize(content.getSize());
            frame.getContentPane().add(content, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * Initial specific components
     */
    private void initialOthers()
    {
        this.getDbchooser();
        this.getFilechooser();
        this.getOutputchooser();
        this.getPeakListChooser();
    }

    /**
     * @return the dbchooser
     */
    private MyJFileChooser getDbchooser()
    {
        if (this.dbchooser == null) {
            this.dbchooser = new MyJFileChooser();
            this.dbchooser.setFileFilter(new String[]{"fasta"}, "Fasta database (*.fasta)");
        }
        return dbchooser;
    }

    /**
     * @return the filechooser
     */
    private MyJFileChooser getFilechooser()
    {
        if (this.filechooser == null) {
            this.filechooser = new MyJFileChooser(currentFile);
            this.filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            this.filechooser.setFileFilter(new String[]{"sqt", ZippedDtaOutUltility.EXTENSION},
                    "SEQUEST output (*.sqt, *." + ZippedDtaOutUltility.EXTENSION + " or directory)");
        }
        return filechooser;
    }

    /**
     * @return the outputchooser
     */
    private MyJFileChooser getOutputchooser()
    {
        if (this.outputchooser == null) {
            this.outputchooser = new MyJFileChooser(currentFile);
            this.outputchooser.setFileFilter(new String[]{"ppl"}, "Peptide list file (*.ppl)");
        }
        return outputchooser;
    }

    /**
     * @return the peaklist chooser
     */
    private MyJFileChooser getPeakListChooser()
    {
        if (this.jCheckBoxMgf.isSelected()) {
            this.peakListChooser = new MyJFileChooser(currentFile);
            this.peakListChooser.setFileFilter(new String[]{"mgf"}, "mgf file (*.mgf)");

        } else if (this.jCheckBoxMzData.isSelected()) {

            this.peakListChooser = new MyJFileChooser(currentFile);
            this.peakListChooser.setFileFilter(new String[]{"mzData", "xml"}, "mzData file (*.mzData, *.xml)");
        } else if (this.jCheckBoxMzXML.isSelected()) {

            this.peakListChooser = new MyJFileChooser(currentFile);
            this.peakListChooser.setFileFilter(new String[]{"mzxml"}, "mzXML file (*.mzXML)");
        }
        return peakListChooser;
    }

    private void initComponents()
    {
        initButtonGroup1();
        setSize(1088, 632);
        javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup().addGap(17)
                        .addComponent(getJPanel0(), javax.swing.GroupLayout.PREFERRED_SIZE, 431,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9).addComponent(getJPanel5(), javax.swing.GroupLayout.PREFERRED_SIZE, 621,
                                javax.swing.GroupLayout.PREFERRED_SIZE)));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup().addGap(12)
                        .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(getJPanel0(),
                                        javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(getJPanel5(), javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))));
        setLayout(groupLayout);
    }

    public JButton getJButtonClose()
    {
        if (jButtonClose == null) {
            jButtonClose = new JButton();
            jButtonClose.setText("Close");
        }
        return jButtonClose;
    }

    private JCheckBox getJCheckBoxMgf()
    {
        if (jCheckBoxMgf == null) {
            jCheckBoxMgf = new JCheckBox();
            jCheckBoxMgf.setText("Mgf");
            jCheckBoxMgf.addItemListener(this);
        }
        return jCheckBoxMgf;
    }

    private JCheckBox getJCheckBoxDecoySym()
    {
        if (jCheckBoxDecoySym == null) {
            jCheckBoxDecoySym = new JCheckBox();
            jCheckBoxDecoySym.setFont(new Font("Dialog", Font.PLAIN, 12));
            jCheckBoxDecoySym.setText("Decoy Sym in the middle");
            jCheckBoxDecoySym.setEnabled(false);
        }
        return jCheckBoxDecoySym;
    }

    private JTextField getJTextFieldDecoy()
    {
        if (jTextFieldDecoy == null) {
            jTextFieldDecoy = new JTextField();
            jTextFieldDecoy.setText("REV");
            jTextFieldDecoy.setEnabled(false);
        }
        return jTextFieldDecoy;
    }

    private JLabel getJLabel1()
    {
        if (jLabel1 == null) {
            jLabel1 = new JLabel();
            jLabel1.setText("Decoy");
            jLabel1.setToolTipText("The decoy system, default: \"REV\" at the beginning.");
        }
        return jLabel1;
    }

    private JCheckBox getJCheckBoxDecoy()
    {
        if (jCheckBoxDecoy == null) {
            jCheckBoxDecoy = new JCheckBox();
            jCheckBoxDecoy.addItemListener(this);
        }
        return jCheckBoxDecoy;
    }

    private JPanel getJPanel2()
    {
        if (jPanel2 == null) {
            jPanel2 = new JPanel();
            jPanel2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            javax.swing.GroupLayout gl_jPanel2 = new javax.swing.GroupLayout(jPanel2);
            gl_jPanel2.setHorizontalGroup(gl_jPanel2.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_jPanel2.createSequentialGroup().addGap(8)
                            .addGroup(gl_jPanel2.createParallelGroup(Alignment.LEADING)
                                    .addGroup(gl_jPanel2.createSequentialGroup().addComponent(getJCheckBoxDecoy())
                                            .addGap(3).addComponent(getJLabel1()).addGap(6)
                                            .addComponent(getJTextFieldDecoy(), javax.swing.GroupLayout.PREFERRED_SIZE,
                                                    103, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(getJCheckBoxDecoySym()))));
            gl_jPanel2.setVerticalGroup(gl_jPanel2.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_jPanel2.createSequentialGroup().addGap(15)
                            .addGroup(gl_jPanel2.createParallelGroup(Alignment.LEADING)
                                    .addGroup(gl_jPanel2.createSequentialGroup().addGap(2)
                                            .addComponent(getJCheckBoxDecoy()))
                                    .addGroup(gl_jPanel2.createSequentialGroup().addGap(2).addComponent(getJLabel1()))
                                    .addComponent(getJTextFieldDecoy(), javax.swing.GroupLayout.PREFERRED_SIZE, 23,
                                            javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(1).addComponent(getJCheckBoxDecoySym())));
            jPanel2.setLayout(gl_jPanel2);
        }
        return jPanel2;
    }

    private JCheckBox getJCheckBoxEmbPeaks()
    {
        if (jCheckBoxEmbPeaks == null) {
            jCheckBoxEmbPeaks = new JCheckBox();
            jCheckBoxEmbPeaks.setText("Embedded peak list");
            jCheckBoxEmbPeaks.addItemListener(this);
        }
        return jCheckBoxEmbPeaks;
    }

    private JCheckBox getJCheckBoxMzData()
    {
        if (jCheckBoxMzData == null) {
            jCheckBoxMzData = new JCheckBox();
            jCheckBoxMzData.setSelected(true);
            jCheckBoxMzData.setText("MzData");
            jCheckBoxMzData.addItemListener(this);
        }
        return jCheckBoxMzData;
    }

    private void initButtonGroup1()
    {
        buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(getJCheckBoxMzData());
        buttonGroup1.add(getJCheckBoxMzXML());
        buttonGroup1.add(getJCheckBoxEmbPeaks());
        buttonGroup1.add(getJCheckBoxMgf());
    }

    private JCheckBox getJCheckBoxMzXML()
    {
        if (jCheckBoxMzXML == null) {
            jCheckBoxMzXML = new JCheckBox();
            jCheckBoxMzXML.setText("MzXML");
            jCheckBoxMzXML.addItemListener(this);
        }
        return jCheckBoxMzXML;
    }

    private JScrollPane getJScrollPane0()
    {
        if (jScrollPane0 == null) {
            jScrollPane0 = new JScrollPane();
            jScrollPane0.setViewportView(getJListTasks());
        }
        return jScrollPane0;
    }

    private JList getJListTasks()
    {
        if (jListTasks == null) {
            jListTasks = new JList();
            jListTasks.setBackground(new Color(238, 238, 238));
            DefaultListModel listModel = new DefaultListModel();
            jListTasks.setModel(listModel);
            JPopupMenu menu = UIutilities.setPopupMenu(jListTasks);
            JMenuItem mitem = new JMenuItem("Remove task");
            mitem.addActionListener(e -> {
                int[] inds = jListTasks.getSelectedIndices();
                if (inds.length > 0) {
                    DefaultListModel model = ((DefaultListModel) jListTasks.getModel());
                    for (int i = inds.length - 1; i >= 0; i--) {
                        model.remove(inds[i]);
                    }
                }
            });
            menu.add(mitem);

            mitem = new JMenuItem("Clear tasks");
            mitem.addActionListener(e -> {
                DefaultListModel model = ((DefaultListModel) jListTasks.getModel());
                model.removeAllElements();

            });
            menu.add(mitem);
        }
        return jListTasks;
    }

    private JPanel getJPanel1()
    {
        if (jPanel1 == null) {
            jPanel1 = new JPanel();
            jPanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 80, 0));
            jPanel1.add(getJButtonStart());
            jPanel1.add(getJButtonClose());
        }
        return jPanel1;
    }

    private JPanel getJPanel0()
    {
        if (jPanel0 == null) {
            jPanel0 = new JPanel();
            jPanel0.setBorder(BorderFactory.createTitledBorder(null, "Edit a task", TitledBorder.LEADING,
                    TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
            javax.swing.GroupLayout gl_jPanel0 = new javax.swing.GroupLayout(jPanel0);
            gl_jPanel0.setHorizontalGroup(gl_jPanel0.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_jPanel0.createSequentialGroup().addGap(2)
                            .addComponent(getJPanelAlgorithms(), javax.swing.GroupLayout.PREFERRED_SIZE, 192,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(20).addComponent(getJPanel2(), javax.swing.GroupLayout.PREFERRED_SIZE, 193,
                                    javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(gl_jPanel0.createSequentialGroup().addGap(2).addComponent(getJPanelSource(),
                            javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(getJPanelPeaklist(), javax.swing.GroupLayout.PREFERRED_SIZE, 403,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(gl_jPanel0.createSequentialGroup().addGap(2).addComponent(getJPanelDatabase(),
                            javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(gl_jPanel0.createSequentialGroup().addGap(2).addComponent(getJPanelOutput(),
                            javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(gl_jPanel0.createSequentialGroup().addGap(8).addComponent(getJPanelMascotAcc(),
                            javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(gl_jPanel0.createSequentialGroup().addGap(17).addComponent(getJLabel0()).addGap(31)
                            .addComponent(getJSpinnerTopN(), javax.swing.GroupLayout.PREFERRED_SIZE, 57,
                                    javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(gl_jPanel0.createSequentialGroup().addGap(132).addComponent(getJButtonAddTask(),
                            javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)));
            gl_jPanel0.setVerticalGroup(gl_jPanel0.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_jPanel0.createSequentialGroup()
                            .addGroup(gl_jPanel0.createParallelGroup(Alignment.LEADING)
                                    .addComponent(getJPanelAlgorithms(), javax.swing.GroupLayout.PREFERRED_SIZE, 72,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(gl_jPanel0.createSequentialGroup().addGap(8).addComponent(getJPanel2(),
                                            javax.swing.GroupLayout.PREFERRED_SIZE, 65,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(3)
                            .addComponent(getJPanelSource(), javax.swing.GroupLayout.PREFERRED_SIZE, 72,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(8)
                            .addComponent(getJPanelPeaklist(), javax.swing.GroupLayout.PREFERRED_SIZE, 92,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(6)
                            .addComponent(getJPanelDatabase(), javax.swing.GroupLayout.PREFERRED_SIZE, 72,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(6)
                            .addComponent(getJPanelOutput(), javax.swing.GroupLayout.PREFERRED_SIZE, 72,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(12)
                            .addComponent(getJPanelMascotAcc(), javax.swing.GroupLayout.PREFERRED_SIZE, 72,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(6)
                            .addGroup(gl_jPanel0.createParallelGroup(Alignment.LEADING)
                                    .addGroup(gl_jPanel0.createSequentialGroup().addGap(3).addComponent(getJLabel0()))
                                    .addComponent(getJSpinnerTopN(), javax.swing.GroupLayout.PREFERRED_SIZE, 24,
                                            javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(6).addComponent(getJButtonAddTask(), javax.swing.GroupLayout.PREFERRED_SIZE, 28,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)));
            jPanel0.setLayout(gl_jPanel0);
        }
        return jPanel0;
    }

    private JTextField getJTextFieldPeakList()
    {
        if (jTextFieldPeakList == null) {
            jTextFieldPeakList = new JTextField();
            jTextFieldPeakList.setEditable(false);
        }
        return jTextFieldPeakList;
    }

    private JButton getJButtonPeakList()
    {
        if (jButtonPeakList == null) {
            jButtonPeakList = new JButton();
            jButtonPeakList.setText(">>");
            jButtonPeakList.addActionListener(this);
        }
        return jButtonPeakList;
    }

    private JLabel getJLabel0()
    {
        if (jLabel0 == null) {
            jLabel0 = new JLabel();
            jLabel0.setText("Select top N matched peptides for a spectrum ");
        }
        return jLabel0;
    }

    private JPanel getJPanelPeaklist()
    {
        if (jPanelPeaklist == null) {
            jPanelPeaklist = new JPanel();
            jPanelPeaklist
                    .setBorder(BorderFactory.createTitledBorder(null, "Select peak list file", TitledBorder.LEADING,
                            TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
            javax.swing.GroupLayout gl_jPanelPeaklist = new javax.swing.GroupLayout(jPanelPeaklist);
            gl_jPanelPeaklist.setHorizontalGroup(gl_jPanelPeaklist.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_jPanelPeaklist.createSequentialGroup()
                            .addComponent(getJTextFieldPeakList(), javax.swing.GroupLayout.PREFERRED_SIZE, 314,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(15).addComponent(getJButtonPeakList()))
                    .addGroup(gl_jPanelPeaklist.createSequentialGroup().addComponent(getJCheckBoxMzData()).addGap(13)
                            .addComponent(getJCheckBoxMzXML()).addGap(16).addComponent(getJCheckBoxMgf()).addGap(29)
                            .addComponent(getJCheckBoxEmbPeaks())));
            gl_jPanelPeaklist.setVerticalGroup(gl_jPanelPeaklist.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_jPanelPeaklist.createSequentialGroup()
                            .addGroup(gl_jPanelPeaklist.createParallelGroup(Alignment.LEADING)
                                    .addComponent(getJTextFieldPeakList(), javax.swing.GroupLayout.PREFERRED_SIZE, 24,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(getJButtonPeakList(), javax.swing.GroupLayout.PREFERRED_SIZE, 24,
                                            javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(1)
                            .addGroup(gl_jPanelPeaklist.createParallelGroup(Alignment.LEADING)
                                    .addComponent(getJCheckBoxMzData()).addComponent(getJCheckBoxMzXML())
                                    .addComponent(getJCheckBoxMgf()).addComponent(getJCheckBoxEmbPeaks()))));
            jPanelPeaklist.setLayout(gl_jPanelPeaklist);
        }
        return jPanelPeaklist;
    }

    private JSpinner getJSpinnerTopN()
    {
        if (jSpinnerTopN == null) {
            jSpinnerTopN = new JSpinner();
            SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 25, 1);
            jSpinnerTopN.setModel(model);
        }
        return jSpinnerTopN;
    }

    private JPanel getJPanelOutput()
    {
        if (jPanelOutput == null) {
            jPanelOutput = new JPanel();
            jPanelOutput.setBorder(BorderFactory.createTitledBorder(null, "Select output", TitledBorder.LEADING,
                    TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
            javax.swing.GroupLayout gl_jPanelOutput = new javax.swing.GroupLayout(jPanelOutput);
            gl_jPanelOutput.setHorizontalGroup(gl_jPanelOutput.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_jPanelOutput.createSequentialGroup()
                            .addComponent(getJTextFieldOutput(), javax.swing.GroupLayout.PREFERRED_SIZE, 316,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(15).addComponent(getJButtonOutput())));
            gl_jPanelOutput.setVerticalGroup(gl_jPanelOutput.createParallelGroup(Alignment.LEADING)
                    .addComponent(getJTextFieldOutput(), javax.swing.GroupLayout.PREFERRED_SIZE, 24,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(getJButtonOutput(), javax.swing.GroupLayout.PREFERRED_SIZE, 24,
                            javax.swing.GroupLayout.PREFERRED_SIZE));
            jPanelOutput.setLayout(gl_jPanelOutput);
        }
        return jPanelOutput;
    }

    private JTextField getJTextFieldOutput()
    {
        if (jTextFieldOutput == null) {
            jTextFieldOutput = new JTextField();
            jTextFieldOutput.setEditable(false);
        }
        return jTextFieldOutput;
    }

    private JButton getJButtonOutput()
    {
        if (jButtonOutput == null) {
            jButtonOutput = new JButton();
            jButtonOutput.setText(">>");
            jButtonOutput.addActionListener(this);
        }
        return jButtonOutput;
    }

    private JPanel getJPanelSearchParams()
    {
        if (this.jPanelSearchParams == null) {
            jPanelSearchParams = new JPanel();
            jPanelSearchParams
                    .setBorder(BorderFactory.createTitledBorder(null, "Search Parameter", TitledBorder.LEADING,
                            TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
            jPanelSearchParams.setLayout(new GroupLayout());
            jPanelSearchParams.add(getJTextFieldSearchParams(),
                    new Constraints(new Bilateral(0, 59, 316), new Leading(0, 24, 6, 6)));
            jPanelSearchParams.add(getJButtonSearchParams(),
                    new Constraints(new Trailing(2, 10, 328), new Leading(0, 24, 6, 6)));

            jPanelSearchParams.setVisible(false);
        }
        return jPanelSearchParams;
    }

    private JTextField getJTextFieldSearchParams()
    {
        if (jTextFieldSearchParams == null) {
            jTextFieldSearchParams = new JTextField();
            jTextFieldSearchParams.setEditable(false);
        }
        return jTextFieldSearchParams;
    }

    private JButton getJButtonSearchParams()
    {
        if (jButtonSearchParams == null) {
            jButtonSearchParams = new JButton();
            jButtonSearchParams.setText(">>");
            jButtonSearchParams.addActionListener(this);
        }
        return jButtonSearchParams;
    }

    private JPanel getJPanel5()
    {
        if (jPanel5 == null) {
            jPanel5 = new JPanel();
            jPanel5.setBorder(BorderFactory.createTitledBorder(null, "Tasks", TitledBorder.LEADING,
                    TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
            javax.swing.GroupLayout gl_jPanel5 = new javax.swing.GroupLayout(jPanel5);
            gl_jPanel5.setHorizontalGroup(gl_jPanel5.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_jPanel5.createSequentialGroup().addGap(6).addComponent(getJScrollPane0(),
                            javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(gl_jPanel5.createSequentialGroup().addGap(83).addComponent(getJPanel1(),
                            javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)));
            gl_jPanel5.setVerticalGroup(gl_jPanel5.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_jPanel5.createSequentialGroup().addGap(6)
                            .addComponent(getJScrollPane0(), javax.swing.GroupLayout.PREFERRED_SIZE, 516,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(12).addComponent(getJPanel1(), javax.swing.GroupLayout.PREFERRED_SIZE, 32,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)));
            jPanel5.setLayout(gl_jPanel5);
        }
        return jPanel5;
    }

    private JButton getJButtonStart()
    {
        if (jButtonStart == null) {
            jButtonStart = new JButton();
            jButtonStart.setText("  Start  ");
            jButtonStart.addActionListener(this);
        }
        return jButtonStart;
    }

    private JPanel getJPanelMascotAcc()
    {
        if (jPanelMascotAcc == null) {
            jPanelMascotAcc = new JPanel();
            jPanelMascotAcc.setBorder(
                    BorderFactory.createTitledBorder(null, "Mascot Accesion regular expressions", TitledBorder.LEADING,
                            TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
            javax.swing.GroupLayout gl_jPanelMascotAcc = new javax.swing.GroupLayout(jPanelMascotAcc);
            gl_jPanelMascotAcc.setHorizontalGroup(
                    gl_jPanelMascotAcc.createParallelGroup(Alignment.LEADING).addComponent(getJComboBoxMascotPattern(),
                            javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE));
            gl_jPanelMascotAcc.setVerticalGroup(
                    gl_jPanelMascotAcc.createParallelGroup(Alignment.LEADING).addComponent(getJComboBoxMascotPattern(),
                            javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE));
            jPanelMascotAcc.setLayout(gl_jPanelMascotAcc);
        }
        return jPanelMascotAcc;
    }

    private JComboBox getJComboBoxMascotPattern()
    {
        if (jComboBoxMascotPattern == null) {
            jComboBoxMascotPattern = new JComboBox();
            jComboBoxMascotPattern.setEditable(true);
            jComboBoxMascotPattern.setMaximumRowCount(10);
            jComboBoxMascotPattern.setModel(new DefaultComboBoxModel(new MascotDBPatterns().getPatterns()));
            jComboBoxMascotPattern.setEnabled(false);
            jComboBoxMascotPattern.setRequestFocusEnabled(false);
        }
        return jComboBoxMascotPattern;
    }

    private JPanel getJPanelSource()
    {
        if (jPanelSource == null) {
            jPanelSource = new JPanel();
            jPanelSource.setBorder(
                    BorderFactory.createTitledBorder(null, "Select Peptide identification file", TitledBorder.LEADING,
                            TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
            javax.swing.GroupLayout gl_jPanelSource = new javax.swing.GroupLayout(jPanelSource);
            gl_jPanelSource.setHorizontalGroup(gl_jPanelSource.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_jPanelSource.createSequentialGroup()
                            .addComponent(getJTextFieldSource(), javax.swing.GroupLayout.PREFERRED_SIZE, 314,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(15).addComponent(getJButtonSource())));
            gl_jPanelSource.setVerticalGroup(gl_jPanelSource.createParallelGroup(Alignment.LEADING)
                    .addComponent(getJTextFieldSource(), javax.swing.GroupLayout.PREFERRED_SIZE, 24,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(getJButtonSource(), javax.swing.GroupLayout.PREFERRED_SIZE, 24,
                            javax.swing.GroupLayout.PREFERRED_SIZE));
            jPanelSource.setLayout(gl_jPanelSource);
        }
        return jPanelSource;
    }

    private JTextField getJTextFieldSource()
    {
        if (jTextFieldSource == null) {
            jTextFieldSource = new JTextField();
            jTextFieldSource.setEditable(false);
        }
        return jTextFieldSource;
    }

    private JButton getJButtonSource()
    {
        if (jButtonSource == null) {
            jButtonSource = new JButton();
            jButtonSource.setText(">>");
            jButtonSource.addActionListener(this);
        }
        return jButtonSource;
    }

    private JPanel getJPanelDatabase()
    {
        if (jPanelDatabase == null) {
            jPanelDatabase = new JPanel();
            jPanelDatabase
                    .setBorder(BorderFactory.createTitledBorder(null, "Select sequence database", TitledBorder.LEADING,
                            TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
            javax.swing.GroupLayout gl_jPanelDatabase = new javax.swing.GroupLayout(jPanelDatabase);
            gl_jPanelDatabase.setHorizontalGroup(gl_jPanelDatabase.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_jPanelDatabase
                            .createSequentialGroup().addComponent(getJTextFielddb(),
                                    javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(15).addComponent(getJButtondb())));
            gl_jPanelDatabase.setVerticalGroup(gl_jPanelDatabase.createParallelGroup(Alignment.LEADING)
                    .addComponent(getJTextFielddb(), javax.swing.GroupLayout.PREFERRED_SIZE, 24,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(getJButtondb(), javax.swing.GroupLayout.PREFERRED_SIZE, 24,
                            javax.swing.GroupLayout.PREFERRED_SIZE));
            jPanelDatabase.setLayout(gl_jPanelDatabase);
        }
        return jPanelDatabase;
    }

    private JTextField getJTextFielddb()
    {
        if (jTextFielddb == null) {
            jTextFielddb = new JTextField();
            jTextFielddb.setEditable(false);
        }
        return jTextFielddb;
    }

    private JButton getJButtondb()
    {
        if (jButtondb == null) {
            jButtondb = new JButton();
            jButtondb.setText(">>");
            jButtondb.addActionListener(this);
        }
        return jButtondb;
    }

    private JPanel getJPanelAlgorithms()
    {
        if (jPanelAlgorithms == null) {
            jPanelAlgorithms = new JPanel();
            jPanelAlgorithms
                    .setBorder(BorderFactory.createTitledBorder(null, "Select a search engine", TitledBorder.LEADING,
                            TitledBorder.ABOVE_TOP, new Font("SansSerif", Font.BOLD, 12), new Color(59, 59, 59)));
            javax.swing.GroupLayout gl_jPanelAlgorithms = new javax.swing.GroupLayout(jPanelAlgorithms);
            gl_jPanelAlgorithms.setHorizontalGroup(
                    gl_jPanelAlgorithms.createParallelGroup(Alignment.LEADING).addComponent(getJComboBoxType(),
                            javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE));
            gl_jPanelAlgorithms.setVerticalGroup(
                    gl_jPanelAlgorithms.createParallelGroup(Alignment.LEADING).addComponent(getJComboBoxType(),
                            javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE));
            jPanelAlgorithms.setLayout(gl_jPanelAlgorithms);
        }
        return jPanelAlgorithms;
    }

    private JComboBox getJComboBoxType()
    {
        if (jComboBoxType == null) {
            jComboBoxType = new JComboBox();
            jComboBoxType.setModel(new DefaultComboBoxModel(new Object[]{PeptideType.SEQUEST, PeptideType.MASCOT,
                    PeptideType.XTANDEM, PeptideType.OMSSA, PeptideType.INSPECT, PeptideType.CRUX}));
            jComboBoxType.addItemListener(this);
        }
        return jComboBoxType;
    }

    private JButton getJButtonAddTask()
    {
        if (jButtonAddTask == null) {
            jButtonAddTask = new JButton();
            jButtonAddTask.setText("Add a task");
            jButtonAddTask.addActionListener(this);
        }
        return jButtonAddTask;
    }

    /**
     * Get the default ppl file path
     *
     * @param name
     * @return
     */
    private String getDefaultPplPath(String name)
    {
        String lowname = name.toLowerCase();
        String pplname;
        if (lowname.endsWith(ZippedDtaOutUltility.EXTENSION)) {
            pplname = name.substring(0, name.length() - ZippedDtaOutUltility.EXTENSION.length() - 1) + ".ppl";
        } else {
            pplname = name + ".ppl";
        }

        return pplname;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try {

            Object obj = e.getSource();

            // set the database
            if (obj == this.getJButtondb()) {
                int value = this.getDbchooser().showOpenDialog(this);
                if (value == JFileChooser.APPROVE_OPTION)
                    this.getJTextFielddb().setText(this.dbchooser.getSelectedFile().getAbsolutePath());
                return;
            }

            // set the
            if (obj == this.getJButtonSource()) {
                int value = this.getFilechooser().showOpenDialog(this);
                if (value == JFileChooser.APPROVE_OPTION) {
                    File file = this.getFilechooser().getSelectedFile();
                    this.currentFile = file.getParentFile();
                    String name = file.getAbsolutePath();
                    this.jTextFieldSource.setText(name);
                    this.jTextFieldOutput.setText(this.getDefaultPplPath(name));
                }
                return;
            }

            if (obj == this.getJButtonPeakList()) {
                int value = this.getPeakListChooser().showOpenDialog(this);
                if (value == JFileChooser.APPROVE_OPTION) {
                    File file = this.peakListChooser.getSelectedFile();
                    String name = file.getAbsolutePath();
                    this.jTextFieldPeakList.setText(name);
                }
                return;
            }

            if (obj == this.getJButtonOutput()) {
                int value = this.getOutputchooser().showSaveDialog(this);
                if (value == JFileChooser.APPROVE_OPTION) {
                    String name = this.outputchooser.getSelectedFile().getAbsolutePath();
                    if (!name.toLowerCase().endsWith(".ppl"))
                        name += ".ppl";
                    this.jTextFieldOutput.setText(name);
                }
                return;
            }

            if (obj == this.getJButtonAddTask()) {

                PeptideType type = (PeptideType) this.getJComboBoxType().getSelectedItem();

                String input = this.getJTextFieldSource().getText();
                String peaklistfile = this.getJTextFieldPeakList().getText();
                String output = this.getJTextFieldOutput().getText();
                String database = this.getJTextFielddb().getText();
                int topn = ((Number) this.jSpinnerTopN.getValue()).intValue();
                String mascot_regex = ((MascotDBPattern) this.getJComboBoxMascotPattern().getSelectedItem())
                        .getMascotPattern();

                boolean useEmbedPeaklist = this.getJCheckBoxEmbPeaks().isSelected();

                boolean decoy = this.getJCheckBoxDecoy().isSelected();

                IDecoyReferenceJudger judger;
                if (!decoy) {
                    judger = new DefaultDecoyRefJudger();
                } else {
                    String sym = this.getJTextFieldDecoy().getText();
                    boolean isStart = !this.getJCheckBoxDecoySym().isSelected();
                    judger = new GlobalDecoyRefJudger(sym, isStart);
                }

                // The type
                DtaType dtatype = null;

                if (!useEmbedPeaklist) {
                    if (this.jCheckBoxMzData.isSelected()) {
                        dtatype = DtaType.MZDATA;
                    } else if (this.jCheckBoxMzXML.isSelected()) {
                        dtatype = DtaType.MZXML;
                    } else if (this.jCheckBoxMgf.isSelected()) {
                        dtatype = DtaType.MGF;
                    }
                }

                PplCreateTaskDetails task;
                if (!useEmbedPeaklist)
                    task = new PplCreateTaskDetails(type, input, peaklistfile, dtatype, topn, output, database,
                            mascot_regex, judger);
                else
                    task = new PplCreateTaskDetails(type, input, true, topn, output, database, mascot_regex, judger);

                ((DefaultListModel) this.jListTasks.getModel()).addElement(task);

                return;
            }

            if (obj == this.getJButtonStart()) {

                // Clean up
                System.gc();
                DefaultListModel model = ((DefaultListModel) jListTasks.getModel());

                int start = 0;
                int end = jListTasks.getModel().getSize() - 1;
                if (end >= 0) {
                    jListTasks.setSelectionInterval(start, end);
                }

                int[] inds = this.jListTasks.getSelectedIndices();
                Object[] taskObjs = new Object[inds.length];
                for (int i = 0; i < inds.length; i++) {
                    taskObjs[i] = model.getElementAt(inds[i]);
                }

                if (taskObjs == null || taskObjs.length == 0)
                    JOptionPane.showMessageDialog(this, "No task.", "Error", JOptionPane.ERROR_MESSAGE);
                else {
                    try {
                        int size = taskObjs.length;
                        ITaskDetails[] tasks = new ITaskDetails[size];
                        for (int i = 0; i < size; i++) {
                            tasks[i] = (ITaskDetails) taskObjs[i];
                        }

                        ControlableTaskProgress progress = new ControlableTaskProgress(tasks);
                        progress.begin();

                        new ProgressControllerDialog(this, progress, false);
                    } catch (Exception ex) {
                        // Wait the dialog disappear
                        Thread.sleep(8000);
                        throw ex;
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /*
     * Can only show one panel at the same time
     */
    private void showParameterPanel(boolean isshow)
    {
        this.getJPanelSearchParams().setVisible(isshow);
        this.getJPanelMascotAcc().setVisible(!isshow);

    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        Object obj = e.getSource();
        try {
            if (obj == this.getJComboBoxType()) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (this.getJComboBoxType().getSelectedItem() == PeptideType.SEQUEST) {
                        this.getJComboBoxMascotPattern().setEnabled(false);
                        this.getFilechooser().setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                        this.getFilechooser().setFileFilter(new String[]{"sqt", ZippedDtaOutUltility.EXTENSION},
                                "SEQUEST output (*.sqt, *." + ZippedDtaOutUltility.EXTENSION + " or directory)");

                        this.getJPanelMascotAcc().setVisible(true);
                        this.getJPanelSearchParams().setVisible(false);

                        this.getJCheckBoxEmbPeaks().setEnabled(true);
                        this.getJCheckBoxEmbPeaks().setSelected(true);
                        this.fireCheckboxEmbPeaks();

                        return;
                    }

                    if (this.getJComboBoxType().getSelectedItem() == PeptideType.MASCOT) {
                        this.getJComboBoxMascotPattern().setEnabled(true);
                        this.getFilechooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
                        this.getFilechooser().setFileFilter(new String[]{"dat", "csv"},
                                "Mascot output (*.dat or *.csv)");

                        this.getJPanelMascotAcc().setVisible(true);
                        this.getJPanelSearchParams().setVisible(false);

                        this.getJCheckBoxMzData().setSelected(true);
                        this.fireCheckboxEmbPeaks();

                        return;
                    }

                    if (this.getJComboBoxType().getSelectedItem() == PeptideType.XTANDEM) {
                        this.getJComboBoxMascotPattern().setEnabled(false);
                        this.getFilechooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
                        this.getFilechooser().setFileFilter(new String[]{"xml"}, "XTandem output (*.xml)");

                        this.getJPanelMascotAcc().setVisible(true);
                        this.getJPanelSearchParams().setVisible(false);

                        this.getJCheckBoxMzData().setSelected(true);
                        this.getJCheckBoxEmbPeaks().setEnabled(false);
                        this.fireCheckboxEmbPeaks();

                        return;
                    }

                    if (this.getJComboBoxType().getSelectedItem() == PeptideType.OMSSA) {
                        this.getJComboBoxMascotPattern().setEnabled(false);
                        this.getFilechooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
                        this.getFilechooser().setFileFilter(new String[]{"omx"}, "OMSSA output (*.omx)");

                        this.getJPanelMascotAcc().setVisible(true);
                        this.getJPanelSearchParams().setVisible(false);

                        this.getJCheckBoxMzData().setSelected(true);
                        this.getJCheckBoxEmbPeaks().setEnabled(true);
                        this.fireCheckboxEmbPeaks();

                        return;
                    }

                    if (this.getJComboBoxType().getSelectedItem() == PeptideType.INSPECT) {
                        this.getJComboBoxMascotPattern().setEnabled(false);
                        this.getFilechooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
                        this.getFilechooser().setFileFilter(null, "Inspect plain file");

                        this.getJPanelMascotAcc().setVisible(false);
                        this.getJPanelSearchParams().setVisible(true);

                        this.getJCheckBoxMzData().setSelected(true);
                        this.getJCheckBoxEmbPeaks().setEnabled(false);
                        this.fireCheckboxEmbPeaks();

                        return;
                    }

                    if (this.getJComboBoxType().getSelectedItem() == PeptideType.CRUX) {
                        this.getJComboBoxMascotPattern().setEnabled(false);
                        this.getFilechooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
                        this.getFilechooser().setFileFilter(new String[]{"sqt"}, "Crux sqt output (*.sqt)");

                        this.getJPanelMascotAcc().setVisible(true);
                        this.getJPanelSearchParams().setVisible(false);

                        this.getJCheckBoxMzData().setSelected(true);
                        this.getJCheckBoxEmbPeaks().setEnabled(false);
                        this.fireCheckboxEmbPeaks();

                        return;
                    }
                }

                return;
            }

            if (obj == this.getJCheckBoxMzXML()) {
                this.getPeakListChooser();
                return;
            }

            if (obj == this.getJCheckBoxMzData()) {
                this.getPeakListChooser();
                return;
            }

            if (obj == this.getJCheckBoxMgf()) {
                this.getPeakListChooser();
                return;
            }

            if (obj == this.getJCheckBoxEmbPeaks()) {
                this.fireCheckboxEmbPeaks();
                return;
            }

            if (obj == this.getJCheckBoxDecoy()) {
                if (this.jCheckBoxDecoy.isSelected()) {
                    this.getJTextFieldDecoy().setEnabled(true);
                    this.getJCheckBoxDecoySym().setEnabled(true);
                } else {
                    this.getJTextFieldDecoy().setEnabled(false);
                    this.getJCheckBoxDecoySym().setEnabled(false);
                    this.getJCheckBoxDecoySym().setSelected(false);
                    this.getJTextFieldDecoy().setText(DecoyDBHelper.DEFAULT_DECOY_SYM);
                }

                return;
            }

            if (obj == this.getJButtonClose()) {
                return;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Action when the check box of
     */
    private void fireCheckboxEmbPeaks()
    {
        if (this.getJCheckBoxEmbPeaks().isSelected()) {
            this.getJButtonPeakList().setEnabled(false);
        } else {
            this.getJButtonPeakList().setEnabled(true);
        }
    }
}
