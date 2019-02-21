/*
 ******************************************************************************
 * File: OGlycoPeakConvertFrame.java * * * Created on 2013-12-2
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.gui;

import cn.ac.dicp.gp1809.glyco.oglycan.OGlycanSpecSpliter5;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//VS4E -- DO NOT REMOVE THIS LINE!
public class OGlycoPeakConvertFrame extends JFrame implements ActionListener
{

    private static final long serialVersionUID = 1L;
    private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
    private JButton jButtonStart;
    private JButton jButtonClose;
    private JLabel jLabelOutput;
    private JTextField jTextFieldOutput;
    private JButton jButtonOutput;
    private JLabel jLabelInput;
    private JTextField jTextFieldInput;
    private JButton jButtonInput;
    // private JLabel jLabelInfo;
    // private JTextField jTextFieldInfo;
    // private JButton jButtonInfo;
    private MyJFileChooser outchooser;
    private MyJFileChooser inchooser;
    private MyJFileChooser infochooser;
    private JProgressBar jProgressBar0;

    public OGlycoPeakConvertFrame()
    {
        initComponents();
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
            OGlycoPeakConvertFrame frame = new OGlycoPeakConvertFrame();
            frame.setDefaultCloseOperation(OGlycoPeakConvertFrame.EXIT_ON_CLOSE);
            frame.setTitle("OGlycoPeakConvertFrame");
            frame.getContentPane().setPreferredSize(frame.getSize());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private void initComponents()
    {
        setLayout(new GroupLayout());
        add(getJLabelInput(), new Constraints(new Leading(25, 10, 10), new Leading(42, 10, 10)));
        add(getJTextFieldInput(), new Constraints(new Leading(105, 240, 12, 12), new Leading(40, 6, 6)));
        add(getJButtonInput(), new Constraints(new Leading(380, 10, 10), new Leading(40, 12, 12)));
        // add(getJLabelInfo(), new Constraints(new Leading(25, 10, 10), new
        // Leading(92, 10, 10)));
        // add(getJTextFieldInfo(), new Constraints(new Leading(105, 240, 12,
        // 12), new Leading(90, 6, 6)));
        // add(getJButtonInfo(), new Constraints(new Leading(380, 10, 10), new
        // Leading(90, 12, 12)));
        add(getJLabelOutput(), new Constraints(new Leading(25, 10, 10), new Leading(90, 10, 10)));
        add(getJTextFieldOutput(), new Constraints(new Leading(105, 240, 12, 12), new Leading(90, 6, 6)));
        add(getJButtonOutput(), new Constraints(new Leading(380, 10, 10), new Leading(90, 12, 12)));
        add(getJButtonClose(), new Constraints(new Leading(250, 10, 10), new Leading(195, 6, 6)));
        add(getJButtonStart(), new Constraints(new Leading(100, 10, 10), new Leading(195, 6, 6)));
        add(getJProgressBar0(), new Constraints(new Leading(25, 392, 6, 6), new Leading(155, 10, 10)));
        setSize(450, 250);
    }

    private JProgressBar getJProgressBar0()
    {
        if (jProgressBar0 == null) {
            jProgressBar0 = new JProgressBar();
        }
        return jProgressBar0;
    }

    private MyJFileChooser getInchooser()
    {
        if (this.inchooser == null) {
            this.inchooser = new MyJFileChooser();
            this.inchooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            this.inchooser.setFileFilter(new String[]{""}, "mgf spectra (*.mgf or file directory)");
        }
        return inchooser;
    }

    private MyJFileChooser getOutchooser()
    {
        if (this.outchooser == null) {
            this.outchooser = new MyJFileChooser();
            this.outchooser.setFileFilter(new String[]{"mgf"}, "mgf spectra (*.mgf)");
        }
        return outchooser;
    }

    private MyJFileChooser getInfochooser()
    {
        if (this.infochooser == null) {
            this.infochooser = new MyJFileChooser();
            this.infochooser.setFileFilter(new String[]{"info"}, "O-glycan information (*.info)");
        }
        return infochooser;
    }

    private JButton getJButtonOutput()
    {
        if (jButtonOutput == null) {
            jButtonOutput = new JButton();
            jButtonOutput.setText("...");
            jButtonOutput.addActionListener(this);
        }
        return jButtonOutput;
    }

    private JTextField getJTextFieldOutput()
    {
        if (jTextFieldOutput == null) {
            jTextFieldOutput = new JTextField();
        }
        return jTextFieldOutput;
    }

    private JLabel getJLabelOutput()
    {
        if (jLabelOutput == null) {
            jLabelOutput = new JLabel();
            jLabelOutput.setText("Output");
        }
        return jLabelOutput;
    }

    private JButton getJButtonInput()
    {
        if (jButtonInput == null) {
            jButtonInput = new JButton();
            jButtonInput.setText("...");
            jButtonInput.addActionListener(this);
        }
        return jButtonInput;
    }

    private JTextField getJTextFieldInput()
    {
        if (jTextFieldInput == null) {
            jTextFieldInput = new JTextField();
        }
        return jTextFieldInput;
    }

    private JLabel getJLabelInput()
    {
        if (jLabelInput == null) {
            jLabelInput = new JLabel();
            jLabelInput.setText("Input");
        }
        return jLabelInput;
    }

    /*
     * private JButton getJButtonInfo() { if (jButtonInfo== null) { jButtonInfo
     * = new JButton(); jButtonInfo.setText("...");
     * jButtonInfo.addActionListener(this); } return jButtonInfo; }
     *
     * private JTextField getJTextFieldInfo() { if (jTextFieldInfo == null) {
     * jTextFieldInfo = new JTextField(); } return jTextFieldInfo; }
     *
     * private JLabel getJLabelInfo() { if (jLabelInfo == null) { jLabelInfo =
     * new JLabel(); jLabelInfo.setText("Info"); } return jLabelInfo; }
     */
    private JButton getJButtonClose()
    {
        if (jButtonClose == null) {
            jButtonClose = new JButton();
            jButtonClose.setText("Close");
            jButtonClose.addActionListener(this);
        }
        return jButtonClose;
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

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object obj = e.getSource();

        if (obj == this.getJButtonClose()) {
            this.dispose();
            return;
        }

        if (obj == this.getJButtonInput()) {
            int value = this.getInchooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                String filename = this.getInchooser().getSelectedFile().getAbsolutePath();
                this.getJTextFieldInput().setText(filename);
                if (filename.endsWith("mgf")) {
                    this.getJTextFieldOutput().setText(filename.replace(".mgf", "_deglyco"));
                } else {
                    this.getJTextFieldOutput().setText(filename + "_deglyco");
                }
            }
            return;
        }

        if (obj == this.getJButtonOutput()) {
            int value = this.getOutchooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                String filename = this.getOutchooser().getSelectedFile().getAbsolutePath();
                this.getJTextFieldOutput().setText(filename);
            }
            return;
        }

        /*
         * if(obj==this.getJButtonInfo()){ int value =
         * this.getInfochooser().showOpenDialog(this); if (value ==
         * JFileChooser.APPROVE_OPTION){ String filename =
         * this.getInfochooser().getSelectedFile().getAbsolutePath();
         * this.getJTextFieldInfo().setText(filename); } return; }
         */
        if (obj == this.getJButtonStart()) {

            String input = this.getJTextFieldInput().getText();
            if (input == null || input.length() == 0) {
                JOptionPane.showMessageDialog(this, "The input path have not been set.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            /*
             * String info = this.getJTextFieldInfo().getText(); if(info == null
             * || info.length() == 0) { JOptionPane.showMessageDialog(this,
             * "The info path have not been set.", "Error",
             * JOptionPane.ERROR_MESSAGE); return; }
             */
            String output = this.getJTextFieldOutput().getText();
            if (output == null || output.length() == 0) {
                JOptionPane.showMessageDialog(this, "The output path have not been set.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.jButtonStart.setEnabled(false);
            OGlycoWriteThread thread = new OGlycoWriteThread(input, output, jProgressBar0, this);
            thread.start();
        }
    }

    private class OGlycoWriteThread extends Thread
    {
        private String in;
        private String out;
        private JProgressBar jProgressBar0;
        private OGlycoPeakConvertFrame frame;

        private OGlycoWriteThread(String in, String out, JProgressBar jProgressBar0, OGlycoPeakConvertFrame frame)
        {
            this.in = in;
            this.out = out;
            this.jProgressBar0 = jProgressBar0;
            this.frame = frame;
        }

        public void run()
        {
            jProgressBar0.setStringPainted(true);
            jProgressBar0.setString("Processing...");
            jProgressBar0.setIndeterminate(true);
            getJButtonStart().setEnabled(false);

            try {

                OGlycanSpecSpliter5 spliter = new OGlycanSpecSpliter5(in, out);
                spliter.deglyco();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

            getJButtonStart().setEnabled(true);
            jProgressBar0.setString("Complete");
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            frame.dispose();
        }
    }

}
