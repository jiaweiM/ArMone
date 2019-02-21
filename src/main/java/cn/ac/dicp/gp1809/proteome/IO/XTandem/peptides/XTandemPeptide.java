/*
 * *****************************************************************************
 * File: XTandemPeptide.java * * * Created on 10-06-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.AbstractPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;

/**
 * Peptide identified by XTandem implements IPeptide
 * 
 * @author Xinning
 * @version 0.2.6, 05-02-2010, 10:35:40
 */
public class XTandemPeptide extends AbstractPeptide implements IXTandemPeptide {
	
	// The E-value
	private double evalue;
	// The hyperscore
	private float hyperscore;
	// The next hyperscore
	private float nextscore;

	private float primScore;
	
	private float yScore;
	
	private float bScore;

	public XTandemPeptide(IXTandemPeptide pep) {
		
		this(pep.getScanNum(), pep.getSequence(), pep.getCharge(), pep.getMH(),
		        pep.getDeltaMH(), pep.getRank(), pep.getEvalue(), pep
		                .getHyperscore(), pep.getNextHyperscore(), pep.getYScore(), pep.getBScore(),
		                pep.getProteinReferences(), (IXTandemPeptideFormat<?>) pep
		                .getPeptideFormat());
		
		this.setProbability(pep.getProbabilty());
		this.setEnzyme(pep.getEnzyme());
	}

	public XTandemPeptide(String baseName, int scanNumBeg, int scanNumEnd,
	        String sequence, short charge, double mh, double deltaMs,
	        short rank, double evalue, float hyperscore, float nextscore, float yscore, float bscore, 
	        HashSet<ProteinReference> refs, IXTandemPeptideFormat<?> formatter) {

		super(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
		        rank, refs, formatter);

		this.setEvalue(evalue);
		this.hyperscore = hyperscore;
		this.nextscore = nextscore;
		this.yScore = yscore;
		this.bScore = bscore;
	}

	public XTandemPeptide(String scanNum, String sequence, short charge,
	        double mh, double deltaMs, short rank, double evalue,
	        float hyperscore, float nextscore,  float yscore, float bscore, HashSet<ProteinReference> refs,
	        IXTandemPeptideFormat<?> formatter) {
		super(scanNum, sequence, charge, mh, deltaMs, rank, refs, formatter);

		this.setEvalue(evalue);
		this.hyperscore = hyperscore;
		this.nextscore = nextscore;
		this.yScore = yscore;
		this.bScore = bscore;
	}

	public XTandemPeptide(String baseName, int scanNumBeg, int scanNumEnd,
	        String sequence, short charge, double mh, double deltaMs,
	        short rank, double evalue, float hyperscore, float nextscore, float yscore, float bscore, 
	        HashSet<ProteinReference> refs, float pi, short numofTerms,
	        IXTandemPeptideFormat<?> formatter) {

		this(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
		        rank, evalue, hyperscore, nextscore, yscore, bscore, refs, formatter);

		this.setPI(pi);
		this.setNumberofTerm(numofTerms);
	}

	public XTandemPeptide(String scanNum, String sequence, short charge,
	        double mh, double deltaMs, short rank, double evalue,
	        float hyperscore, float nextscore,  float yscore, float bscore, HashSet<ProteinReference> refs,
	        float pi, short numofTerms, IXTandemPeptideFormat<?> formatter) {
		
		this(scanNum, sequence, charge, mh, deltaMs, rank, evalue, hyperscore,
		        nextscore, yscore, bscore, refs, formatter);

		this.setPI(pi);
		this.setNumberofTerm(numofTerms);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.IOMSSAPeptide#getEvalue()
	 */
	public final double getEvalue() {
		return evalue;
	}

	/**
	 * @param evalue
	 *            the expected value to set
	 */
	public final void setEvalue(double evalue) {
		this.evalue = evalue;
		this.primScore = (float) -Math.log(evalue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.IOMSSAPeptide#getPvalue()
	 */
	public final float getHyperscore() {
		return hyperscore;
	}

	/**
	 * @param pvalue
	 *            the hyperscore value to set
	 */
	public final void setHyperscore(float hyperscore) {
		this.hyperscore = hyperscore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides
	 * .IXTandemPeptide#getNextHyperscore()
	 */
	public final float getNextHyperscore() {
		return this.nextscore;
	}

	/**
	 * @param nextscore
	 *            next hyperscore to be set.
	 */
	public final void setNextHyperscore(float nextscore) {
		this.nextscore = nextscore;
	}

	public final float getYScore(){
		return yScore;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.IXTandemPeptide#getBScore()
	 */
	@Override
	public float getBScore() {
		// TODO Auto-generated method stub
		return bScore;
	}

	public void setYScore(float yScore){
		this.yScore = yScore;
	}
	
	public void setBScore(float bScore){
		this.bScore = bScore;
	}
	
	/**
	 * =-ln(evalue);
	 */
	@Override
	public float getPrimaryScore() {
		return this.primScore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		return PeptideType.XTANDEM;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#setPeptideFormat(
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat)
	 */
	@Override
	public void setPeptideFormat(IPeptideFormat<?> format) {
		if (format == null) {
			return;
		}

		if (format instanceof IXTandemPeptideFormat<?>) {
			super.setPeptideFormat(format);
		} else
			throw new IllegalArgumentException(
			        "The formater for set must be X!Tandem formater");
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getInten()
	 */
	@Override
	public double getInten() {
		// TODO Auto-generated method stub
		return 0;
	}

}
