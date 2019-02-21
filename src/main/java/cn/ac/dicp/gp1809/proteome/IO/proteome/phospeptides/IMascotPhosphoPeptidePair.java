/* 
 ******************************************************************************
 * File: IMascotPhosphoPeptidePair.java * * * Created on 02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides;

/**
 * The mascot phosphopeptide pair
 * 
 * @author Xinning
 * @version 0.1, 02-18-2009, 09:16:53
 */
public interface IMascotPhosphoPeptidePair extends IPhosPeptidePair {
	
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
	public float getMS2Ionscore();
	
	/**
	 * @return the Ionscore
	 */
	public float getMS3Ionscore();
	
	/**
	 * The summed ion score
	 * 
	 * @return
	 */
	public float getIonscore();
	
}
