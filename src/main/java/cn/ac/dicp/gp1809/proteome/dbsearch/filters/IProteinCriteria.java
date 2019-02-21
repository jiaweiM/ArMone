/*
 * *****************************************************************************
 * File: IProteinCriteria.java * * * Created on 08-07-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters;

import java.io.Serializable;

import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;

/**
 * Criteria used for the filtering of protein identifications.
 * 
 * @author Xinning
 * @version 0.1.1, 08-07-2008, 20:20:08
 */
public interface IProteinCriteria extends Serializable{

	/**
	 * Filter the Protein.
	 * 
	 * @param protein
	 * @return true if the protein pass the filter, otherwise return false.
	 */
	public boolean filter(Protein protein);

}
