/* 
 ******************************************************************************
 * File: MascotPhosphoPeptide.java * * * Created on 02-17-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.instances;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.PhosphoSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.PhosphoUtil;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequenceUpdateException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;

/**
 * A phosphorylated peptide from mascot
 * 
 * @author Xinning
 * @version 0.1.1, 05-25-2010, 16:28:02
 */
public class MascotPhosphoPeptide extends MascotPeptide implements
        IPhosphoPeptide {

	//The number of phosphorylated sites
	private int phosSite = -1;
	//The phosphosite
	private PhosphoSite[] sites;

	private char phosSymbol;
	private char neutralSymbol;

	/**
	 * Construct from an mascot peptide
	 * 
	 * @param pep
	 * @param phosSymbol
	 * @param neutralSymbol
	 */
	public MascotPhosphoPeptide(IMascotPeptide pep, char phosSymbol,
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
	 * @param ionscore
	 * @param evalue
	 * @param refs
	 * @param phosSymbol
	 *            the phosphorylation symbol (e.g. #)
	 * @param neutralSymbol
	 *            the phosphorylation site with the lost phosphate. If this is
	 *            MS2, this can be ((char)0)
	 */
	public MascotPhosphoPeptide(String baseName, int scanNumBeg,
	        int scanNumEnd, String sequence, short charge, double mh,
	        double deltaMs, short rank, float ionscore, double evalue,
	        HashSet<ProteinReference> refs, IMascotPeptideFormat formatter, char phosSymbol, char neutralSymbol) {
		super(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
		        rank, ionscore, evalue, refs, formatter);
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
	 * @param ionsocre
	 * @param evalue
	 * @param refs
	 */
	public MascotPhosphoPeptide(String scanNum, String sequence, short charge,
	        double mh, double deltaMs, short rank, float ionsocre,
	        double evalue, HashSet<ProteinReference> refs, IMascotPeptideFormat formatter, char phosSymbol,
	        char neutralSymbol) {
		super(scanNum, sequence, charge, mh, deltaMs, rank, ionsocre, evalue,
		        refs, formatter);
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
	 * @param ionsocre
	 * @param evalue
	 * @param refs
	 * @param pi
	 * @param numofTerms
	 * @param phosSymbol
	 *            the phosphorylation symbol (e.g. #)
	 * @param neutralSymbol
	 *            the phosphorylation site with the lost phosphate. If this is
	 *            MS2, this can be ((char)0)
	 */
	public MascotPhosphoPeptide(String baseName, int scanNumBeg,
	        int scanNumEnd, String sequence, short charge, double mh,
	        double deltaMs, short rank, float ionsocre, double evalue,
	        HashSet<ProteinReference> refs, float pi, short numofTerms, IMascotPeptideFormat formatter,
	        char phosSymbol, char neutralSymbol) {
		super(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
		        rank, ionsocre, evalue, refs, formatter);
		this.setPI(pi);
		this.setNumberofTerm(numofTerms);
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
	 * @param ionsocre
	 * @param evalue
	 * @param refs
	 * @param pi
	 * @param numofTerms
	 * @param phosSymbol
	 *            the phosphorylation symbol (e.g. #)
	 * @param neutralSymbol
	 *            the phosphorylation site with the lost phosphate. If this is
	 *            MS2, this can be ((char)0)
	 */
	public MascotPhosphoPeptide(String scanNum, String sequence, short charge,
	        double mh, double deltaMs, short rank, float ionsocre,
	        double evalue, HashSet<ProteinReference> refs, float pi,
	        short numofTerms, IMascotPeptideFormat formatter, char phosSymbol, char neutralSymbol) {
		super(scanNum, sequence, charge, mh, deltaMs, rank, ionsocre, evalue,
		        refs, formatter);
		this.setPI(pi);
		this.setNumberofTerm(numofTerms);
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
