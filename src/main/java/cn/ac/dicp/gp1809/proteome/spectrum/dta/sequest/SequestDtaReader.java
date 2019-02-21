/*
 * *****************************************************************************
 * File: DtaReader.java * * * Created on 09-29-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IDtaReader;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;

/**
 * Reader for DTA file from Sequest.
 * 
 * @author Xinning
 * @version 0.2.6, 06-10-2009, 16:59:47
 */
public class SequestDtaReader implements IDtaReader {

	private File dtafile;

	private BufferedReader reader;

	private SequestScanName sequestname;

	private boolean isCloseAfterReading;

	/**
	 * Create a reader for the dta file
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public SequestDtaReader(File file) throws FileNotFoundException {
		this(new FileInputStream(file), true, file, file.getName());
	}

	/**
	 * Create a reader from a dta file
	 * 
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public SequestDtaReader(String filename) throws FileNotFoundException {
		this(new File(filename));
	}

	/**
	 * Create a reader from a dta file
	 * 
	 * @param filename
	 *            filename of the dtafile, can NOT be null, the scan number and
	 *            preference are genereted from this name.
	 * @param dtafile
	 *            : if the dta is read from a independent dta file, this file
	 *            should be this file; otherwise, if the dta file is read from a
	 *            visual dta file (e.g. from the zipped dta file), this file can
	 *            be null.
	 * @param instream
	 *            the input stream
	 * @param isCloseAfterReading
	 *            if close the stream after reading of dta file
	 */
	public SequestDtaReader(InputStream instream, boolean isCloseAfterReading,
	        File dtafile, String filename) {
		// this.dtafile = null;

		this.reader = new BufferedReader(new InputStreamReader(instream));
		this.isCloseAfterReading = isCloseAfterReading;
		this.dtafile = dtafile;
		this.sequestname = new SequestScanName(filename);
	}

	/**
	 * Create a reader from a dta file
	 * 
	 * @param preference
	 *            preference of the dta file.
	 * @param scanNumBeg
	 *            the begin scan number of the dta file.
	 * @param scanNumEnd
	 *            the end scan number fo the dta file.
	 * @param dtafile
	 *            : if the dta is read from a independent dta file, this file
	 *            should be this file; otherwise, if the dta file is read from a
	 *            visual dta file (e.g. from the zipped dta file), this file can
	 *            be null.
	 * @param instream
	 *            the input stream
	 * @param isCloseAfterReading
	 *            if close the stream after reading of dta file
	 */
	public SequestDtaReader(InputStream instream, boolean isCloseAfterReading,
	        File dtafile, String baseName, int scanNumBeg, int scanNumEnd) {
		this.reader = new BufferedReader(new InputStreamReader(instream));
		this.isCloseAfterReading = isCloseAfterReading;
		this.dtafile = dtafile;

		this.sequestname = new SequestScanName(baseName, scanNumBeg,
		        scanNumEnd, (short) 0, "dta");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.spectrum.dta.IDtaReader#getDtaFile(boolean)
	 */
	@Override
	public SequestScanDta getDtaFile(boolean isIncludePeakList)
	        throws DtaFileParsingException {

		try {
			String line = reader.readLine();// first line

			String [] ss = line.split("\\s+");
//			int idx = line.indexOf("\\s+");
//			double precursorMH = Double.parseDouble(line.substring(0, idx));
			//charge is read from the inside of the file
//			short charge = Short.parseShort(line.substring(idx + 1).trim());

			double precursorMH = Double.parseDouble(ss[0]);
			short charge = Short.parseShort(ss[1]);
			
			this.sequestname.setCharge(charge);

			SequestScanDta dta = new SequestScanDta(this.dtafile,
			        this.sequestname, precursorMH);

			if (isIncludePeakList) {
				PrecursePeak parent = new PrecursePeak();
				parent.setCharge(charge);
				parent.setMH(precursorMH);
				IMS2PeakList peaks = new MS2PeakList();
				peaks.setPrecursePeak(parent);

				while ((line = reader.readLine()) != null) {
					if (line.length() == 0)
						break;
/*
					int index = line.indexOf("\\s+");
					double intense = Double.parseDouble(line
					        .substring(index + 1));
					double mz = Double.parseDouble(line.substring(0, index));
*/
					String [] sss = line.split("\\s+");
					double mz = Double.parseDouble(sss[0]);
					double intense = Double.parseDouble(sss[1]);
					peaks.add(new Peak(mz, intense));
				}

				dta.setPeaks(peaks);
			}

			try {
				if (this.isCloseAfterReading)
					reader.close();
			} catch (IOException ioe) {
				System.err.println("Error occurs while closing the dta file, "
				        + "but it doesn't matter :)");
			}

			return dta;

		} catch (Exception e) {
			throw new DtaFileParsingException(
			        "Error occurs while reading the dta file: "
			                + e.getMessage(), e);
		}
	}

}
