/*
 * *****************************************************************************
 * File: PepCompare.java * * * Created on 12-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.spcounter;

import java.text.DecimalFormat;

/**
 * 
 * 
 * @author Xinning
 * @version 0.1, 12-08-2008, 13:33:05
 */
public class PepCompare {
	public static DecimalFormat df = new DecimalFormat("#.###");
	//Name of the protein
	String sequence;
	String ref;
	double mw;
	double pi;
	
	
	//The number of replicate in this experiment
	private int replicateNum;
	
	//The spectrum count of this pro statistic.
	private double[] spcount;
	
	private double[] RSD;
	
	/**
	 * Create a pro statistic instance without computation of pep replicate information
	 * @param how many replicates in this experiment.
	 */
	public PepCompare(int replicateNum){
		this.replicateNum = replicateNum;
		this.spcount = new double[replicateNum];
		for(int i=0;i<replicateNum;i++)
			this.spcount[i] = 0.1d;
		
		this.RSD = new double[replicateNum];
	}
	
	/**
	 * Set a same protein as this pro.
	 * The added protein commonly comes from different replicate.
	 * 
	 * @param protein
	 * @param curtReplicate the current replicate number.
	 */
	public void set(PepStatistic pep, int curtReplicate){
		
		if(curtReplicate>=this.replicateNum)
			throw new RuntimeException("");
		
		//The first time 
		if(this.sequence == null){
			this.sequence = pep.sequence;
			this.ref = pep.ref;
			this.mw = pep.mw;
			this.pi = pep.pi;
		}
		
		this.spcount[curtReplicate] = pep.getCount();
		this.RSD[curtReplicate] = pep.getCV();
	}
	
	@Override
	public String toString(){
		StringBuilder  sb = new StringBuilder();
		
		sb.append("\t");
		for(int i=0;i<this.replicateNum;i++){
			sb.append( df.format(this.spcount[i]));
			sb.append("\t");
			sb.append(df.format(this.RSD[i])).append("\t");
			sb.append("\t");//unique
		}
		
		sb.append(this.sequence);
		sb.append("\t");
		sb.append(this.ref);
		sb.append("\t");
		sb.append(df.format(this.mw));
		sb.append("\t");
		sb.append(df.format(this.pi));
		sb.append("\t");
		
		return sb.toString();
	}
}
