/* 
 ******************************************************************************
 * File: PTMUtil.java * * * Created on 02-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM;

import java.util.LinkedList;

import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * The utilities used for the parsing of PTM after database search.
 * 
 * @author Xinning
 * @version 0.1, 02-19-2009, 21:42:38
 */
public class PTMUtil {

	/**
	 * Get the number of PTM modifications. Herein, the assumption is that only
	 * character with upper case is a aminoacid, other symbols and lower cased
	 * characters are all modifications
	 * 
	 * @param sequence
	 *            input seq
	 * @return number of modification site;
	 */
	public static int getModifSiteNum(String sequence) {
		String seq_no_term = PeptideUtil.getSequence(sequence);

		int count = 0;
		for (int i = 0, n = seq_no_term.length(); i < n; i++) {
			char c = seq_no_term.charAt(i);
			if (c < 'A' || c > 'Z') {
				count++;
			}
		}

		return count;
	}

	/**
	 * Get the PTM modifications. Herein, the assumption is that only character
	 * with upper case is a aminoacid, other symbols and lower cased characters
	 * are all modifications.
	 * 
	 * <p>
	 * <b>The localization of the PTM is from 1-n. There is the index of the
	 * first aminoacid is 1.<b>
	 * <p>The N-term modification is located in <b>0<b>.
	 * 
	 * @param sequence
	 *            input seq
	 * 
	 * @return modification sites
	 */
	public static IModifSite[] getModifSites(String sequence) {
//		System.out.println(sequence);
		String seq_no_term = PeptideUtil.getSequence(sequence);  //without term, with modification	
		LinkedList<ModifSite> list = new LinkedList<ModifSite>();
		char c0 = seq_no_term.charAt(0);
		int idx = 0;
		if(c0 < 'A' || c0 > 'Z'){
			list.add(new ModifSite(ModSite.newInstance_PepNterm(), 0, c0));
			for (int i = 1, n = seq_no_term.length(); i < n; i++) {
				char c = seq_no_term.charAt(i);
				if (c < 'A' || c > 'Z') {
					list.add(new ModifSite(ModSite.newInstance_aa(seq_no_term.charAt(i-1)), idx, c));
				} else {
					idx++;
				}
			}
		}else{
			for (int i = 0, n = seq_no_term.length(); i < n; i++) {
				char c = seq_no_term.charAt(i);
				if (c < 'A' || c > 'Z') {
					list.add(new ModifSite(ModSite.newInstance_aa(seq_no_term.charAt(i-1)), idx, c));
				} else {
					idx++;
				}
			}
		}

		if (list.size() == 0)
			return null;

		return list.toArray(new ModifSite[list.size()]);
	}
	
	public static IModifiedPeptideSequence getModifiedSequenceNoTargetModif(
	        String seq, char symbol) {

		StringBuilder sb = new StringBuilder(seq.length());
		for (int i = 0, n = seq.length(); i < n; i++) {
			char c = seq.charAt(i);
			if (c < 'A' || c > 'Z') {
				if (c == symbol) {
					continue;
				}
			}

			sb.append(c);
		}

		return ModifiedPeptideSequence.parseSequence(sb.toString());
	}
	
	
}
