/*
 ******************************************************************************
 * File: SequestFitnessFunction.java * * * Created on 08-07-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.sequest;

import cn.ac.dicp.gp1809.ga.Chromosome;
import cn.ac.dicp.gp1809.ga.FitnessFunction;

/**
 * The fitness function
 * 
 * @author Xinning
 * @version 0.1.1, 09-14-2010, 15:08:27
 */
public class SequestFitnessFunction extends FitnessFunction {
	
	private float[][] peptides;
	private double maxfalseratio;
	private double currentfalseratio;
	/**
	 * 0: 2*FP/(TP+FP); 
	 * 1: FP/(TP+FP);
	 * 2: FP/TP;
	 */
	private short optimizeType = 0;
	
	/**
	 * Peptides is double array which takes peptides identified from different spectra as its first dimension
	 * and scores assigened by Sequest as its second dimension. The meaning of each value in the second 
	 * dimension is same as that in filter point by point. And the length of second dimension in peptide
	 * is bigger than filter by one, where -1 and 1 is assigned for true peptide or false positive peptide;
	 * By default, this order is Xcorr deltaCn Sp Rsp Ion and so on with the last point as true-or-false;
	 * @param vlimit
	 * @param peptides
	 * @param maxFPR
	 */
	SequestFitnessFunction(float[][] peptides,double maxFPR) {
		if(peptides==null||peptides.length==0)
			throw new NullPointerException("No peptide!");
		
		this.maxfalseratio = maxFPR;
		this.peptides = peptides;
	}
	
	SequestFitnessFunction(float[][] peptides, double maxFPR, short optimizeType) {
		if(peptides==null||peptides.length==0)
			throw new NullPointerException("No peptide!");
		
		this.maxfalseratio = maxFPR;
		this.peptides = peptides;
		this.optimizeType = optimizeType;
	}

	/**
	 * 
	 * @param peptide
	 * @param filter
	 * @return
	 */
	protected boolean ifPassFilter(float[] peptide, double[] filter){
		boolean pass = true;
		
		if(peptide[0]<filter[0]) return false; //xcorr
		if(peptide[1]<filter[1]) return false; //dcn
		if(peptide[2]<filter[2]) return false; //sp
		if(peptide[3]>filter[3]) return false; //rsp
		if(peptide[4]>filter[4]) return false; //dms
		if(peptide[5]<filter[5]) return false; //ions
		
		return pass;
	}
	
	@Override
	public double evaluate(Chromosome a_chrome) {
		double[] values = a_chrome.values();
		
		float[] temp = peptides[0];
		int len = temp.length-1;
		
		int totalcount=0;
		int falsecount=0;
		
		for(int i=0;i<this.peptides.length;i++){
			temp = peptides[i];
			if(this.ifPassFilter(temp,values)){
				if(temp[len]<0)
					falsecount++;
				totalcount++;
			}
		}
		
		double fitness = totalcount-falsecount;
		
		this.currentfalseratio = getFalseRatio(falsecount, totalcount);
		
		if(this.currentfalseratio>this.maxfalseratio)
			fitness = 0d;
		
		return fitness;
	}
	
	
	private double getFalseRatio(int falsecount, int totalcount){
		switch (optimizeType) {
			case 0:
				return falsecount*2.0 / (double)totalcount;
			case 1:
				return falsecount / (double)totalcount;
			case 2:
				return falsecount / (double)(totalcount - falsecount);
			default:
				return 0;
		}
			
	}
	
	public double getCurrentFPR(){
		return this.currentfalseratio;
	}
}
