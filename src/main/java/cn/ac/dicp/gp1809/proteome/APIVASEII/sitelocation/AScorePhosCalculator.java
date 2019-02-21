/*
 ******************************************************************************
 * File: AScoreCalculator.java * * * Created on 06-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.IO.PTM.Ascore.AScore;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IPhosphoSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.SeqvsAscore;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.PhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.util.SpectrumUtil;
import cn.ac.dicp.gp1809.util.math.Combinator;

/**
 * The Combined Peptide score and Ascore calculator
 * 
 * @author Xinning
 * @version 0.1.3.1, 09-29-2009, 14:53:30
 */
public class AScorePhosCalculator {

	private static Logger logger = Logger.getLogger(AScorePhosCalculator.class.getName());

	/**
	 * If two values than this value, they equal to each other
	 */
	private static final double EQUAL_TOL = 0.001;

	/**
	 * Currently, only accept the phosphopeptide with less than 6
	 * phosphorylation site
	 */
	private static final int MAX_PHOS_SITE_PER_PEP = 6;

	/**
	 * The max number of combination while generate the potential
	 * phosphorylation sites.
	 */
	private static final int MAX_NUM_COMBINATION = 5000;

	private static Pattern site = Pattern.compile("[STY]");

	private static boolean isMono = true;

	private static int[] types = new int[] { Ion.TYPE_B, Ion.TYPE_Y };

	private AScorePhosCalculator() {}

	/**
	 * <b>For MS2/MS3 peptides</b>
	 * <p/>
	 * 
	 * Calculate the most probable site localizations for a sequence. In some
	 * special conditions, such as the number of sites which may have possible
	 * phosphorylations, null will be returned.
	 * <p>
	 * The types of ions are b&y
	 * 
	 * <p>
	 * e.g. for Mascot X is also considered to may be with phosphorylation
	 * modifications.
	 * 
	 * @param sequence_no_phos unique sequence without phosphorylation modif
	 *            (but with other modifications)
	 * @param peaksMS2 peaklist for MS2 spectrum.
	 * @param peaksMS3 peaklist for MS3 spectrum
	 * @param neuSiteNum number of phosphorylation site.
	 * @param phossym symbol indicating modification of phosphoric acid.
	 * @param neusym symbol indicating a loss of phosporic acid.
	 */
	public static SeqvsAscore compute(IModifiedPeptideSequence sequence_no_phos, short charge, IMS2PeakList peaksMS2,
			IMS2PeakList peaksMS3, int phosSiteNum, AminoacidFragment aafrage, ISpectrumThreshold threshold,
			char phossym, char neusym) {

		return compute(sequence_no_phos, charge, peaksMS2, peaksMS3, phosSiteNum, aafrage, types, threshold, phossym,
				neusym);
	}

