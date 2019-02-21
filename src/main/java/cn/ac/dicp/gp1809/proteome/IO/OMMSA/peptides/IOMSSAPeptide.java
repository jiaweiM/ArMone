/* 
 ******************************************************************************
 * File: IOMSSAPeptide.java * * * Created on 09-22-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;

/**
 * The OMSSA Peptide
 * 
 * @author Xinning
 * @version 0.1, 09-22-2008, 21:43:07
 */
public interface IOMSSAPeptide extends IPeptide{

	/**
	 * @return the expected value
	 */
	public double getEvalue();

	/**
	 * @return the p value
	 */
	public double getPvalue();

}