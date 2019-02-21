/* 
 ******************************************************************************
 * File: AscoreCalculator.java * * * Created on 2011-9-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.Ascore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation.PTMPeptideScore;
import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.SeqvsAscore;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.util.math.Combinator;

/**
 * @author ck
 *
 * @version 2011-9-20, 14:50:20
 */
public class AscoreCalculator {

	private static Logger logger = Logger.getLogger(AscoreCalculator.class.getName());
	private static final int MAX_SITE_PER_PEP = 6;
	private static final int MAX_NUM_COMBINATION = 5000;
	private static boolean isMono = true;
	private static final double EQUAL_TOL = 0.001;
	private Pattern site;

	public static AscoreCalculator phosCal = new AscoreCalculator("[STY]");

	public AscoreCalculator(String site) {
		this(Pattern.compile(site));
	}

	public AscoreCalculator(Pattern site) {
		this.site = site;
	}

	public SeqvsAscore compute(IModifiedPeptideSequence sequence_no_mod, short charge, IMS2PeakList peaksMS2,
			int siteNum, AminoacidFragment aafrage, int[] iontypes, ISpectrumThreshold threshold, char symbol) {

		if (siteNum > MAX_SITE_PER_PEP) {
			logger.info("Currently, only accept max " + MAX_SITE_PER_PEP + " modification site per peptide, skip.");
			return null;
		}

		String[] pseqs = getPossibleSeq(sequence_no_mod.getSequence(), siteNum, symbol);

		if (pseqs == null || pseqs.length == 0) {
			return null;
		}

		int len = pseqs.length;

		PTMPeptideScore pscore2 = new PTMPeptideScore(peaksMS2, charge, threshold);

		SeqvsScore[] svss = new SeqvsScore[len];
		for (int i = 0; i < len; i++) {

			StringBuilder sb = new StringBuilder();
			sb.append(sequence_no_mod.getPreviousAA()).append('.').append(pseqs[i]).append('.')
					.append(sequence_no_mod.getNextAA());

			IModifiedPeptideSequence pseq = ModifiedPeptideSequence.parseSequence(sb.toString());

			double[] scores2 = getScore(pseq, aafrage, iontypes, pscore2);
			double tscore = pscore2.getTotalScore(scores2);
			svss[i] = new SeqvsScore(pseq, tscore, scores2);
		}

		Arrays.sort(svss);

		// The most probable sequence
		SeqvsScore svs = svss[0];

		/*
		 * Only one possible sequence for phosphorylation sites
		 */
		if (len == 1) {
			return new SeqvsAscore(svs.getSeq(), svs.getTotalScore(), new double[] { 1000 });
		}

		AScore aScore = new AScore(pscore2);

		double[] ascores = getAScores(svss, siteNum, aafrage, iontypes, isMono, aScore);

		return new SeqvsAscore(svs.getSeq(), svs.getTotalScore(), ascores);
	}

	private double getAScore(IModifiedPeptideSequence seq1, IModifiedPeptideSequence seq2, double[] scores1,
			double[] scores2, AminoacidFragment aafrage, int[] types, boolean isMono, AScore aScore) {

		double max_gap = 0;
		int depth = 4;

		int len = scores1.length;
		for (int i = 1; i < len; i++) {
			double gap = scores1[i] - scores2[i];

			if (gap > max_gap) {
				max_gap = gap;
				depth = i;
			}
		}

		Ions ions1 = aafrage.fragment(seq1, types, isMono);
		Ions ions2 = aafrage.fragment(seq2, types, isMono);

		ArrayList<Ion> siteDeterminIons1 = new ArrayList<Ion>();
		ArrayList<Ion> siteDeterminIons2 = new ArrayList<Ion>();

		for (int type : types) {
			Ion[] ins1 = ions1.getIons(type);
			Ion[] ins2 = ions2.getIons(type);

			len = ins1.length;

			for (int i = 0; i < len; i++) {
				double mz1 = ins1[i].getMz();
				double mz2 = ins2[i].getMz();

				if (Math.abs(mz1 - mz2) > EQUAL_TOL) {
					siteDeterminIons1.add(ins1[i]);
					siteDeterminIons2.add(ins2[i]);
				}
			}
		}

		return aScore.calculate(siteDeterminIons1.toArray(new Ion[siteDeterminIons1.size()]),
				siteDeterminIons2.toArray(new Ion[siteDeterminIons1.size()]), depth);
	}

	private double[] getAScores(SeqvsScore[] sortedSvss, int siteNum, AminoacidFragment aafrage, int[] types,
			boolean isMono, AScore aScore) {

		if (siteNum == 1) {

			double ascore;

			ascore = getAScore(sortedSvss[0].getSeq(), sortedSvss[1].getSeq(), sortedSvss[0].getScores(),
					sortedSvss[1].getScores(), aafrage, types, isMono, aScore);

			return new double[] { ascore };

		} else {

			int num = sortedSvss.length;
			SeqvsScore top = sortedSvss[0];
			IModifiedPeptideSequence modseq = top.getSeq();
			IModifSite[] modssites = modseq.getModifications();
			
			double[] ascores = new double[siteNum];

			for (int i = 0; i < siteNum; i++) {
				// other site except the test site
				HashSet<Integer> otherSites = new HashSet<Integer>();

				for (int j = 0; j < siteNum; j++) {
					if (j != i) {
						otherSites.add(modssites[j].modifLocation());
					}
				}

				/*
				 * Determine the phosphorylation peptide with only one different
				 * phosphorylation site.
				 */
				SeqvsScore amb = null;
				L2: for (int j = 1; j < num; j++) {

					IModifiedPeptideSequence tmod = sortedSvss[j].getSeq();
					IModifSite[] tsites = tmod.getModifications();

					int difference = 0;
					for (IModifSite tsite : tsites) {
						if (!otherSites.contains(tsite.modifLocation())) {
							// not the same phosphopeptide with only one different phosphorylation site
							if (difference == 1)
								continue L2;
							else
								difference++;
						}
					}

					amb = sortedSvss[j];
					break;
				}

				if (amb == null) {
					Arrays.fill(ascores, 0);
					return ascores;
				}

				ascores[i] = getAScore(modseq, amb.getSeq(), top.getScores(), amb.getScores(), aafrage, types, isMono,
						aScore);
			}

			return ascores;
		}
	}

