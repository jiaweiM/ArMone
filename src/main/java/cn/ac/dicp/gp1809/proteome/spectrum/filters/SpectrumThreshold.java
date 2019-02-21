/*
 ****************************************************************************** File: SpectrumThreshold.java * * * Created on 02-24-2009 Copyright (c) 2009
 * Xinning Jiang vext@163.com All right reserved. Use is subject to license
 * terms.
 */
package cn.ac.dicp.gp1809.proteome.spectrum.filters;

/**
 * The instance of threshold.
 * 
 * @author Xinning
 * @version 0.1.1, 03-18-2009, 16:51:03
 */
public class SpectrumThreshold implements ISpectrumThreshold {

	/**
	 * Threshold with mass tolerance of 1d and intensity threshold of 0.5
	 */
	public final static ISpectrumThreshold HALF_INTENSE_THRESHOLD = new SpectrumThreshold(1, 0.5);

	/**
	 * Threshold with mass tolerance of 1d and intensity threshold of 0.3
	 */
	public final static ISpectrumThreshold PERCENT_30_INTENSE_THRESHOLD = new SpectrumThreshold(1, 0.3);

	/**
	 * Threshold with mass tolerance of 1d and intensity threshold of 0.1
	 */
	public final static ISpectrumThreshold PERCENT_10_INTENSE_THRESHOLD = new SpectrumThreshold(1, 0.1);

	/**
	 * Threshold with mass tolerance of 1d and intensity threshold of 0.01.
	 */
	public final static ISpectrumThreshold PERCENT_1_INTENSE_THRESHOLD = new SpectrumThreshold(1, 0.01);

	/**
	 * Threshold with mass tolerance of 1d and intensity threshold of 0.
	 */
	public final static ISpectrumThreshold ZERO_INTENSE_THRESHOLD = new SpectrumThreshold(1, 0);

	private double massToler, instenThres;

	/**
	 * Constructor.
	 * @param massToler (> 0)
	 * @param instenThres [0 - 1]
	 */
	public SpectrumThreshold(double massToler, double instenThres) {
		this.massToler = massToler;
		this.instenThres = instenThres;

		this.validate(massToler, instenThres);
	}

	/**
	 * Validate the threshold
	 * 
	 * @param massToler mass tolerance
	 * @param instenThres intensity threshold.
	 */
	private void validate(double massToler, double instenThres) {
		if (massToler <= 0)
			throw new IllegalArgumentException(
					"The mass tolerance is illegal, should be bigger than 0, current: " + massToler);

		if (instenThres > 1 || instenThres < 0)
			throw new IllegalArgumentException(
					"The instensity threshold is illegal, should within 0 - 1, current: " + instenThres);
	}

	@Override
	public double getInstensityThreshold() {
		return this.instenThres;
	}

	@Override
	public double getMassTolerance() {
		return this.massToler;
	}

	@Override
	public String toString() {
		return "Mass toleracne: " + this.massToler + "; Min intensity: " + this.instenThres;
	}
}
