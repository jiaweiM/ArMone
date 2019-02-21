/* 
 ******************************************************************************
 * File: SimpleProInfo.java * * * Created on 2011-8-9
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2011-8-9, 14:48:02
 */
public class SimpleProInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7877228070541828138L;
	
	private String partRef;
	private String ref;
	private int length;
	private double mw;
	private double hydroScore;
	private double PI;
	private boolean isDecoy;
	
	public SimpleProInfo(String partRef, String ref, int length, double mw, double hydroScore, 
			double PI, boolean isDecoy){
		this.partRef = partRef;
		this.ref = ref;
		this.length = length;
		this.mw = mw;
		this.hydroScore = hydroScore;
		this.PI = PI;
		this.isDecoy = isDecoy;
	}
	
	public String getPartRef(){
		return this.partRef;
	}
	
	public String getRef(){
		return this.ref;
	}
	
	public int getLength(){
		return this.length;
	}
	
	public double getMw(){
		return this.mw;
	}
	
	public double getHydroScore(){
		return this.hydroScore;
	}
	
	public double getPI(){
		return this.PI;
	}
	
	public boolean isDecoy(){
		return this.isDecoy;
	}
	
	public float getCoverage(IPeptide [] peps){
		
		int [] ps = new int [length];
		Arrays.fill(ps, 0);
		for(int i=0;i<peps.length;i++){
			HashMap <String, SeqLocAround> locAroundMap = peps[i].getPepLocAroundMap();
			Iterator <String> it = locAroundMap.keySet().iterator();
			while(it.hasNext()){
				String pr = it.next();
				if(pr.substring(0, pr.indexOf("(")+1).equals(this.partRef)){
					SeqLocAround sla = locAroundMap.get(pr);
					int beg = sla.getBeg();
					int end = sla.getEnd();
					for(int j=beg-1;j<end;j++){
						ps[j] = 1;
					}
				}
			}
		}
		int total = MathTool.getTotal(ps);
		float cover = (float)total/(float)ps.length;
		return cover;
	}

	public static SimpleProInfo parseInfo(int partLength, String line){
		
		String [] ss = line.split("\\$");
		String ref = ss[0];
		String partRef = ref.substring(0, partLength);
		int length = Integer.parseInt(ss[1]);
		double mw = Double.parseDouble(ss[2]);
		double hydroScore = Double.parseDouble(ss[3]);
		double PI = Double.parseDouble(ss[4]);
		boolean isDecoy = Boolean.parseBoolean(ss[5]);
		
		SimpleProInfo info = new SimpleProInfo(partRef, ref, length, mw, hydroScore, PI, isDecoy);
		return info;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(ref).append("$");
		sb.append(length).append("$");
		sb.append(mw).append("$");
		sb.append(hydroScore).append("$");
		sb.append(PI).append("$");
		sb.append(isDecoy);
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int len = 6;
		String line = "P00634|PPB_ECOLI Alkaline phosphatase - Escherichia coli (strain K12).$555$5.5$5.5";
		SimpleProInfo sp = SimpleProInfo.parseInfo(len, line);
		String pr = sp.partRef;
		System.out.println(pr);
	}

}
