/*
 * *****************************************************************************
 * File: OMSSAPeptideListReader.java * * * Created on 09-07-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.readers;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.OMMSA.OMSSAParameter;
import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.OMSSAPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;

/**
 * Peptide list reader for OMSSA outputted peptide list.
 * 
 * @author Xinning
 * @version 0.1, 09-07-2008, 09:52:34
 */
public class OMSSAPeptideListReader extends PeptideListReader implements
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
	public OMSSAPeptideListReader(String listfile)
	        throws ReaderGenerateException, FileDamageException, IOException {
		this(new File(listfile));
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
	public OMSSAPeptideListReader(File listfile)
	        throws ReaderGenerateException, FileDamageException, IOException {
		super(listfile);

		if (!this.validate())
			throw new ReaderGenerateException(
			        "Cann't create OMSSA PeptideListReader for a peptide list file "
			                + "which was not generated from OMSSA output");

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
