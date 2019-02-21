/* 
 ******************************************************************************
 * File: ISequestPeptide.java * * * Created on 09-22-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;

/**
 * The sequest peptide
 * 
 * @author Xinning
 * @version 0.1, 09-22-2008, 21:12:07
 */
public interface ISequestPeptide extends IPeptide{

	/**
	 * Xcorr score
	 * 
	 * @return
	 */
	public float getXcorr();

	/**
	 * DeltaCn score
	 * 
	 * @return
	 */
	public float getDeltaCn();

	/**
	 * Sp score
	 * 
	 * @return
	 */
	public float getSp();

	/**
	 * Rsp score
	 * 
	 * @return
	 */
	public short getRsp();

	/**
	 * @return matched/all ions.
	 */
	public String getIons();

	/**
	 * The matched ions over the total number of predicted ions. This value
	 * should with 1 and 0. For different charge states, because the number of
	 * total predicted ions is not the same, ion percent for different charge
	 * states may be not comparable
	 * 
	 * @return
	 */
	public float getIonPercent();
	
	public double getInten();

}