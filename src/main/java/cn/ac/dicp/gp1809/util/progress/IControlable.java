/*
 ******************************************************************************
 * File: IControlable.java * * * Created on 07-28-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.progress;

/**
 * Every class implements this interface is controlable. There are four major
 * methods for this interface: begin, end, pause and resume. Through these
 * methods, this class tends to be control able.
 * 
 * @author Xinning
 * @version 0.1.1, 03-13-2009, 10:43:18
 */
public interface IControlable {

	/**
	 * The controlable object is in unknown state.
	 */
	public static final int STATES_UNKOWN = 0;
	
	/**
	 * The controlable object is running.
	 */
	public static final int STATES_RUNNING = 1;

	/**
	 * The controlable object has been stopped.
	 */
	public static final int STATES_STOPPED = 2;

	/**
	 * The controlable object has been paused.
	 */
	public static final int STATES_PAUSED = 3;

	/**
	 * The object has receive the commond for stop or pause and trying for
	 * action.
	 */
	public static final int STATES_STOPING_PAUSEING = 4;

	/**
	 * Start a new thread and begin to run. If it is running current now, the
	 * call of this method should do nothing.
	 * 
	 * <p>
	 * Notice: the calling of this method will start a new thread.
	 */
	public void begin();

	/**
	 * Stop the current run.
	 */
	public void end();

	/**
	 * The current will pause until the call of resume() method.
	 * 
	 */
	public void pause();

	/**
	 * Resume the current paused thread. If the current thread is still running,
	 * the call of this method will do nothing.
	 */
	public void resume();

	/**
	 * The current state. The current state should be one of the following
	 * states: STATES_RUNNING, STATES_STOPPED, STATES_PAUSED.
	 * 
	 * @return the current state
	 * @see STATES_RUNNING, STATES_STOPPED, STATES_PAUSED
	 */
	public int currentState();
}
