/* 
 ******************************************************************************
 * File: ProTurnoverUnit.java * * * Created on 2011-11-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.turnover;

import flanagan.analysis.Regression;

/**
 * @author ck
 *
 * @version 2011-11-21, 13:51:23
 */
public class ProTurnoverUnit {

	private String ref;
	private double [][] timePoints;
	private double [][] ratios;
	private double [] k;
	private double [] yyr;

	/**
	 * 
	 * @param ref
	 * @param ratios
	 * @param timePoints
	 */
	public ProTurnoverUnit(String ref, double [][] ratios, double [][] timePoints){
		
		this.ref = ref;
		this.timePoints = timePoints;
		this.ratios = ratios;
		
		this.k = new double[ratios.length];
		this.yyr = new double[ratios.length];
		
		this.fit();
	}
	
	private void fit(){
		
		for(int i=0;i<ratios.length;i++){
			
			int num = ratios[i].length;
			if(num<3){
				
				this.k[i] = 0;
				this.yyr[i] = 0;
				
			}else{
				
				Regression reg = new Regression(timePoints[i], ratios[i]);
				reg.setYscaleFactor(1);
				reg.exponentialMultiple(1);
				this.k[i] = -reg.getBestEstimates()[1];
				this.yyr[i] = reg.getYYcorrCoeff();
			}
		}
	}
	
	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(ref).append("\t");
		for(int i=0;i<ratios.length;i++){
			sb.append(k[i]).append("\t");
			sb.append(yyr[i]).append("\t");
			for(int j=0;j<ratios[i].length;j++){
				sb.append(ratios[i][j]).append(";");
			}
			sb.append("\t");
		}
		
		return sb.toString();
	}

	public static void main(String [] args){
		
		double [] time = new double [20];
		double [] value = new double [20];
		for(int i=0;i<20;i++){
			time[i] = i*2+1;
			value[i] = 2*Math.exp(-time[i]*0.25);
//			System.out.println(time[i]+"\t"+value[i]);
		}
		
		String file = "H:\\reg.txt";
		Regression reg = new Regression(time, value);
		reg.setYscaleFactor(1);
		reg.exponentialMultiple(1);
		reg.print();
//		reg.print(file);
		double [] best = reg.getBestEstimates();
		for(int i=0;i<best.length;i++){
			System.out.println(best[i]);
		}
		System.out.println(reg.getYYcorrCoeff());
	}
	
	
}
