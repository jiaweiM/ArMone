/*
 * *****************************************************************************
 * File: PeptideUtil.java  * * * Created on 12-06-2007 
 * Copyright (c) 2007 Xinning Jiang vext@163.com 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome;

import java.util.regex.*;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequenceUpdateException;

/**
 * Tools usually used in peptide parsing are inclued in this class. This class
 * is used to get the scan number,peptide string ,charge,molecularweight and so
 * on.Use the string of a line get in the summary files as input,and output the
 * proper form.
 * 
 * <p>
 * Changes:
 * <li>0.4.5, 02-23-2009: the character between A and Z are all be considered as
 * legal aminoacid char because these characters such as J, B may have special
 * usage.
 * 
 * @author Xinning
 * @version 0.4.5, 02-23-2009, 10:14:11
 */
public class PeptideUtil {

	private PeptideUtil() {}

	/**
	 * @param sequence : a raw peptide
	 * @return parsed unique sequence with terminal peptide eg: input
	 *         D.GSA@SSS.- ; return : D.GSASSS.-
	 */
	public static String getUniqueSequenceWithTermine(String seq_sequest) {
		StringBuilder sb = new StringBuilder(seq_sequest);

		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (!((c >= 'A' && c <= 'Z') || c == '.' || c == '-')) {
				sb.deleteCharAt(i);
				i--;
			}
		}
		return sb.toString();
	}

	/**
	 * @param sequence : a raw peptide
	 * @return parsed unique sequence without terminal peptide eg: input
	 *         D.GSA@SSS.- ; return : GSASSS
	 */
	public static String getUniqueSequence(String seq_sequest) {
		return validateSequence(getSequence(seq_sequest));
	}

	/**
	 * Return a sequence without terminal but with modif symbols;
	 * 
	 * @param sequence sequence input (with the format of sequest output,
	 *            X.X#XXXX.X)
	 * @return sequence without term(X#XXXX);
	 */
	public static String getSequence(String seq_sequest) {
		
		int st = 2;
		int en = seq_sequest.length() - 2;

		if (seq_sequest.charAt(1) != '.')
			st = 0;
		if (seq_sequest.charAt(en) != '.') {
			// D.GSA@SSS.
			if (seq_sequest.charAt(en + 1) == '.')
				en++;
			// D.GSA@SSS
			else
				en += 2;
		}

		return seq_sequest.substring(st, en);
	}

	/**
	 * The number of aminoacids
	 * 
	 * @param sequence sequence input (with the format of sequest output,
	 *            X.X#XXXX.X)
	 * @return the number of aminoaicd in the peptide sequence
	 */
	public static int getSequenceLength(String seq) {
		return getUniqueSequence(seq).length();
	}

	/**
	 * This sequence can contain any illegal aminoacid which can be removed
	 * automaticlly. e.g. AABBB#SDFDSLKLL -> AASDFDSLKLL, AABBBpSDFDSLKLL ->
	 * AABBBSDFDSLKLL
	 * 
	 * @param sequence not a sequest outputted peptide.
	 * @return
	 */
	public static String validateSequence(String sequence) {
		int len = sequence.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = sequence.charAt(i);
			if (c >= 'A' && c <= 'Z') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * Format the sequence into the following type: <div>A.AAAAAAA#AA.A (A can
	 * be - if the terminal of protein sequence).</div>
	 * 
	 * 
	 * @param seq_no_term raw sequence: AAAAAA#AAAAA
	 * @param left the left one aminoacid (can be - if the terminal of protein)
	 * @param right the right one aminoacid (can be - if the terminal of
	 *            protein)
	 * @return the formatted sequence
	 */
	public static String formatSequence(String seq_no_term, char left, char right) {

		StringBuilder sb = new StringBuilder(seq_no_term.length() + 4);
		sb.append(left).append('.').append(seq_no_term).append('.').append(right);

		return sb.toString();
	}

	public static String formatSequence(String seq) {
		char[] aas = seq.toCharArray();
		int length = aas.length;
		if (aas[1] == '.') {
			if (aas[length - 2] == '.') {
				return seq;
			} else if (aas[length - 1] == '.') {
				return seq + "-";
			} else {
				return seq + ".-";
			}
		} else if (aas[0] == '.') {
			if (aas[length - 2] == '.') {
				return "-" + seq;
			} else if (aas[length - 1] == '.') {
				return "-" + seq + "-";
			} else {
				return "-" + seq + ".-";
			}
		} else {
			if (aas[length - 2] == '.') {
				return "-." + seq;
			} else if (aas[length - 1] == '.') {
				return "-." + seq + "-";
			} else {
				return "-." + seq + ".-";
			}
		}
	}

	/**
	 * Get the number of modification;
	 * 
	 * @param sequence input seq
	 * @param regex symbol of the modification;Input as regex;e.g. [#*] for two
	 *            modif symol # or *
	 * @return number of modification site;
	 */
	public static short getModifSiteNum(String sequence, String regex) {
		Pattern pattern = Pattern.compile(regex);
		return getModifSiteNum(sequence, pattern);
	}

	/**
	 * Get the number of modification;
	 * 
	 * @param sequence input seq
	 * @param regex symbol pattern of the modification;Input as regex; e.g. [#*]
	 *            for two modif symol # or *
	 * @return number of modification site;
	 */
	public static short getModifSiteNum(String sequence, Pattern pattern) {
		short count = 0;
		Matcher matcher = pattern.matcher(sequence);
		while (matcher.find()) {
			count++;
		}
		return count;
	}

	/**
	 * Get the number of modification;
	 * 
	 * @param sequence input seq
	 * @param regex symbol pattern of the modification;Input as regex; e.g. [#*]
	 *            for two modif symol # or *
	 * @return number of modification site;
	 */
	public static short getModifSiteNum(String sequence, char symbol) {
		short count = 0;
		int from = 0;
		while ((from = sequence.indexOf(symbol, from)) != -1) {
			count++;
			from++;
		}
		return count;
	}

	/**
	 * Methord for the replacement of peptide with neutral loss eg, in the
	 * sequence of MS2 return by sequest phos modif symol is # while in MS3,
	 * phos modif with neutral losss become @ R.LRS#LEGS#EAEGNAGEQS#R.S equals
	 * R.LRS#LEGS@EAEGNAGEQS#R.S
	 * 
	 * This methord can also be used in the replacement of modification only
	 * needing to use the "" as newsymbol.
	 * 
	 * @param peptide for the replacement
	 * @param oldsymbol in the sequence
	 * @param newsybol for the replacement two symbols in paramter use pattern
	 *            statements.
	 * 
	 * 
	 * @throws SequenceUpdateException
	 */

	public static void replaceModification(IPeptide peptide, String oldsymbolregx, String newsymbol)
			throws SequenceUpdateException {
		String newSequence = peptide.getSequence().replaceAll(oldsymbolregx, newsymbol);
		peptide.updateSequence(newSequence);
	}

	public static void main(String[] args) {
		String seq = "*DDDD@B#SSS.";
		/*
		 * System.out.println("Sequence: " + PeptideUtil.getSequence(seq));
		 * System.out.println("UniWithTerm: " +
		 * PeptideUtil.getUniqueSequenceWithTermine(seq));
		 * System.out.println("UniSeq: " + PeptideUtil.getUniqueSequence(seq));
		 */

		System.out.println(PeptideUtil.formatSequence(seq));
	}
}