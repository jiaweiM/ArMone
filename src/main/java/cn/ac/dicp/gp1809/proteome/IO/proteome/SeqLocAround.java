/* 
 ******************************************************************************
 * File: SeqLocAround.java * * * Created on 2011-10-13
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome;

/**
 * The location of the peptide in the protein sequence and the previous and next 7 aminoacids
 * sequence.
 * 
 * @author ck
 *
 * @version 2011-10-13, 08:38:04
 */
public class SeqLocAround {

	private int beg;
	private int end;
	private String pre;
	private String next;
	
	public static final String spliter = "+";
	
	public SeqLocAround(int beg, int end, String pre, String next){
		this.beg = beg;
		this.end = end;
		this.pre = pre;
		this.next = next;
	}
	
	public static SeqLocAround parse(String info) throws Exception{
		String [] ss = info.split(spliter);
		if(ss.length!=4){
			throw new Exception("Cannot parse "+info+" to SeqLocAround.");
		}
		
		int beg = Integer.parseInt(ss[0]);
		int end = Integer.parseInt(ss[1]);
		return new SeqLocAround(beg, end, ss[2], ss[3]);
	}
	
	/**
	 * Begin with 1.
	 * @return
	 */
	public int getBeg(){
		return beg;
	}
	
	public int getEnd(){
		return end;
	}
	
	public String getPre(){
		return pre;
	}
	
	public String getNext(){
		return next;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(beg).append(spliter);
		sb.append(end).append(spliter);
		sb.append(pre).append(spliter);
		sb.append(next);
		return sb.toString();
	}
	
}
