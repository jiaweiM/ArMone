/*
 ******************************************************************************
 * File:QResOutFrame.java * * * Created on 2010-8-26
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.gui;

import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.IO.FeaturesPagedRowGetter;
import cn.ac.dicp.gp1809.proteome.quant.profile.IO.QuanResultWriter;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanResult;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

//VS4E -- DO NOT REMOVE THIS LINE!
public class QResOutFrame extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;
    private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
    private FeaturesPagedRowGetter getter;
    private MyJFileChooser outChooser;
    private JLabel jLabelOutput;
    private JTextField jTextFieldOutput;
    private JButton jButtonOutput;
    private JButton jButtonStart;
    private JButton jButtonClose;
    private JLabel jLabelPepnum;
    private JTextField jTextFieldPepnum;
    private JCheckBox jCheckBoxNoMod;
    private RatioSelectPanel ratioSelectPanel0;
    private Object[][] objs;
    private JProgressBar jProgressBar0;

    public QResOutFrame()
    {
        initComponents();
    }

    public QResOutFrame(FeaturesPagedRowGetter getter)
    {
        this.getter = getter;
        this.objs = getter.getRatioModelInfo();
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
                QResOutFrame frame = new QResOutFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setTitle("QResOutFrame");
                frame.getContentPane().setPreferredSize(frame.getSize());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    private void initComponents()
    {
        setLayout(new GroupLayout());
        add(getJLabelOutput(), new Constraints(new Leading(24, 6, 6), new Leading(300, 10, 10)));
        add(getJTextFieldOutput(), new Constraints(new Leading(90, 210, 10, 10), new Leading(295, 6, 6)));
        add(getJButtonOutput(), new Constraints(new Leading(330, 6, 6), new Leading(295, 10, 10)));
//		add(getJCheckBoxNoMod(), new Constraints(new Leading(204, 10, 10), new Leading(235, 6, 6)));
        add(getJButtonStart(), new Constraints(new Leading(120, 10, 10), new Leading(410, 10, 10)));
        add(getJButtonClose(), new Constraints(new Leading(230, 10, 10), new Leading(410, 10, 10)));
        add(getJLabelPepnum(), new Constraints(new Leading(24, 10, 10), new Leading(235, 10, 10)));
        add(getJTextFieldPepnum(), new Constraints(new Leading(130, 33, 6, 6), new Leading(230, 6, 6)));
        add(getRatioSelectPanel0(), new Constraints(new Bilateral(0, 0, 6, 6), new Leading(0, 220, 6, 6)));
        add(getJProgressBar0(), new Constraints(new Leading(24, 343, 6, 6), new Leading(357, 10, 10)));
        setSize(410, 480);
    }

    private JProgressBar getJProgressBar0()
    {
        if (jProgressBar0 == null) {
            jProgressBar0 = new JProgressBar();
        }
        return jProgressBar0;
    }

    private RatioSelectPanel getRatioSelectPanel0()
    {
        if (ratioSelectPanel0 == null) {
            if (objs == null)
                ratioSelectPanel0 = new RatioSelectPanel();
            else
                ratioSelectPanel0 = new RatioSelectPanel(objs);
        }
        return ratioSelectPanel0;
    }

    private JCheckBox getJCheckBoxNoMod()
    {
        if (jCheckBoxNoMod == null) {
            jCheckBoxNoMod = new JCheckBox();
            jCheckBoxNoMod.setText("Use no_mod peptide");
        }
        return jCheckBoxNoMod;
    }

    private JTextField getJTextFieldPepnum()
    {
        if (jTextFieldPepnum == null) {
            jTextFieldPepnum = new JTextField();
            jTextFieldPepnum.setText("1");
        }
        return jTextFieldPepnum;
    }

    private JLabel getJLabelPepnum()
    {
        if (jLabelPepnum == null) {
            jLabelPepnum = new JLabel();
            jLabelPepnum.setText("Peptide Count >=");
        }
        return jLabelPepnum;
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

    private MyJFileChooser getOutchooser()
    {
        if (this.outChooser == null) {
            this.outChooser = new MyJFileChooser(getter.getFile());
            this.outChooser.setFileFilter(new String[]{"xls"},
                    "Label Quantitation result file (*.xls)");
        }
        return outChooser;
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

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        // TODO Auto-generated method stub

        Object obj = e.getSource();

        if (obj == this.getJButtonOutput()) {
            int value = this.getOutchooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION)
                this.getJTextFieldOutput().setText(
                        this.getOutchooser().getSelectedFile().getAbsolutePath() + ".xls");
            return;
        }

        if (obj == this.getJButtonClose()) {
            this.dispose();
        }

        if (obj == this.getJButtonStart()) {

            String output = this.getJTextFieldOutput().getText();
            if (output == null || output.length() == 0) {
                JOptionPane.showMessageDialog(null, "The output path is null.", "Error", JOptionPane.ERROR_MESSAGE);
                throw new NullPointerException("The output path is null.");
            }

//			boolean useNoMod = this.getJCheckBoxNoMod().isSelected();
            boolean useNoMod = true;

            LabelType type = getter.getType();
            ModInfo[] mods = getter.getMods();

            int[] select = this.ratioSelectPanel0.getSelect();
            double[] theRatio = this.ratioSelectPanel0.getTheRatio();
            double[] usedTheRatio = this.ratioSelectPanel0.getUsedTheRatio();
            String[] ratioNames = this.ratioSelectPanel0.getRatioNames();
            boolean isNormal = this.ratioSelectPanel0.isNormal();

            this.getter.setTheoryRatio(theRatio);

            int pepCount = Integer.parseInt(this.getJTextFieldPepnum().getText());

            this.jButtonStart.setEnabled(false);

            try {

                WriteThread writerThread = new WriteThread(output, type, mods, pepCount, select, ratioNames,
                        theRatio, usedTheRatio, isNormal, useNoMod, getter, jProgressBar0, this);

                writerThread.start();

            } catch (Exception e1) {
                JOptionPane.showMessageDialog(this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e1.printStackTrace();
            }

            return;
        }

    }

    private class WriteThread extends Thread
    {

        private String output;
        private LabelType type;
        private ModInfo[] mods;
        private int pepCount;
        private int[] outputRatios;
        private String[] ratioNames;
        private double[] theRatio;
        private double[] usedTheRatio;
        private boolean isNormal;
        private boolean useNoMod;
        private FeaturesPagedRowGetter getter;
        private JProgressBar bar;
        private QResOutFrame frame;

        private WriteThread(String output, LabelType type, ModInfo[] mods, int pepCount,
                int[] outputRatios, String[] ratioNames,
                double[] theRatio, double[] usedTheRatio, boolean isNormal, boolean useNoMod,
                FeaturesPagedRowGetter getter,
                JProgressBar bar, QResOutFrame frame)
        {

            this.output = output;
            this.type = type;
            this.mods = mods;
            this.pepCount = pepCount;
            this.outputRatios = outputRatios;
            this.ratioNames = ratioNames;
            this.theRatio = theRatio;
            this.usedTheRatio = usedTheRatio;
            this.isNormal = isNormal;
            this.useNoMod = useNoMod;
            this.getter = getter;
            this.bar = bar;
            this.frame = frame;
        }

        public void run()
        {

            bar.setStringPainted(true);
            bar.setString("Processing...");
            bar.setIndeterminate(true);

            QuanResultWriter writer = null;
            try {
                writer = new QuanResultWriter(output, type, getter.isGradient(), mods, ratioNames, usedTheRatio);

            } catch (RowsExceededException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            QuanResult[] reslist = null;
            try {

                getter.setTheoryRatio(theRatio);
                reslist = getter.getAllResult(useNoMod, isNormal, outputRatios);

                for (int i = 0; i < reslist.length; i++) {
                    if (reslist[i].validata(pepCount)) {
                        writer.write(reslist[i]);
                    }
                }

                writer.writeSummary();
                writer.close();

            } catch (Exception e) {
                // TODO Auto-generated catch block
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
