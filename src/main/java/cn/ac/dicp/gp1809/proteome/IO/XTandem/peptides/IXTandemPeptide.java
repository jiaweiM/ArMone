/* 
 ******************************************************************************
 * File: IXTandemPeptide.java * * * Created on 09-22-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;

/**
 * The XTandem Peptide
 * 
 * @author Xinning
 * @version 0.2.0.1, 03-14-2009, 12:49:42
 */
public interface IXTandemPeptide extends IPeptide {

	/**
	 * @return the expected value
	 */
	public double getEvalue();

	/**
	 * @return the Hyperscore
	 */
	public float getHyperscore();

	/**
	 * @return the hyperscore of the second top matched peptides (similar as
	 *         DeltaCn ??)
	 */
	public float getNextHyperscore();
	
	/**
	 * 
	 * @return
	 */
	public float getYScore();
	
	/**
	 * 
	 * @return
	 */
	public float getBScore();

}