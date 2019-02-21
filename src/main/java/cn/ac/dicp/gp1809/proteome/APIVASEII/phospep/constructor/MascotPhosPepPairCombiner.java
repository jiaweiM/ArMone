/* 
 ******************************************************************************
 * File: MascotPhosPepPairCombiner.java * * * Created on 02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.constructor;

import cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation.SeqvsTscore;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.SeqvsAscore;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.MascotPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IMascotPhosphoPeptidePair;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.MascotPhosphoPeptidePair;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;

/**
 * The phosphopeptide pair merger for Mascot peptide
 * 
 * @author Xinning
 * @version 0.1.2, 06-08-2010, 15:51:44
 */
public class MascotPhosPepPairCombiner extends
        AbstractPhosPairCombiner<IMascotPhosphoPeptidePair, IMascotPeptide> {

	private static MascotPhosPairFormat formatter = new MascotPhosPairFormat();

	private static MascotPhosPairFormat formatterT = new MascotPhosPairFormat(
	        false);

	protected MascotPhosPepPairCombiner() {
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
	public MascotPhosphoPeptidePair combine(IMascotPeptide pepMS2,
	        IMascotPeptide pepMS3, int ms2, int ms3, SeqvsTscore seqt) {

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

		float ionscore = pepMS2.getIonscore() + pepMS3.getIonscore();

		double evalue = -Math.log10(pepMS2.getEvalue() * pepMS3.getEvalue());

		MascotPhosphoPeptidePair spair = new MascotPhosphoPeptidePair(pepMS2
		        .getBaseName(), ms2, ms3, seqt.getSequence(), seqt.getNLSite(),
		        charge, pepMS2.getMH(), pepMS2.getDeltaMH(), mz_ms2, mz_ms3,
		        rankmax, pepMS2.getProteinReferences(), pepMS2.getPI(), pepMS2
		                .getNumberofTerm(), pepMS2.getIonscore(), pepMS3
		                .getIonscore(), ionscore, pepMS2.getEvalue(), pepMS3
		                .getEvalue(), evalue, seqt.getSiteSocres(), formatterT);
		
		spair.setEnzyme(pepMS2.getEnzyme());

		return spair;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.constructor.IPhosPepPairCombiner#
	 * combineAScore(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide,
	 * cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide, int, int,
	 * cn.ac.dicp.gp1809.APIVASEII.sitelocation.SeqvsAscore)
	 */
	@Override
	public IMascotPhosphoPeptidePair combineAScore(IMascotPeptide pepMS2,
	        IMascotPeptide pepMS3, int ms2, int ms3, SeqvsAscore seqt) {

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

		float ionscore = pepMS2.getIonscore() + pepMS3.getIonscore();

		double evalue = -Math.log10(pepMS2.getEvalue() * pepMS3.getEvalue());

		double[] ascores1 = seqt.getAscores();
		double[] ascores2 = seqt.getAscores2();
		
		int len = ascores1.length;
		double[] ascores = new double[len];
		
		for(int i=0; i<len; i++) {
			ascores[i] = Math.max(ascores1[i], ascores2[i]);
		}
		
		MascotPhosphoPeptidePair spair = new MascotPhosphoPeptidePair(pepMS2
		        .getBaseName(), ms2, ms3, (IPhosphoPeptideSequence) seqt.getSequence(), seqt.getNLSite(),
		        charge, pepMS2.getMH(), pepMS2.getDeltaMH(), mz_ms2, mz_ms3,
		        rankmax, pepMS2.getProteinReferences(), pepMS2.getPI(), pepMS2
		                .getNumberofTerm(), pepMS2.getIonscore(), pepMS3
		                .getIonscore(), ionscore, pepMS2.getEvalue(), pepMS3
		                .getEvalue(), evalue, ascores, formatter);
		
		spair.setEnzyme(pepMS2.getEnzyme());

		return spair;

	}
}
