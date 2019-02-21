/* 
 ******************************************************************************
 * File: DtaReaderFactory.java * * * Created on 03-29-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ms2.MS2DtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzDataStaxReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;

/**
 * The factory for dta reader.
 * 
 * @author Xinning
 * @version 0.1.1, 08-10-2009, 16:45:46
 */
public class DtaReaderFactory {

	private DtaReaderFactory() {
	}

	/**
	 * Create the reader for different type of dta files
	 * 
	 * @param type
	 * @param path
	 * @return
	 * @throws DtaFileParsingException 
	 * @throws FileNotFoundException 
	 * @throws XMLStreamException 
	 */
	public static IBatchDtaReader createReader(DtaType type, String path) throws DtaFileParsingException, FileNotFoundException, XMLStreamException
	        {
		return createReader(type, new File(path));
	}

	/**
	 * @throws DtaFileParsingException 
	 * Create the reader for different type of dta files
	 * 
	 * @param type
	 * @param path
	 * @return
	 * @throws  DtaFileParsingException
	 * @throws FileNotFoundException 
	 * @throws XMLStreamException 
	 */
	public static IBatchDtaReader createReader(DtaType type, File pathfile) throws DtaFileParsingException, FileNotFoundException, XMLStreamException
	        {

		switch (type) {
		case DTA:
			return new SequestBatchDtaReader(pathfile);
		case MGF:
			return new MgfReader(pathfile);
		case MS2:
			return new MS2DtaReader(pathfile);
		case MZXML:
			return new MzXMLReader(pathfile);
		case MZDATA:
			return new MzDataStaxReader(pathfile);
		}

		throw new IllegalArgumentException("Unkown type fo the dta file.");

	}
}
