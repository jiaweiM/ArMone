/* 
 ******************************************************************************
 * File: MgfWriter.java * * * Created on 09-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.ms2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.util.IScanName;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * Writer to construct a MS2 file. The MS2 file format is used to record MS/MS
 * spectra. A full description of the MS2 file format may be found in:
 * McDonald,W.H. et al. MS1, MS2, and SQT-three unified, compact, and easily
 * parsed file formats for the storage of shotgun proteomic spectra and
 * identifications. Rapid Commun. Mass Spectrom. 18, 2162-2168 (2004).
 * 
 * <p>
 * Changes:
 * <li>0.2, 04-04-2009: merge the adjacent same spectrum with different charge
 * state together
 * 
 * @author Xinning
 * @version 0.2.1, 08-10-2009, 19:33:15
 */
public class MS2DtaWriter implements IBatchDtaWriter {

	private static final String lineSeparator = IOConstant.lineSeparator;

	private static final DecimalFormat DF6 = DecimalFormats.DF0_6;

	private PrintWriter pw;

	private String baseName;
	private int preScanBeg, preScanEnd;

	private IScanDta preDta;

	//Contains the "S" line
	private StringBuilder preHeader;

	/**
	 * 
	 * @param mgffile
	 *            the path of output mgf file
	 * @param titleParam
	 *            the global title parameters.
	 * @throws DtaWritingException
	 */
	public MS2DtaWriter(String mgffile, MS2Header titleParam)
	        throws DtaWritingException {
		try {
			this.pw = new PrintWriter(new BufferedWriter(
			        new FileWriter(mgffile)));
		} catch (IOException e) {
			throw new DtaWritingException(e);
		}

		if (titleParam != null)
			this.pw.print(titleParam.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IDtaWriter
	 * #write(cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFile)
	 */
	@Override
	public void write(IScanDta dtafile) {

		IScanName scanname = dtafile.getScanName();
		String basename = scanname.getBaseName();
		int scanBeg = scanname.getScanNumBeg();
		int scanEnd = scanname.getScanNumEnd();

		boolean sameSpectrum = true;
		if (this.baseName == null) {
			if (this.baseName != basename)
				sameSpectrum = false;
		}

		if (this.preScanBeg != scanBeg)
			sameSpectrum = false;

		if (this.preScanEnd != scanEnd)
			sameSpectrum = false;

		if (sameSpectrum) {
			preHeader.append("Z\t").append(dtafile.getCharge()).append('\t')
			        .append(DF6.format(dtafile.getPrecursorMH())).append(
			                lineSeparator);
		} else {
			this.writePreDta();

			this.preHeader = new StringBuilder(1000);

			preHeader.append("S\t").append(dtafile.getScanNumberBeg()).append(
			        '\t').append(dtafile.getScanNumberEnd()).append('\t')
			        .append(DF6.format(dtafile.getPrecursorMZ())).append(
			                lineSeparator);
			preHeader.append("Z\t").append(dtafile.getCharge()).append('\t')
			        .append(DF6.format(dtafile.getPrecursorMH())).append(
			                lineSeparator);

			this.preDta = dtafile;
			this.baseName = basename;
			this.preScanBeg = scanBeg;
			this.preScanEnd = scanEnd;
		}
	}

	/**
	 * Write pre dta to the file
	 * 
	 */
	private void writePreDta() {
		if (this.preHeader != null) {
			StringBuilder sb = this.preHeader;
			IMS2PeakList peaks = this.preDta.getPeakList();

			if (peaks == null)
				throw new NullPointerException(
				        "Current dta for writing contains no peak list.");

			int size = peaks.size();

			for (int i = 0; i < size; i++) {
				IPeak peak = peaks.getPeak(i);
				sb.append(peak.getMz()).append(' ').append(peak.getIntensity())
				        .append(lineSeparator);
			}

			this.pw.print(sb);
		}

		this.preHeader = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IDtaWriter#close()
	 */
	@Override
	public void close() {
		this.writePreDta();
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
	public static class MS2Header {

		private LinkedHashMap<String, String> params;

		public MS2Header() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaWriter#getDtaType()
	 */
	@Override
	public DtaType getDtaType() {
		return DtaType.MS2;
	}

}
