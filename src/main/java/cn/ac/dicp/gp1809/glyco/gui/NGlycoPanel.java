/*
 ******************************************************************************
 * File: GlycoPanel.java * * * Created on 2012-5-17
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.gui;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.Trailing;

import javax.swing.GroupLayout.Alignment;
import javax.swing.*;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NGlycoPanel extends JPanel implements ActionListener
{
    private static final long serialVersionUID = 1L;

    private JPanel jPanel;
    private GlycoIdenPanel idenPanel;
    private GlycoMatchPanel matchPanel;
    private GlycoLabelQuanPanel labelQuanPanel;

    private JButton jButtonIden;
    private JButton jButtonMatch;
    private JButton jButtonQuan;

    public NGlycoPanel()
    {
        initComponents();
    }

    private void initComponents()
    {
        this.jPanel = getIdenPanel();
        add(jPanel, new Constraints(new Trailing(0, 270, 10, 10), new Bilateral(0, 0, 10, 10)));
        setSize(450, 240);
        javax.swing.GroupLayout groupLayout = new javax.swing.GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(26)
                                .addComponent(getJButtonIden(), javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(getJButtonMatch(), javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(getJButtonQuan(), javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(100)
                                .addComponent(getJButtonMatch(), javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20)
                                .addComponent(getJButtonQuan(), javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                                .addContainerGap(119, Short.MAX_VALUE)
                                .addComponent(getJButtonIden(), javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(71))
        );
        setLayout(groupLayout);
    }

    private JButton getJButtonQuan()
    {
        if (jButtonQuan == null) {
            jButtonQuan = new JButton();
            jButtonQuan.setText("<html><p align=\"center\">Quantitative analysis<p><html>");
            jButtonQuan.addActionListener(this);
            Color blue = new Color(125, 190, 255);
            jButtonQuan.setBackground(blue);
        }
        return jButtonQuan;
    }

    private JButton getJButtonMatch()
    {
        if (jButtonMatch == null) {
            jButtonMatch = new JButton();
            jButtonMatch.setText("<html><p align=\"center\">GlycoPeptide analysis<p><html>");
            jButtonMatch.addActionListener(this);
            Color green = new Color(150, 255, 150);
            jButtonMatch.setBackground(green);
        }
        return jButtonMatch;
    }

    private JButton getJButtonIden()
    {
        if (jButtonIden == null) {
            jButtonIden = new JButton();
            jButtonIden.setText("<html><p align=\"center\">Structure identification<p><html>");
            jButtonIden.addActionListener(this);
            jButtonIden.setBackground(Color.pink);
        }
        return jButtonIden;
    }

    private GlycoIdenPanel getIdenPanel()
    {
        if (idenPanel == null) {
            idenPanel = new GlycoIdenPanel();
        }
        return idenPanel;
    }

    private GlycoMatchPanel getMatchPanel()
    {
        if (matchPanel == null) {
            matchPanel = new GlycoMatchPanel();
        }
        return matchPanel;
    }

    private GlycoLabelQuanPanel getLabelQuanPanel()
    {
        if (labelQuanPanel == null) {
            labelQuanPanel = new GlycoLabelQuanPanel();
        }
        return labelQuanPanel;
    }

    public JButton getJButtonGlycoStrucIden()
    {
        return this.getIdenPanel().getJButtonGlycoStrucIden();
    }

    public JButton getJButtonLoadGlycoStruc()
    {
        return this.getIdenPanel().getJButtonLoadGlycoStruc();
    }

    public JButton getJButtonLoadGlycoMatch()
    {
        return this.getMatchPanel().getJButtonLoadGlycoMatch();
    }

    public JButton getJButtonLoadLabelQuanGlyco()
    {
        return this.getLabelQuanPanel().getJButtonLoadGlycoQuan();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object obj = e.getSource();

        if (obj == this.getJButtonIden()) {
            this.remove(jPanel);
            this.jPanel = this.getIdenPanel();
            add(jPanel, new Constraints(new Trailing(0, 270, 10, 10), new Bilateral(0, 0, 10, 10)));
            this.repaint();
            this.updateUI();
            return;
        }

        if (obj == this.getJButtonMatch()) {
            this.remove(jPanel);
            this.jPanel = this.getMatchPanel();
            add(jPanel, new Constraints(new Trailing(0, 270, 10, 10), new Bilateral(0, 0, 10, 10)));
            this.repaint();
            this.updateUI();
            return;
        }

        if (obj == this.getJButtonQuan()) {
            this.remove(jPanel);
            this.jPanel = this.getLabelQuanPanel();
            add(jPanel, new Constraints(new Trailing(0, 270, 10, 10), new Bilateral(0, 0, 10, 10)));
            this.repaint();
            this.updateUI();
            return;
        }
    }
}
