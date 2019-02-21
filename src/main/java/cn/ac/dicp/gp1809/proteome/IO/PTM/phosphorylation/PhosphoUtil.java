/* 
 ******************************************************************************
 * File: PhosphoUtil.java * * * Created on 02-17-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation;

import java.util.LinkedList;

import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * Utilities for phosphopeptides
 * 
 * @author Xinning
 * @version 0.1.1, 03-03-2009, 21:39:16
 */
public class PhosphoUtil {

	/**
	 * Parse the phosphorylation sites. return null, if the peptide is a non-
	 * phosphorylated peptide
	 * 
	 * @param seq
	 * @param phosSymbol
	 * @param neutralSymbol
	 *            if no neutral loss, this char can be (char)0, or use the
	 *            method {@link #getPhosphoSites(String, char)}
	 * @return
	 */
	public static PhosphoSite[] getPhosphoSites(String seq, char phosSymbol,
	        char neutralSymbol) {
		String seq_no_term = PeptideUtil.getSequence(seq);

		int idx = 0;
		LinkedList<PhosphoSite> list = new LinkedList<PhosphoSite>();
		for (int i = 0, n = seq_no_term.length(); i < n; i++) {
			char c = seq_no_term.charAt(i);
			if (c < 'A' || c > 'Z') {
				if (c == phosSymbol) {
					list
					        .add(new PhosphoSite(ModSite.newInstance_aa(seq_no_term.charAt(i - 1)),
					                idx, c));
				} else if (c == neutralSymbol) {
					list.add(new PhosphoSite(ModSite.newInstance_aa(seq_no_term.charAt(i - 1)), idx, c,
					        true));
				}
			} else {
				idx++;
			}
		}

		if (list.size() == 0)
			return null;

		return list.toArray(new PhosphoSite[list.size()]);
	}

	/**
	 * Parse the phosphorylation sites. if the peptide is a non- phosphorylated
	 * peptide
	 * 
	 * @param seq
	 *            the sequest formatted sequence, e.g. A.AAAAA#AAA*AAA.A
	 * @param phosSymbol
	 *            the char symbol of the phosphosite
	 * @param neutralSymbol
	 *            if no neutral loss, this char can be (char)0, or use the
	 *            method
	 * @return the phosphosites, if a non-phosphorylated peptide, return null.
	 */
	public static PhosphoSite[] getPhosphoSites(String seq, char phosSymbol) {
		String seq_no_term = PeptideUtil.getSequence(seq);

		int idx = 0;
		LinkedList<PhosphoSite> list = new LinkedList<PhosphoSite>();
		for (int i = 0, n = seq_no_term.length(); i < n; i++) {
			char c = seq_no_term.charAt(i);
			if (c < 'A' || c > 'Z') {
				if (c == phosSymbol) {
					list
					        .add(new PhosphoSite(ModSite.newInstance_aa(seq_no_term.charAt(i - 1)),
					                idx, c));
				}
			} else {
				idx++;
			}
		}

		if (list.size() == 0)
			return null;

		return list.toArray(new PhosphoSite[list.size()]);
	}

