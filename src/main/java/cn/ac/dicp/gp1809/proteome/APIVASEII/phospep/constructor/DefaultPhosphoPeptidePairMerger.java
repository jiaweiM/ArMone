/* 
 ******************************************************************************
 * File: DefaultPhosphoPeptidePairMerger.java * * * Created on 02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.constructor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation.AScorePhosCalculator;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.PhosphoUtil;
import cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.SeqvsAscore;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.IPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.PhosConstants;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;

/**
 * The merger which merges the phosphopeptides list from ms2 and ms3 into
 * phosphopeptide pairs and only return the top matched phosphopeptide pair
 * 
 * <p>
 * Changes:
 * <li>0.1.1, 09-07-2009: use deep clone for the variable modification and
 * static modification
 * 
 * @author Xinning
 * @version 0.1.1, 09-07-2009, 21:52:13
 */
public class DefaultPhosphoPeptidePairMerger implements IPhosphoPeptideMerger {
	// The added mass for phosphate
	private static final double PHOS_ADD = PhosConstants.PHOS_ADD;

	// The added mass dehydrate (loss of phosphate)
	private static final double NEU_ADD = PhosConstants.NEU_ADD;

	/**
	 * The new defined symbols. These symbols related to the new
	 * AminoacidModification instance.
	 */
	private static final char phosSymbol = PhosConstants.PHOS_SYMBOL, neuSymbol = PhosConstants.NEU_SYMBOL;

	// The threshold to judge where they are the same modification
	private static final double THRES = 0.1;

	private AminoacidFragment aafrage_ms3;

	/**
	 * The old symbols for MS2 and MS3 which are generated from the
	 * AminoacidMosification
	 */
	private char oldPhosSymbolMS2, oldPhosSymbolMS3, oldNeutralSymbol;

	/**
	 * The new Aminoacid modification
	 */
	private AminoacidModification aamodif;

	/**
	 * The map for MS2 and MS3 symbol with the same meaning. Key = MS2 symbol
	 * Value = MS3 symbol
	 */
	private HashMap<Character, Character> oldSymbolMap;

	private IPhosPepPairCombiner combiner;

	private ISpectrumThreshold threshold;

	/**
	 * 
	 * @param aafrage_ms3 the search parameters of static and variable
	 *            modification should come from ms3 (with neutral loss
	 *            modification);
	 */
	public DefaultPhosphoPeptidePairMerger(Aminoacids aasms2, Aminoacids aasms3, AminoacidModification aamodifms2,
			AminoacidModification aamodifms3, ISpectrumThreshold threshold, PeptideType type) {
		this.parseModif(aasms2, aasms3, aamodifms2, aamodifms3);

		this.combiner = this.getCombiner(type, phosSymbol, neuSymbol);
		this.threshold = threshold;
	}

