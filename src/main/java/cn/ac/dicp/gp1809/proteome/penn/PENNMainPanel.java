/*
 ******************************************************************************
 * File: PENNMainPanel.java * * * Created on 08-08-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.penn;

import cn.ac.dicp.gp1809.proteome.penn.probability.ProbCalculator;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Xinning
 * @version 0.1, 08-08-2009, 15:58:34
 */
public class PENNMainPanel extends JPanel implements ItemListener, ActionListener
{

    private static final long serialVersionUID = 1L;

    private MyJFileChooser pplchooser;
    private MyJFileChooser databasechooser;

    private JLabel jLabel0;
    private JTextField jTextFieldInput;
    private JLabel jLabel1;
    private JButton jButtonInput;
    private JButton jButtonOutput;
    private JTextField jTextFieldOutput;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JButton jButtonDatabase;
    private JTextField jTextFieldDatabase;
    private JCheckBox jCheckBoxPepxml;
    private JCheckBox jCheckBoxPpl;
    private JPanel jPanel0;
    private JButton jButtonStart;
    private JPanel jPanel1;
    private JProgressBar jProgressBar;

    public PENNMainPanel()
    {
        initComponents();
    }

    private void initComponents()
    {
        setSize(516, 298);
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(12)
                                .addComponent(getJPanel0(), GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE)
                                .addGap(23)
                                .addComponent(getJLabel1(), GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(12)
                                .addComponent(getJLabel0())
                                .addGap(8)
                                .addComponent(getJTextFieldInput(), GroupLayout.PREFERRED_SIZE, 377, GroupLayout.PREFERRED_SIZE)
                                .addGap(12)
                                .addComponent(getJButtonInput()))
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(12)
                                .addComponent(getJLabel2(), GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
                                .addGap(8)
                                .addComponent(getJTextFieldOutput(), GroupLayout.PREFERRED_SIZE, 377, GroupLayout.PREFERRED_SIZE)
                                .addGap(12)
                                .addComponent(getJButtonOutput()))
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(6)
                                .addComponent(getJLabel3(), GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
                                .addGap(3)
                                .addComponent(getJTextFieldDatabase(), GroupLayout.PREFERRED_SIZE, 377, GroupLayout.PREFERRED_SIZE)
                                .addGap(12)
                                .addComponent(getJButtonDatabase()))
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(6)
                                .addComponent(getJProgressBar(), GroupLayout.PREFERRED_SIZE, 498, GroupLayout.PREFERRED_SIZE))
                        .addComponent(getJPanel1(), GroupLayout.PREFERRED_SIZE, 510, GroupLayout.PREFERRED_SIZE)
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(18)
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGap(22)
                                                .addComponent(getJPanel0(), GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(getJLabel1(), GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
                                .addGap(25)
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGap(6)
                                                .addComponent(getJLabel0()))
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGap(1)
                                                .addComponent(getJTextFieldInput(), GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(getJButtonInput()))
                                .addGap(11)
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGap(6)
                                                .addComponent(getJLabel2()))
                                        .addComponent(getJTextFieldOutput(), GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(getJButtonOutput()))
                                .addGap(9)
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGap(5)
                                                .addComponent(getJLabel3()))
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGap(2)
                                                .addComponent(getJTextFieldDatabase(), GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(getJButtonDatabase()))
                                .addGap(17)
                                .addComponent(getJProgressBar(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6)
                                .addComponent(getJPanel1(), GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
        );
        setLayout(groupLayout);
    }

    private MyJFileChooser getJFileChooserPpl()
    {
        if (this.pplchooser == null) {
            this.pplchooser = new MyJFileChooser();
            this.pplchooser.setFileFilter(new String[]{"ppl"}, "The peptide list file (*.ppl)");
        }
        return this.pplchooser;
    }

    private MyJFileChooser getJFileChooserDatabase()
    {
        if (this.databasechooser == null) {
            this.databasechooser = new MyJFileChooser();
            this.databasechooser.setFileFilter(new String[]{"fasta"}, "The fasta database (*.fasta)");
        }
        return this.databasechooser;
    }


    private JProgressBar getJProgressBar()
    {
        if (jProgressBar == null) {
            jProgressBar = new JProgressBar();
        }
        return jProgressBar;
    }

    private JPanel getJPanel1()
    {
        if (jPanel1 == null) {
            jPanel1 = new JPanel();
            jPanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            jPanel1.add(getJButtonStart());
        }
        return jPanel1;
    }

    private JButton getJButtonStart()
    {
        if (jButtonStart == null) {
            jButtonStart = new JButton();
            jButtonStart.setText("Start");
            jButtonStart.addActionListener(this);
        }
        return jButtonStart;
    }

    private JPanel getJPanel0()
    {
        if (jPanel0 == null) {
            jPanel0 = new JPanel();
            jPanel0.setBorder(new LineBorder(Color.black, 1, false));
            GroupLayout gl_jPanel0 = new GroupLayout(jPanel0);
            gl_jPanel0.setHorizontalGroup(
                    gl_jPanel0.createParallelGroup(Alignment.LEADING)
                            .addGroup(gl_jPanel0.createSequentialGroup()
                                    .addGap(6)
                                    .addComponent(getJCheckBoxPepxml())
                                    .addGap(48)
                                    .addComponent(getJCheckBoxPpl(), GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
            );
            gl_jPanel0.setVerticalGroup(
                    gl_jPanel0.createParallelGroup(Alignment.LEADING)
                            .addGroup(gl_jPanel0.createSequentialGroup()
                                    .addGap(11)
                                    .addGroup(gl_jPanel0.createParallelGroup(Alignment.LEADING)
                                            .addComponent(getJCheckBoxPepxml())
                                            .addComponent(getJCheckBoxPpl())))
            );
            jPanel0.setLayout(gl_jPanel0);
        }
        return jPanel0;
    }

    private JCheckBox getJCheckBoxPpl()
    {
        if (jCheckBoxPpl == null) {
            jCheckBoxPpl = new JCheckBox();
            jCheckBoxPpl.setSelected(true);
            jCheckBoxPpl.setEnabled(false);
            jCheckBoxPpl.setText("Peptide list");
        }
        return jCheckBoxPpl;
    }

    private JCheckBox getJCheckBoxPepxml()
    {
        if (jCheckBoxPepxml == null) {
            jCheckBoxPepxml = new JCheckBox();
            jCheckBoxPepxml.setText("Pepxml");
        }
        return jCheckBoxPepxml;
    }

    private JTextField getJTextFieldDatabase()
    {
        if (jTextFieldDatabase == null) {
            jTextFieldDatabase = new JTextField();
        }
        return jTextFieldDatabase;
    }

    private JButton getJButtonDatabase()
    {
        if (jButtonDatabase == null) {
            jButtonDatabase = new JButton();
            jButtonDatabase.setText("<<");
            jButtonDatabase.addActionListener(this);
        }
        return jButtonDatabase;
    }

    private JLabel getJLabel3()
    {
        if (jLabel3 == null) {
            jLabel3 = new JLabel();
            jLabel3.setText("Database");
        }
        return jLabel3;
    }

    private JLabel getJLabel2()
    {
        if (jLabel2 == null) {
            jLabel2 = new JLabel();
            jLabel2.setText("Output");
        }
        return jLabel2;
    }

    private JTextField getJTextFieldOutput()
    {
        if (jTextFieldOutput == null) {
            jTextFieldOutput = new JTextField();
        }
        return jTextFieldOutput;
    }

    private JButton getJButtonOutput()
    {
        if (jButtonOutput == null) {
            jButtonOutput = new JButton();
            jButtonOutput.setText("<<");
            jButtonOutput.addActionListener(this);
        }
        return jButtonOutput;
    }

    private JButton getJButtonInput()
    {
        if (jButtonInput == null) {
            jButtonInput = new JButton();
            jButtonInput.setText("<<");
            jButtonInput.addActionListener(this);
        }
        return jButtonInput;
    }

    private JLabel getJLabel1()
    {
        if (jLabel1 == null) {
            jLabel1 = new JLabel();
            jLabel1.setIcon(new ImageIcon(getClass().getResource("/resources/PENN.PNG")));
        }
        return jLabel1;
    }

    private JTextField getJTextFieldInput()
    {
        if (jTextFieldInput == null) {
            jTextFieldInput = new JTextField();
        }
        return jTextFieldInput;
    }

    private JLabel getJLabel0()
    {
        if (jLabel0 == null) {
            jLabel0 = new JLabel();
            jLabel0.setText("Input ppl");
        }
        return jLabel0;
    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {

        Object obj = e.getSource();

        if (obj == this.getJCheckBoxPepxml()) {
            if (this.getJCheckBoxPepxml().isSelected()) {
                this.getJCheckBoxPpl().setSelected(true);
            }

            return;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {

        Object obj = e.getSource();

        if (obj == this.getJButtonDatabase()) {
            int val = this.getJFileChooserDatabase().showOpenDialog(this);
            if (val == JFileChooser.APPROVE_OPTION) {
                this.getJTextFieldDatabase().setText(this.getJFileChooserDatabase().getSelectedFile().getAbsolutePath());
            }
            return;
        }

        if (obj == this.getJButtonInput()) {
            int val = this.getJFileChooserPpl().showOpenDialog(this);
            if (val == JFileChooser.APPROVE_OPTION) {
                String name = this.getJFileChooserPpl().getSelectedFile().getAbsolutePath();
                this.getJTextFieldInput().setText(name);
                this.getJTextFieldOutput().setText(name.substring(0, name.length() - 3) + "prob.ppl");
            }
            return;
        }

        if (obj == this.getJButtonOutput()) {
            int val = this.getJFileChooserPpl().showSaveDialog(this);
            if (val == JFileChooser.APPROVE_OPTION) {
                String name = this.getJFileChooserPpl().getSelectedFile().getAbsolutePath();
                if (!name.toLowerCase().endsWith(".prob.ppl"))
                    name += ".prob.ppl";
                this.getJTextFieldOutput().setText(name);
            }
            return;
        }

        if (obj == this.getJButtonStart()) {

            try {
                final String input = this.getJTextFieldInput().getText();
                if (input.length() < 3)
                    throw new NullPointerException("Please select input first");

                final String output = this.getJTextFieldOutput().getText();
                if (output.length() < 3)
                    throw new NullPointerException("Please select output first");
                final String database = this.getJTextFieldDatabase().getText();
                if (database.length() < 3)
                    throw new NullPointerException("Please select database first");
                final String type = this.getJCheckBoxPepxml().isSelected() ? "1" : "0";

                new Thread()
                {
                    @Override
                    public void run()
                    {

                        try {
                            getJButtonStart().setEnabled(false);
                            getJProgressBar().setString("Processing, please wait ...");
                            getJProgressBar().setIndeterminate(true);
                            String[] args = new String[]{input, output, database, type};

                            ProbCalculator.main(args);

                            getJProgressBar().setString("Completed!");
                        } catch (Exception ie) {
                            JOptionPane.showMessageDialog(PENNMainPanel.this, ie, "Error", JOptionPane.ERROR_MESSAGE);
                            ie.printStackTrace();
                        } finally {
                            getJProgressBar().setIndeterminate(false);
                            getJButtonStart().setEnabled(true);
                        }

                    }
                }.start();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }


            return;
        }

    }

}
