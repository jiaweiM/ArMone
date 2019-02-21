/* 
 ******************************************************************************
 * File: cn.ac.dicp.gp1809.proteome.util * * * Created on 2010-11-15
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
 * For mgf file from QTof, with the title such as "F000001,quan:0.000,start:4.459,end:4.563,survey:
 * S000022,parent:542.731323,Qstart:4.459,Qend:4.459,AnalTime:30".
 * 
 * @author ck
 *
 * @version 2010-11-15, 02:22:32
 */
public class QTofMgfScanName extends AbstractKnownScanName{

	private short charge;
	
	private static final Pattern PATTERN = Pattern.compile(
			"([^/.]*:[\\d/.]*)*,(survey:.\\d*),([^/.]*:[\\d/.]*)*",
	        Pattern.CASE_INSENSITIVE);
	
	public QTofMgfScanName(String title){
		Matcher m = PATTERN.matcher(title);
		String s = "";
		if(m.matches()){
			s = m.group(2);
		}

		StringBuilder sb = new StringBuilder();
		boolean start = false;
		char [] chs = s.toCharArray();
		for(int i=0;i<chs.length;i++){
			if(start){
				if(chs[i]>='0' && chs[i]<='9'){
					sb.append(chs[i]);
				}else{
					break;
				}
			}else{
				if(chs[i]>'0' && chs[i]<='9'){
					sb.append(chs[i]);
					start = true;
				}
			}
		}
		int scan = Integer.parseInt(sb.toString());

		this.setScanNumBeg(scan);
		this.setScanNumEnd(scan);
	}
	
	public QTofMgfScanName(String fileName, String title){
		Matcher m = PATTERN.matcher(title);
		String s = m.group(2);
		StringBuilder sb = new StringBuilder();
		boolean start = false;
		char [] chs = s.toCharArray();
		for(int i=0;i<chs.length;i++){
			if(start){
				if(chs[i]>='0' && chs[i]<=9){
					sb.append(chs[i]);
				}else{
					break;
				}
			}else{
				if(chs[i]>'0' && chs[i]<=9){
					sb.append(chs[i]);
					start = true;
				}
			}
		}
		int scan = Integer.parseInt(sb.toString());
		
		this.setBaseName(fileName);
		this.setScanNumBeg(scan);
		this.setScanNumEnd(scan);
	}
	
	public QTofMgfScanName(String baseName, int scanBeg, int scanEnd) {
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
	
	@Override
	public QTofMgfScanName clone() {
		try {
	        return (QTofMgfScanName) super.clone();
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String title = "Elution from: 33.785 to 33.785 period: 0 experiment: 1 cycles: 1 " +
				"precIntensity: 80277.0 FinneganScanNumber: 5151 MStype: ";

		Pattern p2 = Pattern.compile(
    	        "Elution from:.*FinneganScanNumber: (\\d*).*", Pattern.CASE_INSENSITIVE);
		
		Matcher m = p2.matcher(title);
		System.out.println(m.matches());
		System.out.println(m.groupCount());
		System.out.println(m.group(1));

	}
}
