/* 
 ******************************************************************************
 * File: FormattedScanName.java * * * Created on 03-03-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The formatted scan name with the the following formats: XXXXX, 0000 or XXXXX,
 * 0000 - 0000 or 0000 or 0000 - 0000. <b>This scan name contains no charge
 * state informations.</b>
 * 
 * @author Xinning
 * @version 0.1.1, 08-10-2009, 20:09:19
 */
public class FormattedScanName extends AbstractKnownScanName {

	/**
	 * Matches the formatted scan name. For example, XXXXX, 0000 or XXXXX, 0000
	 * - 0000 or 0000 or 0000 - 0000
	 */
	private static final Pattern PATTERN = Pattern.compile(
	        "(?:(.+), )?(\\d+)(?: - (\\d+))?", Pattern.CASE_INSENSITIVE);


	/**
	 * Matches the formatted scan name. For example, XXXXX, 0000 or XXXXX, 0000
	 * - 0000 or 0000 or 0000 - 0000
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isFormat(String scanNum) {
		return (PATTERN.matcher(scanNum).matches());
	}

	/**
	 * Matches the formatted scan name. For example, XXXXX, 0000 or XXXXX, 0000
	 * - 0000 or 0000 or 0000 - 0000
	 * 
	 * @param scanname
	 * @throws IllegalArgumentException
	 *             if the filename is not a valid sequest filename
	 */
	public FormattedScanName(String scanNum) throws IllegalArgumentException {
		Matcher matcher = PATTERN.matcher(scanNum);

		if (matcher.matches()) {
			this.setBaseName(matcher.group(1));
			int scanBeg = Integer.parseInt(matcher.group(2));
			this.setScanNumBeg(scanBeg);
			
			String end = matcher.group(3);
			if(end != null)
				this.setScanNumEnd(Integer.parseInt(end));
			else
				this.setScanNumEnd(scanBeg);
		}
		else{
			throw new IllegalArgumentException(
			        "Error in parsing the formatted scanname: \"" + scanNum
			                + "\".");
		}
	}

	/**
	 * Construct a SequestScanName from the elements specified.
	 * 
	 * @param baseName
	 * @param scanBeg
	 * @param scanEnd
	 * @param charge
	 * @param extension
	 *            : "dta" for dta files and "out" for outfiles. The extension
	 *            can also be Null or "" if it is useless.
	 */
	public FormattedScanName(String baseName, int scanBeg, int scanEnd) {
		this.setBaseName(baseName);
		this.setScanNumBeg(scanBeg);
		this.setScanNumEnd(scanEnd);
	}

	/**
	 * The charge state. <b>Always return 0</b>
	 */
	@Override
	public short getCharge() {
		return 0;
	}

	/**
	 * The formatted scan name with the format of XXXXX, 0000 or XXXXX, 0000 -
	 * 0000 or 0000 or 0000 - 0000
	 */
	public String getScanName() {
		
		StringBuilder sb = new StringBuilder();
		String basename = this.getBaseName();
		if(basename != null && basename.length()>0)
			sb.append(basename).append(", ");
		
		int scanBeg = this.getScanNumBeg();
		int scanEnd = this.getScanNumEnd();
		
		sb.append(scanBeg);
		if(scanBeg != scanEnd)
			sb.append(" - ").append(scanEnd);
		
		
		return sb.toString();
	}

	@Override
    public FormattedScanName getFormattedScanName() {
	    return this;
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public FormattedScanName deepClone() {
	    return this.clone();
    }
	
	@Override
	public FormattedScanName clone() {
		try {
	        return (FormattedScanName) super.clone();
        } catch (CloneNotSupportedException e) {
	        e.printStackTrace();
        }
        
        return null;
	}
	
	public static void main(String [] args){
		FormattedScanName name = new FormattedScanName("TITLE=Elution from: " +
				"0.035 to 0.035 period: 0 experiment: 1 cycles: 1 precIntensity: 5386039.0 " +
				"FinneganScanNumber: 5 MStype: enumIsNormalMS rawFile: mouse-liver_W_0mM.raw ");
		System.out.println(name.getScanNumBeg());
		
	}
}
