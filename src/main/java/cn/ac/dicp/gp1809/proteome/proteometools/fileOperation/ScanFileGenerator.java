package cn.ac.dicp.gp1809.proteome.proteometools.fileOperation;

/*
 ******************************************************************************
 * File: ScanFileGenerator.java * * * Created on 06-15-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.util.SequestScanName;

/**
 * The scan file generator
 * 
 * @author Xinning
 * @version 0.1, 06-15-2009, 09:27:09
 */
public class ScanFileGenerator {

	private String filetype = null;
	private ScanFileUtil scanfileutil;
	private File directory;

	public ScanFileGenerator(ScanFileUtil scanfileutil) {
		this.filetype = scanfileutil.fileType();
		this.scanfileutil = scanfileutil;
		this.directory = scanfileutil.parent;
	}

	/**
	 * The scan filter generator
	 * 
	 * @param dir
	 * @param extension
	 *            extension ("dta" or "out")
	 */
	public ScanFileGenerator(String dir, String extension) {
		this(new ScanFileUtil(dir, extension));
	}

	/**
	 * The extension of the file
	 * 
	 * @return
	 */
	public String fileType() {
		return this.filetype;
	}

	/**
	 * @return file of the containing folder;
	 */
	public File getParent() {
		return this.directory;
	}

	/**
	 * Name of the file (not path)
	 * 
	 * @param scan
	 *            sdf
	 * @param charge
	 *            s
	 * @return filenames
	 */
	public String getFileName(int scan, short charge) {
		return this.scanfileutil.getScanFileName(scan, charge).getScanName();
	}

	/**
	 * @param scan
	 * @param charge
	 * @return file with this scan number and charge state;
	 */
	public File getFile(int scan, short charge) {
		return new File(this.directory, getFileName(scan, charge));
	}

	/**
	 * Get the files for the scan and charge
	 * 
	 * @param scans
	 * @param charges
	 * @return
	 */
	public String[] getFileNames(int[] scans, short[] charges) {
		int size = scans.length;

		if (size != charges.length) {
			throw new IllegalArgumentException("The length of the names");
		}

		ArrayList<String> namelist = new ArrayList<String>();

		for (int i = 0; i < size; i++) {
			String name = this.scanfileutil.getScanFileName(scans[i],
			        charges[i]).getScanName();

			if (name != null)
				namelist.add(name);
		}

		return namelist.toArray(new String[namelist.size()]);
	}

	/**
	 * filename with no duplicates;(merged by Hashset, order reseted) the
	 * duplicates come from the scan with more than one origin spectra
	 * 
	 * @param scancharge
	 * @return filename with no duplicated;
	 */
	public String[] getUndupeFileNames(int[] scans, short[] charges) {

		int size = scans.length;

		if (size != charges.length) {
			throw new IllegalArgumentException("The length of the names");
		}

		HashSet<String> nameset = new HashSet<String>();

		for (int i = 0; i < size; i++) {
			SequestScanName scanname = this.scanfileutil.getScanFileName(
			        scans[i], charges[i]);

			if (scanname != null) {
				nameset.add(scanname.getScanName());
			}
			else
				System.err.println("Warnning: cannot find file with scan \""
				        + scans[i] + "\" & charge \"" + charges[i] + "\", skip.");
		}

		return nameset.toArray(new String[0]);
	}

}
