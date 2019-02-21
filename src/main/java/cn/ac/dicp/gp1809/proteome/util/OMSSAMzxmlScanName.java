/*
 ******************************************************************************
 * File: OMSSAMzxmlScanName.java * * * Created on 05-02-2010
 *
 * Copyright (c) 2010 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The automatically assigined scan name by omssa while the input file is mzxml
 * 
 * @author Xinning
 * @version 0.1, 05-02-2010, 19:15:09
 */
public class OMSSAMzxmlScanName extends AbstractKnownScanName{

	/**
	 * Matches the dta standard scan name. For example, Cmpd 166, +MSn(821.4), 8.7 min
	 */
	private static final Pattern DTA_SCAN = Pattern.compile(
	        "(.+) (\\d+), .+",
	        Pattern.CASE_INSENSITIVE);

	/**
	 * The raw scan name
	 */
	private String scanname;
	

	/**
	 * Test whether the name is a well formatted OMSSA mzxml scan name. For example,
	 * Cmpd 166, +MSn(821.4), 8.7 min
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isFormat(String scanNum) {
		return (DTA_SCAN.matcher(scanNum).matches());
	}

	/**
	 * For example, Cmpd 166, +MSn(821.4), 8.7 min
	 * 
	 * @param scanname
	 * @throws IllegalArgumentException
	 *             if the filename is not a valid omssa filename
	 */
	public OMSSAMzxmlScanName(String scanname) throws IllegalArgumentException {

		try {
			this.parseDtaScanNum(scanname);
		} catch (Exception e) {
			throw new IllegalArgumentException(
			        "Error in parsing the OMSSA scanname: \"" + scanname
			                + "\".", e);
		}
	}

	/**
	 * Parse the dta formatted scan number. Cmpd 166, +MSn(821.4), 8.7 min
	 * 
	 * @since 0.1.2
	 * 
	 * @param scanNum
	 * @return
	 */
	protected final void parseDtaScanNum(String scanNum) {

		this.scanname = scanNum;
		
		Matcher matcher = DTA_SCAN.matcher(scanNum);
		if(!matcher.matches()) {
			throw new IllegalArgumentException("Not a OMSSA scan name.");
		}
		
		String basename = matcher.group(1);
		int scannumber = Integer.parseInt(matcher.group(2));
		
		this.setBaseName(basename);
		this.setScanNumBeg(scannumber);
		this.setScanNumEnd(scannumber);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.util.IScanName#getScanName()
	 */
	public String getScanName() {
		return this.scanname;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public OMSSAMzxmlScanName deepClone() {
	    return this.clone();
    }
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public OMSSAMzxmlScanName clone() {
		try {
	        return (OMSSAMzxmlScanName) super.clone();
        } catch (CloneNotSupportedException e) {
	        e.printStackTrace();
        }
        
        return null;
	}
}
