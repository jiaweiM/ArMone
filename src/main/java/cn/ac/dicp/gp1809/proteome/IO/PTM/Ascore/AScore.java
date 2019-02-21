package cn.ac.dicp.gp1809.proteome.IO.PTM.Ascore;

import cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation.PTMPeptideScore;
import cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.IPTMSiteScore;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;

/**
 * The AScore as described in the paper of Beausoleil, S. A.; Villen, J.;
 * Gerber, S. A.; Rush, J.; Gygi, S. P. A probability-based approach for
 * high-throughput protein phosphorylation analysis and site localization Nat.
 * Biotechnol. 2006, 24, 1285-1292.
 * 
 * @author Xinning
 * @version 0.1.1, 09-28-2009, 20:28:33
 */
public class AScore implements IPTMSiteScore {

	private PTMPeptideScore peptideScore;

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
	public AScore(IMS2PeakList rawpeaklist, short charge, ISpectrumThreshold threshold) {
		this.peptideScore = new PTMPeptideScore(rawpeaklist, charge, threshold);
	}

	/**
	 * Construct a Ascore calculator from the peptide score
	 * 
	 * @param PTMPeptideScore
	 */
	public AScore(PTMPeptideScore peptideScore) {
		this.peptideScore = peptideScore;
	}

	/**
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
	public AScore(IMS2PeakList rawpeaklist, short charge, int min_topN, int max_topN, int window,
			ISpectrumThreshold threshold) {

		this.peptideScore = new PTMPeptideScore(rawpeaklist, charge, min_topN, max_topN, window, threshold);
	}

	/**
	 * Calculate the ambiguous score for the two
	 * 
	 * @param ions1 the site determine ions
	 * @param ions2 the site determine ions
	 * @return ascore
	 */
	public double calculate(Ion[] ions1, Ion[] ions2, int peakDepth) {
		
		double score1 = this.peptideScore.calculateScore(ions1, peakDepth);
		double score2 = this.peptideScore.calculateScore(ions2, peakDepth);

		double score = score1 - score2;

		/*
		 * make sure the score >=0;
		 */
		if (score < 0) {
			score = 0;
		}

		return score;
	}
}
