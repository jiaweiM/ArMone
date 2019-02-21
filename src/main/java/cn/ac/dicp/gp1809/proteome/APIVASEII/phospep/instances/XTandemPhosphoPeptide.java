/* 
 ******************************************************************************
 * File: XTandemPhosphoPeptide.java * * * Created on 02-17-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.instances;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.PhosphoSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.PhosphoUtil;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.IXTandemPeptide;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.IXTandemPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.XTandemPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequenceUpdateException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;

/**
 * An XTandem phosphopeptide
 * 
 * @author Xinning
 * @version 0.1.1, 05-25-2010, 17:29:11
 */
public class XTandemPhosphoPeptide extends XTandemPeptide implements
        IPhosphoPeptide {

	//The number of phosphorylated sites
	private int phosSite = -1;
	//The phosphosite
	private PhosphoSite[] sites;

	private char phosSymbol;
	private char neutralSymbol;

	public XTandemPhosphoPeptide(IXTandemPeptide pep, char phosSymbol,
	        char neutralSymbol) {
		super(pep);
		this.phosSymbol = phosSymbol;
		this.neutralSymbol = neutralSymbol;
	}

	/**
	 * @param baseName
	 * @param scanNumBeg
	 * @param scanNumEnd
	 * @param sequence
	 * @param charge
	 * @param mh
	 * @param deltaMs
	 * @param rank
	 * @param evalue
	 * @param hyperscore
	 * @param nextscore
	 * @param refs
	 */
	public XTandemPhosphoPeptide(String baseName, int scanNumBeg,
	        int scanNumEnd, String sequence, short charge, double mh,
	        double deltaMs, short rank, double evalue, float hyperscore,
	        float nextscore, float yscore, float bscore, HashSet<ProteinReference> refs,
	        IXTandemPeptideFormat<?> formatter, char phosSymbol,
	        char neutralSymbol) {
		super(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
		        rank, evalue, hyperscore, nextscore, yscore, bscore, refs, formatter);
		this.phosSymbol = phosSymbol;
		this.neutralSymbol = neutralSymbol;
	}

	/**
	 * @param scanNum
	 * @param sequence
	 * @param charge
	 * @param mh
	 * @param deltaMs
	 * @param rank
	 * @param evalue
	 * @param hyperscore
	 * @param nextscore
	 * @param refs
	 */
	public XTandemPhosphoPeptide(String scanNum, String sequence, short charge,
	        double mh, double deltaMs, short rank, double evalue,
	        float hyperscore, float nextscore, float yscore, float bscore, HashSet<ProteinReference> refs,
	        IXTandemPeptideFormat<?> formatter, char phosSymbol,
	        char neutralSymbol) {
		super(scanNum, sequence, charge, mh, deltaMs, rank, evalue, hyperscore,
		        nextscore, yscore, bscore, refs, formatter);
		this.phosSymbol = phosSymbol;
		this.neutralSymbol = neutralSymbol;
	}

	/**
	 * @param baseName
	 * @param scanNumBeg
	 * @param scanNumEnd
	 * @param sequence
	 * @param charge
	 * @param mh
	 * @param deltaMs
	 * @param rank
	 * @param evalue
	 * @param hyperscore
	 * @param nextscore
	 * @param refs
	 * @param pi
	 * @param numofTerms
	 */
	public XTandemPhosphoPeptide(String baseName, int scanNumBeg,
	        int scanNumEnd, String sequence, short charge, double mh,
	        double deltaMs, short rank, double evalue, float hyperscore,
	        float nextscore, float yscore, float bscore, HashSet<ProteinReference> refs, float pi,
	        short numofTerms, IXTandemPeptideFormat<?> formatter,
	        char phosSymbol, char neutralSymbol) {
		super(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
		        rank, evalue, hyperscore, nextscore, yscore, bscore, refs, pi, numofTerms,
		        formatter);
		this.phosSymbol = phosSymbol;
		this.neutralSymbol = neutralSymbol;
	}

	/**
	 * @param scanNum
	 * @param sequence
	 * @param charge
	 * @param mh
	 * @param deltaMs
	 * @param rank
	 * @param evalue
	 * @param hyperscore
	 * @param nextscore
	 * @param refs
	 * @param pi
	 * @param numofTerms
	 */
	public XTandemPhosphoPeptide(String scanNum, String sequence, short charge,
	        double mh, double deltaMs, short rank, double evalue,
	        float hyperscore, float nextscore, float yscore, float bscore, HashSet<ProteinReference> refs,
	        float pi, short numofTerms, IXTandemPeptideFormat<?> formatter,
	        char phosSymbol, char neutralSymbol) {
		super(scanNum, sequence, charge, mh, deltaMs, rank, evalue, hyperscore,
		        nextscore, yscore, bscore, refs, pi, numofTerms, formatter);
		this.phosSymbol = phosSymbol;
		this.neutralSymbol = neutralSymbol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.IPhosphoPeptide#
	 * getPhosphoSiteNumber()
	 */
	@Override
	public int getPhosphoSiteNumber() {
		if (this.phosSite < 0) {
			this.parsePhosphoSite();
		}

		return this.phosSite;
	}

	/*
	 * (non-Javadoc) Reparse the phosphopeptide sites.
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.proteome.AbstractPeptide#updateSequence
	 * (java.lang.String)
	 */
	@Override
	public void updateSequence(String newSeq) throws SequenceUpdateException {
		super.updateSequence(newSeq);

		this.parsePhosphoSite();
	}

	@Override
	public PhosphoSite[] getPhosphoSites() {
		if (this.phosSite < 0)
			this.parsePhosphoSite();

		return this.sites;
	}

	/**
	 * Parse the phosphorylation informations for this peptide
	 */
	protected final void parsePhosphoSite() {
		this.sites = PhosphoUtil.getPhosphoSites(this.getSequence(),
		        phosSymbol, neutralSymbol);

		if (this.sites == null)
			this.phosSite = 0;
		else
			this.phosSite = this.sites.length;
	}

}
