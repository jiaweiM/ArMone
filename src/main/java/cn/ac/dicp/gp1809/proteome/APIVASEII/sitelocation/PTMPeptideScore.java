/* 
 ******************************************************************************
 * File: PhosPeptideScore.java * * * Created on 06-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation;

import java.util.Arrays;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.RegionTopNIntensityFilter;

/**
 * The phospeptide score as described in the paper Beausoleil, S. A.; Villen,
 * J.; Gerber, S. A.; Rush, J.; Gygi, S. P. A probability-based approach for
 * high-throughput protein phosphorylation analysis and site localization Nat.
 * Biotechnol. 2006, 24, 1285-1292.
 * 
 * <p>
 * <b>Also used in calculate other PTMs score.
 * 
 * @author Xinning
 * @version 0.1, 06-12-2009, 10:05:09
 */
public class PTMPeptideScore implements IPTMPeptideScore {

	private static final int MIN_TOPN = 1;

	private static final int MAX_TOPN = 10;

	private static final int WINDOW = 100;

	/**
	 * For different topn, the weights (0 - 10)
	 */
	private static final double[] WEIGHTS = new double[] { 0, 0.5, 0.75, 1, 1, 1, 1, 0.75, 0.5, 0.25, 0.25 };

	private PTMScore[] ptmScore;
	private double[] weights;
	private int len;

	/**
	 * 
	 * <li>min_topN the min_topn_value for region filtering in 100 mz mass
	 * window (default >=1)
	 * <li>max_topN the max_topn_value for region filtering in 100 mz mass
	 * window (default <=10)
	 * <li>window the window for the filtering of peak list to retain the n top
	 * peaks within the window (default 100)
	 * 
	 * @param rawpeaklist
	 * @param charge
	 * @param threshold used for the m/z tolerance match
	 */
	public PTMPeptideScore(IMS2PeakList rawpeaklist, short charge, ISpectrumThreshold threshold) {
		this(rawpeaklist, charge, MIN_TOPN, MAX_TOPN, WINDOW, threshold);
	}

	/**
	 * 
	 * @param rawpeaklist
	 * @param charge
	 * @param min_topN the min_topn_value for region filtering in 100 mz mass
	 *            window (default >=1)
	 * @param max_topN the max_topn_value for region filtering in 100 mz mass
	 *            window (default <=10)
	 * @param window the window for the filtering of peak list to retain the n
	 *            top peaks within the window (default 100)
	 * @param threshold used for the m/z tolerance match
	 */
	public PTMPeptideScore(IMS2PeakList rawpeaklist, short charge, int min_topN, int max_topN, int window,
			ISpectrumThreshold threshold) {
		this.initialPTMScore(rawpeaklist, charge, threshold, min_topN, max_topN, window);
	}

	/**
	 * Initial the PTMScores
	 * 
	 * @param rawpeaklist
	 * @param charge
	 * @param threshold
	 * @param min
	 * @param max
	 */
	private void initialPTMScore(IMS2PeakList rawpeaklist, short charge, ISpectrumThreshold threshold, int min, int max,
			int window) {

		if (min < MIN_TOPN) {
			throw new IllegalArgumentException("The minimum top n is " + MIN_TOPN);
		}

		if (max > MAX_TOPN) {
			throw new IllegalArgumentException("The minimum top n is " + MAX_TOPN);
		}

		if (max < min) {
			throw new IllegalArgumentException("The max topN must bigger than min topN");
		}

		len = max - min + 1;

		this.weights = Arrays.copyOfRange(WEIGHTS, min, max + 1);
		this.ptmScore = new PTMScore[len];

		IMS2PeakList filtered = null;

		for (int i = len - 1; i >= 0; i--) {
			RegionTopNIntensityFilter filter = new RegionTopNIntensityFilter(min + i, window);

			/*
			 * Increase the speed. Only the first use the biggest top N
			 */
			if (filtered == null) {
				filtered = filter.filter(rawpeaklist);
			}

			this.ptmScore[i] = new PTMScore(filtered, charge, filter, threshold);

		}
	}

	@Override
	public double calculateScore(Ion[] ions) {

		double[] scores = this.calculateScores(ions);

		return this.getTotalScore(scores);
	}

	/**
	 * The weighted total score. The score array must the calculated from the
	 * same phosphopeptide score
	 * 
	 * @param scores
	 * @return
	 */
	public double getTotalScore(double[] scores) {
		double pscore = 0;
		for (int i = 0; i < len; i++) {
			pscore += scores[i] * this.weights[i];
		}

		return pscore;
	}

	/**
	 * Calculate the PTM scores for each of the topN filtering.
	 * <p>
	 * The scores are arranged by the top n value from big to small. If the top
	 * n value is not preset, the min is 1 and max is 10.
	 */
	public double[] calculateScores(Ion[] ions) {

		double[] scores = new double[len];

		for (int i = 0; i < len; i++) {
			PTMScore score = this.ptmScore[i];
			scores[i] = score.calculateScore(ions);
		}

		return scores;
	}

	/**
	 * Use the topN depth of topN peak filter to calculate the peptide score
	 * 
	 * @param ions
	 * @param topN the depth of the peak list filter
	 * @return
	 */
	public double calculateScore(Ion[] ions, int topN) {

		if (topN < MIN_TOPN || topN > MAX_TOPN) {
			throw new IllegalArgumentException(
					"The legal peak depth should within [" + MIN_TOPN + ", " + MAX_TOPN + "]");
		}

		PTMScore score = this.ptmScore[topN];
		return score.calculateScore(ions);
	}

}
