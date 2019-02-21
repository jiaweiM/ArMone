/*
 * *****************************************************************************
 * File: SequestScanName.java * * * Created on 11-07-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The sequest formatted scan name of dta or out, or with no extension. The
 * format of these files is
 * <p>
 * "XXXXXXX.scanBeg.scanEnd.charge.extension"
 * <p>
 * or "XXXXXXX.scanBeg.scanEnd.charge"
 * 
 * <p>
 * Changes:
 * <li>0.1.3, 03-03-2009: implements IKnownFormatScanName
 * 
 * @author Xinning
 * @version 0.1.3, 03-03-2009, 14:32:04
 */
public class SequestScanName extends AbstractKnownScanName{

	/**
	 * Matches the dta standard scan name. For example, XXXXX.0000.0000.0 or
	 * XXXXX.0000.0000.0.dta or XXXXX.0000.0000.0.out
	 */
	private static final Pattern DTA_SCAN = Pattern.compile(
	        ".+\\.\\d+\\.\\d+\\.[1-9]\\.?(?:dta|out)?",
	        Pattern.CASE_INSENSITIVE);

	/**
	 * Dta scan without extensions. For example, XXXXX.0000.0000.0
	 */
	private static final Pattern DTA_SCAN_NO_EXTENSION = Pattern
	        .compile(".+\\.\\d+\\.\\d+\\.[1-9]");

	/**
	 * The raw scan name
	 */
	private String scanname;
	

