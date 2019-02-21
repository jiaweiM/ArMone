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
        ITableRowGettor<Row> {

	/**
	 * The number of pages
	 * 
	 * @return
	 */
	public int getNumberofPages();

	/**
	 * Max number of records per page
	 * 
	 * @return
	 */
	public int getMaxRecordsperPage();

	/**
	 * The current page (0 - n-1)
	 * 
	 * @param pageIdx
	 */
	public void setCurrentPage(int pageIdx) throws IndexOutOfBoundsException;
	
	/**
	 * The current page index (0 - n-1)
	 * 
	 * @return
	 */
	public int getCurrentPage();
	
	/**
	 * Get the number of rows in current page
	 * 
	 * @return
	 */
	public int getRowCountCurtPage();

	/**
	 * Set the max number of records per page.
	 * 
	 * @param max
	 */
	public void setMaxRecordsperPage(int max);
}
