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
public interface ITableRowGettor<Row extends ITableRowObject> {

	/**
	 * The names of each column. Must with the same number as the column count
	 * 
	 * @return
	 */
	public String[] getColumnNames();

	/**
	 * The widths for each column. If want to use the default value, leave as
	 * null.
	 * 
	 * @return
	 */
	public int[] getColumnWidths();

	/**
	 * The classes for each column
	 * 
	 * @return
	 */
	public Class<?>[] getColumnClasses();

	/**
	 * If editable for each of the column
	 * 
	 * @return
	 */
	public boolean[] isColumnEditable();

	/**
	 * The number of columns in the table
	 * 
	 * @return
	 */
	public int getColumnCount();

	/**
	 * the total number of records
	 * 
	 * @return
	 */
	public int getRowCount();

	/**
	 * The nth row in the table of current displayed records. From 0 - n-1
	 * 
	 * @param idx
	 *            the idx in the current shown records.
	 * @return
	 */
	public Row getRow(int idx);

	/**
	 * Get the nth rows.
	 * 
	 * @param idxs
	 * @return
	 */
	public Row[] getRows(int[] idxs);

	/**
	 * Set the filter for the getting of the row. The row objects will be
	 * filtered at the same time of the setting. That is, the returned rows
	 * after the setting of filter will be the rows passed the filter. All other
	 * informations will be renewed at the same time.
	 * <p>
	 * It should be noted that, the latter set filter will overwrite the
	 * previous ones.
	 * 
	 * @param filter
	 */
	//	public void setRowFilter(ITableRowFilter<Row> filter);

	/**
	 * Add another filter for the getting of the row. The rows which passed
	 * previous filters will be filtered by this filter. The row objects will be
	 * filtered at the same time of the setting. That is, the returned rows
	 * after the setting of filter will be the rows passed the filter. All other
	 * informations will be renewed at the same time.
	 * 
	 * @param filter
	 */
	//	public void addAdditionFilter(ITableRowFilter<Row> filter);
}
