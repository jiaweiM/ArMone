/*
 ******************************************************************************
 * File: ProgressControllerDialog.java * * * Created on 05-07-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.gui;

import cn.ac.dicp.gp1809.util.beans.gui.ProgressControlPanel;
import cn.ac.dicp.gp1809.util.progress.IControlable;
import cn.ac.dicp.gp1809.util.progress.IControlableProgress;
import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Xinning
 * @version 0.1, 05-07-2009, 21:22:23
 */
public class ProgressControllerDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
    private IControlableProgress progress;
    private boolean showPause;
    private JLabel jLabel0;
    private JProgressBar jProgressBar0;
    private ProgressControlPanel progressControlPanel0;

    public ProgressControllerDialog()
    {
        initComponents();
    }

    public ProgressControllerDialog(Component owner, IControlableProgress progress, boolean showPause)
    {
        super(UIutilities.getWindowForComponent(owner));

        this.progress = progress;
        this.showPause = showPause;

        initComponents();

        this.getContentPane().setPreferredSize(this.getSize());
        this.pack();

        this.initializeProgress(owner, progress);
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
            ProgressControllerDialog dialog = new ProgressControllerDialog();
            dialog
                    .setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setTitle("ProgressControllerDialog");
            dialog.setLocationRelativeTo(null);
            dialog.getContentPane().setPreferredSize(dialog.getSize());
            dialog.pack();
            dialog.setVisible(true);
        });
    }

    private void initComponents()
    {
        setTitle("Processing ...");
        setFont(new Font("Dialog", Font.PLAIN, 12));
        setBackground(new Color(204, 232, 207));
        setResizable(false);
        setForeground(Color.black);
        setLayout(new GroupLayout());
        add(getJLabel0(), new Constraints(new Bilateral(17, 16, 393), new Leading(23, 12, 12)));
        add(getJProgressBar0(), new Constraints(new Bilateral(12, 11, 403), new Leading(47, 21, 12, 12)));
        add(getProgressControlPanel0(), new Constraints(new Bilateral(77, 76, 273), new Bilateral(74, 12, 30)));
        setSize(426, 116);
    }

    /**
     * Initialization for progress.
     *
     * @param owner
     * @param progress
     */
    private void initializeProgress(Component owner, final IControlableProgress progress)
    {
        if (progress == null)
            throw new NullPointerException("Progress object is null.");

        this.progress = progress;

        final ProgressMonitor monitor = new ProgressMonitor(this
                .getJProgressBar0(), this.getJLabel0(), progress);
        monitor.startMonitor();

        new Timer(true).schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (progress.currentState() == IControlable.STATES_STOPPED) {
                    ProgressControllerDialog.this.dispose();
                    this.cancel();
                }

            }

        }, 3000, 1000);

        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                // Only when the progress has been stopped, the window can be closed.
                if (progress.currentState() == IControlable.STATES_STOPPED) {
                    ProgressControllerDialog.this.dispose();
                }
            }
        });

        this.setLocationRelativeTo(UIutilities.getWindowForComponent(owner));
        this.setVisible(true);
    }

    private ProgressControlPanel getProgressControlPanel0()
    {
        if (progressControlPanel0 == null) {
            if (this.progress != null)
                progressControlPanel0 = new ProgressControlPanel(this.progress,
                        this.showPause);
            else
                progressControlPanel0 = new ProgressControlPanel();
        }
        return progressControlPanel0;
    }

    private JProgressBar getJProgressBar0()
    {
        if (jProgressBar0 == null) {
            jProgressBar0 = new JProgressBar();
        }
        return jProgressBar0;
    }

    private JLabel getJLabel0()
    {
        if (jLabel0 == null) {
            jLabel0 = new JLabel();
            jLabel0.setText("Processing ...");
            jLabel0.setPreferredSize(new Dimension(100, 18));
        }
        return jLabel0;
    }

}
