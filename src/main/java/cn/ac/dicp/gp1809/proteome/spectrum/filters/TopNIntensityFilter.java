/* 
 ******************************************************************************
 * File: TopNIntensityFilter.java * * * Created on 05-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.filters;

import java.util.Arrays;
import java.util.Comparator;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;

/**
 * Filter the peak list and only retain the top n peaks in the spectrum
 * 
 * @author Xinning
 * @version 0.1, 05-12-2009, 19:36:57
 */
public class TopNIntensityFilter implements ISpectrumFilter {

	private Comparator<IPeak> comparator = new IntensityComparator();
	
	private Comparator<IPeak> mzcomparator = new MzComparator();

	private int topN;

	public TopNIntensityFilter(int topN) {
		if (topN <= 0) {
			throw new IllegalArgumentException("Top n must bigger than 0");
		}

		this.topN = topN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumFilter#filter(cn
	 * .ac.dicp.gp1809.proteome.spectrum.PeakList)
	 */
	@Override
	public IMS2PeakList filter(IMS2PeakList peaklist) {

		IPeak[] peaks = peaklist.getPeakArray();
		Arrays.sort(peaks, this.comparator);

		IMS2PeakList rlist = peaklist.newInstance();
		rlist.setPrecursePeak(peaklist.getPrecursePeak());

		int size = peaklist.size();
		size = size >= this.topN ? this.topN : size;
		IPeak[] topPeaks = new IPeak[size];
		for(int i=0; i< size; i++) {
			topPeaks[i] = peaks[i].deepClone();
		}
		
		Arrays.sort(topPeaks, mzcomparator);

		rlist.setPeakList(peaks);
		return rlist;
	}

}
