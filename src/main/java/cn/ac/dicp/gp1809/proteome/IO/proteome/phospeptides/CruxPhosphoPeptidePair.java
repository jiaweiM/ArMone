/*
 ******************************************************************************
 * File: CruxPhosphoPeptidePair.java * * * Created on 04-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.ICruxPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.TScores;

/**
 * The curx phospho peptide pair
 * 
 * @author Xinning
 * @version 0.1, 04-02-2009, 23:07:04
 */
public class CruxPhosphoPeptidePair extends AbstractPhosphoPeptidePair
        implements ICruxPhosphoPeptidePair {

	/**
	 * The enzyme (default trypsin)
	 */
	public static Enzyme enzyme = Enzyme.TRYPSIN;

	private float xcorrms2, xcorrms3;
	private float xcorrs;
	private float detaCnm;
	private float pvaluems2, pvaluems3;
	private float pvalue;
	private float qvaluems2, qvaluems3;
	private float qvalue;
	private float perscorems2, perscorems3;
	private float perscore;

	/**
	 * 
	 * @param baseName
	 * @param scanNumMS2
	 * @param scanNumMS3
	 * @param phosSeq_neu
	 * @param neuloc
	 * @param charge
	 * @param mh_ms2
	 * @param deltaMs_ms2
	 * @param rank_max
	 * @param refs
	 * @param pi
	 * @param numofTerm
	 * @param xcorrms2
	 * @param xcorrms3
	 * @param xcorrs
	 *            xcorr norm
	 * @param deltaCnm
	 *            dcn norm
	 * @param tscores
	 * @param formatter
	 */
	public CruxPhosphoPeptidePair(String baseName, int scanNumMS2,
	        int scanNumMS3, IPhosphoPeptideSequence phosSeq_neu, int neuloc,
	        short charge, double mh_ms2, double deltaMs_ms2, double mz_ms2,
	        double mz_ms3, short rank_max, HashSet<ProteinReference> refs,
	        float pi, short numofTerm, float xcorrms2, float xcorrms3,
	        float xcorrs, float deltaCnm, float pvaluems2, float pvaluems3,
	        float pvalue, float qvaluems2, float qvaluems3, float qvalue,
	        float perscorems2, float perscorems3, float perscore,
	        TScores tscores, ICruxPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(baseName, scanNumMS2, scanNumMS3, phosSeq_neu, neuloc, charge,
		        mh_ms2, deltaMs_ms2, mz_ms2, mz_ms3, rank_max, refs, pi,
		        numofTerm, tscores, formatter);

		this.detaCnm = deltaCnm;
		this.xcorrms2 = xcorrms2;
		this.xcorrms3 = xcorrms3;
		this.xcorrs = xcorrs;

		this.pvalue = pvalue;
		this.pvaluems2 = pvaluems2;
		this.pvaluems3 = pvaluems3;

		this.qvalue = qvalue;
		this.qvaluems2 = qvaluems2;
		this.qvaluems3 = qvaluems3;

		this.perscore = perscore;
		this.perscorems2 = perscorems2;
		this.perscorems3 = perscorems3;
	}

	/**
	 * 
	 * @param scannum
	 * @param phosSeq_neu
	 * @param neuloc
	 * @param charge
	 * @param mh_ms2
	 * @param deltaMs_ms2
	 * @param rank_max
	 * @param refs
	 * @param pi
	 * @param numofTerm
	 * @param xcorrms2
	 * @param xcorrms3
	 * @param xcorrs
	 * @param deltaCnm
	 * @param tscores
	 * @param formatter
	 */
	public CruxPhosphoPeptidePair(String scannum,
	        IPhosphoPeptideSequence phosSeq_neu, int neuloc, short charge,
	        double mh_ms2, double deltaMs_ms2, double mz_ms2, double mz_ms3,
	        short rank_max, HashSet<ProteinReference> refs, float pi,
	        short numofTerm, float xcorrms2, float xcorrms3, float xcorrs,
	        float deltaCnm, float pvaluems2, float pvaluems3, float pvalue,
	        float qvaluems2, float qvaluems3, float qvalue, float perscorems2,
	        float perscorems3, float perscore, TScores tscores,
	        ICruxPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(scannum, phosSeq_neu, neuloc, charge, mh_ms2, deltaMs_ms2,
		        mz_ms2, mz_ms3, rank_max, refs, pi, numofTerm, tscores,
		        formatter);

		this.detaCnm = deltaCnm;
		this.xcorrms2 = xcorrms2;
		this.xcorrms3 = xcorrms3;
		this.xcorrs = xcorrs;

		this.pvalue = pvalue;
		this.pvaluems2 = pvaluems2;
		this.pvaluems3 = pvaluems3;

		this.qvalue = qvalue;
		this.qvaluems2 = qvaluems2;
		this.qvaluems3 = qvaluems3;

		this.perscore = perscore;
		this.perscorems2 = perscorems2;
		this.perscorems3 = perscorems3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.instances.ISequestPhosphoPeptidePair
	 * #getMS2Xcorr()
	 */
	@Override
	public float getMS2Xcorr() {
		return this.xcorrms2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.instances.ISequestPhosphoPeptidePair
	 * #getMS3Xcorr()
	 */
	@Override
	public float getMS3Xcorr() {
		return this.xcorrms3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.instances.ISequestPhosphoPeptidePair
	 * #getXcorrSum()
	 */
	@Override
	public float getXcorrSum() {
		return this.xcorrs;
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
		return PeptideType.APIVASE_CRUX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPrimaryScore()
	 */
	@Override
	public float getPrimaryScore() {

		if (!Float.isNaN(this.perscore))
			return this.perscore;

		if (!Float.isNaN(this.pvalue))
			return this.perscore;

		return this.xcorrs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * ISequestPhosphoPeptidePair#getDeltaCn()
	 */
	@Override
	public float getDeltaCn() {
		return this.detaCnm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * ICruxPhosphoPeptidePair#getMS2PercolatorScore()
	 */
	@Override
	public float getMS2PercolatorScore() {
		return this.perscorems2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * ICruxPhosphoPeptidePair#getMS2Pvalue()
	 */
	@Override
	public float getMS2Pvalue() {
		return this.pvaluems2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * ICruxPhosphoPeptidePair#getMS2Qvalue()
	 */
	@Override
	public float getMS2Qvalue() {
		return this.qvaluems2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * ICruxPhosphoPeptidePair#getMS3PercolatorScore()
	 */
	@Override
	public float getMS3PercolatorScore() {
		return this.perscorems3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * ICruxPhosphoPeptidePair#getMS3Pvalue()
	 */
	@Override
	public float getMS3Pvalue() {
		return this.pvaluems3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * ICruxPhosphoPeptidePair#getMS3Qvalue()
	 */
	@Override
	public float getMS3Qvalue() {
		return this.qvaluems3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * ICruxPhosphoPeptidePair#getPercolatorScoreSum()
	 */
	@Override
	public float getPercolatorScoreSum() {
		return this.perscore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * ICruxPhosphoPeptidePair#getPvalueSum()
	 */
	@Override
	public float getPvalueSum() {
		return this.pvalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * ICruxPhosphoPeptidePair#getQvalueSum()
	 */
	@Override
	public float getQvalueSum() {
		return this.qvalue;
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
