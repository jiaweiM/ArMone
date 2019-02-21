/*
 * *****************************************************************************
 * File: SequestPeptideProbListReader.java * * * Created on 09-07-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideProbListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SequestPeptide;

/**
 * Reader for Sequest peptide probability list file
 * 
 * @author Xinning
 * @version 0.1, 09-07-2008, 15:24:28
 */
public class SequestPeptideProbListReader extends PeptideProbListReader
        implements ISequestPeptideReader {
	/**
	 * Create a sequest peptide list reader from an existing peptide list
	 * reader.
	 * 
	 * <p>
	 * If the existing list reader can not covert to sequest peptide list reader
	 * (the peptide list file is not created from a sequest database search
	 * resource), ReaderGenerateException will be threw.
	 * 
	 * @param listfile
	 * @throws ReaderGenerateException
	 *             if the list reader is not reading a peptide list from sequest
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public SequestPeptideProbListReader(String listfile)
	        throws ReaderGenerateException, FileDamageException, IOException {
		this(new File(listfile));
	}

	/**
	 * Create a sequest peptide list reader from an existing peptide list
	 * reader.
	 * 
	 * <p>
	 * If the existing list reader can not covert to sequest peptide list reader
	 * (the peptide list file is not created from a sequest database search
	 * resource), ReaderGenerateException will be threw.
	 * 
	 * @param listfile
	 * @throws ReaderGenerateException
	 *             if the list reader is not reading a peptide list from sequest
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public SequestPeptideProbListReader(File listfile)
	        throws ReaderGenerateException, FileDamageException, IOException {
		super(listfile);

		if (!this.validate())
			throw new ReaderGenerateException(
			        "Cann't create SequestPeptideListReader for a peptide "
			                + "list file which was not generated from sequest output");

	}

	/**
	 * Test whether the specific peptide list reader can be converted (The
	 * peptide list file is OMSSA peptide list file). The validation rely mainly
	 * on the getpeptideType();
	 * 
	 * @return true is the list reader is reading a OMSSA outputted list file
	 */
	protected boolean validate() {
		if (this.getPeptideType() == PeptideType.OMSSA)
			return true;

		return false;
	}

	@Override
	public SequestPeptide getPeptide() {
		return (SequestPeptide) super.getPeptide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 *      .PeptideListReader#getSearchParameter()
	 */
	@Override
	public SequestParameter getSearchParameter() {
		return (SequestParameter) super.getSearchParameter();
	}
}
