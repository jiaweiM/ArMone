/*
 * *****************************************************************************
 * File: SequestScanDta.java * * * Created on 09-29-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ScanDta;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;

/**
 * A package of class containing all the informations of a dta file. These
 * informations include: dta name (preference, scan number begin and scan number
 * end, peaklist).
 * 
 * 
 * @author Xinning
 * @version 0.3.5, 06-04-2009, 10:20:03
 */
public class SequestScanDta extends ScanDta {

	private File dtafile;

	public SequestScanDta(File dtafile, SequestScanName scanName,
	        double precursorMH) {
		super(scanName, precursorMH);
		this.dtafile = dtafile;
	}

	public SequestScanDta(File dtafile, SequestScanName scanName,
	        IMS2PeakList peaks) {
		super(scanName, peaks);
		this.dtafile = dtafile;
	}

	/**
	 * The name of dta file for creation of this DtaFile instance.
	 * <p>
	 * <b>Note: If this DtaFile is created from srf file, this name will be the
	 * formatted out filename: "Basename.scanNumBeg.scanNumEnd.charge.dta"</b>
	 * 
	 * @return
	 */
	public String getFileName() {
		if (this.dtafile != null)
			return this.dtafile.getName();

		return this.getScanName().getScanName();
	}

	/**
	 * May be null when this dta file is read from srf file. Use
	 * getFormattedDtaName() to get the name of dta file.
	 * 
	 * @return the file of current dta file.
	 */
	public File getFile() {
		return this.dtafile;
	}

	/**
	 * The dta file path
	 * 
	 * @param dtafile
	 *            the dta file.
	 */
	public void setDtaFile(File dtafile) {
		this.dtafile = dtafile;
	}

	/**
	 * The preference of the file. (XXXX.1200.1201.2, return XXXX)
	 * 
	 * @return
	 */
	public String getBasename() {
		return this.getScanName().getBaseName();
	}

	/**
	 * The preference of the file. (XXXX.1200.1201.2, return XXXX)
	 * 
	 * @return
	 */
	public void setBasename(String baseName) {
		this.getScanName().setBaseName(baseName);
	}
}
