/*
 * *****************************************************************************
 * File: XTandemPeptideListReader.java * * * Created on 10-06-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.readers;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.XTandem.XTandemParameter;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.XTandemPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;

/**
 * Peptide list reader for X!Tandem outputted peptide list.
 * 
 * @author Xinning
 * @version 0.1, 10-06-2008, 14:23:48
 */
public class XTandemPeptideListReader extends PeptideListReader implements
        IXTandemPeptideReader {

	/**
	 * Create a X!Tandem peptide list reader from an existing peptide list file.
	 * 
	 * <p>
	 * If the existing list is not a X!Tandem peptide list file (the peptide list
	 * file is not created from a X!Tandem database search resource),
	 * ReaderGenerateException will be threw.
	 * 
	 * @param listfile
	 * @throws ReaderGenerateException
	 *             if the list reader is not reading a peptide list from X!Tandem
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public XTandemPeptideListReader(String listfile)
	        throws ReaderGenerateException, FileDamageException, IOException {
		this(new File(listfile));
	}

	/**
	 * Create a X!Tandem peptide list reader from an existing peptide list file.
	 * 
	 * <p>
	 * If the existing list is not a X!Tandem peptide list file (the peptide list
	 * file is not created from a X!Tandem database search resource),
	 * ReaderGenerateException will be threw.
	 * 
	 * @param listfile
	 * @throws ReaderGenerateException
	 *             if the list reader is not reading a peptide list from X!Tandem
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public XTandemPeptideListReader(File listfile)
	        throws ReaderGenerateException, FileDamageException, IOException {
		super(listfile);

		if (!this.validate())
			throw new ReaderGenerateException(
			        "Cann't create X!Tandem PeptideListReader for a peptide list file "
			                + "which was not generated from X!Tandem output");

	}

	/**
	 * Test whether the specific peptide list reader can be converted (The
	 * peptide list file is X!Tandem peptide list file). The validation rely mainly
	 * on the getpeptideType();
	 * 
	 * @return true is the list reader is reading a OMSSA outputted list file
	 */
	protected boolean validate() {
		if (this.getPeptideType() == PeptideType.XTANDEM)
			return true;

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader#getPeptide()
	 */
	@Override
	public XTandemPeptide getPeptide() {
		return (XTandemPeptide) super.getPeptide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 *      .PeptideListReader#getSearchParameter()
	 */
	@Override
	public XTandemParameter getSearchParameter() {
		return (XTandemParameter) super.getSearchParameter();
	}

}
