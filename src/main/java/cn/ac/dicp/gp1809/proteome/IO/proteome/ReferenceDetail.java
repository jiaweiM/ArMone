/* 
 ******************************************************************************
 * File: ReferenceDetail.java * * * Created on 08-28-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome;

import java.text.DecimalFormat;
import java.util.Arrays;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.DefaultReferenceDetailFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IReferenceDetailFormat;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;

/**
 * The reference detail in a protein reference collection
 * 
 * @author Xinning
 * @version 0.1.1, 09-20-2008, 10:49:43
 */
public class ReferenceDetail implements IReferenceDetail {

	/**
	 * The formatter of ReferenceDetail, you can replace the formatter with
	 * another defined formatter
	 */
	public static IReferenceDetailFormat formatter = new DefaultReferenceDetailFormat();

	private DecimalFormat df4 = new DecimalFormat(".####");

	private String reference;
	private float coverage;
	private int peptideCount;
	private int spectrumCount;
	private float pi;
	private double mw;
	private int numaas;
	private float prob = -1f;
	private boolean isTarget = true;
	private int groupIdx = -1;
	private int crossPro = -1;
	private double SIn;
	private double ratio;
	private double [] SIns;
	private String subRef;
	private double hyproScore;
	/*
	 * The Fasta protein sequence of this protein, can be null;
	 */
	private ProteinSequence pSeq;


	/**
	 * @param reference
	 * @param spectrumCount
	 * @param peptideCount
	 * @param coverage
	 * @param PI
	 * @param averagePeptideMass
	 * @param length
	 */
	public ReferenceDetail(String reference, int spectrumCount,
			int peptideCount, float coverage, float PI,
			double mw, int length) {
		// TODO Auto-generated constructor stub
		this.reference = reference;
		this.spectrumCount = spectrumCount;
		this.peptideCount = peptideCount;
		this.coverage = coverage;
		this.pi = PI < 0f ? 0f : PI;
		this.mw = mw;	
		this.numaas = length;
	}

	/**
	 * @param reference
	 * @param spectrumCount
	 * @param peptideCount
	 * @param coverage
	 * @param compute
	 * @param averagePeptideMass
	 * @param length
	 * @param b
	 */
	public ReferenceDetail(String reference, int spectrumCount,
			int peptideCount, float coverage, float PI,
			double mw, int length, boolean isTarget) {
		// TODO Auto-generated constructor stub
		
		this.reference = reference;
		this.spectrumCount = spectrumCount;
		this.peptideCount = peptideCount;
		this.coverage = coverage;
		this.pi = PI < 0f ? 0f : PI;
		this.mw = mw;
		this.numaas = length;
		this.isTarget = isTarget;
	}
	
	/*
	 * As the inaccuracy of float values, the comparison may be bias. And the
	 * output values into a file should be formated with a constant bit after
	 * point. So this value is the final outputted value, and also should be
	 * used for comparison of is they are the same;
	 */
	// private String formatedProb;
	public ReferenceDetail(String reference, int spcount, int pepcount,
	        float coverage, float pi, double mw, int num_aminoacids,
	        boolean isTarget, double SIn) {
		this.reference = reference;
		this.spectrumCount = spcount;
		this.peptideCount = pepcount;
		this.coverage = coverage;
		this.pi = pi < 0f ? 0f : pi;
		this.mw = mw;
		this.numaas = num_aminoacids;
		this.isTarget = isTarget;
		this.SIn = SIn;
	}

	public ReferenceDetail(String reference, int spcount, int pepcount,
	        float coverage, float probability, float pi, double mw,
	        int num_aminoacids, boolean isTarget, int groupIdx, int crossPro, double SIn) {

		this(reference, spcount, pepcount, coverage, probability, pi, mw,
		        num_aminoacids, isTarget, SIn);

		this.groupIdx = groupIdx;
		this.crossPro = crossPro;
	}

	public ReferenceDetail(String reference, int spcount, int pepcount,
	        float coverage, float probability, float pi, double mw,
	        int num_aminoacids, boolean isTarget, int groupIdx, int crossPro, double SIn, double hyproScore) {

		this(reference, spcount, pepcount, coverage, probability, pi, mw,
		        num_aminoacids, isTarget, groupIdx, crossPro, SIn);
		this.hyproScore = hyproScore;
	}
	
	public ReferenceDetail(String reference, int spcount, int pepcount,
	        float coverage, float probability, float pi, double mw,
	        int num_aminoacids, boolean isTarget, double SIn) {
		this(reference, spcount, pepcount, coverage, pi, mw, num_aminoacids,
		        isTarget, SIn);

		this.setProbability(probability);
	}

	public ReferenceDetail(String reference, int spcount, int pepcount,
	        float coverage, float probability, float pi, double mw, int num_aminoacids,
	        boolean isTarget, int groupIdx, int crossPro, double SIn, double hyproScore, double ratio) {
		this(reference, spcount, pepcount, coverage, probability, pi, mw,
		        num_aminoacids, isTarget, groupIdx, crossPro, SIn, hyproScore);
		
		this.ratio = ratio;
	}
	
