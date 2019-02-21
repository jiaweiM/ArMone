/* 
 ******************************************************************************
 * File: SequestPhosphoPeptide.java * * * Created on 02-17-2009
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
import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequenceUpdateException;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SequestPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;

/**
 * A sequest phosphopeptide
 * 
 * @author Xinning
 * @version 0.1.1, 05-25-2010, 17:23:01
 */
public class SequestPhosphoPeptide extends SequestPeptide implements
        IPhosphoPeptide {

	//The number of phosphorylated sites
	private int phosSite = -1;
	//The phosphosite
	private PhosphoSite[] sites;

	private char phosSymbol;
	private char neutralSymbol;

	/**
	 * 
	 * @param pep
	 * @param phosSymbol
	 * @param neutralSymbol
	 */
	public SequestPhosphoPeptide(ISequestPeptide pep, char phosSymbol,
	        char neutralSymbol) {
		super(pep);
		this.phosSymbol = phosSymbol;
		this.neutralSymbol = neutralSymbol;
	}

	/**
	 * Create a phosphopeptide from the values
	 * 
	 * @param sequence
	 *            sequece with terminals
	 * @param rank
	 *            rank of this peptide
	 * @param charge
	 * @param rsp
	 * @param mh
	 * @param dcn
	 * @param xcorr
	 * @param sp
	 * @param ions
	 * @param refs
	 *            references from out (Null not permitted )
	 * @param phosSymbol
	 *            the phosphorylation symbol (e.g. #)
	 * @param neutralSymbol
	 *            the phosphorylation site with the lost phosphate. If this is
	 *            MS2, this can be ((char)0)
	 */
	public SequestPhosphoPeptide(SequestScanName scanname, String sequence,
	        short charge, short rsp, double mh, double deltaMs, short rank,
	        float dcn, float xcorr, float sp, String ions,
	        HashSet<ProteinReference> refs,ISequestPeptideFormat<?> formatter, char phosSymbol, char neutralSymbol) {
		super(scanname, sequence, charge, rsp, mh, deltaMs, rank, dcn, xcorr,
		        sp, ions, refs, formatter);
		this.phosSymbol = phosSymbol;
		this.neutralSymbol = neutralSymbol;
	}

	/**
	 * Create a peptide from the values
	 * 
	 * @param scanname
	 *            the sequestscan name instance.
	 * @param sequence
	 *            sequece with terminals
	 * @param rank
	 *            rank of this peptide
	 * @param charge
	 * @param rsp
	 * @param mh
	 * @param dcn
	 * @param xcorr
	 * @param sp
	 * @param ions
	 * @param refs
	 *            references from out (Null not permitted ) * @param phosSymbol
	 *            the phosphorylation symbol (e.g. #)
	 * @param neutralSymbol
	 *            the phosphorylation site with the lost phosphate. If this is
	 *            MS2, this can be ((char)0)
	 */
	public SequestPhosphoPeptide(String baseName, int scanNumBeg,
	        int scanNumEnd, String sequence, short charge, short rsp,
	        double mh, double deltaMs, short rank, float dcn, float xcorr,
	        float sp, String ions, HashSet<ProteinReference> refs,ISequestPeptideFormat<?> formatter, char phosSymbol,
	        char neutralSymbol) {
		super(baseName, scanNumBeg, scanNumEnd, sequence, charge, rsp, mh,
		        deltaMs, rank, dcn, xcorr, sp, ions, refs, formatter);
		this.phosSymbol = phosSymbol;
		this.neutralSymbol = neutralSymbol;
	}

	/**
	 * 
	 * Peptide from exported file of Bioworks
	 * 
	 * @param scanNum
	 * @param sequence
	 * @param charge
	 * @param mh
	 * @param deltaMs
	 * @param dcn
	 * @param xcorr
	 * @param sp
	 * @param rsp
	 * @param ions
	 * @param refs
	 * @param phosSymbol
	 *            the phosphorylation symbol (e.g. #)
	 * @param neutralSymbol
	 *            the phosphorylation site with the lost phosphate. If this is
	 *            MS2, this can be ((char)0)
	 */
	public SequestPhosphoPeptide(String scanNum, String sequence, short charge,
	        double mh, double deltaMs, short rank, float dcn, float xcorr,
	        float sp, short rsp, String ions, float sim,
	        HashSet<ProteinReference> refs, short numofterms, float pi,
	        ISequestPeptideFormat<?> formatter, char phosSymbol, char neutralSymbol) {
		super(scanNum, sequence, charge, mh, deltaMs, rank, dcn, xcorr, sp,
		        rsp, ions, sim, refs, numofterms, pi, formatter);
		this.phosSymbol = phosSymbol;
		this.neutralSymbol = neutralSymbol;
	}

	/**
	 * 
	 * Mainly for the PeptideFormater input
	 * 
	 * @param scanNum
	 * @param sequence
	 * @param charge
	 * @param mh
	 * @param deltaMs
	 * @param dcn
	 * @param xcorr
	 * @param sp
	 * @param rsp
	 * @param ions
	 * @param sim
	 * @param refs
	 * @param numofterms
	 * @param probability
	 * @param phosSymbol
	 *            the phosphorylation symbol (e.g. #)
	 * @param neutralSymbol
	 *            the phosphorylation site with the lost phosphate. If this is
	 *            MS2, this can be ((char)0)
	 */
	public SequestPhosphoPeptide(String scanNum, String sequence, short charge,
	        short rsp, double mh, double deltaMs, short rank, float dcn,
	        float xcorr, float sp, String ions, HashSet<ProteinReference> refs,ISequestPeptideFormat<?> formatter,
	        char phosSymbol, char neutralSymbol) {
		super(scanNum, sequence, charge, rsp, mh, deltaMs, rank, dcn, xcorr,
		        sp, ions, refs, formatter);
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
