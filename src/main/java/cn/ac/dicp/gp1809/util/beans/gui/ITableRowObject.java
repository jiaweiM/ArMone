/*
 ******************************************************************************
 * File: ITableRowObject.java * * * Created on 04-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.beans.gui;

/**
 * A row object in a line
 *
 * @author Xinning
 * @version 0.1, 04-10-2009, 15:40:44
 */
public interface ITableRowObject
{
    /**
     * The value at the specific column
     *
     * @param colIdx column index
     * @return value at the index
     */
    Object getValueAt(int colIdx);

    /**
     * Set the value at specific column
     *
     * @param obj    value
     * @param colIdx column index
     */
    void setValueAt(Object obj, int colIdx);
}