	private double[] getScore(IModifiedPeptideSequence seq, AminoacidFragment aafrage, int[] iontypes,
			PTMPeptideScore pscore) {
		Ions ions = aafrage.fragment(seq, iontypes, isMono);
		Ion[] tions = ions.getTotalIons();
		return pscore.calculateScores(tions);
	}

	private String[] getPossibleSeq(String sequence, int phosSiteNum, char phossym) {
		Matcher matcher = site.matcher(sequence);

		// all potential sites, 1-based
		ArrayList<Integer> list = new ArrayList<Integer>(6);
		int st = 0;
		while (matcher.find(st)) {
			int end = matcher.end();
			list.add(new Integer(end));
			st = end;
		}

		// number of potential sites
		int psnum = list.size();
		Integer[] psites = list.toArray(new Integer[psnum]);

		String[] rstrings;
		if (psnum < phosSiteNum) {
			logger.info("The sequence: " + sequence + " has " + psnum
					+ " probable site(s), which is less than expected: " + phosSiteNum);
			rstrings = null;
		} else if (psnum == phosSiteNum) {
			rstrings = new String[] { getModSequence(sequence, psites, phossym) };
		} else {
			// If only one phosphorylation site, the Y aa must not be the phosphorylated site.
			if (phosSiteNum == 1) {
				ArrayList<String> rlist = new ArrayList<String>();
				for (int i = 0; i < psnum; i++) {
					// int s = psites[i].intValue();
					// if(sequence.charAt(s-1)!='Y'){
					rlist.add(getModSequence(sequence, new Integer[] { psites[i] }, phossym));
					// }
				}
				rstrings = rlist.toArray(new String[0]);
			} else {

				Object[][] combines = Combinator.getCombination(psites, phosSiteNum);

				/**
				 * Too many combinations
				 */
				if (combines.length > MAX_NUM_COMBINATION) {
					logger.info("Sequence \"" + sequence + "\" is with too many potentials, skip!");
					return null;
				}

				int len = combines.length;
				rstrings = new String[len];

				for (int i = 0; i < len; i++) {
					Object[] tobj = combines[i];
					int l = tobj.length;
					Integer[] tints = new Integer[l];
					for (int j = 0; j < l; j++) {
						tints[j] = (Integer) tobj[j];
					}
					rstrings[i] = getModSequence(sequence, tints, phossym);
				}
			}

		}

		return rstrings;
	}

	private String getModSequence(String sequence, Integer[] sites, char symbol) {

		int psnum = sites.length;
		StringBuilder sb = new StringBuilder(sequence.length() + psnum);
		int start = 0;
		for (int i = 0; i < psnum; i++) {
			int p = sites[i].intValue();
			sb.append(sequence.substring(start, p));
			sb.append(symbol);
			start = p;
		}

		if (start != sequence.length())
			sb.append(sequence.substring(start));

		return sb.toString();
	}

	private class SeqvsScore implements Comparable<SeqvsScore> {

		private IModifiedPeptideSequence phosseq, neuseq;
		private double tscore;
		private double[] scores1;
		private double[] scores2;
		private int NLSite = -1;

		/**
		 * @param seq
		 * @param score
		 */
		private SeqvsScore(IModifiedPeptideSequence phosseq, double tscore, double[] scores) {
			this.phosseq = phosseq;
			this.scores1 = scores;
			this.tscore = tscore;
		}

		/**
		 * @param seq
		 * @param score
		 */
		private SeqvsScore(IModifiedPeptideSequence phosseq, IModifiedPeptideSequence neuseq, double tscore,
				double[] scores1, double[] scores2, int NLSite) {

			this.phosseq = phosseq;
			this.neuseq = neuseq;
			this.scores1 = scores1;
			this.scores2 = scores2;
			this.tscore = tscore;
			this.NLSite = NLSite;
		}

		/**
		 * @return the sequence
		 */
		public IModifiedPeptideSequence getSeq() {
			return this.phosseq;
		}

		/**
		 * If loss sequence is Null (for MS2 only spectrum), return null.
		 * 
		 * @return
		 */
		public IModifiedPeptideSequence getLossSeq() {
			return this.neuseq;
		}

		/**
		 * @return the score of this peptide
		 */
		public double[] getScores() {
			return scores1;
		}

		/**
		 * Scores2 for MS3
		 * 
		 * @return
		 */
		public double[] getScores2() {
			return this.scores2;
		}

		/**
		 * The weighted total score
		 * 
		 * @return
		 */
		public double getTotalScore() {
			return this.tscore;
		}

		/**
		 * The neutral loss site. If the Neutral loss site is not assigned, this
		 * value will be -1.
		 * 
		 * @return
		 */
		public int getNLSite() {
			return this.NLSite;
		}

		/**
		 * Sort by the score, from big to small.
		 * 
		 */
		@Override
		public int compareTo(SeqvsScore o) {

			double score2 = o.tscore;

			if (tscore > score2)
				return -1;

			return tscore == score2 ? 0 : 1;
		}
	}

}
