/* 
 ******************************************************************************
 * File: DecoyProteinRemovalFilter.java * * * Created on 03-25-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.protein;

import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria;

/**
 * Filter of proteins by remove the decoy hits
 * 
 * @author Xinning
 * @version 0.1, 06-07-2010, 14:54:44
 */
public class DecoyProteinRemovalFilter implements IProteinCriteria {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria#filter(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.Protein)
	 */
	@Override
	public boolean filter(Protein protein) {

		if (protein.isTarget())
			return true;

		return false;
	}

}
