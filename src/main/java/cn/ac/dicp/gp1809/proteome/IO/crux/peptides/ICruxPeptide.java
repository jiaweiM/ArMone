/*
 ******************************************************************************
 * File: ICruxPeptide.java * * * Created on 04-01-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.crux.peptides;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;

/**
 * The crux Peptide
 * 
 * @author Xinning
 * @version 0.1, 04-01-2009, 22:18:18
 */
public interface ICruxPeptide extends IPeptide {

	/**
	 * The rank by the xcorr score. In the original output, this is the primary
	 * rank which can be get by {@link #getRank()}. After the calculation of
	 * percalotar score, the primary score will become the rank by percalotar
	 * score and they will not be the same
	 * 
	 * @return
	 */
	public short getRxc();

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

	/**
	 * Xcorr score. If not contains, return Float.NaN.
	 * 
	 * @return
	 */
	public float getXcorr();

	/**
	 * DeltaCn score. If not contains, return Float.NaN.
	 * 
	 * @return
	 */
	public float getDeltaCn();

	/**
	 * Sp score. If not contains, return Float.NaN.
	 * 
	 * @return
	 */
	public float getSp();

	/**
	 * The pvalue (may be the log() value?). If not contains, return Float.NaN.
	 * 
	 * @return
	 */
	public float getPValue();

	/**
	 * The Qvalue (may be the log() value?). If not contains, return Float.NaN.
	 * 
	 * @return
	 */
	public float getQValue();

	/**
	 * The score after the percolator calculation. (may be the log() value?). If
	 * not contains, return Float.NaN.
	 * 
	 * @return
	 */
	public float getPercolator_score();

}