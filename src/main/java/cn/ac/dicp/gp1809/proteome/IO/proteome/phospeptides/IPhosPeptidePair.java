/* 
 ******************************************************************************
 * File: IPhosPeptidePair.java * * * Created on 02-17-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides;

import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IPhosphoSite;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.TScores;

/**
 * The phosphopeptide pair which is identified by both MS2 and MS3
 * 
 * <p>Changes:
 * <li>0.1.2, 06-09-2009: add method {@link #getNeutralLossPeptideSequence()}
 * <li>0.1.3, 06-12-2009: add {@link #getAscores()}
 * 
 * @author Xinning
 * @version 0.1.3, 06-12-2009, 20:46:46
 */
public interface IPhosPeptidePair extends IPeptide {

	/**
	 * The sequence without neutral loss site localization information
	 * 
	 * @return the parsed original sequence of this peptide
	 */
	public IPhosphoPeptideSequence getPeptideSequence();

	/**
	 * The sequence with neutral loss site localization information. This
	 * sequence is the peptide sequence of MS3 spectra.
	 * 
	 * @return 
	 */
	public IPhosphoPeptideSequence getNeutralLossPeptideSequence();

	/**
	 * The scan number of MS2 for this peptide identification
	 * 
	 * @return
	 */
	public int getMS2Scan();

	/**
	 * The scan number of MS3 for this peptide identification
	 * 
	 * @return
	 */
	public int getMS3Scan();

	/**
	 * The actual MZ value of MS2 for this peptide identification
	 * 
	 * @return
	 */
	public double getMS2MZ();

	/**
	 * The actual MZ value of MS3 for this peptide identification
	 * 
	 * @return
	 */
	public double getMS3MZ();

	/**
	 * Because the at least one top matched peptide is needed when construct the
	 * phospho peptide pair, this is the rank of peptide not from the top one.
	 * Or the peptide with high rank.
	 * 
	 * @return
	 */
	public short getRank();

	/**
	 * The number of phosphorylated sites
	 * 
	 * @return
	 */
	public int getPhosphoSiteNumber();

	/**
	 * The site on which the phosphate was lost (we assume only one phosphosite
	 * can lose the phosphate)
	 * 
	 * @return
	 */
	public int getNeutralLossSite();

	/**
	 * The phosphorylated site. If this peptide is not a phosphopeptide, null
	 * will be return.
	 * 
	 * @return
	 */
	public IPhosphoSite[] getPhosphoSites();

	/**
	 * The TScore of each site
	 * 
	 * @return
	 */
	public TScores getTScores();

	/**
	 * The phosphopeptide sequence with neutral loss symbol
	 * 
	 * @return
	 */
	public String getPhosSequencewithNeutralSymbol();
}
