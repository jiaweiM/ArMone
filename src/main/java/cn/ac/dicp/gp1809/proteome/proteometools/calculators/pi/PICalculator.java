/*
 * *****************************************************************************
 * File: PICalculator.java * * * Created on 11-28-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.calculators.pi;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.BioException;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.math.ComputeObject;
import cn.ac.dicp.gp1809.proteome.math.FocusSearch;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.IPropertyCalculator;

/**
 * Input a sequence and then calculate its pi values. This algorithm is same as
 * that in TPP and "Compute pI/Mw" on Expasy.
 * (http://cn.expasy.org/tools/pi_tool.html)
 * 
 * References: # Bjellqvist, B.,Hughes, G.J., Pasquali, Ch., Paquet, N., Ravier,
 * F., Sanchez, J.-Ch., Frutiger, S. & Hochstrasser, D.F. The focusing positions
 * of polypeptides in immobilized pH gradients can be predicted from their amino
 * acid sequences. Electrophoresis 1993, 14, 1023-1031.
 * 
 * MEDLINE: 8125050 # Bjellqvist, B., Basse, B., Olsen, E. and Celis, J.E.
 * Reference points for comparisons of two-dimensional maps of proteins from
 * different human cell types defined in a pH scale where isoelectric points
 * correlate with polypeptide compositions. Electrophoresis 1994, 15, 529-539.
 * 
 * MEDLINE: 8055880
 * 
 * @author Xinning
 * @version 0.3, 12-09-2008, 21:27:16
 */
public final class PICalculator implements IPropertyCalculator{
	public static double noneTermineAAPk[] = new double[26];

	private static double NTermineAAPk[] = new double[26];
	private static double CTermineAAPk[] = new double[26];

	static {

		for (int i = 0; i < 26; i++) {
			noneTermineAAPk[i] = Aminoacids.AAPK[i][2];
			NTermineAAPk[i] = Aminoacids.AAPK[i][1];
			CTermineAAPk[i] = Aminoacids.AAPK[i][0];
		}
	}

	private static double PH_MIN = 0.0;
	private static double PH_MAX = 14.0;
	private static double PRECISION = 0.001;

	public PICalculator() {
	}
	
	/**
	 * Compute the pI value for the input aminoacid sequence. Equals 
	 * to the static calling of {@link #compute(String)}
	 * 
	 * @since 0.3
	 */
	@Override
	public double calculate(String sequence){
		return compute(sequence);
	}

	/**
	 * Compute the PI value for the input aminoacid sequence.
	 * 
	 * 
	 * @param sequence:
	 *            sequence with only aminoacid characters.
	 * @return
	 */
	public static double compute(String sequence) {

		double[] termpk = getTerminalPk(sequence);
		int[] composition = getPeptideComposition(sequence);

		ComputeObject obj = new ChargeCalculator(termpk, composition);

		double pi = 0.0;

		try {
			pi = FocusSearch.solve(PH_MIN, PH_MAX, PRECISION, obj);
		} catch (BioException e) {

			String out = sequence;
			int len = sequence.length();
			if (len > 50)
				out = sequence.substring(0, 50) + "...";

			char c = e.getMessage().charAt(30);
			if (c == '>') {
				pi = -1d;
				System.out.println("Sequence: \"" + out
				        + "\" with pi less than 0 and will be set as -1!");
			} else if (c == '<') {
				pi = 15d;
				System.out.println("Sequence: \"" + out
				        + "\" with pi bigger than 14 and will be set as 15!");
			} else {
				e.printStackTrace();
			}
		}

		return pi;
	}

	/*
	 * return: pk, a double array contains terminal pk information for this
	 * peptide 0 for N terminal 1 for C terminal
	 */
	public static double[] getTerminalPk(String sequence) {
		double[] pk = new double[2];

		int len = sequence.length();
		char nt = 0;

		for (int i = 0; i < len; i++) {
			char c = sequence.charAt(i);
			if (Aminoacids.isAminoacid(c)) {
				nt = c;
				break;
			}
		}

		char ct = 0;

		for (int i = len - 1; i >= 0; i--) {
			char c = sequence.charAt(i);
			if (Aminoacids.isAminoacid(c)) {
				ct = c;
				break;
			}
		}

		if (nt == 0 || ct == 0)
			throw new NullPointerException(
			        "No legal aminoaicd in the input sequence!");

		pk[0] = NTermineAAPk[nt - 'A'];
		pk[1] = CTermineAAPk[ct - 'A'];

		return pk;
	}

	public static int[] getPeptideComposition(String sequence) {
		int[] composition = new int[26];
		int len = sequence.length();

		for (int i = 0; i < len; i++) {
			char c = sequence.charAt(i);
			if (Aminoacids.isAminoacid(c))
				composition[c - 'A']++;
			else {
//				System.err.println("Skip the illegal aminoacid '" + c
//				        + "' for pI calculation.");
			}
		}

		return composition;
	}

	public static void main(String[] args) {
		String seq = "MAYTTFSQTKNDQLKEPMFFGQPVNVARYDQQKYDIFEKLIEKQLSFFWRPE";
		System.err.println("Sequence: " + seq + ";");
		System.err.println("PI: " + PICalculator.compute(seq));
	}
}
