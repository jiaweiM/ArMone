/* 
 ******************************************************************************
 * File: MinimumIntensityFilter.java * * * Created on 05-25-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.filters;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;

/**
 * Filter out the peak with intensity less than specified
 * 
 * @author Xinning
 * @version 0.1, 05-25-2009, 20:45:18
 */
public class MinimumIntensityFilter implements ISpectrumFilter {

	private double min_intensity_percent;

	public MinimumIntensityFilter(double min_intensity_percent) {

		if (min_intensity_percent >= 1 || min_intensity_percent < 0) {
			throw new IllegalArgumentException(
			        "The legal intensity percent is [0, 1)");
		}

		this.min_intensity_percent = min_intensity_percent;
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

		double min_intense = peaklist.getBasePeak().getIntensity()
		        * this.min_intensity_percent;

		IMS2PeakList copy = peaklist.newInstance();
		PrecursePeak ppeak = peaklist.getPrecursePeak();
		if (ppeak != null)
			copy.setPrecursePeak(ppeak.deepClone());

		int size = peaklist.size();

		for (int i = 0; i < size; i++) {
			IPeak peak = peaklist.getPeak(i);
			if (peak.getIntensity() >= min_intense)
				copy.add(peak.deepClone());
		}

		return copy;
	}
}
