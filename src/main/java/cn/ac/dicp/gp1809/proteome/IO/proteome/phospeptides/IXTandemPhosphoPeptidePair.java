/* 
 ******************************************************************************
 * File: IXTandemPhosphoPeptidePair.java * * * Created on 02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides;

/**
 * The phosphopeptide pair from X!Tandem
 * 
 * @author Xinning
 * @version 0.1, 02-18-2009, 09:25:08
 */
public interface IXTandemPhosphoPeptidePair extends IPhosPeptidePair {
	/**
	 * @return the expected value
	 */
	public double getMS2Evalue();
	
	/**
	 * @return the expected value
	 */
	public double getMS3Evalue();
	
	/**
	 * The merged evalue
	 * 
	 * @return
	 */
	public double getEvalue();

	/**
	 * @return the Ionscore
	 */
	public float getMS2HyperScore();
	
	/**
	 * @return the Ionscore
	 */
	public float getMS3HyperScore();
	
	/**
	 * The summed ion score
	 * 
	 * @return
	 */
	public float getHyperScore();
}