	/**
	 * Parse the modifications
	 * 
	 * @param aasms2
	 * @param aasms3
	 * @param aamodifms2
	 * @param aamodifms3
	 */
	private void parseModif(Aminoacids aasms2, Aminoacids aasms3, AminoacidModification aamodifms2,
			AminoacidModification aamodifms3) {

		/*
		 * First judge the equalization of two Aminoacids
		 */
		if (!aasms2.equals(aasms3)) {
			throw new IllegalArgumentException("The static modification for MS2 and MS3 must be the same.");
		}

		/*
		 * The AminoacidModification
		 */
		this.validateVaribleModif(aamodifms2, aamodifms3);

		/*
		 * Parse the old modification symbols
		 */
		HashSet<Modif> set = aamodifms2.getModifSymbols('S');
		if (set == null || set.size() == 0) {
			System.out.println(
					"The modifications specified " + "on STY were not found, try to use the globle modifications.");

			Modif[] modifs = aamodifms2.getModifications();

			if (modifs == null || modifs.length == 0)
				throw new IllegalArgumentException(
						"The phosphorylation on STY was not defined in the search parameter of MS2.");
			else {
				set = new HashSet<Modif>();
				for (Modif modif : modifs)
					set.add(modif);
			}
		}

		Modif[] symbols = set.toArray(new Modif[set.size()]);
		for (int i = 0; i < symbols.length; i++) {
			Modif mod = symbols[i];
			char sym = mod.getSymbol();
			double addmass = mod.getMass();

			if (Math.abs(addmass - PHOS_ADD) < THRES) {
				this.oldPhosSymbolMS2 = sym;
			}
		}

		set = aamodifms3.getModifSymbols('S');
		if (set == null || set.size() == 0) {
			System.out.println(
					"The modifications specified " + "on STY were not found, try to use the globle modifications.");

			Modif[] modifs = aamodifms3.getModifications();

			if (modifs == null || modifs.length == 0)
				throw new IllegalArgumentException(
						"The phosphorylation on STY was not defined in the search parameter of MS2.");
			else {
				set = new HashSet<Modif>();
				for (Modif modif : modifs)
					set.add(modif);
			}
		}

		symbols = set.toArray(new Modif[set.size()]);
		for (int i = 0; i < symbols.length; i++) {
			Modif mod = symbols[i];
			char sym = mod.getSymbol();
			double addmass = mod.getMass();

			if (Math.abs(addmass - PHOS_ADD) < THRES) {
				this.oldPhosSymbolMS3 = sym;
			} else if (Math.abs(addmass - NEU_ADD) < THRES) {
				this.oldNeutralSymbol = sym;
			}
		}

		if (this.oldPhosSymbolMS2 == 0)
			throw new IllegalArgumentException("Cannot find the phosphorylation modification in search parameter MS2.");

		if (this.oldPhosSymbolMS3 == 0)
			throw new IllegalArgumentException("Cannot find the phosphorylation modification in search parameter MS3.");

		if (this.oldNeutralSymbol == 0)
			throw new IllegalArgumentException(
					"Cannot find the neutral lost phosphorylation modification in search parameter of MS3.");

		/*
		 * new AminoacidModification and new symbols
		 */
		this.aamodif = aamodifms3.deepClone();
		this.aamodif.changeModifSymbol(this.oldPhosSymbolMS3, phosSymbol);
		this.aamodif.changeModifSymbol(this.oldNeutralSymbol, neuSymbol);

		this.aafrage_ms3 = new AminoacidFragment(aasms3.deepClone(), this.aamodif);
	}

	/**
	 * Validate the variable modifications. For MS2 and MS3 should be the same
	 * except the dehydrate for ST
	 * 
	 * @param aamodifms2
	 * @param aamodifms3
	 */
	private void validateVaribleModif(AminoacidModification aamodifms2, AminoacidModification aamodifms3) {
		Modif[] modsms3 = aamodifms3.getModifications();
		Modif[] modsms2 = aamodifms2.getModifications();

		if (modsms3.length != modsms2.length + 1)
			throw new IllegalArgumentException(
					"The varible mdofication for MS2 and MS3 should be " + "the same except the dehydrate on ST");

		Comparator<Modif> cp = new Comparator<Modif>() {

			@Override
			public int compare(Modif o1, Modif o2) {
				double m1 = o1.getMass();
				double m2 = o2.getMass();

				if (m1 == m2)
					return 0;

				return m1 > m2 ? 1 : -1;
			}
		};

		Arrays.sort(modsms2, cp);
		Arrays.sort(modsms3, cp);

		this.oldSymbolMap = new HashMap<Character, Character>();

		int mi = 0;
		for (int i = 0; i < modsms2.length; i++) {
			Modif m2 = modsms2[i];
			Modif m3 = modsms3[i + mi];

			double m2m = m2.getMass();
			double m3m = m3.getMass();

			if (Math.abs(m2m - m3m) > THRES) {
				// Only the dehydrate can be different
				if (Math.abs(m3.getMass() - NEU_ADD) > THRES) {
					throw new IllegalArgumentException("The varible mdofication for MS2 and MS3 should be "
							+ "the same except the dehydrate on ST");
				} else {
					mi = 1;
					i--;
				}
			} else {
				this.oldSymbolMap.put(m2.getSymbol(), m3.getSymbol());
			}
		}
	}

