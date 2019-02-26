/*
 ******************************************************************************
 * File: ITableRowGettor.java * * * Created on 04-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.beans.gui;

/**
 * Get the specific row(s) to print in a table
 *
 * @author Xinning
 * @version 0.1, 04-10-2009, 15:33:40
 */
public interface ITableRowGettor<Row extends ITableRowObject>
{
    /**
     * The names of each column. Must with the same number as the column count
     */
    String[] getColumnNames();

    /**
     * The widths for each column. If want to use the default value, leave as
     * null.
     */
    int[] getColumnWidths();

    /**
     * The classes for each column
     */
    Class<?>[] getColumnClasses();

    /**
     * If editable for each of the column
     */
    boolean[] isColumnEditable();

    /**
     * The number of columns in the table
     */
    int getColumnCount();

    /**
     * the total number of records
     */
    int getRowCount();

    /**
     * The nth row in the table of current displayed records. From 0 - n-1
     *
     * @param idx the idx in the current shown records.
     */
    Row getRow(int idx);

    /**
     * Get the nth rows.
     *
     * @param idxs row indexes
     */
    Row[] getRows(int[] idxs);
}
