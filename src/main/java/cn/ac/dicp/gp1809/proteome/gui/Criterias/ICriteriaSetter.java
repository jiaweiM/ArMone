/* 
 ******************************************************************************
 * File: ICriteriaSetter.java * * * Created on 04-09-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * Setter of criteria for peptide identification filtering
 * 
 * @author Xinning
 * @version 0.1, 04-09-2009, 21:14:47
 */
public interface ICriteriaSetter<Pep extends IPeptide> {
	
	
	/**
	 * Get the peptide criteria for filtering
	 * 
	 * @return
	 */
	public IPeptideCriteria<Pep> getCriteria();
	
}
