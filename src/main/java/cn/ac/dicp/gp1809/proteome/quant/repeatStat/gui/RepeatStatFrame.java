/*
 ******************************************************************************
 * File: RepeatStatFrame.java * * * Created on 2011-11-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.repeatStat.gui;

import cn.ac.dicp.gp1809.proteome.gui2.util.JFileSelectPanel;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.repeatStat.RepeatModXlsWriter;
import cn.ac.dicp.gp1809.proteome.quant.repeatStat.RepeatResultXlsWriter;
import cn.ac.dicp.gp1809.proteome.quant.turnover.gui.TurnOverPanel2;
import cn.ac.dicp.gp1809.proteome.quant.turnover.gui.TurnOverPanel3;
import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

//VS4E -- DO NOT REMOVE THIS LINE!
public class RepeatStatFrame extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;
    private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
    private JButton jButtonNext;
    private File[] files;
    private JButton jButtonClose;
    private JPanel jPanel0;
    private JFileSelectPanel jFileSelectPanel0;
    private TurnOverPanel2 mutilCompPanel20;
    private TurnOverPanel3 mutilCompPanel30;
    private ModInfo[] mods;
    private Object[][] ratioModel;
    private LabelType type;

    public RepeatStatFrame()
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
                RepeatStatFrame frame = new RepeatStatFrame();
                frame.setDefaultCloseOperation(RepeatStatFrame.EXIT_ON_CLOSE);
                frame.setTitle("RepeatStatFrame");
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
        add(getJPanel0(), new Constraints(new Bilateral(0, 0, 0), new Bilateral(0, 0, 0)));
        setSize(540, 390);
        this.setResizable(false);
    }

    private TurnOverPanel2 getMutilCompPanel20()
    {
        if (mutilCompPanel20 == null) {
            if (this.ratioModel == null)
                mutilCompPanel20 = new TurnOverPanel2();
            else
                mutilCompPanel20 = new TurnOverPanel2(ratioModel);

            mutilCompPanel20.getJButtonClose().addActionListener(this);
            mutilCompPanel20.getJButtonMod().addActionListener(this);
            mutilCompPanel20.getJButtonPre().addActionListener(this);
            mutilCompPanel20.getJButtonStart().addActionListener(this);
        }
        return mutilCompPanel20;
    }

    private TurnOverPanel3 getMutilCompPanel30()
    {
        if (mutilCompPanel30 == null) {
            if (this.mods == null)
                mutilCompPanel30 = new TurnOverPanel3();
            else
                mutilCompPanel30 = new TurnOverPanel3(mods);

            mutilCompPanel30.getJButtonClose().addActionListener(this);
            mutilCompPanel30.getJButtonPre().addActionListener(this);
            mutilCompPanel30.getJButtonStart().addActionListener(this);
        }
        return mutilCompPanel30;
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

    private JButton getJButtonNex()
    {
        if (jButtonNext == null) {
            jButtonNext = new JButton();
            jButtonNext.setText("Next");
            jButtonNext.addActionListener(this);
        }
        return jButtonNext;
    }

    private JFileSelectPanel getJFileSelectPanel0()
    {
        if (jFileSelectPanel0 == null) {
            jFileSelectPanel0 = new JFileSelectPanel("Repeat quantitation result", "pxml", "peptide quantitation result");
        }
        return jFileSelectPanel0;
    }

    private JPanel getJPanel0()
    {
        if (jPanel0 == null) {
            jPanel0 = new JPanel();
            jPanel0.setLayout(new GroupLayout());
            jPanel0.add(getJButtonClose(), new Constraints(new Leading(420, 10, 10), new Leading(320, 10, 10)));
            jPanel0.add(getJButtonNex(), new Constraints(new Leading(330, 10, 10), new Leading(320, 12, 12)));
            jPanel0.add(getJFileSelectPanel0(), new Constraints(new Bilateral(0, 0, 10, 10), new Leading(0, 302, 6, 6)));
        }
        return jPanel0;
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
            return;
        }

        if (obj == this.getJButtonNex()) {

            File[] files = this.jFileSelectPanel0.getFiles();

            if (files == null || files.length == 0) {
                JOptionPane.showMessageDialog(null, "The input files are null.", "Error", JOptionPane.ERROR_MESSAGE);
                throw new NullPointerException("The input files are null.");

            } else {

                this.files = files;
                try {
                    this.readFirstFile(files[0]);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            this.remove(jPanel0);
            this.add(getMutilCompPanel20(), new Constraints(new Bilateral(0, 0, 200, 540), new Bilateral(0, 0, 360, 360)));
            this.repaint();
            this.setVisible(true);
            return;
        }

        if (obj == this.getMutilCompPanel20().getJButtonClose()) {
            this.dispose();
            return;
        }

        if (obj == this.getMutilCompPanel20().getJButtonMod()) {
            this.remove(mutilCompPanel20);
            this.add(getMutilCompPanel30(), new Constraints(new Bilateral(0, 0, 200, 540), new Bilateral(0, 0, 360, 360)));
            this.repaint();
            this.setVisible(true);
            return;
        }

        if (obj == this.getMutilCompPanel20().getJButtonPre()) {
            this.remove(this.mutilCompPanel20);
            this.add(this.jPanel0, new Constraints(new Bilateral(0, 0, 200, 540), new Bilateral(0, 0, 360, 360)));
            this.repaint();
            this.setVisible(true);
            return;
        }

        if (obj == this.getMutilCompPanel20().getJButtonStart()) {

            String output = this.mutilCompPanel20.getOutput();
            if (output == null || output.length() == 0) {
                JOptionPane.showMessageDialog(null, "The output path is null.", "Error", JOptionPane.ERROR_MESSAGE);
                throw new NullPointerException("The output path is null.");
            }

            RepeatResultXlsWriter rrWriter = new RepeatResultXlsWriter(files, output, type);

            boolean isNormal = this.mutilCompPanel20.isNormal();
            String[] ratioNames = this.mutilCompPanel20.getRatioNames();
            int[] selectRatios = this.mutilCompPanel20.getSelectRatio();
            double[] theoryRatios = this.mutilCompPanel20.getTheoryRatios();
            double[] usedTheoryRatios = this.mutilCompPanel20.getUsedTheoryRatios();

            RepeatStatThread thread = new RepeatStatThread(rrWriter, isNormal, ratioNames, theoryRatios,
                    usedTheoryRatios, selectRatios, this.mutilCompPanel20.getJProgressBar0(), this);

            thread.start();

            return;
        }

        if (obj == this.getMutilCompPanel30().getJButtonClose()) {
            this.dispose();
            return;
        }

        if (obj == this.getMutilCompPanel30().getJButtonPre()) {
            this.remove(this.mutilCompPanel30);
            this.add(this.mutilCompPanel20, new Constraints(new Bilateral(0, 0, 200, 540), new Bilateral(0, 0, 360, 360)));
            this.repaint();
            this.setVisible(true);
            return;
        }

        if (obj == this.getMutilCompPanel30().getJButtonStart()) {

            String output = this.mutilCompPanel30.getOutput();
            if (output == null || output.length() == 0) {
                JOptionPane.showMessageDialog(null, "The output path is null.", "Error", JOptionPane.ERROR_MESSAGE);
                throw new NullPointerException("The output path is null.");
            }

            RepeatModXlsWriter rrWriter = new RepeatModXlsWriter(files, output, type);

            boolean isNormal = this.mutilCompPanel20.isNormal();
            String[] ratioNames = this.mutilCompPanel20.getRatioNames();
            int[] selectRatios = this.mutilCompPanel20.getSelectRatio();
            double[] theoryRatios = this.mutilCompPanel20.getTheoryRatios();
            double[] usedTheoryRatios = this.mutilCompPanel20.getUsedTheoryRatios();

            RepeatModStatThread thread = new RepeatModStatThread(rrWriter, isNormal, ratioNames, theoryRatios,
                    usedTheoryRatios, selectRatios, mutilCompPanel30.getMods(), mutilCompPanel30.getJProgressBar0(), this);

            thread.start();

            return;
        }
    }

    private void readFirstFile(File pxml) throws Exception
    {

        LabelFeaturesXMLReader xmlreader = new LabelFeaturesXMLReader(pxml);
        this.mods = xmlreader.getMods();
        this.ratioModel = xmlreader.getRatioModelInfo();
    }

    private class RepeatStatThread extends Thread
    {

        private RepeatResultXlsWriter writer;
        private boolean normal;
        private String[] ratioNames;
        private double[] theoryRatios;
        private double[] usedTheoryRatios;
        private int[] outputRatios;
        private JProgressBar bar;
        private RepeatStatFrame frame;

        private RepeatStatThread(RepeatResultXlsWriter writer, boolean normal, String[] ratioNames,
                double[] theoryRatios, double[] usedTheoryRatios, int[] outputRatios,
                JProgressBar bar, RepeatStatFrame frame)
        {

            this.writer = writer;
            this.normal = normal;
            this.ratioNames = ratioNames;
            this.theoryRatios = theoryRatios;
            this.usedTheoryRatios = usedTheoryRatios;
            this.outputRatios = outputRatios;
            this.bar = bar;
            this.frame = frame;
        }

        public void run()
        {

            bar.setStringPainted(true);
            bar.setString("Processing...");
            bar.setIndeterminate(true);

            try {
                writer.stat(normal, ratioNames, theoryRatios, usedTheoryRatios, outputRatios);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            bar.setString("Complete");
//			bar.setIndeterminate(false);

            try {
                sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            frame.dispose();
        }

    }

    private class RepeatModStatThread extends Thread
    {

        private RepeatModXlsWriter writer;
        private boolean normal;
        private String[] ratioNames;
        private double[] theoryRatios;
        private double[] usedTheoryRatios;
        private int[] outputRatios;
        private ModInfo[] mods;
        private JProgressBar bar;
        private RepeatStatFrame frame;

        private RepeatModStatThread(RepeatModXlsWriter writer, boolean normal, String[] ratioNames,
                double[] theoryRatios, double[] usedTheoryRatios, int[] outputRatios,
                ModInfo[] mods, JProgressBar bar, RepeatStatFrame frame)
        {

            this.writer = writer;
            this.normal = normal;
            this.ratioNames = ratioNames;
            this.theoryRatios = theoryRatios;
            this.usedTheoryRatios = usedTheoryRatios;
            this.outputRatios = outputRatios;
            this.mods = mods;
            this.bar = bar;
            this.frame = frame;
        }

        public void run()
        {

            bar.setStringPainted(true);
            bar.setString("Processing...");
            bar.setIndeterminate(true);

            try {
                writer.stat(false, normal, mods, ratioNames, theoryRatios, usedTheoryRatios, outputRatios);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            bar.setString("Complete");
//			bar.setIndeterminate(false);
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
