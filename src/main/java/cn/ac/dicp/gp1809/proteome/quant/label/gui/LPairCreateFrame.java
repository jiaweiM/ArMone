/*
 ******************************************************************************
 * File:LPairLoadPanel.java * * * Created on 2010-6-29
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.gui;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.Quan.label.GlycoLPRowGetter;
import cn.ac.dicp.gp1809.glyco.Quan.label.GlycoLabelQuanTask;
import cn.ac.dicp.gp1809.glyco.gui.NGlycoParaPanel;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IFilteredPeptideListReader;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.gui2.MainGui2;
import cn.ac.dicp.gp1809.proteome.quant.gui.PepPairViewPanel;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelQuanTask;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;
import cn.ac.dicp.gp1809.proteome.quant.label.multiple.MutilLabelQuanTask;
import cn.ac.dicp.gp1809.proteome.quant.profile.IO.FeaturesPagedRowGetter;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import javax.swing.*;
import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

//VS4E -- DO NOT REMOVE THIS LINE!
public class LPairCreateFrame extends JFrame implements ActionListener, ItemListener
{

    private static final long serialVersionUID = 1L;
    private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
    private LabelType type;
    private JPanel jPanel0;
    private JPanel jPanelLabelType;
    private JPanel jPanelGlycoPara;
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
    private PepPairViewPanel pepPairViewPanel;
    private MainGui2 m2;
    private JComboBox<Object> jComboBoxLabelType;
    private JLabel jLabelLabelType;
    private JLabel jLabelGlyco;
    private JButton jButtonClose;
    private File file;
    private int glycoType;
    private JProgressBar jProgressBar0;

    public LPairCreateFrame()
    {
        initComponents();
    }

    public LPairCreateFrame(LabelType type, IFilteredPeptideListReader reader, int glycoType, File file)
    {

        this.type = type;
        this.reader = reader;
        this.file = file;
        this.glycoType = glycoType;
        initComponents();
        this.selectPanel();
    }

    public LPairCreateFrame(LabelType type, IFilteredPeptideListReader reader, MainGui2 m2,
            int glycoType, File file)
    {

        this.type = type;
        this.reader = reader;
        this.m2 = m2;
        this.file = file;
        this.glycoType = glycoType;
        initComponents();
        this.selectPanel();
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
                JFrame frame = new LPairCreateFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setTitle("LPairLoadPanel");
                LPairCreateFrame content = new LPairCreateFrame();
                content.setPreferredSize(content.getSize());
                frame.add(content, BorderLayout.CENTER);
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
        add(getJButtonPeak(), new Constraints(new Leading(365, 30, 10, 10), new Trailing(185, 10, 10)));
        add(getJLabelPeak(), new Constraints(new Leading(20, 10, 10), new Trailing(185, 10, 10)));
        add(getJTextFieldPeak(), new Constraints(new Leading(90, 252, 12, 12), new Trailing(185, 10, 10)));
        add(getJLabelResult(), new Constraints(new Leading(20, 6, 6), new Trailing(125, 10, 10)));
        add(getJButtonResult(), new Constraints(new Leading(365, 30, 6, 6), new Trailing(125, 10, 10)));
        add(getJTextFieldResult(), new Constraints(new Leading(90, 252, 6, 6), new Trailing(125, 10, 10)));
        add(getJLabelLabelType(), new Constraints(new Leading(20, 6, 6), new Trailing(245, 6, 6)));
        add(getJComboBoxLabelType(), new Constraints(new Leading(92, 120, 10, 10), new Trailing(240, 6, 6)));
//		add(getJLabelGlyco(), new Constraints(new Leading(244, 10, 10), new Trailing(245, 6, 6)));
//		add(getJComboBoxGlyco(), new Constraints(new Leading(320, 100, 10, 10), new Trailing(240, 6, 6)));
        add(getJButtonStart(), new Constraints(new Leading(90, 10, 10), new Trailing(28, 6, 6)));
        add(getJButtonClose(), new Constraints(new Leading(280, 6, 6), new Trailing(28, 6, 6)));
        add(getJProgressBar0(), new Constraints(new Leading(20, 400, 12, 12), new Trailing(75, 12, 12)));
        setSize(460, 500);
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

    private JLabel getJLabelGlyco()
    {
        if (jLabelGlyco == null) {
            jLabelGlyco = new JLabel();
            jLabelGlyco.setText("GlycoQuant");
        }
        return jLabelGlyco;
    }

    private JLabel getJLabelLabelType()
    {
        if (jLabelLabelType == null) {
            jLabelLabelType = new JLabel();
            jLabelLabelType.setText("Label type");
        }
        return jLabelLabelType;
    }

    private JComboBox<Object> getJComboBoxLabelType()
    {
        if (jComboBoxLabelType == null) {
            jComboBoxLabelType = new JComboBox<Object>();
            jComboBoxLabelType.setModel(new DefaultComboBoxModel(new Object[]{LabelType.SILAC,
                    LabelType.Dimethyl, LabelType.ICPL, LabelType.SixLabel, LabelType.User_Defined}));
            jComboBoxLabelType.addItemListener(this);
        }
        return jComboBoxLabelType;
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

    private JPanel getJPanel0()
    {
        if (jPanel0 == null) {
            jPanel0 = new JPanel();
//			jPanel0.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
            jPanel0.setLayout(new GroupLayout());
        }
        return jPanel0;
    }

    private void selectPanel()
    {

        JPanel panel = null;

        switch (type) {

            case Dimethyl:
                panel = getDimePanel();
                break;

            case SILAC:
                panel = getSilacPanel();
                break;

            case ICPL:
                panel = getIcplPanel();
                break;

            case SixLabel:
                panel = getSixPanel();
                break;

            case User_Defined:
                panel = getUDFPanel();
                break;

            default:
                break;
        }

        switch (glycoType) {

            case 0: {

                if (jPanel0 == null) {

                    jPanelLabelType = panel;
                    jPanel0 = new JPanel();
                    jPanel0.setLayout(new GroupLayout());
                    jPanel0.add(panel, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 155, 10, 10)));
                    jPanel0.setSize(460, 155);
//					jPanel0.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));

                    add(jPanel0, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 155, 10, 10)));
                    setSize(465, 450);
                    setLocationRelativeTo(m2);

                } else {
                    jPanelLabelType = panel;
                    remove(jPanel0);
                    jPanel0 = new JPanel();
                    jPanel0.setLayout(new GroupLayout());
                    jPanel0.add(panel, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 155, 10, 10)));
                    jPanel0.setSize(460, 155);
//					jPanel0.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
                    jPanel0.updateUI();

                    add(jPanel0, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 155, 10, 10)));
                    setSize(465, 450);
                    repaint();
                    setLocationRelativeTo(m2);
                }
                break;
            }

            case 1: {

                jPanelLabelType = panel;
                NGlycoParaPanel glyParaPanel = new NGlycoParaPanel(true, false);
                jPanelGlycoPara = glyParaPanel;

                if (jPanel0 != null)
                    remove(jPanel0);

                jPanel0 = new JPanel();
                jPanel0.setLayout(new GroupLayout());
                jPanel0.add(panel, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 155, 10, 10)));
                jPanel0.add(glyParaPanel, new Constraints(new Leading(0, 460, 10, 10), new Leading(155, 185, 10, 10)));
                jPanel0.setSize(460, 185);
//				jPanel0.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
                jPanel0.updateUI();

                add(jPanel0, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 10, 10)));
                setSize(465, 480);
//				setSize(465, 710);
                repaint();
                setLocationRelativeTo(m2);

                break;
            }

            case 2: {

                jPanelLabelType = panel;
                NGlycoParaPanel glyParaPanel = new NGlycoParaPanel(true, true);
                jPanelGlycoPara = glyParaPanel;

                if (jPanel0 != null)
                    remove(jPanel0);

                jPanel0 = new JPanel();
                jPanel0.setLayout(new GroupLayout());
                jPanel0.add(panel, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 155, 10, 10)));
                jPanel0.add(glyParaPanel, new Constraints(new Leading(0, 460, 10, 10), new Leading(155, 185, 10, 10)));
                jPanel0.setSize(460, 185);
//				jPanel0.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
                jPanel0.updateUI();

                add(jPanel0, new Constraints(new Leading(0, 460, 10, 10), new Leading(0, 10, 10)));
                setSize(465, 480);
//				setSize(465, 710);
                repaint();
                setLocationRelativeTo(m2);
                break;
            }
        }
    }

    private LInfoSILACPanel getSilacPanel()
    {
        return new LInfoSILACPanel();
    }

    private LInfoDimePanel getDimePanel()
    {
        return new LInfoDimePanel();
    }

    private LInfoICPLPanel getIcplPanel()
    {
        return new LInfoICPLPanel();
    }

    private LInfoUDFPanel getUDFPanel()
    {
        return new LInfoUDFPanel();
    }

    private LInfoSixPanel getSixPanel()
    {
        return new LInfoSixPanel();
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

    private void setMod(LabelType type)
    {
        HashMap<Character, Double> symbolMap = new HashMap<Character, Double>();
        ISearchParameter para = reader.getSearchParameter();
        AminoacidModification aamodif = para.getVariableInfo();
        LabelInfo[][] infos = type.getInfo();
        Modif[] mods = aamodif.getModifications();
        for (int i = 0; i < infos.length; i++) {
            for (int j = 0; j < infos[i].length; j++) {
                LabelInfo info = infos[i][j];
                double mass = info.getMass();
                for (int k = 0; k < mods.length; k++) {
                    Modif m = mods[k];
                    double s = m.getMass();
                    if (Math.abs(mass - s) < 0.1) {
                        info.setSymbol(m.getSymbol());
                        symbolMap.put(m.getSymbol(), s);
                    }
                }
            }
        }
        type.setSymbolSet(symbolMap);
    }

    public PepPairViewPanel getPepPairViewPanel()
    {
        return this.pepPairViewPanel;
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

            LabelType t = null;
            switch (type) {
                case Dimethyl:
                    t = ((LInfoDimePanel) jPanelLabelType).getLabelType();
                    break;

                case SILAC:
                    t = ((LInfoSILACPanel) jPanelLabelType).getLabelType();
                    break;

                case ICPL:
                    t = ((LInfoICPLPanel) jPanelLabelType).getLabelType();
                    break;

                case User_Defined:
                    t = ((LInfoUDFPanel) jPanelLabelType).getLabelType();
                    break;

                case SixLabel:
                    t = ((LInfoSixPanel) jPanelLabelType).getLabelType();
                    break;

                default:
                    throw new IllegalArgumentException("Unknown label type: "
                            + type);
            }

            this.setMod(t);

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

                    switch (glycoType) {

                        case 0: {

                            LabelQuanThread thread = new LabelQuanThread(reader, peakStr,
                                    resStr, t, jProgressBar0, this);
                            thread.start();

                            break;
                        }

                        case 1: {

//							NGlycoParaPanel glyParaPanel = (NGlycoParaPanel) jPanelGlycoPara;
//							GlycoJudgeParameter para = glyParaPanel.getPara();
                            GlycoJudgeParameter para = GlycoJudgeParameter.defaultParameter();
                            NGlycoQuanThread thread = new NGlycoQuanThread(reader, peakStr, resStr, t,
                                    para, jProgressBar0, this);

                            thread.start();

                            break;
                        }

                        case 2: {

//							NGlycoParaPanel glyParaPanel = (NGlycoParaPanel) jPanelGlycoPara;
//							GlycoJudgeParameter para = glyParaPanel.getPara();
                            GlycoJudgeParameter para = GlycoJudgeParameter.defaultParameter();
                            GlycoLabelQuanTask task = new GlycoLabelQuanTask(reader, peakStr,
                                    resStr, para, t, 1);
                            try {
                                getJButtonStart().setEnabled(false);

                                while (task.hasNext()) {
                                    task.processNext();
                                }
                            } finally {
                                task.dispose();
                                this.dispose();
                            }

                            GlycoLPRowGetter getter = null;
                            try {
                                getter = new GlycoLPRowGetter(task.createReader());
                                this.pepPairViewPanel = new PepPairViewPanel(getter);
                                this.m2.addTabbedPane(pepPairViewPanel);
                                pepPairViewPanel.getJButtonClose().addActionListener(m2);
                                m2.addToCloseList(pepPairViewPanel.getJButtonClose());

                            } catch (Exception ex2) {
                                // TODO Auto-generated catch block
                                ex2.printStackTrace();
                            }

                            break;
                        }
                    }
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

    }

    /* (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    @Override
    public void itemStateChanged(ItemEvent e)
    {
        // TODO Auto-generated method stub
        Object obj = e.getSource();

        if (obj == this.getJComboBoxLabelType()) {

            Object type = this.jComboBoxLabelType.getSelectedItem();
            this.type = (LabelType) type;
            this.selectPanel();

            return;
        }

    }

    private class LabelQuanThread extends Thread
    {

        private IFilteredPeptideListReader reader;
        private String peak;
        private String output;
        private LabelType type;
        private JProgressBar bar;
        private LPairCreateFrame frame;

        private LabelQuanThread(IFilteredPeptideListReader reader, String peak, String output,
                LabelType type, JProgressBar bar, LPairCreateFrame frame)
        {

            this.reader = reader;
            this.peak = peak;
            this.output = output;
            this.type = type;
            this.bar = bar;
            this.frame = frame;
        }

        public void run()
        {

            this.bar.setStringPainted(true);
            this.bar.setString("Processing...");
            this.bar.setIndeterminate(true);
            getJButtonStart().setEnabled(false);

            LabelQuanTask task = null;

            try {

                if (type == LabelType.SixLabel) {
                    task = new MutilLabelQuanTask(reader, peak, output, 6, 0);
                } else {
                    task = new LabelQuanTask(reader, peak, output, type, 0);
                }

                while (task.hasNext()) {
                    task.processNext();
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FastaDataBaseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            } finally {

                task.dispose();
            }

            this.bar.setString("Complete");
            getJButtonStart().setEnabled(true);

            try {
                sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            frame.dispose();

            FeaturesPagedRowGetter getter = null;
            try {
                getter = new FeaturesPagedRowGetter(task.createReader());
                pepPairViewPanel = new PepPairViewPanel(getter);
                m2.addTabbedPane(pepPairViewPanel);
                pepPairViewPanel.getJButtonClose().addActionListener(m2);
                m2.addToCloseList(pepPairViewPanel.getJButtonClose());

            } catch (Exception ex2) {
                // TODO Auto-generated catch block
                ex2.printStackTrace();
            }
        }
    }

    private class NGlycoQuanThread extends Thread
    {

        private IFilteredPeptideListReader reader;
        private String peak;
        private String output;
        private LabelType type;
        private GlycoJudgeParameter para;
        private JProgressBar bar;
        private LPairCreateFrame frame;

        private NGlycoQuanThread(IFilteredPeptideListReader reader, String peak, String output,
                LabelType type, GlycoJudgeParameter para, JProgressBar bar, LPairCreateFrame frame)
        {

            this.reader = reader;
            this.peak = peak;
            this.output = output;
            this.type = type;
            this.para = para;
            this.bar = bar;
            this.frame = frame;
        }

        public void run()
        {

            this.bar.setStringPainted(true);
            this.bar.setString("Processing...");
            this.bar.setIndeterminate(true);
            getJButtonStart().setEnabled(false);

            GlycoLabelQuanTask task = null;

            try {

                task = new GlycoLabelQuanTask(reader, peak,
                        output, para, type, 0);
                while (task.hasNext()) {
                    task.processNext();
                }

                task.dispose();

            } catch (FastaDataBaseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XMLStreamException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            this.bar.setString("Complete");
            getJButtonStart().setEnabled(true);

            try {
                sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            frame.dispose();

            GlycoLPRowGetter getter = null;
            try {
                getter = new GlycoLPRowGetter(task.createReader());
                pepPairViewPanel = new PepPairViewPanel(getter);
                m2.addTabbedPane(pepPairViewPanel);
                pepPairViewPanel.getJButtonClose().addActionListener(m2);
                m2.addToCloseList(pepPairViewPanel.getJButtonClose());

            } catch (Exception ex2) {
                // TODO Auto-generated catch block
                ex2.printStackTrace();
            }
        }
    }

}
