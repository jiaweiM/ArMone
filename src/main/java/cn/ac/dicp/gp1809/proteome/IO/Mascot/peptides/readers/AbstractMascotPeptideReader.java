/*
 * *****************************************************************************
 * File: AbstractMascotPeptideReader.java * * * Created on 11-04-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.readers;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.DefaultMascotPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.aasequence.PeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.IModification;

/**
 * Abstract Mascot peptide reader
 * 
 * @author Xinning
 * @version 0.1.1, 05-03-2009, 20:42:39
 */
public abstract class AbstractMascotPeptideReader extends AbstractPeptideReader implements IMascotPeptideReader {

	private IMascotPeptideFormat<?> formatter;

	public AbstractMascotPeptideReader(String filename) {
		this(new File(filename));
	}

	public AbstractMascotPeptideReader(File file) {
		super(file, new DefaultMascotPeptideFormat());
	}

	@Override
	public IMascotPeptide getPeptide() throws PeptideParsingException {
		
		return (IMascotPeptide) super.getPeptide();
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
	 * @param PeptideSequence peptide sequence
	 * @param modifs: modifs on this peptide sequence.
	 * @param modif_at the modified aminoacid indexes according to the modifs.
	 *            The index is from 0 -n
	 * @return
	 * @throws PeptideParsingException
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.OMMSA.OMSSAParameter
	 *      #parseSequence(PeptideSequence, OMSSAMod[], int[])
	 */
	protected final String parseSequence(PeptideSequence raw, IModification[] modifs, int[] modif_at)
			throws PeptideParsingException {
		return this.getSearchParameter().parseSequence(raw, modifs, modif_at);
	}

	@Override
	public PeptideType getPeptideType() {
		return PeptideType.MASCOT;
	}

	@Override
	public IMascotPeptideFormat<?> getPeptideFormat() {
		return this.formatter;
	}

	@Override
	public void setPeptideFormat(IPeptideFormat<?> format) {

		if (format == null) {
			throw new NullPointerException("The format is null.");
		}

		if (format instanceof IMascotPeptideFormat<?>) {
			this.formatter = (IMascotPeptideFormat<?>) format;
			super.setPeptideFormat(format);
		} else
			throw new IllegalArgumentException("The formater for set must be Mascot formater");
	}
}
