/*
 * *****************************************************************************
 * File: SEQUESTXMLReader.java * * * Created on 03-25-2008
 * 
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.export;

import java.io.File;

import cn.ac.dicp.gp1809.exceptions.UnSupportingMethodException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.AbstractSequestPeptideReader;
import cn.ac.dicp.gp1809.util.ioUtil.xml.XmlReadingException;

/**
 * Reader for the xml file outputed by Sequest after database search. Results
 * are first loaded by bioworks and then output as xml (or excel). The excel
 * output format can be read by ExcelProReader.
 * 
 * @author Xinning
 * @version 0.3.2, 05-02-2010, 10:31:01
 */
@SuppressWarnings("unchecked")
public abstract class SEQUESTXMLReader extends AbstractSequestPeptideReader {

	/**
	 * Create a XML reader using stax xml parser.
	 */
	public static final int READER_STAX = 0;

	/**
	 * Create a XML reader as a document reader (plain txt reader).
	 */
	public static final int READER_DOC = 1;

	/**
	 * The name of the original raw file, "XXXXX" may be null or ""; If null,
	 * the result was loaded in multiple result mode.
	 * <p>
	 * <b>Note: not extension ".raw"</b>
	 */
	protected String originfile;

	/**
	 * The file name of the xml
	 */
	protected String filename;

	/**
	 * If use the filename instead of the original raw file name. If the xml
	 * contains no raw filename information, this value will be true, and the
	 * xml filename will be used.
	 */
	protected boolean usefilename = true;

	//version information of bioworks
	protected String bioworksinfo;

	//name of current protein for the peptides
	protected String currentProtein = null;

	private SequestParameter parameter;

	/**
	 * Create a xml reader using default parser : STAX
	 * 
	 * @param file
	 * @return reader
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws XmlReadingException
	 */
	public static final SEQUESTXMLReader createReader(File file,
	        SequestParameter parameter) throws ImpactReaderTypeException,
	        ReaderGenerateException, XmlReadingException {
		return createReader(file, parameter, READER_STAX);
	}

	/**
	 * Create a xml reader using selected parser: doc or stax
	 * 
	 * @param file
	 * @param parser
	 *            type @see READER_STAX and READER_DOC
	 * @return reader
	 * @throws ReaderGenerateException
	 * @throws ImpactReaderTypeException
	 * @throws XmlReadingException
	 */
	public static final SEQUESTXMLReader createReader(File file,
	        SequestParameter parameter, int parser)
	        throws ImpactReaderTypeException, ReaderGenerateException,
	        XmlReadingException {

		if (parser == READER_STAX) {
			return new SEQUESTXMLSTAXReader(file, parameter);
		} else if (parser == READER_DOC) {
			return new SEQUESTXMLDocReader(file, parameter);
		} else {
			System.out
			        .println("Illegal parser type, default xml reader is returned!");

			return new SEQUESTXMLDocReader(file, parameter);
		}
	}

	protected SEQUESTXMLReader(String filename, SequestParameter parameter) {
		this(new File(filename), parameter);
	}

	protected SEQUESTXMLReader(File file, SequestParameter parameter) {
		super(file);

		this.filename = file.getName();
		this.parameter = parameter;
	}

	/**
	 * Triger the information of each cell for peptide creation. So that
	 * informations can be quickly filled into peptide information array.
	 * 
	 * proteinlabelposition[] and peptidelabelposition[] should be filled.
	 */
	protected abstract void trigerPositionInfo();

	/**
	 * move the point to the proper position for read in so that peptide can be
	 * read directly.
	 * 
	 * @throws XmlReadingException
	 * @throws
	 */
	protected abstract void getReadeyToRead() throws XmlReadingException;

	/**
	 * The name of the original raw file, "XXXXX.raw" may be null; If null, the
	 * result was loaded in multi result mode.
	 * 
	 * @return the raw file name.
	 */
	//	public String getRawFile(){
	//		return this.originfile+".raw";
	//	}

	/**
	 * @return the version information of bioworks
	 */
	public String getBioworksInfo() {
		return this.bioworksinfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideReader#getSearchParameter
	 * ()
	 */
	public SequestParameter getSearchParameter() {
		return this.parameter;
	}

	/**
	 * Always return the maximum integer value
	 */
	@Override
	public int getTopN() {
		return Integer.MAX_VALUE;
	}

	/**
	 * throw new UnSupportingMethodException
	 */
	@Override
	public void setTopN(int topn) {
		throw new UnSupportingMethodException(
		        "Cannot limit the top n for peptide reading.");
	}
}
