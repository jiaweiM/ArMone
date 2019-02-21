/*
 ******************************************************************************
 * File: OGlycoValidateFrame.java * * * Created on 2013-12-2
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.gui;

import cn.ac.dicp.gp1809.glyco.oglycan.OGlycanValidator4PPL;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

//VS4E -- DO NOT REMOVE THIS LINE!
public class OGlycoValidateFrame extends JFrame implements ActionListener
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
    private JLabel jLabelInfo;
    private JTextField jTextFieldInfo;
    private JButton jButtonInfo;
    private JLabel jLabelOriginal;
    private JTextField jTextFieldOriginal;
    private JButton jButtonOriginal;
    private MyJFileChooser outchooser;
    private MyJFileChooser inchooser;
    private MyJFileChooser infochooser;
    private MyJFileChooser mgfschooser;
    private JProgressBar jProgressBar0;
    private File parent;
    private JCheckBox jCheckBox0;
    private JLabel jLabelFdr;
    private JTextField jTextFieldPercent;
    private JLabel jLabelPercent;
    private JLabel jLabelFThres;
    private JTextField jTextFieldFThres;
    private JLabel jLabelSiteThres;
    private JTextField jTextFieldSiteThres;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private ButtonGroup buttonGroup;

    public OGlycoValidateFrame()
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
        SwingUtilities.invokeLater(() -> {
            OGlycoValidateFrame frame = new OGlycoValidateFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setTitle("OGlycoValidateFrame");
            frame.getContentPane().setPreferredSize(frame.getSize());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private void initComponents()
    {
        getContentPane().setLayout(new GroupLayout());
        getContentPane().add(getJLabelInput(), new Constraints(new Leading(25, 10, 10), new Leading(42, 10, 10)));
        getContentPane().add(getJTextFieldInput(), new Constraints(new Leading(100, 240, 12, 12), new Leading(40, 6, 6)));
        getContentPane().add(getJButtonInput(), new Constraints(new Leading(380, 10, 10), new Leading(40, 12, 12)));
        getContentPane().add(getJLabelInfo(), new Constraints(new Leading(25, 10, 10), new Leading(92, 10, 10)));
        getContentPane().add(getJTextFieldInfo(), new Constraints(new Leading(100, 240, 12, 12), new Leading(90, 6, 6)));
        getContentPane().add(getJButtonInfo(), new Constraints(new Leading(380, 10, 10), new Leading(90, 12, 12)));
        getContentPane().add(getJLabelOutput(), new Constraints(new Leading(25, 10, 10), new Leading(142, 10, 10)));
        getContentPane().add(getJTextFieldOutput(), new Constraints(new Leading(100, 240, 12, 12), new Leading(140, 6, 6)));
        getContentPane().add(getJButtonOutput(), new Constraints(new Leading(380, 10, 10), new Leading(140, 12, 12)));
        getContentPane().add(getJLabelOriginal(), new Constraints(new Leading(25, 10, 10), new Leading(188, 10, 10)));
        getContentPane().add(getJTextFieldOriginal(), new Constraints(new Leading(100, 240, 12, 12), new Leading(190, 6, 6)));
        getContentPane().add(getJButtonOriginal(), new Constraints(new Leading(380, 10, 10), new Leading(190, 12, 12)));
        getContentPane().add(getJButtonClose(), new Constraints(new Leading(250, 10, 10), new Leading(405, 6, 6)));
        getContentPane().add(getJButtonStart(), new Constraints(new Leading(100, 10, 10), new Leading(405, 6, 6)));
        getContentPane().add(getJProgressBar0(), new Constraints(new Leading(25, 392, 6, 6), new Leading(365, 10, 10)));
        getContentPane().add(getJCheckBox0(), new Constraints(new Leading(28, 10, 10), new Leading(261, 10, 10)));
        getContentPane().add(getJLabelFdr(), new Constraints(new Leading(30, 10, 10), new Leading(316, 12, 12)));
        getContentPane().add(getJTextFieldPercent(), new Constraints(new Leading(70, 39, 10, 10), new Leading(310, 12, 12)));
        getContentPane().add(getJLabelPercent(), new Constraints(new Leading(115, 10, 10), new Leading(316, 12, 12)));
        getContentPane().add(getJTextFieldFThres(), new Constraints(new Leading(240, 39, 10, 10), new Leading(310, 12, 12)));
        getContentPane().add(getJLabelFThres(), new Constraints(new Leading(155, 10, 10), new Leading(316, 12, 12)));
        getContentPane().add(getJTextFieldSiteThres(), new Constraints(new Leading(370, 39, 10, 10), new Leading(310, 12, 12)));
        getContentPane().add(getJLabelSiteThres(), new Constraints(new Leading(305, 10, 10), new Leading(316, 12, 12)));
        getContentPane().add(getJRadioButtonDeglyco(), new Constraints(new Leading(135, 10, 10), new Leading(261, 12, 12)));
        getContentPane().add(getJRadioButtonAll(), new Constraints(new Leading(250, 10, 10), new Leading(261, 12, 12)));
        initButtonGroup();
        setSize(450, 460);
    }

    private void initButtonGroup()
    {
        buttonGroup = new ButtonGroup();
        buttonGroup.add(getJRadioButtonAll());
        buttonGroup.add(getJRadioButtonDeglyco());
    }

    /**
     * @wbp.nonvisual location=327,307
     */
    private JRadioButton getJRadioButtonAll()
    {
        if (radioButton2 == null) {
            radioButton2 = new JRadioButton("Deglycosylated+Original");
            radioButton2.setEnabled(false);
            radioButton2.addActionListener(this);
        }
        return radioButton2;
    }

    /**
     * @wbp.nonvisual location=177,307
     */
    private JRadioButton getJRadioButtonDeglyco()
    {
        if (radioButton1 == null) {
            radioButton1 = new JRadioButton("Deglycosylated");
            radioButton1.setSelected(true);
            radioButton1.setEnabled(false);
            radioButton1.addActionListener(this);
        }
        return radioButton1;
    }

    private JTextField getJTextFieldSiteThres()
    {
        if (jTextFieldSiteThres == null) {
            jTextFieldSiteThres = new JTextField();
            jTextFieldSiteThres.setText("0.6");
            jTextFieldSiteThres.setAutoscrolls(true);
        }
        return jTextFieldSiteThres;
    }

    private JLabel getJLabelSiteThres()
    {
        if (jLabelSiteThres == null) {
            jLabelSiteThres = new JLabel();
            jLabelSiteThres.setText("Site score>");
        }
        return jLabelSiteThres;
    }

    private JTextField getJTextFieldFThres()
    {
        if (jTextFieldFThres == null) {
            jTextFieldFThres = new JTextField();
            jTextFieldFThres.setText("0.5");
            jTextFieldFThres.setAutoscrolls(true);
        }
        return jTextFieldFThres;
    }

    private JLabel getJLabelFThres()
    {
        if (jLabelFThres == null) {
            jLabelFThres = new JLabel();
            jLabelFThres.setText("Delta F score>");
        }
        return jLabelFThres;
    }

    private JLabel getJLabelPercent()
    {
        if (jLabelPercent == null) {
            jLabelPercent = new JLabel();
            jLabelPercent.setText("%");
        }
        return jLabelPercent;
    }

    private JTextField getJTextFieldPercent()
    {
        if (jTextFieldPercent == null) {
            jTextFieldPercent = new JTextField();
            jTextFieldPercent.setText("1.0");
            jTextFieldPercent.setAutoscrolls(true);
        }
        return jTextFieldPercent;
    }

    private JLabel getJLabelFdr()
    {
        if (jLabelFdr == null) {
            jLabelFdr = new JLabel();
            jLabelFdr.setText("FDR < ");
        }
        return jLabelFdr;
    }

    private JCheckBox getJCheckBox0()
    {
        if (jCheckBox0 == null) {
            jCheckBox0 = new JCheckBox();
            jCheckBox0.setText("Draw spectra");
            jCheckBox0.addActionListener(this);
        }
        return jCheckBox0;
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
        this.inchooser = new MyJFileChooser(parent);
        this.inchooser.setFileFilter(new String[]{""}, "*.ppl file or directory");
        this.inchooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        return inchooser;
    }

    private MyJFileChooser getOutchooser()
    {
        this.outchooser = new MyJFileChooser(parent);
        this.outchooser.setFileFilter(new String[]{"xls"},
                "*.xls");
        return outchooser;
    }

    private MyJFileChooser getInfochooser()
    {
        this.infochooser = new MyJFileChooser(parent);
        this.infochooser.setFileFilter(new String[]{"info"},
                "O-glycan information (*.info)");
        return infochooser;
    }

    private MyJFileChooser getMgfschooser()
    {
        this.mgfschooser = new MyJFileChooser(parent);
        this.mgfschooser.setFileFilter(new String[]{""},
                ".mgf file or directory");
        this.mgfschooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        return mgfschooser;
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

    private JButton getJButtonInfo()
    {
        if (jButtonInfo == null) {
            jButtonInfo = new JButton();
            jButtonInfo.setText("...");
            jButtonInfo.addActionListener(this);
        }
        return jButtonInfo;
    }

    private JTextField getJTextFieldInfo()
    {
        if (jTextFieldInfo == null) {
            jTextFieldInfo = new JTextField();
        }
        return jTextFieldInfo;
    }

    private JLabel getJLabelInfo()
    {
        if (jLabelInfo == null) {
            jLabelInfo = new JLabel();
            jLabelInfo.setText("Info");
        }
        return jLabelInfo;
    }

    private JButton getJButtonOriginal()
    {
        if (jButtonOriginal == null) {
            jButtonOriginal = new JButton();
            jButtonOriginal.setText("...");
            jButtonOriginal.addActionListener(this);
            jButtonOriginal.setEnabled(false);
        }
        return jButtonOriginal;
    }

    private JTextField getJTextFieldOriginal()
    {
        if (jTextFieldOriginal == null) {
            jTextFieldOriginal = new JTextField();
            jTextFieldOriginal.setEnabled(false);
        }
        return jTextFieldOriginal;
    }

    private JLabel getJLabelOriginal()
    {
        if (jLabelOriginal == null) {
            jLabelOriginal = new JLabel();
            jLabelOriginal.setText("<html>Original<br>spectra</html>");
            jLabelOriginal.setEnabled(false);
        }
        return jLabelOriginal;
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

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object obj = e.getSource();

        if (obj == this.getJButtonClose()) {
            this.dispose();
            return;
        }

        if (obj == this.getJCheckBox0()) {
            if (this.jCheckBox0.isSelected()) {
                this.radioButton1.setEnabled(true);
                this.radioButton2.setEnabled(true);
            } else {
                this.radioButton1.setEnabled(false);
                this.radioButton2.setEnabled(false);
            }
        }

        if (obj == this.getJRadioButtonDeglyco()) {
            if (this.radioButton2.isSelected()) {
                this.jLabelOriginal.setEnabled(true);
                this.jTextFieldOriginal.setEnabled(true);
                this.jButtonOriginal.setEnabled(true);
            } else {
                this.jLabelOriginal.setEnabled(false);
                this.jTextFieldOriginal.setEnabled(false);
                this.jButtonOriginal.setEnabled(false);
            }
        }

        if (obj == this.getJRadioButtonAll()) {
            if (this.radioButton2.isSelected()) {
                this.jLabelOriginal.setEnabled(true);
                this.jTextFieldOriginal.setEnabled(true);
                this.jButtonOriginal.setEnabled(true);
            } else {
                this.jLabelOriginal.setEnabled(false);
                this.jTextFieldOriginal.setEnabled(false);
                this.jButtonOriginal.setEnabled(false);
            }
        }

        if (obj == this.getJButtonInput()) {
            int value = this.getInchooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                this.parent = this.inchooser.getSelectedFile();
                String filename = parent.getAbsolutePath();
                this.getJTextFieldInput().setText(filename);
            }
            return;
        }

        if (obj == this.getJButtonOutput()) {
            int value = this.getOutchooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                String filename = this.outchooser.getSelectedFile().getAbsolutePath();
                this.getJTextFieldOutput().setText(filename + ".xls");
            }
            return;
        }

        if (obj == this.getJButtonInfo()) {
            int value = this.getInfochooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                String filename = this.infochooser.getSelectedFile().getAbsolutePath();
                this.getJTextFieldInfo().setText(filename);
            }
            return;
        }

        if (obj == this.getJButtonOriginal()) {
            int value = this.getMgfschooser().showOpenDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                String filename = this.mgfschooser.getSelectedFile().getAbsolutePath();
                this.getJTextFieldOriginal().setText(filename);
            }
            return;
        }

        if (obj == this.getJButtonStart()) {

            String input = this.getJTextFieldInput().getText();
            if (input == null || input.length() == 0) {
                JOptionPane.showMessageDialog(this, "The input path have not been set.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String info = this.getJTextFieldInfo().getText();
            if (info == null || info.length() == 0) {
                JOptionPane.showMessageDialog(this, "The info path have not been set.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String output = this.getJTextFieldOutput().getText();
            if (output == null || output.length() == 0) {
                JOptionPane.showMessageDialog(this, "The output path have not been set.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean draw = this.jCheckBox0.isSelected();
            boolean drawOriginal = this.radioButton2.isSelected();
            String original = this.getJTextFieldOriginal().getText();
            if (drawOriginal && (original == null || original.length() == 0)) {
                JOptionPane.showMessageDialog(this, "The original spectra path have not been set.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double fdr = Double.parseDouble(this.getJTextFieldPercent().getText()) / 100.0;
            double fthres = Double.parseDouble(this.getJTextFieldFThres().getText());
            double sitethres = Double.parseDouble(this.getJTextFieldSiteThres().getText());
            this.jButtonStart.setEnabled(false);
            OGlycanValiditeThread thread = new OGlycanValiditeThread(input, info, output, draw, drawOriginal, original, fdr, fthres, sitethres, jProgressBar0, this);
            thread.start();
        }
    }

    private class OGlycanValiditeThread extends Thread
    {
        private String in;
        private String info;
        private String out;
        private boolean draw;
        private boolean drawOriginal;
        private String mgfs;
        private double fdr;
        private double fthres; // F score threshold
        private double sitethres;
        private JProgressBar jProgressBar0;
        private OGlycoValidateFrame frame;

        private OGlycanValiditeThread(String in, String info, String out, boolean draw, boolean drawOriginal,
                String mgfs, double fdr, double fthres, double sitethres,
                JProgressBar jProgressBar0, OGlycoValidateFrame frame)
        {

            this.in = in;
            this.info = info;
            this.out = out;
            this.draw = draw;
            this.drawOriginal = drawOriginal;
            this.mgfs = mgfs;
            this.fdr = fdr;
            this.fthres = fthres;
            this.sitethres = sitethres;
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
                OGlycanValidator4PPL validator = new OGlycanValidator4PPL(info);
                File filein = new File(in);
                if (filein.isFile()) {
                    validator.readIn(in);
                } else if (filein.isDirectory()) {
                    File[] files = filein.listFiles();
                    for (File file : files) {
                        if (file.getName().endsWith("ppl")) {
                            validator.readIn(file.getAbsolutePath());
                        }
                    }
                }
                validator.validate();

                if (draw) {
                    File outDirectory = (new File(out)).getParentFile();
                    if (!drawOriginal) {
                        validator.writeDrawFinal(out, outDirectory.getAbsolutePath() + "\\spectra", fdr, fthres, sitethres);
                    } else {
                        validator.writeFinalDrawOriginal(out, outDirectory.getAbsolutePath() + "\\spectra", mgfs, fdr, fthres, sitethres);
                    }

                } else {
                    validator.writeFinal(out, fdr, fthres, sitethres);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

            getJButtonStart().setEnabled(true);
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
