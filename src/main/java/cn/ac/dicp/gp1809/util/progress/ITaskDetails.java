/* 
 ******************************************************************************
 * File: ITaskDetails.java * * * Created on 05-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.progress;

/**
 * Task details
 * 
 * @author Xinning
 * @version 0.1, 05-13-2009, 21:55:20
 */
public interface ITaskDetails {

	/**
	 * Get the task described by the detail
	 * 
	 * @return the task described by the detail
	 */
	public ITask getTask();

}
