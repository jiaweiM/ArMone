/*
 ******************************************************************************
 * File: SeqvsAscore.java * * * Created on 06-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation;

import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;

/**
 * The most possible sequence and the Ascore.
 * 
 * @author Xinning
 * @version 0.1, 06-12-2009, 16:31:42
 */
public class SeqvsAscore implements ISeqvsScore {

	/**
	 * The Tscore (or peptide score) for current possible phosphopeptide
	 */
	private double peptidescore;

	// sequence and the site localization
	private IModifiedPeptideSequence seqsite;

	/**
	 * The ambiguous score.
	 */
	private double[] ascores;

	/**
	 * For MS3
	 */
	private double[] ascores2;

	/**
	 * The site where NL occurred for MS3. If not set be -1
	 */
	private int NLsite = -1;

	/**
	 * 
	 * 
	 * @param seqsite
	 * @param peptidescore
	 * @param ascore
	 */
	public SeqvsAscore(IModifiedPeptideSequence seqsite, double peptidescore, double[] ascores) {
		this.seqsite = seqsite;
		this.peptidescore = peptidescore;
		this.ascores = ascores;
	}

	/**
	 * 
	 * 
	 * @param seqsite
	 * @param peptidescore
	 * @param ascore
	 */
	public SeqvsAscore(IModifiedPeptideSequence seqsite, double peptidescore, double[] ascores, double[] ascores2) {
		this.seqsite = seqsite;
		this.peptidescore = peptidescore;
		this.ascores = ascores;
		this.ascores2 = ascores2;
	}

	/**
	 * Phosphopeptide with most probable site localization(s). The symbol is the
	 * original symbols for phospho symbol and neutral loss symbol.
	 */
	public IModifiedPeptideSequence getSequence() {
		return this.seqsite;
	}

	/**
	 * The Tscore of the peptide or Peptidescore of the peptide for MS2 only
	 * peptide identification
	 */
	public double getSiteSocre() {
		return this.peptidescore;
	}

	/**
	 * The ambiguous score.
	 * 
	 * @return
	 */
	public double[] getAscores() {
		return this.ascores;
	}

	/**
	 * The ambiguous score of MS3
	 * 
	 * @return
	 */
	public double[] getAscores2() {
		return this.ascores2;
	}

	/**
	 * The site where NL occurred for MS3, If not set be -1
	 */
	public void setNLSite(int NLsite) {
		this.NLsite = NLsite;
	}

	/**
	 * The site where NL occurred for MS3, If not set be -1
	 */
	public int getNLSite() {
		return this.NLsite;
	}

	@Override
	public String toString() {
		return this.seqsite + ": " + this.peptidescore;
	}
}
