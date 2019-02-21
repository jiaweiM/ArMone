/*
 ******************************************************************************
 * File: XTandemMzxmlScanName.java * * * Created on 05-02-2010
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
 * The automatically assigined scan name by X!Tandem while the input file is mzxml
 * 
 * @author Xinning
 * @version 0.1, 05-02-2010, 19:15:09
 */
public class XTandemMzxmlScanName extends AbstractKnownScanName{

	/**
	 * Matches the dta standard scan name. For example, CGItemp35715 scan 1180 (charge 2)
	 */
	private static final Pattern DTA_SCAN = Pattern.compile(
	        "(.+) scan (\\d+) \\(charge (\\d)\\)",
	        Pattern.CASE_INSENSITIVE);

	/**
	 * The raw scan name
	 */
	private String scanname;
	

	/**
	 * Test whether the name is a well formatted X!Tandem mzxml scan name. For example,
	 * CGItemp35715 scan 1180 (charge 2)
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isFormat(String scanNum) {
		return (DTA_SCAN.matcher(scanNum).matches());
	}

	/**
	 * For example, CGItemp35715 scan 1180 (charge 2)
	 * 
	 * @param scanname
	 * @throws IllegalArgumentException
	 *             if the filename is not a valid xtandem filename
	 */
	public XTandemMzxmlScanName(String scanname) throws IllegalArgumentException {

		try {
			this.parseDtaScanNum(scanname);
		} catch (Exception e) {
			throw new IllegalArgumentException(
			        "Error in parsing the X!Tandem scanname: \"" + scanname
			                + "\".", e);
		}
	}

	/**
	 * Parse the dta formatted scan number. CGItemp35715 scan 1180 (charge 2)
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
			throw new IllegalArgumentException("Not a X!Tandem scan name.");
		}
		
		String basename = matcher.group(1);
		int scannumber = Integer.parseInt(matcher.group(2));
		short charge = Short.parseShort(matcher.group(3));
		
		this.setBaseName(basename);
		this.setScanNumBeg(scannumber);
		this.setScanNumEnd(scannumber);
		this.setCharge(charge);
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
    public XTandemMzxmlScanName deepClone() {
	    return this.clone();
    }
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public XTandemMzxmlScanName clone() {
		try {
	        return (XTandemMzxmlScanName) super.clone();
        } catch (CloneNotSupportedException e) {
	        e.printStackTrace();
        }
        
        return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String title = "CGItemp35715 scan 1180 (charge 2)";
		Matcher m = DTA_SCAN.matcher(title);
		System.out.println(m.matches());
		while(m.find()){   
            System.out.println(m.group());    
        }  

	}
	
}
