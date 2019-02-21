/*
 ******************************************************************************
 * File: ScanFromFile.java * * * Created on 06-15-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.fileOperation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import cn.ac.dicp.gp1809.util.StringUtil;
import cn.ac.dicp.gp1809.util.arrayutil.IntArrayList;

/**
 * get scannumber and charge states from a plan file, the format as below:
 * 
 * scan(blank||tab)charge
 * 
 * @author Xinning
 * @version 0.1, 06-15-2009, 09:59:16
 */
public class ScanFromFile {
	private static final String SEPERATOR = "[ \t,]";

	private String filename = null;
	private int[] scans;
	private short[] charges;
	private char separator;

	/**
	 * The scans in the file of scan charge list
	 * 
	 * @param filename
	 * @throws IOException 
	 */
	public ScanFromFile(String filename) throws IOException {
		this.filename = filename;
		this.read();
	}

	/**
	 * The scans in the file of scan charge list
	 * 
	 * @param filename
	 * @throws IOException 
	 */
	public ScanFromFile(String filename, char separator) throws IOException {
		this.filename = filename;
		this.separator = separator;
		this.read();
	}

	/**
	 * Parse
	 * @throws IOException 
	 */
	private void read() throws IOException {
		IntArrayList scanlist = new IntArrayList();
		IntArrayList chargelist = new IntArrayList();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = null;

			while ((line = reader.readLine()) != null && line.length() != 0) {
				String[] scancharge = null;

				if (this.separator == 0) {
					scancharge = line.split(SEPERATOR);
				} else {
					scancharge = StringUtil.split(line, separator);
				}

				scanlist.add(Integer.parseInt(scancharge[0]));
				chargelist.add(Short.parseShort(scancharge[1]));
			}

			reader.close();

		}catch(IOException e) {
			throw e;
		}
		catch (Exception e) {
			throw new IllegalArgumentException(
			        "The scans in the file must be with the format of: scan(blank or tab)charge",
			        e);
		}

		this.scans = scanlist.toArray();
		this.charges = chargelist.toShortArray();
	}

	/**
	 * The scans in the file list
	 * 
	 * @return
	 */
	public int[] getScans() {
		return this.scans;
	}

	/**
	 * The charges in the file list
	 * 
	 * @return
	 */
	public short[] getCharges() {
		return this.charges;
	}
}
