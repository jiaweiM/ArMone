/* 
 ******************************************************************************
 * File: MS1DtaWriter.java * * * Created on 04-05-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.fullms.ms1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS1Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IScanWriter;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * Writer to construct a MS1 file. The MS1 file format is used to record MS1
 * spectra. A full description of the MS2 file format may be found in:
 * McDonald,W.H. et al. MS1, MS2, and SQT-three unified, compact, and easily
 * parsed file formats for the storage of shotgun proteomic spectra and
 * identifications. Rapid Commun. Mass Spectrom. 18, 2162-2168 (2004).
 * 
 * 
 * @author Xinning
 * @version 0.1, 04-05-2010, 17:28:18
 */
public class MS1DtaWriter implements IScanWriter {

	private static final String lineSeparator = "\n"; //IOConstant.lineSeparator;

	private PrintWriter pw;

	/**
	 * 
	 * @param mgffile
	 *            the path of output mgf file
	 * @param titleParam
	 *            the global title parameters.
	 * @throws DtaWritingException
	 */
	public MS1DtaWriter(String file, MS1Header titleParam)
	        throws DtaWritingException {
		try {
			this.pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		} catch (IOException e) {
			throw new DtaWritingException(e);
		}

		if (titleParam != null)
			this.pw.print(titleParam.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IScanWriter#write(cn.ac.dicp
	 * .gp1809.proteome.spectrum.rawdata.IScan)
	 */
	public void write(ISpectrum scan) {

		if(scan == null || scan.getMSLevel() != 1) {
			return ;
		}
		
		int num = scan.getScanNum();
		double rt = ((IMS1Scan)scan).getRTMinute();

		StringBuilder sb = new StringBuilder(1000);
		String numstr = DecimalFormats.DF6_0.format(num);
		sb.append("S\t").append(numstr).append('\t').append(numstr).append(lineSeparator)
		      .append("I\tRetTime\t").append(DecimalFormats.DF0_2.format(rt)).append(lineSeparator);

		IPeakList peaklist = scan.getPeakList();
		if (peaklist != null) {
			IPeak[] peaks = peaklist.getPeakArray();
			
			for (IPeak peak : peaks) {
				sb.append(DecimalFormats.DF0_4.format(peak.getMz()))
				        .append(' ').append(
				                DecimalFormats.DF0_1
				                        .format(peak.getIntensity())).append(lineSeparator);
			}
		}

		this.pw.print(sb);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IDtaWriter#close()
	 */
	@Override
	public void close() {
		this.pw.close();
	}

	/**
	 * 
	 * The header for the MS2 file. The input entry and its corresponding value
	 * should be validated.
	 * 
	 * @author Xinning
	 * @version 0.1, 09-29-2008, 13:55:58
	 */
	public static class MS1Header {

		private LinkedHashMap<String, String> params;

		public MS1Header() {
			this.params = new LinkedHashMap<String, String>();

			this.addParameter("CreationDate", new SimpleDateFormat(
			        "MM/dd/yyyy KK:mm:ss aaa", Locale.US).format(new Date()));
		}

		/**
		 * Add a parameter.
		 * 
		 * @param entry
		 * @param value
		 * @return
		 */
		public boolean addParameter(String entry, String value) {

			if (!validate(entry, value))
				return false;

			this.params.put(entry, value);

			return true;
		}

		/*
		 * Validate the entry and its corresponding value.
		 */
		private static boolean validate(String entry, String value) {

			// Validation has not been used, current accept all entries
			return true;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			for (Iterator<Entry<String, String>> iterator = this.params
			        .entrySet().iterator(); iterator.hasNext();) {
				Entry<String, String> entry = iterator.next();
				sb.append("H\t").append(entry.getKey()).append('\t').append(
				        entry.getValue()).append(lineSeparator);
			}

			return sb.toString();
		}
	}

}
