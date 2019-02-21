/* 
 ******************************************************************************
 * File: ITask.java * * * Created on 03-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.progress;

/**
 * A runnable task
 * 
 * @author Xinning
 * @version 0.1.1, 04-30-2009, 21:10:12
 */
public interface ITask2 {

	/**
	 * start the task and return the thread for the execution
	 * 
	 * @return the thread of current running task
	 */
	public Thread execute();

}