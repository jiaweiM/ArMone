/*
 * *****************************************************************************
 * File: AbstractXTandemPeptideReader.java * * * Created on 10-06-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.readers;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.IO.OMMSA.OMSSAMod;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.DefaultXTandemPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.IXTandemPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.XTandemPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.aasequence.PeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.IModification;

/**
 * Abstract XTandem peptides
 * 
 * @author Xinning
 * @version 0.1.3, 05-03-2009, 21:20:02
 */
public abstract class AbstractXTandemPeptideReader extends
        AbstractPeptideReader implements IXTandemPeptideReader {
	
	private IXTandemPeptideFormat<?> formatter;
	
	public AbstractXTandemPeptideReader(String filename) {
		this(new File(filename));
	}

	public AbstractXTandemPeptideReader(File file) {
		super(file, new DefaultXTandemPeptideFormat());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 *      .AbstractPeptideReader#getPeptide()
	 */
	@Override
	public XTandemPeptide getPeptide() throws PeptideParsingException {
		return (XTandemPeptide) super.getPeptide();
	}

	/**
	 * Parse the raw sequence after OMSSA peptide identification into the
	 * overall formatted peptide sequence. The format is:
	 * <p>
	 * A.AAAAAA#AAAA.A
	 * <p>
	 * where A. and .A indicate the previous and next aminoacids after database
	 * search, and the symbol of # (and so on) is the variable modification
	 * symbol which indicates a variable modification.
	 * 
	 * 
	 * @param PeptideSequence
	 *            peptide sequence
	 * @param modifs:
	 *            modifs on this peptide sequence.
	 * @param modif_at
	 *            the modified aminoacid indexes according to the modifs. The
	 *            index is from 0 -n
	 * @return
	 * @throws PeptideParsingException
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.OMMSA.OMSSAParameter
	 *      #parseSequence(PeptideSequence, OMSSAMod[], int[])
	 */

	protected final String parseSequence(PeptideSequence raw,
	        IModification[] modifs, int[] modif_at)
	        throws PeptideParsingException {
		return this.getSearchParameter().parseSequence(raw, modifs, modif_at);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideReader#getPeptideType()
	 */
	@Override
	public PeptideType getPeptideType() {
		return PeptideType.XTANDEM;
	}

	/**
	 * Always return the maximum integer value
	 */
	@Override
	public int getTopN() {

		System.err.println("The setting of topn=" + super.getTopN()
		        + " is useless for X!Tandem because it only report "
		        + "the best matched peptide.");

		return super.getTopN();
	}

	/**
	 * throw new UnSupportingMethodException
	 */
	@Override
	public void setTopN(int topn) {
		super.setTopN(topn);

		System.err.println("The setting of topn=" + topn
		        + " is useless for X!Tandem because it only report "
		        + "the best matched peptide.");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#getPeptideFormat()
	 */
	@Override
	public IXTandemPeptideFormat<?> getPeptideFormat() {
		return this.formatter;
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
			throw new NullPointerException("The format is null.");
		}

		if (format instanceof IXTandemPeptideFormat<?>) {
			this.formatter = (IXTandemPeptideFormat<?>) format;
			super.setPeptideFormat(format);
		} else
			throw new IllegalArgumentException(
			        "The formater for set must be X!Tandem formater");
	}

}
