/* 
 ******************************************************************************
 * File: MascotFitnessFunction.java * * * Created on 2011-8-31
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.mascot;

import cn.ac.dicp.gp1809.ga.Chromosome;
import cn.ac.dicp.gp1809.ga.FitnessFunction;

/**
 * @author ck
 *
 * @version 2011-8-31, 13:52:20
 */
public class MascotFitnessFunction extends FitnessFunction {

	private float[][] peptides;
	private double maxfalseratio;
	private double currentfalseratio;

	/**
	 * 0: 2*FP/(TP+FP); 
	 * 1: FP/(TP+FP);
	 * 2: FP/TP;
	 */
	private short optimizeType = 0;
	
	
	MascotFitnessFunction(float[][] peptides,double maxFPR){
		
		if(peptides==null||peptides.length==0)
			throw new NullPointerException("No peptide!");
		
		this.maxfalseratio = maxFPR;
		this.peptides = peptides;
	}
	
	MascotFitnessFunction(float[][] peptides,double maxFPR, short optimizeType){
		
		if(peptides==null||peptides.length==0)
			throw new NullPointerException("No peptide!");
		
		this.maxfalseratio = maxFPR;
		this.peptides = peptides;
		this.optimizeType = optimizeType;
	}

	protected boolean ifPassFilter(float[] peptide, double[] filter){
		
		boolean pass = true;
		
		if(peptide[0]<filter[0]) return false; //ion score
//		if(peptide[1]<filter[1]) return false; //delta ion score
		if(peptide[2]<filter[2]) return false; //ion score-MHT
		if(peptide[3]<filter[3]) return false; //ion score-MIT
		if(peptide[4]>filter[4]) return false; //ion score-MIT

		return pass;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.FitnessFunction#evaluate(cn.ac.dicp.gp1809.ga.Chromosome)
	 */
	@Override
	protected double evaluate(Chromosome a_chrome) {
		// TODO Auto-generated method stub

		double[] values = a_chrome.values();
		
		float[] temp = peptides[0];
		int len = temp.length-1;
		
		int totalcount=0;
		int falsecount=0;
		
		for(int i=0;i<this.peptides.length;i++){
			temp = peptides[i];
			if(this.ifPassFilter(temp, values)){
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
