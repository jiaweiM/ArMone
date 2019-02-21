/*
 * *****************************************************************************
 * File: PepStatistic.java * * * Created on 12-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.spcounter;

import cn.ac.dicp.gp1809.util.math.Statisticer;

/**
 * The statistic value for peptide in replicate experiments.
 * 
 * @author Xinning
 * @version 0.1, 12-08-2008, 13:32:53
 */
public class PepStatistic {
	//Name of the protein
	String sequence;
	String ref;
	double mw;
	double pi;
	
	
	//The number of replicate in this experiment
	private int replicateNum;
	
	//The spectrum count of this pro statistic.
	private int[] spcount;
	
	private double RSD=-1d;
	
	private boolean isAvg = true;
	
	private double avg = -1d;
	
	/**
	 * Create a pro statistic instance without computation of pep replicate information
	 * @param how many replicates in this experiment.
	 */
	public PepStatistic(int replicateNum, boolean isAvg){
		this.replicateNum = replicateNum;
		this.spcount = new int[replicateNum];
		this.isAvg = isAvg;
	}
	
	/**
	 * Set a same protein as this pro.
	 * The added protein commonly comes from different replicate.
	 * 
	 * @param protein
	 * @param curtReplicate the current replicate number.
	 */
	public void set(UniquePeptide uniquepep, int curtReplicate){
		
		if(curtReplicate>=this.replicateNum)
			throw new RuntimeException("The number to be set must smaller than " +
					"the replicate number");
		
		//The first time 
		if(this.sequence == null){
			this.sequence = uniquepep.sequence;
			this.ref = uniquepep.ref;
			this.mw = uniquepep.mw;
			this.pi = uniquepep.pi;
		}
		
		this.spcount[curtReplicate] = uniquepep.getCount();
	}
	
	/**
	 * @return the relative sd of the spectra count
	 *  	   <b> Compute only once at the first excution of this method</b>
	 */
	public double getCV(){
		if(this.RSD<0)
			this.RSD = Statisticer.getCV(this.spcount);
		
		return this.RSD;
	}
	
	/**
	 * @return the count of spectra for this proStats identification.
	 * 		  <b>This is a statistic value, so it may be a double value.
	 * 			 The compution of this value is the average count of all spectra or
	 * 			 the median value and this compution form can be assigned in conductor
	 * 			 of this prostatistic</b>
	 */
	public double getCount(){
		if(this.avg<0)
			this.avg = Statisticer.getStatisticValue(this.spcount, isAvg);
		
		return this.avg;
	}
	
	@Override
	public String toString(){
		StringBuilder  sb = new StringBuilder();
		
		sb.append("\t");
		for(int i=0;i<this.replicateNum;i++){
			sb.append( this.spcount[i]);
			sb.append("\t");
		}
		
		sb.append(this.getCount());
		sb.append("\t");
		sb.append(this.getCV());
		sb.append("\t");
		sb.append("\t");
		sb.append(this.ref);
		sb.append("\t");
		sb.append(this.sequence);
		sb.append("\t");
		sb.append(this.mw);
		sb.append("\t");
		sb.append(this.pi);
		sb.append("\t");
		
		return sb.toString();
	}
}