	/**
	 * <b>For MS2/MS3 peptides</b>
	 * <p/>
	 * 
	 * Calculate the most probable site localizations for a sequence. In some
	 * special conditions, such as the number of sites which may have possible
	 * phosphorylations, null will be returned.
	 * 
	 * <p>
	 * e.g. for Mascot X is also considered to may be with phosphorylation
	 * modifications.
	 * 
	 * @param sequence_no_phos unique sequence without phosphorylation modif (but with other modifications)
	 * @param peaksMS2 peaklist for MS2 spectrum.
	 * @param peaksMS3 peaklist for MS3 spectrum
	 * @param neuSiteNum number of phosphorylation site.
	 * @param phossym symbol indicating modification of phosphoric acid.
	 * @param neusym symbol indicating a loss of phosporic acid.
	 */
	public static SeqvsAscore compute(IModifiedPeptideSequence sequence_no_phos, short charge, IMS2PeakList peaksMS2,
			IMS2PeakList peaksMS3, int phosSiteNum, AminoacidFragment aafrage, int[] iontypes,
			ISpectrumThreshold threshold, char phossym, char neusym) {

		if (phosSiteNum > MAX_PHOS_SITE_PER_PEP) {
			logger.info("Currently, only accept max " + MAX_PHOS_SITE_PER_PEP + " phosphorylation site per peptide, skip.");
			return null;
		}

		// possible peptide sequences
		String[] pseqs = getPossibleSeq(sequence_no_phos.getSequence(), phosSiteNum, phossym);
		if (pseqs == null || pseqs.length == 0) {
			// logger.info("Null possible sequence or illegal sequence:
			// \""+sequence_no_phos+"\".");
			return null;
		}

		int len = pseqs.length;

		// Remove the neutral loss peak as possible.
		PTMPeptideScore pscore2 = new PTMPeptideScore(SpectrumUtil.getPeakListNoNLPeak(peaksMS2, phosSiteNum, 98d, 1d),
				charge, threshold);

		// Remove the neutral loss peak as possible.
		PTMPeptideScore pscore3 = new PTMPeptideScore(
				SpectrumUtil.getPeakListNoNLPeak(peaksMS3, phosSiteNum - 1, 98d, 1d), charge, threshold);

		SeqvsScore[] svss = new SeqvsScore[len];
		for (int i = 0; i < len; i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(sequence_no_phos.getPreviousAA()).append('.').append(pseqs[i]).append('.')
					.append(sequence_no_phos.getNextAA());
			PhosphoPeptideSequence pseq = PhosphoPeptideSequence.parseSequence(sb.toString(), phossym, (char) 0);

			double[] scores2 = getScore(pseq, aafrage, iontypes, pscore2);
			double score2 = pscore2.getTotalScore(scores2);
			/*
			 * when there are more than one neutral site, more than one neutral
			 * lost sequence may occurred. Only select the neutral loss sequence
			 * with the lowest random probability (biggest score) as the
			 * sequence.
			 * 
			 * AAAS#AAAS#AA -> AAAS@AAAS#AA or AAAS#AAAS@AA.
			 */
			IPhosphoPeptideSequence neuSeq = pseq.deepClone();
			IPhosphoSite[] sites = neuSeq.getPhosphorylations();
			double score3 = -100;// voluntariness small value.
			IPhosphoPeptideSequence seq3 = null;
			int NLSite = -1;
			double[] scores3 = null;
			for (IPhosphoSite site : sites) {
				// Y can not lose the phosphate
				// if (site.modifiedAA() != 'Y') {
				site.setSymbol(neusym);
				site.setNeutralLoss(true);

				neuSeq.renewModifiedSequence();
				double[] tscores = getScore(neuSeq, aafrage, iontypes, pscore3);
				double score = pscore3.getTotalScore(tscores);

				if (score > score3) {
					score3 = score;
					scores3 = tscores;
					seq3 = neuSeq.deepClone();
					NLSite = site.modifLocation();
				}

				// Set back
				site.setSymbol(phossym);
				site.setNeutralLoss(false);
				// }
			}

			if (seq3 == null) {
				logger.info("No possible neutral loss site for \"" + pseq.getFormattedSequence() + "\", skip.");
				return null;
			}

			svss[i] = new SeqvsScore(pseq, seq3, score2 + score3, scores2, scores3, NLSite);
		}

		Arrays.sort(svss);

		// The most probable sequence
		SeqvsScore svs = svss[0];

		/*
		 * Only one possible sequence for phosphorylation sites
		 */
		if (len == 1) {
			SeqvsAscore sva = new SeqvsAscore(svs.getSeq(), svs.getTotalScore(), new double[] { 1000 },
					new double[] { 1000 });
			sva.setNLSite(svs.getNLSite());
			return sva;
		}

		AScore aScore2 = new AScore(pscore2);
		/*
		 * double ascore2 = getAScore(svs.getSeq(), svss[1].getSeq(), svs
		 * .getScores(), svss[1].getScores(), aafrage, types, isMono, aScore2);
		 */

		double[] ascores2 = getAScores(svss, phosSiteNum, aafrage, iontypes, isMono, aScore2, false);

		AScore aScore3 = new AScore(pscore3);
		/*
		 * double ascore3 = getAScore(svs.getLossSeq(), svss[1].getLossSeq(),
		 * svs .getScores2(), svss[1].getScores2(), aafrage, types, isMono,
		 * aScore3);
		 */
		double[] ascores3 = getAScores(svss, phosSiteNum, aafrage, iontypes, isMono, aScore3, true);

		SeqvsAscore sva = new SeqvsAscore(svs.getSeq(), svs.getTotalScore(), ascores2, ascores3);
		sva.setNLSite(svs.getNLSite());

		return sva;
	}

