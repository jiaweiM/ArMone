/*
 * *****************************************************************************
 * File: Population.java * * * Created on 01-16-2008 
 * Copyright (c) 2008 Xinning Jiang vext@163.com 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.ga;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is a group of individuals that is in the evolution. 
 * 
 * @author Xinning
 * @version 0.2.1, 08-11-2009, 15:38:38
 */
public class Population {

	  /**
	   * The array of Chromosomes that makeup the  population.
	   */
	  private List<Chromosome> m_chromosomes;

	  /**
	   * The fittest Chromosome of the population.
	   */
	  private Chromosome m_fittestChromosome;
	  
	  /**
	   * The array of fitnesses in this population
	   */
	  private double[] fitnessArray;
	  
	  /**
	   * The best fitness in this population .
	   */
	  private double bestFitness = -1d;

	  /**
	   * Indicates whether at least one of the chromosomes has been changed
	   * (deleted, added, modified).
	   */
	  private boolean m_changed;


	  private Configuration m_config;
	  
	  
	  public Population(final Configuration a_config){
		  this(a_config,a_config.getPopulationSize());
	  }
	  
	  public Population(final Configuration a_config,final Chromosome[] a_chromosomes){
	    this(a_config, a_chromosomes.length);
	    synchronized(m_chromosomes) {
	      for (int i = 0; i < a_chromosomes.length; i++) {
	        m_chromosomes.add(a_chromosomes[i]);
	      }
	    }
	    setChanged(true);
	  }
	  
	  public Population(final Configuration a_config, int a_size){
			if (a_config == null) {
				throw new RuntimeException("Configuration must not be null!");
			}
			
			m_config = a_config;
			m_chromosomes = new ArrayList<Chromosome>(a_size);
			
			setChanged(true);
		  }
	  
	  public static Population GenerateRandomPopulation(Configuration a_config){
		  Population population= new Population(a_config);
		  Chromosome sample = a_config.getSampleChromosome();
		  
		  int size = a_config.getPopulationSize();
		  
		  for(int i=0;i<size;i++){
			  Chromosome chromosome = sample.newChromosome();
			  population.addChromosome(chromosome);
		  }
		  return population;
	  }

	  public Configuration getConfiguration() {
	    return m_config;
	  }

	  /**
	   * Adds a Chromosome to this Population. Does nothing when given null.
	   *
	   * @param a_toAdd the Chromosome to add
	   */
	  public void addChromosome(final Chromosome a_toAdd) {
	    if (a_toAdd != null) {
	      synchronized(m_chromosomes) {
	        m_chromosomes.add(a_toAdd);
	      }
	      setChanged(true);
	    }
	  }

	  /**
	   * Adds all the Chromosomes in the given Population.
	   * Does nothing on null or an empty Population.
	   *
	   * @param a_population the Population to add
	   */
	  public void addChromosomes(final Population a_population) {
	    if (a_population != null) {
	      synchronized(m_chromosomes) {
	        m_chromosomes.addAll(a_population.getChromosomes());
	      }
	      
	      setChanged(true);
	    }
	  }

	  /**
	   * Replaces all chromosomes in the population with the give list of
	   * chromosomes.
	   * @param a_chromosomes the chromosomes to make the population up from
	   */
	  public void setChromosomes(final List<Chromosome> a_chromosomes) {
	    synchronized(m_chromosomes) {
	      m_chromosomes = a_chromosomes;
	    }
	    setChanged(true);
	  }

	  /**
	   * Sets in the given Chromosome on the given index in the list of chromosomes.
	   * If the given index is exceeding the list by one, the chromosome is
	   * appended.
	   *
	   * @param a_index the index to set the Chromosome in
	   * @param a_chromosome the Chromosome to be set
	   */
	  public void setChromosome(final int a_index, final Chromosome a_chromosome) {
	    if (m_chromosomes.size() == a_index) {
	      addChromosome(a_chromosome);
	    }
	    else {
	      synchronized(m_chromosomes) {
	        m_chromosomes.set(a_index, a_chromosome);
	      }
	      setChanged(true);
	    }
	  }

	  /**
	   * @return the list of Chromosome's in the Population. Don't modify the
	   * retrieved list by using clear(), remove(int) etc. If you do so, you need to
	   * call setChanged(true)
	   */
	  public List<Chromosome> getChromosomes() {
	    return m_chromosomes;
	  }

	  /**
	   * Returns a Chromosome at given index in the Population.
	   * @param a_index the index of the Chromosome to be returned
	   * @return Chromosome at given index in the Population
	   */
	  public Chromosome getChromosome(final int a_index) {
	    return m_chromosomes.get(a_index);
	  }

	  /**
	   * @return number of Chromosome's in the Population
	   */
	  public int size() {
	    return m_chromosomes.size();
	  }

	  /**
	   * @return Iterator for the Chromosome list in the Population. Please be aware
	   * that using remove() forces you to call setChanged(true)
	   */
	  public Iterator<Chromosome> iterator() {
	    return m_chromosomes.iterator();
	  }

	  /**
	   * @return the Population converted into a list of Chromosome's
	   */
	  public Chromosome[] toChromosomes() {
	    return m_chromosomes.toArray(new Chromosome[0]);
	  }
	  
