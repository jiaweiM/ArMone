/* 
 ******************************************************************************
 * File: ISequestPhosphoPeptidePair.java * * * Created on 02-17-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides;

/**
 * The sequest phosphopeptide pair
 * 
 * @author Xinning
 * @version 0.1, 02-17-2009, 22:28:05
 */
public interface ISequestPhosphoPeptidePair extends IPhosPeptidePair {

	/**
	 * The ms2 xcorr
	 * 
	 * @return
	 */
	public float getMS2Xcorr();

	/**
	 * The ms3 xcorr
	 * 
	 * @return
	 */
	public float getMS3Xcorr();

	/**
	 * The summed xcorr score
	 * 
	 * @return
	 */
	public float getXcorrSum();

	/**
	 * Because only the top ranked peptide has Dcn, and the merge of peptides
	 * into pairs must contains at least one of the top ranked peptides, this
	 * value is the deltaCn value of the top ranked peptides without
	 * consideration of which scan level this peptide is
	 * 
	 * @return
	 */
	public float getDeltaCn();

}
