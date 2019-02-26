/*
 ******************************************************************************
 * File:MainGui2.java * * * Created on 2011-8-3
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import cn.ac.dicp.gp1809.glyco.Iden.GlycoIdenPagedRowGetter;
import cn.ac.dicp.gp1809.glyco.Iden.GlycoIdenXMLReader;
import cn.ac.dicp.gp1809.glyco.Quan.label.GlycoLPRowGetter;
import cn.ac.dicp.gp1809.glyco.Quan.label.GlycoLabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.glyco.Quan.labelFree2.GlycoLFFeasXMLReader2;
import cn.ac.dicp.gp1809.glyco.Quan.labelFree2.GlycoLFreeRowGetter2;
import cn.ac.dicp.gp1809.glyco.drawjf.MatchSpecDrawFrame;
import cn.ac.dicp.gp1809.glyco.gui.*;
import cn.ac.dicp.gp1809.proteome.APIVASEII.gui.APVFrame;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IFilteredPeptideListReader;
import cn.ac.dicp.gp1809.proteome.gui.BatchPplCreatorPanel;
import cn.ac.dicp.gp1809.proteome.gui.ProteinIntegrDlg;
import cn.ac.dicp.gp1809.proteome.penn.PENNFrm;
import cn.ac.dicp.gp1809.proteome.phosval.BatchDrawDlg;
import cn.ac.dicp.gp1809.proteome.quant.counter2.SPCounterFrame;
import cn.ac.dicp.gp1809.proteome.quant.gui.PepPairViewPanel;
import cn.ac.dicp.gp1809.proteome.quant.gui.QModTableFrame;
import cn.ac.dicp.gp1809.proteome.quant.gui.QResOutFrame;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.label.gui.LPairCreateFrame;
import cn.ac.dicp.gp1809.proteome.quant.label.gui.LabelGradWriterFrame;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.gui.LFreeBatchWriterFrame;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.gui.LFreeCreateFrame;
import cn.ac.dicp.gp1809.proteome.quant.profile.IO.AbstractFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.profile.IO.FeaturesPagedRowGetter;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.repeatStat.gui.RepeatStatFrame;
import cn.ac.dicp.gp1809.proteome.quant.turnover.gui.SixTurnOverFrame;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfViewer;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import cn.ac.dicp.gp1809.util.gui.ProcessingDlgNew;

import javax.swing.GroupLayout.Alignment;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class MainGui2 extends JFrame implements ActionListener, ChangeListener
{
    private static final long serialVersionUID = 1L;
    private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
    private JTabbedPane jTabbedPane0;
    private MainPanel mainPanel;
    private PTMPanel pTMPanel0;
    private JMenuItem jMenuItemOpen;
    private JMenu jMenuFile;
    private JMenuBar jMenuBar0;
    private JMenuItem jMenuItemExit;
    private JScrollPane jScrollPane0;
    private JTextArea jTextArea0;
    private MyJFileChooser quanResultChooser;
    private ArrayList<Object> closeList;
    private JMenuItem jMenuItemProtein;
    private JMenu jMenuExport;
    private JMenuItem jMenuItemSpectra;
    private JMenuItem jMenuItemPTM;
    private JMenuItem jMenuItemProSeq;
    private JMenuItem jMenuItemLabel;
    private JMenu jMenuQuan;
    private JMenuItem jMenuItemModQuant;
    private JMenuItem jMenuItemGlycoQuant;
    private JMenuItem jMenuItemGlycoSpec;
    private JMenuItem jMenuItemQuant;
    private JMenuItem jMenuItemLFree;
    private JMenu jMenuGlycoQuant;
    private JMenuItem jMenuItemGlycoMatch;
    private JMenuItem jMenuItemGlycoLabelQuant;
    private JMenu jMenuTools;
    private JMenuItem jMenuItenMgfviewer;

    public MainGui2()
    {
        initComponents();
        this.closeList = new ArrayList<>();
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
        SwingUtilities.invokeLater(() -> {

            MainGui2 frame = new MainGui2();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setTitle("ArMone v2.0");
            frame.getContentPane().setPreferredSize(frame.getSize());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }

    private void initComponents()
    {
        javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(getJTabbedPane0(), javax.swing.GroupLayout.PREFERRED_SIZE, 1062, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(getJTabbedPane0(), javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        getContentPane().setLayout(groupLayout);
        setJMenuBar(getJMenuBar0());
        setSize(1080, 755);
    }

    private JMenuItem getJMenuItemMgfviewer()
    {
        if (jMenuItenMgfviewer == null) {
            jMenuItenMgfviewer = new JMenuItem();
            jMenuItenMgfviewer.setText("Mgf viewer");
            jMenuItenMgfviewer.addActionListener(this);
            jMenuItenMgfviewer.setEnabled(true);
        }
        return jMenuItenMgfviewer;
    }

    private JMenu getJMenuTools()
    {
        if (jMenuTools == null) {
            jMenuTools = new JMenu();
            jMenuTools.setText("Tools");
            jMenuTools.add(getJMenuItemMgfviewer());
        }
        return jMenuTools;
    }

    private JMenuItem getJMenuItemGlycoMatch()
    {
        if (jMenuItemGlycoMatch == null) {
            jMenuItemGlycoMatch = new JMenuItem();
            jMenuItemGlycoMatch.setText("Glyco Spectra match");
            jMenuItemGlycoMatch.addActionListener(this);
            jMenuItemGlycoMatch.setEnabled(false);
        }
        return jMenuItemGlycoMatch;
    }

    private JMenuItem getJMenuItemGlycoLabelQuan()
    {
        if (jMenuItemGlycoLabelQuant == null) {
            jMenuItemGlycoLabelQuant = new JMenuItem();
            jMenuItemGlycoLabelQuant.setText("Glyco label Quant");
            jMenuItemGlycoLabelQuant.addActionListener(this);
            jMenuItemGlycoLabelQuant.setEnabled(false);
        }
        return jMenuItemGlycoLabelQuant;
    }

    private JMenu getJMenuGlycoQuant()
    {
        if (jMenuGlycoQuant == null) {
            jMenuGlycoQuant = new JMenu();
            jMenuGlycoQuant.setText("GlycoQuant");
            jMenuGlycoQuant.add(getJMenuItemGlycoMatch());
//			getJMenuItemGlycoLabelQuan();
            jMenuGlycoQuant.add(getJMenuItemGlycoLabelQuan());
        }
        return jMenuGlycoQuant;
    }

    private JMenuItem getJMenuItemGlycoSpec()
    {
        if (jMenuItemGlycoSpec == null) {
            jMenuItemGlycoSpec = new JMenuItem();
            jMenuItemGlycoSpec.setText("Glyco Spectra result");
            jMenuItemGlycoSpec.addActionListener(this);
            jMenuItemGlycoSpec.setEnabled(false);
        }
        return jMenuItemGlycoSpec;
    }

    private JMenuItem getJMenuItemQuant()
    {
        if (jMenuItemQuant == null) {
            jMenuItemQuant = new JMenuItem();
            jMenuItemQuant.setText("Quant. result");
            jMenuItemQuant.addActionListener(this);
            jMenuItemQuant.setEnabled(false);
        }
        return jMenuItemQuant;
    }

    private JMenuItem getJMenuItemModQuant()
    {
        if (jMenuItemModQuant == null) {
            jMenuItemModQuant = new JMenuItem();
            jMenuItemModQuant.setText("Mod Quant result");
            jMenuItemModQuant.addActionListener(this);
            jMenuItemModQuant.setEnabled(false);
        }
        return jMenuItemModQuant;
    }

    private JMenuItem getJMenuItemGlycoQuant()
    {
        if (jMenuItemGlycoQuant == null) {
            jMenuItemGlycoQuant = new JMenuItem();
            jMenuItemGlycoQuant.setText("Glyco Quant result");
            jMenuItemGlycoQuant.addActionListener(this);
            jMenuItemGlycoQuant.setEnabled(false);
        }
        return jMenuItemGlycoQuant;
    }

    private JMenuItem getJMenuItemLFree()
    {
        if (jMenuItemLFree == null) {
            jMenuItemLFree = new JMenuItem();
            jMenuItemLFree.setText("Label-free");
            jMenuItemLFree.addActionListener(this);
            jMenuItemLFree.setEnabled(false);
        }
        return jMenuItemLFree;
    }

    private JMenu getJMenuQuan()
    {
        if (jMenuQuan == null) {
            jMenuQuan = new JMenu();
            jMenuQuan.setText("Quantitation");
            jMenuQuan.add(getJMenuItemLabel());
//			getJMenuItemLFree();
            jMenuQuan.add(getJMenuItemLFree());

        }
        return jMenuQuan;
    }

    private JMenuItem getJMenuItemLabel()
    {
        if (jMenuItemLabel == null) {
            jMenuItemLabel = new JMenuItem();
            jMenuItemLabel.setText("Isotope labeling");
            jMenuItemLabel.addActionListener(this);
            jMenuItemLabel.setEnabled(false);
        }
        return jMenuItemLabel;
    }

    private JMenuItem getJMenuItemProSeq()
    {
        if (jMenuItemProSeq == null) {
            jMenuItemProSeq = new JMenuItem();
            jMenuItemProSeq.setText("Protein sequence");
            jMenuItemProSeq.addActionListener(this);
            jMenuItemProSeq.setEnabled(false);
        }
        return jMenuItemProSeq;
    }

    private JMenuItem getJMenuItemPTM()
    {
        if (jMenuItemPTM == null) {
            jMenuItemPTM = new JMenuItem();
            jMenuItemPTM.setText("PTM information");
            jMenuItemPTM.addActionListener(this);
            jMenuItemPTM.setEnabled(false);
        }
        return jMenuItemPTM;
    }

    private JMenuItem getJMenuItemSpectra()
    {
        if (jMenuItemSpectra == null) {
            jMenuItemSpectra = new JMenuItem();
            jMenuItemSpectra.setText("Spectra");
            jMenuItemSpectra.addActionListener(this);
            jMenuItemSpectra.setEnabled(false);
        }
        return jMenuItemSpectra;
    }

    private JMenu getJMenuExport()
    {
        if (jMenuExport == null) {
            jMenuExport = new JMenu();
            jMenuExport.setText("Export");
            jMenuExport.add(getJMenuItemProtein());
            jMenuExport.add(getJMenuItemSpectra());
            jMenuExport.add(getJMenuItemPTM());
            jMenuExport.add(getJMenuItemProSeq());
            jMenuExport.addSeparator();
            jMenuExport.add(getJMenuItemQuant());
            jMenuExport.add(getJMenuItemModQuant());
            jMenuExport.addSeparator();
            jMenuExport.add(getJMenuItemGlycoQuant());
            jMenuExport.add(getJMenuItemGlycoSpec());
        }
        return jMenuExport;
    }

    private JMenuItem getJMenuItemProtein()
    {
        if (jMenuItemProtein == null) {
            jMenuItemProtein = new JMenuItem();
            jMenuItemProtein.setText("Protein & Peptide");
            jMenuItemProtein.addActionListener(this);
            jMenuItemProtein.setEnabled(false);
        }
        return jMenuItemProtein;
    }

    private JTextArea getJTextArea0()
    {
        if (jTextArea0 == null) {
            jTextArea0 = new JTextArea();
            jTextArea0.setEditable(false);
            jTextArea0.setText("\nArMone v2.0\n");
        }
        return jTextArea0;
    }

    private JScrollPane getJScrollPane0()
    {
        if (jScrollPane0 == null) {
            jScrollPane0 = new JScrollPane();
            jScrollPane0.setViewportView(getJTextArea0());
        }
        return jScrollPane0;
    }

    private JMenuItem getJMenuItemExit()
    {
        if (jMenuItemExit == null) {
            jMenuItemExit = new JMenuItem();
            jMenuItemExit.setText("Exit");
            jMenuItemExit.addActionListener(this);
        }
        return jMenuItemExit;
    }

    private JMenuBar getJMenuBar0()
    {
        if (jMenuBar0 == null) {
            jMenuBar0 = new JMenuBar();
            jMenuBar0.add(getJMenuFile());
            jMenuBar0.add(getJMenuExport());
            jMenuBar0.add(getJMenuQuan());
            jMenuBar0.add(getJMenuGlycoQuant());
            jMenuBar0.add(getJMenuTools());
        }
        return jMenuBar0;
    }

    private JMenu getJMenuFile()
    {
        if (jMenuFile == null) {
            jMenuFile = new JMenu();
            jMenuFile.setText("File");
            jMenuFile.add(getJMenuItemOpen());
            jMenuFile.add(getJMenuItemExit());
        }
        return jMenuFile;
    }

    private JMenuItem getJMenuItemOpen()
    {
        if (jMenuItemOpen == null) {
            jMenuItemOpen = new JMenuItem();
            jMenuItemOpen.setText("Open ppl");
            jMenuItemOpen.addActionListener(this);
        }
        return jMenuItemOpen;
    }

    private PTMPanel getPTMPanel0()
    {
        if (pTMPanel0 == null) {
            pTMPanel0 = new PTMPanel();
        }
        return pTMPanel0;
    }

    private MyJFileChooser getResultChooser()
    {
        if (this.quanResultChooser == null) {
            this.quanResultChooser = new MyJFileChooser();
            this.quanResultChooser.setFileFilter(new String[]{"pxml"},
                    " Peptide quantitative XML file (*.pxml)");
        }
        return quanResultChooser;
    }

    private MainPanel getPplPanelPPL()
    {
        if (mainPanel == null) {
            mainPanel = new MainPanel();
            mainPanel.setName("Main panel");

            mainPanel.getJButtonCreate().addActionListener(this);
            mainPanel.getJButtonLoading().addActionListener(this);
            mainPanel.getJButtonPenn().addActionListener(this);
            mainPanel.getJButtonApivase().addActionListener(this);

            mainPanel.getJButtonLabelFreeGenerate().addActionListener(this);
            mainPanel.getJButtonLabelFreeLoad().addActionListener(this);
            mainPanel.getJButtonLabelFreeRepeat().addActionListener(this);
            mainPanel.getJButtonLabelFreeTurnover().addActionListener(this);

            mainPanel.getJButtonLabelLoad().addActionListener(this);
            mainPanel.getJButtonLabelMerge().addActionListener(this);
            mainPanel.getJButtonLabelRepeat().addActionListener(this);
            mainPanel.getJButtonLabelTurnover().addActionListener(this);
            mainPanel.getJButtonLabelSpectralCount().addActionListener(this);

            mainPanel.getJButtonGlycoLabelLoad().addActionListener(this);
            mainPanel.getJButtonGlycoStrucIden().addActionListener(this);
            mainPanel.getJButtonLoadGlycoStruc().addActionListener(this);
            mainPanel.getJButtonLoadGlycoMatch().addActionListener(this);

            mainPanel.getJButtonOGlycoSpectra().addActionListener(this);
            mainPanel.getJButtonOGlycoValidate().addActionListener(this);
            mainPanel.add(getJScrollPane0());
        }
        return mainPanel;
    }

    private JTabbedPane getJTabbedPane0()
    {
        if (jTabbedPane0 == null) {
            jTabbedPane0 = new JTabbedPane(JTabbedPane.LEFT);
            jTabbedPane0.addTab("Main panel", getPplPanelPPL());
            jTabbedPane0.addChangeListener(this);
        }
        return jTabbedPane0;
    }

    public void addTabbedPane(Component component)
    {
        String name = component.getName();
        jTabbedPane0.addTab(name, component);
        jTabbedPane0.setSelectedComponent(component);

        if (component instanceof PeptideListViewerPanel2) {

            this.jMenuItemProtein.setEnabled(true);
            this.jMenuItemSpectra.setEnabled(true);
            this.jMenuItemPTM.setEnabled(true);
            this.jMenuItemProSeq.setEnabled(true);

            this.jMenuItemLabel.setEnabled(true);
            this.jMenuItemLFree.setEnabled(true);
            this.jMenuItemGlycoMatch.setEnabled(true);
            this.jMenuItemGlycoLabelQuant.setEnabled(true);
            this.jMenuItemQuant.setEnabled(false);
            this.jMenuItemModQuant.setEnabled(false);
            this.jMenuItemGlycoQuant.setEnabled(false);
            this.jMenuItemGlycoSpec.setEnabled(false);

        } else if (component instanceof PepPairViewPanel) {

            this.jMenuItemProtein.setEnabled(false);
            this.jMenuItemSpectra.setEnabled(false);
            this.jMenuItemPTM.setEnabled(false);
            this.jMenuItemProSeq.setEnabled(false);

            this.jMenuItemLabel.setEnabled(false);
            this.jMenuItemLFree.setEnabled(false);
            this.jMenuItemGlycoMatch.setEnabled(false);
            this.jMenuItemGlycoLabelQuant.setEnabled(false);

            if (((PepPairViewPanel) component).isGlyco()) {
                this.jMenuItemQuant.setEnabled(false);
                this.jMenuItemModQuant.setEnabled(false);
                this.jMenuItemGlycoQuant.setEnabled(true);
                this.jMenuItemGlycoSpec.setEnabled(true);

            } else {
                this.jMenuItemQuant.setEnabled(true);
                this.jMenuItemModQuant.setEnabled(true);
                this.jMenuItemGlycoQuant.setEnabled(false);
                this.jMenuItemGlycoSpec.setEnabled(false);
            }

        } else {
            this.jMenuItemProtein.setEnabled(false);
            this.jMenuItemSpectra.setEnabled(false);
            this.jMenuItemPTM.setEnabled(false);
            this.jMenuItemProSeq.setEnabled(false);

            this.jMenuItemLabel.setEnabled(false);
            this.jMenuItemQuant.setEnabled(false);
            this.jMenuItemModQuant.setEnabled(false);
            this.jMenuItemLFree.setEnabled(false);
            this.jMenuItemGlycoQuant.setEnabled(false);
            this.jMenuItemGlycoSpec.setEnabled(false);
            this.jMenuItemGlycoMatch.setEnabled(false);
            this.jMenuItemGlycoLabelQuant.setEnabled(false);
        }
    }

    private void showQuantOutputFrame(FeaturesPagedRowGetter getter)
    {
        JFrame frame = new QResOutFrame(getter);
        frame.getContentPane().setPreferredSize(frame.getSize());
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
    }

    private void showModQuantOutputFrame(FeaturesPagedRowGetter getter)
    {
        JFrame frame = new QModTableFrame(getter);
        frame.getContentPane().setPreferredSize(frame.getSize());
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);

    }

    private void showGlycoQuantOutputFrame(GlycoLPRowGetter getter)
    {
        NGlycoExportFrame frame = new NGlycoExportFrame(getter);
        frame.getContentPane().setPreferredSize(frame.getSize());
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);

    }

    private void showGlycoMatchSpecFrame(GlycoLPRowGetter getter)
    {
        MatchSpecDrawFrame frame = new MatchSpecDrawFrame(getter);
        frame.getContentPane().setPreferredSize(frame.getSize());
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
    }

    public void addToCloseList(Object button)
    {
        this.closeList.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object obj = e.getSource();

        if (obj == this.getJMenuItemOpen()) {
            boolean isload = mainPanel.load();
            if (isload) {
                PeptideListViewerPanel2 panel = mainPanel.getViewPanel();
                addTabbedPane(panel);
                panel.getJButtonClose().addActionListener(this);
                closeList.add(panel.getJButtonClose());
            }

            return;
        }

        if (obj == this.getJMenuItemExit()) {
            this.dispose();
            return;
        }

        if (obj == this.mainPanel.getJButtonCreate()) {
            BatchPplCreatorPanel jPopPanel = new BatchPplCreatorPanel();
            addTabbedPane(jPopPanel);
            jPopPanel.getJButtonClose().addActionListener(this);
            closeList.add(jPopPanel.getJButtonClose());
            return;
        }

        if (obj == this.mainPanel.getJButtonLoading()) {
            boolean isload = mainPanel.load();
            if (isload) {
                PeptideListViewerPanel2 panel = mainPanel.getViewPanel();
                addTabbedPane(panel);
                panel.getJButtonClose().addActionListener(this);
                closeList.add(panel.getJButtonClose());
            }

            return;
        }

        if (obj == this.mainPanel.getJButtonPenn()) {
            JFrame frame = new PENNFrm();
            frame.getContentPane().setPreferredSize(frame.getSize());
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);
            return;
        }

        if (obj == this.mainPanel.getJButtonApivase()) {
            JFrame frame = new APVFrame();
            frame.getContentPane().setPreferredSize(frame.getSize());
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);
            return;
        }

        if (obj == this.mainPanel.getJButtonOGlycoSpectra()) {
            OGlycoPeakConvertFrame frame = new OGlycoPeakConvertFrame();
            frame.getContentPane().setPreferredSize(frame.getSize());
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);
            return;
        }

        if (obj == this.mainPanel.getJButtonOGlycoValidate()) {
            OGlycoValidateFrame frame = new OGlycoValidateFrame();
            frame.getContentPane().setPreferredSize(frame.getSize());
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);
            return;
        }

        if (obj == this.getJMenuItemProtein()) {

            Component com = jTabbedPane0.getSelectedComponent();
            if (com instanceof PeptideListViewerPanel2) {
                PeptideListViewerPanel2 pepview = (PeptideListViewerPanel2) com;
                ProteinIntegrDlg dlg = new ProteinIntegrDlg(pepview.getPepGetter().getSelectedPeptideReader(), pepview.getFile());
                dlg.getContentPane().setPreferredSize(dlg.getSize());
                dlg.pack();
                dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                dlg.setLocationRelativeTo(this);
                dlg.setVisible(true);
            }

            return;
        }

        if (obj == this.getJMenuItemSpectra()) {
            Component com = jTabbedPane0.getSelectedComponent();
            if (com instanceof PeptideListViewerPanel2) {
                PeptideListViewerPanel2 pepview = (PeptideListViewerPanel2) com;
                BatchDrawDlg frame = new BatchDrawDlg(this, pepview.getPepGetter().getSelectedPeptideReader());
                frame.pack();
                frame.setLocationRelativeTo(this);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            }
            return;
        }

        if (obj == this.getJMenuItemPTM()) {
            Component com = jTabbedPane0.getSelectedComponent();
            if (com instanceof PeptideListViewerPanel2) {
                PeptideListViewerPanel2 pepview = (PeptideListViewerPanel2) com;
                ModInfoFrame modframe = new ModInfoFrame(pepview.getPepGetter(), pepview.getFile());
                modframe.getContentPane().setPreferredSize(modframe.getSize());
                modframe.pack();
                modframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                modframe.setLocationRelativeTo(this);
                modframe.setVisible(true);
                modframe.setAlwaysOnTop(true);
            }
            return;
        }

        if (obj == this.getJMenuItemProSeq()) {
            Component com = jTabbedPane0.getSelectedComponent();
            if (com instanceof PeptideListViewerPanel2) {
                PeptideListViewerPanel2 pepview = (PeptideListViewerPanel2) com;
                FastaExpFrame frame = new FastaExpFrame(pepview.getPepGetter().getSelectedAllPeptideReader());
                frame.getContentPane().setPreferredSize(frame.getSize());
                frame.pack();
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setLocationRelativeTo(this);
                frame.setVisible(true);
                frame.setAlwaysOnTop(true);
            }
            return;
        }

        if (obj == this.getJMenuItemQuant()) {
            Component com = jTabbedPane0.getSelectedComponent();
            if (com instanceof PepPairViewPanel) {
                PepPairViewPanel pepPairView = (PepPairViewPanel) com;
                FeaturesPagedRowGetter getter = pepPairView.getFeaturesGetter();
                showQuantOutputFrame(getter);
            }
            return;
        }

        if (obj == this.getJMenuItemModQuant()) {
            Component com = jTabbedPane0.getSelectedComponent();
            if (com instanceof PepPairViewPanel) {
                PepPairViewPanel pepPairView = (PepPairViewPanel) com;
                FeaturesPagedRowGetter getter = pepPairView.getFeaturesGetter();
                showModQuantOutputFrame(getter);
            }
            return;
        }

        if (obj == this.getJMenuItemGlycoQuant()) {
            Component com = jTabbedPane0.getSelectedComponent();
            if (com instanceof PepPairViewPanel) {
                PepPairViewPanel pepPairView = (PepPairViewPanel) com;
                FeaturesPagedRowGetter getter = pepPairView.getFeaturesGetter();
                showGlycoQuantOutputFrame((GlycoLPRowGetter) getter);
            }
            return;
        }

        if (obj == this.getJMenuItemGlycoSpec()) {

            Component com = jTabbedPane0.getSelectedComponent();
            if (com instanceof PepPairViewPanel) {
                PepPairViewPanel pepPairView = (PepPairViewPanel) com;
                FeaturesPagedRowGetter getter = pepPairView.getFeaturesGetter();
                showGlycoMatchSpecFrame((GlycoLPRowGetter) getter);
            }
            return;
        }

        if (obj == this.getJMenuItemLabel()) {

            Component com = jTabbedPane0.getSelectedComponent();
            if (com instanceof PeptideListViewerPanel2) {

                PeptideListViewerPanel2 pepview = (PeptideListViewerPanel2) com;
                IFilteredPeptideListReader reader = pepview.getPepGetter().getSelectedPeptideReader();
                LPairCreateFrame qFrame = new LPairCreateFrame(LabelType.SILAC, reader, this, 0, pepview.getFile());
                qFrame.getContentPane().setPreferredSize(qFrame.getSize());
                qFrame.pack();
                qFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                qFrame.setLocationRelativeTo(this);
                qFrame.setVisible(true);

                return;
            }
        }

        if (obj == this.getJMenuItemLFree()) {

            Component com = jTabbedPane0.getSelectedComponent();
            if (com instanceof PeptideListViewerPanel2) {

                PeptideListViewerPanel2 pepview = (PeptideListViewerPanel2) com;
                IFilteredPeptideListReader reader = pepview.getPepGetter().getSelectedPeptideReader();
                LFreeCreateFrame qFrame = new LFreeCreateFrame(reader, this, 0, pepview.getFile());
                qFrame.getContentPane().setPreferredSize(qFrame.getSize());
                qFrame.pack();
                qFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                qFrame.setLocationRelativeTo(this);
                qFrame.setVisible(true);
                qFrame.setAlwaysOnTop(true);

                return;
            }
        }

        if (obj == this.mainPanel.getJButtonLabelLoad()) {
            int value = this.getResultChooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                File file = this.getResultChooser().getSelectedFile();
                try {
                    AbstractFeaturesXMLReader reader = new LabelFeaturesXMLReader(file);
                    FeaturesPagedRowGetter getter = new FeaturesPagedRowGetter(reader);
                    PepPairViewPanel pepPairViewPanel = new PepPairViewPanel(getter);
                    addTabbedPane(pepPairViewPanel);
                    pepPairViewPanel.getJButtonClose().addActionListener(this);
                    closeList.add(pepPairViewPanel.getJButtonClose());

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return;
        }

        if (obj == this.mainPanel.getJButtonGlycoLabelLoad()) {
            int value = this.getResultChooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                File file = this.getResultChooser().getSelectedFile();
                try {

                    GlycoLabelFeaturesXMLReader reader = new GlycoLabelFeaturesXMLReader(file);
                    GlycoLPRowGetter getter = new GlycoLPRowGetter(reader);
                    PepPairViewPanel pepPairViewPanel = new PepPairViewPanel(getter);
                    addTabbedPane(pepPairViewPanel);
                    pepPairViewPanel.getJButtonClose().addActionListener(this);
                    closeList.add(pepPairViewPanel.getJButtonClose());

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return;
        }

        if (obj == this.mainPanel.getJButtonLabelMerge()) {

            LabelGradWriterFrame frame = new LabelGradWriterFrame(this);
            frame.getContentPane().setPreferredSize(frame.getSize());
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);

            return;
        }

        if (obj == this.mainPanel.getJButtonLabelRepeat()) {

            RepeatStatFrame frame = new RepeatStatFrame();
            frame.getContentPane().setPreferredSize(frame.getSize());
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);

            return;
        }

        if (obj == this.mainPanel.getJButtonLabelTurnover()) {

            SixTurnOverFrame frame = new SixTurnOverFrame();
            frame.getContentPane().setPreferredSize(frame.getSize());
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);

            return;
        }

        if (obj == this.mainPanel.getJButtonLabelFreeGenerate()) {

            LFreeBatchWriterFrame frame = new LFreeBatchWriterFrame(this);
            frame.getContentPane().setPreferredSize(frame.getSize());
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);

            return;
        }

        if (obj == this.mainPanel.getJButtonLabelFreeLoad()) {

            int value = this.getResultChooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                File file = this.getResultChooser().getSelectedFile();
                try {

/*					AbstractFeaturesXMLReader reader = new LFreePairXMLReader(file);
					PairPagedRowGetter getter = new PairPagedRowGetter(reader);
					PepPairViewPanel pepPairViewPanel = new PepPairViewPanel(getter);
					addTabbedPane(pepPairViewPanel);
					pepPairViewPanel.getJButtonClose().addActionListener(this);
					closeList.add(pepPairViewPanel.getJButtonClose());
*/
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return;
        }

        if (obj == this.mainPanel.getJButtonLabelSpectralCount()) {
            SPCounterFrame spframe = new SPCounterFrame();
            spframe.getContentPane().setPreferredSize(spframe.getSize());
            spframe.pack();
            spframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            spframe.setLocationRelativeTo(this);
            spframe.setVisible(true);
            spframe.setAlwaysOnTop(true);
            return;
        }

        if (obj == this.mainPanel.getJButtonGlycoStrucIden()) {

            NGlycoStrucIdenFrame frame = new NGlycoStrucIdenFrame(this);
            frame.getContentPane().setPreferredSize(frame.getSize());
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);
            frame.setAlwaysOnTop(true);
            return;
        }

        if (obj == this.mainPanel.getJButtonLoadGlycoStruc()) {

            int value = this.getResultChooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                File file = this.getResultChooser().getSelectedFile();
                try {

                    GlycoIdenXMLReader reader = new GlycoIdenXMLReader(file);
                    GlycoIdenPagedRowGetter getter = new GlycoIdenPagedRowGetter(reader);
                    NGlycoStrucViewPanel glycanViewPanel = new NGlycoStrucViewPanel(getter);
                    addTabbedPane(glycanViewPanel);
                    glycanViewPanel.getJButtonClose().addActionListener(this);
                    closeList.add(glycanViewPanel.getJButtonClose());

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return;
        }

        if (obj == this.mainPanel.getJButtonLoadGlycoMatch()) {

            int value = this.getResultChooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                File file = this.getResultChooser().getSelectedFile();
                try {

                    GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(file);
                    GlycoLFreeRowGetter2 getter = new GlycoLFreeRowGetter2(reader);
                    NGlycoMatchViewPanel glycanViewPanel = new NGlycoMatchViewPanel(getter);
                    addTabbedPane(glycanViewPanel);
                    glycanViewPanel.getJButtonClose().addActionListener(this);
                    closeList.add(glycanViewPanel.getJButtonClose());

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return;
        }

        if (obj == this.getJMenuItemGlycoLabelQuan()) {

            Component com = jTabbedPane0.getSelectedComponent();
            if (com instanceof PeptideListViewerPanel2) {

                PeptideListViewerPanel2 pepview = (PeptideListViewerPanel2) com;
                IFilteredPeptideListReader reader = pepview.getPepGetter().getSelectedPeptideReader();
                LPairCreateFrame qFrame = new LPairCreateFrame(LabelType.SILAC, reader, this, 1, pepview.getFile());
                qFrame.getContentPane().setPreferredSize(qFrame.getSize());
                qFrame.pack();
                qFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                qFrame.setLocationRelativeTo(this);
                qFrame.setVisible(true);

                return;
            }
        }

        if (obj == this.getJMenuItemGlycoMatch()) {
            Component com = jTabbedPane0.getSelectedComponent();
            if (com instanceof PeptideListViewerPanel2) {
                PeptideListViewerPanel2 pepview = (PeptideListViewerPanel2) com;
                IFilteredPeptideListReader reader = pepview.getPepGetter().getSelectedPeptideReader();
                LFreeCreateFrame qFrame = new LFreeCreateFrame(reader, this, 1, pepview.getFile());
                qFrame.getContentPane().setPreferredSize(qFrame.getSize());
                qFrame.pack();
                qFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                qFrame.setLocationRelativeTo(this);
                qFrame.setVisible(true);

                return;
            }
        }

        if (obj == this.getJMenuItemMgfviewer()) {

            int value = this.getResultChooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                File file = this.getResultChooser().getSelectedFile();
                try {

                    MgfViewer mgfViewPanel = new MgfViewer(file);
                    addTabbedPane(mgfViewPanel);
                    mgfViewPanel.getJButtonClose().addActionListener(this);
                    closeList.add(mgfViewPanel.getJButtonClose());

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return;
        }

        Iterator<Object> it = closeList.iterator();
        while (it.hasNext()) {

            Object closeObj = it.next();

            if (obj == closeObj) {

                it.remove();
                Component com = jTabbedPane0.getSelectedComponent();
                if (com instanceof PeptideListViewerPanel2) {
                    ((PeptideListViewerPanel2) com).dispose();
                } else if (com instanceof PepPairViewPanel) {
                    ((PepPairViewPanel) com).dispose();
                }

                jTabbedPane0.remove(jTabbedPane0.getSelectedComponent());
                Component com0 = jTabbedPane0.getSelectedComponent();

                if (com0 instanceof PeptideListViewerPanel2) {

                    this.jMenuItemProtein.setEnabled(true);
                    this.jMenuItemSpectra.setEnabled(true);
                    this.jMenuItemPTM.setEnabled(true);
                    this.jMenuItemProSeq.setEnabled(true);

                    this.jMenuItemLabel.setEnabled(true);
                    this.jMenuItemLFree.setEnabled(true);
                    this.jMenuItemQuant.setEnabled(false);
                    this.jMenuItemModQuant.setEnabled(false);
                    this.jMenuItemGlycoQuant.setEnabled(false);
                    this.jMenuItemGlycoSpec.setEnabled(false);
                    this.jMenuItemGlycoMatch.setEnabled(true);
                    this.jMenuItemGlycoLabelQuant.setEnabled(true);

                    if (((PeptideListViewerPanel2) com0).getFileNum() == 1) {

                        this.jMenuItemLabel.setEnabled(true);
                        this.jMenuItemLFree.setEnabled(true);
                        this.jMenuItemQuant.setEnabled(false);
                        this.jMenuItemModQuant.setEnabled(false);

                    } else {
                        this.jMenuItemLabel.setEnabled(false);
                        this.jMenuItemQuant.setEnabled(false);
                        this.jMenuItemModQuant.setEnabled(false);
                        this.jMenuItemLFree.setEnabled(false);
                    }

                } else if (com0 instanceof PepPairViewPanel) {

                    this.jMenuItemProtein.setEnabled(false);
                    this.jMenuItemSpectra.setEnabled(false);
                    this.jMenuItemPTM.setEnabled(false);
                    this.jMenuItemProSeq.setEnabled(false);

                    this.jMenuItemLabel.setEnabled(false);
                    this.jMenuItemLFree.setEnabled(false);
                    this.jMenuItemGlycoMatch.setEnabled(false);
                    this.jMenuItemGlycoLabelQuant.setEnabled(false);

                    if (((PepPairViewPanel) com0).isGlyco()) {

                        this.jMenuItemQuant.setEnabled(false);
                        this.jMenuItemModQuant.setEnabled(false);
                        this.jMenuItemGlycoQuant.setEnabled(true);
                        this.jMenuItemGlycoSpec.setEnabled(true);

                    } else {

                        this.jMenuItemQuant.setEnabled(true);
                        this.jMenuItemModQuant.setEnabled(true);
                        this.jMenuItemGlycoQuant.setEnabled(false);
                        this.jMenuItemGlycoSpec.setEnabled(false);
                    }

                } else {

                    this.jMenuItemProtein.setEnabled(false);
                    this.jMenuItemSpectra.setEnabled(false);
                    this.jMenuItemPTM.setEnabled(false);
                    this.jMenuItemProSeq.setEnabled(false);

                    this.jMenuItemLabel.setEnabled(false);
                    this.jMenuItemQuant.setEnabled(false);
                    this.jMenuItemModQuant.setEnabled(false);
                    this.jMenuItemLFree.setEnabled(false);

                    this.jMenuItemQuant.setEnabled(false);
                    this.jMenuItemModQuant.setEnabled(false);
                    this.jMenuItemGlycoQuant.setEnabled(false);
                    this.jMenuItemGlycoSpec.setEnabled(false);
                }

                return;
            }
        }

    }

    @Override
    public void stateChanged(ChangeEvent e)
    {
        Component com = jTabbedPane0.getSelectedComponent();

        if (com instanceof PeptideListViewerPanel2) {

            this.jMenuItemProtein.setEnabled(true);
            this.jMenuItemSpectra.setEnabled(true);
            this.jMenuItemPTM.setEnabled(true);
            this.jMenuItemProSeq.setEnabled(true);

            this.jMenuItemLabel.setEnabled(true);
            this.jMenuItemLFree.setEnabled(true);
            this.jMenuItemQuant.setEnabled(false);
            this.jMenuItemModQuant.setEnabled(false);
            this.jMenuItemGlycoQuant.setEnabled(false);
            this.jMenuItemGlycoSpec.setEnabled(false);
            this.jMenuItemGlycoMatch.setEnabled(true);
            this.jMenuItemGlycoLabelQuant.setEnabled(true);


            if (((PeptideListViewerPanel2) com).getFileNum() == 1) {

                this.jMenuItemLabel.setEnabled(true);
                this.jMenuItemLFree.setEnabled(true);
                this.jMenuItemQuant.setEnabled(false);
                this.jMenuItemModQuant.setEnabled(false);

            } else {
                this.jMenuItemLabel.setEnabled(false);
                this.jMenuItemQuant.setEnabled(false);
                this.jMenuItemModQuant.setEnabled(false);
                this.jMenuItemLFree.setEnabled(false);
            }

        } else if (com instanceof PepPairViewPanel) {

            this.jMenuItemProtein.setEnabled(false);
            this.jMenuItemSpectra.setEnabled(false);
            this.jMenuItemPTM.setEnabled(false);
            this.jMenuItemProSeq.setEnabled(false);

            this.jMenuItemLabel.setEnabled(false);
            this.jMenuItemLFree.setEnabled(false);
            this.jMenuItemGlycoMatch.setEnabled(false);
            this.jMenuItemGlycoLabelQuant.setEnabled(false);

            if (((PepPairViewPanel) com).isGlyco()) {

                this.jMenuItemQuant.setEnabled(false);
                this.jMenuItemModQuant.setEnabled(false);
                this.jMenuItemGlycoQuant.setEnabled(true);
                this.jMenuItemGlycoSpec.setEnabled(true);

            } else {

                this.jMenuItemQuant.setEnabled(true);
                this.jMenuItemModQuant.setEnabled(true);
                this.jMenuItemGlycoQuant.setEnabled(false);
                this.jMenuItemGlycoSpec.setEnabled(false);
            }

        } else {

            this.jMenuItemProtein.setEnabled(false);
            this.jMenuItemSpectra.setEnabled(false);
            this.jMenuItemPTM.setEnabled(false);
            this.jMenuItemProSeq.setEnabled(false);

            this.jMenuItemLabel.setEnabled(false);
            this.jMenuItemQuant.setEnabled(false);
            this.jMenuItemModQuant.setEnabled(false);
            this.jMenuItemLFree.setEnabled(false);

            this.jMenuItemGlycoQuant.setEnabled(false);
            this.jMenuItemGlycoSpec.setEnabled(false);
        }
    }

    private class LabelLoadThread extends Thread
    {
        private MainGui2 m2;
        private File file;
        private ProcessingDlgNew bar;

        private LabelLoadThread(File file, MainGui2 m2)
        {
            this.file = file;
            this.m2 = m2;
        }

        private void setBar(ProcessingDlgNew bar)
        {
            this.bar = bar;
        }

        public void run()
        {
            AbstractFeaturesXMLReader reader = null;
            try {
                reader = new LabelFeaturesXMLReader(file);

            } catch (Exception e) {
                e.printStackTrace();
            }
            FeaturesPagedRowGetter getter = new FeaturesPagedRowGetter(reader);
            PepPairViewPanel pepPairViewPanel = new PepPairViewPanel(getter);

            this.bar.dispose();

            m2.addTabbedPane(pepPairViewPanel);
            pepPairViewPanel.getJButtonClose().addActionListener(m2);
            m2.closeList.add(pepPairViewPanel.getJButtonClose());
        }

    }

    private class progressBarThread extends Thread
    {
        private Component com;

        private progressBarThread(Component com)
        {
            this.com = com;
        }

        public void run()
        {
            ProcessingDlgNew bar = new ProcessingDlgNew(com);
            bar.setVisible(true);
        }
    }
}
