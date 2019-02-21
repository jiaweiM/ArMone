/* 
 ******************************************************************************
 * File: PhosphoPeptideSequence.java * * * Created on 02-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.aasequence;

import java.util.ArrayList;
import java.util.LinkedList;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IPhosphoSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.PhosphoSite;

/**
 * The phosphorylated peptide sequence
 * 
 * @author Xinning
 * @version 0.1.2, 09-04-2009, 23:00:46
 */
public class PhosphoPeptideSequence extends ModifiedPeptideSequence implements
        IPhosphoPeptideSequence {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	/**
	 * Parse a peptide sequence from the sequest(or other database search engin
	 * ?) outputted sequences. These sequence should with the following format:
	 * "X.XXXXXX#XX.X", "X.XXXXpXXXX." or "X.XXXXgXXXX".
	 * 
	 * @param seq_sequest
	 */
	public static PhosphoPeptideSequence parseSequence(String seq_sequest,
	        char phosSymbol, char neuSymbol) {

		if (seq_sequest == null || seq_sequest.length() == 0)
			return null;

		int st;
		int en = seq_sequest.length() - 2;
		char prev;
		char next;
		if (seq_sequest.charAt(1) != '.') {
			st = 0;
			prev = '-';
		} else {
			st = 2;
			prev = seq_sequest.charAt(0);
		}

		if (seq_sequest.charAt(en) == '.') {
			next = seq_sequest.charAt(en + 1);
		} else {
			next = '-';

			// D.GSA@SSS.
			if (seq_sequest.charAt(en + 1) == '.')
				en++;

			// D.GSA@SSS
			else
				en += 2;
		}

		String seq = seq_sequest.substring(st, en);

		return new PhosphoPeptideSequence(seq, prev, next, phosSymbol,
		        neuSymbol);
	}

	private IPhosphoSite[] phosphosites;
	private IPhosphoSite[] neuphosites;
	private char phosSymbol, neuSymbol;

	/**
	 * 
	 * @param seq
	 * @param pep_prev_aa
	 * @param pep_next_aa
	 * @param phosSymbol
	 * @param neuSymbol
	 */
	public PhosphoPeptideSequence(String seq, char pep_prev_aa,
	        char pep_next_aa, char phosSymbol, char neuSymbol) {
		super(seq, pep_prev_aa, pep_next_aa);

		this.phosSymbol = phosSymbol;
		this.neuSymbol = neuSymbol;
		this.phosphosites = parsePhosphorylation(this.getModifications(),
		        phosSymbol, neuSymbol);
		this.neuphosites = parseNeuPhosphorylations(this.phosphosites);
	}

	/**
	 * Construct a phosphopeptide sequence from a modified peptide sequence
	 * 
	 * @param msequence
	 * @param phosSymbol
	 * @param neuSymbol
	 */
	public PhosphoPeptideSequence(IModifiedPeptideSequence msequence,
	        char phosSymbol, char neuSymbol) {
		super(msequence);

		this.phosSymbol = phosSymbol;
		this.neuSymbol = neuSymbol;
		this.phosphosites = parsePhosphorylation(this.getModifications(),
		        phosSymbol, neuSymbol);
		this.neuphosites = parseNeuPhosphorylations(this.phosphosites);
	}

	/**
	 * Construct another phosphorylated peptide sequence. The information will
	 * be deep cloned.
	 * 
	 * @param msequence
	 */
	public PhosphoPeptideSequence(IPhosphoPeptideSequence psequence) {
		super(psequence);

		this.phosphosites = parseKnownPhosphorylation(this.getModifications());
		this.neuphosites = parseNeuPhosphorylations(this.phosphosites);
	}

	/**
	 * Parse the phosphorylation sites
	 * 
	 * @param msites
	 * @param phosSymbol
	 * @param neuSymbol
	 * @return
	 */
	private static IPhosphoSite[] parsePhosphorylation(IModifSite[] msites,
	        char phosSymbol, char neuSymbol) {

		if (msites != null && msites.length != 0) {
			LinkedList<IPhosphoSite> list = new LinkedList<IPhosphoSite>();
			for (int i = 0; i < msites.length; i++) {
				IModifSite site = msites[i];

				if (site instanceof IPhosphoSite) {
					//Do nothing
					list.add((IPhosphoSite) site);
				} else {
					char sym = site.symbol();

					PhosphoSite psite = null;
					if (sym == phosSymbol) {
						psite = new PhosphoSite(site.modifiedAt(), site
						        .modifLocation(), sym);
					} else if (sym == neuSymbol) {
						psite = new PhosphoSite(site.modifiedAt(), site
						        .modifLocation(), sym, true);
					}

					if (psite != null) {
						list.add(psite);
						//update the modification sites with phosphosite
						msites[i] = psite;
					}
				}

			}

			if (list.size() != 0)
				return list.toArray(new IPhosphoSite[list.size()]);
		}

		return null;
	}

	/**
	 * Parse the known phosphorylation sites from the modification site list.
	 * The phosphorylation sites have been "a IPhosphoSite"
	 * 
	 * @param msites
	 * @param phosSymbol
	 * @param neuSymbol
	 * @return
	 */
	private static IPhosphoSite[] parseKnownPhosphorylation(IModifSite[] msites) {
		if (msites != null && msites.length != 0) {

			ArrayList<IPhosphoSite> list = null;

			for (IModifSite site : msites) {
				if (site instanceof IPhosphoSite) {
					
					if(list == null)
						list = new ArrayList<IPhosphoSite>();
					
					list.add((IPhosphoSite) site);
				}
			}
			
			return list == null ? null : list.toArray(new IPhosphoSite[list.size()]);
		}
		
		return null;
	}

	/**
	 * Generate the phosphorylation sites with neutral loss.
	 * 
	 * @param allsites
	 * @return
	 */
	private static IPhosphoSite[] parseNeuPhosphorylations(
	        IPhosphoSite[] allsites) {

		if (allsites == null || allsites.length == 0)
			return null;

		ArrayList<IPhosphoSite> neusites = null;

		for (IPhosphoSite site : allsites) {

			if (site.isNeutralLoss()) {
				if (neusites == null)
					neusites = new ArrayList<IPhosphoSite>();

				neusites.add(site);
			}

		}

		return neusites == null ? null : neusites
		        .toArray(new IPhosphoSite[neusites.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence#
	 * getPhosphorylations()
	 */
	@Override
	public IPhosphoSite[] getPhosphorylations() {
		return phosphosites;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence#
	 * getNeuLostPhosphorylations()
	 */
	@Override
	public IPhosphoSite[] getNeuLostPhosphorylations() {
		return this.neuphosites;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.aasequence.ModifiedPeptideSequence#
	 * renewModifiedSequence(cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite[])
	 */
	@Override
	public void renewModifiedSequence(IModifSite[] newmodifs) {
		super.renewModifiedSequence(newmodifs);
		this.phosphosites = parsePhosphorylation(this.getModifications(),
		        this.phosSymbol, this.neuSymbol);
		this.neuphosites = parseNeuPhosphorylations(this.phosphosites);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence#
	 * renewPhosphoSequence
	 * (cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IPhosphoSite[])
	 */
	@Override
	public void renewPhosphoSequence(IPhosphoSite[] phosSites) {
		IModifSite[] sites = this.getModifications();

		if (sites == null) {
			sites = phosSites;
		} else {
			//The original phosphosites must be removed

			//Add other modifications
			LinkedList<IModifSite> list = new LinkedList<IModifSite>();
			for (IModifSite site : sites) {
				if (!(site instanceof IPhosphoSite))
					list.add(site);
			}

			if (phosSites != null) {
				for (IPhosphoSite site : phosSites) {
					if (site != null)
						list.add(site);
				}
			}

			sites = list.toArray(new IModifSite[list.size()]);
			//No new modifications
			if (sites.length == 0)
				sites = null;
		}

		this.renewModifiedSequence(sites);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence#
	 * getPhosphorylationNumber()
	 */
	@Override
	public int getPhosphorylationNumber() {
		return this.phosphosites == null ? 0 : this.phosphosites.length;
	}

	@Override
	public int getNeutralLostPhosphorylationNumber() {
		return this.neuphosites == null ? 0 : this.neuphosites.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PhosphoPeptideSequence clone() {
		return (PhosphoPeptideSequence) super.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.lang.IDeepCloneable#deepClone()
	 */
	@Override
	public PhosphoPeptideSequence deepClone() {
		PhosphoPeptideSequence mseq = (PhosphoPeptideSequence) super
		        .deepClone();
		
		mseq.phosphosites = parseKnownPhosphorylation(mseq.getModifications());
		mseq.neuphosites = parseNeuPhosphorylations(mseq.phosphosites);

		return mseq;
	}
}