	/**
	 * Test whether the name is a well formatted sequest scan name. For example,
	 * XXXXX.0000.0000.0 or XXXXX.0000.0000.0.dta or XXXXX.0000.0000.0.out
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isFormat(String scanNum) {
		return (DTA_SCAN.matcher(scanNum).matches());
	}

	/**
	 * For example, XXXXX.0000.0000.0 or XXXXX.0000.0000.0.dta or
	 * XXXXX.0000.0000.0.out
	 * 
	 * @param scanname
	 * @throws IllegalArgumentException
	 *             if the filename is not a valid sequest filename
	 */
	public SequestScanName(String scanname) throws IllegalArgumentException {

		try {
			this.parseDtaScanNum(scanname);
		} catch (Exception e) {
			throw new IllegalArgumentException(
			        "Error in parsing the sequest scanname: \"" + scanname
			                + "\".", e);
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
	public SequestScanName(String baseName, int scanBeg, int scanEnd,
	        short charge, String extension) {
		super(baseName, scanBeg, scanEnd, charge, extension);
	}


	
	/**
	 * Construct the sequest scan name without extension. For example, for
	 * sequest scan name, XXXXX.0000.0000.0
	 * 
	 * @return
	 */
	private String getScanName(String baseName, int scanBeg, int scanEnd,
	        short charge, String extension) {
		StringBuilder sb = new StringBuilder(18);
		sb.append(baseName).append('.').append(scanBeg).append('.').append(
		        scanEnd).append('.').append(charge);
		
		if (extension != null && extension.length() != 0)
			sb.append('.').append(extension);
		
		return sb.toString();
	}

	/**
	 * Parse the dta formatted scan number. XXXXX.0000.0000.0 or
	 * XXXXX.0000.0000.0.dta(out)
	 * 
	 * @since 0.1.2
	 * 
	 * @param scanNum
	 * @return
	 */
	protected final void parseDtaScanNum1(String scanNum) {

		int count = 0;
		int preindex = scanNum.length();

		// Contains extension
		if (DTA_SCAN_NO_EXTENSION.matcher(scanNum).matches()) {
			for (int i = preindex - 1; i >= 0; i--) {
				char c = scanNum.charAt(i);
				if (c == '.') {
					count++;

					switch (count) {
					case 1:
						this.setCharge(Short.parseShort(scanNum.substring(
						        i + 1, preindex)));
					case 2:
						this.setScanNumEnd(Integer.parseInt(scanNum.substring(
						        i + 1, preindex)));
						break;
					case 3:
						this.setScanNumBeg(Integer.parseInt(scanNum.substring(
						        i + 1, preindex)));
						this.setBaseName(scanNum.substring(0, i));
						i = 0;
						break;// End, skip the first blank
					default:
						break;
					}

					preindex = i;
				}
			}
		} else {
			for (int i = preindex - 1; i >= 0; i--) {
				char c = scanNum.charAt(i);
				if (c == '.') {
					count++;

					switch (count) {
					case 2:
						this.setCharge(Short.parseShort(scanNum.substring(
						        i + 1, preindex)));
					case 3:
						this.setScanNumEnd(Integer.parseInt(scanNum.substring(
						        i + 1, preindex)));
						break;
					case 4:
						this.setScanNumBeg(Integer.parseInt(scanNum.substring(
						        i + 1, preindex)));
						this.setBaseName(scanNum.substring(0, i));
						i = 0;
						break;// End, skip the first blank
					case 1:
						this.setExtension(scanNum.substring(i + 1));
						break;
					default:
						break;
					}

					preindex = i;
				}
			}
		}
	}

	/**
	 * Parse the dta formatted scan number. XXXXX.0000.0000.0 or
	 * XXXXX.0000.0000.0.dta(out)
	 * 
	 * @since 0.1.2
	 * 
	 * @param scanNum
	 * @return
	 */
	protected final void parseDtaScanNum(String scanNum) {

		this.scanname = scanNum;
		
		int count = 0;
		int preindex = scanNum.length();

		// Contains extension
		if (DTA_SCAN_NO_EXTENSION.matcher(scanNum).matches()) {
			for (int i = preindex - 1; i >= 0; i--) {
				char c = scanNum.charAt(i);
				if (c == '.') {
					count++;

					switch (count) {
					case 1:
						this.setCharge(Short.parseShort(scanNum.substring(
						        i + 1, preindex)));
					case 2:
						this.setScanNumEnd(Integer.parseInt(scanNum.substring(
						        i + 1, preindex)));
						break;
					case 3:
						this.setScanNumBeg(Integer.parseInt(scanNum.substring(
						        i + 1, preindex)));
						this.setBaseName(scanNum.substring(0, i));
						i = 0;
						break;// End, skip the first blank
					default:
						break;
					}

					preindex = i;
				}
			}
		} else {
			for (int i = preindex - 1; i >= 0; i--) {
				char c = scanNum.charAt(i);
				if (c == '.') {
					count++;

					switch (count) {
					case 2:
						this.setCharge(Short.parseShort(scanNum.substring(
						        i + 1, preindex)));
					case 3:
						this.setScanNumEnd(Integer.parseInt(scanNum.substring(
						        i + 1, preindex)));
						break;
					case 4:
						this.setScanNumBeg(Integer.parseInt(scanNum.substring(
						        i + 1, preindex)));
						this.setBaseName(scanNum.substring(0, i));
						i = 0;
						break;// End, skip the first blank
					case 1:
						this.setExtension(scanNum.substring(i + 1));
						break;
					default:
						break;
					}

					preindex = i;
				}
			}
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.util.IScanName#getScanName()
	 */
	public String getScanName() {
		
		if(this.scanname != null)
			return this.scanname;
		else
		return this.getScanName(getBaseName(), getScanNumBeg(),
		        getScanNumEnd(), getCharge(), this.getExtension());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.util.IKnownFormatScanName#getFormattedScanName
	 * ()
	 */
	@Override
	public FormattedScanName getFormattedScanName() {
		return new FormattedScanName(this.getBaseName(), this.getScanNumBeg(),
		        this.getScanNumEnd());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public SequestScanName deepClone() {
	    return this.clone();
    }
	
	@Override
	public SequestScanName clone() {
		try {
	        return (SequestScanName) super.clone();
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

		String title = "20110601_CK05_2.07999.07999.2";
		System.out.println(SequestScanName.isFormat(title));

	}
	
}
