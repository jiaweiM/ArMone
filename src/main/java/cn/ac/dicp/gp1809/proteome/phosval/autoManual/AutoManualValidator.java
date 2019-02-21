/* 
 ******************************************************************************
 * File: AutoManualValidator.java * * * Created on 05-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.phosval.autoManual;

import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.proteome.spectrum.PeakForMatch;
import cn.ac.dicp.gp1809.proteome.spectrum.SpectrumMatcher;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumFilter;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;

/**
 * Automatic manual validation module of the matches
 * 
 * @author Xinning
 * @version 0.1.1, 06-01-2009, 09:28:06
 */
public class AutoManualValidator {

	private AminoacidFragment aaf;

	private int min_contine_series = 3;

	public AutoManualValidator(AminoacidFragment aaf, int min_contine_series) {
		this.aaf = aaf;
		this.min_contine_series = min_contine_series;
	}

	/**
	 * 
	 * Validate the input peptide sequence
	 * 
	 * @param peaklist
	 *            the peak list
	 * @param seq
	 *            the modified peptide sequence
	 * @param types
	 *            the types of ions for validation (e.g. b&y ions or c&z ions)
	 * @param charge
	 *            the charge state
	 * @param filter
	 *            the spectrum filter
	 * @param threshold
	 *            the match threshold
	 * @param isMono
	 *            if use mono isotope mass
	 * @return
	 */
	public boolean validate(IMS2PeakList peaklist, String seq, int[] types,
	        short charge, ISpectrumFilter filter, ISpectrumThreshold threshold,
	        boolean isMono) {
		return this.validate(peaklist, ModifiedPeptideSequence
		        .parseSequence(seq), types, charge, filter, threshold, isMono);
	}

	/**
	 * 
	 * Validate the input peptide sequence
	 * 
	 * @param peaklist
	 *            the peak list
	 * @param mseq
	 *            the modified sequence
	 * @param types
	 *            the types of ions for validation (e.g. b&y ions or c&z ions)
	 * @param charge
	 *            the charge state
	 * @param filter
	 *            the spectrum filter
	 * @param threshold
	 *            the match threshold
	 * @param isMono
	 *            if use mono isotope mass
	 * @return
	 */
	public boolean validate(IMS2PeakList peaklist,
	        IModifiedPeptideSequence mseq, int[] types, short charge,
	        ISpectrumFilter filter, ISpectrumThreshold threshold, boolean isMono) {

		IMS2PeakList filtered = filter.filter(peaklist);

		Ions ions = this.aaf.fragment(mseq, types, isMono);

		PeakForMatch[] matchedPeaks = SpectrumMatcher.match(filtered, ions,
		        charge, SpectrumThreshold.ZERO_INTENSE_THRESHOLD, types);

		boolean[][] matches = new boolean[types.length][];

		for (int i = 0; i < types.length; i++) {
			int type = types[i];

			matches[i] = new boolean[ions.getIons(type).length];
		}

		for (PeakForMatch peak : matchedPeaks) {
			if (peak.isMatched()) {
				for (int i = 0; i < types.length; i++) {
					int type = types[i];
					this.parseMatches(matches[i], peak, type);
				}
			}
		}

		int max_series = -1;
		int max_type = -1;

		for (int i = 0; i < types.length; i++) {
			int max = this.maxContinuousSeries(matches[i]);

			if (max > max_series) {
				max_series = max;
				max_type = types[i];
			}
		}

		if (max_series >= this.min_contine_series) {
			return true;
		}

		return false;
	}

	/**
	 * The max continuous matched ions
	 * 
	 * @param matches
	 * @return
	 */
	private int maxContinuousSeries(boolean[] matches) {

		int max = 1;
		int curt = 0;

		for (boolean match : matches) {
			if (match) {
				curt++;
			} else {
				if (curt > max)
					max = curt;

				curt = 0;
			}
		}

		return max;
	}

	/**
	 * Parse the matches
	 * 
	 * @param matches
	 * @param peak
	 * @param type
	 */
	private void parseMatches(boolean[] matches, PeakForMatch peak, int type) {
		if (peak.isMatch2(type)) {
			Ion[] ins = peak.getMatchIons(type);
			for (Ion in : ins) {
				int ser = in.getSeries();
				if (!matches[ser]) {
					matches[ser] = true;
				}
			}
		}
	}

}
