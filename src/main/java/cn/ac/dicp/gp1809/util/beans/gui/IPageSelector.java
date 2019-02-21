/* 
 ******************************************************************************
 * File: IPageSelector.java * * * Created on 04-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.beans.gui;

/**
 * The selector of pages
 * 
 * @author Xinning
 * @version 0.1, 04-10-2009, 13:55:08
 */
public interface IPageSelector {

	/**
	 * Too save the system resource, informations can be splitted into multiple
	 * pages and only show the specific page at a time. Use this method to show
	 * the specific information
	 * 
	 * @param pageIdx a page index
	 */
	public void selectPage(int pageIdx);
	
	/**
	 * The number of pages
	 * @return The number of pages
	 */
	public int totalPages();

}
