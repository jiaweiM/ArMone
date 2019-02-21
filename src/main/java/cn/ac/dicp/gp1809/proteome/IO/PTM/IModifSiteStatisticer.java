/* 
 ******************************************************************************
 * File: IModifSiteStatisticer.java * * * Created on 05-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM;

import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;

/**
 * The PTM site statisticer
 * 
 * @author Xinning
 * @version 0.1, 05-19-2009, 16:17:42
 */
public interface IModifSiteStatisticer {

	/**
	 * Add a protein to the statisticer and generate the statistic information
	 * for the protein
	 * 
	 * @param protein
	 */
	public void add(Protein protein);

}