	/**
	 * Parse the phosphorylation sites. return null, if the peptide is a non-
	 * phosphorylated peptide
	 * 
	 * @param seq
	 * @param phosSymbol
	 * @param neutralSymbol
	 *            if no neutral loss, this char can be (char)0, or use the
	 *            method {@link #getPhosphoSites(String, char)}
	 * @return
	 */
	public static int getPhosphoSitesNumber(String seq, char phosSymbol,
	        char neutralSymbol) {
		String seq_no_term = PeptideUtil.getSequence(seq);

		int count = 0;
		for (int i = 0, n = seq_no_term.length(); i < n; i++) {
			char c = seq_no_term.charAt(i);
			if (c < 'A' || c > 'Z') {
				if (c == phosSymbol) {
					count++;
				} else if (c == neutralSymbol) {
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * Parse the phosphorylation sites. if the peptide is a non- phosphorylated
	 * peptide
	 * 
	 * @param seq
	 *            the sequest formatted sequence, e.g. A.AAAAA#AAA*AAA.A
	 * @param phosSymbol
	 *            the char symbol of the phosphosite
	 * @param neutralSymbol
	 *            if no neutral loss, this char can be (char)0, or use the
	 *            method
	 * @return the phosphosites, if a non-phosphorylated peptide, return null.
	 */
	public static int getPhosphoSitesNumber(String seq, char phosSymbol) {
		String seq_no_term = PeptideUtil.getSequence(seq);

		int count = 0;
		for (int i = 0, n = seq_no_term.length(); i < n; i++) {
			char c = seq_no_term.charAt(i);
			if (c < 'A' || c > 'Z') {
				if (c == phosSymbol) {
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * Remove the terminal aminoaicids, the phosphorylation symbols and the
	 * neutral lost phosphorylation symbols.
	 * 
	 * A.AAAAAA#AAAA@AAAA*AAA.A ---> AAAAAAAAAAAAAAA*AAA
	 * 
	 * A.AAAAApAAAAAAA*AAAA.A --> AAAAAAAAAA*AAA
	 * 
	 * @param seq
	 * @param phosSymbol
	 * @param neuSymbol
	 * @return
	 */
	public static String getSequenceNoPhosModif(String seq, char phosSymbol,
	        char neutralSymbol) {
		String seq_no_term = PeptideUtil.getSequence(seq);

		StringBuilder sb = new StringBuilder(seq_no_term.length());
		for (int i = 0, n = seq_no_term.length(); i < n; i++) {
			char c = seq_no_term.charAt(i);
			if (c < 'A' || c > 'Z') {
				if (c == phosSymbol || c == neutralSymbol) {
					continue;
				}
			}

			sb.append(c);
		}

		return sb.toString();
	}

	/**
	 * Remove the terminal aminoaicids, the phosphorylation symbols and the
	 * neutral lost phosphorylation symbols.
	 * 
	 * A.AAAAAA#AAAA@AAAA*AAA.A ---> AAAAAAAAAAAAAAA*AAA
	 * 
	 * A.AAAAApAAAAAAA*AAAA.A --> AAAAAAAAAA*AAA
	 * 
	 * @param seq
	 * @param phosSymbol
	 * @param neuSymbol
	 * @return
	 */
	public static String getSequenceNoPhosModif(String seq, char phosSymbol) {
		String seq_no_term = PeptideUtil.getSequence(seq);

		StringBuilder sb = new StringBuilder(seq_no_term.length());
		for (int i = 0, n = seq_no_term.length(); i < n; i++) {
			char c = seq_no_term.charAt(i);
			if (c < 'A' || c > 'Z') {
				if (c == phosSymbol) {
					continue;
				}
			}

			sb.append(c);
		}

		return sb.toString();
	}

	/**
	 * Remove the terminal aminoaicids, the phosphorylation symbols and the
	 * neutral lost phosphorylation symbols.
	 * 
	 * A.AAAAAA#AAAA@AAAA*AAA.A ---> A.AAAAAAAAAAAAAAA*AAA.A
	 * 
	 * A.AAAAApAAAAAAA*AAAA.A --> A.AAAAAAAAAA*AAA.A
	 * 
	 * @since 0.1.1
	 * @param seq
	 * @param phosSymbol
	 * @param neuSymbol
	 * @return
	 */
	public static IModifiedPeptideSequence getModifiedSequenceNoPhosModif(
	        String seq, char phosSymbol, char neutralSymbol) {

		StringBuilder sb = new StringBuilder(seq.length());
		for (int i = 0, n = seq.length(); i < n; i++) {
			char c = seq.charAt(i);
			if (c < 'A' || c > 'Z') {
				if (c == phosSymbol || c == neutralSymbol) {
					continue;
				}
			}

			sb.append(c);
		}

		return ModifiedPeptideSequence.parseSequence(sb.toString());
	}

	/**
	 * Remove the terminal aminoaicids, the phosphorylation symbols and the
	 * neutral lost phosphorylation symbols.
	 * 
	 * A.AAAAAA#AAAA@AAAA*AAA.A ---> A.AAAAAAAAAAAAAAA*AAA.A
	 * 
	 * A.AAAAApAAAAAAA*AAAA.A --> A.AAAAAAAAAA*AAA.A
	 * 
	 * @since 0.1.1
	 * @param seq
	 * @param phosSymbol
	 * @param neuSymbol
	 * @return
	 */
	public static IModifiedPeptideSequence getModifiedSequenceNoPhosModif(
	        String seq, char phosSymbol) {
		StringBuilder sb = new StringBuilder(seq.length());
		for (int i = 0, n = seq.length(); i < n; i++) {
			char c = seq.charAt(i);
			if (c < 'A' || c > 'Z') {
				if (c == phosSymbol) {
					continue;
				}
			}

			sb.append(c);
		}

		return ModifiedPeptideSequence.parseSequence(sb.toString());
	}

}
