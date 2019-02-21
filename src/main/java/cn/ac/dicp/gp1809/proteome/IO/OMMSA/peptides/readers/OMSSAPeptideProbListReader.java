/* 
 ******************************************************************************
 * File: OMSSAPeptideProbListReader.java * * * Created on 09-07-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.readers;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.OMMSA.OMSSAParameter;
import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.OMSSAPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideProbListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;

/**
 * Reader for OMSSA peptide probability list file
 * 
 * @author Xinning
 * @version 0.1, 09-07-2008, 15:24:28
 */
public class OMSSAPeptideProbListReader extends PeptideProbListReader implements
        IOMSSAPeptideReader {

	/**
	 * Create a OMSSA peptide list reader from an existing peptide list file.
	 * 
	 * <p>
	 * If the existing list is not a OMSSA peptide list file (the peptide list
	 * file is not created from a OMSSA database search resource),
	 * ReaderGenerateException will be threw.
	 * 
	 * @param listfile
	 * @throws ReaderGenerateException
	 *             if the list reader is not reading a peptide list from OMSSA
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public OMSSAPeptideProbListReader(File file) throws FileDamageException,
	        IOException, ReaderGenerateException {
		super(file);

		if (!this.validate())
			throw new ReaderGenerateException(
			        "Cann't create OMSSA PeptideListReader for a peptide list file "
			                + "which was not generated from OMSSA output");
	}

	/**
	 * Create a OMSSA peptide list reader from an existing peptide list file.
	 * 
	 * <p>
	 * If the existing list is not a OMSSA peptide list file (the peptide list
	 * file is not created from a OMSSA database search resource),
	 * ReaderGenerateException will be threw.
	 * 
	 * @param listfile
	 * @throws ReaderGenerateException
	 *             if the list reader is not reading a peptide list from OMSSA
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public OMSSAPeptideProbListReader(String listfile)
	        throws FileDamageException, IOException, ReaderGenerateException {
		this(new File(listfile));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO
	 *      .ioUtil.PeptideListReader#getPeptide()
	 */
	@Override
	public OMSSAPeptide getPeptide() {
		return (OMSSAPeptide) super.getPeptide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 *      .PeptideListReader#getSearchParameter()
	 */
	@Override
	public OMSSAParameter getSearchParameter() {
		return (OMSSAParameter) super.getSearchParameter();
	}
}
