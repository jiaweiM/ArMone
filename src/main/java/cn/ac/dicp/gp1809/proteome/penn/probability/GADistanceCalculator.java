/* 
 ******************************************************************************
 * File: GADistanceCalculator.java * * * Created on 04-10-2008
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
 * Using GA to optimize the dimension weights. Distance weights are randomly assigned
 * and optimized by GA, so that the peptide identifications identified by the distance
 * filter which is the value between peptides and the origin point are the maximum.
 * 
 * 
 * @author Xinning
 * @version 0.1, 04-10-2008, 21:07:24
 */
public class GADistanceCalculator extends AbstractDistanceCalculator {

	public GADistanceCalculator(PepNorm[] peps) {
		super(peps);
		System.out.println("GADistanceCalculator is used.");
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.probability.AbstractDistanceCalculator
	 * 			#calculateWeights(cn.ac.dicp.gp1809.proteome.probability.PepNorm[])
	 */
	@Override
	protected void calculateWeights(PepNorm[] peps) {
		
		/*
		//Sum distance of all the target matches away from the origin
		float sum_target_dist = 0f;
		//Sum distance of all the decoy matches away from the origin
		float sum_decoy_dist = 0f;
		
		//The away distance is normalized to 1;
		for(PepNorm pep : peps){
			
			if(pep.isRev())
				sum_decoy_dist += pep.getDcn();
			else
				sum_target_dist += pep.getDcn();
		}
		
		System.out.println("Dcn dist diff: "+(sum_target_dist - sum_decoy_dist));
		
		
		//Sum distance of all the target matches away from the origin
		sum_target_dist = 0f;
		//Sum distance of all the decoy matches away from the origin
		sum_decoy_dist = 0f;
		
		//The away distance is normalized to 1;
		for(PepNorm pep : peps){
			
			if(pep.isRev())
				sum_decoy_dist += pep.getXcn();
			else
				sum_target_dist += pep.getXcn();
		}
		
		System.out.println("Xcn dist diff: "+(sum_target_dist - sum_decoy_dist));
		*/
		
		ConfigurationImp config = new ConfigurationImp(peps, 0.01d);
		double[] weights1 = this.getWeights(config, -1d, null);
		System.out.println("fitness: "+this.fitnessall);
//		for(double d : weights1){
//			System.out.println(d);
//		}
		/*
		config.setFDR(0.05d);
		double[] weights2 = this.determineWeights(config);
		System.out.println("FDR: "+config.fdr+"; pepcount: "+this.fitness);
		for(double d : weights2){
			System.out.println(d);
		}
		config.setFDR(0.15d);
		double[] weights3 = this.determineWeights(config);
		System.out.println("FDR: "+config.fdr+"; pepcount: "+this.fitness);
		for(double d : weights3){
			System.out.println(d);
		}
		
		this.setWeights2((weights1[0]+weights2[0]+weights3[0])/3d, 
				(weights1[1]+weights2[1]+weights3[1])/3d,
				(weights1[2]+weights2[2]+weights3[2])/3d,
				(weights1[3]+weights2[3]+weights3[3])/3d,
				(weights1[4]+weights2[4]+weights3[4])/3d,
				(weights1[5]+weights2[5]+weights3[5])/3d,
				(weights1[6]+weights2[6]+weights3[6])/3d,
				(weights1[7]+weights2[7]+weights3[7])/3d);
		*/
		
		this.setWeights2(weights1[0],weights1[1],weights1[2],weights1[3],weights1[4],
				weights1[5],weights1[6],weights1[7]);
				
	}
	
	private double fitnessall;
	
	private double[] getWeights(ConfigurationImp config, double count, double[] weights){
		if(count < 0d){
			weights = this.determineWeights(config);
			count = this.fitness;
		}
		fitnessall = count;
		
		/*
		System.out.println("all "+count);
		double sum = 0d;
		for(double w : weights)
			sum += w;
		for(double w : weights)
			System.out.println(w/sum);
		*/
		
		if(config.isIonFilter()){
			config.setIonFilter(false);
			double[] w2 = this.determineWeights(config);
			double fit2 = this.fitness;
			if(fit2 >= count){//Indicating that this filter is useless
				System.out.println("Ions no use.");
				return this.getWeights(config, fit2, w2);
			}
			else{
				config.setIonFilter(true);
			}
		}
		
		if(config.isDmsFilter()){
			config.setDmsFilter(false);
			double[] w2 = this.determineWeights(config);
			double fit2 = this.fitness;
			if(fit2 >= count){//Indicating that this filter is useless
				System.out.println("DeltaMH+ no use.");
				return this.getWeights(config, fit2, w2);
			}
			else{
				config.setDmsFilter(true);
			}
		}
		
		if(config.isSpFilter()){
			config.setSpFilter(false);
			double[] w2 = this.determineWeights(config);
			double fit2 = this.fitness;
			if(fit2 >= count){//Indicating that this filter is useless
				System.out.println("Sp no use.");
				return this.getWeights(config, fit2, w2);
			}
			else{
				config.setSpFilter(true);
			}
		}
		
		if(config.isRspFilter()){
			config.setRspFilter(false);
			double[] w2 = this.determineWeights(config);
			double fit2 = this.fitness;
			if(fit2 >= count){//Indicating that this filter is useless
				System.out.println("Rsp no use.");
				return this.getWeights(config, fit2, w2);
			}
			else{
				config.setRspFilter(true);
			}
		}
		
		if(config.isMPFFilter()){
			config.setMPFFilter(false);
			double[] w2 = this.determineWeights(config);
			double fit2 = this.fitness;
			if(fit2 >= count){//Indicating that this filter is useless
				System.out.println("MPF no use.");
				return this.getWeights(config, fit2, w2);
			}
			else{
				config.setMPFFilter(true);
			}
		}

		if(config.isXcorrFilter()){
			config.setXcorrFilter(false);
			double[] w2 = this.determineWeights(config);
			double fit2 = this.fitness;
			if(fit2 >= count){//Indicating that this filter is useless
				System.out.println("Xcorr no use.");
				return this.getWeights(config, fit2, w2);
			}
			else{
				config.setXcorrFilter(true);
			}
		}
		
		if(config.isSimFilter()){
			config.setSimFilter(false);
			double[] w2 = this.determineWeights(config);
			double fit2 = this.fitness;
			if(fit2 >= count){//Indicating that this filter is useless
				System.out.println("Sim no use.");
				return this.getWeights(config, fit2, w2);
			}
			else{
				config.setSimFilter(true);
			}
		}
		
		if(config.isDeltaCnFilter()){
			config.setDeltaCnFilter(false);
			double[] w2 = this.determineWeights(config);
			double fit2 = this.fitness;
			if(fit2 >= count){//Indicating that this filter is useless
				System.out.println("DeltaCn no use.");
				return this.getWeights(config, fit2, w2);
			}
			else{
				config.setDeltaCnFilter(true);
			}
		}
		
		return weights;
	}
	
	/**
	 * If GA have converged to a fix point during the last 
	 * <max_duriation> loops, the evolution will be terminated.
	 */
	private static int max_duriation = 100;
	private double fitness;
	private double[] determineWeights(Configuration config){
		Population last = Population.GenerateRandomPopulation(config);
		Population next;
		Selector selector = new RoulettewheelSelector(config);
		GeneticOperator operator = new GeneticOperator(config);
		int generation = 0;
		int max = config.getGenerationSize();
		int size = config.getPopulationSize()-1;
		
		double lastbestfit = 0d;
		Chromosome lastbest = last.getChromosome(size);
		int duriation = 0;
		
		while(generation++<max){
			next = new Population(config);
			selector.select(last,next);
			operator.operate(next);
			
			Chromosome curtbest = next.getChromosome(size);
			double curtbestfit = curtbest.getFitnessValue();
			if(curtbestfit <= lastbestfit){//should always be ==
				if(duriation > max_duriation)//end of evolution
					break;
				
				duriation= (duriation == 0) ? 1 : duriation+1;
			}
			else{
				duriation = 0;
				lastbest = curtbest;
				lastbestfit = curtbestfit;
			}
			
			last = next;
		}
		
//		System.out.println("Evoluation ended in "+generation+" loops.");
		
		fitness = lastbestfit;
		return lastbest.values();
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
			return getRandomGenerator().generateBinString(this.length());
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
	
	private class ChromosomeImp extends Chromosome{

		public ChromosomeImp(Configuration a_configuration, Gene gene, int size) {
			super(a_configuration, gene, size);
		}

		public ChromosomeImp(Configuration a_configuration, int size) {
			super(a_configuration, size);
		}
		
		@Override
		public double[] values(){
			int len = this.size();
			double[] values = new double[len];
			for(int i=0;i<len;i++){
				Gene temp = this.getGene(i);
				if(temp==null)
					values[i] = 0d;
				else
					values[i] = temp.value();
			}
				
			return values;
		}
	}
	
	private class ConfigurationImp extends Configuration{
		private PepNorm[] peps;
		
		private double fdr;
		
		private boolean isxcorr = true;
		private boolean isdcn = true;
		private boolean issp = true;
		private boolean isrsp = true;
		private boolean ision = true;
		private boolean isdms = true;
		private boolean isMPF = true;
		private boolean isSim = true;
		
		public ConfigurationImp(PepNorm[] peps, double fdr){
			this.setGeneNumber(8);
			this.peps = peps;
			this.fdr = fdr;
			
			this.setCrossRate(0.9f);
			this.setPopulationSize(20);
			this.setMutateRate(0.3f);
		}
		
		@Override
		public FitnessFunction getFitnessFunction() {
			return new FitnessFunctionImp(peps, fdr);
		}

		@Override
		public Chromosome getSampleChromosome() {
			int maxgene = this.getGeneNumber();
			Chromosome sample = new ChromosomeImp(this,maxgene);
			
			Gene gene = new GeneImp(this);
			if(this.isXcorrFilter())
				sample.setGene(gene.newGene(),0);

			if(this.isDeltaCnFilter())
				sample.setGene(gene.newGene(),1);

			if(this.isSpFilter())
				sample.setGene(gene.newGene(),2);
			
			if(this.isRspFilter())
				sample.setGene(gene.newGene(),3);
			
			if(this.isDmsFilter())
				sample.setGene(gene.newGene(),4);

			if(this.isIonFilter())
				sample.setGene(gene.newGene(),5);
			
			if(this.isMPFFilter())
				sample.setGene(gene.newGene(),6);
			
			if(this.isSim)
				sample.setGene(gene.newGene(), 7);
			
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
		
		public boolean isXcorrFilter(){
			return this.isxcorr;
		}
		
		public boolean isDeltaCnFilter(){
			return this.isdcn;
		}
		
		public boolean isSpFilter(){
			return this.issp;
		}
		
		public boolean isRspFilter(){
			return this.isrsp;
		}
		
		public boolean isIonFilter(){
			return this.ision;
		}
		
		public boolean isDmsFilter(){
			return this.isdms;
		}
		
		public boolean isMPFFilter(){
			return this.isMPF;
		}
		
		public boolean isSimFilter(){
			return this.isSim;
		}
		
		public void setXcorrFilter(boolean isxcorr){
			this.isxcorr = isxcorr;
		}
		
		public void setDeltaCnFilter(boolean isdcn){
			this.isdcn = isdcn;
		}
		
		public void setSpFilter(boolean issp){
			this.issp = issp;
		}
		
		public void setRspFilter(boolean isrsp){
			this.isrsp = isrsp;
		}
		
		public void setIonFilter(boolean ision){
			this.ision = ision;
		}
		
		public void setDmsFilter(boolean isdms){
			this.isdms = isdms;
		}
		
		public void setMPFFilter(boolean isMPF){
			this.isMPF = isMPF;
		}
		
		public void setSimFilter(boolean isSim){
			this.isSim = isSim;
		}
		
		public void setFDR(double fdr){
			this.fdr = fdr;
		}

		@Override
		public Configuration readFromFile(InputStream instream) {
			return null;
		}

		@Override
		public void write(OutputStream outstream) {
		}
	}
	
	/**
	 * Calculator used for the fitness calculation which combined different algorithms
	 * 
	 * @author Xinning
	 * @version 0.1, 04-11-2008, 09:56:01
	 */
	private class FCalculator{
		private int precision = 1000;
		private int[] counts;
		private int[] falsecounts;
		private double total;
		private double totalfalse;
		
		/**
		 * @param precision the precision, e.g. if the precision is 1000, this
		 *        indicates that the float value 0.001 is actual.
		 */
		FCalculator(int precision, int total, int totalfalse){
			this.precision = precision;
			this.counts = new int[this.precision+1];
			this.falsecounts = new int[this.precision+1];
			this.total = total;
			this.totalfalse = totalfalse;
		}
		
		public void put(PepNorm pep){
			int idx = (int)(pep.getAway()*precision);
			counts[idx]++;
			if(pep.isRev()){
				falsecounts[idx] ++;
			}
		}
		
		/**
		 * Reset for the new statistical
		 */
		public void reset(){
			for(int i=0, n= this.precision;i<n;i++){
				this.counts[i] = 0;// = new int[this.precision+1];
				this.falsecounts[i] = 0;// = new int[this.precision+1];
			}
		}
		
		public double getEntropy(){
			double en = 1d;
			int curti = 0;
			int curtfalse = 0;
			for(int i=0;i<=precision;i++){
				curti += this.counts[i];
				curtfalse += this.falsecounts[i];
				double curten = GADistanceCalculator.getEntropy(this.total, this.totalfalse, curti, curtfalse);
				if(curten<en){ en = curten;}
			}
					
			return en;
		}
		
		/**
		 * The count of identified peptides by this c
		 * @return
		 */
		public double getIdentifiedPepCount(double fdr){
			double total = this.total;
			double totalfalse = this.totalfalse;
			
			int curti = 0;
			int curtfalse = 0;
			for(int i=0;i<=precision;i++){
				curti += this.counts[i];
				curtfalse += this.falsecounts[i];
				double curtfdr = (totalfalse-curtfalse)*2d/(total-curti);
				if(curtfdr<=fdr)
					return total - curti;
			}
			
//			System.out.println(en+" "+best+" "+bestfalse+" "+(this.total-best)+" "+(this.totalfalse-bestfalse));
			
			return 0d;
		}
	}
	
	private class FitnessFunctionImp extends FitnessFunction{
		private PepNorm[] peps;
		
		private FCalculator precise;
		
		private int total;
		private int totalfalse;
		
//		private double fdr;
		
		public FitnessFunctionImp(PepNorm[] peps, double fdr){
			this.total = peps.length;
			for(PepNorm pep : peps)
				if(pep.isRev())
					totalfalse ++;
//			this.fdr = fdr;
			this.peps = peps.clone();
			this.precise = new FCalculator(1000, total , totalfalse);
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
			PepNorm[] peps = this.peps;
			
			double sum = 0d;
			for(double value : values)
				sum += value;
			
			float xcorr =(float) (values[0]/sum); 
			float dcn =(float) (values[1]/sum); 
			float sp =(float) (values[2]/sum); 
			float rsp =(float) (values[3]/sum); 
			float dms =(float) (values[4]/sum); 
			float ions =(float) (values[5]/sum); 
			float mpf =(float) (values[6]/sum); 
			float sim =(float) (values[7]/sum); 
			
			this.precise.reset();
			
			//Sum distance of all the target matches away from the origin
//			float sum_target_dist = 0f;
			//Sum distance of all the decoy matches away from the origin
//			float sum_decoy_dist = 0f;
			
			//The away distance is normalized to 1;
			for(PepNorm pep : peps){
				float away = xcorr*pep.getXcn()*pep.getXcn()+ dcn*pep.getDcn()*pep.getDcn()
							+ sp*pep.getSpn()*pep.getSpn()+rsp*pep.getRspn()*pep.getRspn()
							+dms*pep.getDMS()*pep.getDMS()+ions*pep.getIons()*pep.getIons()
							+mpf*pep.getMPF()*pep.getMPF()+sim*pep.getSim()*pep.getSim();
				pep.away = away;
				
//				if(pep.isRev())
//					sum_decoy_dist += Math.sqrt(away);
//				else
//					sum_target_dist += Math.sqrt(away);
				
				this.precise.put(pep);
			}
			
//			return sum_target_dist - sum_decoy_dist;
			
			return 1d-precise.getEntropy();
		}
	}
	
	/*
	 * Compute the current entropy
	 * @param total total number of peptide
	 * @param totalfalse total number of false positive peptides
	 * @param curti current split point
	 * @param curtfalse current false positive number within the current split point
	 */
	private static double getEntropy(double total, double totalfalse, double curti, double curtfalse){
		double e1 = 0d;
		double percent = curti/total;
		if(percent > 0.000001d){
			double p11 = curtfalse/curti;
			p11 = p11 > 1d? 1d : p11;
			double p12 = 1d-p11;
			
			e1 = percent*(getInfValue(p11)+getInfValue(p12));
		}
		
		double e2 = 0d;
		percent = 1d - percent;
		if(percent>0.000001d){
			double p21 = (totalfalse-curtfalse)/(total-curti);
			p21 = p21 > 1d ? 1d : p21;
			double p22 = 1- p21;
			e2 = percent*(getInfValue(p21)+getInfValue(p22));
		}

//		System.out.println("Total: "+curti+", false: "+curtfalse+" entropy: "+(e1+e2));
		
		return e1 + e2;
	}
	
	private static double getInfValue(double pi){
		if(pi<0.000001d)//the percent 1/1000,000
			return 0;
		return -pi*Logarithm.log2(pi);
	}

}
