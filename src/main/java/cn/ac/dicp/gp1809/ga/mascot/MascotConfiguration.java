/* 
 ******************************************************************************
 * File: MascotConfiguration.java * * * Created on 2011-8-31
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.mascot;

import java.io.InputStream;
import java.io.OutputStream;

import cn.ac.dicp.gp1809.ga.Chromosome;
import cn.ac.dicp.gp1809.ga.Configuration;
import cn.ac.dicp.gp1809.ga.FitnessFunction;

/**
 * @author ck
 *
 * @version 2011-8-31, 13:32:27
 */
public class MascotConfiguration extends Configuration {
	
	private boolean isIonScore = true;
	private boolean isDeltaIS = false;
	private boolean isISmht = true;
	private boolean isISMit = true;
	private boolean isEValue = true;
	
	private short ionScoreBit, deltaISBit, isMhtBit, isMitBit, eValueBit;
	
	private double maxFPR;
	private short optimizeType = 0;
	private int maxgenenum = 5;
	private float[][] peptides;
	
	private MascotValueLimit valueLimit;
	
	/**
	 * whenever setFPR() or setPeptides is excuted, this value is set to true;
	 * And function of fitness need to refresh;
	 */
	private boolean isChanged=true;
	private FitnessFunction function;
	
	/**
	 * Sometimes not all the genes are used for the optimizing, then default values should be used
	 * for the final filtering, use this values.
	 */
	private double[] nullChromosomeValues = new double[] {0, 0, 0, 0, 0, 0, 0};;

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.Configuration#getFitnessFunction()
	 */
	@Override
	public FitnessFunction getFitnessFunction() {
		// TODO Auto-generated method stub
		if(this.isChanged) {
			this.function = new MascotFitnessFunction(peptides, this.getMaxFPR(), optimizeType);
		}
		
		return this.function;
	}

	public void setPeptides(float[][] peptides){
		this.peptides = peptides;
		this.isChanged = true;
	}
	
	/**
	 * @return
	 */
	private double getMaxFPR() {
		// TODO Auto-generated method stub
		return this.maxFPR;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.Configuration#getSampleChromosome()
	 */
	@Override
	public Chromosome getSampleChromosome() {
		// TODO Auto-generated method stub
		
		int maxgene = this.maxGeneNum();
		Chromosome sample = new MascotChromosome(this, maxgene);
		
		if(this.isIonScoreFilter())
			sample.setGene(new IonScoreGene(this),0);

//		if(this.isDeltaISFilter())
//			sample.setGene(new DeltaISGene(this),1);

		if(this.isIsMhtFilter())
			sample.setGene(new ScoreVsMHTGene(this),2);

		if(this.isIsMitFilter())
			sample.setGene(new ScoreVsMITGene(this),3);
		
		if(this.isEvalueFilter())
			sample.setGene(new DeltaISGene(this),4);

		return sample;
	}

	/**
	 * @return
	 */
	private boolean isIsMitFilter() {
		// TODO Auto-generated method stub
		return this.isISMit;
	}

	/**
	 * @return
	 */
	private boolean isIsMhtFilter() {
		// TODO Auto-generated method stub
		return this.isISmht;
	}

	/**
	 * @return
	 */
	private boolean isDeltaISFilter() {
		// TODO Auto-generated method stub
		return this.isDeltaIS;
	}

	/**
	 * @return
	 */
	private boolean isIonScoreFilter() {
		// TODO Auto-generated method stub
		return this.isIonScore;
	}
	
	/**
	 * @return
	 */
	private boolean isEvalueFilter() {
		// TODO Auto-generated method stub
		return this.isEValue;
	}

	public MascotValueLimit getMascotValueLimit(){
		if(this.valueLimit==null)
			this.valueLimit = new MascotValueLimit();
		
		return this.valueLimit;
	}
	
	public void setMascotValueLimit(MascotValueLimit valueLimit){
		this.valueLimit = valueLimit;
	}
	
	public double setMaxFPR(double fpr){
		this.maxFPR = fpr;
		this.isChanged = true;
		return fpr;
	}

	public void setOptimizeType(short optimizeType){
		if(this.optimizeType != optimizeType){
			this.isChanged = true;
			this.optimizeType = optimizeType;
		}
	}
	
	public short getOptimizeType(){
		return this.optimizeType;
	}

	/**
	 * @return
	 */
	private int maxGeneNum() {
		// TODO Auto-generated method stub
		return this.maxgenenum;
	}

	public short getIonScoreGeneBit(){
		return this.ionScoreBit;
	}
	
	public void setIonScoreGeneBit(short genebit){
		this.ionScoreBit = genebit;
	}
	
	public short getDeltaISGeneBit(){
		return this.deltaISBit;
	}
	
	public void setDeltaISGeneBit(short genebit){
		this.deltaISBit = genebit;
	}
	
	public short getMhtGeneBit(){
		return this.isMhtBit;
	}
	
	public void setMhtGeneBit(short genebit){
		this.isMhtBit = genebit;
	}
	
	public short getMitGeneBit(){
		return this.isMitBit;
	}
	
	public void setEvalueGeneBit(short genebit){
		this.eValueBit = genebit;
	}
	
	public short getEvalueGeneBit(){
		return this.eValueBit;
	}
	
	public void setMitGeneBit(short genebit){
		this.isMitBit = genebit;
	}

	public void setIonScoreFilter(boolean isIonScore){
		this.isIonScore = isIonScore;
	}
	
	public void setDeltaISFilter(boolean isDeltaIS){
		this.isDeltaIS = isDeltaIS;
	}
	
	public void setMhtFilter(boolean isISmht){
		this.isISmht = isISmht;
	}
	
	public void setMitFilter(boolean isISMit){
		this.isISMit = isISMit;
	}
	
	public void setEvalueFilter(boolean isEValue){
		this.isEValue = isEValue;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.Configuration#readFromFile(java.io.InputStream)
	 */
	@Override
	public Configuration readFromFile(InputStream instream) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.ga.Configuration#write(java.io.OutputStream)
	 */
	@Override
	public void write(OutputStream outstream) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return
	 */
	public double [] getNullChromosomeValues() {
		// TODO Auto-generated method stub
		return this.nullChromosomeValues;
	}

}
