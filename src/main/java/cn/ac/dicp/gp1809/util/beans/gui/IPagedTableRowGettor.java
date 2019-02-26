/*
 ******************************************************************************
 * File: IPagedTableRowGettor.java * * * Created on 04-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.beans.gui;

/**
 * The paged row getter. Just change the records in this class to refresh the
 * contents in the PagedTable.
 *
 * @author Xinning
 * @version 0.1, 04-10-2009, 16:08:18
 */
public interface IPagedTableRowGettor<Row extends ITableRowObject> extends
        ITableRowGettor<Row>
{
    /**
     * The number of pages
     */
    int getNumberofPages();

    /**
     * Max number of records per page
     */
    int getMaxRecordsperPage();

    /**
     * The current page (0 - n-1)
     */
    void setCurrentPage(int pageIdx) throws IndexOutOfBoundsException;

    /**
     * The current page index (0 - n-1)
     */
    int getCurrentPage();

    /**
     * Get the number of rows in current page
     */
    int getRowCountCurtPage();

    /**
     * Set the max number of records per page.
     */
    void setMaxRecordsperPage(int max);
}