	/**
	 * Only used in quantitation result.
	 * @param reference
	 * @param SIn
	 */
	public ReferenceDetail(String subRef, double SIn){
		this.subRef = subRef;
		this.SIn = SIn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IReferenceDetail#getName()
	 */
	public String getName() {
		return this.reference;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IReferenceDetail#setProbability(float)
	 */
	public void setProbability(float value) {
		float v;
		if (value < 0) {
			//Set as unsigned
			return ;
			
		} else if (value > 1) {
			System.out.println("The protein probablity value \"" + value
			        + "\" is bigger than 1. Set as 1 instead.");
			v = 1f;
		} else
			v = value;
		this.setProb(v);
	}

	private void setProb(float value) {
		this.prob = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IReferenceDetail#getProbability()
	 */
	public float getProbability() {
		return this.prob;
	}

	/**
	 * @return the protein sequence of this protein
	 */
	public ProteinSequence getProteinSequence() {
		return this.pSeq;
	}

	/*
	 * The formated probability with constant accuracy; If two proteins with the
	 * same probability, this value should be the same. Obviously, the bit count
	 * of the formatted value should less than that of a float value.
	 */
	// public String getFormatedProbability() {
	// return this.formatedProb;
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IReferenceDetail#getPI()
	 */
	public double getPI() {
		return this.pi;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IReferenceDetail#isTarget()
	 */
	public boolean isTarget() {
		return this.isTarget;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IReferenceDetail#getMW()
	 */
	public double getMW() {
		return this.mw;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IReferenceDetail#getCoverage()
	 */
	public float getCoverage() {
		return this.coverage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IReferenceDetail#getSpectrumCount()
	 */
	public int getSpectrumCount() {
		return this.spectrumCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IReferenceDetail#getGroupIndex()
	 */
	public int getGroupIndex() {
		return this.groupIdx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IReferenceDetail#getCrossProtein()
	 */
	public int getCrossProtein() {
		return this.crossPro;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IReferenceDetail#getPeptideCount()
	 */
	public int getPeptideCount() {
		return this.peptideCount;
	}

	@Override
	public void setCoverage(float coverage) {
		this.coverage = coverage;
	}

	@Override
	public void setCrossProtein(int count) {
		this.crossPro = count;
	}

	@Override
	public void setGroupIndex(int idx) {
		this.groupIdx = idx;
	}

	@Override
	public void setPeptideCount(int peptideCount) {
		this.peptideCount = peptideCount;
	}

	@Override
	public void setSpectrumCount(int spectrumCount) {
		this.spectrumCount = spectrumCount;
	}

	@Override
	public String toString() {
		return formatter.format(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome
	 *      .IReferenceDetail#getNumAminoacids()
	 */
	@Override
	public int getNumAminoacids() {
		return this.numaas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome
	 *      .IReferenceDetail#getReferenceFormat()
	 */
	@Override
	public IReferenceDetailFormat getReferenceFormat() {
		return formatter;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail#getSIn()
	 */
	@Override
	public double getSIn() {
		// TODO Auto-generated method stub
		return SIn;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail#setSIn(double)
	 */
	@Override
	public void setSIn(double SIn) {
		// TODO Auto-generated method stub
		this.SIn = SIn;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail#getRatio()
	 */
	@Override
	public double getRatio() {
		// TODO Auto-generated method stub
		return ratio;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail#setRatio(double)
	 */
	@Override
	public void setRatio(double Ratio) {
		// TODO Auto-generated method stub
		this.ratio = Ratio;
	}
	
	public double [] getSIns(){
		return SIns;
	}
	
	public void setSIns(double [] SIns){
		this.SIns = SIns;
	}

	public String getSubRef(){
		return subRef;
	}
	
	public void setSubRef(String subRef){
		this.subRef = subRef;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail#getSInStr()
	 */
	@Override
	public String getSInStr() {
		// TODO Auto-generated method stub
		
		StringBuilder sb = new StringBuilder();
		String [] ratios = new String[SIns.length];
		double SIn = 0;
		for(int i=0;i<SIns.length;i++){
			if(SIns[i]>0){
				SIn = SIns[i];
				break;
			}									
		}
		if(SIn==0){
			Arrays.fill(ratios, 0);
		}else{
			for(int i=0;i<SIns.length;i++){
				ratios[i] = df4.format(SIns[i]/SIn);
			}
		}
		
		for(int i=0;i<ratios.length;i++){
			sb.append(ratios[i]).append(" : ");
		}
		return sb.substring(0,sb.length()-3);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail#getHyporScore()
	 */
	@Override
	public double getHyporScore() {
		// TODO Auto-generated method stub
		return hyproScore;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail#setHyproScore()
	 */
	@Override
	public void setHyproScore(double hyproScore) {
		// TODO Auto-generated method stub
		this.hyproScore = hyproScore;
	}
}
