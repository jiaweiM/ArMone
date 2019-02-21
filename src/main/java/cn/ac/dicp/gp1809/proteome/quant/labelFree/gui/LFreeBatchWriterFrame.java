/*
 ******************************************************************************
 * File: LFreeBatchWriterFrame.java * * * Created on 2011-8-2
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.labelFree.gui;

import cn.ac.dicp.gp1809.proteome.gui2.MainGui2;
import cn.ac.dicp.gp1809.proteome.gui2.util.JFileSelectPanel;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.IO.LFreePairXMLWriter;
import cn.ac.dicp.gp1809.proteome.quant.profile.IO.AbstractFeaturesXMLWriter;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

//VS4E -- DO NOT REMOVE THIS LINE!
public class LFreeBatchWriterFrame extends JFrame implements ActionListener
{

    private static final long serialVersionUID = 1L;
    private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
    private JLabel jLabelOutput;
    private JTextField jTextFieldOutput;
    private JButton jButtonOutput;
    private JButton jButtonStart;
    private JFileSelectPanel jFileSelectPanel0;
    private JCheckBox jCheckBox0;
    private MyJFileChooser outChooser;
    private File currentFile;
    private JButton jButtonClose;
    private MainGui2 maingui;

    public LFreeBatchWriterFrame()
    {
        initComponents();
    }

    public LFreeBatchWriterFrame(MainGui2 maingui)
    {
        this.maingui = maingui;
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
            System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL
                    + " on this platform:" + e.getMessage());
        }
    }

    /**
     * Main entry of the class. Note: This class is only created so that you can easily preview the result at runtime.
     * It is not expected to be managed by the designer. You can modify it as you like.
     */
    public static void main(String[] args)
    {
        installLnF();
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                LFreeBatchWriterFrame frame = new LFreeBatchWriterFrame();
                frame
                        .setDefaultCloseOperation(LFreeBatchWriterFrame.EXIT_ON_CLOSE);
                frame.setTitle("LFreeBatchWriterFrame");
                frame.getContentPane().setPreferredSize(frame.getSize());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    private void initComponents()
    {
        setResizable(false);
        setLayout(new GroupLayout());
        add(getJLabelOutput(), new Constraints(new Leading(25, 10, 10), new Leading(258, 10, 10)));
        add(getJTextFieldOutput(), new Constraints(new Leading(85, 270, 10, 10), new Leading(255, 6, 6)));
        add(getJButtonOutput(), new Constraints(new Leading(380, 10, 10), new Leading(255, 10, 10)));
        add(getJButtonClose(), new Constraints(new Leading(280, 10, 10), new Leading(305, 10, 10)));
        add(getJFileSelectPanel0(), new Constraints(new Bilateral(10, 10, 10, 10), new Leading(0, 240, 10, 10)));
        add(getJCheckBox0(), new Constraints(new Leading(22, 10, 10), new Leading(311, 12, 12)));
        add(getJButtonStart(), new Constraints(new Leading(178, 10, 10), new Leading(305, 12, 12)));
        setSize(450, 360);
    }

    private JCheckBox getJCheckBox0()
    {
        if (jCheckBox0 == null) {
            jCheckBox0 = new JCheckBox();
            jCheckBox0.setText("Open the result");
            jCheckBox0.setSelected(true);
            jCheckBox0.addActionListener(this);
        }
        return jCheckBox0;
    }

    private JFileSelectPanel getJFileSelectPanel0()
    {
        if (jFileSelectPanel0 == null) {
            jFileSelectPanel0 = new JFileSelectPanel("Label free quantitative file", "xml", "Label free quantitation result");
        }
        return jFileSelectPanel0;
    }

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

    private MyJFileChooser getOutchooser()
    {
        if (this.outChooser == null) {
            this.outChooser = new MyJFileChooser(currentFile);
            this.outChooser.setFileFilter(new String[]{"pxml"},
                    " Peptide quantitation XML file (*.pxml)");
        }
        return outChooser;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        // TODO Auto-generated method stub

        Object obj = e.getSource();

        if (obj == this.getJButtonClose()) {
            this.dispose();
        }

        if (obj == this.getJButtonOutput()) {

            int value = this.getOutchooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                this.getJTextFieldOutput().setText(this.getOutchooser().getSelectedFile().getAbsolutePath() + ".pxml");
            }

            return;
        }

        if (obj == this.getJButtonStart()) {

            File[] files = this.jFileSelectPanel0.getFiles();

            if (files == null || files.length == 0) {
                JOptionPane.showMessageDialog(null, "The input files are null.", "Error", JOptionPane.ERROR_MESSAGE);
                throw new NullPointerException("The input files are null.");
            }

            String outstr = this.getJTextFieldOutput().getText();
            if (outstr == null || outstr.length() == 0) {
                JOptionPane.showMessageDialog(null, "The output path is null.", "Error", JOptionPane.ERROR_MESSAGE);
                throw new NullPointerException("The output path is null.");
            }

            AbstractFeaturesXMLWriter writer = null;

            if (files.length == 1) {
                try {
                    writer = new LFreePairXMLWriter(files[0], outstr);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            } else {
                try {
                    writer = new LFreePairXMLWriter(files, outstr);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            try {
                writer.write();
                writer.close();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (this.jCheckBox0.isSelected()) {

            }

            this.dispose();
            return;
        }
    }


}
