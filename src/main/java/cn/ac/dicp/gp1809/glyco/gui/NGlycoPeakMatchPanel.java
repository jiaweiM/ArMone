/*
 ******************************************************************************
 * File: GlycoPeakMatchPanel.java * * * Created on 2012-5-18
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.gui;

import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.drawjf.JFreeChartPanel;
import cn.ac.dicp.gp1809.glyco.drawjf.GlycoSpecMatchDataset;
import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class NGlycoPeakMatchPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private JFreeChartPanel jFreeChartPanel0;

    public NGlycoPeakMatchPanel()
    {
        initComponents();
    }

    private void initComponents()
    {
        setBorder(BorderFactory.createTitledBorder(null, null, TitledBorder.LEADING, TitledBorder.CENTER, new Font("Dialog", Font.BOLD, 12),
                new Color(51, 51, 51)));
        setLayout(new GroupLayout());
        add(getJFreeChartPanel0(), new Constraints(new Bilateral(0, 0, 0), new Bilateral(0, 0, 0)));
        setSize(560, 340);
    }

    public void draw(GlycoSpecMatchDataset glycoDataset)
    {
        this.jFreeChartPanel0.drawChart(JFChartDrawer.createXYBarChart(glycoDataset));
        this.repaint();
        this.updateUI();
    }

    private JFreeChartPanel getJFreeChartPanel0()
    {
        if (jFreeChartPanel0 == null) {
            jFreeChartPanel0 = new JFreeChartPanel();
            jFreeChartPanel0.setBorder(BorderFactory.createCompoundBorder(null, null));
            jFreeChartPanel0.setMinimumSize(new Dimension(560, 360));
            jFreeChartPanel0.setPreferredSize(new Dimension(560, 360));
        }
        return jFreeChartPanel0;
    }
}
