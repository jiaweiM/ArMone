/* 
 ******************************************************************************
 * File: SequestPeptideListReader.java * * * Created on 08-31-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SequestPeptide;

/**
 * Peptide list reader for sequest outputted peptide list.
 * 
 * @author Xinning
 * @version 0.1, 08-31-2008, 09:20:49
 */
public class SequestPeptideListReader extends PeptideListReader implements
        ISequestPeptideReader {

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
	public SequestPeptideListReader(String listfile)
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
	public SequestPeptideListReader(File listfile)
	        throws ReaderGenerateException, FileDamageException, IOException {
		super(listfile);

		if (!this.validate())
			throw new ReaderGenerateException(
			        "Cann't create SequestPeptideListReader for a peptide "
			                + "list file which was not generated from sequest output");

	}

	/**
	 * Test whether the specific peptide list reader can be converted. The
	 * validation rely mainly on the PetpideFormat used to format the peptide
	 * form the list string
	 * 
	 * @param listReader
	 * @return true is the list reader is reading a sequest outputted list file
	 */
	protected boolean validate() {
		if (this.getPeptideType() == PeptideType.SEQUEST)
			return true;

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 *      .PeptideListReader#getPeptide()
	 */
	@Override
	public SequestPeptide getPeptide() {
		return (SequestPeptide) super.getPeptide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.
	 *      PeptideListReader#getSearchParameter()
	 */
	@Override
	public SequestParameter getSearchParameter() {
		return (SequestParameter) super.getSearchParameter();
	}
}
