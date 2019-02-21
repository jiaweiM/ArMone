/* 
 ******************************************************************************
 * File: MascotPhosphoPeptidePair.java * * * Created on 02-18-2009
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
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.IOMSSAPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.TScores;

/**
 * 
 * @author Xinning
 * @version 0.1, 02-18-2009, 10:18:04
 */
public class OMSSAPhosphoPeptidePair extends AbstractPhosphoPeptidePair
        implements IOMSSAPhosphoPeptidePair {

	/**
	 * The enzyme (default trypsin)
	 */
	public static Enzyme enzyme = Enzyme.TRYPSIN;

	private double pvaluems2, pvaluems3, pvalue;
	private double evaluems2, evaluems3, evalue;

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
	 * @param evaluems2
	 * @param evaluems3
	 * @param tscores
	 * @param formatter
	 */
	public OMSSAPhosphoPeptidePair(String baseName, int scanNumMS2,
	        int scanNumMS3, IPhosphoPeptideSequence phosSeq_neu, int neuloc,
	        short charge, double mh_ms2, double deltaMs_ms2, double mz_ms2,
	        double mz_ms3, short rank_max, HashSet<ProteinReference> refs,
	        float pi, short numofTerm, double pvaluems2, double pvaluems3,
	        double pvalue, double evaluems2, double evaluems3, double evalue,
	        TScores tscores, IOMSSAPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(baseName, scanNumMS2, scanNumMS3, phosSeq_neu, neuloc, charge,
		        mh_ms2, deltaMs_ms2, mz_ms2, mz_ms3, rank_max, refs, pi,
		        numofTerm, tscores, formatter);

		this.pvalue = pvalue;
		this.pvaluems2 = pvaluems2;
		this.pvaluems3 = pvaluems3;
		this.evaluems2 = evaluems2;
		this.evaluems3 = evaluems3;
		this.evalue = evalue;
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
	public OMSSAPhosphoPeptidePair(String scannum,
	        IPhosphoPeptideSequence phosSeq_neu, int neuloc, short charge,
	        double mh_ms2, double deltaMs_ms2, double mz_ms2, double mz_ms3,
	        short rank_max, HashSet<ProteinReference> refs, float pi,
	        short numofTerm, double pvaluems2, double pvaluems3, double pvalue,
	        double evaluems2, double evaluems3, double evalue, TScores tscores,
	        IOMSSAPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(scannum, phosSeq_neu, neuloc, charge, mh_ms2, deltaMs_ms2,
		        mz_ms2, mz_ms3, rank_max, refs, pi, numofTerm, tscores,
		        formatter);

		this.pvalue = pvalue;
		this.pvaluems2 = pvaluems2;
		this.pvaluems3 = pvaluems3;
		this.evaluems2 = evaluems2;
		this.evaluems3 = evaluems3;
		this.evalue = evalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IOMSSAPhosphoPeptidePair#getEvalueMS2()
	 */
	@Override
	public double getMS2Evalue() {
		return evaluems2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IOMSSAPhosphoPeptidePair#getEvalueMS3()
	 */
	@Override
	public double getMS3Evalue() {
		return this.evaluems3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IOMSSAPhosphoPeptidePair#getPvalue()
	 */
	@Override
	public double getPvalue() {
		return pvalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IOMSSAPhosphoPeptidePair#getPvalueMS2()
	 */
	@Override
	public double getMS2Pvalue() {
		return pvaluems2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IOMSSAPhosphoPeptidePair#getPvalueMS3()
	 */
	@Override
	public double getMS3Pvalue() {
		return pvaluems3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPrimaryScore()
	 */
	@Override
	public float getPrimaryScore() {
		return (float) -Math.log10(evalue);
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
		return PeptideType.APIVASE_MASCOT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IMascotPhosphoPeptidePair#getEvalue()
	 */
	@Override
	public double getEvalue() {
		return this.evalue;
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