	/**
	 * Get the phosphopeptide pair combiner.
	 * 
	 * @param type
	 * @param newPhosSymbol
	 * @param newNeuSymbol
	 * @return
	 */
	private IPhosPepPairCombiner getCombiner(PeptideType type, char newPhosSymbol, char newNeuSymbol) {
		return PhosPairCombinerFacotry.construct(type, newPhosSymbol, newNeuSymbol);
	}

	/**
	 * Get the AminoacidModification with new phosphorylation and neutral loss
	 * symbols.
	 * 
	 * @return
	 */
	public AminoacidModification getModificationNewSymbol() {
		return this.aamodif;
	}

	/**
	 * The aminoacids instance.
	 * 
	 * @return
	 */
	public Aminoacids getAminoacids() {
		return this.aafrage_ms3.getStaticInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.constructor.IPhosphoPeptideMerger
	 * #merge(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide[],
	 * cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide[],
	 * cn.ac.dicp.gp1809.proteome.spectrum.PeakList,
	 * cn.ac.dicp.gp1809.proteome.spectrum.PeakList, java.lang.String)
	 */
	@Override
	public IPhosPeptidePair merge(int scanms2, int scanms3, short charge, IPeptide[] pepsMS2, IPeptide[] pepsMS3,
			IMS2PeakList peaksMS2, IMS2PeakList peaksMS3) {

		if (pepsMS2 == null || pepsMS3 == null || pepsMS2.length == 0 || pepsMS3.length == 0) {
			return null;
		}

		PeptidePair topMatchedPair = this.getTopMatchedPairofRank(pepsMS2, pepsMS3);

		IPhosPeptidePair pair = this.construct(scanms2, scanms3, charge, topMatchedPair, peaksMS2, peaksMS3);

		return pair;
	}

	/**
	 * First assemble all the peptide pairs and only retain the peptide pair
	 * with highest sum of primary score without consideritation
	 * 
	 * @param pepsMS2
	 * @param pepsMS3
	 * @return
	 */
	private PeptidePair getTopMatchedPairofScore(IPeptide[] pepsMS2, IPeptide[] pepsMS3) {

		PeptidePair topMatchedPair = null;

		float topScore = -Float.MAX_VALUE;
		HashSet<String> parsedSeq = new HashSet<String>();

		for (IPeptide pepMS2 : pepsMS2) {
			IModifiedPeptideSequence seq2_no_phos = PhosphoUtil.getModifiedSequenceNoPhosModif(pepMS2.getSequence(),
					this.oldPhosSymbolMS2);
			if (!parsedSeq.contains(seq2_no_phos.getSequence())) {
				parsedSeq.add(seq2_no_phos.getSequence());

				for (IPeptide pepMS3 : pepsMS3) {
					IModifiedPeptideSequence seq3_no_phos = PhosphoUtil.getModifiedSequenceNoPhosModif(
							pepMS3.getSequence(), this.oldPhosSymbolMS3, this.oldNeutralSymbol);

					if (this.equals(seq2_no_phos, seq3_no_phos)) {
						int phosnum = PhosphoUtil.getPhosphoSitesNumber(pepMS2.getSequence(), this.oldPhosSymbolMS2);
						// The top matched non-phosphopeptide
						if (phosnum != 0) {
							PeptidePair pair = new PeptidePair(seq3_no_phos, phosnum, pepMS2, pepMS3);
							float score = pair.getSumPrimaryScore();
							if (score > topScore) {
								topMatchedPair = pair;
								topScore = score;
							}
						}

						parsedSeq.add(seq3_no_phos.getSequence());
						break;
					}
				}
			}
		}

		for (IPeptide pepMS3 : pepsMS3) {
			IModifiedPeptideSequence seq3_no_phos = PhosphoUtil.getModifiedSequenceNoPhosModif(pepMS3.getSequence(),
					this.oldPhosSymbolMS3, this.oldNeutralSymbol);

			if (!parsedSeq.contains(seq3_no_phos.getSequence())) {
				parsedSeq.add(seq3_no_phos.getSequence());

				for (IPeptide pepMS2 : pepsMS2) {
					IModifiedPeptideSequence seq2_no_phos = PhosphoUtil
							.getModifiedSequenceNoPhosModif(pepMS2.getSequence(), this.oldPhosSymbolMS2);

					if (this.equals(seq2_no_phos, seq3_no_phos)) {
						int phosnum = PhosphoUtil.getPhosphoSitesNumber(pepMS2.getSequence(), this.oldPhosSymbolMS2);
						// The top matched non-phosphopeptide
						if (phosnum != 0) {
							PeptidePair pair = new PeptidePair(seq3_no_phos, phosnum, pepMS2, pepMS3);
							float score = pair.getSumPrimaryScore();
							if (score > topScore) {
								topMatchedPair = pair;
								topScore = score;
							}
						}

						parsedSeq.add(seq3_no_phos.getSequence());
						break;
					}
				}
			}
		}

		return topMatchedPair;
	}

