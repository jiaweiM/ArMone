/* 
 ******************************************************************************
 * File: IPepMatch.java * * * Created on 03-31-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sqt;

import java.util.HashSet;

/**
 * A matched peptide
 * 
 * @author Xinning
 * @version 0.1, 03-31-2009, 20:37:24
 */
public interface IPepMatch {

	/**
	 * The rank by the primary score. Commonly be the rank by xcorr score.
	 * 
	 * @return
	 */
	public short getRankPrim();

	/**
	 * The rank by the pre- calculated score. Commonly be the rank by Sp. But
	 * may be others.
	 * 
	 * @return
	 */
	public short getRankPre();

	/**
	 * The calculated MH+
	 * 
	 * @return
	 */
	public double getCalculatedMH();

	/**
	 * The scores
	 * 
	 * @return
	 */
	public double[] getScores();

	/**
	 * The matched ions
	 * 
	 * @return
	 */
	public int getMatchedIons();

	/**
	 * The total ions trial
	 * 
	 * @return
	 */
	public int getTotalIons();

	/**
	 * The sequence
	 * 
	 * @return
	 */
	public String getSequence();

	/**
	 * The validation status. [validation status U = unknown, Y = yes, N = no, M
	 * = Maybe]
	 * 
	 * @return
	 */
	public char getValidationStatus();
	
	/**
	 * The protein references cotains this peptide
	 * 
	 * @return
	 */
	public HashSet<ProMatch> getReferences();
}
