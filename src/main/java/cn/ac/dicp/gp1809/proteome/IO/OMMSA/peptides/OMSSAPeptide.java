/*
 * *****************************************************************************
 * File: OMSSAPeptide.java * * * Created on 09-01-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.AbstractPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;

/**
 * Peptide identified by OMSSA implements IPeptide
 * 
 * @author Xinning
 * @version 0.1.6, 05-03-2009, 20:40:20
 */
public class OMSSAPeptide extends AbstractPeptide implements IOMSSAPeptide {

	// The E-value
	private double evalue;
	// The P-value
	private double pvalue;

	private float primScore;

	/**
	 * 
	 * @param pep
	 */
	public OMSSAPeptide(IOMSSAPeptide pep) {
		this(pep.getScanNum(), pep.getSequence(), pep.getCharge(), pep.getMH(),
		        pep.getDeltaMH(), pep.getRank(), pep.getEvalue(), pep
		                .getPvalue(), pep.getProteinReferences(), pep.getPI(),
		        pep.getNumberofTerm(), (IOMSSAPeptideFormat<?>) pep
		                .getPeptideFormat());
		
		this.setProbability(pep.getProbabilty());
		this.setEnzyme(pep.getEnzyme());
	}

	public OMSSAPeptide(String baseName, int scanNumBeg, int scanNumEnd,
	        String sequence, short charge, double mh, double deltaMs,
	        short rank, double evalue, double pvalue,
	        HashSet<ProteinReference> refs, IOMSSAPeptideFormat<?> formatter) {

		super(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
		        rank, refs, formatter);

		this.setEvalue(evalue);
		this.pvalue = pvalue;
	}

	public OMSSAPeptide(String scanNum, String sequence, short charge,
	        double mh, double deltaMs, short rank, double evalue,
	        double pvalue, HashSet<ProteinReference> refs,
	        IOMSSAPeptideFormat<?> formatter) {
		super(scanNum, sequence, charge, mh, deltaMs, rank, refs, formatter);

		this.setEvalue(evalue);
		this.pvalue = pvalue;
	}

	public OMSSAPeptide(String baseName, int scanNumBeg, int scanNumEnd,
	        String sequence, short charge, double mh, double deltaMs,
	        short rank, double evalue, double pvalue,
	        HashSet<ProteinReference> refs, float pi, short numofTerms,
	        IOMSSAPeptideFormat<?> formatter) {

		this(baseName, scanNumBeg, scanNumEnd, sequence, charge, mh, deltaMs,
		        rank, evalue, pvalue, refs, formatter);

		this.setPI(pi);
		this.setNumberofTerm(numofTerms);
	}

	public OMSSAPeptide(String scanNum, String sequence, short charge,
	        double mh, double deltaMs, short rank, double evalue,
	        double pvalue, HashSet<ProteinReference> refs, float pi,
	        short numofTerms, IOMSSAPeptideFormat<?> formatter) {
		this(scanNum, sequence, charge, mh, deltaMs, rank, evalue, pvalue,
		        refs, formatter);

		this.setPI(pi);
		this.setNumberofTerm(numofTerms);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.IOMSSAPeptide#getEvalue()
	 */
	public final double getEvalue() {
		return evalue;
	}

	/**
	 * @param evalue
	 *            the expected value to set
	 */
	public final void setEvalue(double evalue) {
		this.evalue = evalue;
		this.primScore = (float) -Math.log(evalue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.IOMSSAPeptide#getPvalue()
	 */
	public final double getPvalue() {
		return pvalue;
	}

	/**
	 * @param pvalue
	 *            the p value to set
	 */
	public final void setPvalue(double pvalue) {
		this.pvalue = pvalue;
	}

	/**
	 * =-ln(evalue);
	 */
	@Override
	public float getPrimaryScore() {
		return this.primScore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		return PeptideType.OMSSA;
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

		if (format instanceof IOMSSAPeptideFormat<?>) {
			super.setPeptideFormat(format);
		} else
			throw new IllegalArgumentException(
			        "The formater for set must be OMSSA formater");
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
