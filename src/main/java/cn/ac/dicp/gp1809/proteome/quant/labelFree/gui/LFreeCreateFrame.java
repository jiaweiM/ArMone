/*
 ******************************************************************************
 * File: LFreeCreateFrame.java * * * Created on 2011-8-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.labelFree.gui;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.Quan.labelFree2.GlycoLFreeQuanTask2;
import cn.ac.dicp.gp1809.glyco.Quan.labelFree2.GlycoLFreeRowGetter2;
import cn.ac.dicp.gp1809.glyco.gui.NGlycoMatchViewPanel;
import cn.ac.dicp.gp1809.glyco.gui.NGlycoParaPanel;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IFilteredPeptideListReader;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.gui2.MainGui2;
import cn.ac.dicp.gp1809.proteome.quant.label.gui.LPairCreateFrame;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.IO.LFreeQuanTask;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.xml.stream.XMLStreamException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class LFreeCreateFrame extends JFrame implements ActionListener
{
    private IFilteredPeptideListReader reader;

    private JLabel jLabelPeak;
    private JTextField jTextFieldPeak;
    private JButton jButtonPeak;
    private MyJFileChooser peakchooser;
    private JButton jButtonStart;
    private MyJFileChooser reschooser;
    private JLabel jLabelResult;
    private JTextField jTextFieldResult;
    private JButton jButtonResult;
    private MainGui2 m2;
    private File file;
    private JPanel jPanelGlycoPara;
    private JPanel jPanel0;

    private static final long serialVersionUID = 1L;

    private JLabel jLabelLIN;
    private JTextField jTextFieldNumber;
    private JLabel jLabelGlyco;
    private JButton jButtonClose;
    private JProgressBar jProgressBar0;
    private int glycoType;

    private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

    /**
     * @param reader
     * @param m2
     * @param glycoType a for Glyco spectra match
     * @param file
     */
    public LFreeCreateFrame(IFilteredPeptideListReader reader, MainGui2 m2,
                            int glycoType, File file)
    {
        this.reader = reader;
        this.m2 = m2;
        this.file = file;
        this.glycoType = glycoType;
        initComponents();
    }

    private void initComponents()
    {
        setResizable(false);
        setLayout(new GroupLayout());
        add(getJButtonPeak(), new Constraints(new Leading(377, 30, 10, 10), new Trailing(230, 10, 10)));
        add(getJLabelPeak(), new Constraints(new Leading(32, 10, 10), new Trailing(230, 24, 10, 10)));
        add(getJTextFieldPeak(), new Constraints(new Leading(102, 252, 10, 10), new Trailing(230, 10, 10)));
        add(getJLabelLIN(), new Constraints(new Leading(32, 10, 10), new Trailing(125, 6, 6)));
        add(getJLabelResult(), new Constraints(new Leading(32, 6, 6), new Trailing(175, 10, 10)));
        add(getJButtonResult(), new Constraints(new Leading(377, 30, 6, 6), new Trailing(175, 6, 6)));
        add(getJTextFieldResult(), new Constraints(new Leading(102, 252, 6, 6), new Trailing(175, 6, 6)));
        add(getJTextFieldNumber(), new Constraints(new Leading(160, 28, 6, 6), new Trailing(120, 6, 6)));
        add(getJButtonStart(), new Constraints(new Leading(90, 10, 10), new Trailing(28, 6, 6)));
        add(getJButtonClose(), new Constraints(new Leading(280, 6, 6), new Trailing(28, 6, 6)));
        add(getJProgressBar0(), new Constraints(new Leading(20, 400, 12, 12), new Trailing(75, 12, 12)));
        setSize(460, 300);
    }

    private JProgressBar getJProgressBar0()
    {
        if (jProgressBar0 == null) {
            jProgressBar0 = new JProgressBar();
        }
        return jProgressBar0;
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

    private JTextField getJTextFieldNumber()
    {
        if (jTextFieldNumber == null) {
            jTextFieldNumber = new JTextField();
            jTextFieldNumber.setText("1");
        }
        return jTextFieldNumber;
    }

    private JLabel getJLabelLIN()
    {
        if (jLabelLIN == null) {
            jLabelLIN = new JLabel();
            jLabelLIN.setText("Least Iden Num");
        }
        return jLabelLIN;
    }

    private MyJFileChooser getReschooser()
    {
        if (this.reschooser == null) {
            this.reschooser = new MyJFileChooser(file);
            this.reschooser.setFileFilter(new String[]{"xml"},
                    " XML file (*.xml)");
        }
        return reschooser;
    }

    private JButton getJButtonResult()
    {
        if (jButtonResult == null) {
            jButtonResult = new JButton();
            jButtonResult.setText("...");
            jButtonResult.addActionListener(this);
        }
        return jButtonResult;
    }

    private JTextField getJTextFieldResult()
    {
        if (jTextFieldResult == null) {
            jTextFieldResult = new JTextField();
        }
        return jTextFieldResult;
    }

    private JLabel getJLabelResult()
    {
        if (jLabelResult == null) {
            jLabelResult = new JLabel();
            jLabelResult.setText("Result file");
        }
        return jLabelResult;
    }

    private MyJFileChooser getPeakchooser()
    {
        if (this.peakchooser == null) {
            this.peakchooser = new MyJFileChooser(file);
            this.peakchooser.setFileFilter(new String[]{"mzxml"},
                    " MzXML file (*.mzxml)");
        }
        return peakchooser;
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

    private JButton getJButtonPeak()
    {
        if (jButtonPeak == null) {
            jButtonPeak = new JButton();
            jButtonPeak.setText("...");
            jButtonPeak.addActionListener(this);
        }
        return jButtonPeak;
    }

    private JTextField getJTextFieldPeak()
    {
        if (jTextFieldPeak == null) {
            jTextFieldPeak = new JTextField();
        }
        return jTextFieldPeak;
    }

    private JLabel getJLabelPeak()
    {
        if (jLabelPeak == null) {
            jLabelPeak = new JLabel();
            jLabelPeak.setText("Peak file");
        }
        return jLabelPeak;
    }

    private void selectPanel(int glycoType)
    {
        switch (glycoType) {
            case 0: {

                if (jPanel0 == null) {

                    jPanel0 = new JPanel();
                    add(jPanel0, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 0, 10, 10)));
/*				
				jPanel0 = new JPanel();
				jPanel0.setLayout(new GroupLayout());
				jPanel0.setSize(460, 155);
				jPanel0.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
				
				add(jPanel0, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 0, 10, 10)));
				setSize(465, 450);
				setLocationRelativeTo(m2);
*/
                } else {

                    remove(jPanel0);
//				jPanel0 = null;

                    setSize(460, 253);
                    repaint();
                    setLocationRelativeTo(m2);
/*				
				jPanel0 = new JPanel();
				jPanel0.setLayout(new GroupLayout());
				jPanel0.setSize(460, 155);
				jPanel0.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
				jPanel0.updateUI();
				
				add(jPanel0, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 0, 10, 10)));
				setSize(465, 450);
				repaint();
				setLocationRelativeTo(m2);
*/
                }
                break;
            }

            case 1: {

                NGlycoParaPanel glyParaPanel = new NGlycoParaPanel(false, false);
                jPanelGlycoPara = glyParaPanel;

                jPanel0 = new JPanel();
                jPanel0.setLayout(new GroupLayout());
//			jPanel0.add(glyParaPanel, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 240, 10, 10)));
                jPanel0.setSize(460, 240);
                jPanel0.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
                jPanel0.updateUI();

                add(jPanel0, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 240, 10, 10)));
                setSize(465, 550);
                repaint();
                setLocationRelativeTo(m2);

                break;
            }

            case 2: {

                NGlycoParaPanel glyParaPanel = new NGlycoParaPanel(false, true);
                jPanelGlycoPara = glyParaPanel;

                jPanel0 = new JPanel();
                jPanel0.setLayout(new GroupLayout());
//			jPanel0.add(glyParaPanel, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 240, 10, 10)));
                jPanel0.setSize(460, 240);
                jPanel0.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
                jPanel0.updateUI();

                add(jPanel0, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 240, 10, 10)));
                setSize(465, 550);
                repaint();
                setLocationRelativeTo(m2);
                break;
            }
        }
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
     * Main entry of the class.
     * Note: This class is only created so that you can easily preview the result at runtime.
     * It is not expected to be managed by the designer.
     * You can modify it as you like.
     */
    public static void main(String[] args)
    {
        installLnF();
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                LPairCreateFrame frame = new LPairCreateFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setTitle("LFreeCreateFrame");
                frame.setPreferredSize(frame.getSize());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object obj = e.getSource();

        if (obj == this.getJButtonClose()) {
            this.dispose();
            return;
        }

        if (obj == this.getJButtonPeak()) {
            int value = this.getPeakchooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                File peakfile = this.getPeakchooser().getSelectedFile();
                this.getJTextFieldPeak().setText(peakfile.getAbsolutePath());
                this.getJTextFieldResult().setText(peakfile.getAbsolutePath().substring(0,
                        peakfile.getAbsolutePath().length() - 5) + "pxml");
            }
            return;
        }

        if (obj == this.getJButtonResult()) {
            int value = this.getReschooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION)
                this.getJTextFieldResult().setText(
                        this.getReschooser().getSelectedFile().getAbsolutePath() + ".pxml");
            return;
        }

        try {

            if (obj == this.getJButtonStart()) {

                if (this.reader != null) {
                    String peakStr = this.getJTextFieldPeak().getText();
                    if (peakStr == null || peakStr.length() == 0) {
                        throw new NullPointerException("The peak file path is null.");
                    }

                    String resStr = this.getJTextFieldResult().getText();
                    if (resStr == null || resStr.length() == 0) {
                        throw new NullPointerException("The result file path is null.");
                    }

                    int leastN = Integer.parseInt(this.getJTextFieldNumber().getText());

                    switch (glycoType) {
                        case 0: {
                            LFreeQuanThread thread = new LFreeQuanThread(reader, peakStr, resStr,
                                    leastN, jProgressBar0, this);

                            thread.start();
                        }
                        case 1: {
                            GlycoJudgeParameter para = GlycoJudgeParameter.defaultParameter();
                            NGlycoLFreeQuanThread thread = new NGlycoLFreeQuanThread(reader, peakStr,
                                    resStr, para, 0, jProgressBar0, this);

                            thread.start();
                        }
                        case 2: {

                        }
                    }
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private class LFreeQuanThread extends Thread
    {
        private String peak;
        private String output;
        private IFilteredPeptideListReader reader;
        private JProgressBar bar;
        private int leastN;
        private LFreeCreateFrame frame;

        private LFreeQuanThread(IFilteredPeptideListReader reader, String peak,
                                String output, int leastN, JProgressBar bar, LFreeCreateFrame frame)
        {

            this.reader = reader;
            this.peak = peak;
            this.output = output;
            this.leastN = leastN;
            this.bar = bar;
            this.frame = frame;
        }

        public void run()
        {

            this.bar.setStringPainted(true);
            this.bar.setString("Processing...");
            this.bar.setIndeterminate(true);
            getJButtonStart().setEnabled(false);

            LFreeQuanTask task = null;
            try {
                task = new LFreeQuanTask(reader, peak, output, leastN);

                while (task.hasNext()) {
                    task.processNext();
                }

                task.dispose();

            } catch (FastaDataBaseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                task.dispose();
            }

            this.bar.setString("Complete");
            getJButtonStart().setEnabled(true);

            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            frame.dispose();
        }

    }

    private class NGlycoLFreeQuanThread extends Thread
    {
        private String peak;
        private String output;
        private IFilteredPeptideListReader reader;
        private GlycoJudgeParameter jpara;
        private JProgressBar bar;
        private int glycoType;
        private LFreeCreateFrame frame;

        private NGlycoLFreeQuanThread(IFilteredPeptideListReader reader, String peak,
                                      String output, GlycoJudgeParameter jpara, int glycoType, JProgressBar bar, LFreeCreateFrame frame)
        {
            this.reader = reader;
            this.peak = peak;
            this.output = output;
            this.jpara = jpara;
            this.glycoType = glycoType;
            this.bar = bar;
            this.frame = frame;
        }

        public void run()
        {
            this.bar.setStringPainted(true);
            this.bar.setString("Processing...");
            this.bar.setIndeterminate(true);
            getJButtonStart().setEnabled(false);

            GlycoLFreeQuanTask2 task = null;

            try {
                task = new GlycoLFreeQuanTask2(reader, peak, output, jpara, glycoType);

                while (task.hasNext()) {
                    task.processNext();
                }
                task.dispose();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }

            this.bar.setString("Complete");
            getJButtonStart().setEnabled(true);

            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            frame.dispose();

            GlycoLFreeRowGetter2 getter = new GlycoLFreeRowGetter2(task.createReader());
            NGlycoMatchViewPanel panel = new NGlycoMatchViewPanel(getter);

            m2.addTabbedPane(panel);
            panel.getJButtonClose().addActionListener(m2);
            m2.addToCloseList(panel.getJButtonClose());
        }
    }
}
