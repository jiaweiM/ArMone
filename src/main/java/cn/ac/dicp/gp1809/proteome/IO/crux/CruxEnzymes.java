/* 
 ******************************************************************************
 * File: InspectEnzymes.java * * * Created on 03-23-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.crux;

import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;

/**
 * The static crux predefined enzymes.
 * 
 * @author Xinning
 * @version 0.1, 03-23-2009, 20:14:26
 */
public class CruxEnzymes {

	public CruxEnzymes() {
	}

	/**
	 * Get the enzyme by name. Currently crux only use the trypsin. Always
	 * return trypsin
	 * 
	 * @param name
	 *            the name of enzyme (case insensitive)
	 * @return
	 */
	public Enzyme getEnzymeByName(String name) throws NullPointerException {

		return Enzyme.TRYPSIN;
	}

}
