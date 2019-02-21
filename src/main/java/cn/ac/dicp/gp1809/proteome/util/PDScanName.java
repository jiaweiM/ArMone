/* 
 ******************************************************************************
 * File: PDScanName.java * * * Created on 2011-6-8
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ck
 *
 * @version 2011-6-8, 09:01:20
 */
public class PDScanName extends AbstractKnownScanName {

	private static final Pattern PATTERN = Pattern.compile(
	        "Spectrum(\\d*) scans: (\\d*),", Pattern.CASE_INSENSITIVE);
	
	private short charge;

	public PDScanName(String scanName){
		
		Matcher matcher = PATTERN.matcher(scanName);

		int scanBeg = 0;

		String baseName = "";
		
		if (matcher.matches()) {

			scanBeg = Integer.parseInt(matcher.group(2));

			this.setBaseName(baseName);
			this.setScanNumBeg(scanBeg);
			this.setScanNumEnd(scanBeg);
			
//			System.out.println(scanBeg);
			
		}else{
			throw new IllegalArgumentException(
			        "Error in parsing the formatted scanname: \"" + scanName
			                + "\".");
		}
	}
	
	public PDScanName(String baseName, int scanBeg, int scanEnd){
		this.setBaseName(baseName);
		this.setScanNumBeg(scanBeg);
		this.setScanNumEnd(scanEnd);
	}

	@Override
	public void setCharge(short charge) {
		this.charge = charge;
	}
	
	/**
	 * The charge state. <b>Always return 0</b>
	 */
	@Override
	public short getCharge() {
		return charge;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.util.IKnownFormatScanName#deepClone()
	 */
	@Override
	public IKnownFormatScanName deepClone() {
		// TODO Auto-generated method stub
		return this.clone();
	}

	@Override
	public PDScanName clone() {
		try {
	        return (PDScanName) super.clone();
        } catch (CloneNotSupportedException e) {
	        e.printStackTrace();
        }
        
        return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.util.IScanName#getScanName()
	 */
	@Override
	public String getScanName() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder(18);
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

	public static boolean isFormat(String scanNum) {
		return (PATTERN.matcher(scanNum).matches());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String title = "Spectrum3 scans: 222,";
		title = "File6028 Spectrum7878 scans: 13662";
		PDScanName m = new PDScanName(title);
		System.out.println(PDScanName.isFormat(title));
		System.out.println(m.getScanName());
		System.out.println(m.getScanNumBeg());
	}
	
}
