/* 
 ******************************************************************************
 * File: DefaultXTandemPhosphoPepFormat.java * * * Created on 02-17-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.instances;

import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.DefaultXTandemPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;

/**
 * The xtandem phosphopeptide format
 * 
 * @author Xinning
 * @version 0.1, 02-17-2009, 20:56:33
 */
public class DefaultXTandemPhosphoPepFormat extends DefaultXTandemPeptideFormat {

	/**
	 * 
	 */
	public DefaultXTandemPhosphoPepFormat() {
		
	}

	/**
	 * @param peptideIndexMap
	 * @throws IllegalFormaterException
	 */
	public DefaultXTandemPhosphoPepFormat(
	        HashMap<String, Integer> peptideIndexMap)
	        throws IllegalFormaterException {
		super(peptideIndexMap);
		// TODO Auto-generated constructor stub
	}

}
