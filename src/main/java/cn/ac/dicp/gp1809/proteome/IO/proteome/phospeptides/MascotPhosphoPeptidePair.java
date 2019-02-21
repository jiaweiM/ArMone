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
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.IMascotPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.TScores;

/**
 * The mascot phosphopeptide pair
 * 
 * @author Xinning
 * @version 0.1.2, 06-16-2009, 21:15:59
 */
public class MascotPhosphoPeptidePair extends AbstractPhosphoPeptidePair
        implements IMascotPhosphoPeptidePair {

	/**
	 * The enzyme (default trypsin)
	 */
	public static Enzyme enzyme = Enzyme.TRYPSIN;

	private float ionscorems2, ionscorems3;
	private float ionscore; 
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
	public MascotPhosphoPeptidePair(String baseName, int scanNumMS2,
	        int scanNumMS3, IPhosphoPeptideSequence phosSeq_neu, int neuloc,
	        short charge, double mh_ms2, double deltaMs_ms2, double mz_ms2,
	        double mz_ms3, short rank_max, HashSet<ProteinReference> refs,
	        float pi, short numofTerm, float ionscorems2, float ionscorems3,
	        float ionscore, double evaluems2, double evaluems3, double evalue, TScores tscores,
	        IMascotPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(baseName, scanNumMS2, scanNumMS3, phosSeq_neu, neuloc, charge,
		        mh_ms2, deltaMs_ms2, mz_ms2, mz_ms3, rank_max, refs, pi,
		        numofTerm, tscores, formatter);

		this.ionscorems2 = ionscorems2;
		this.ionscorems3 = ionscorems3;
		this.ionscore = ionscore;
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
	public MascotPhosphoPeptidePair(String scannum,
	        IPhosphoPeptideSequence phosSeq_neu, int neuloc, short charge,
	        double mh_ms2, double deltaMs_ms2, double mz_ms2, double mz_ms3,
	        short rank_max, HashSet<ProteinReference> refs, float pi,
	        short numofTerm, float ionscorems2, float ionscorems3,
	        float ionscore, double evaluems2, double evaluems3, double evalue, TScores tscores,
	        IMascotPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(scannum, phosSeq_neu, neuloc, charge, mh_ms2, deltaMs_ms2,
		        mz_ms2, mz_ms3, rank_max, refs, pi, numofTerm, tscores,
		        formatter);

		this.ionscorems2 = ionscorems2;
		this.ionscorems3 = ionscorems3;
		this.ionscore = ionscore;
		this.evaluems2 = evaluems2;
		this.evaluems3 = evaluems3;
		this.evalue = evalue;
	}
	
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
	public MascotPhosphoPeptidePair(String baseName, int scanNumMS2,
	        int scanNumMS3, IPhosphoPeptideSequence phosSeq_neu, int neuloc,
	        short charge, double mh_ms2, double deltaMs_ms2, double mz_ms2,
	        double mz_ms3, short rank_max, HashSet<ProteinReference> refs,
	        float pi, short numofTerm, float ionscorems2, float ionscorems3,
	        float ionscore, double evaluems2, double evaluems3, double evalue, double[] ascores,
	        IMascotPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(baseName, scanNumMS2, scanNumMS3, phosSeq_neu, neuloc, charge,
		        mh_ms2, deltaMs_ms2, mz_ms2, mz_ms3, rank_max, refs, pi,
		        numofTerm, ascores, formatter);

		this.ionscorems2 = ionscorems2;
		this.ionscorems3 = ionscorems3;
		this.ionscore = ionscore;
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
	public MascotPhosphoPeptidePair(String scannum,
	        IPhosphoPeptideSequence phosSeq_neu, int neuloc, short charge,
	        double mh_ms2, double deltaMs_ms2, double mz_ms2, double mz_ms3,
	        short rank_max, HashSet<ProteinReference> refs, float pi,
	        short numofTerm, float ionscorems2, float ionscorems3,
	        float ionscore, double evaluems2, double evaluems3, double evalue, double[] ascores,
	        IMascotPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(scannum, phosSeq_neu, neuloc, charge, mh_ms2, deltaMs_ms2,
		        mz_ms2, mz_ms3, rank_max, refs, pi, numofTerm, ascores,
		        formatter);

		this.ionscorems2 = ionscorems2;
		this.ionscorems3 = ionscorems3;
		this.ionscore = ionscore;
		this.evaluems2 = evaluems2;
		this.evaluems3 = evaluems3;
		this.evalue = evalue;
	}

	/* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IMascotPhosphoPeptidePair#getMS2Evalue()
     */
    @Override
    public double getMS2Evalue() {
	    return this.evaluems2;
    }

	/* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IMascotPhosphoPeptidePair#getMS2Ionscore()
     */
    @Override
    public float getMS2Ionscore() {
	    return this.ionscorems2;
    }

	/* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IMascotPhosphoPeptidePair#getMS3Evalue()
     */
    @Override
    public double getMS3Evalue() {
	    return this.evaluems3;
    }

	/* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IMascotPhosphoPeptidePair#getMS3Ionscore()
     */
    @Override
    public float getMS3Ionscore() {
	    return this.ionscorems3;
    }

	/* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IMascotPhosphoPeptidePair#getSumIonscore()
     */
    @Override
    public float getIonscore() {
	    return this.ionscore;
    }

	/* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getEnzyme()
     */
    @Override
    public Enzyme getEnzyme() {
	    return enzyme;
    }

	/* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPeptideType()
     */
    @Override
    public PeptideType getPeptideType() {
	    return PeptideType.APIVASE_MASCOT;
    }

	/* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPrimaryScore()
     */
    @Override
    public float getPrimaryScore() {
	    return this.ionscore;
    }

    /*
     * (non-Javadoc)
     * @see cn.ac.dicp.gp1809.APIVASEII.phospep.instances.peptidePairs.IMascotPhosphoPeptidePair#getEvalue()
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
