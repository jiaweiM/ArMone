/* 
 ******************************************************************************
 * File: AbstractPhosphoPeptidePair.java * * * Created on 02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IPhosphoSite;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.IPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.AbstractPeptide;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.PhosConstants;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.TScores;

/**
 * The abstract phosphopeptide pair
 * 
 * @author Xinning
 * @version 0.1.3, 06-16-2009, 21:09:59
 */
public abstract class AbstractPhosphoPeptidePair extends AbstractPeptide
        implements IPhosPeptidePair {

	//The site on which the neutral loss occurred
	private int NLSite;
	private IPhosphoPeptideSequence phoseq;
	//The neutral loss sequence 
	private IPhosphoPeptideSequence neuphoseq;

	private IPhosphoSite[] phosphoSites;
	private TScores tscores;

	private double mz_ms2;
	private double mz_ms3;

	/**
	 * 
	 * @param baseName
	 * @param scanNumMS2
	 * @param scanNumMS3
	 * @param phosSeq_neu
	 *            The phosPeptideSequence containing neutral loss site
	 *            informations.
	 * @param neuloc
	 *            the localization of the neutral loss site
	 * @param charge
	 * @param mh
	 * @param deltaMs_ms2
	 * @param rankms2
	 * @param rankms3
	 * @param refs
	 * @param pi
	 * @param numofTerm
	 */
	protected AbstractPhosphoPeptidePair(String baseName, int scanNumMS2,
	        int scanNumMS3, IPhosphoPeptideSequence phosSeq_neu, int neuloc,
	        short charge, double mh_ms2, double deltaMs_ms2, double mz_ms2,
	        double mz_ms3, short rank_max, HashSet<ProteinReference> refs,
	        float pi, short numofTerm, TScores tscores,
	        IPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(baseName, scanNumMS2, scanNumMS3, phosSeq_neu, charge, mh_ms2,
		        deltaMs_ms2, rank_max, refs, pi, numofTerm, formatter);

		this.phoseq = phosSeq_neu;
		this.phosphoSites = phosSeq_neu.getPhosphorylations();
		this.tscores = tscores;
		this.NLSite = neuloc;

		this.mz_ms2 = mz_ms2;
		this.mz_ms3 = mz_ms3;
	}

	/**
	 * For parsing from ppl
	 * 
	 * @param scanNum
	 *            , well formated scan number
	 * @param phosSeq_neu
	 * @param neuloc
	 * @param charge
	 * @param mh_ms2
	 * @param deltaMs_ms2
	 * @param rank_max
	 * @param refs
	 * @param pi
	 * @param numofTerm
	 */
	protected AbstractPhosphoPeptidePair(String scanNum,
	        IPhosphoPeptideSequence phosSeq_neu, int neuloc, short charge,
	        double mh_ms2, double deltaMs_ms2, double mz_ms2, double mz_ms3,
	        short rank_max, HashSet<ProteinReference> refs, float pi,
	        short numofTerm, TScores tscores, IPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(scanNum, phosSeq_neu, charge, mh_ms2, deltaMs_ms2, rank_max,
		        refs, pi, numofTerm, formatter);

		this.phoseq = phosSeq_neu;
		this.phosphoSites = phosSeq_neu.getPhosphorylations();
		this.tscores = tscores;
		this.NLSite = neuloc;
		
		this.mz_ms2 = mz_ms2;
		this.mz_ms3 = mz_ms3;
	}

	/**
	 * 
	 * @param baseName
	 * @param scanNumMS2
	 * @param scanNumMS3
	 * @param phosSeq_neu
	 *            The phosPeptideSequence containing neutral loss site
	 *            informations.
	 * @param neuloc
	 *            the localization of the neutral loss site
	 * @param charge
	 * @param mh
	 * @param deltaMs_ms2
	 * @param rankms2
	 * @param rankms3
	 * @param refs
	 * @param pi
	 * @param numofTerm
	 */
	protected AbstractPhosphoPeptidePair(String baseName, int scanNumMS2,
	        int scanNumMS3, IPhosphoPeptideSequence phosSeq_neu, int neuloc,
	        short charge, double mh_ms2, double deltaMs_ms2, double mz_ms2,
	        double mz_ms3, short rank_max, HashSet<ProteinReference> refs,
	        float pi, short numofTerm, double[] ascores, IPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(baseName, scanNumMS2, scanNumMS3, phosSeq_neu, charge, mh_ms2,
		        deltaMs_ms2, rank_max, refs, pi, numofTerm, formatter);

		this.phoseq = phosSeq_neu;
		this.phosphoSites = phosSeq_neu.getPhosphorylations();
		this.NLSite = neuloc;

		this.mz_ms2 = mz_ms2;
		this.mz_ms3 = mz_ms3;
		
		this.setAscores(ascores);
	}

	/**
	 * For parsing from ppl
	 * 
	 * @param scanNum
	 *            , well formated scan number
	 * @param phosSeq_neu
	 * @param neuloc
	 * @param charge
	 * @param mh_ms2
	 * @param deltaMs_ms2
	 * @param rank_max
	 * @param refs
	 * @param pi
	 * @param numofTerm
	 */
	protected AbstractPhosphoPeptidePair(String scanNum,
	        IPhosphoPeptideSequence phosSeq_neu, int neuloc, short charge,
	        double mh_ms2, double deltaMs_ms2, double mz_ms2, double mz_ms3,
	        short rank_max, HashSet<ProteinReference> refs, float pi,
	        short numofTerm, double[] ascores, IPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(scanNum, phosSeq_neu, charge, mh_ms2, deltaMs_ms2, rank_max,
		        refs, pi, numofTerm, formatter);

		this.phoseq = phosSeq_neu;
		this.phosphoSites = phosSeq_neu.getPhosphorylations();
		this.NLSite = neuloc;
		
		this.mz_ms2 = mz_ms2;
		this.mz_ms3 = mz_ms3;
		
		this.setAscores(ascores);
	}

	/**
	 * parse the neural lost phosphorylation site phosphorylation sites
	 * 
	 * @param phosphoSites
	 * @return
	 */
	private int parseNeutrualSite(IPhosphoSite[] phosphoSites) {
		int site = 0;
		if (phosphoSites != null) {
			for (IPhosphoSite phosite : phosphoSites) {
				if (phosite.isNeutralLoss()) {
					if (site > 0)
						throw new IllegalArgumentException(
						        "More than one phosphorylation sites are "
						                + "with neutral loss. Expected: one.");

					site = phosite.modifLocation();
				}
			}

			if (site <= 0)
				throw new IllegalArgumentException(
				        "No neutral loss site in this peptide. Excepted: one.");
		}

		return site;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IPhosPeptidePair
	 * #getMS2Scan()
	 */
	@Override
	public int getMS2Scan() {
		return this.getScanNumBeg();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IPhosPeptidePair
	 * #getMS3Scan()
	 */
	@Override
	public int getMS3Scan() {
		return this.getScanNumEnd();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IPhosPeptidePair
	 * #getNeutralLossSite()
	 */
	@Override
	public int getNeutralLossSite() {
		return this.NLSite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IPhosPeptidePair
	 * #getPhosphoSiteNumber()
	 */
	@Override
	public int getPhosphoSiteNumber() {
		return this.phosphoSites == null ? 0 : this.phosphoSites.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IPhosPeptidePair
	 * #getPhosphoSites()
	 */
	@Override
	public IPhosphoSite[] getPhosphoSites() {
		return this.phosphoSites;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.proteome.AbstractPeptide#getPeptideSequence
	 * ()
	 */
	@Override
	public IPhosphoPeptideSequence getPeptideSequence() {
		return this.phoseq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair#
	 * getNeutralLossPeptideSequence()
	 */
	@Override
	public IPhosphoPeptideSequence getNeutralLossPeptideSequence() {

		if (this.neuphoseq == null) {
			this.neuphoseq = this.phoseq.deepClone();
			IModifSite site = this.neuphoseq.getModificationAt(this.NLSite);
			if (site == null)
				throw new NullPointerException("The aminoacid at the "
				        + this.NLSite + " index is not modified.");

			site.setSymbol(PhosConstants.NEU_SYMBOL);

			this.neuphoseq.renewModifiedSequence();
		}

		return this.neuphoseq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IPhosPeptidePair
	 * #getTScores()
	 */
	@Override
	public TScores getTScores() {
		return this.tscores;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IPhosPeptidePair
	 * #getMS2MZ()
	 */
	@Override
	public double getMS2MZ() {
		return this.mz_ms2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IPhosPeptidePair
	 * #getMS3MZ()
	 */
	@Override
	public double getMS3MZ() {
		return this.mz_ms3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair#
	 * getPhosSequencewithNeutralSymbol()
	 */
	@Override
	public String getPhosSequencewithNeutralSymbol() {
		/*
		 * if (this.seqWithNeu == null) { String org =
		 * this.getPeptideSequence().getSequence();
		 * 
		 * int curtl = 0; for (int i = 2; i < org.length(); i++) { char c =
		 * org.charAt(i); if (c <= 'Z' && c >= 'A') curtl++;
		 * 
		 * if (curtl == this.NLSite) { StringBuilder sb = new
		 * StringBuilder(org); sb.setCharAt(i + 1, PhosConstants.NEU_SYMBOL);
		 * this.seqWithNeu = sb.toString();
		 * 
		 * break; } } }
		 * 
		 * return this.seqWithNeu;
		 */

		return this.getNeutralLossPeptideSequence().getFormattedSequence();
	}
}
