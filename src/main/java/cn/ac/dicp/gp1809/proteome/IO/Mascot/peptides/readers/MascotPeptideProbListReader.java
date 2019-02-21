/*
 * *****************************************************************************
 * File: MascotPeptideProbListReader.java * * * Created on 11-18-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.readers;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotParameter;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideProbListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;

/**
 * Reader for Mascot peptide probability list file
 * 
 * @author Xinning
 * @version 0.1, 11-18-2008, 09:38:33
 */
public class MascotPeptideProbListReader extends PeptideProbListReader implements
        IMascotPeptideReader {

	/**
	 * Create a Mascot peptide list reader from an existing peptide list file.
	 * 
	 * <p>
	 * If the existing list is not a Mascot peptide list file (the peptide list
	 * file is not created from a Mascot database search resource),
	 * ReaderGenerateException will be threw.
	 * 
	 * @param listfile
	 * @throws ReaderGenerateException
	 *             if the list reader is not reading a peptide list from Mascot
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public MascotPeptideProbListReader(File file) throws FileDamageException,
	        IOException, ReaderGenerateException {
		super(file);

		if (!this.validate())
			throw new ReaderGenerateException(
			        "Cann't create Mascot PeptideListReader for a peptide list file "
			                + "which was not generated from Mascot output");
	}

	/**
	 * Create a Mascot peptide list reader from an existing peptide list file.
	 * 
	 * <p>
	 * If the existing list is not a Mascot peptide list file (the peptide list
	 * file is not created from a Mascot database search resource),
	 * ReaderGenerateException will be threw.
	 * 
	 * @param listfile
	 * @throws ReaderGenerateException
	 *             if the list reader is not reading a peptide list from Mascot
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public MascotPeptideProbListReader(String listfile)
	        throws FileDamageException, IOException, ReaderGenerateException {
		this(new File(listfile));
	}

	/**
	 * Test whether the specific peptide list reader can be converted (The
	 * peptide list file is Mascot peptide list file). The validation rely mainly
	 * on the getpeptideType();
	 * 
	 * @return true is the list reader is reading a Mascot outputted list file
	 */
	protected boolean validate() {
		if (this.getPeptideType() == PeptideType.MASCOT)
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
	public MascotPeptide getPeptide() {
		return (MascotPeptide) super.getPeptide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 *      .PeptideListReader#getSearchParameter()
	 */
	@Override
	public MascotParameter getSearchParameter() {
		return (MascotParameter) super.getSearchParameter();
	}
}
