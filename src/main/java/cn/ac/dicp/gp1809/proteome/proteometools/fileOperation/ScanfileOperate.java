/*
 ******************************************************************************
 * File: ScanfileOperate.java * * * Created on 06-15-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.fileOperation;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.util.ioUtil.FileUtil;

/**
 * Some utilities for the directory contains dta and out files
 * 
 * @author Xinning
 * @version 0.1, 06-15-2009, 10:13:41
 */
public class ScanfileOperate {
	private ScanfileOperate() {

	}

	/**
	 * Get the scan and charge list from a file, distill and copy them to the
	 * new directory. The default type is dta file. If the out files also exist,
	 * they are copied too.
	 * 
	 * @param scanlistfile
	 *            text file contains scannumber and charge
	 * @param originfolder
	 *            origin folder
	 * @param targetfolder
	 *            target folder
	 * @throws IOException
	 */
	public static void batchCopy(String scanlistfile_path, String originfolder,
	        String targetfolder) throws IOException {
		ScanFromFile sffile = new ScanFromFile(scanlistfile_path);
		int[] scans = sffile.getScans();
		short[] charges = sffile.getCharges();

		batchCopy(scans, charges, originfolder, targetfolder);
	}

	/**
	 * Copy the selected scan and charges from the original folder to the target
	 * folder. If the out file existed for the selected scan, both dta and out
	 * are copied.
	 * 
	 * @param scancharge
	 * @param originfolder
	 * @param targetfolder
	 * @throws IOException
	 */
	public static void batchCopy(int scans[], short[] charges,
	        String originfolder, String targetfolder) throws IOException {
		File originfolderfile = new File(originfolder);
		File targetfolderfile = new File(targetfolder);

		ScanFileUtil sfutil = new ScanFileUtil(originfolder,
		        ScanFileUtil.DTA_FILE);
		ScanFileGenerator sfgenerator = new ScanFileGenerator(sfutil);

		String[] filenames = sfgenerator.getUndupeFileNames(scans, charges);

		if (!targetfolderfile.exists()) {
			targetfolderfile.mkdirs();
		}

		for (String filename : filenames) {
			FileUtil.copyTo(new File(originfolderfile, filename),
			        targetfolderfile);

			File out = new File(originfolderfile, filename.substring(0,
			        filename.length() - 3)
			        + "out");

			if (out.exists())
				FileUtil.copyTo(out, targetfolderfile);
		}
	}

	private static String usage() {
		return "BatchCopy file_scanlist source_dir target_dir";
	}

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 3)
			System.out.println(usage());
		else
			batchCopy(args[0], args[1], args[2]);
	}
}
