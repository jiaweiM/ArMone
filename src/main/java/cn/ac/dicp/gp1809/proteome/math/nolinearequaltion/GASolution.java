/* 
 ******************************************************************************
 * File: GASolution.java * * * Created on 01-16-2008
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
package cn.ac.dicp.gp1809.proteome.math.nolinearequaltion;

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


/**
 * Solution for nonlinear function group using genetic algorithm
 * 
 * @author Xinning
 * @version 0.1, 01-16-2008, 20:54:01
 */
public class GASolution implements ISolution {

	//When the precise sloution is hardly accessed, after the max generation reached, the circulation will
	//stop.
	private static final int maxGeneration = 1000;
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.math.nolinearequaltion.ISolution#solute(double[], cn.ac.dicp.gp1809.proteome.math.nolinearequaltion.ISolution.IModeFunction, double)
	 */
	@Override
	public double[] solute(double[] x0, IModeFunction function, double precise) {
		Configuration config = new ConfigurationImp(x0.length,function);
		Population last = Population.GenerateRandomPopulation(config);
		Population next;
		Selector selector = new RoulettewheelSelector(config);
		GeneticOperator operator = new GeneticOperator(config);
		int generation = 1;
		
		while((1d/last.determineBestFitness())>precise){
			next = new Population(config);
			selector.select(last,next);
			operator.operate(next);
			last = next;
			
			if(++generation>maxGeneration){
				break;
			}
		}	
		
		System.out.println("In GASolution: "+generation+" "+1/last.determineBestFitness());
		
		double[] fitest = last.determineFittestChromosome().values();
		return fitest;
	}
	
	
	private class GeneImp extends Gene{
		private static final short l = 20;
		
		/*
		 * Max value of this gene. When decoding gene to actual value, this max bound must be computed;
		 * this value equals 2^genelength;
		 * The observed value of the random sequest gene is ranged from 0-maxbitvalue.
		 */
		private final double maxBitValue;
		
		public GeneImp(final Configuration config){
			super(config);
			this.maxBitValue = Math.pow(2d,this.length());
		}
		
		@Override
		public String encode() {
			String geneString = this.getRandomGenerator().generateBinString(this.length());
			return geneString;
		}

		@Override
		public void setLength() {
			this.setLength(l);
		}

		@Override
		public double value() {
			double obvalue = Integer.parseInt(this.genestring,2);
			double acvalue = this.getActualLowerBound()+
			(getActualUpperBound()-getActualLowerBound())*obvalue/this.getMaxBitValue();
			
			return acvalue;
		}
		
		public final double getMaxBitValue(){
			return this.maxBitValue;
		}
		
		/**
		 * In sequest filtering criteria optimization, not all double values can be a criterion.
		 * To minish search region, both actual upper bound and lower bound are needed;
		 * @return actual upper bound for search;
		 */
		private double getActualUpperBound(){
			return 0d;
		}
		
		/**
		 * In sequest filtering criteria optimization, not all double values can be a criterion.
		 * To minish search region, both actual upper bound and lower bound are needed;
		 * @return actual lower bound for search;
		 */
		protected double getActualLowerBound(){
			return 1d;
		}
	}
	
	private class ConfigurationImp extends Configuration{
		
		private IModeFunction modefunction;
		
		public ConfigurationImp(int geneNum, IModeFunction modefunction){
			this.modefunction = modefunction;
			this.setGeneNumber(geneNum);
		}
		
		@Override
		public FitnessFunction getFitnessFunction() {
			return new FitnessFunctionImp(this.modefunction);
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

		@Override
		public Configuration readFromFile(InputStream instream) {
			return null;
		}

		@Override
		public void write(OutputStream outstream) {
		}
		
	}
	
	private class FitnessFunctionImp extends FitnessFunction{
		private IModeFunction mfunction;
		public FitnessFunctionImp(IModeFunction mfunction){
			this.mfunction = mfunction;
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
			double mode = mfunction.compute(values);
			
			if(mode == 0d)
				mode = 0.0000000000001d;
			
			return 1/mode;
		}
	}

}
