/* 
 ******************************************************************************
 * File: PepStatInfo.java * * * Created on 2011-11-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.repeatStat;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2011-11-15, 15:50:29
 */
public class PepStatInfo implements Comparable <PepStatInfo> {

	private String seq;
	private int num;
	private double [] ave;
	private double [] RSD;
	private double [][] ratios;
	private int ratioNum;
	private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;
	
	public PepStatInfo(String seq, int num, double [][] ratios){
		
		this.seq = seq;
		this.num = num;
		this.ratios = ratios;
		this.ratioNum = ratios.length;
		this.ave = new double[ratios.length];
		this.RSD = new double[ratios.length];
		
		ArrayList <Double> [] list = new ArrayList [ratios.length];
		for(int i=0;i<ratios.length;i++){
			list[i] = new ArrayList <Double>();
			for(int j=0;j<ratios[i].length;j++){
				if(ratios[i][j]>0)
					list[i].add(ratios[i][j]);
			}
			this.ave[i] = MathTool.getAveInDouble(list[i]);
			this.RSD[i] = MathTool.getRSDInDouble(list[i]);
		}
	}

	public double [] getAve(){
		return ave;
	}
	
	public double [][] getRatios(){
		return ratios;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PepStatInfo o) {
		// TODO Auto-generated method stub
		int n1 = this.num;
		int n2 = o.num;
		if(n1>n2)
			return -1;
		else if(n1<n2)
			return 1;
		else{
			String s1 = this.seq;
			String s2 = o.seq;
			return s1.compareTo(s2);
		}
	}
	
	public String toStringProFormat(){
		StringBuilder sb = new StringBuilder();
		sb.append(seq).append("\t");
		sb.append(num).append("\t");
		for(int i=0;i<ratioNum;i++){
			sb.append(ave[i]).append("\t");
			sb.append(dfPer.format(RSD[i])).append("\t\t");
			for(int j=0;j<ratios[i].length;j++){
				sb.append(ratios[i][j]).append("\t");
			}
		}
		return sb.toString();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(seq).append("\t");
		sb.append(num).append("\t");
		for(int i=0;i<ratioNum;i++){
			sb.append(ave[i]).append("\t");
			sb.append(dfPer.format(RSD[i])).append("\t");
			for(int j=0;j<ratios[i].length;j++){
				sb.append(ratios[i][j]).append("\t");
			}
		}
		return sb.toString();
	}
	

}
