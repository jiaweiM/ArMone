/* 
 ******************************************************************************
 * File: SimpleScanName.java * * * Created on 2012-8-15
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
 * @version 2012-8-15, 18:47:01
 */
public class SimpleScanName extends AbstractKnownScanName {
	
	private static final Pattern PATTERN = Pattern.compile(
	        ".*Scan[s]?[:]? (\\d+).*", Pattern.CASE_INSENSITIVE);

	private String scanName;
	
	public SimpleScanName(String scanName){
		
		this.scanName = scanName;
		
		Matcher matcher = PATTERN.matcher(scanName);
		int scanBeg = 0;
		String baseName = "";
		
		if (matcher.matches()) {
			
			int count = matcher.groupCount();
			if(count==1){
				
				baseName = scanName;
				scanBeg = Integer.parseInt(matcher.group(1));
				
				this.setBaseName(baseName);
				this.setScanNumBeg(scanBeg);
				this.setScanNumEnd(scanBeg);
				
			}else{
				
				baseName = scanName;
				
				this.setBaseName(baseName);
				this.setScanNumBeg(scanBeg);
				this.setScanNumEnd(scanBeg);
			}
			
		}else{
			throw new IllegalArgumentException(
			        "Error in parsing the formatted scanname: \"" + scanName
			                + "\".");
		}
	}
	
	public SimpleScanName(String baseName, int scanBeg, int scanEnd){
		this.setBaseName(baseName);
		this.setScanNumBeg(scanBeg);
		this.setScanNumEnd(scanEnd);
	}

	public static boolean isFormat(String scanNum) {
		return (PATTERN.matcher(scanNum).matches());
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.util.IKnownFormatScanName#deepClone()
	 */
	@Override
	public IKnownFormatScanName deepClone() {
		// TODO Auto-generated method stub
		return this.clone();
	}

	public SimpleScanName clone() {
		try {
	        return (SimpleScanName) super.clone();
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Pattern p = SimpleScanName.PATTERN;
		
		String s = "TITLE=4: scans: 61 (rt=1.45365) " +
				"[\\\\wzw.tum.de\\ipag\\projects\\Phosphoscoring\\Manuscript\\Data\\Orbitrap_raw\\CID_raw\\ppeptidemix1_CID_Orbi.RAW]";
		
//		String s = "File312 Spectrum17638 scans: 20858";
		Matcher m = p.matcher(s);
		if(m.matches()){
//			System.out.println(m.groupCount());
			System.out.println(m.group(0));
			System.out.println(m.group(1));
		}
		
		System.out.println(s);
	}
	
}
