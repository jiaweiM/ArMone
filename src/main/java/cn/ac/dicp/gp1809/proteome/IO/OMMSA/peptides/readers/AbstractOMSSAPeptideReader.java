/* 
 ******************************************************************************
 * File: AbstractOMSSAPeptideReader.java * * * Created on 09-02-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.readers;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.IO.OMMSA.OMSSAMod;
import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.DefaultOMSSAPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.IOMSSAPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.OMSSAPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.aasequence.PeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.IModification;

/**
 * Abstract OMSSA peptides
 * 
 * @author Xinning
 * @version 0.1.1, 05-03-2009, 20:32:47
 */
public abstract class AbstractOMSSAPeptideReader extends AbstractPeptideReader
        implements IOMSSAPeptideReader {

	private IOMSSAPeptideFormat<?> formatter;

	protected AbstractOMSSAPeptideReader(String filename) {
		super(new File(filename), new DefaultOMSSAPeptideFormat());
	}

	public AbstractOMSSAPeptideReader(File file) {
		super(file, new DefaultOMSSAPeptideFormat());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 * .AbstractPeptideReader#getPeptide()
	 */
	@Override
	public OMSSAPeptide getPeptide() throws PeptideParsingException {
		return (OMSSAPeptide) super.getPeptide();
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
	 * @param modifs
	 *            : modifs on this peptide sequence.
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
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideReader#getPeptideType
	 * ()
	 */
	@Override
	public PeptideType getPeptideType() {
		return PeptideType.OMSSA;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#getPeptideFormat()
	 */
	@Override
	public IOMSSAPeptideFormat<?> getPeptideFormat() {
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

		if (format instanceof IOMSSAPeptideFormat<?>) {
			this.formatter = (IOMSSAPeptideFormat<?>) format;
			super.setPeptideFormat(format);
		} else
			throw new IllegalArgumentException(
			        "The formater for set must be OMSSA formater");
	}
}
