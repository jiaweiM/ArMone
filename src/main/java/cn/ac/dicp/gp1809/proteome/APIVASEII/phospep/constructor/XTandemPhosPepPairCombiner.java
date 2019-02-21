/* 
 ******************************************************************************
 * File: XTandemPhosPepPairCombiner.java * * * Created on 02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.constructor;

import cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation.SeqvsTscore;
import cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.SeqvsAscore;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.IXTandemPeptide;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.XTandemPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IXTandemPhosphoPeptidePair;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.XTandemPhosphoPeptidePair;

/**
 * The phosphopeptide pair merger for Mascot peptide
 * 
 * @author Xinning
 * @version 0.1.000, 05-25-2010, 14:55:39
 */
public class XTandemPhosPepPairCombiner extends
        AbstractPhosPairCombiner<IXTandemPhosphoPeptidePair, IXTandemPeptide> {

	private static XTandemPhosPairFormat formatter = new XTandemPhosPairFormat();

	protected XTandemPhosPepPairCombiner() {
		super(formatter);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.constructor.IPhosPepPairCombiner#
	 * combine(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide,
	 * cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide, int, int,
	 * cn.ac.dicp.gp1809.APIVASEII.sitelocation.SeqvsTscore)
	 */
	@Override
	public XTandemPhosphoPeptidePair combine(IXTandemPeptide pepMS2,
	        IXTandemPeptide pepMS3, int ms2, int ms3, SeqvsTscore seqt) {

		short rank2 = pepMS2.getRank();
		short rank3 = pepMS3.getRank();

		if (Math.min(rank2, rank3) != 1) {
			System.err.println("Both MS2 and MS3 phosphopeptides "
			        + "are not top matched peptides, return null.");
		}

		short rankmax = (short) Math.max(rank2, rank3);
		short charge = pepMS2.getCharge();
		double mz_ms2 = this.getMZ(pepMS2.getMH(), pepMS2.getDeltaMH(), charge);
		double mz_ms3 = this.getMZ(pepMS3.getMH(), pepMS3.getDeltaMH(), charge);

		float hyperscore = pepMS2.getHyperscore() + pepMS3.getHyperscore();

		double evalue = pepMS2.getEvalue() * pepMS3.getEvalue();

		XTandemPhosphoPeptidePair spair = new XTandemPhosphoPeptidePair(pepMS2
		        .getBaseName(), ms2, ms3, seqt.getSequence(), seqt.getNLSite(),
		        charge, pepMS2.getMH(), pepMS2.getDeltaMH(), mz_ms2, mz_ms3,
		        rankmax, pepMS2.getProteinReferences(), pepMS2.getPI(), pepMS2
		                .getNumberofTerm(), pepMS2.getHyperscore(), pepMS3
		                .getHyperscore(), hyperscore, pepMS2.getEvalue(),
		        pepMS3.getEvalue(), evalue, seqt.getSiteSocres(), formatter);
		
		spair.setEnzyme(pepMS2.getEnzyme());

		return spair;
	}

	@Override
    public IXTandemPhosphoPeptidePair combineAScore(IXTandemPeptide pepMS2,
            IXTandemPeptide pepMS3, int ms2, int ms3, SeqvsAscore seqt) {
		throw new IllegalArgumentException("Not designed");
    }
}
