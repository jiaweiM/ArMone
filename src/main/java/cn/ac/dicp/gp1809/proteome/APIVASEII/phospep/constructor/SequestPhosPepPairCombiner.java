/* 
 ******************************************************************************
 * File: SequestPhosphoPeptidePairMerger.java * * * Created on 02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.constructor;

import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation.SeqvsTscore;
import cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.SeqvsAscore;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.SequestPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.ISequestPhosphoPeptidePair;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.SequestPhosphoPeptidePair;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptide;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;

/**
 * The phosphopeptide merger for sequest identified peptides
 * 
 * @author Xinning
 * @version 0.1.2, 06-08-2010, 15:50:56
 */
class SequestPhosPepPairCombiner extends
        AbstractPhosPairCombiner<ISequestPhosphoPeptidePair, ISequestPeptide> {

	//max 9+
	private static int[] max_pep_len = new int[] { 100, 15, 25, 50, 100, 100,
	        100, 100, 100, 100 };

	private static SequestPhosPairFormat formatter = new SequestPhosPairFormat();

	private static SequestPhosPairFormat formatterT = new SequestPhosPairFormat(false);
	
	/**
	 * 
	 * @param phosSymbol
	 * @param neuSymbol
	 */
	public SequestPhosPepPairCombiner() {
		super(formatter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.constructor.IPhosPepPairCombiner#
	 * combine(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide,
	 * cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide, int, int,
	 * cn.ac.dicp.gp1809.APIVASEII.sitelocation.SeqvsTscore, java.lang.String)
	 */
	@Override
	public ISequestPhosphoPeptidePair combine(ISequestPeptide pepMS2,
	        ISequestPeptide pepMS3, int ms2, int ms3, SeqvsTscore seqt) {

		short rank2 = pepMS2.getRank();
		short rank3 = pepMS3.getRank();

		if (Math.min(rank2, rank3) != 1) {
			System.err.println("Both MS2 and MS3 phosphopeptides "
			        + "are not top matched peptides, return null.");
		}

		float dcnm = 0f;
		if (rank2 == 1 && rank3 == 1)
			dcnm = Math.max(pepMS2.getDeltaCn(), pepMS3.getDeltaCn());
		else if (rank2 == 1)
			dcnm = pepMS2.getDeltaCn();

		else if (rank3 == 1)
			dcnm = pepMS3.getDeltaCn();

		short rankmax = (short) Math.max(rank2, rank3);
		IPhosphoPeptideSequence pseq = seqt.getSequence();
		float xcorrms2 = pepMS2.getXcorr();
		float xcorrms3 = pepMS3.getXcorr();
		short charge = pepMS2.getCharge();
		float xcorrs = getXcorrNorm(xcorrms2, xcorrms3, charge, pseq.length());

		double mz_ms2 = this.getMZ(pepMS2.getMH(), pepMS2.getDeltaMH(), charge);
		double mz_ms3 = this.getMZ(pepMS3.getMH(), pepMS3.getDeltaMH(), charge);

		SequestPhosphoPeptidePair spair = new SequestPhosphoPeptidePair(pepMS2
		        .getBaseName(), ms2, ms3, pseq, seqt.getNLSite(), charge,
		        pepMS2.getMH(), pepMS2.getDeltaMH(), mz_ms2, mz_ms3, rankmax,
		        pepMS2.getProteinReferences(), pepMS2.getPI(), pepMS2
		                .getNumberofTerm(), xcorrms2, xcorrms3, xcorrs, dcnm,
		        seqt.getSiteSocres(), formatterT);
		
		spair.setEnzyme(pepMS2.getEnzyme());

		return spair;
	}

	/**
	 * Get xcorr'
	 * 
	 * @param temppair
	 * @return normalized xcorr with length
	 */
	protected final static float getXcorrNorm(float xcorrMS2, float xcorrMS3,
	        short charge, int len) {

		return (float) (Math.log(xcorrMS2 + xcorrMS3) / Math.log(Math.min(len,
		        max_pep_len[charge])));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public ISequestPhosphoPeptidePair combineAScore(ISequestPeptide pepMS2,
	        ISequestPeptide pepMS3, int ms2, int ms3, SeqvsAscore seqt) {


		short rank2 = pepMS2.getRank();
		short rank3 = pepMS3.getRank();

		if (Math.min(rank2, rank3) != 1) {
			System.err.println("Both MS2 and MS3 phosphopeptides "
			        + "are not top matched peptides, return null.");
		}

		float dcnm = 0f;
		if (rank2 == 1 && rank3 == 1)
			dcnm = Math.max(pepMS2.getDeltaCn(), pepMS3.getDeltaCn());
		else if (rank2 == 1)
			dcnm = pepMS2.getDeltaCn();

		else if (rank3 == 1)
			dcnm = pepMS3.getDeltaCn();

		short rankmax = (short) Math.max(rank2, rank3);
		IPhosphoPeptideSequence pseq = (IPhosphoPeptideSequence) seqt.getSequence();
		float xcorrms2 = pepMS2.getXcorr();
		float xcorrms3 = pepMS3.getXcorr();
		short charge = pepMS2.getCharge();
		float xcorrs = getXcorrNorm(xcorrms2, xcorrms3, charge, pseq.length());

		double mz_ms2 = this.getMZ(pepMS2.getMH(), pepMS2.getDeltaMH(), charge);
		double mz_ms3 = this.getMZ(pepMS3.getMH(), pepMS3.getDeltaMH(), charge);

		double[] ascores1 = seqt.getAscores();
		double[] ascores2 = seqt.getAscores2();
		
		int len = ascores1.length;
		double[] ascores = new double[len];
		
		for(int i=0; i<len; i++) {
			double ascore1 = ascores1[i];
			double ascore2 = ascores2[i];
			
//			if(ascore2 > ascore1) {
//				System.out.println("Use MS3 ascore.");
//			}
			
			ascores[i] = Math.max(ascore1, ascore2);
		}
		
		SequestPhosphoPeptidePair spair = new SequestPhosphoPeptidePair(pepMS2
		        .getBaseName(), ms2, ms3, pseq, seqt.getNLSite(), charge,
		        pepMS2.getMH(), pepMS2.getDeltaMH(), mz_ms2, mz_ms3, rankmax,
		        pepMS2.getProteinReferences(), pepMS2.getPI(), pepMS2
		                .getNumberofTerm(), xcorrms2, xcorrms3, xcorrs, dcnm,
		                ascores, formatter);
		
		spair.setEnzyme(pepMS2.getEnzyme());

		HashMap<String, SeqLocAround> ms2Map = pepMS2.getPepLocAroundMap();
		HashMap<String, SeqLocAround> ms3Map = pepMS3.getPepLocAroundMap();
		
		HashMap<String, SeqLocAround> newMap = new HashMap<String, SeqLocAround>();
		newMap.putAll(ms2Map);
		newMap.putAll(ms3Map);
		
		spair.setPepLocAroundMap(newMap);
		
		return spair;
	}

}