	/**
	 * <b>For MS2 peptides</b>
	 * <p/>
	 * 
	 * Calculate the most probable site localizations for a sequence. In some
	 * special conditions (bug of some database search algorithm?), such as the
	 * number of sites which may not have possible phosphorylations, null will
	 * be returned.
	 * <p>
	 * The types of ions are b&y
	 * <p>
	 * e.g. for Mascot X is also considered to may be with phosphorylation
	 * modifications.
	 * 
	 * @param sequence_no_phos unique sequence without phosphorylation modif
	 *            (but with other modifications)
	 * @param peaksMS2 peaklist for MS2 spectrum.
	 * @param neuSiteNum number of phosphorylation site.
	 * @param phossym symbol indicating modification of phosphoric acid.
	 */
	public static SeqvsAscore compute(IModifiedPeptideSequence sequence_no_phos, short charge, IMS2PeakList peaksMS2,
			int phosSiteNum, AminoacidFragment aafrage, ISpectrumThreshold threshold, char phossym) {
		return compute(sequence_no_phos, charge, peaksMS2, phosSiteNum, aafrage, types, threshold, phossym);
	}

	/**
	 * <b>For MS2 peptides</b>
	 * <p/>
	 * Calculate the most probable site localizations for a sequence. In some
	 * special conditions (bug of some database search algorithm?), such as the
	 * number of sites which may not have possible phosphorylations, null will
	 * be returned.
	 * 
	 * <p>
	 * e.g. for Mascot X is also considered to may be with phosphorylation
	 * modifications.
	 * 
	 * @param sequence_no_phos unique sequence without phosphorylation modif
	 *            (but with other modifications)
	 * @param peaksMS2 peaklist for MS2 spectrum.
	 * @param neuSiteNum number of phosphorylation site.
	 * @param phossym symbol indicating modification of phosphoric acid.
	 */
	public static SeqvsAscore compute(IModifiedPeptideSequence sequence_no_phos, short charge, IMS2PeakList peaksMS2,
			int phosSiteNum, AminoacidFragment aafrage, int[] iontypes, ISpectrumThreshold threshold, char phossym) {
		if (phosSiteNum > MAX_PHOS_SITE_PER_PEP) {
			logger.info("Currently, only accept max " + MAX_PHOS_SITE_PER_PEP + " phosphorylation site per peptide, skip.");
			return null;
		}

		String[] pseqs = getPossibleSeq(sequence_no_phos.getSequence(), phosSiteNum, phossym);
		if (pseqs == null || pseqs.length == 0) {
			return null;
		}

		// Remove the neutral loss peak as possible.
		PTMPeptideScore pscore2 = new PTMPeptideScore(SpectrumUtil.getPeakListNoNLPeak(peaksMS2, phosSiteNum, 98d, 1d),
				charge, threshold);

		int len = pseqs.length;
		// score for all possible sequences
		SeqvsScore[] svss = new SeqvsScore[len];
		for (int i = 0; i < len; i++) {

			StringBuilder sb = new StringBuilder();
			sb.append(sequence_no_phos.getPreviousAA()).append('.').append(pseqs[i]).append('.')
					.append(sequence_no_phos.getNextAA());

			IPhosphoPeptideSequence pseq = PhosphoPeptideSequence.parseSequence(sb.toString(), phossym, (char) 0);
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
		double[] ascores = getAScores(svss, phosSiteNum, aafrage, iontypes, isMono, aScore, false);

		return new SeqvsAscore(svs.getSeq(), svs.getTotalScore(), ascores);
	}

	/**
	 * <b>For MS3 peptides</b>
	 * <p/>
	 * Calculate the most probable site localizations for a sequence. In some
	 * special conditions (bug of some database search algorithm?), such as the
	 * number of sites which may not have possible phosphorylations, null will
	 * be returned.
	 * 
	 * <p/>
	 * e.g. for Mascot X is also considered to may be with phosphorylation
	 * modifications.
	 * 
	 * @param sequence_no_phos unique sequence without phosphorylation modif
	 *            (but with other modifications)
	 * @param peaksMS2 peaklist for MS2 spectrum.
	 * @param phosSiteNum number of phosphorylation sites
	 * @param neuSiteNum number of phosphorylation sites which have lost the
	 *            phosphate
	 * @param phossym symbol indicating modification of phosphoric acid.
	 */
	public static SeqvsAscore compute(IModifiedPeptideSequence sequence_no_phos, short charge, IMS2PeakList peaksMS3,
			int phosSiteNum, int neuSiteNum, AminoacidFragment aafrage, ISpectrumThreshold threshold, char phossym,
			char neusym) {
		return compute(sequence_no_phos, charge, peaksMS3, phosSiteNum, neuSiteNum, aafrage, types, threshold, phossym,
				neusym);
	}

	/**
	 * <b>For MS3 peptides</b>
	 * <p/>
	 * Calculate the most probable site localizations for a sequence. In some
	 * special conditions (bug of some database search algorithm?), such as the
	 * number of sites which may not have possible phosphorylations, null will
	 * be returned.
	 * 
	 * <p>
	 * e.g. for Mascot X is also considered to may be with phosphorylation
	 * modifications.
	 * 
	 * @param sequence_no_phos unique sequence without phosphorylation modif
	 *            (but with other modifications)
	 * @param peaksMS2 peaklist for MS2 spectrum.
	 * @param phosSiteNum number of phosphorylation sites
	 * @param neuSiteNum number of phosphorylation sites which have lost the
	 *            phosphate
	 * @param phossym symbol indicating modification of phosphoric acid.
	 */
	public static SeqvsAscore compute(IModifiedPeptideSequence sequence_no_phos, short charge, IMS2PeakList peaksMS3,
			int phosSiteNum, int neuSiteNum, AminoacidFragment aafrage, int[] iontypes, ISpectrumThreshold threshold,
			char phossym, char neusym) {

		if (phosSiteNum > MAX_PHOS_SITE_PER_PEP) {
			logger.info(
					"Currently, only accept max " + MAX_PHOS_SITE_PER_PEP + " phosphorylation site per peptide, skip.");
			return null;
		}

		String[] pseqs = getPossibleSeq(sequence_no_phos.getSequence(), phosSiteNum, phossym);
		if (pseqs == null || pseqs.length == 0) {
			return null;
		}

		int len = pseqs.length;

		// Remove the neutral loss peak as possible.
		PTMPeptideScore pscore3 = new PTMPeptideScore(
				SpectrumUtil.getPeakListNoNLPeak(peaksMS3, phosSiteNum - neuSiteNum, 98d, 1d), charge, threshold);

		SeqvsScore[] svss = new SeqvsScore[len];
		for (int i = 0; i < len; i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(sequence_no_phos.getPreviousAA()).append('.').append(pseqs[i]).append('.')
					.append(sequence_no_phos.getNextAA());
			
			PhosphoPeptideSequence pseq = PhosphoPeptideSequence.parseSequence(sb.toString(), phossym, (char) 0);

			/*
			 * when there are more than one neutral site, more than one neutral
			 * lost sequence may occurred. Only select the neutral loss sequence
			 * with the lowest random probability (biggest score) as the
			 * sequence.
			 * 
			 * AAAS#AAAS#AA -> AAAS@AAAS#AA or AAAS#AAAS@AA.
			 */

			IPhosphoPeptideSequence neuSeq = pseq.deepClone();
			IPhosphoSite[] sites = neuSeq.getPhosphorylations();
			double score3 = -100;// voluntariness small value.
			IPhosphoPeptideSequence seq3 = null;
			int NLSite = -1;
			double[] scores3 = null;

			Object[][] objss = Combinator.getCombination(sites, neuSiteNum);
			for (Object[] objs : objss) {

				for (Object obj : objs) {
					IPhosphoSite site = (IPhosphoSite) obj;

					// Y can not lose the phosphate
					// if (site.modifiedAA() != 'Y') {
					site.setSymbol(neusym);
					site.setNeutralLoss(true);
				}

				neuSeq.renewModifiedSequence();
				double[] tscores = getScore(neuSeq, aafrage, iontypes, pscore3);
				double score = pscore3.getTotalScore(tscores);

				if (score > score3) {
					score3 = score;
					scores3 = tscores;
					seq3 = neuSeq.deepClone();
				}

				for (Object obj : objs) {
					IPhosphoSite site = (IPhosphoSite) obj;

					// Set back
					site.setSymbol(phossym);
					site.setNeutralLoss(false);
				}
			}

			if (seq3 == null) {
				logger.info("No possible neutral loss site for \"" + pseq.getFormattedSequence() + "\", skip.");
				return null;
			}

			svss[i] = new SeqvsScore(pseq, seq3, score3, null, scores3, NLSite);
		}

		Arrays.sort(svss);

		// The most probable sequence
		SeqvsScore svs = svss[0];

		/*
		 * Only one possible sequence for phosphorylation sites
		 */
		if (len == 1) {
			SeqvsAscore sva = new SeqvsAscore(svs.getLossSeq(), svs.getTotalScore(), new double[] { 1000 });
			return sva;
		}

		AScore aScore3 = new AScore(pscore3);
		/*
		 * double ascore3 = getAScore(svs.getLossSeq(), svss[1].getLossSeq(),
		 * svs .getScores2(), svss[1].getScores2(), aafrage, types, isMono,
		 * aScore3);
		 */
		double[] ascores3 = getAScores(svss, phosSiteNum, aafrage, iontypes, isMono, aScore3, true);

		SeqvsAscore sva = new SeqvsAscore(svs.getLossSeq(), svs.getTotalScore(), ascores3);

		return sva;
	}

	/**
	 * The probability of match for this sequence of every peak depth. <b>The lower the probability,
	 * the the less this match tend to be random. In other word, the lower, the
	 * better.</b>
	 * 
	 * @param seq peptide sequence
	 * @param aafrage a {@link AminoAcidFragment} object.
	 * @param pscore
	 * @return
	 */
	private static double[] getScore(IModifiedPeptideSequence seq, AminoacidFragment aafrage, int[] iontypes,
			PTMPeptideScore pscore) {
		Ions ions = aafrage.fragment(seq, iontypes, isMono);
		Ion[] tions = ions.getTotalIons();
		return pscore.calculateScores(tions);
	}

	/**
	 * If the number of phosphorylation sites is larger than 2, more than one
	 * Ascore should be returned for each of the phosphorylation sites.
	 * 
	 * @return
	 */
	private static double[] getAScores(SeqvsScore[] sortedSvss, int siteNum, AminoacidFragment aafrage, int[] types,
			boolean isMono, AScore aScore, boolean isMS3) {

		if (siteNum == 1) {

			double ascore;
			if (isMS3) {
				ascore = getAScore(sortedSvss[0].getLossSeq(), sortedSvss[1].getLossSeq(), sortedSvss[0].getScores2(),
						sortedSvss[1].getScores2(), aafrage, types, isMono, aScore);
			} else {
				ascore = getAScore(sortedSvss[0].getSeq(), sortedSvss[1].getSeq(), sortedSvss[0].getScores(),
						sortedSvss[1].getScores(), aafrage, types, isMono, aScore);
			}

			return new double[] { ascore };
		} else {
			int num = sortedSvss.length;
			SeqvsScore top = sortedSvss[0];
			IPhosphoPeptideSequence phosseq = isMS3 ? top.getLossSeq() : top.getSeq();	// top peptide sequence
			IPhosphoSite[] phossites = phosseq.getPhosphorylations();
			double[] ascores = new double[siteNum];

			for (int i = 0; i < siteNum; i++) {
				HashSet<Integer> otherSites = new HashSet<Integer>();

				for (int j = 0; j < siteNum; j++) {
					if (j != i) {
						otherSites.add(phossites[j].modifLocation());
					}
				}

				/*
				 * Determine the phosphorylation peptide with only one different
				 * phosphorylation site.
				 */
				SeqvsScore amb = null;
				L2: for (int j = 1; j < num; j++) {

					// other peptide sequence
					IPhosphoPeptideSequence tphos = isMS3 ? sortedSvss[j].getLossSeq() : sortedSvss[j].getSeq();
					IPhosphoSite[] tsites = tphos.getPhosphorylations();

					int difference = 0;
					for (IPhosphoSite tsite : tsites) {
						if (otherSites.contains(tsite.modifLocation())) {

						} else {
							// not the same phosphopeptide with only one
							// different phosphorylation site
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
					throw new NullPointerException("No peptide sequence with only one different phosphosite.");
				}

				ascores[i] = getAScore(phosseq, isMS3 ? amb.getLossSeq() : amb.getSeq(),
						isMS3 ? top.getScores2() : top.getScores(), isMS3 ? amb.getScores2() : amb.getScores(), aafrage,
						types, isMono, aScore);
			}

			return ascores;
		}
	}

	/**
	 * get the ascore value of the top 2 peptides.
	 */
	private static double getAScore(IModifiedPeptideSequence seq1, IModifiedPeptideSequence seq2, double[] scores1,
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

		// get site determined ions.
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

	/**
	 * Generate the possible combination(s) of the neutral modification site.
	 * This sequence can be seen as the MS2 sequence whose phosphorylation
	 * modification has not lost. AAAAS#AAAAA but not AAAAS@AAAAA;
	 * 
	 * @param sequence sequence without phosphorylation modification (but with other modification)
	 * @param phosSiteNum the number of phosphorylated sites
	 * @param phossym the symbol of phosphorylation modification (not dehydate modification)
	 * @return
	 */
	private static String[] getPossibleSeq(String sequence, int phosSiteNum, char phossym) {
		Matcher matcher = site.matcher(sequence);
		ArrayList<Integer> list = new ArrayList<Integer>(6);
		int st = 0;
		while (matcher.find(st)) {
			int end = matcher.end();
			st = end;
			if (end < sequence.length()) {
				char aa = sequence.charAt(end);
				if (aa >= 'A' && aa <= 'Z') {
					list.add(end);
				}
			} else {
				list.add(end);
			}
		}
		int psnum = list.size();
		Integer[] psites = list.toArray(new Integer[psnum]);

		String[] rstrings;
		if (psnum < phosSiteNum) {
			logger.info("The sequence: " + sequence + " has " + psnum
					+ " probable site(s), which is less than expected: " + phosSiteNum);
			rstrings = null;
		} else if (psnum == phosSiteNum) {
			rstrings = new String[] { getPhosSequence(sequence, psites, phossym) };
		} else {
			// If only one phosphorylation site, the Y aa must not be the phosphorylated site.
			if (phosSiteNum == 1) {
				ArrayList<String> rlist = new ArrayList<String>();
				for (int i = 0; i < psnum; i++) {
					rlist.add(getPhosSequence(sequence, new Integer[] { psites[i] }, phossym));
				}
				rstrings = rlist.toArray(new String[0]);
			} else {

				Object[][] combines = Combinator.getCombination(psites, phosSiteNum);

				/**
				 * Tooo many combinations
				 */
				if (combines.length > MAX_NUM_COMBINATION) {
					logger.info("Sequence \"" + sequence + "\" is with tooo many potentials, skip!");
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
					rstrings[i] = getPhosSequence(sequence, tints, phossym);
				}
			}

		}

		return rstrings;
	}

	/**
	 * Assign neutral symbol to the specific site(s) in sites array.
	 */
	private static String getPhosSequence(String sequence, Integer[] sites, char phossym) {
		int psnum = sites.length;
		StringBuilder sb = new StringBuilder(sequence.length() + psnum);
		int start = 0;
		for (int i = 0; i < psnum; i++) {
			int p = sites[i];
			sb.append(sequence.substring(start, p));
			sb.append(phossym);
			start = p;
		}

		if (start != sequence.length())
			sb.append(sequence.substring(start));

		return sb.toString();
	}

	/**
	 * The sequence and its score (Peptide score for MS2 only peptide and Tscore
	 * for MS2/MS3)
	 * 
	 * 
	 * @author Xinning
	 * @version 0.1, 06-12-2009, 16:12:49
	 */
	private static class SeqvsScore implements Comparable<SeqvsScore> {

		private IPhosphoPeptideSequence phosseq, neuseq;
		private double tscore;
		private double[] scores1;
		private double[] scores2;
		private int NLSite = -1;

		/**
		 * @param seq
		 * @param score
		 */
		private SeqvsScore(IPhosphoPeptideSequence phosseq, double tscore, double[] scores) {
			this.phosseq = phosseq;
			this.scores1 = scores;
			this.tscore = tscore;
		}

		/**
		 * @param seq
		 * @param score
		 */
		private SeqvsScore(IPhosphoPeptideSequence phosseq, IPhosphoPeptideSequence neuseq, double tscore,
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
		public IPhosphoPeptideSequence getSeq() {
			return this.phosseq;
		}

		/**
		 * If loss sequence is Null (for MS2 only spectrum), return null.
		 * 
		 * @return
		 */
		public IPhosphoPeptideSequence getLossSeq() {
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
