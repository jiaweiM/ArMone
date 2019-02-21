/*
 ******************************************************************************
 * File: AbstractCruxPeptideReader.java * * * Created on 04-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.crux.peptides.reader;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide;
import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;

/**
 * Abstract crux peptide reader
 * 
 * @author Xinning
 * @version 0.1.1, 05-03-2009, 21:07:59
 */
public abstract class AbstractCruxPeptideReader extends
        AbstractPeptideReader implements ICruxPeptideReader {

	private ICruxPeptideFormat<?> formatter;
	
	public AbstractCruxPeptideReader(String filename) {
		this(new File(filename));
	}

	public AbstractCruxPeptideReader(File file) {
		super(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 * .AbstractPeptideReader#getPeptide()
	 */
	@Override
	public ICruxPeptide getPeptide() throws PeptideParsingException {
		return (ICruxPeptide) super.getPeptide();
	}

	/**
	 * Parse the raw sequence after Crux peptide identification into the
	 * overall formatted peptide sequence. The format is:
	 * <p>
	 * A.AAAAAA#AAAA.A
	 * <p>
	 * where A. and .A indicate the previous and next aminoacids after database
	 * search, and the symbol of # (and so on) is the variable modification
	 * symbol which indicates a variable modification.
	 * 
	 * 
	 * @param the
	 *            raw peptide sequence after database
	 * @return
	 * @throws PeptideParsingException
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.Inspect.InspectParameter
	 */
	protected final String parseSequence(String rawseq)
	        throws PeptideParsingException {
		return this.getSearchParameter().parsePeptide(rawseq);
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
		return PeptideType.CRUX;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#getPeptideFormat()
	 */
	@Override
	public ICruxPeptideFormat<?> getPeptideFormat() {
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

		if (format instanceof ICruxPeptideFormat<?>) {
			this.formatter = (ICruxPeptideFormat<?>) format;
			super.setPeptideFormat(format);
		} else
			throw new IllegalArgumentException(
			        "The formater for set must be Crux formater");
	}
}
