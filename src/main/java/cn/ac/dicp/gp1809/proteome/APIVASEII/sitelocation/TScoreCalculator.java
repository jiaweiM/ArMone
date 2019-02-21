/*
 ******************************************************************************
 * File: TScore.java * * * Created on 07-11-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IPhosphoSite;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.RegionTopNIntensityFilter;
import cn.ac.dicp.gp1809.proteome.spectrum.util.SpectrumUtil;
import cn.ac.dicp.gp1809.util.math.Combinator;

/**
 * The combined PTM score for a phosphosite location from MS2 and MS3.
 * 
 * <p>
 * Changes:
 * <li>0.2 remove the limitation of pY. The pY neutral loss is also considered
 * if its score is bigger than pS or pT
 * <li>0.3, 02-23-2009: More reasonable instances.
 * <li>0.3.1, 03-02-2009: Use TScores to maintain the sites and their TScore
 * <li>0.3.2, 04-23-2009: the symbol of neutral loss will not be label in final
 * sequence
 * 
 * 
 * @author Xinning
 * @version 0.3.2, 04-23-2009, 21:32:15
 */
public class TScoreCalculator {

	private static Pattern site = Pattern.compile("[STY]");

	private static boolean isMono = true;

	private static int[] types = new int[] { Ion.TYPE_B, Ion.TYPE_Y };

	// public static double tolerance = 0.5d;

	private TScoreCalculator() {
	}

	/**
	 * Calculate the most probable site localizations for a sequence. In some
	 * special conditions, such as the number of sites which may have possible
	 * phosphorylations, null will be returned.
	 * 
	 * <p>
	 * e.g. for Mascot X is also considered to may be with phosphorylation
	 * modifications.
	 * 
	 * @param sequence_no_phos
	 *            unique sequence without phosphorylation modif (but with other
	 *            modifications)
	 * @param peaksMS2
	 *            peaklist for MS2 spectrum.
	 * @param peaksMS3
	 *            peaklist for MS3 spectrum
	 * @param neuSiteNum
	 *            number of phosphorylation site.
	 * @param phossym
	 *            symbol indicating modification of phosphoric acid.
	 * @param neusym
	 *            symbol indicating a loss of phosporic acid.
	 */
	public static SeqvsTscore compute(
	        IModifiedPeptideSequence sequence_no_phos, short charge,
	        IMS2PeakList peaksMS2, IMS2PeakList peaksMS3, int neuSiteNum,
	        AminoacidFragment aafrage, ISpectrumThreshold threshold,
	        char phossym, char neusym) {
		String[] pseqs = getPossibleSeq(sequence_no_phos.getSequence(),
		        neuSiteNum, phossym);
		if (pseqs == null) {
			System.out.println("Null possible sequence. Set tscore as 0!");
			return null;
		}

		RegionTopNIntensityFilter filter = new RegionTopNIntensityFilter(4, 100);
		// Remove the neutral loss peak as possible.
		IPTMPeptideScore pscore2 = new PTMScore(SpectrumUtil
		        .getPeakListNoNLPeak(peaksMS2, neuSiteNum, 98d, 1d), charge,
		        filter, threshold);

		IPTMPeptideScore pscore3 = new PTMScore(SpectrumUtil
		        .getPeakListNoNLPeak(peaksMS3, neuSiteNum - 1, 98d, 1d),
		        charge, filter, threshold);

		SeqvsTscore stc = new SeqvsTscore(sequence_no_phos, neuSiteNum, phossym);

		for (int i = 0; i < pseqs.length; i++) {
			String pseq = pseqs[i];
			double score2 = getScore(pseq, aafrage, pscore2);

			/*
			 * when there are more than one neutral site, more than one neutral
			 * lost sequence may occurred. Only select the neutral loss sequence
			 * with the lowest random probability (biggest score) as the
			 * sequence.
			 * 
			 * AAAS#AAAS#AA -> AAAS@AAAS#AA or AAAS#AAAS@AA.
			 */
			int st = -1;
			double score3 = -100;// voluntariness small value.
			// String seq3 = null;
			while ((st = pseq.indexOf(phossym, st + 1)) != -1) {
				StringBuilder sb = new StringBuilder(pseq);
				sb.setCharAt(st, neusym);
				String tseq3 = sb.toString();
				double score = getScore(tseq3, aafrage, pscore3);

				if (score > score3) {
					score3 = score;
					// seq3 = tseq3;
				}
			}

			stc.add(pseq, score2 + score3);
		}

		//The most probable sequence 
		IPhosphoPeptideSequence seqsite = stc.getSequence();

		/*
		 * The site on which neutral loss occurs. The neutral loss site will not
		 * be renewed now
		 */
		int NLSite;
		if (seqsite.getPhosphorylationNumber() == 1) {
			IPhosphoSite site = seqsite.getPhosphorylations()[0];

			//			site.setSymbol(neusym);
			site.setNeutralLoss(true);
			NLSite = site.modifLocation();

			seqsite.renewModifiedSequence();

		} else {
			NLSite = parseNeutralLoss(seqsite, aafrage, pscore3, phossym,
			        neusym);
		}

		stc.setNLSite(NLSite);

		return stc;
	}

