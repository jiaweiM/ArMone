/*
 * *****************************************************************************
 * File: PTMScore.java * * * Created on 08-04-2008 Copyright (c) 2008 Xinning
 * Jiang (vext@163.com) All right reserved. Use is subject to license terms.
 * *****************************************************************************
 * *
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation;

import java.util.ArrayList;

import cn.ac.dicp.gp1809.proteome.math.RandomPCalor;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.RegionTopNIntensityFilter;

/**
 * To estimate the match score for a match
 * 
 * <p>
 * The input is the peak list contains experimental spectra data generated for
 * MS spectrometer And an serial of ions to be match (neutral ions), and return
 * the match score;
 * 
 * <p>
 * The bigger the match score is ,the better the match is;
 * 
 * @author Xinning
 * @version 0.2, 06-12-2009, 10:04:11
 */
public class PTMScore implements IPTMPeptideScore {

	private double peakThreshold;
	private IMS2PeakList peaklist;
	private double[] peakmzarr = null;
	private double singlep = 0;
	private short charge = 1;
	private ArrayList<Ion> matchlist;

	/**
	 * @param rawpeaklist spectrum geted from mass spectromater;
	 * @param topn filter a spectrum by top n per 100amu window;
	 */
	public PTMScore(IMS2PeakList rawpeaklist, short charge, RegionTopNIntensityFilter filter,
			ISpectrumThreshold threshold) {
		
		this.charge = charge;
		this.peaklist = filter.filter(rawpeaklist);
		this.peakmzarr = peaklist.getPeakMzArray();
		this.singlep = filter.singleP();
		this.charge = peaklist.getPrecursePeak().getCharge();
		this.peakThreshold = threshold.getMassTolerance();
	}

	/**
	 * In the match, if the charge state is 1+ or 2+ only 1+ fragment is
	 * considered if 3+ or higher, 1+ and 2+ fragments are both considered, then
	 * the probability for the match is returned. <b>The lower the probability,
	 * the the less this match tend to be random. In other word, the lower, the
	 * better.</b>
	 * 
	 * @param ions candidate ions to be matched;
	 * @return probability for the match as a random match. the lower the
	 *         probability, the the less this match tend to be random.
	 */
	public double calculateProbability(Ion[] ions) {
		int trial = 0;
		if (ions == null || ions.length == 0) {
			return 1.0;
		}

		int len = this.peakmzarr.length;

		int matchedcount = 0;
		trial = ions.length;

		matchlist = new ArrayList<Ion>();

		/*
		 * If the charge state of the precursor ion is 1+ or 2+, only 1+
		 * fragment is used for identification. Otherwise, fragment with charge
		 * state from 1+ to charge-1 is used.
		 */
		int iz = this.charge <= 1 ? 2 : this.charge;

		/*
		 * Only the fragment of peptide is considered as a trial (without
		 * considerison of charge isomers). That is, whenever one of b7 fragment
		 * with whatever charge state (b7+ b7++ or b7+++), b7 will be considered
		 * as match with the match count of 1;
		 */
		L1:
		for (int i = 0; i < ions.length; i++) {
			Ion ion = ions[i];

			for (int c = 1; c < iz; c++) {
				double ms = ion.getMzVsCharge(c);

				for (int j = 0; j < len; j++) {
					double tempmz = this.peakmzarr[j];

					if (approximateEqual(ms, tempmz)) {
						matchedcount++;
						matchlist.add(ion);
						continue L1;
					}

					if (tempmz > ms) // over the range
						break;
				}
			}
		}

		return RandomPCalor.getProbility(trial, matchedcount, this.singlep);
	}

	/**
	 * In the match, if the charge state is 1+ or 2+ only 1+ fragement is
	 * considered if 3+ or higher, 1+ and 2+ fragments are both consdered, then
	 * the probility for the match is returned.
	 * 
	 * @param ions candidate ions to be matched;
	 * @return Score for the match bigger is better
	 */
	public double calculateScore(Ion[] ions) {
		return RandomPCalor.getScore(calculateProbability(ions));
	}

	/**
	 * If the ion and the peak match under the current ms resolution
	 * (tolerence).
	 * 
	 * @param ionms
	 * @param peak
	 */

	private boolean approximateEqual(double ionms, double peak) {
		if (Math.abs(ionms - peak) <= peakThreshold)
			return true;

		return false;
	}

	/**
	 * @return the matched ions array
	 */
	public Ion[] getMatchedIons() {
		return this.matchlist.toArray(new Ion[this.matchlist.size()]);
	}
}
