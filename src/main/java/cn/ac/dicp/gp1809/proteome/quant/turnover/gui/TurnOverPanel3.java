/*
 ******************************************************************************
 * File: MutilCompPanel3.java * * * Created on 2011-11-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.turnover.gui;

import cn.ac.dicp.gp1809.proteome.gui2.ModInfoPanel;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.util.gui.MyJFileChooser;
import org.dyno.visual.swing.layouts.*;
import org.dyno.visual.swing.layouts.GroupLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//VS4E -- DO NOT REMOVE THIS LINE!
public class TurnOverPanel3 extends JPanel implements ActionListener
{
    private static final long serialVersionUID = 1L;
    private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
    private ModInfoPanel modInfoPanel0;
    private JButton jButtonClose;
    private JButton jButtonStart;
    private JButton jButtonPre;
    private JProgressBar jProgressBar0;
    private ModInfo[] mods;
    private JLabel jLabelOutput;
    private JTextField jTextFieldOutput;
    private JButton jButtonOutput;
    private JCheckBox jCheckBox0;
    private MyJFileChooser outChooser;

    public TurnOverPanel3()
    {
        initComponents();
    }

    public TurnOverPanel3(ModInfo[] mods)
    {
        this.mods = mods;
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
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setTitle("MutilCompPanel3");
                TurnOverPanel3 content = new TurnOverPanel3();
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
        setLayout(new GroupLayout());
        add(getJButtonClose(), new Constraints(new Trailing(30, 10, 10), new Leading(340, 12, 12)));
        add(getJButtonStart(), new Constraints(new Trailing(115, 10, 10), new Leading(340, 12, 12)));
        add(getJButtonPre(), new Constraints(new Trailing(200, 10, 10), new Leading(340, 12, 12)));
        add(getModInfoPanel0(), new Constraints(new Bilateral(0, 0, 410), new Bilateral(0, 140, 10, 10)));
        add(getJProgressBar0(), new Constraints(new Bilateral(20, 20, 10), new Leading(305, 12, 12)));
//		add(getJCheckBox0(), new Constraints(new Leading(15, 10, 10), new Leading(340, 12, 12)));
        add(getJLabelOutput(), new Constraints(new Leading(25, 10, 10), new Leading(265, 10, 10)));
        add(getJButtonOutput(), new Constraints(new Leading(450, 10, 10), new Leading(263, 10, 10)));
        add(getJTextFieldOutput(), new Constraints(new Leading(85, 335, 10, 10), new Leading(265, 25, 10, 10)));
        setSize(540, 390);
    }

    public String getOutput()
    {
        return this.getJTextFieldOutput().getText();
    }

    private MyJFileChooser getOutchooser()
    {
        if (this.outChooser == null) {
            this.outChooser = new MyJFileChooser();
            this.outChooser.setFileFilter(new String[]{"xls"},
                    " Average quantitation result (*.xls)");
        }
        return outChooser;
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

    private JCheckBox getJCheckBox0()
    {
        if (jCheckBox0 == null) {
            jCheckBox0 = new JCheckBox();
            jCheckBox0.setText("Use no_mod peptide");
        }
        return jCheckBox0;
    }

    public JProgressBar getJProgressBar0()
    {
        if (jProgressBar0 == null) {
            jProgressBar0 = new JProgressBar();
        }
        return jProgressBar0;
    }

    public JButton getJButtonPre()
    {
        if (jButtonPre == null) {
            jButtonPre = new JButton();
            jButtonPre.setText("Previous");
        }
        return jButtonPre;
    }

    public JButton getJButtonStart()
    {
        if (jButtonStart == null) {
            jButtonStart = new JButton();
            jButtonStart.setText("Start");
        }
        return jButtonStart;
    }

    public JButton getJButtonClose()
    {
        if (jButtonClose == null) {
            jButtonClose = new JButton();
            jButtonClose.setText("Close");
        }
        return jButtonClose;
    }

    public ModInfoPanel getModInfoPanel0()
    {
        if (modInfoPanel0 == null) {
            if (mods == null)
                modInfoPanel0 = new ModInfoPanel();
            else
                modInfoPanel0 = new ModInfoPanel(mods);
        }
        return modInfoPanel0;
    }

    public ModInfo[] getMods()
    {
        return modInfoPanel0.getMods();
    }

    /*
        public boolean useNoMod(){
            return this.jCheckBox0.isSelected();
        }
    */
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
            if (value == JFileChooser.APPROVE_OPTION) {
                this.getJTextFieldOutput().setText(this.getOutchooser().getSelectedFile().getAbsolutePath() + ".xls");
            }

            return;
        }
    }

}
