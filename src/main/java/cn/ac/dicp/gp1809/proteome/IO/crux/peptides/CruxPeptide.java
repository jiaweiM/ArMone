/*
 ******************************************************************************
 * File: CruxPeptide.java * * * Created on 04-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.crux.peptides;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.AbstractPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;

/**
 * Peptide identified by CruxPeptide implements IPeptide
 * 
 * @author Xinning
 * @version 0.1.1, 05-02-2010, 11:19:46
 */
public class CruxPeptide extends AbstractPeptide implements ICruxPeptide {

	/**
	 * The enzyme used for the protein digestion. The mainly usage is to
	 * determin the ntt value of the peptides. The default enzyme was set as
	 * trypsin(KR/P).
	 */
	public static Enzyme enzyme = Enzyme.TRYPSIN;

	private short rxc;
	private short rsp;
	private String ions;
	private float ionPercent = -1f;

	private float xcorr;
	private float deltaCn;
	private float sp;
	private float pvalue;
	private float qvalue;
	private float percolatorscore;

	/**
	 * Construct a same peptide from the original one
	 * 
	 * @param pep
	 */
	public CruxPeptide(CruxPeptide pep) {
		this(pep.getScanNum(), pep.getSequence(), pep.getCharge(), pep
		        .getRank(), pep.getMH(), pep.getDeltaMH(), pep.getXcorr(), pep
		        .getDeltaCn(), pep.getSp(), pep.getPValue(), pep.getQValue(),
		        pep.getPercolator_score(), pep.getRxc(), pep.getRsp(), pep
		                .getIons(), pep.getProteinReferences(), pep
		                .getNumberofTerm(), pep.getPI(),
		        (ICruxPeptideFormat) pep.getPeptideFormat());

		this.setProbability(pep.getProbabilty());
		this.setEnzyme(pep.getEnzyme());
	}

	/**
	 * If a score has no value, just set as NaN
	 * 
	 * @param scanNum
	 *            the formatted scan name
	 * @param sequence
	 * @param charge
	 * @param rank
	 * @param mh
	 * @param deltaMs
	 * @param xcorr
	 * @param dcn
	 * @param sp
	 * @param pvalue
	 * @param qvalue
	 * @param percolatorvalue
	 * @param rxc
	 * @param rsp
	 * @param ions
	 * @param refs
	 * @param formatter
	 */
	public CruxPeptide(String scanNum, String sequence, short charge,
	        short rank, double mh, double deltaMs, float xcorr, float dcn,
	        float sp, float pvalue, float qvalue, float percolatorvalue,
	        short rxc, short rsp, String ions, HashSet<ProteinReference> refs,
	        short numofterms, float pi, ICruxPeptideFormat formatter) {

		super(scanNum, sequence, charge, mh, deltaMs, rank, refs, formatter);

		this.rxc = rxc;
		this.rsp = rsp;
		this.ions = ions;

		this.setPI(pi);
		this.setNumberofTerm(numofterms);

		this.deltaCn = dcn;
		this.xcorr = xcorr;
		this.sp = sp;
		this.pvalue = pvalue;
		this.qvalue = qvalue;
		this.percolatorscore = percolatorvalue;
	}

	/**
	 * If a score has no value, just set as NaN
	 * 
	 * @param baseName
	 * @param scanNumBeg
	 * @param scanNumEnd
	 * @param sequence
	 * @param charge
	 * @param rank
	 * @param mh
	 * @param deltaMs
	 * @param xcorr
	 * @param dcn
	 * @param sp
	 * @param pvalue
	 * @param qvalue
	 * @param percolatorvalue
	 * @param rxc
	 * @param rsp
	 * @param ions
	 * @param refs
	 * @param formatter
	 */
	public CruxPeptide(String baseName, int scanNumBeg, int scanNumEnd,
	        String sequence, short charge, short rank, double mh,
	        double deltaMs, float xcorr, float dcn, float sp, float pvalue,
	        float qvalue, float percolatorvalue, short rxc, short rsp,
	        String ions, HashSet<ProteinReference> refs,
	        ICruxPeptideFormat formatter) {

		super(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
		        rank, refs, formatter);

		this.rxc = rxc;
		this.rsp = rsp;
		this.ions = ions;

		this.deltaCn = dcn;
		this.xcorr = xcorr;
		this.sp = sp;
		this.pvalue = pvalue;
		this.qvalue = qvalue;
		this.percolatorscore = percolatorvalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide#getDeltaCn()
	 */
	@Override
	public float getDeltaCn() {
		return this.deltaCn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide#getIonPercent()
	 */
	@Override
	public float getIonPercent() {
		if (this.ionPercent < 0f)
			this.ionPercent = getIonPercent(this.ions);

		return this.ionPercent;
	}

	/**
	 * Compute float value of matched ion percent
	 * 
	 * @param ions
	 * @return value of ion percent;
	 */
	public static final float getIonPercent(String ions) {
		int point = ions.indexOf('/');
		float match = Float.parseFloat(ions.substring(0, point));
		float total = Float.parseFloat(ions.substring(point + 1));

		return match / total;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide#getIons()
	 */
	@Override
	public String getIons() {
		return this.ions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide#getPValue()
	 */
	@Override
	public float getPValue() {
		return this.pvalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide#getRsp()
	 */
	@Override
	public short getRsp() {
		return this.rsp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide#getSp()
	 */
	@Override
	public float getSp() {
		return this.sp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide#getXcorr()
	 */
	@Override
	public float getXcorr() {
		return this.xcorr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide#getPercolator_score
	 * ()
	 */
	@Override
	public float getPercolator_score() {
		return this.percolatorscore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide#getQValue()
	 */
	@Override
	public float getQValue() {
		return this.qvalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide#getRxc()
	 */
	@Override
	public short getRxc() {
		return this.rxc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getEnzyme()
	 */
	@Override
	public Enzyme getEnzyme() {
		return enzyme;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		return PeptideType.CRUX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPrimaryScore()
	 */
	@Override
	public float getPrimaryScore() {
		return 0;
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

		if (format instanceof ICruxPeptideFormat<?>) {
			super.setPeptideFormat(format);
		} else
			throw new IllegalArgumentException(
			        "The formater for set must be Crux formater");
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
