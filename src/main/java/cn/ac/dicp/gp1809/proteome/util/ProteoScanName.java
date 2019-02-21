/* 
 ******************************************************************************
 * File: ProteoScanName.java * * * Created on 2012-7-13
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
 * @version 2012-7-13, 19:07:29
 */
public class ProteoScanName extends AbstractKnownScanName {

	private static final Pattern PATTERN = Pattern.compile(
	        "([^.]*).*NativeID:\".*scan=(\\d+)\"", Pattern.CASE_INSENSITIVE);
	
	private String scanName;
	private short charge;
	
	public ProteoScanName(String scanName){
		
		this.scanName = scanName;
		Matcher matcher = PATTERN.matcher(scanName);
		int scanBeg = 0;
		String baseName = "";
		
		if (matcher.matches()) {
			
			int count = matcher.groupCount();
			if(count==2){
				
				baseName = matcher.group(1);
				scanBeg = Integer.parseInt(matcher.group(2));
				
				this.setBaseName(baseName);
				this.setScanNumBeg(scanBeg);
				this.setScanNumEnd(scanBeg);
				
			}else{
				baseName = matcher.group(1);
				
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
	
	public ProteoScanName(String baseName, int scanBeg, int scanEnd) {
		this.setBaseName(baseName);
		this.setScanNumBeg(scanBeg);
		this.setScanNumEnd(scanEnd);
	}
	
	public static boolean isFormat(String scanNum) {
		return (PATTERN.matcher(scanNum).matches());
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
	
	public ProteoScanName clone() {
		try {
	        return (ProteoScanName) super.clone();
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

		Pattern p = Pattern.compile(
		        "TITLE=([^.]*).*NativeID:\".*scan=(\\d+)\"", Pattern.CASE_INSENSITIVE);
		
		String s = "20110922_EXQ4_NaNa_SA_YeastEasy_Labelfree_02.17011.17011.2 File:\"\", NativeID:\"controllerType=0 controllerNumber=1 scan=17011\"";
		
		Matcher m = p.matcher(s);
		if(m.matches()){
//			System.out.println(m.groupCount());
			System.out.println(m.group(0));
			System.out.println(m.group(1));
			System.out.println(m.group(2));
		}
		
		System.out.println(s);
	}

}
