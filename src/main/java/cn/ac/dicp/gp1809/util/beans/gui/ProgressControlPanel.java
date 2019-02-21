/* 
 ******************************************************************************
 * File: ProgressControlPanel.java * * * Created on 04-09-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.beans.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.ac.dicp.gp1809.util.progress.IControlable;

/**
 * The controller for Controlable object
 * 
 * @author Xinning
 * @version 0.2, 05-04-2009, 18:38:20
 */
public class ProgressControlPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton jButtonStart;
	private JButton jButtonStop;
	private IControlable control;
	private int preState = -1;

	private Timer timer;
	public ProgressControlPanel() {
		initComponents();
	}

	/**
	 * This is the default constructor
	 */
	public ProgressControlPanel(IControlable control) {
		initComponents();

		if (control == null)
			throw new NullPointerException("The IControlable object is null.");

		this.control = control;
		//A daemon thread
		this.timer = new Timer(true);
		this.addStateListener();
	}
	
	/**
	 * If show the pause button for the control of pause
	 */
	public ProgressControlPanel(IControlable control, boolean showPause) {
		initComponents();

		if(!showPause)
			this.remove(getJButtonStart());
		
		if (control == null)
			throw new NullPointerException("The IControlable object is null.");

		this.control = control;
		//A daemon thread
		this.timer = new Timer(true);
		this.addStateListener();
	}

	private void initComponents() {
    	setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0));
    	add(getJButtonStart());
    	add(getJButtonStop());
    	setSize(273, 30);
    }

	private JButton getJButtonStop() {
    	if (jButtonStop == null) {
    		jButtonStop = new JButton();
    		jButtonStop.setText("  Stop  ");
    		jButtonStop.addActionListener(this);
    	}
    	return jButtonStop;
    }

	private JButton getJButtonStart() {
    	if (jButtonStart == null) {
    		jButtonStart = new JButton();
    		jButtonStart.setText("Pause");
    		jButtonStart.addActionListener(this);
    	}
    	return jButtonStart;
    }

	/**
	 * Dispose this panel by removing the action listeners for this panel.
	 */
	public void dispose() {
		this.timer.cancel();
	}

	/**
	 * Monitor the current running state
	 */
	private void addStateListener() {
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				int state = control.currentState();

				if (state != preState) {

					switch (state) {
					case IControlable.STATES_RUNNING:
						getJButtonStart().setText("Pause");
						break;
					case IControlable.STATES_PAUSED:
						getJButtonStart().setText("Resume");
						break;
					case IControlable.STATES_STOPPED:
						getJButtonStart().setText("Start");
						break;

					default:
						throw new IllegalArgumentException(
						        "The argument is illegal.");
					}

					preState = state;
				}
			}

		}, 300, 1000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();

		if (obj == this.getJButtonStart()) {
			int state = this.control.currentState();
			if (state == IControlable.STATES_RUNNING) {
				this.control.pause();
			} else {
				if (state == IControlable.STATES_PAUSED) {
					this.control.resume();
				} else {
					this.control.begin();
				}
			}

			return;
		}

		if (obj == this.jButtonStop) {
			this.control.end();
			return;
		}
	}
}
