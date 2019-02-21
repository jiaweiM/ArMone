/*
 ******************************************************************************
 * File: LInfoICPLPanel.java * * * Created on 2010-12-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.gui;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

//VS4E -- DO NOT REMOVE THIS LINE!
public class LInfoICPLPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
    private JLabel jLabelICPL;
    private JCheckBox jCheckBoxS1;
    private JCheckBox jCheckBoxS2;
    private JCheckBox jCheckBoxS3;
    private JComboBox jComboBoxS11;
    private JComboBox jComboBoxS12;
    private JComboBox jComboBoxS21;
    private JComboBox jComboBoxS22;
    private JComboBox jComboBoxS31;
    private JComboBox jComboBoxS32;

    public LInfoICPLPanel()
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
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setTitle("LInfoICPLPanel");
                LInfoICPLPanel content = new LInfoICPLPanel();
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
        add(getJLabelICPL(), new Constraints(new Leading(23, 10, 10), new Leading(16, 10, 10)));
        add(getJCheckBoxS1(), new Constraints(new Leading(23, 10, 10), new Leading(44, 10, 10)));
        add(getJCheckBoxS2(), new Constraints(new Leading(23, 10, 10), new Leading(79, 10, 10)));
        add(getJCheckBoxS3(), new Constraints(new Leading(23, 10, 10), new Leading(117, 10, 10)));
        add(getJComboBoxS11(), new Constraints(new Leading(140, 100, 10, 10), new Leading(41, 6, 6)));
        add(getJComboBoxS12(), new Constraints(new Leading(300, 10, 10), new Leading(41, 6, 6)));
        add(getJComboBoxS21(), new Constraints(new Leading(140, 100, 10, 10), new Leading(76, 6, 6)));
        add(getJComboBoxS22(), new Constraints(new Leading(300, 10, 10), new Leading(76, 6, 6)));
        add(getJComboBoxS31(), new Constraints(new Leading(140, 100, 10, 10), new Leading(114, 6, 6)));
        add(getJComboBoxS32(), new Constraints(new Leading(300, 10, 10), new Leading(114, 6, 6)));
        setSize(460, 155);
    }

    private JComboBox getJComboBoxS11()
    {
        if (jComboBoxS11 == null) {
            jComboBoxS11 = new JComboBox();
            jComboBoxS11.setModel(new DefaultComboBoxModel(new Object[]{null, LabelInfo.ICPL0_K,
                    LabelInfo.ICPL4_K, LabelInfo.ICPL6_K, LabelInfo.ICPL10_K}));
            jComboBoxS11.setDoubleBuffered(false);
            jComboBoxS11.setBorder(null);
        }
        return jComboBoxS11;
    }

    private JComboBox getJComboBoxS12()
    {
        if (jComboBoxS12 == null) {
            jComboBoxS12 = new JComboBox();
            jComboBoxS12.setModel(new DefaultComboBoxModel(new Object[]{null, LabelInfo.ICPL0_PepN,
                    LabelInfo.ICPL4_PepN, LabelInfo.ICPL6_PepN, LabelInfo.ICPL10_PepN,
                    LabelInfo.ICPL0_ProN, LabelInfo.ICPL4_ProN, LabelInfo.ICPL6_ProN,
                    LabelInfo.ICPL10_ProN}));
            jComboBoxS12.setDoubleBuffered(false);
            jComboBoxS12.setBorder(null);
        }
        return jComboBoxS12;
    }

    private JComboBox getJComboBoxS21()
    {
        if (jComboBoxS21 == null) {
            jComboBoxS21 = new JComboBox();
            jComboBoxS21.setModel(new DefaultComboBoxModel(new Object[]{null, LabelInfo.ICPL0_K,
                    LabelInfo.ICPL4_K, LabelInfo.ICPL6_K, LabelInfo.ICPL10_K}));
            jComboBoxS21.setDoubleBuffered(false);
            jComboBoxS21.setBorder(null);
        }
        return jComboBoxS21;
    }

    private JComboBox getJComboBoxS22()
    {
        if (jComboBoxS22 == null) {
            jComboBoxS22 = new JComboBox();
            jComboBoxS22.setModel(new DefaultComboBoxModel(new Object[]{null, LabelInfo.ICPL0_PepN,
                    LabelInfo.ICPL4_PepN, LabelInfo.ICPL6_PepN, LabelInfo.ICPL10_PepN,
                    LabelInfo.ICPL0_ProN, LabelInfo.ICPL4_ProN, LabelInfo.ICPL6_ProN,
                    LabelInfo.ICPL10_ProN}));
            jComboBoxS22.setDoubleBuffered(false);
            jComboBoxS22.setBorder(null);
        }
        return jComboBoxS22;
    }

    private JComboBox getJComboBoxS31()
    {
        if (jComboBoxS31 == null) {
            jComboBoxS31 = new JComboBox();
            jComboBoxS31.setModel(new DefaultComboBoxModel(new Object[]{null, LabelInfo.ICPL0_K,
                    LabelInfo.ICPL4_K, LabelInfo.ICPL6_K, LabelInfo.ICPL10_K}));
            jComboBoxS31.setDoubleBuffered(false);
            jComboBoxS31.setBorder(null);
        }
        return jComboBoxS31;
    }

    private JComboBox getJComboBoxS32()
    {
        if (jComboBoxS32 == null) {
            jComboBoxS32 = new JComboBox();
            jComboBoxS32.setModel(new DefaultComboBoxModel(new Object[]{null, LabelInfo.ICPL0_PepN,
                    LabelInfo.ICPL4_PepN, LabelInfo.ICPL6_PepN, LabelInfo.ICPL10_PepN,
                    LabelInfo.ICPL0_ProN, LabelInfo.ICPL4_ProN, LabelInfo.ICPL6_ProN,
                    LabelInfo.ICPL10_ProN}));
            jComboBoxS32.setDoubleBuffered(false);
            jComboBoxS32.setBorder(null);
        }
        return jComboBoxS32;
    }

    private JCheckBox getJCheckBoxS3()
    {
        if (jCheckBoxS3 == null) {
            jCheckBoxS3 = new JCheckBox();
            jCheckBoxS3.setText("   3. ICPL_3");
        }
        return jCheckBoxS3;
    }

    private JCheckBox getJCheckBoxS2()
    {
        if (jCheckBoxS2 == null) {
            jCheckBoxS2 = new JCheckBox();
            jCheckBoxS2.setText("   2. ICPL_2");
        }
        return jCheckBoxS2;
    }

    private JCheckBox getJCheckBoxS1()
    {
        if (jCheckBoxS1 == null) {
            jCheckBoxS1 = new JCheckBox();
            jCheckBoxS1.setText("   1. ICPL_1");
        }
        return jCheckBoxS1;
    }

    private JLabel getJLabelICPL()
    {
        if (jLabelICPL == null) {
            jLabelICPL = new JLabel();
            jLabelICPL.setText("ICPL");
        }
        return jLabelICPL;
    }

    public LabelType getLabelType()
    {
        LabelType silac = LabelType.ICPL;
        ArrayList<Short> intList = new ArrayList<Short>();

        ArrayList<LabelInfo[]> infoList = new ArrayList<LabelInfo[]>();
        if (getJCheckBoxS1().isSelected()) {
            intList.add((short) 1);
            ArrayList<LabelInfo> iList = new ArrayList<LabelInfo>();
            Object obj1 = getJComboBoxS11().getSelectedItem();
            if (obj1 != null) {
                iList.add((LabelInfo) obj1);
            }
            Object obj2 = getJComboBoxS12().getSelectedItem();
            if (obj2 != null) {
                iList.add((LabelInfo) obj2);
            }
            infoList.add(iList.toArray(new LabelInfo[iList.size()]));
        }
        if (getJCheckBoxS2().isSelected()) {
            intList.add((short) 2);
            ArrayList<LabelInfo> iList = new ArrayList<LabelInfo>();
            Object obj1 = getJComboBoxS21().getSelectedItem();
            if (obj1 != null) {
                iList.add((LabelInfo) obj1);
            }
            Object obj2 = getJComboBoxS22().getSelectedItem();
            if (obj2 != null) {
                iList.add((LabelInfo) obj2);
            }
            infoList.add(iList.toArray(new LabelInfo[iList.size()]));
        }
        if (getJCheckBoxS3().isSelected()) {
            intList.add((short) 3);
            ArrayList<LabelInfo> iList = new ArrayList<LabelInfo>();
            Object obj1 = getJComboBoxS31().getSelectedItem();
            if (obj1 != null) {
                iList.add((LabelInfo) obj1);
            }
            Object obj2 = getJComboBoxS32().getSelectedItem();
            if (obj2 != null) {
                iList.add((LabelInfo) obj2);
            }
            infoList.add(iList.toArray(new LabelInfo[iList.size()]));
        }
        LabelInfo[][] infos = infoList.toArray(new LabelInfo[infoList.size()][]);
        short[] used = new short[intList.size()];
        for (int i = 0; i < used.length; i++) {
            used[i] = intList.get(i);
        }
        silac.setInfo(infos);
        silac.setUsed(used);
        return silac;
    }

    public short[] getUsed()
    {
        return getLabelType().getUsed();
    }

}
