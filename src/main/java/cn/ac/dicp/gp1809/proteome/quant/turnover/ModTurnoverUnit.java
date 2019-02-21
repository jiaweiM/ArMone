/* 
 ******************************************************************************
 * File: ModTurnoverUnit.java * * * Created on 2011-11-21
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
 * @version 2011-11-21, 19:39:56
 */
public class ModTurnoverUnit {

	private String ref;
	private int loc;
	private char aa;
	private String aaround;
	
	private double [][] abRatios;
	private double [][] reRatios;
	private double [][] timePoints;
	
	private double [] abk;
	private double [] abyyr;
	private double [] rek;
	private double [] reyyr;
	
	public ModTurnoverUnit(String ref, int loc, char aa, String aaround,
			double [][] abRatios, double [][] reRatios, double [][] timePoints){
		
		this.ref = ref;
		this.loc = loc;
		this.aa = aa;
		this.aaround = aaround;
		
		this.abRatios = abRatios;
		this.reRatios = reRatios;
		this.timePoints = timePoints;
		
		this.abk = new double[abRatios.length];
		this.rek = new double[reRatios.length];
		this.abyyr = new double[abRatios.length];
		this.reyyr = new double[reRatios.length];
		
		this.fit();
	}
	
	private void fit(){
		
		for(int i=0;i<abRatios.length;i++){
			
			int num = abRatios[i].length;
			if(num<3){
				
				this.abk[i] = 0;
				this.abyyr[i] = 0;
				
				this.rek[i] = 0;
				this.abyyr[i] = 0;
				
			}else{
				
				Regression reg1 = new Regression(timePoints[i], abRatios[i]);
				reg1.setYscaleFactor(1);
				reg1.exponentialMultiple(1);
				this.abk[i] = -reg1.getBestEstimates()[1];
				this.abyyr[i] = reg1.getYYcorrCoeff();
				
				Regression reg2 = new Regression(timePoints[i], reRatios[i]);
				reg2.setYscaleFactor(1);
				reg2.exponentialMultiple(1);
				this.rek[i] = -reg2.getBestEstimates()[1];
				this.reyyr[i] = reg2.getYYcorrCoeff();
			}
		}
	}
	
	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(aa+""+loc).append("\t");
		sb.append(aaround).append("\t");
		for(int i=0;i<abRatios.length;i++){
			
			sb.append(abk[i]).append("\t");
			sb.append(abyyr[i]).append("\t");
			for(int j=0;j<abRatios[i].length;j++){
				sb.append(abRatios[i][j]).append(";");
			}
			sb.append("\t");
			
			sb.append(rek[i]).append("\t");
			sb.append(reyyr[i]).append("\t");
			for(int j=0;j<reRatios[i].length;j++){
				sb.append(reRatios[i][j]).append(";");
			}
			sb.append("\t");
		}
		
		sb.append(ref);
		return sb.toString();
	}
	
}
