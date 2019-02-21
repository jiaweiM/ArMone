/*
 ******************************************************************************
 * File: CruxPhosPepPairCombiner.java * * * Created on 04-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.constructor;

import cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation.SeqvsTscore;
import cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.SeqvsAscore;
import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.CruxPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.CruxPhosphoPeptidePair;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.ICruxPhosphoPeptidePair;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;

/**
 * The phosphopeptide merger for crux identified peptides
 * 
 * @author Xinning
 * @version 0.1.000, 05-25-2010, 14:56:21
 */
class CruxPhosPepPairCombiner extends
        AbstractPhosPairCombiner<ICruxPhosphoPeptidePair, ICruxPeptide> {

	//max 9+
	private static int[] max_pep_len = new int[] { 100, 15, 25, 50, 100, 100,
	        100, 100, 100, 100 };

	private static CruxPhosPairFormat formatter = new CruxPhosPairFormat();

	/**
	 * 
	 * @param phosSymbol
	 * @param neuSymbol
	 */
	public CruxPhosPepPairCombiner() {
		super(formatter);
	}

	@Override
	public ICruxPhosphoPeptidePair combine(ICruxPeptide pepMS2,
	        ICruxPeptide pepMS3, int ms2, int ms3, SeqvsTscore seqt) {

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

		float pvaluems2 = pepMS2.getPValue();
		float pvaluems3 = pepMS3.getPValue();
		float pvalue = this.combine(pvaluems2, pvaluems3);

		float qvaluems2 = pepMS2.getQValue();
		float qvaluems3 = pepMS3.getQValue();
		float qvalue = this.combine(qvaluems2, qvaluems3);

		float perscorems2 = pepMS2.getPercolator_score();
		float perscorems3 = pepMS3.getPercolator_score();
		float perscore = this.combine(perscorems2, perscorems3);

		double mz_ms2 = this.getMZ(pepMS2.getMH(), pepMS2.getDeltaMH(), charge);
		double mz_ms3 = this.getMZ(pepMS3.getMH(), pepMS3.getDeltaMH(), charge);

		CruxPhosphoPeptidePair spair = new CruxPhosphoPeptidePair(pepMS2
		        .getBaseName(), ms2, ms3, pseq, seqt.getNLSite(), charge,
		        pepMS2.getMH(), pepMS2.getDeltaMH(), mz_ms2, mz_ms3, rankmax,
		        pepMS2.getProteinReferences(), pepMS2.getPI(), pepMS2
		                .getNumberofTerm(), xcorrms2, xcorrms3, xcorrs, dcnm,
		        pvaluems2, pvaluems3, pvalue, qvaluems2, qvaluems3, qvalue,
		        perscorems2, perscorems3, perscore, seqt.getSiteSocres(),
		        formatter);
		spair.setEnzyme(pepMS2.getEnzyme());
		return spair;
	}

	/**
	 * If both are NaN, return NaN. If one of the score is NaN, return another
	 * score. If both are not NaN, return score1 + score2.
	 * 
	 * @param score1
	 * @param score2
	 * @return
	 */
	private float combine(float score1, float score2) {
		boolean nan1 = Float.isNaN(score1);
		boolean nan2 = Float.isNaN(score2);
		if (nan1 && nan2)
			return Float.NaN;

		if ((!nan1) && (!nan2))
			return score1 + score2;

		return nan1 ? score2 : score1;
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

	@Override
    public ICruxPhosphoPeptidePair combineAScore(ICruxPeptide pepMS2,
            ICruxPeptide pepMS3, int ms2, int ms3, SeqvsAscore seqt) {
		throw new IllegalArgumentException("Not designed");
    }

}
