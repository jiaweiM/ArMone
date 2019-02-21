/* 
 ******************************************************************************
 * File: ProgressMonitor.java * * * Created on 08-19-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.gui;

import java.awt.Component;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import cn.ac.dicp.gp1809.util.progress.IProgress;

/**
 * Create a progress monitor for the progress
 * 
 * @author Xinning
 * @version 0.2.1, 04-09-2009, 19:40:50
 */
public class ProgressMonitor {

	private JProgressBar pbar;
	private JLabel actionText;
	private IProgress progress;

	// The total count
	private int total = 100;

	private Timer timer = new Timer(true);

	// The delay for timer moniter
	protected long delay = 300;

	protected long period = 1000;

	protected int prePercent;
	protected String preAction = "";
	
	private boolean indetermine;

	/**
	 * While the creation of Monitor, the monitoring started.
	 * 
	 * @param pbar
	 * @param progress
	 */
	public ProgressMonitor(JProgressBar pbar, IProgress progress) {
		if (pbar == null)
			throw new NullPointerException("The input JProgressBar is null.");

		if (progress == null)
			throw new NullPointerException("The progress is null.");

		this.pbar = pbar;
		
		this.indetermine = progress.isIndeterminate();
		
		if(this.indetermine) {
			pbar.setIndeterminate(true);
		}
		else {
			pbar.setMaximum(total);
			pbar.setStringPainted(true);
		}


		this.progress = progress;
	}

	/**
	 * While the creation of Monitor, the monitoring started.
	 * 
	 * @param pbar
	 * @param actionText
	 *            the text field showing the current process details (can be
	 *            null).
	 * @param progress
	 */
	public ProgressMonitor(JProgressBar pbar, JLabel actionText,
	        IProgress progress) {
		this(pbar, progress);

		this.actionText = actionText;
	}

	/**
	 * Begin to monitor the progress
	 */
	public void startMonitor() {

		this.timer.schedule(new TimerTask() {

			@Override
			public void run() {
				
				//The progress is determinable
				if(!indetermine) {
					int percent = (int) (progress.completedPercent() * total);
					if (percent > prePercent) {
						pbar.setValue((percent));

						if (percent >= total) {
							cancelMonitor();
							percent = total;
						}

						pbar.setString(percent + " %");
					}
				}

				if (actionText != null) {
					String action = progress.currentAction();
					if (!preAction.equals(action)) {
						actionText.setText(action);
					}
				}

			}

		}, delay, period);
	}

	/**
	 * Cancel the Monitor
	 */
	public void cancelMonitor() {
		this.timer.cancel();
	}

	/**
	 * Create a progress monitor for the progress. The monitor will open another
	 * thread. If the process has been completed (100% finished), the monitor
	 * will be automatically terminated.
	 * 
	 * @param pbar
	 * @param progress
	 */
	public static void startMonitor(JProgressBar pbar, IProgress progress) {
		new ProgressMonitor(pbar, progress).startMonitor();
	}

	/**
	 * Create a progress monitor for the progress. The monitor will open another
	 * thread. If the process has been completed (100% finished), the monitor
	 * will be automatically terminated.
	 * 
	 * @param pbar
	 * @param actionText
	 *            text field showing the current process details (can be null).
	 * @param progress
	 */
	public static void startMonitor(JProgressBar pbar, JLabel actionText,
	        IProgress progress) {
		new ProgressMonitor(pbar, actionText, progress).startMonitor();
	}
	
	/**
	 * Construct a monitor dialog for the progress. The monitor will open another
	 * thread. If the process has been completed (100% finished), the monitor
	 * will be automatically terminated.
	 * 
	 * @param progress
	 * @return
	 */
	public static JDialog showMonitorDialog(Component parent, IProgress progress) {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(parent);
		
		new ProgressMonitor(dialog.getJProgressBar(), progress).startMonitor();
		
		return dialog;
	}
}
