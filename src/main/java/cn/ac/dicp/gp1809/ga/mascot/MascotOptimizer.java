/* 
 ******************************************************************************
 * File: MascotOptimizer.java * * * Created on 2011-8-31
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.mascot;

import cn.ac.dicp.gp1809.ga.GeneticOperator;
import cn.ac.dicp.gp1809.ga.Population;
import cn.ac.dicp.gp1809.ga.RoulettewheelSelector;
import cn.ac.dicp.gp1809.ga.Selector;

/**
 * @author ck
 *
 * @version 2011-8-31, 14:44:59
 */
public class MascotOptimizer {

	private MascotConfiguration config;
	private float[][] peptides;
	private int curtGeneration;
	private int totalGeneration;

	public MascotOptimizer(MascotConfiguration config, float[][] peptides, double maxFDR, short optimizeType) {
		this.config = config;
		this.config.setMaxFPR(maxFDR);
		this.config.setOptimizeType(optimizeType);
		this.peptides = peptides;
	}

	/**
	 * Use default configuration
	 * 
	 * @param peptides
	 */
	public MascotOptimizer(float[][] peptides, double maxFDR, short optimizeType) {
		this(getDefaultConfiguration(), peptides, maxFDR, optimizeType);
	}

	/**
	 * Construct the default configuration
	 * 
	 * @return
	 */
	private static MascotConfiguration getDefaultConfiguration() {

		MascotConfiguration config = new MascotConfiguration();

		//use default value limit
		MascotValueLimit vlimit = new MascotValueLimit();

		config.setMascotValueLimit(vlimit);

		MascotParameter parameter = new MascotParameter();

		config.setCrossRate(parameter.populationcross);
		config.setMaxFPR(parameter.maxfalseratio);
		config.setMutateRate(parameter.populationmutate);
		config.setPopulationSize(parameter.populationsize);
		config.setGenerationSize(parameter.maxgeneration);
		config.setMax_no_improv_generations(parameter.no_improv_generations);

		config.setIonScoreFilter(true);
		config.setIonScoreGeneBit(parameter.ionScorelen);
		config.setDeltaISFilter(false);
		config.setDeltaISGeneBit(parameter.deltaISlen);
		config.setMhtFilter(true);
		config.setMhtGeneBit(parameter.mhtlen);
		config.setMitFilter(true);
		config.setMitGeneBit(parameter.mitlen);
		config.setEvalueFilter(true);
		config.setEvalueGeneBit(parameter.evaluelen);

		return config;
	}

	/**
	 * Start to optimize
	 * 
	 * @return
	 */
	public MascotOptimizedFilter optimize() {

		if (this.peptides == null || this.peptides.length == 0) {
			System.out.println("No peptide input.");
			return null;
		}

		this.curtGeneration = 0;
		this.totalGeneration = config.getGenerationSize();

		config.setPeptides(peptides);

		Population last = Population.GenerateRandomPopulation(config);
		Population next;
		Selector selector = new RoulettewheelSelector(config);
		GeneticOperator operator = new GeneticOperator(config);

		int len = config.getGenerationSize();
		int max_no_improv = config.getMax_no_improv_generations();

		double bestfit = 0;
		int cont_best = 0;

		for (; curtGeneration <= len; curtGeneration++) {
			/*
			 * Max loop of the generations without improvemnt of fitness
			 */
			double fit = last.determineBestFitness();
			if (fit > bestfit) {
				bestfit = fit;
				cont_best = 0;
			} else {
				cont_best++;

				if (cont_best >= max_no_improv)
					break;
			}

			next = new Population(config);
			selector.select(last, next);
			operator.operate(next);
			last = next;
		}

		double[] parameters = last.determineFittestChromosome().values();

		MascotOptimizedFilter of = new MascotOptimizedFilter((float) parameters[0],
		        (float) parameters[1], (float) parameters[2],
		        (short) parameters[3], (double) parameters[4],
		        (float) parameters[5], (float) parameters[6]);

		System.out.println("Generation idx: " + this.curtGeneration
		        + "; Fitness: " + bestfit);

		return of;
	}

	/**
	 * Current generation
	 * 
	 * @return
	 */
	public int getCurtGeneration() {
		return this.curtGeneration;
	}

	/**
	 * Total generation
	 * 
	 * @return
	 */
	public int getTotalGeneration() {
		return this.totalGeneration;
	}

	/**
	 * The optimized filter
	 * 
	 * @author Xinning
	 * @version 0.2, 08-04-2010, 10:35:28
	 */
	public static class MascotOptimizedFilter {

		private float ionScore;
		private float deltaIS;
		private float mht;
		private float mit;
		private double evalue;
		private float deltaMSppm;
		private float ions;

		/**
		 * @param ionScore
		 * @param deltaIS
		 * @param sp
		 * @param mit
		 */
		/*
		 * public OptimizedFilter(float ionScore, float deltaIS, float sp, short mit) {
		 * this.ionScore = ionScore; this.deltaIS = deltaIS; this.sp = sp; this.mit = mit; }
		 */

		/**
		 * @param ionScore
		 * @param deltaIS
		 * @param sp
		 * @param mit
		 */
		public MascotOptimizedFilter(float ionScore, float deltaIS, float mht, float mit,
		        double evalue, float deltaMSppm, float ions) {
			this.ionScore = ionScore;
			this.deltaIS = deltaIS;
			this.mht = mht;
			this.mit = mit;
			this.evalue = evalue;
			this.deltaMSppm = deltaMSppm;
			this.ions = ions;
		}

		/**
		 * @return the ionScore
		 */
		public float getIonScore() {
			return ionScore;
		}

		/**
		 * @param ionScore
		 *            the ionScore to set
		 */
		public void setIonScore(float ionScore) {
			this.ionScore = ionScore;
		}

		/**
		 * @return the deltaIS
		 */
		public float getDeltaIS() {
			return deltaIS;
		}

		/**
		 * @param deltaIS
		 *            the deltaIS to set
		 */
		public void setDeltaIS(float deltaIS) {
			this.deltaIS = deltaIS;
		}

		/**
		 * @return the sp
		 */
		public float getMht() {
			return mht;
		}

		/**
		 * @param sp
		 *            the sp to set
		 */
		public void setSp(float mht) {
			this.mht = mht;
		}

		/**
		 * @return the mit
		 */
		public float getMit() {
			return mit;
		}

		/**
		 * @param mit
		 *            the mit to set
		 */
		public void setMit(float mit) {
			this.mit = mit;
		}

		public double getEvalue(){
			return this.evalue;
		}
		
		public void setEvalue(double evalue){
			this.evalue = evalue;
		}
		
		/**
		 * @return the deltaMSppm
		 */
		public float getDeltaMSppm() {
			return this.deltaMSppm;
		}

		/**
		 * @param deltaMSppm
		 *            the deltaMSppm to set
		 */
		public void setDeltaMSppm(float deltams) {
			this.deltaMSppm = deltams;
		}

		/**
		 * @return the ion percent
		 */
		public float getIons() {
			return this.ions;
		}

		/**
		 * @param ions
		 *            (percent) the ion percent to set
		 */
		public void setIons(float ions) {
			this.ions = ions;
		}
	}


}
