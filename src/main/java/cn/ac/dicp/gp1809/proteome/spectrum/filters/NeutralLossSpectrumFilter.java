/* 
 ******************************************************************************
 * File: NeutralLossSpectrumFilter.java * * * Created on 05-24-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.filters;

import java.util.Arrays;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.PeakForMatch;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;

/**
 * 
 * The spectrum filter of neutral loss, remove the specified neutral loss from
 * the spectrum peak lists and normalize
 * 
 * @author Xinning
 * @version 0.1, 05-24-2009, 14:58:39
 */
public class NeutralLossSpectrumFilter implements ISpectrumFilter {

	private ISpectrumThreshold threshold = SpectrumThreshold.ZERO_INTENSE_THRESHOLD;
	private double tol;

	private double[] losses;
	private int size;

	public NeutralLossSpectrumFilter(NeutralLossInfo[] infos,
	        ISpectrumThreshold threshold) {

		if (infos == null || infos.length == 0)
			throw new NullPointerException("The neutral loss info is null");

		size = infos.length;
		this.losses = new double[size];

		for (int i = 0; i < size; i++) {
			this.losses[i] = infos[i].getLoss();
		}

		Arrays.sort(this.losses);

		if (threshold != null)
			this.threshold = threshold;

		this.tol = this.threshold.getMassTolerance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumFilter#filter(cn
	 * .ac.dicp.gp1809.proteome.spectrum.IPeakList)
	 */
	@Override
	public IMS2PeakList filter(IMS2PeakList peaklist) {

		PrecursePeak ppeak = peaklist.getPrecursePeak();

		if (ppeak == null)
			throw new NullPointerException(
			        "To filter out the neutral loss peaks, the precursor peak must not be null.");

		double pmz = ppeak.getMz();
		double charge = ppeak.getCharge();

		if (charge == 0) {
			throw new NullPointerException("The charge state is unknown.");
		}

		double[] lossmz = new double[size];

		for (int i = 0; i < size; i++) {
			lossmz[i] = pmz - this.losses[i] / charge;
		}

		IMS2PeakList copy = peaklist.newInstance();
		copy.setPrecursePeak(ppeak);
		
		int num = peaklist.size();

		int idx = 0;
		double loss = lossmz[idx];
		double premz = -1d;
		
		
		
		int i=0;
		for (; i < num; i++) {
			IPeak peak = peaklist.getPeak(i);
			double mz = peak.getMz();

			if (premz > mz)
				throw new IllegalArgumentException(
				        "The peak list must be sorted by mz before the neutral loss filtering");

			int v = PeakForMatch.toleranceMatch(mz, loss, this.tol);

			if (v != 0) {
				if (v > 0) {
					/*
					 * Recompare the current peak
					 */
					i--;
					if (++idx < size) {
						loss = lossmz[idx];
					}
					else {
						break;
					}
					

				}
				else {
					copy.add(peak.deepClone());
				}
			}
		}
		
		for(; i<num; i++) {
			copy.add(peaklist.getPeak(i).deepClone());
		}

		return copy;
	}

}
