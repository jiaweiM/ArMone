/* 
 ******************************************************************************
 * File: FeaturePair.java * * * Created on 2010-11-26
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.rsc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeature;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2010-11-26, 14:56:56
 */
public class FeaturePair {

	private LabelFeature [] feas;
	private double [] r2;
	private double [] slope;
	private double [][] intens;
	private double [][] ratios;
	private int index;
	private boolean [] use;
	
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	
	public FeaturePair(LabelFeature [] feas){
		this.feas = feas;
		this.initial();
	}
	
	public FeaturePair(ArrayList <LabelFeature> feas){
		LabelFeature [] fs = feas.toArray(new LabelFeature[feas.size()]);
		this.feas = fs;
		this.initial();
	}
	
	/**
	 * 
	 */
	private void initial(){
		int flength = feas.length;
		double [][] intens = new double [flength][];
		for(int i=0;i<flength;i++){
			if(feas[i]==null)
				Arrays.fill(intens[i], 0);
//			else
//				intens[i] = feas[i].getIntenList();
		}
		this.intens = intens;
		this.ratios = getRatioArray();
		
/*		
		ArrayList <Double> rslist = new ArrayList <Double> ();
		ArrayList <Double> slolist = new ArrayList <Double> ();
		for(int i=0;i<intens.length;i++){
			for(int j=i+1;j<intens.length;j++){
				CurveFitting fitting = new CurveFitting(intens[i], intens[j], 
						new SLineFunction());
				fitting.fit();
				rslist.add(fitting.getR2());
				double [] para = fitting.getBestParams();
				slolist.add(para[0]);
			}
		}
		this.r2 = new double[rslist.size()];
		for(int i=0;i<r2.length;i++){
			r2[i] = rslist.get(i);
			if(r2[i]>0.5){
				this.use[i] = true;
			}else{
				this.use[i] = false;
			}
		}
		this.slope = new double[slolist.size()];
		for(int i=0;i<slope.length;i++){
			slope[i] = slolist.get(i);
		}
		
		double [] r2 = new double[feas.length-1];
		double [] slope = new double[feas.length-1];
		for(int i=1;i<intens.length;i++){
			CurveFitting fitting = new CurveFitting(intens[0], intens[i], 
					new SLineFunction());
			fitting.fit();
			r2[i-1] = fitting.getR2();
			double [] para = fitting.getBestParams();
			slope[i-1] = para[0];
		}
		this.r2 = r2;
		this.slope = slope;
*/		
	}
	
	public double [] getR2(){
		return r2;
	}
	
	public double [] getSlope(){
		return slope;
	}

	private double [][] getRatioArray(){
		int num = intens.length*(intens.length-1)/2;
		this.use = new boolean [num];
		int pixNum = intens[0].length;
		double [][] ratioArray = new double[num][];
		int index = 0;
		for(int i=0;i<intens.length;i++){
			for(int j=i+1;j<intens.length;j++){
				double [] rij = new double[pixNum];
				for(int k=0;k<pixNum;k++){
					if(intens[i][k]==0)
						rij[k] = 0;
					else{
						rij[k] = intens[j][k]/intens[i][k];
						rij[k] = Float.parseFloat(df4.format(rij[k]));
						this.use[index] = true;
					}
				}
				ratioArray[index] = rij;
				index++;
			}
		}
		return ratioArray;
	}
	
	public double [][] getIntenArray(){
		return intens;
	}
	
	public double [][] getRatio(){
		return ratios;
	}
	
	public boolean [] getUse(){
		return use;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	public int getIndex(){
		return index;
	}
	
}
