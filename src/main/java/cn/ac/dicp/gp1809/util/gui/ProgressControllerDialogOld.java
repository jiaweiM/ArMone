/* 
 ******************************************************************************
 * File: ProgressDialog.java * * * Created on 08-19-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.gui;

import javax.swing.JPanel;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import java.awt.Rectangle;

import cn.ac.dicp.gp1809.util.beans.gui.ProgressControlPanel;
import cn.ac.dicp.gp1809.util.progress.IControlable;
import cn.ac.dicp.gp1809.util.progress.IControlableProgress;

import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ProgressDialog used to show the progress of the task and provide control
 * options
 * 
 * @author Xinning
 * @version 0.1.1, 04-09-2009, 16:37:01
 */
public class ProgressControllerDialogOld extends JDialog {

	private static final long serialVersionUID = 1L;

	private IControlableProgress progress;
	
	private ProgressControlPanel progressControlPanel;
	private JPanel jContentPane = null;
	private JProgressBar jProgressBar = null;
	private boolean showPause;

	/**
	 * This method initializes jProgressBar
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setBounds(new Rectangle(1, 19, 408, 22));
			jProgressBar.setFont(new Font("Calibri", Font.BOLD, 14));
		}
		return jProgressBar;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ProgressControllerDialogOld();
	}

	/**
	 * @param owner
	 */
	protected ProgressControllerDialogOld() {
		initialize(null);

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setUndecorated(true);
		this.setVisible(true);
	}

	/**
	 * Create the progress dialog to monitor and control the progress.
	 * 
	 * @param IControlableProgress
	 *            a controlable progress
	 */
	public ProgressControllerDialogOld(final IControlableProgress progress) {
		this(null, progress);
	}

	/**
	 * Create the progress dialog to monitor and control the progress.
	 * 
	 * @param owner
	 */
	public ProgressControllerDialogOld(Component owner, final IControlableProgress progress) {
		super(UIutilities.getWindowForComponent(owner));
		
		this.initializeProgress(this.getParent(), progress);
	}
	
	/**
	 * 
	 * 
	 * @param owner
	 * @param progress
	 * @param showPause the pause action controlable?
	 */
	public ProgressControllerDialogOld(Component owner, final IControlableProgress progress, boolean showPause) {
		super(UIutilities.getWindowForComponent(owner));
		this.showPause = showPause;
		this.initializeProgress(this.getParent(), progress);
	}

	/**
	 * Initialization for progress.
	 * 
	 * @param owner
	 * @param progress
	 */
	protected void initializeProgress(Container owner,
	        final IControlableProgress progress) {
		if (progress == null)
			throw new NullPointerException("Progress object is null.");

		this.progress = progress;

		final ProgressMonitor monitor = new ProgressMonitor(this
		        .getJProgressBar(), progress);
		monitor.startMonitor();

		this.addCloseListener();

		initialize(owner);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {

				// Only when the progress has been stopped, the window can be
				// closed.
				if (progress.currentState() == IControlable.STATES_STOPPED) {
					disposeWhenClose();
				}

			}
		});

		this.setVisible(true);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize(Container owner) {
		this.setSize(416, 123);
		this.setModalityType(ModalityType.DOCUMENT_MODAL);
		this.setTitle("Progress");
		this.setResizable(false);
		this.setContentPane(getJContentPane());
		this.setLocation(UIutilities.getProperLocation(owner, this));
	}

	/*
	 * Dispose the progress dialog
	 */
	private void disposeWhenClose() {
		this.dispose();
	}

	//When the progress has finished or has been stopped, close the progress dialog
	private void addCloseListener() {
		new Timer(true).schedule(new TimerTask() {

			@Override
			public void run() {

				if (progress.currentState() == IControlable.STATES_STOPPED) {
					disposeWhenClose();
					this.cancel();
				}

			}

		}, 3000, 1000);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJProgressBar(), null);

			if (this.getProgressControlPanel() != null)
				jContentPane.add(getProgressControlPanel(), null);
		}
		return jContentPane;
	}

	private ProgressControlPanel getProgressControlPanel() {
		if (this.progressControlPanel == null) {
			if (this.progress != null) {
				this.progressControlPanel = new ProgressControlPanel(this.progress, this.showPause);
				this.progressControlPanel.setLocation(104, 53);
			}
		}

		return this.progressControlPanel;
	}

} // @jve:decl-index=0:visual-constraint="155,56"
