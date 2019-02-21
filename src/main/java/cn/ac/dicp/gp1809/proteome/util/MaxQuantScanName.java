/* 
 ******************************************************************************
 * File:MaxQuantScanName.java * * * Created on 2009-11-19
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
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
 * @version 2009-11-19, 09:51:17
 */
public class MaxQuantScanName extends AbstractKnownScanName {
	
	private static final Pattern PATTERN = Pattern.compile(
	        "([^\\d]+\\d+\\.?\\d*){7,}.*", Pattern.CASE_INSENSITIVE);
	
	public static boolean isFormat(String scanNum) {
		return (PATTERN.matcher(scanNum).matches());
	}
	
	private static Pattern one = Pattern.compile("([^\\d]+\\d+\\.?\\d*)+?", Pattern.CASE_INSENSITIVE);
	
	private String scanName;
	
	private short charge;
	
	public MaxQuantScanName(String scanName) throws IllegalArgumentException {
		
		this.scanName = scanName;
		Matcher matcher = PATTERN.matcher(scanName);
		Matcher oneMatch = one.matcher(scanName);
		int scanBeg = 0;
		int scanEnd = 0;
		String baseName = "";
		
		if (matcher.matches()) {
			
			if((scanName.indexOf("FinneganScanNumber"))>-1){
				
				int i = scanName.indexOf("FinneganScanNumber");
				if(oneMatch.find(i)){
					baseName = oneMatch.group();
				}
				
				String scanBegStr = baseName.substring(baseName.indexOf(':')+1).trim();
				scanBeg = Integer.parseInt(scanBegStr);
				scanEnd = scanBeg;
			}
			
			if(scanName.indexOf("rawFile")>-1){
				int i = scanName.indexOf("rawFile")+8;
				if(scanName.indexOf(".raw")>-1){
					int j = scanName.indexOf(".raw");
					baseName = scanName.substring(i,j).trim();
				}
				else{
					baseName = scanName.substring(i).trim();
				}
			}
			
			this.setBaseName(baseName);
			this.setScanNumBeg(scanBeg);
			this.setScanNumEnd(scanBeg);
		}
		else{
			throw new IllegalArgumentException(
			        "Error in parsing the formatted scanname: \"" + scanName
			                + "\".");
		}
	}
	
	/**
	 * Construct a MaxQuantScanName from the elements specified.
	 * 
	 * @param baseName
	 * @param scanBeg
	 * @param scanEnd
	 * @param charge
	 * @param extension
	 *            : "dta" for dta files and "out" for outfiles. The extension
	 *            can also be Null or "" if it is useless.
	 */
	public MaxQuantScanName(String baseName, int scanBeg, int scanEnd) {
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

	/**
	 * The formatted scan name with the format of XXXXX, 0000 or XXXXX, 0000 -
	 * 0000 or 0000 or 0000 - 0000
	 */
	public String getScanName() {
		
		if(this.scanName==null || this.scanName.length()==0){
			
			StringBuilder sb = new StringBuilder(18);
			String basename = this.getBaseName();
			if(basename != null && basename.length()>0)
				sb.append(basename).append(", ");
			
			int scanBeg = this.getScanNumBeg();
			int scanEnd = this.getScanNumEnd();
			
			sb.append(scanBeg);
			if(scanBeg != scanEnd)
				sb.append(" - ").append(scanEnd);
			
			this.scanName = sb.toString();
			
		}
		return this.scanName;
	}

    public MaxQuantScanName getMaxQuantScanName() {
	    return this;
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public MaxQuantScanName deepClone() {
	    return this.clone();
    }
	
	@Override
	public MaxQuantScanName clone() {
		try {
	        return (MaxQuantScanName) super.clone();
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
		String title = "Elution from: 0.020 to 0.020 period: " +
				"0 experiment: 1 cycles: 1 precIntensity: 280100.0 FinneganScanNumber: " +
				"3 MStype: enumIsNormalMS rawFile: 20091105_wy_phosphopeptide.raw ";
		MaxQuantScanName m = new MaxQuantScanName(title);
		System.out.println(MaxQuantScanName.isFormat(title));
		System.out.println(title);
	}

}
