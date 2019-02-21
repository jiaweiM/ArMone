/* 
 ******************************************************************************
 * File: SequestPhosphoPeptidePair.java * * * Created on 02-18-2009
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
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.ISequestPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.TScores;

/**
 * The sequence phospho peptide pair
 * 
 * @author Xinning
 * @version 0.1.1, 06-12-2009, 20:56:25
 */
public class SequestPhosphoPeptidePair extends AbstractPhosphoPeptidePair
        implements ISequestPhosphoPeptidePair {

	/**
	 * The enzyme (default trypsin)
	 */
	public static Enzyme enzyme = Enzyme.TRYPSIN;

	private float xcorrms2, xcorrms3;
	private float xcorrs;
	private float detaCnm;

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
	 * @param xcorrs xcorr norm
	 * @param deltaCnm dcn norm
	 * @param tscores
	 * @param formatter
	 */
	public SequestPhosphoPeptidePair(String baseName, int scanNumMS2,
	        int scanNumMS3, IPhosphoPeptideSequence phosSeq_neu, int neuloc,
	        short charge, double mh_ms2, double deltaMs_ms2,double mz_ms2,
	        double mz_ms3, short rank_max,
	        HashSet<ProteinReference> refs, float pi, short numofTerm,
	        float xcorrms2, float xcorrms3, float xcorrs, float deltaCnm,
	        TScores tscores, ISequestPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(baseName, scanNumMS2, scanNumMS3, phosSeq_neu, neuloc, charge,
		        mh_ms2, deltaMs_ms2, mz_ms2, mz_ms3, rank_max, refs, pi, numofTerm, tscores,
		        formatter);

		this.detaCnm = deltaCnm;
		this.xcorrms2 = xcorrms2;
		this.xcorrms3 = xcorrms3;
		this.xcorrs = xcorrs;
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
	public SequestPhosphoPeptidePair(String scannum,
	        IPhosphoPeptideSequence phosSeq_neu, int neuloc, short charge,
	        double mh_ms2, double deltaMs_ms2, double mz_ms2,
	        double mz_ms3,short rank_max,
	        HashSet<ProteinReference> refs, float pi, short numofTerm,
	        float xcorrms2, float xcorrms3, float xcorrs, float deltaCnm,
	        TScores tscores, ISequestPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(scannum, phosSeq_neu, neuloc, charge, mh_ms2, deltaMs_ms2, mz_ms2, mz_ms3,
		        rank_max, refs, pi, numofTerm, tscores, formatter);

		this.detaCnm = deltaCnm;
		this.xcorrms2 = xcorrms2;
		this.xcorrms3 = xcorrms3;
		this.xcorrs = xcorrs;
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
	 * @param rank_max
	 * @param refs
	 * @param pi
	 * @param numofTerm
	 * @param xcorrms2
	 * @param xcorrms3
	 * @param xcorrs xcorr norm
	 * @param deltaCnm dcn norm
	 * @param tscores
	 * @param formatter
	 */
	public SequestPhosphoPeptidePair(String baseName, int scanNumMS2,
	        int scanNumMS3, IPhosphoPeptideSequence phosSeq_neu, int neuloc,
	        short charge, double mh_ms2, double deltaMs_ms2,double mz_ms2,
	        double mz_ms3, short rank_max,
	        HashSet<ProteinReference> refs, float pi, short numofTerm,
	        float xcorrms2, float xcorrms3, float xcorrs, float deltaCnm,
	        double[] ascores, ISequestPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(baseName, scanNumMS2, scanNumMS3, phosSeq_neu, neuloc, charge,
		        mh_ms2, deltaMs_ms2, mz_ms2, mz_ms3, rank_max, refs, pi, numofTerm, ascores,
		        formatter);

		this.detaCnm = deltaCnm;
		this.xcorrms2 = xcorrms2;
		this.xcorrms3 = xcorrms3;
		this.xcorrs = xcorrs;
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
	public SequestPhosphoPeptidePair(String scannum,
	        IPhosphoPeptideSequence phosSeq_neu, int neuloc, short charge,
	        double mh_ms2, double deltaMs_ms2, double mz_ms2,
	        double mz_ms3,short rank_max,
	        HashSet<ProteinReference> refs, float pi, short numofTerm,
	        float xcorrms2, float xcorrms3, float xcorrs, float deltaCnm,
	        double[] ascores, ISequestPhosPairFormat formatter) {
		/*
		 * Here I use the existed scan name format to maintain the scan number
		 * of ms2 and ms3
		 */
		super(scannum, phosSeq_neu, neuloc, charge, mh_ms2, deltaMs_ms2, mz_ms2, mz_ms3,
		        rank_max, refs, pi, numofTerm, ascores, formatter);

		this.detaCnm = deltaCnm;
		this.xcorrms2 = xcorrms2;
		this.xcorrms3 = xcorrms3;
		this.xcorrs = xcorrs;
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
		return PeptideType.APIVASE_SEQUEST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPrimaryScore()
	 */
	@Override
	public float getPrimaryScore() {
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

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getInten()
	 */
	@Override
	public double getInten() {
		// TODO Auto-generated method stub
		return 0;
	}

}
