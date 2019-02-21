/*
 ******************************************************************************
 * File: TrialGenerator.java * * * Created on 02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation;

import java.util.ArrayList;

import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.PeakForMatch;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.util.SpectrumUtil;

/**
 * Not used yet
 * 
 * @author Xinning
 * @version 0.1, 02-18-2009, 14:35:49
 */
public class TrialGenerator {

	private static final ISpectrumThreshold THRESHOLD = SpectrumThreshold.PERCENT_10_INTENSE_THRESHOLD;
	/**
	 * Tolerance for the match of peaks
	 */
	private static double Tolerence = THRESHOLD.getMassTolerance();

	private PeakForMatch[] peaks;

	private short charge;

	public static float NL_MS = 98f;

	/**
	 * @param peaklist
	 *            the peaklist in MS2 spectrum.
	 * @param neuSiteNum
	 *            the number of neutral site in the peptide sequence.
	 */
	public TrialGenerator(MS2PeakList peaklist, int neuSiteNum) {
		this.charge = peaklist.getPrecursePeak().getCharge();
		this.peaks = SpectrumUtil.getPeaksNoNeuLossPeak(peaklist, neuSiteNum,
		        NL_MS, Tolerence);
	}

	/**
	 * For the computation of match score, the neutral site-containing fragments
	 * are firstly matched to the MS2 spectra for the selection of
	 * may-be-produced ions.
	 * 
	 * @param usedions
	 *            the ions with modif that neutral loss can occures. this ions
	 *            can be generated from AminoacidFragment.getNeuIons();
	 * @return the ion which can be used for further process. the mass of ion
	 *         has been minus by the NL_MS for the neutral loss, so this ions
	 *         can be directly used for MS3 comparison.
	 */
	public Ion[] getTrial(Ion[] usedions) {
		ArrayList<Ion> list = new ArrayList<Ion>(20);
		for (int m = 0; m < usedions.length; m++) {
			Ion ion = usedions[m];
			int j = 0;
			boolean match = false;
			for (int i = charge; i > 0; i--) {
				double mz = ion.getMzVsCharge(i);
				for (; j < this.peaks.length; j++) {
					PeakForMatch peak = peaks[j];
					match = PeakForMatch.isMatch2(peak, mz, THRESHOLD);

					//out of match.
					if (match || peak.getMz() - mz > Tolerence) {
						break;
					}
				}

				if (match) {
					list.add(new Ion(ion.getMz() - NL_MS, ion.getType(), ion
					        .getSeries()));
					break;
				}
			}
		}

		return list.toArray(new Ion[list.size()]);
	}
}
