/*
 * *****************************************************************************
 * File: InspectPeptide.java * * * Created on 03-24-2009
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.AbstractPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.util.IScanName;

/**
 * Peptide identified by InspectPeptide implements IPeptide
 * 
 * @author Xinning
 * @version 0.1.0.1, 04-02-2009, 11:02:20
 */
public class InspectPeptide extends AbstractPeptide implements IInspectPeptide {

	/**
	 * The enzyme used for the protein digestion. The mainly usage is to
	 * determin the ntt value of the peptides. The default enzyme was set as
	 * trypsin(KR/P).
	 */
	public static Enzyme enzyme = Enzyme.TRYPSIN;

	// The p-value
	private double pvalue;
	// The match quality score
	private float MQscore;
	//The fscore
	private float fscore;

	/**
	 * Construct a new peptide with the same informations
	 * 
	 * @param pep
	 */
	public InspectPeptide(IInspectPeptide pep) {
		this(pep.getScanNum(), pep.getSequence(), pep.getCharge(), pep.getMH(),
		        pep.getDeltaMH(), pep.getRank(), pep.getMQScore(), pep
		                .getFscore(), pep.getPValue(), pep
		                .getProteinReferences(), pep.getPI(), pep
		                .getNumberofTerm(), (IInspectPeptideFormat) pep
		                .getPeptideFormat());
		
		this.setProbability(pep.getProbabilty());
	}

	public InspectPeptide(String baseName, int scanNumBeg, int scanNumEnd,
	        String sequence, short charge, double mh, double deltaMs,
	        short rank, float mqscore, float fscore, double pvalue,
	        HashSet<ProteinReference> refs, short numofTerms,
	        IInspectPeptideFormat formatter) {

		super(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
		        rank, refs, formatter);

		this.setNumberofTerm(numofTerms);

		this.pvalue = pvalue;
		this.MQscore = mqscore;
		this.fscore = fscore;
	}

	public InspectPeptide(String scanNum, String sequence, short charge,
	        double mh, double deltaMs, short rank, float mqscore, float fscore,
	        double pvalue, HashSet<ProteinReference> refs, short numofTerms,
	        IInspectPeptideFormat formatter) {
		super(scanNum, sequence, charge, mh, deltaMs, rank, refs, formatter);
		this.setNumberofTerm(numofTerms);

		this.pvalue = pvalue;
		this.MQscore = mqscore;
		this.fscore = fscore;
	}
	
	public InspectPeptide(IScanName scanName, String sequence, short charge,
	        double mh, double deltaMs, short rank, float mqscore, float fscore,
	        double pvalue, HashSet<ProteinReference> refs, short numofTerms,
	        IInspectPeptideFormat formatter) {
		super(scanName, sequence, charge, mh, deltaMs, rank, refs, formatter);
		this.setNumberofTerm(numofTerms);

		this.pvalue = pvalue;
		this.MQscore = mqscore;
		this.fscore = fscore;
	}

	public InspectPeptide(String baseName, int scanNumBeg, int scanNumEnd,
	        String sequence, short charge, double mh, double deltaMs,
	        short rank, float mqscore, float fscore, double pvalue,
	        HashSet<ProteinReference> refs, float pi, short numofTerms,
	        IInspectPeptideFormat formatter) {

		this(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
		        rank, mqscore, fscore, pvalue, refs, numofTerms, formatter);

		this.setPI(pi);
	}

	public InspectPeptide(String scanNum, String sequence, short charge,
	        double mh, double deltaMs, short rank, float mqscore, float fscore,
	        double pvalue, HashSet<ProteinReference> refs, float pi,
	        short numofTerms, IInspectPeptideFormat formatter) {
		this(scanNum, sequence, charge, mh, deltaMs, rank, mqscore, fscore,
		        pvalue, refs, numofTerms, formatter);

		this.setPI(pi);
	}
	
	public InspectPeptide(IScanName scanName, String sequence, short charge,
	        double mh, double deltaMs, short rank, float mqscore, float fscore,
	        double pvalue, HashSet<ProteinReference> refs, float pi,
	        short numofTerms, IInspectPeptideFormat formatter) {
		this(scanName, sequence, charge, mh, deltaMs, rank, mqscore, fscore,
		        pvalue, refs, numofTerms, formatter);

		this.setPI(pi);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.IInspectPeptide#getFscore
	 * ()
	 */
	@Override
	public float getFscore() {
		return this.fscore;
	}

	/**
	 * @param pvalue
	 *            the pvalue to set
	 */
	public void setPvalue(double pvalue) {
		this.pvalue = pvalue;
	}

	/**
	 * @param fscore
	 *            the fscore to set
	 */
	public void setFscore(float fscore) {
		this.fscore = fscore;
	}

	/**
	 * @param mQscore
	 *            the mQscore to set
	 */
	public void setMQscore(float mQscore) {
		MQscore = mQscore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.IInspectPeptide#getMQScore
	 * ()
	 */
	@Override
	public float getMQScore() {
		return this.MQscore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.IInspectPeptide#getPValue
	 * ()
	 */
	@Override
	public double getPValue() {
		return this.pvalue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#getEnzyme()
	 */
	@Override
	public Enzyme getEnzyme() {
		return enzyme;
	}

	/**
	 * =ionsocre;
	 */
	@Override
	public float getPrimaryScore() {
		return this.MQscore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		return PeptideType.INSPECT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#setPeptideFormat(
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat)
	 */
	@Override
	public void setPeptideFormat(IPeptideFormat<?> format) {

		if (format == null) {
			return;
		}

		if (format instanceof IInspectPeptideFormat<?>) {
			super.setPeptideFormat(format);
		} else
			throw new IllegalArgumentException(
			        "The formater for set must be Inspect formater");
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
