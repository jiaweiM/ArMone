/* 
 ******************************************************************************
 * File: Optimizer.java * * * Created on 08-07-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.sequest;

import cn.ac.dicp.gp1809.ga.GeneticOperator;
import cn.ac.dicp.gp1809.ga.Population;
import cn.ac.dicp.gp1809.ga.RoulettewheelSelector;
import cn.ac.dicp.gp1809.ga.Selector;

/**
 * Optimizer
 * 
 * @author Xinning
 * @version 0.2.2, 09-14-2010, 20:09:59
 */
public class Optimizer {

	private SequestConfiguration config;
	private float[][] peptides;
	private int curtGeneration;
	private int totalGeneration;

	public Optimizer(SequestConfiguration config, float[][] peptides) {
		this.config = config;
		this.peptides = peptides;
	}

	/**
	 * Use default configuration
	 * 
	 * @param peptides
	 */
	public Optimizer(float[][] peptides) {
		this(getDefaultConfiguration(), peptides);
	}

	/**
	 * Use
	 * 
	 * @param peptides
	 * @param maxFDR
	 * @param use_xc_dcn_only
	 */
	public Optimizer(float[][] peptides, double maxFDR,
	        boolean use_xc_dcn_only, boolean use_deltaMS, short optimizeType) {
		this.config = getDefaultConfiguration();
		this.config.setMaxFPR(maxFDR);
		this.peptides = peptides;

		if (use_xc_dcn_only) {
			this.config.setSpFilter(false);
			this.config.setRspFilter(false);
		}

		this.config.setDeltaMSFilter(use_deltaMS);
		this.config.setOptimizeType(optimizeType);
	}

	/**
	 * Construct the default configuration
	 * 
	 * @return
	 */
	private static SequestConfiguration getDefaultConfiguration() {

		SequestConfiguration config = new SequestConfiguration();

		//use default value limit
		SequestValueLimit vlimit = new SequestValueLimit();

		config.setValueLimit(vlimit);

		Parameter parameter = new Parameter();

		config.setCrossRate(parameter.populationcross);
		config.setMaxFPR(parameter.maxfalseratio);
		config.setMutateRate(parameter.populationmutate);
		config.setPopulationSize(parameter.populationsize);
		config.setGenerationSize(parameter.maxgeneration);
		config.setMax_no_improv_generations(parameter.no_improv_generations);

		config.setXcorrFilter(true);
		config.setXcorrGeneBit(parameter.xcorrlen);
		config.setDeltaCnFilter(true);
		config.setDeltaCnGeneBit(parameter.dcnlen);
		config.setSpFilter(true);
		config.setSpGeneBit(parameter.splen);
		config.setRspFilter(true);
		config.setRspGeneBit(parameter.rsplen);
//		config.setDeltaMSFilter(true);
//		config.setDeltaMSGeneBit(parameter.dmslen);

		return config;
	}

	/**
	 * Start to optimize
	 * 
	 * @return
	 */
	public OptimizedFilter optimize() {

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

		OptimizedFilter of = new OptimizedFilter((float) parameters[0],
		        (float) parameters[1], (float) parameters[2],
		        (short) parameters[3], (float) parameters[4],
		        (float) parameters[5]);

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
	public static class OptimizedFilter {

		private float xcorr;
		private float dcn;
		private float sp;
		private short rsp;
		private float deltaMSppm;
		private float ions;

		/**
		 * @param xcorr
		 * @param dcn
		 * @param sp
		 * @param rsp
		 */
		/*
		 * public OptimizedFilter(float xcorr, float dcn, float sp, short rsp) {
		 * this.xcorr = xcorr; this.dcn = dcn; this.sp = sp; this.rsp = rsp; }
		 */

		/**
		 * @param xcorr
		 * @param dcn
		 * @param sp
		 * @param rsp
		 */
		public OptimizedFilter(float xcorr, float dcn, float sp, short rsp,
		        float deltaMSppm, float ions) {
			this.xcorr = xcorr;
			this.dcn = dcn;
			this.sp = sp;
			this.rsp = rsp;
			this.deltaMSppm = deltaMSppm;
			this.ions = ions;
		}

		/**
		 * @return the xcorr
		 */
		public float getXcorr() {
			return xcorr;
		}

		/**
		 * @param xcorr
		 *            the xcorr to set
		 */
		public void setXcorr(float xcorr) {
			this.xcorr = xcorr;
		}

		/**
		 * @return the dcn
		 */
		public float getDcn() {
			return dcn;
		}

		/**
		 * @param dcn
		 *            the dcn to set
		 */
		public void setDcn(float dcn) {
			this.dcn = dcn;
		}

		/**
		 * @return the sp
		 */
		public float getSp() {
			return sp;
		}

		/**
		 * @param sp
		 *            the sp to set
		 */
		public void setSp(float sp) {
			this.sp = sp;
		}

		/**
		 * @return the rsp
		 */
		public short getRsp() {
			return rsp;
		}

		/**
		 * @param rsp
		 *            the rsp to set
		 */
		public void setRsp(short rsp) {
			this.rsp = rsp;
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
