/*
 ******************************************************************************
 * File: ITask1.java * * * Created on 05-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.progress;

/**
 * The task
 *
 * @author Xinning
 * @version 0.1, 05-13-2009, 19:37:58
 */
public interface ITask
{
    /**
     * Has next process
     *
     * @return true if has next process.
     */
    boolean hasNext();

    /**
     * Process the inner task one by one
     */
    void processNext();

    /**
     * @return The percent of the progress
     */
    float completedPercent();

    /**
     * @return true if the progress is determineable
     */
    boolean inDetermineable();

    /**
     * Dispose the tasks, and release the resources
     */
    void dispose();
}
