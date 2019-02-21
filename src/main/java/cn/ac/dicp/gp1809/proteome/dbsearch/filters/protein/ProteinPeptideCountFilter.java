/* 
 ******************************************************************************
 * File: ProteinPeptideCountFilter.java * * * Created on 03-25-2010
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
 * Filter of proteins by the number of unique peptide count
 * 
 * @author Xinning
 * @version 0.1, 03-25-2010, 14:43:10
 */
public class ProteinPeptideCountFilter implements IProteinCriteria {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private int peptidecount;

	public ProteinPeptideCountFilter(int peptidecount) {
		this.peptidecount = peptidecount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria#filter(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.Protein)
	 */
	@Override
	public boolean filter(Protein protein) {

		if (protein.getPeptideCount() >= peptidecount)
			return true;

		return false;
	}

}
