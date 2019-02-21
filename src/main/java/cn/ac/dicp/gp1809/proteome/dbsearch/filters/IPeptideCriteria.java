/*
 * *****************************************************************************
 * File: IPeptideCriteria.java * * * Created on 09-15-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters;

import java.io.Serializable;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;

/**
 * Criteria used to filter peptide identifications
 * 
 * @author Xinning
 * @version 0.2, 09-15-2008, 15:42:56
 */
public interface IPeptideCriteria<Pep extends IPeptide> extends Serializable{

	/**
	 * Filter peptides:
	 * <p>
	 * if a peptide passes the criteria, then pep.setUsed(true); otherwise
	 * pep.setUsed(false), and at the same time a boolean value will be
	 * returned.
	 * 
	 * @param peptide
	 * @return if this peptide passes the criteria
	 */
	public boolean filter(Pep pep);

	/**
	 * To remove vast of invalid peptide identifications with very low scores,
	 * prefilter may be useful. After prefilter, all invalid peptides will be
	 * removed. This method is different with filter() method. Peptide which
	 * doesn't pass this filter will be direct removed from the final peptide
	 * list.
	 * 
	 * @param pep
	 * @return if pass
	 */
//	public boolean preFilter(Pep pep);

	/**
	 * The type of the peptide
	 * 
	 * @return
	 */
	public PeptideType getPeptideType();
	
	public String getFilterName();
	
}
