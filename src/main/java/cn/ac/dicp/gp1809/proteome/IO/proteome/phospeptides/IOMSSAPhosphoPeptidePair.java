/* 
 ******************************************************************************
 * File: IOMSSAPhosphoPeptidePair.java * * * Created on 02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides;

/**
 * The phosphopeptide pair from OMSSA
 * 
 * @author Xinning
 * @version 0.1, 02-18-2009, 09:24:30
 */
public interface IOMSSAPhosphoPeptidePair extends IPhosPeptidePair{
	
	/**
	 * @return the expected value
	 */
	public double getEvalue();

	/**
	 * @return the p value
	 */
	public double getPvalue();
	

	/**
	 * @return the expected value
	 */
	public double getMS2Evalue();

	/**
	 * @return the p value
	 */
	public double getMS2Pvalue();
	
	/**
	 * @return the expected value
	 */
	public double getMS3Evalue();

	/**
	 * @return the p value
	 */
	public double getMS3Pvalue();
	

}
