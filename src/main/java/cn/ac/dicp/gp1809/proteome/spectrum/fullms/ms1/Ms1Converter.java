/* 
 ******************************************************************************
 * File: Mzxml2ms1Converter.java * * * Created on 04-05-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.fullms.ms1;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.fullms.ms1.MS1DtaWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.fullms.ms1.MS1DtaWriter.MS1Header;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IScanList;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;

/**
 * Convert mzxml to ms1 file
 * 
 * @author Xinning
 * @version 0.1, 04-05-2010, 22:14:49
 */
public class Ms1Converter {

	/**
	 * Convert the mzxml file to ms1 file.
	 * 
	 * @param mzxml
	 * @param ms1
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 * @throws DtaWritingException
	 */
/*	
	public static void convertFromMzxml(String mzxml, String ms1)
	        throws FileNotFoundException, XMLStreamException,
	        DtaWritingException {

		MzXMLStaxReader reader = new MzXMLStaxReader(mzxml);

		IScanList list = reader.rapScanList(true);
		int firstscan = list.getFirstScan();
		int lastscan = list.getLastScan();

		MS1Header header = new MS1Header();
		header.addParameter("Extractor", "MS1convertor");
		header.addParameter("Comment",
		        "MS1 convertor written by Xinning Jiang, 2010");
		header.addParameter("ScanType", "MS");

		header.addParameter("FirstScan", String.valueOf(firstscan));
		header.addParameter("LastScan", String.valueOf(lastscan));

		MS1DtaWriter writer = new MS1DtaWriter(ms1, header);

		IScan[] scans = list.getScans();
		for (IScan scan : scans) {
			if(scan.getMSLev() == 1) {
				writer.write(scan);
				continue;
			}
		}

		writer.close();
		reader.close();
	}
*/
	
	public static void convertFromMzxml(String mzxml, String ms1)
		throws FileNotFoundException, XMLStreamException,
		DtaWritingException {

		MzXMLReader reader = new MzXMLReader(mzxml);

		MS1Header header = new MS1Header();
		header.addParameter("Extractor", "MS1convertor");
		header.addParameter("Comment",
        "MS1 convertor written by Xinning Jiang, 2010");
		header.addParameter("ScanType", "MS");

		header.addParameter("FirstScan", String.valueOf(0));
		header.addParameter("LastScan", String.valueOf(0));

		MS1DtaWriter writer = new MS1DtaWriter(ms1, header);

		ISpectrum scan = null;
		while((scan=reader.getNextMS1Scan())!=null){
			if(scan.getMSLevel() == 1) {
				writer.write(scan);
				continue;
			}
		}

		writer.close();
		reader.close();
	}
	
	/**
	 * @param args
	 * @throws XMLStreamException
	 * @throws DtaWritingException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
	        DtaWritingException, XMLStreamException {

		String option = args[0];
		String input = args[1];
		String output = args[2];

		if (option.equals("-mzxml")) {
			Ms1Converter.convertFromMzxml(input, output);
		}

	}

}
