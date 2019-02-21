/* 
 ******************************************************************************
 * File: XTandemPhosphoPeptidePair.java * * * Created on 02-18-2009
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
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.IXTandemPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.TScores;

/**
 * XTandem phosphopeptide pair
 * 
 * @author Xinning
 * @version 0.1, 02-18-2009, 10:18:04
 */
public class XTandemPhosphoPeptidePair extends AbstractPhosphoPeptidePair
        implements IXTandemPhosphoPeptidePair {

	/**
	 * The enzyme (default trypsin)
	 */
	public static Enzyme enzyme = Enzyme.TRYPSIN;

	private float hyperscorems2, hyperscorems3;
	private float hyperscore;
	private double evaluems2, evaluems3, evalue;

	public XTandemPhosphoPeptidePair(String baseName, int scanNumMS2,
	        int scanNumMS3, IPhosphoPeptideSequence phosSeq_neu, int neuloc,
	        short charge, double mh_ms2, double deltaMs_ms2, double mz_ms2,
	        double mz_ms3, short rank_max, HashSet<ProteinReference> refs,
	        float pi, short numofTerm, float hyperscorems2,
	        float hyperscorems3, float hyperscore, double evaluems2,
	        double evaluems3, double evalue, TScores tscores,
	        IXTandemPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(baseName, scanNumMS2, scanNumMS3, phosSeq_neu, neuloc, charge,
		        mh_ms2, deltaMs_ms2, mz_ms2, mz_ms3, rank_max, refs, pi,
		        numofTerm, tscores, formatter);

		this.hyperscorems2 = hyperscorems2;
		this.hyperscorems3 = hyperscorems3;
		this.hyperscore = hyperscore;
		this.evaluems2 = evaluems2;
		this.evaluems3 = evaluems3;
		this.evalue = evalue;
	}

	public XTandemPhosphoPeptidePair(String scannum,
	        IPhosphoPeptideSequence phosSeq_neu, int neuloc, short charge,
	        double mh_ms2, double deltaMs_ms2, double mz_ms2, double mz_ms3,
	        short rank_max, HashSet<ProteinReference> refs, float pi,
	        short numofTerm, float hyperscorems2, float hyperscorems3,
	        float hyperscore, double evaluems2, double evaluems3,
	        double evalue, TScores tscores, IXTandemPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(scannum, phosSeq_neu, neuloc, charge, mh_ms2, deltaMs_ms2,
		        mz_ms2, mz_ms3, rank_max, refs, pi, numofTerm, tscores,
		        formatter);

		this.hyperscorems2 = hyperscorems2;
		this.hyperscorems3 = hyperscorems3;
		this.hyperscore = hyperscore;
		this.evaluems2 = evaluems2;
		this.evaluems3 = evaluems3;
		this.evalue = evalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IMascotPhosphoPeptidePair#getMS2Evalue()
	 */
	@Override
	public double getMS2Evalue() {
		return this.evaluems2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IMascotPhosphoPeptidePair#getMS3Evalue()
	 */
	@Override
	public double getMS3Evalue() {
		return this.evaluems3;
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
		return PeptideType.APIVASE_XTANDEM;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPrimaryScore()
	 */
	@Override
	public float getPrimaryScore() {
		return this.hyperscore;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IXTandemPhosphoPeptidePair#getHyperScore()
	 */
	@Override
	public float getHyperScore() {
		return hyperscore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IXTandemPhosphoPeptidePair#getMS2HyperScore()
	 */
	@Override
	public float getMS2HyperScore() {
		return hyperscorems2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.
	 * IXTandemPhosphoPeptidePair#getMS3HyperScore()
	 */
	@Override
	public float getMS3HyperScore() {
		return hyperscorems3;
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
