/*
 ******************************************************************************
 * File: ICruxPhosphoPeptidePair.java * * * Created on 04-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides;

/**
 * The PercolatorScore phosphopeptide pair
 * 
 * @author Xinning
 * @version 0.1, 04-02-2009, 22:35:13
 */
public interface ICruxPhosphoPeptidePair extends IPhosPeptidePair {

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
	
	/**
	 * The ms2 Pvalue
	 * 
	 * @return
	 */
	public float getMS2Pvalue();

	/**
	 * The ms3 Pvalue
	 * 
	 * @return
	 */
	public float getMS3Pvalue();

	/**
	 * The summed Pvalue score
	 * 
	 * @return
	 */
	public float getPvalueSum();
	
	/**
	 * The ms2 Qvalue
	 * 
	 * @return
	 */
	public float getMS2Qvalue();

	/**
	 * The ms3 Qvalue
	 * 
	 * @return
	 */
	public float getMS3Qvalue();

	/**
	 * The summed Qvalue score
	 * 
	 * @return
	 */
	public float getQvalueSum();
	
	/**
	 * The ms2 PercolatorScore
	 * 
	 * @return
	 */
	public float getMS2PercolatorScore();

	/**
	 * The ms3 PercolatorScore
	 * 
	 * @return
	 */
	public float getMS3PercolatorScore();

	/**
	 * The summed PercolatorScore score
	 * 
	 * @return
	 */
	public float getPercolatorScoreSum();

}
