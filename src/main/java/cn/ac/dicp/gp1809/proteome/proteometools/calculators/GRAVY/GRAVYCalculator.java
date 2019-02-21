/* 
 ******************************************************************************
 * File:GRAVYCalculator.java * * * Created on 2010-7-2
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.calculators.GRAVY;

import java.text.DecimalFormat;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * 
 * Amino Acid Hydropathy Scores. These scores are based on the values given by
 * the original Kyte-Doolittle paper, "Kyte, J. and Doolittle, R. 1982. A simple
 * method for displaying the hydropathic character of a protein. J. Mol. Biol.
 * 157: 105-132."
 * 
 * http://gcat.davidson.edu/rakarnik/aminoacidscores.htm
 * 
 * @author ck
 * @version 2010-7-2, 18:49:40
 */
public class GRAVYCalculator {

	private static DecimalFormat df4 = DecimalFormats.DF0_4;

	public static double calculate(IPeptide pep) {
		String sequence = PeptideUtil.getUniqueSequence(pep.getSequence());
		return calculate(sequence);
	}

	public static double calculate(String sequence) {
		double[] HydroScore = Aminoacids.HydroScore;
		double total = 0;
		sequence = PeptideUtil.getUniqueSequence(sequence);
		char[] aas = sequence.toCharArray();
		for (int i = 0; i < aas.length; i++) {
			double score = HydroScore[aas[i] - 65];
			total += score;
		}

		int len = sequence.length();
		total = Double.parseDouble(df4.format(total / len));
		return total;
	}

	public static void main(String[] args) {
		String seq = "KGGAKRHRKVLRD";
		System.out.println(calculate(seq));

	}
}
