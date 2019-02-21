/* 
 ******************************************************************************
 * File: GAEntropyDistanceCalculator.java * * * Created on 04-08-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.penn.probability;

import java.io.InputStream;
import java.io.OutputStream;

import cn.ac.dicp.gp1809.ga.Chromosome;
import cn.ac.dicp.gp1809.ga.Configuration;
import cn.ac.dicp.gp1809.ga.FitnessFunction;
import cn.ac.dicp.gp1809.ga.Gene;
import cn.ac.dicp.gp1809.ga.GeneticOperator;
import cn.ac.dicp.gp1809.ga.Population;
import cn.ac.dicp.gp1809.ga.RoulettewheelSelector;
import cn.ac.dicp.gp1809.ga.Selector;
import cn.ac.dicp.gp1809.util.math.Logarithm;

/**
 * 
 * @author Xinning
 * @version 0.1, 04-08-2008, 10:38:02
 */
public class GAEntropyDistanceCalculator extends AbstractDistanceCalculator {

	//When the precise sloution is hardly accessed, after the max generation reached, the circulation will
	//stop.
	private static final int maxGeneration = 200;
	
	/**
	 * @param peps
	 */
	public GAEntropyDistanceCalculator(PepNorm[] peps) {
		super(peps);
		System.out.println("GAEntropyDistanceCalculator is used.");
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.probability.AbstractDistanceCalculator
	 * 			#calculateWeights(cn.ac.dicp.gp1809.proteome.probability.PepNorm[])
	 */
	@Override
	protected void calculateWeights(PepNorm[] peps) {
		this.getWeights(new ConfigurationImp(peps), -1d);
	}
	
	private void getWeights(ConfigurationImp config, double fitnessall){
		if(fitnessall < 0d){
			int falsecount = 0;
			for(PepNorm pep : config.getPeps()){
				if(pep.isRev())
					falsecount ++;
			}
			System.out.println(config.getPeps().length+" "+falsecount);
			System.out.println(1d-((FitnessFunctionImp)config.getFitnessFunction())
						.getEntropy(config.getPeps().length, falsecount));
			
			fitnessall = this.getFitness(config);
		}
		
		System.out.println("all"+fitnessall);
		
		
		double fitnessions = fitnessall;
		if(config.isIonsu()){
			config.setIonsu(false);
			fitnessions = this.getFitness(config);
			if(fitnessions >= fitnessall){//Indicating that this filter is useless
				System.out.println("Ions no use.");
				this.getWeights(config, fitnessions);
				return ;
			}
			else{
				config.setIonsu(true);
			}
		}
		
		double fitnessdms = fitnessall;
		if(config.isDmsu()){
			config.setDmsu(false);
			fitnessdms = this.getFitness(config);
			if(fitnessdms >= fitnessall){//Indicating that this filter is useless
				System.out.println("Deltams no use.");
				this.getWeights(config, fitnessdms);
				return ;
			}
			else{
				config.setDmsu(true);
			}
		}
		
		double fitnesssp = fitnessall;
		if(config.isSpu()){
			config.setSpu(false);
			fitnesssp = this.getFitness(config);
			if(fitnesssp >= fitnessall){//Indicating that this filter is useless
				System.out.println("Sp no use.");
				this.getWeights(config, fitnesssp);
				return ;
			}
			else{
				config.setSpu(true);
			}
		}
		
		double fitnessrsp = fitnessall;
		if(config.isRspu()){
			config.setRspu(false);
			fitnessrsp = this.getFitness(config);
			if(fitnessrsp >= fitnessall){//Indicating that this filter is useless
				System.out.println("Rsp no use.");
				this.getWeights(config, fitnessrsp);
				return ;
			}
			else{
				config.setRspu(true);
			}
		}
		
		double fitnessMPF = fitnessall;
		if(config.isMPFu()){
			config.setMPFu(false);
			fitnessrsp = this.getFitness(config);
			if(fitnessrsp >= fitnessall){//Indicating that this filter is useless
				System.out.println("MPF no use.");
				this.getWeights(config, fitnessrsp);
				return ;
			}
			else{
				config.setMPFu(true);
			}
		}
		
		double fitnessxc = fitnessall;
		if(config.isXcu()){
			config.setXcu(false);
			fitnessxc = this.getFitness(config);
			if(fitnessxc >= fitnessall){//Indicating that this filter is useless
				System.out.println("Xcorr no use.");
				this.getWeights(config, fitnessxc);//Reiterate
				return ;
			}
			else{
				config.setXcu(true);
			}
		}
		
		double fitnessdcn = fitnessall;
		if(config.isDcnu()){
			config.setDcnu(false);
			fitnessdcn = this.getFitness(config);
			if(fitnessdcn >= fitnessall){//Indicating that this filter is useless
				System.out.println("Dcn no use.");
				this.getWeights(config, fitnessdcn);
				return ;
			}
			else{
				config.setDcnu(true);
			}
		}
		
		double fitnessSim = fitnessall;
		if(config.isSimu()){
			config.setSimu(false);
			fitnessSim = this.getFitness(config);
			if(fitnessSim >= fitnessall){//Indicating that this filter is useless
				System.out.println("Sim no use.");
				this.getWeights(config, fitnessSim);
				return ;
			}
			else{
				config.setSimu(true);
			}
		}
		
		this.setWeights2(fitnessall-fitnessxc, fitnessall-fitnessdcn, fitnessall-fitnesssp,
				fitnessall-fitnessrsp, fitnessall-fitnessdms, fitnessall-fitnessions, 
				fitnessall-fitnessMPF, fitnessall-fitnessSim);
	}
	
	
	/*
	 * Calculate the fitness which is the information entropy of the datasets after 
	 * filter.
	 */
	private double getFitness(ConfigurationImp config){
		Population last = Population.GenerateRandomPopulation(config);
		Population next;
		Selector selector = new RoulettewheelSelector(config);
		GeneticOperator operator = new GeneticOperator(config);
		int generation = 0;
		
		while(generation++<maxGeneration){
			next = new Population(config);
			selector.select(last,next);
			operator.operate(next);
			last = next;
			
//			System.out.println(last.determineBestFitness());
		}
		
//		double[] values = last.determineFittestChromosome().values();
//		for(double value : values){
	//		System.out.print(value+" ");
	//	}
//		System.out.println();
		
		return last.determineBestFitness();
	}
	
	private class GeneImp extends Gene{
		private static final short L = 10;
		
		/*
		 * Max value of this gene. When decoding gene to actual value, this max bound must be computed;
		 * this value equals 2^genelength;
		 * The observed value of the random sequest gene is ranged from 0-maxbitvalue.
		 */
		private static final double maxBitValue = 1024d; //Math.pow(2d,L);
		
		public GeneImp(final Configuration config){
			super(config);
		}
		
		@Override
		public String encode() {
			return  getRandomGenerator().generateBinString(this.length());
		}

		@Override
		public void setLength() {
			this.setLength(L);
		}

		@Override
		public double value() {
			double obvalue = Integer.parseInt(this.genestring,2);
			
			return obvalue/maxBitValue;
		}
	}
	
	private class ConfigurationImp extends Configuration{
		private PepNorm[] peps;
		
		private boolean xcu = true;
		private boolean dcnu = true;
		private boolean spu = true;
		private boolean rspu = true;
		private boolean dmsu = true;
		private boolean ionsu = true;
		private boolean mpfu = true;
		private boolean simu = true;
		
		public ConfigurationImp(PepNorm[] peps){
			this.setGeneNumber(8);
			this.peps = peps;
			
			this.setCrossRate(0.9f);
			this.setPopulationSize(20);
			this.setMutateRate(0.3f);
		}
		
		@Override
		public FitnessFunction getFitnessFunction() {
			return new FitnessFunctionImp(peps, this);
		}

		@Override
		public Chromosome getSampleChromosome() {
			int genenum = this.getGeneNumber();
			Chromosome sample = new Chromosome(this, genenum);
			
			for(int i=0;i<genenum;i++){
				sample.setGene(new GeneImp(this), i);
			}
			
			return sample;
		}
		

		/**
		 * @return the peps
		 */
		protected final PepNorm[] getPeps() {
			return peps;
		}

		/**
		 * @param peps the peps to set
		 */
		protected final void setPeps(PepNorm[] peps) {
			this.peps = peps;
		}

		/**
		 * @return the xcu
		 */
		protected final boolean isXcu() {
			return xcu;
		}

		/**
		 * @param xcu the xcu to set
		 */
		protected final void setXcu(boolean xcu) {
			if(this.xcu != xcu){
				if(xcu)
					this.setGeneNumber(this.getGeneNumber()+1);
				else 
					this.setGeneNumber(this.getGeneNumber()-1);
				
				this.xcu = xcu;
			}
		}

		/**
		 * @return the dcnu
		 */
		protected final boolean isDcnu() {
			return dcnu;
		}

		/**
		 * @param dcnu the dcnu to set
		 */
		protected final void setDcnu(boolean dcnu) {
			if(this.dcnu != dcnu){
				if(dcnu)
					this.setGeneNumber(this.getGeneNumber()+1);
				else 
					this.setGeneNumber(this.getGeneNumber()-1);
				
				this.dcnu = dcnu;
			}
			
		}

		/**
		 * @return the spu
		 */
		protected final boolean isSpu() {
			return spu;
		}

		/**
		 * @param spu the spu to set
		 */
		protected final void setSpu(boolean spu) {
			if(this.spu != spu){
				if(spu)
					this.setGeneNumber(this.getGeneNumber()+1);
				else 
					this.setGeneNumber(this.getGeneNumber()-1);
				
				this.spu = spu;
			}
		}

		/**
		 * @return the rspu
		 */
		protected final boolean isRspu() {
			return rspu;
		}

		/**
		 * @param rspu the rspu to set
		 */
		protected final void setRspu(boolean rspu) {
			if(this.rspu != rspu){
				if(rspu)
					this.setGeneNumber(this.getGeneNumber()+1);
				else 
					this.setGeneNumber(this.getGeneNumber()-1);
				
				this.rspu = rspu;
			}
		}

		/**
		 * @return the dmsu
		 */
		protected final boolean isDmsu() {
			return dmsu;
		}

		/**
		 * @param dmsu the dmsu to set
		 */
		protected final void setDmsu(boolean dmsu) {
			if(this.dmsu != dmsu){
				if(dmsu)
					this.setGeneNumber(this.getGeneNumber()+1);
				else 
					this.setGeneNumber(this.getGeneNumber()-1);
				
				this.dmsu= dmsu;
			}
		}

		/**
		 * @return the ionsu
		 */
		protected final boolean isIonsu() {
			return ionsu;
		}
		
		/**
		 * @param ionsu the ionsu to set
		 */
		protected final void setIonsu(boolean ionsu) {
			if(this.ionsu != ionsu){
				if(ionsu)
					this.setGeneNumber(this.getGeneNumber()+1);
				else 
					this.setGeneNumber(this.getGeneNumber()-1);
				
				this.ionsu = ionsu;
			}
		}
		
		/**
		 * @return the ionsu
		 */
		protected final boolean isMPFu(){
			return mpfu;
		}

		/**
		 * @param mpfu the mpfu to set
		 */
		protected final void setMPFu(boolean mpfu) {
			if(this.mpfu != mpfu){
				if(mpfu)
					this.setGeneNumber(this.getGeneNumber()+1);
				else 
					this.setGeneNumber(this.getGeneNumber()-1);
				
				this.mpfu = mpfu;
			}
		}
		
		/**
		 * @return the ionsu
		 */
		protected final boolean isSimu(){
			return simu;
		}

		/**
		 * @param mpfu the mpfu to set
		 */
		protected final void setSimu(boolean simu) {
			if(this.simu != simu){
				if(simu)
					this.setGeneNumber(this.getGeneNumber()+1);
				else 
					this.setGeneNumber(this.getGeneNumber()-1);
				
				this.simu = simu;
			}
		}
		
		@Override
		public Configuration readFromFile(InputStream instream) {
			return null;
		}

		@Override
		public void write(OutputStream outstream) {
		}
		
	}
	
	private class FitnessFunctionImp extends FitnessFunction{
		private PepNorm[] peps;
		
		/*
		 * Sortted arrays by different attribute, from small to big
		 */
//		private PepNorm[] pepsxc;
//		private PepNorm[] pepsdcn;
		
		private ConfigurationImp config;
		
		private int total;
		
		private int totalfalse;
		
		public FitnessFunctionImp(PepNorm[] peps, ConfigurationImp config){
			this.config = config;
			
			this.total = peps.length;
			for(PepNorm pep : peps)
				if(pep.isRev())
					totalfalse ++;
			this.peps = peps;
			
			/*
			 * Clone and sort by different attributes, for fast search.
			 */
			/*
			this.pepsxc = peps.clone();
			Arrays.sort(pepsxc,new PepNormComparator(PepNormComparator.SORT_BY_XCORR));
			this.pepsdcn = this.pepsxc.clone();
			Arrays.sort(pepsdcn,new PepNormComparator(PepNormComparator.SORT_BY_DCN));
			this.pepssp = this.pepsxc.clone();
			Arrays.sort(pepssp,new PepNormComparator(PepNormComparator.SORT_BY_SP));
			this.pepsrsp = this.pepsxc.clone();
			Arrays.sort(pepsrsp,new PepNormComparator(PepNormComparator.SORT_BY_RSP));
			this.pepsdms = this.pepsxc.clone();
			Arrays.sort(pepsdms,new PepNormComparator(PepNormComparator.SORT_BY_DMS));
			this.pepsions = this.pepsxc.clone();
			Arrays.sort(pepsions,new PepNormComparator(PepNormComparator.SORT_BY_IONS));
			*/
		}
		
		/*
		 * The fitness is the reverse value of precision. 
		 * 1/precision.
		 * 
		 * (non-Javadoc)
		 * @see cn.ac.dicp.gp1809.ga.FitnessFunction#evaluate(cn.ac.dicp.gp1809.ga.Chromosome)
		 */
		@Override
		protected double evaluate(Chromosome a_chrome) {
			double[] values = a_chrome.values();
			int curti = 0;
			int curtfalse = 0;
			PepNorm[] peps = this.peps;
			
			boolean xcu = config.isXcu();
			boolean dcnu = config.isDcnu();
			boolean spu = config.isSpu();
			boolean rspu = config.isRspu();
			boolean dmsu = config.isDmsu();
			boolean ionsu = config.isIonsu();
			boolean mpfu = config.isMPFu();
			
			
			for(PepNorm pep : peps){
				int idx = 0;
				
				if(xcu&&values[idx++] > pep.getXcn()) continue;
				if(dcnu&&values[idx++] > pep.getDcn()) continue;
				if(spu&&values[idx++] > pep.getSpn()) continue;
				if(rspu&&values[idx++] > pep.getRspn()) continue;
				if(dmsu&&values[idx++] > pep.getDMS()) continue;
				if(ionsu&&values[idx++] > pep.getIons()) continue;
				if(ionsu&&values[idx++] > pep.getIons()) continue;
				if(mpfu&&values[idx++] > pep.getMPF()) continue;
				
				curti ++;
				if(pep.isRev())
					curtfalse ++;
			}
			
			return 1d-this.getEntropy(curti, curtfalse);
		}
		
		/*
		 * Compute the current entropy
		 * @param total total number of peptide
		 * @param totalfalse total number of false positive peptides
		 * @param curti current split point
		 * @param curtfalse current false positive number within the current split point
		 */
		private double getEntropy(double curti, double curtfalse){
			double e1 = 0d;
			double percent = curti/total;
			if(percent > 0.000001d){
				double p11 = curtfalse*2d/curti;
				p11 = p11 > 1d? 1d : p11;
				double p12 = 1d-p11;
				e1 = percent*(getInfValue(p11)+getInfValue(p12));
			}
			
			double e2 = 0d;
			percent = 1d - percent;
			if(percent>0.000001d){
				double p21 = (totalfalse-curtfalse)*2d/(total-curti);
				p21 = p21 > 1d ? 1d : p21;
				double p22 = 1- p21;
				e2 = percent*(getInfValue(p21)+getInfValue(p22));
			}

//			System.out.println("Total: "+curti+", false: "+curtfalse+" entropy: "+(e1+e2));
			
			return e1 + e2;
		}
		
		private double getInfValue(double pi){
			if(pi<0.000001d)//the percent 1/1000,000
				return 0;
			return -pi*Logarithm.log2(pi);
		}
	}
}
