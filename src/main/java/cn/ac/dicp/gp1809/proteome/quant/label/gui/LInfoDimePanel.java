/*
 ******************************************************************************
 * File:LInfoDimePanel.java * * * Created on 2010-5-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.gui;

import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

//VS4E -- DO NOT REMOVE THIS LINE!
public class LInfoDimePanel extends JPanel
{

    private static final long serialVersionUID = 1L;
    private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
    private JLabel jLabelName;
    private JCheckBox jCheckBox1;
    private JCheckBox jCheckBox2;
    private JCheckBox jCheckBox3;
    private JComboBox jComboBoxS11;
    private JComboBox jComboBoxS12;
    private JComboBox jComboBoxS21;
    private JComboBox jComboBoxS22;
    private JComboBox jComboBoxS31;
    private JComboBox jComboBoxS32;
    private AminoacidModification aam;
    private char symbol1;
    private char symbol2;
    private char symbol3;

    public LInfoDimePanel()
    {
        initComponents();
    }

    public LInfoDimePanel(AminoacidModification aam)
    {
        this.aam = aam;
        initComponents();
        initial();
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
                frame.setTitle("LInfoDimePanel");
                LInfoDimePanel content = new LInfoDimePanel();
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
        add(getJLabelName(), new Constraints(new Leading(23, 10, 10), new Leading(16, 10, 10)));
        add(getJCheckBox1(), new Constraints(new Leading(23, 10, 10), new Leading(45, 10, 10)));
        add(getJCheckBox2(), new Constraints(new Leading(23, 10, 10), new Leading(80, 10, 10)));
        add(getJCheckBox3(), new Constraints(new Leading(23, 10, 10), new Leading(115, 10, 10)));
        add(getJComboBoxS11(), new Constraints(new Leading(140, 10, 10), new Leading(42, 10, 10)));
        add(getJComboBoxS12(), new Constraints(new Leading(300, 10, 10), new Leading(42, 10, 10)));
        add(getJComboBoxS21(), new Constraints(new Leading(140, 10, 10), new Leading(77, 10, 10)));
        add(getJComboBoxS22(), new Constraints(new Leading(300, 10, 10), new Leading(77, 10, 10)));
        add(getJComboBoxS31(), new Constraints(new Leading(140, 10, 10), new Leading(112, 10, 10)));
        add(getJComboBoxS32(), new Constraints(new Leading(300, 10, 10), new Leading(112, 10, 10)));
        setSize(460, 155);
    }

    private void initial()
    {

        Modif[] modifs = aam.getModifications();
        Arrays.sort(modifs);

        for (int i = 0; i < modifs.length; i++) {
            double mass = modifs[i].getMass();
            char symbol = modifs[i].getSymbol();
            if (Math.abs(mass - 28.03) < 0.1) {

                this.jCheckBox1.setSelected(true);
                symbol1 = symbol;

            } else if (Math.abs(mass - 32.0564) < 0.1) {

                this.jCheckBox2.setSelected(true);
                symbol2 = symbol;

            } else if (Math.abs(mass - 36.0757) < 0.1) {

                this.jCheckBox3.setSelected(true);
                symbol3 = symbol;

            } else if (Math.abs(mass - 34.0631) < 0.1) {

                if (this.jCheckBox2.isSelected()) {

                    this.jCheckBox3.setSelected(true);
                    symbol3 = symbol;
                    jComboBoxS31.setSelectedIndex(3);
                    jComboBoxS32.setSelectedIndex(3);

                } else {

                    this.jCheckBox2.setSelected(true);
                    symbol2 = symbol;
                    jComboBoxS21.setSelectedIndex(3);
                    jComboBoxS22.setSelectedIndex(3);
                }

            }
        }
    }

    private JComboBox getJComboBoxS11()
    {
        if (jComboBoxS11 == null) {
            jComboBoxS11 = new JComboBox();
            jComboBoxS11.setModel(new DefaultComboBoxModel(new Object[]{LabelInfo.Dimethyl_CH3_K,
                    LabelInfo.Dimethyl_CHD2_K, LabelInfo.Dimethyl_C13HD2_K, LabelInfo.Dimethyl_C13D3_K}));
            jComboBoxS11.setDoubleBuffered(false);
            jComboBoxS11.setBorder(null);
        }
        return jComboBoxS11;
    }

    private JComboBox getJComboBoxS12()
    {
        if (jComboBoxS12 == null) {
            jComboBoxS12 = new JComboBox();
            jComboBoxS12.setModel(new DefaultComboBoxModel(new Object[]{LabelInfo.Dimethyl_CH3_Nt,
                    LabelInfo.Dimethyl_CHD2_Nt, LabelInfo.Dimethyl_C13HD2_Nt, LabelInfo.Dimethyl_C13D3_Nt}));
            jComboBoxS12.setDoubleBuffered(false);
            jComboBoxS12.setBorder(null);
        }
        return jComboBoxS12;
    }

    private JComboBox getJComboBoxS21()
    {
        if (jComboBoxS21 == null) {
            jComboBoxS21 = new JComboBox();
            jComboBoxS21.setModel(new DefaultComboBoxModel(new Object[]{LabelInfo.Dimethyl_CHD2_K,
                    LabelInfo.Dimethyl_CH3_K, LabelInfo.Dimethyl_C13HD2_K, LabelInfo.Dimethyl_C13D3_K}));
            jComboBoxS21.setDoubleBuffered(false);
            jComboBoxS21.setBorder(null);
        }
        return jComboBoxS21;
    }

    private JComboBox getJComboBoxS22()
    {
        if (jComboBoxS22 == null) {
            jComboBoxS22 = new JComboBox();
            jComboBoxS22.setModel(new DefaultComboBoxModel(new Object[]{LabelInfo.Dimethyl_CHD2_Nt,
                    LabelInfo.Dimethyl_CH3_Nt, LabelInfo.Dimethyl_C13HD2_Nt, LabelInfo.Dimethyl_C13D3_Nt}));
            jComboBoxS22.setDoubleBuffered(false);
            jComboBoxS22.setBorder(null);
        }
        return jComboBoxS22;
    }

    private JComboBox getJComboBoxS31()
    {
        if (jComboBoxS31 == null) {
            jComboBoxS31 = new JComboBox();
            jComboBoxS31.setModel(new DefaultComboBoxModel(new Object[]{LabelInfo.Dimethyl_C13D3_K,
                    LabelInfo.Dimethyl_CHD2_K, LabelInfo.Dimethyl_C13HD2_K, LabelInfo.Dimethyl_CH3_K}));
            jComboBoxS31.setDoubleBuffered(false);
            jComboBoxS31.setBorder(null);
        }
        return jComboBoxS31;
    }

    private JComboBox getJComboBoxS32()
    {
        if (jComboBoxS32 == null) {
            jComboBoxS32 = new JComboBox();
            jComboBoxS32.setModel(new DefaultComboBoxModel(new Object[]{LabelInfo.Dimethyl_C13D3_Nt,
                    LabelInfo.Dimethyl_CHD2_Nt, LabelInfo.Dimethyl_C13HD2_Nt, LabelInfo.Dimethyl_CH3_Nt}));
            jComboBoxS32.setDoubleBuffered(false);
            jComboBoxS32.setBorder(null);
        }
        return jComboBoxS32;
    }

    private JCheckBox getJCheckBox3()
    {
        if (jCheckBox3 == null) {
            jCheckBox3 = new JCheckBox();
            jCheckBox3.setText("   3. Dimethyl_3");
        }
        return jCheckBox3;
    }

    private JCheckBox getJCheckBox2()
    {
        if (jCheckBox2 == null) {
            jCheckBox2 = new JCheckBox();
            jCheckBox2.setText("   2. Dimethyl_2");
        }
        return jCheckBox2;
    }

    private JCheckBox getJCheckBox1()
    {
        if (jCheckBox1 == null) {
            jCheckBox1 = new JCheckBox();
            jCheckBox1.setText("   1. Dimethyl_1");
        }
        return jCheckBox1;
    }

    private JLabel getJLabelName()
    {
        if (jLabelName == null) {
            jLabelName = new JLabel();
            jLabelName.setText("Dimethyl");
        }
        return jLabelName;
    }

    public LabelType getLabelType()
    {
        LabelType type = LabelType.Dimethyl;
        ArrayList<Short> intList = new ArrayList<Short>();

        ArrayList<LabelInfo[]> infoList = new ArrayList<LabelInfo[]>();
        if (getJCheckBox1().isSelected()) {
            intList.add((short) 1);
            ArrayList<LabelInfo> iList = new ArrayList<LabelInfo>();
            Object obj1 = getJComboBoxS11().getSelectedItem();
            if (obj1 != null) {
                ((LabelInfo) obj1).setSymbol(symbol1);
                iList.add((LabelInfo) obj1);
            }
            Object obj2 = getJComboBoxS12().getSelectedItem();
            if (obj2 != null) {
                ((LabelInfo) obj2).setSymbol(symbol1);
                iList.add((LabelInfo) obj2);
            }
            infoList.add(iList.toArray(new LabelInfo[iList.size()]));
        }
        if (getJCheckBox2().isSelected()) {
            intList.add((short) 2);
            ArrayList<LabelInfo> iList = new ArrayList<LabelInfo>();
            Object obj1 = getJComboBoxS21().getSelectedItem();
            if (obj1 != null) {
                ((LabelInfo) obj1).setSymbol(symbol2);
                iList.add((LabelInfo) obj1);
            }
            Object obj2 = getJComboBoxS22().getSelectedItem();
            if (obj2 != null) {
                ((LabelInfo) obj2).setSymbol(symbol2);
                iList.add((LabelInfo) obj2);
            }
            infoList.add(iList.toArray(new LabelInfo[iList.size()]));
        }
        if (getJCheckBox3().isSelected()) {
            intList.add((short) 3);
            ArrayList<LabelInfo> iList = new ArrayList<LabelInfo>();
            Object obj1 = getJComboBoxS31().getSelectedItem();
            if (obj1 != null) {
                ((LabelInfo) obj1).setSymbol(symbol3);
                iList.add((LabelInfo) obj1);
            }
            Object obj2 = getJComboBoxS32().getSelectedItem();
            if (obj2 != null) {
                ((LabelInfo) obj2).setSymbol(symbol3);
                iList.add((LabelInfo) obj2);
            }
            infoList.add(iList.toArray(new LabelInfo[iList.size()]));
        }
        LabelInfo[][] infos = infoList.toArray(new LabelInfo[infoList.size()][]);
        short[] used = new short[intList.size()];
        for (int i = 0; i < used.length; i++) {
            used[i] = intList.get(i);
        }
        type.setInfo(infos);
        type.setUsed(used);
        return type;
    }

    public short[] getUsed()
    {
        return getLabelType().getUsed();
    }

}
