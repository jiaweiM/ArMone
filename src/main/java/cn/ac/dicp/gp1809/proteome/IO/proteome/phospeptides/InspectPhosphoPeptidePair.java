/* 
 ******************************************************************************
 * File: InspectPhosphoPeptidePair.java * * * Created on 03-25-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.IInspectPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.TScores;

/**
 * 
 * @author Xinning
 * @version 0.1, 03-25-2009, 09:07:39
 */
public class InspectPhosphoPeptidePair extends AbstractPhosphoPeptidePair
        implements IInspectPhosphoPeptidePair {

	/**
	 * The enzyme (default trypsin)
	 */
	public static Enzyme enzyme = Enzyme.TRYPSIN;

	private float MQScorems2, MQScorems3;
	private float MQScore;
	private double pvaluems2, pvaluems3, pvalue;

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
	 * @param mz_ms2
	 * @param mz_ms3
	 * @param rank_max
	 * @param refs
	 * @param pi
	 * @param numofTerm
	 * @param ionscorems2
	 * @param ionscorems3
	 * @param ionscore
	 * @param pvaluems2
	 * @param evaluems3
	 * @param tscores
	 * @param formatter
	 */
	public InspectPhosphoPeptidePair(String baseName, int scanNumMS2,
	        int scanNumMS3, IPhosphoPeptideSequence phosSeq_neu, int neuloc,
	        short charge, double mh_ms2, double deltaMs_ms2, double mz_ms2,
	        double mz_ms3, short rank_max, HashSet<ProteinReference> refs,
	        float pi, short numofTerm, float MQScorems2, float MQScorems3,
	        float MQScore, double pvaluems2, double pvaluems3, double pvalue,
	        TScores tscores, IInspectPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(baseName, scanNumMS2, scanNumMS3, phosSeq_neu, neuloc, charge,
		        mh_ms2, deltaMs_ms2, mz_ms2, mz_ms3, rank_max, refs, pi,
		        numofTerm, tscores, formatter);

		this.MQScorems2 = MQScorems2;
		this.MQScorems3 = MQScorems3;
		this.MQScore = MQScore;
		this.pvaluems2 = pvaluems2;
		this.pvaluems3 = pvaluems3;
		this.pvalue = pvalue;
	}

	/**
	 * 
	 * @param scannum
	 * @param phosSeq_neu
	 * @param neuloc
	 * @param charge
	 * @param mh_ms2
	 * @param deltaMs_ms2
	 * @param mz_ms2
	 * @param mz_ms3
	 * @param rank_max
	 * @param refs
	 * @param pi
	 * @param numofTerm
	 * @param ionscorems2
	 * @param ionscorems3
	 * @param ionscore
	 * @param evaluems2
	 * @param evaluems3
	 * @param tscores
	 * @param formatter
	 */
	public InspectPhosphoPeptidePair(String scannum,
	        IPhosphoPeptideSequence phosSeq_neu, int neuloc, short charge,
	        double mh_ms2, double deltaMs_ms2, double mz_ms2, double mz_ms3,
	        short rank_max, HashSet<ProteinReference> refs, float pi,
	        short numofTerm, float MQScorems2, float MQScorems3,
	        float MQScore, double pvaluems2, double pvaluems3, double pvalue,
	        TScores tscores, IInspectPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(scannum, phosSeq_neu, neuloc, charge, mh_ms2, deltaMs_ms2,
		        mz_ms2, mz_ms3, rank_max, refs, pi, numofTerm, tscores,
		        formatter);

		this.MQScorems2 = MQScorems2;
		this.MQScorems3 = MQScorems3;
		this.MQScore = MQScore;
		this.pvaluems2 = pvaluems2;
		this.pvaluems3 = pvaluems3;
		this.pvalue = pvalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IInspectPhosphoPeptidePair#getMS2Pvalue()
	 */
	@Override
	public double getMS2Pvalue() {
		return this.pvaluems2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IInspectPhosphoPeptidePair#getMS2MQScore()
	 */
	@Override
	public float getMS2MQScore() {
		return this.MQScorems2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IInspectPhosphoPeptidePair#getMS3Pvalue()
	 */
	@Override
	public double getMS3Pvalue() {
		return this.pvaluems3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IInspectPhosphoPeptidePair#getMS3MQScore()
	 */
	@Override
	public float getMS3MQScore() {
		return this.MQScorems3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IInspectPhosphoPeptidePair#getMQScore()
	 */
	@Override
	public float getMQScore() {
		return this.MQScore;
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
		return PeptideType.APIVASE_INSPECT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPrimaryScore()
	 */
	@Override
	public float getPrimaryScore() {
		return this.MQScore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IInspectPhosphoPeptidePair#getPvalue()
	 */
	@Override
	public double getPvalue() {
		return this.pvalue;
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
