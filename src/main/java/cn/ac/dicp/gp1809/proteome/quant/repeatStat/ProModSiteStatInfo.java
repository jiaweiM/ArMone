/* 
 ******************************************************************************
 * File: ProModSiteStatInfo.java * * * Created on 2011-11-16
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
 * @version 2011-11-16, 15:34:30
 */
public class ProModSiteStatInfo {

	private int id;
	private int repeatNum;
	private String ref;
	private double [][] proRatios;
	private double [] ave;
	private double [] RSD;
	private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;
	
	public ProModSiteStatInfo(int id, int repeatNum, String ref, double [][] proRatios){
		
		this.id = id;
		this.repeatNum = repeatNum;
		this.ref = ref;
		this.proRatios = proRatios;
		this.ave = new double[proRatios.length];
		this.RSD = new double[proRatios.length];
		
		ArrayList <Double> [] list = new ArrayList [proRatios.length];
		for(int i=0;i<proRatios.length;i++){
			list[i] = new ArrayList <Double>();
			for(int j=0;j<proRatios[i].length;j++){
				if(proRatios[i][j]>0)
					list[i].add(proRatios[i][j]);
			}
			this.ave[i] = MathTool.getAveInDouble(list[i]);
			this.RSD[i] = MathTool.getRSDInDouble(list[i]);
		}
	}
	
	public double [] getAveRatios(){
		return ave;
	}
	
	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(id).append("\t");
		sb.append(ref).append("\t");
		sb.append(repeatNum).append("\t");
		for(int i=0;i<ave.length;i++){
			sb.append(ave[i]).append("\t");
			sb.append(dfPer.format(RSD[i])).append("\t");
			for(int j=0;j<proRatios[i].length;j++){
				sb.append(proRatios[i][j]).append("\t");
			}
		}
		
		return sb.toString();
	}
	
}