	/**
	 * Only when the rank this peptide pair identification in MS2 or MS3 is with
	 * rank of 1
	 * 
	 * @param pepsMS2
	 * @param pepsMS3
	 * @return
	 */
	private PeptidePair getTopMatchedPairofRank(IPeptide[] pepsMS2, IPeptide[] pepsMS3) {
		IPeptide pepMS2 = pepsMS2[0];
		IPeptide pepMS3 = pepsMS3[0];

		PeptidePair topMatchedPair = null;
		/*
		 * 
		 */
		IModifiedPeptideSequence seq2_no_phos = PhosphoUtil.getModifiedSequenceNoPhosModif(pepMS2.getSequence(),
				this.oldPhosSymbolMS2);
		IModifiedPeptideSequence seq3_no_phos = PhosphoUtil.getModifiedSequenceNoPhosModif(pepMS3.getSequence(),
				this.oldPhosSymbolMS3, this.oldNeutralSymbol);
		/*
		 * The sequence is the top match of both MS2 and MS3
		 */
		if (this.equals(seq2_no_phos, seq3_no_phos)) {
			int phosnum = PhosphoUtil.getPhosphoSitesNumber(pepMS2.getSequence(), this.oldPhosSymbolMS2);
			// The top matched non-phosphopeptide
			if (phosnum == 0)
				return null;

			topMatchedPair = new PeptidePair(seq3_no_phos, phosnum, pepMS2, pepMS3);
		} else {
			// The top matched pair for MS2 scan
			PeptidePair ms2Top = null;
			// The top matched pair for MS3 scan
			PeptidePair ms3Top = null;

			int phosnum2 = PhosphoUtil.getPhosphoSitesNumber(pepMS2.getSequence(), oldPhosSymbolMS2);
			int phosnum3 = PhosphoUtil.getPhosphoSitesNumber(pepMS3.getSequence(), this.oldPhosSymbolMS3,
					this.oldNeutralSymbol);

			if (phosnum2 != 0) {
				for (int i = 1, n = pepsMS3.length; i < n; i++) {
					IPeptide pepMS = pepsMS3[i];
					IModifiedPeptideSequence seq_no_phos = PhosphoUtil.getModifiedSequenceNoPhosModif(
							pepMS.getSequence(), this.oldPhosSymbolMS3, this.oldNeutralSymbol);

					/*
					 * The peptide with same sequence (same other modifications)
					 * should be the same peptide.
					 */
					if (this.equals(seq2_no_phos, seq_no_phos)) {
						ms2Top = new PeptidePair(seq_no_phos, phosnum2, pepMS2, pepMS);
						break;
					}
				}
			}

			if (phosnum3 != 0) {
				for (int i = 1, n = pepsMS2.length; i < n; i++) {
					IPeptide pepMS = pepsMS2[i];
					IModifiedPeptideSequence seq_no_phos = PhosphoUtil
							.getModifiedSequenceNoPhosModif(pepMS.getSequence(), this.oldPhosSymbolMS2);

					/*
					 * The peptide with same sequence (same other modifications)
					 * should be the same peptide.
					 */
					if (this.equals(seq_no_phos, seq3_no_phos)) {
						ms3Top = new PeptidePair(seq3_no_phos, phosnum3, pepMS, pepMS3);
						break;
					}
				}
			}

			topMatchedPair = this.getTopPair(ms2Top, ms3Top);
		}

		return topMatchedPair;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.constructor.IPhosphoPeptideMerger
	 * #getFormatter()
	 */
	@Override
	public IPhosPairFormat getPeptideFormatter() {
		return this.combiner.getPeptideFormatter();
	}

	/**
	 * Compare whether the old sequence from ms2 and ms3 with no phospho symbols
	 * are the same.
	 * 
	 * @param seq_no_phos_ms2
	 * @param seq_no_phos_ms3
	 * @return
	 */
	private boolean equals(IModifiedPeptideSequence seq_no_phos_ms2, IModifiedPeptideSequence seq_no_phos_ms3) {

		int len = seq_no_phos_ms2.length();
		if (len != seq_no_phos_ms3.length())
			return false;

		String ms2 = seq_no_phos_ms2.getSequence();
		String ms3 = seq_no_phos_ms3.getSequence();
		for (int i = 0; i < len; i++) {
			char c2 = ms2.charAt(i);
			char c3 = ms3.charAt(i);

			if (c2 < 'A' || c2 > 'Z') {
				char exp3 = this.oldSymbolMap.get(c2);
				if (c3 != exp3)
					return false;
			} else {
				if (c2 != c3)
					return false;
			}
		}

		return true;

	}

	/**
	 * The top matched peptide pair for ms2 and ms3.
	 * 
	 * <b>Here, we use the sum of primary score to just which pair is the top
	 * matched pair</b>
	 * 
	 * @param ms2top
	 * @param ms3top
	 * @return
	 */
	private PeptidePair getTopPair(PeptidePair ms2top, PeptidePair ms3top) {

		// Including both are null
		if (ms2top == null)
			return ms3top;
		if (ms3top == null)
			return ms2top;

		float sum2 = ms2top.getSumPrimaryScore();
		float sum3 = ms3top.getSumPrimaryScore();

		if (sum2 > sum3)
			return ms2top;

		if (sum2 < sum3)
			return ms3top;

		System.out.println(
				"The top matched phosphopeptide in ms3 " + "and ms2 are with the same score, return ms3 peptide");
		return ms3top;
	}

	/**
	 * Estimate the most probable site localizations. And return the
	 * phosphopeptide pairs
	 * 
	 * @param scanms2
	 * @param scanms3
	 * @param topPair
	 * @param peaksMS2
	 * @param peaksMS3
	 * @param source
	 * @return
	 */
	private IPhosPeptidePair construct(int scanms2, int scanms3, short charge, PeptidePair topPair,
			IMS2PeakList peaksMS2, IMS2PeakList peaksMS3) {
		if (topPair == null)
			return null;

		SeqvsAscore svts = AScorePhosCalculator.compute(topPair.getSeq_no_phos_ms3(), charge, peaksMS2, peaksMS3,
				topPair.getPhosSitesNumber(), this.aafrage_ms3, threshold, phosSymbol, neuSymbol);

		if (svts == null)
			return null;

		return this.combiner.combineAScore(topPair.getPepMS2(), topPair.getPepMS3(), scanms2, scanms3, svts);
	}

	/**
	 * The peptide pair for temporary use
	 * 
	 * @author Xinning
	 * @version 0.1, 02-19-2009, 20:01:47
	 */
	private class PeptidePair {

		private IPeptide pepMS2, pepMS3;
		// Sequence no terminal and phosphorylation modification
		private IModifiedPeptideSequence seq_no_phos_ms3;
		private int phosSite;

		private PeptidePair(IModifiedPeptideSequence seq_no_phos_ms3, int phosSite, IPeptide pepMS2, IPeptide pepMS3) {
			this.seq_no_phos_ms3 = seq_no_phos_ms3;
			this.pepMS2 = pepMS2;
			this.pepMS3 = pepMS3;
			this.phosSite = phosSite;
		}

		public IPeptide getPepMS2() {
			return pepMS2;
		}

		public IPeptide getPepMS3() {
			return pepMS3;
		}

		public IModifiedPeptideSequence getSeq_no_phos_ms3() {
			return seq_no_phos_ms3;
		}

		public int getPhosSitesNumber() {
			return this.phosSite;
		}

		public float getSumPrimaryScore() {
			return pepMS2.getPrimaryScore() + pepMS3.getPrimaryScore();
		}
	}
}
