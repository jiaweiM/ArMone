/* 
 ******************************************************************************
 * File: ITableRowFilter.java * * * Created on 04-11-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.beans.gui;

/**
 * Filter of the table row object
 * 
 * @author Xinning
 * @version 0.1, 04-11-2009, 21:06:50
 */
public interface ITableRowFilter <Row extends ITableRowObject>{

	/**
	 * Test whether the row object passes the need filter.
	 * 
	 * @param row
	 * @return
	 */
	public boolean filter(Row row);

}
