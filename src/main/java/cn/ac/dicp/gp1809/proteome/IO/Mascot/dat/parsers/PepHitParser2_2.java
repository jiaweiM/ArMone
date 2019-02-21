/* 
 ******************************************************************************
 * File: PepHitParser2_2.java * * * Created on 11-12-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.parsers;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.DatEntryParser;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.PeptideHit;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * Peptide hit parser of mascot v2.2
 * 
 * @author Xinning
 * @version 0.1, 11-12-2008, 09:40:56
 */
class PepHitParser2_2 extends AbstractPepHitParser {

	@Override
	public PeptideHit parse(String[] hit_str) {

		if (hit_str == null || hit_str.length < 2)
			throw new IllegalArgumentException(
					"For mascot v2.2, more than two string " + "lines are expected to represent a peptide hit.");

		PeptideHit pephit = this.parsePeptide(DatEntryParser.parseEntry(hit_str[0]).getValue());

		pephit.setHitString(hit_str);
		pephit.setTeminalaa(this.parseTerminus(hit_str[1]));

		return pephit;
	}

	/**
	 * Parse the terminal string into char array of terminal aminoacids
	 * 
	 * @param term
	 * @return
	 */
	private char[][] parseTerminus(String term) {
		// The number of terminals equals the number of proteins
		String[] terms = StringUtil.split(DatEntryParser.parseEntry(term).getValue(), ':');
		int len = terms.length;
		char[][] terminalaa = new char[len][2];

		for (int i = 0; i < len; i++) {
			String ter = terms[i];

			if (ter.length() != 3)
				throw new IllegalArgumentException("The expected terminal aminoacids string should be "
						+ "char(left_term),char(right_term), current: " + ter);
			terminalaa[i][0] = ter.charAt(0);
			terminalaa[i][1] = ter.charAt(2);
		}

		return terminalaa;
	}

	@Override
	public int getNumLineperHit() {
		return 2;
	}
}
