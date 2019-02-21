/* 
 ******************************************************************************
 * File: SeqvsTscore.java * * * Created on 03-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IPhosphoSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.PhosphoSite;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.PhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.TScores;

/**
 * After computing of Tscores for all possible phosphorylation sites for a
 * sequence, the score and the localization will be merged together and return
 * the sequence with most probable phosphorylation sites.
 * 
 * @author Xinning
 * @version 0.2.1, 03-03-2009, 21:45:38
 */
public class SeqvsTscore {
	
	private char phossym;
	private int siteNum;
	// <sitelocalization, score>
	private HashMap<Integer, Double> scoremap;
	private TScores tscores;
	// sequence and the site localization
	private IPhosphoPeptideSequence seqsite;
	//The sequence need to be regenerated.
	private boolean needReparse = true;
	// The site where NL occurred for MS3
	private int NLsite = -1;

	/**
	 * 
	 * @param sequence_no_phos
	 *            sequence no phosphorylation sites
	 * @param siteNum
	 * @param phossym
	 */
	SeqvsTscore(IModifiedPeptideSequence sequence_no_phos, int siteNum,
	        char phossym) {
		this.seqsite = new PhosphoPeptideSequence(sequence_no_phos, (char) 0,
		        (char) 0);
		this.phossym = phossym;
		this.siteNum = siteNum;
		this.scoremap = new HashMap<Integer, Double>();
	}

	/**
	 * add a possible sequence.
	 * 
	 * @param sequence
	 * @param tscore
	 */
	void add(String sequence, double tscore) {
		// the entry must be reset;
		this.needReparse = true;
		this.tscores = null;

		PhosphoPeptideSequence pseq = PhosphoPeptideSequence.parseSequence(
		        sequence, this.phossym, (char) 0);

		for (IPhosphoSite site : pseq.getPhosphorylations()) {
			Integer idx = site.modifLocation();
			Double sum_score = this.scoremap.get(idx);
			if (sum_score == null)
				sum_score = new Double(tscore);
			else
				sum_score = new Double(sum_score.doubleValue() + tscore);

			this.scoremap.put(idx, sum_score);
		}
	}

	/**
	 * Phosphopeptide with most probable site localization(s). The symbol is the
	 * original symbols for phospho symbol and neutral loss symbol.
	 */
	public IPhosphoPeptideSequence getSequence() {
		if (this.needReparse) {
			this.parseSequence();
		}

		return seqsite;
	}

	/**
	 * The most probable sequence. The symbol is the original symbols for
	 * phospho symbol and neutral loss symbol.
	 */
	private void parseSequence() {

		this.needReparse = false;

		Entry<Integer, Double>[] entries = this.getSortedEntries();
		int len = entries.length;

		char[] sites = new char[len];
		int[] locs = new int[len];
		double[] scores = new double[len];

		for (int i = 0; i < len; i++) {
			Entry<Integer, Double> entry = entries[i];
			scores[i] = entry.getValue();
			int loc = entry.getKey();
			locs[i] = loc;
			sites[i] = this.seqsite.getAminoaicdAt(loc);
		}

		this.tscores = new TScores(sites, locs, scores, len);

		IPhosphoSite[] psites = new IPhosphoSite[this.siteNum];
		for (int i = 0; i < this.siteNum; i++) {
			IPhosphoSite site = new PhosphoSite(ModSite.newInstance_aa(sites[i]), locs[i], this.phossym);
			psites[i] = site;
		}

		seqsite.renewPhosphoSequence(psites);
	}

	/**
	 * @return all possible site and their scores with the format of
	 *         "S[siteidx]: score; "
	 */
	public TScores getSiteSocres() {
		if (this.needReparse) {
			this.parseSequence();
		}

		return this.tscores;
	}

	/*
	 * Sort the site by their Tscores
	 */
	@SuppressWarnings("unchecked")
	private Entry<Integer, Double>[] getSortedEntries() {
		if (this.scoremap.size() == 0)
			throw new NullPointerException(
			        "There is no predefined Tscore for sites.");

		Entry<Integer, Double>[] entries = this.scoremap.entrySet().toArray(
		        new Entry[0]);
		Arrays.sort(entries, new Comparator<Entry<Integer, Double>>() {

			public int compare(Entry<Integer, Double> o1,
			        Entry<Integer, Double> o2) {
				double s1 = o1.getValue().doubleValue();
				double s2 = o2.getValue().doubleValue();

				// sort by the site localization;
				if (s1 == s2) {
					int site1 = o1.getKey().intValue();
					int site2 = o1.getKey().intValue();

					return site1 > site2 ? 1 : -1;
				}

				return s1 > s2 ? -1 : 1;
			}

		});

		return entries;
	}

	/**
	 * The site where NL occurred for MS3
	 */
	public void setNLSite(int NLsite) {
		this.NLsite = NLsite;
	}

	/**
	 * The site where NL occurred for MS3
	 */
	public int getNLSite() {
		return this.NLsite;
	}

	@Override
	public String toString() {
		return this.seqsite + ":\r\n " + this.getSiteSocres();
	}
}
