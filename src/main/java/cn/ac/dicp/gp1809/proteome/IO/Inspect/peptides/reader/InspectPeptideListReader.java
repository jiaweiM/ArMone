/*
 * *****************************************************************************
 * File: InspectPeptideListReader.java * * * Created on 03-24-2009
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.reader;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.Inspect.InspectParameter;
import cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.InspectPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;

/**
 * Peptide list reader for Inspect outputted peptide list.
 * 
 * @author Xinning
 * @version 0.1, 03-24-2009, 15:45:22
 */
public class InspectPeptideListReader extends PeptideListReader implements
        IInspectPeptideReader {

	/**
	 * Create a Inspect peptide list reader from an existing peptide list file.
	 * 
	 * <p>
	 * If the existing list is not a Inspect peptide list file (the peptide list
	 * file is not created from a Inspect database search resource),
	 * ReaderGenerateException will be threw.
	 * 
	 * @param listfile
	 * @throws ReaderGenerateException
	 *             if the list reader is not reading a peptide list from Inspect
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public InspectPeptideListReader(String listfile)
	        throws ReaderGenerateException, FileDamageException, IOException {
		this(new File(listfile));
	}

	/**
	 * Create a Inspect peptide list reader from an existing peptide list file.
	 * 
	 * <p>
	 * If the existing list is not a Inspect peptide list file (the peptide list
	 * file is not created from a Inspect database search resource),
	 * ReaderGenerateException will be threw.
	 * 
	 * @param listfile
	 * @throws ReaderGenerateException
	 *             if the list reader is not reading a peptide list from Inspect
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public InspectPeptideListReader(File listfile)
	        throws ReaderGenerateException, FileDamageException, IOException {
		super(listfile);

		if (!this.validate())
			throw new ReaderGenerateException(
			        "Cann't create Inspect PeptideListReader for a peptide list file "
			                + "which was not generated from Inspect output");

	}

	/**
	 * Test whether the specific peptide list reader can be converted (The
	 * peptide list file is Inspect peptide list file). The validation rely
	 * mainly on the getpeptideType();
	 * 
	 * @return true is the list reader is reading a Mascot outputted list file
	 */
	protected boolean validate() {
		if (this.getPeptideType() == PeptideType.INSPECT)
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
	public InspectPeptide getPeptide() {
		return (InspectPeptide) super.getPeptide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 * .PeptideListReader#getSearchParameter()
	 */
	@Override
	public InspectParameter getSearchParameter() {
		return (InspectParameter) super.getSearchParameter();
	}

}
