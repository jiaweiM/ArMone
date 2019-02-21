/* 
 ******************************************************************************
 * File: ICriteria.java * * * Created on 09-15-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters;

/**
 * Criteria used for post- database search filtering.
 * 
 * @author Xinning
 * @version 0.2, 09-04-2009, 16:45:50
 */
public interface ICriteria<T> {

	/**
	 * Filter
	 * 
	 * @param t
	 * @return
	 */
	public boolean filter(T t);

	/**
	 * The priority of the criteria. Criteria with lower priority will be used
	 * to filter the data set after that with higher priority.
	 * 
	 * @return the int value of priority
	 */
	public int getPriority();

}
