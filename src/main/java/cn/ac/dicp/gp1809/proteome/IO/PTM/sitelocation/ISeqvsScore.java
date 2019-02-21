/* 
 ******************************************************************************
 * File: ISeqvsScore.java * * * Created on 06-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation;

import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;

/**
 * 
 * @author Xinning
 * @version 0.1, 06-12-2009, 15:44:14
 */
public interface ISeqvsScore {
	
	/**
	 * Phosphopeptide with most probable site localization(s). The symbol is the
	 * original symbols for phospho symbol and neutral loss symbol.
	 */
	public IModifiedPeptideSequence getSequence();
	
	
	/**
	 * The site where NL occurred for MS3
	 */
	public int getNLSite();
}
