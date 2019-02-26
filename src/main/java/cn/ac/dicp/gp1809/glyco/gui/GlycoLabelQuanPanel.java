/*
 ******************************************************************************
 * File: GlycoLabelQuanPanel.java * * * Created on Jan 19, 2016
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.gui;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;

/**
 * @version Jan 19, 2016, 10:24:31 AM
 */
public class GlycoLabelQuanPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private JButton jButtonLoadGlycoQuan;

    GlycoLabelQuanPanel()
    {
        initComponents();
    }

    private void initComponents()
    {
        setSize(166, 166);
        javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(30)
                                .addComponent(getJButtonLoadGlycoQuan(), GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(139, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(15)
                                .addComponent(getJButtonLoadGlycoQuan(), GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(120, Short.MAX_VALUE))
        );
        setLayout(groupLayout);
    }

    JButton getJButtonLoadGlycoQuan()
    {
        if (jButtonLoadGlycoQuan == null) {
            jButtonLoadGlycoQuan = new JButton();
            jButtonLoadGlycoQuan.setText("<html><p align=\"center\">Load labelQuant result</p></html>");
            Color blue = new Color(125, 190, 255);
            jButtonLoadGlycoQuan.setBackground(blue);
        }
        return jButtonLoadGlycoQuan;
    }

}
