/* 
 ******************************************************************************
 * File: IProgress.java * * * Created on 08-19-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.progress;

/**
 * All the class implements this interface tend to be with known progress, that
 * is, the percentage of the progress is known.
 * 
 * @author Xinning
 * @version 0.3, 05-08-2009, 14:22:55
 */
public interface IProgress {
	
	/**
	 * The String value indicating the current action of this process.
	 * 
	 * @return
	 */
	public String currentAction();

	/**
	 * The percentage of task which has been completed. This value should bigger
	 * than 0 and less than 1. The valid range is [0,1].
	 * 
	 * @return
	 */
	public float completedPercent();
	
	
	/**
	 * Test whether the progress is determinable.
	 * 
	 * @return
	 */
	public boolean isIndeterminate();

}
