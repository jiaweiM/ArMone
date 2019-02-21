/* 
 ******************************************************************************
 * File: IProteinFilterSetter.java * * * Created on 06-07-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias.protein;

import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria;

/**
 * The setter of protein filter
 * 
 * @author Xinning
 * @version 0.1, 06-07-2010, 14:32:54
 */
public interface IProteinFilterSetter {
	
	/**
	 * The protein filter
	 * 
	 * @see IProteinCriteria
	 * @return
	 */
	public IProteinCriteria getProteinFilter();
	
}
