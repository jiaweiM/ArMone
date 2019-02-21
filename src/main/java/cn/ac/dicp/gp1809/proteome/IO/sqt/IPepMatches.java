/* 
 ******************************************************************************
 * File: IPepMatches.java * * * Created on 03-31-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sqt;

/**
 * The peptide matches for a spectrum
 * 
 * @author Xinning
 * @version 0.1, 03-31-2009, 20:52:48
 */
public interface IPepMatches {

	/**
	 * The spectrum info
	 * 
	 * @return
	 */
	public ISpectrumInfo getSpectrumInfo();

	/**
	 * All the output matches for the spectrum. The order of the matches is the
	 * original order in the output file.
	 * 
	 * @return
	 */
	public IPepMatch[] getPepMatches();

	/**
	 * Return the n top matched identifications for the spectrum. The order of
	 * the matches is the original order in the output file. The number of
	 * matches returned is the minimum value of topN and the number of original
	 * matches
	 * 
	 * @return
	 */
	public IPepMatch[] getPepMatches(int topN);
}
