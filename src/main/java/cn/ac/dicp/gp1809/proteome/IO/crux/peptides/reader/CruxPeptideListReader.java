/*
 ******************************************************************************
 * File: CruxPeptideListReader.java * * * Created on 04-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.crux.peptides.reader;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.crux.CruxParameter;
import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.CruxPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;

/**
 * Peptide list reader for crux outputted peptide list.
 * 
 * @author Xinning
 * @version 0.1, 04-02-2009, 14:34:24
 */
public class CruxPeptideListReader extends PeptideListReader implements
        ICruxPeptideReader {

	/**
	 * Create a crux peptide list reader from an existing peptide list file.
	 * 
	 * <p>
	 * If the existing list is not a crux peptide list file (the peptide list
	 * file is not created from a crux database search resource),
	 * ReaderGenerateException will be threw.
	 * 
	 * @param listfile
	 * @throws ReaderGenerateException
	 *             if the list reader is not reading a peptide list from crux
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public CruxPeptideListReader(String listfile)
	        throws ReaderGenerateException, FileDamageException, IOException {
		this(new File(listfile));
	}

	/**
	 * Create a crux peptide list reader from an existing peptide list file.
	 * 
	 * <p>
	 * If the existing list is not a crux peptide list file (the peptide list
	 * file is not created from a crux database search resource),
	 * ReaderGenerateException will be threw.
	 * 
	 * @param listfile
	 * @throws ReaderGenerateException
	 *             if the list reader is not reading a peptide list from crux
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public CruxPeptideListReader(File listfile)
	        throws ReaderGenerateException, FileDamageException, IOException {
		super(listfile);

		if (!this.validate())
			throw new ReaderGenerateException(
			        "Cann't create Inspect PeptideListReader for a peptide list file "
			                + "which was not generated from crux output");

	}

	/**
	 * Test whether the specific peptide list reader can be converted (The
	 * peptide list file is Inspect peptide list file). The validation rely
	 * mainly on the getpeptideType();
	 * 
	 * @return true is the list reader is reading a crux outputted list file
	 */
	protected boolean validate() {
		if (this.getPeptideType() == PeptideType.CRUX)
			return true;

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO
	 * .ioUtil.PeptideListReader#getPeptide()
	 */
	@Override
	public CruxPeptide getPeptide() {
		return (CruxPeptide) super.getPeptide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 * .PeptideListReader#getSearchParameter()
	 */
	@Override
	public CruxParameter getSearchParameter() {
		return (CruxParameter) super.getSearchParameter();
	}

}
