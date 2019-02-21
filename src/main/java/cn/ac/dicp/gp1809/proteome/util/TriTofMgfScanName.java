/* 
 ******************************************************************************
 * File: TriTofMgfScanName.java * * * Created on 2012-9-28
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
 * @version 2012-9-28, 18:20:28
 */
public class TriTofMgfScanName extends AbstractKnownScanName {

	private static final Pattern PATTERN = Pattern.compile(
			"Locus:[\\d\\.]+",
	        Pattern.CASE_INSENSITIVE);
	
	public TriTofMgfScanName(String title){
		
		Matcher m = PATTERN.matcher(title);
		String s = "";
		if(m.find()){
			s = m.group();
		}
		this.setBaseName(s);
	}
	
	public static boolean isFormat(String scanNum) {
		return (PATTERN.matcher(scanNum).find());
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.util.IKnownFormatScanName#deepClone()
	 */
	@Override
	public IKnownFormatScanName deepClone() {
		// TODO Auto-generated method stub
		return this.clone();
	}

	public TriTofMgfScanName clone() {
		try {
	        return (TriTofMgfScanName) super.clone();
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

		return sb.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		String title = "Locus:1.1.1.3707.19 File:~F11.wiff~";
		String title = "Locus:1.1.1.5041.2";

		Pattern p2 = TriTofMgfScanName.PATTERN;
		
		Matcher m = p2.matcher(title);
//		System.out.println(m.find());
//		System.out.println(m.group());
//		System.out.println(m.group(1));
		
		TriTofMgfScanName name = new TriTofMgfScanName(title);
		System.out.println(name.getBaseName());
	}

}
