/*
 * *****************************************************************************
 * File: AbstractInspectPeptideReader.java * * * Created on 03-24-2009
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.reader;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.DefaultInspectPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.IInspectPeptide;
import cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.IInspectPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;

/**
 * Abstract Inspect peptide reader
 * 
 * @author Xinning
 * @version 0.1.1, 05-03-2009, 20:48:31
 */
public abstract class AbstractInspectPeptideReader extends
        AbstractPeptideReader implements IInspectPeptideReader {

	private IInspectPeptideFormat<?> formatter;

	public AbstractInspectPeptideReader(String filename) {
		this(new File(filename));
	}

	public AbstractInspectPeptideReader(File file) {
		super(file, new DefaultInspectPeptideFormat());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 * .AbstractPeptideReader#getPeptide()
	 */
	@Override
	public IInspectPeptide getPeptide() throws PeptideParsingException {
		return (IInspectPeptide) super.getPeptide();
	}

	/**
	 * Parse the raw sequence after Inspect peptide identification into the
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
		return PeptideType.INSPECT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#getPeptideFormat()
	 */
	@Override
	public IInspectPeptideFormat<?> getPeptideFormat() {
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

		if (format instanceof IInspectPeptideFormat<?>) {
			this.formatter = (IInspectPeptideFormat<?>) format;
			super.setPeptideFormat(format);
		} else
			throw new IllegalArgumentException(
			        "The formater for set must be Inspect formater");
	}
}
