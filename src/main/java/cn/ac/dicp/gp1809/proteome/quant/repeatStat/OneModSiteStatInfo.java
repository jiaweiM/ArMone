/* 
 ******************************************************************************
 * File: OneModSiteStatInfo.java * * * Created on 2011-11-16
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
 * @version 2011-11-16, 13:57:49
 */
public class OneModSiteStatInfo {

	private int repeatNum;
	private String ref;
	private int loc;
	private char aa;
	private String aaround;
	
	private double [][] abRatios;
	private double [][] reRatios;
	private double [] abAve;
	private double [] abRSD;
	private double [] reAve;
	private double [] reRSD;
	
	private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;
	
	public OneModSiteStatInfo(int repeatNum, String ref, int loc, char aa, String aaround,
			double [][] abRatios, double [][] reRatios){
		
		this.repeatNum = repeatNum;
		this.ref = ref;
		this.loc = loc;
		this.aa = aa;
		this.aaround = aaround;
		
		this.abRatios = abRatios;
		this.reRatios = reRatios;
		this.abAve = new double [abRatios.length];
		this.abRSD = new double [abRatios.length];
		this.reAve = new double [abRatios.length];
		this.reRSD = new double [abRatios.length];
		
		ArrayList <Double> [] ablist = new ArrayList [abRatios.length];
		for(int i=0;i<abRatios.length;i++){
			ablist[i] = new ArrayList <Double>();
			for(int j=0;j<abRatios[i].length;j++){
				if(abRatios[i][j]>0)
					ablist[i].add(abRatios[i][j]);
			}
			this.abAve[i] = MathTool.getAveInDouble(ablist[i]);
			this.abRSD[i] = MathTool.getRSDInDouble(ablist[i]);
		}
		
		ArrayList <Double> [] relist = new ArrayList [reRatios.length];
		for(int i=0;i<reRatios.length;i++){
			relist[i] = new ArrayList <Double>();
			for(int j=0;j<reRatios[i].length;j++){
				if(reRatios[i][j]>0)
					relist[i].add(reRatios[i][j]);
			}
			this.reAve[i] = MathTool.getAveInDouble(relist[i]);
			this.reRSD[i] = MathTool.getRSDInDouble(relist[i]);
		}
	}
	
	public double [] getAbAve(){
		return abAve;
	}
	
	public double [][] getAbRatios(){
		return abRatios;
	}
	
	public double [] getReAve(){
		return reAve;
	}
	
	public double [][] getReRatios(){
		return reRatios;
	}
	
	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(repeatNum).append("\t");
		sb.append(aa+""+loc).append("\t");
		sb.append(aaround).append("\t");
		for(int i=0;i<abAve.length;i++){
			sb.append(abAve[i]).append("\t");
			sb.append(dfPer.format(abRSD[i])).append("\t");
			for(int j=0;j<abRatios[i].length;j++){
				sb.append(abRatios[i][j]).append("\t");
			}
		}
		for(int i=0;i<reAve.length;i++){
			sb.append(reAve[i]).append("\t");
			sb.append(dfPer.format(reRSD[i])).append("\t");
			for(int j=0;j<reRatios[i].length;j++){
				sb.append(reRatios[i][j]).append("\t");
			}
		}
		sb.append(ref);
		return sb.toString();
	}
	
	public String toStringNoRef(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(repeatNum).append("\t");
		sb.append(aa+""+loc).append("\t");
		sb.append(aaround).append("\t");
		for(int i=0;i<abAve.length;i++){
			sb.append(abAve[i]).append("\t");
			sb.append(dfPer.format(abRSD[i])).append("\t");
			for(int j=0;j<abRatios[i].length;j++){
				sb.append(abRatios[i][j]).append("\t");
			}
		}
		for(int i=0;i<reAve.length;i++){
			sb.append(reAve[i]).append("\t");
			sb.append(dfPer.format(reRSD[i])).append("\t");
			for(int j=0;j<reRatios[i].length;j++){
				sb.append(reRatios[i][j]).append("\t");
			}
		}

		return sb.toString();
	}
	
}
