/*
 ******************************************************************************
 * File: Configuration.java * * * Created on 03-01-2010
 *
 * Copyright (c) 2010 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Configuration for Genetic algorithm (GA)
 * 
 * @author Xinning
 * @version 0.1, 03-01-2010, 12:36:50
 */
public abstract class Configuration {
	
	private float mutate_rate = 0.1f;
	
	private float cross_rate = 0.3f;
	
	private int populationsize = 50;
	
	private int generationsize = 500;
	
	private int max_no_improv_generations = 50;
	
	private int geneNum;
	
	private FitnessEvaluator evaluator;
	
	private RandomGenerator rand;
	
	/**
	 * Evaluator for comparation of fitness;
	 * 
	 * @return fitness evaluator
	 */
	public FitnessEvaluator getFitnessEvaluator(){
		if(this.evaluator==null){
			this.evaluator = new DefaultFitnessEvaluator();
		}
		
		return this.evaluator;
	}
	
	
	public abstract FitnessFunction getFitnessFunction();
	
	/**
	 * Every time when this method is used, a new instence is formed;
	 * 
	 * @return a new instence of random generator
	 */
	public RandomGenerator getRandomGenerator(){
		if(this.rand==null)
			this.rand = new RandomGenerator(System.currentTimeMillis());
		return this.rand;
	}
	
	/**
	 * Get sample chromosome. This chromosome was used as a sample chromosome.
	 * All other chromosomes are generated using this chromosome as seeds.
	 * 
	 * @return chromosome as sample seed
	 */
	public abstract Chromosome getSampleChromosome();
	
	/**
	 * How many genes in chromosome;
	 * 
	 * @return number of genes to form a chromosome;
	 */
	public int getGeneNumber(){
		return this.geneNum;
	}
	
	public void setGeneNumber(int geneNum){
		this.geneNum = geneNum;
	}
	
	/**
	 * Read configuration from the stream;
	 * 
	 * @param instream
	 * @return
	 */
	public abstract Configuration readFromFile(InputStream instream);
	
	
	
	/**
	 * Write the param to a file;
	 * @param outstream
	 */
	public abstract void write(OutputStream outstream);
	
	/**
	 * The probability for mutation of an individual at a single point;
	 * 
	 * @return cross over rate
	 */
	public float getMutateRate(){
		return this.mutate_rate;
	}
	
	public void setMutateRate(float a_mutate_rate){
		this.mutate_rate = a_mutate_rate;
	}
	
	/**
	 * The probability for two individual cross together;
	 * 
	 * @return cross over rate
	 */
	public float getCrossRate(){
		return this.cross_rate;
	}
	
	public void setCrossRate(float a_cross_rate){
		this.cross_rate = a_cross_rate;
	}
	
	/**
	 * Size of population; number of individuls in the population;
	 * 
	 * @return population size
	 */
	public int getPopulationSize(){
		return this.populationsize;
	}
	
	public void setPopulationSize(int population_size){
		this.populationsize = population_size;
	}
	
	/**
	 * How many circulation for ga;
	 * 循环次数；
	 * @return circulation number
	 */
	public int getGenerationSize(){
		return this.generationsize;
	}
	
	public void setGenerationSize(int generation_size){
		this.generationsize = generation_size;
	}


	/**
	 * The maximum number of generations without improvement of fitness
	 * 
     * @return the max_no_improv_generations
     */
    public int getMax_no_improv_generations() {
    	return max_no_improv_generations;
    }


	/**
	 * The maximum number of generations without improvement of fitness
	 * 
     * @param maxNoImprovGenerations the max_no_improv_generations to set
     */
    public void setMax_no_improv_generations(int maxNoImprovGenerations) {
    	max_no_improv_generations = maxNoImprovGenerations;
    }

}