	/**
	 * After computing of the phosphorylation site, the site where the neutral
	 * loss occurred in MS3 spectrum must be determined.
	 * 
	 * @param pseq
	 *            sequence with phosphorylation site (from SeqvsTscore)
	 * @return the most probable site where the NL occurred in MS3 AAApAAAA will
	 *         return 4. start from 1;
	 */
	private static int parseNeutralLoss(IPhosphoPeptideSequence pseq,
	        AminoacidFragment aaf, IPTMPeptideScore pscore3, char phossym,
	        char neusym) {

		IPhosphoSite[] psites = pseq.getPhosphorylations();

		/*
		 * Only on neutral lost site
		 */
		double score3 = -100;
		IPhosphoSite most_prob_site = null;
		for (IPhosphoSite site : psites) {
			site.setSymbol(neusym);

			pseq.renewModifiedSequence();
			double score = getScore(pseq.getSequence(), aaf, pscore3);
			if (score > score3) {
				score3 = score;
				most_prob_site = site;
			}
			//reset for next site
			site.setSymbol(phossym);
		}

		most_prob_site.setSymbol(phossym);
		most_prob_site.setNeutralLoss(true);

		pseq.renewModifiedSequence();

		//Set the symbol in sequence as neutral symbol
		//		most_prob_site.setSymbol(neusym);

		return most_prob_site.modifLocation();
	}

	/**
	 * The probability of match for this sequence. <b>The lower the probability,
	 * the the less this match tend to be random. In other word, the lower, the
	 * better.</b>
	 * 
	 * 
	 * @param seq
	 * @param aafrage
	 * @param pscore
	 * @return
	 */
	private static double getScore(String seq, AminoacidFragment aafrage,
	        IPTMPeptideScore pscore) {
		Ions ions = aafrage.fragment(seq, types, isMono);
		Ion[] tions = ions.getTotalIons();
		return pscore.calculateScore(tions);
	}

	/**
	 * Generate the possible combination(s) of the neutral modification site.
	 * This sequence can be seen as the MS2 sequence whose phosphorylation
	 * modification has not lost. AAAAS#AAAAA but not AAAAS@AAAAA;
	 * 
	 * @param sequence
	 *            sequence without phosphorylation modification (but with other
	 *            modification)
	 * @param phosSiteNum
	 *            the number of phosphorylated sites
	 * @param phossym
	 *            the symbol of phosphorylation modification (not dehydate
	 *            modification)
	 * @return
	 */
	private static String[] getPossibleSeq(String sequence, int phosSiteNum,
	        char phossym) {
		Matcher matcher = site.matcher(sequence);
		ArrayList<Integer> list = new ArrayList<Integer>(6);
		int st = 0;
		while (matcher.find(st)) {
			int end = matcher.end();
			list.add(new Integer(end));
			st = end;
		}
		int psnum = list.size();
		Integer[] psites = list.toArray(new Integer[psnum]);

		String[] rstrings;
		if (psnum < phosSiteNum) {
			System.out.println("The sequence: " + sequence + " has " + psnum
			        + " probable site(s), which is less than expected: "
			        + phosSiteNum);
			rstrings = null;
		} else if (psnum == phosSiteNum) {
			rstrings = new String[] { getNeuSequence(sequence, psites, phossym) };
		} else {
			// If only one phosphorylation site, the Y aa must not be the
			// phosphorylated site.
			if (phosSiteNum == 1) {
				ArrayList<String> rlist = new ArrayList<String>();
				for (int i = 0; i < psnum; i++) {
					// int s = psites[i].intValue();
					// if(sequence.charAt(s-1)!='Y'){
					rlist.add(getNeuSequence(sequence,
					        new Integer[] { psites[i] }, phossym));
					// }
				}
				rstrings = rlist.toArray(new String[0]);
			} else {
				Object[][] combines = Combinator.getCombination(psites,
				        phosSiteNum);
				int len = combines.length;
				rstrings = new String[len];

				for (int i = 0; i < len; i++) {
					Object[] tobj = combines[i];
					int l = tobj.length;
					Integer[] tints = new Integer[l];
					for (int j = 0; j < l; j++) {
						tints[j] = (Integer) tobj[j];
					}
					rstrings[i] = getNeuSequence(sequence, tints, phossym);
				}
			}

		}

		return rstrings;
	}

	/**
	 * Assign neutral symbol to the specific site(s) in sites array.
	 */
	private static String getNeuSequence(String sequence, Integer[] sites,
	        char phossym) {
		int psnum = sites.length;
		StringBuilder sb = new StringBuilder(sequence.length() + psnum);
		int start = 0;
		for (int i = 0; i < psnum; i++) {
			int p = sites[i].intValue();
			sb.append(sequence.substring(start, p));
			sb.append(phossym);
			start = p;
		}

		if (start != sequence.length())
			sb.append(sequence.substring(start));

		return sb.toString();
	}
}