		/**
		 * Clear the current population. (Set the chromosomes as empty).
		 * 
		 * @since 0.2
		 */
		public void clearPopulation(){
			this.m_chromosomes = new ArrayList<Chromosome>();
			this.setChanged(true);
		}

	  /**
	   * Determines the fittest Chromosome in the population (the one with the
	   * highest fitness value) and memorizes it. This is an optimized version
	   * compared to calling determineFittesChromosomes(1).
	   * And after this action, the fitness array for this population will be refilled;
	   * 
	   * @return the fittest Chromosome of the population
	   */
	  public Chromosome determineFittestChromosome() {
	    if (!m_changed && m_fittestChromosome != null) {
	      return m_fittestChromosome;
	    }
	    
	    this.iterateFitness();
	    
	    return m_fittestChromosome;
	  }
	  
	  /**
	   * Determines the fitness values for all chromosome in this population;
	   * 
	   * @return the fitness value array;
	   */
	  public double[] getFitnessArray(){
		  if (!m_changed && this.fitnessArray != null){
			  return this.fitnessArray;
		  }
		  
		  this.iterateFitness();
		  return this.fitnessArray;
	  }
	  
	  /**
	   * Determine the best fitness of individuals in this population.
	   * 
	   * @return the best fitness
	   */
	  public double determineBestFitness(){
		  if (!m_changed && this.bestFitness!= -1d){
			  return this.bestFitness;
		  }
		  
		  this.iterateFitness();
		  return this.bestFitness;
	  }
	  
	  /*
	   * If this population has something changed or has not evaluate for fitness
	   * this method excuted;
	   */
	  private void iterateFitness(){
		  int len = this.size();
		  double[] fitnessArray = new double[len];
		  double bestFitness = -1.0d;
		  FitnessEvaluator evaluator = getConfiguration().getFitnessEvaluator();
		  double fitness;
		  List<Chromosome> temp = this.m_chromosomes;
		  
		  Chromosome best=null;
		  for(int i=0;i<len;i++) {
			  Chromosome chrom = temp.get(i);
		      fitness = chrom.getFitnessValue();
		      fitnessArray[i] = fitness;
		      if (evaluator.isFitter(fitness, bestFitness)) {
		        best = chrom;
		        bestFitness = fitness;
		      }
		  }
		  
		  m_fittestChromosome = best;
		  this.fitnessArray = fitnessArray;
		  this.bestFitness = bestFitness;
		  
		  setChanged(false);
	  }
	  
	  /**
	   * Determines the fittest Chromosome in the population (the one with the
	   * highest fitness value) within the given indices and memorizes it. This is
	   * an optimized version compared to calling determineFittesChromosomes(1).
	   * @param a_startIndex index to begin the evaluation with
	   * @param a_endIndex index to end the evaluation with
	   * @return the fittest Chromosome of the population within the given indices
	   */
	  public Chromosome determineFittestChromosome(int a_startIndex, int a_endIndex) {
	    double bestFitness = -1.0d;
	    FitnessEvaluator evaluator = getConfiguration().getFitnessEvaluator();
	    double fitness;
	    int startIndex = Math.max(0, a_startIndex);
	    int endIndex = Math.min(m_chromosomes.size() - 1, a_endIndex);
	    for (int i = startIndex; i < endIndex; i++) {
	      Chromosome chrom = m_chromosomes.get(i);
	      fitness = chrom.getFitnessValue();
	      if (evaluator.isFitter(fitness, bestFitness)|| m_fittestChromosome == null) {
	        m_fittestChromosome = chrom;
	        bestFitness = fitness;
	      }
	    }
	    return m_fittestChromosome;
	  }

	  /**
	   * Mark that for the population the fittest chromosome may have changed.
	   *
	   * @param a_changed true: population's fittest chromosome may have changed,
	   * false: fittest chromosome evaluated earlier is still valid
	   */
	  protected void setChanged(final boolean a_changed) {
	    m_changed = a_changed;
	  }

	  /**
	   * @return true: population's chromosomes (maybe) were changed,
	   * false: not changed for sure
	   */
	  public boolean isChanged() {
	    return m_changed;
	  }

	  /**
	   * Determines whether the given chromosome is contained within the population.
	   * @param a_chromosome the chromosome to check
	   * @return true: chromosome contained within population
	   */
	  public boolean contains(final Chromosome a_chromosome) {
	    return m_chromosomes.contains(a_chromosome);
	  }

	  /**
	   * Removes a chromosome in the list at the given index. Method has package
	   * visibility to signal that this is a method not to be used outside the
	   * JGAP kernel under normal circumstances.
	   *
	   * @param a_index index of chromosome to be removed in list
	   * @return removed Chromosome
	   */
	  public Chromosome removeChromosome(final int a_index) {
	    if (a_index < 0 || a_index >= size()) {
	      throw new IllegalArgumentException("Index must be within bounds!");
	    }
	    setChanged(true);
	    return m_chromosomes.remove(a_index);
	  }
	  
	  /**
	   * Mutate
	   *
	   */
	  public void mutate(){
		  int len = this.size();
		  for(int i =0;i<len;i++){
			  Chromosome chromosome = this.m_chromosomes.get(i);
			  chromosome.mutate();
		  }
	  }
}
