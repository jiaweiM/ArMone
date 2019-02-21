/* 
 ******************************************************************************
 * File: RawSpectraReaderFactory.java * * * Created on 07-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * A factory used for the creation of RawSpectraReader
 * 
 * @author Xinning
 * @version 0.1, 07-29-2008, 21:06:24
 */
public class RawSpectraReaderFactory {

	/**
	 * Create the reader
	 * 
	 * @param type
	 * @param path
	 * @return
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * @throws DtaFileParsingException 
	 */
	public static IRawSpectraReader createReader(DtaType type, String path)
	        throws FileNotFoundException, XMLStreamException, DtaFileParsingException {

		switch (type) {
		case MZDATA:
			return new MzDataStaxReader(path);
		case MZXML:
//			return new MzXMLStaxReader(path);
			return new MzXMLReader(path);
		case MGF:
			return new MgfReader(path);
			
		default:
			throw new IllegalArgumentException(
			        "Not a raw format of mzdata or mzxml: " + type);
		}
	}

}
