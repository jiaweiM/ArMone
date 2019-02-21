/* 
 ******************************************************************************
 * File: IPeptideSequence.java * * * Created on 02-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.aasequence;

/**
 * The formatted peptide sequence. With previouseAA, and nextAA information.
 * 
 * @author Xinning
 * @version 0.1.1, 02-20-2009, 10:34:28
 */
public interface IPeptideSequence extends IAminoacidSequence {

	/**
	 * @return the previous aminoacid for this peptide sequence.
	 */
	public char getPreviousAA();

	/**
	 * @return the next aminoacid for this peptide sequence.
	 */
	public char getNextAA();

	/**
	 * @return first aminoacid of this peptide sequence.
	 */
	public char getFistAA();

	/**
	 * @return the last aminoacid of this peptide sequence.
	 */
	public char getLastAA();

	/**
	 * @return the sequence with format of A.AAAAAAA.A or A.AAAAA#A.A
	 */
	public String getFormattedSequence();

}
