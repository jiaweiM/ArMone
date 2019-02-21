/* 
 ******************************************************************************
 * File: IAminoacidSequence.java * * * Created on 12-09-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.aasequence;

import cn.ac.dicp.gp1809.lang.IDeepCloneable;

/**
 * The aminoacid sequence. This may be a peptide or a protein. Commonly the
 * aminoaicd sequence is presented in one-character-sequence mode.
 * 
 * <p>
 * Changes:
 * <li>0.2, 02-22-2009: add method {@link #getAminoaicdAt(int)};
 * 
 * @author Xinning
 * @version 0.2, 02-22-2009, 20:59:41
 */
public interface IAminoacidSequence extends java.io.Serializable, IDeepCloneable {

	/**
	 * Get the aminoacid sequence formed by one-char-symbol (e.g. K for lys).
	 * For example, AAAAAAAA
	 * 
	 * @return
	 */
	public String getUniqueSequence();

	/**
	 * The length (number of the aminoacids) of the sequence.
	 * 
	 * @return
	 */
	public int length();

	/**
	 * The aminoacid at the speicified localization. The localization is from
	 * [1-n]
	 * 
	 * @param loc
	 *            the localization (from 1 - n)
	 * @return
	 * @throws IndexOutOfBoundsException
	 *             if the localization index is illegal
	 */
	public char getAminoaicdAt(int loc) throws IndexOutOfBoundsException;
}
