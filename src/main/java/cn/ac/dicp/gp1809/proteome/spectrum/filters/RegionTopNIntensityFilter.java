/*
 * *****************************************************************************
 * File: IntensityFilter.java * * * Created on 08-04-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;

/**
 * A peak list filter to retain "topn" peaks of intensity within a specific m/z
 * window. For example, only retain top 4 peaks within 100 amu.
 * 
 * @author Xinning
 * @version 0.2, 05-12-2009, 19:35:53
 */
public class RegionTopNIntensityFilter implements ISpectrumFilter {

	/**
	 * Within this tolerance, the peaks will be considered as the isotope mass
	 * and will not be selected twice.
	 */
	private static final double ISOTOPE_TOL = 1.2;

	private int topn = 0;
	private int window = 100;

	private Comparator<IPeak> comparator = new IntensityComparator();

	private Comparator<IPeak> mzcomparator = new MzComparator();

	/**
	 * Within a specific m/z window, how many peaks are retained
	 * 
	 * @param topn
	 * @param mzRegion
	 */
	public RegionTopNIntensityFilter(int topn, int mzRegion) {
		this.topn = topn;
		this.window = mzRegion;

		if (this.topn <= 0) {
			throw new IllegalArgumentException(
			        "The retained n top peaks must bigger than 0");
		}

		if (this.window <= this.topn) {
			throw new IllegalArgumentException(
			        "The selected top peaks must less than the total mz region.");
		}
	}

	/**
	 * The probability for a single peak match in the prediction that they are
	 * randomly matches. Equals topn/100.
	 * 
	 * @return topn/100;
	 */
	public double singleP() {
		return (double) topn / this.window;
	}

	/**
	 * @return the topn peaks to be retained per 100 amu window.
	 */
	public int getTopn() {
		return this.topn;
	}

	@Override
	public IMS2PeakList filter(IMS2PeakList peaklist) {
		
		IPeak[] peaks = peaklist.getPeakArray();
		Arrays.sort(peaks);
		IMS2PeakList copy = peaklist.newInstance();
		copy.setPrecursePeak(peaklist.getPrecursePeak());

		int len = peaks.length;
		/*
		 * Start from the first peak
		 */
		int lowlimit = (int) peaks[0].getMz();

		List<IPeak> finallist = new ArrayList<IPeak>();

		int p = 0;

		for (int i = lowlimit + window; p < len; i += window) {
			int count = p;
			for (; p < len; p++) {
				IPeak peak = peaks[p];
				double mz = peak.getMz();

				if (mz >= i)
					break;
			}

			count = p - count;

			IPeak[] inpeaks = new IPeak[count];
			System.arraycopy(peaks, p - count, inpeaks, 0, count);

			IPeak[] filteredpeaks = null;

			if (count <= this.topn) {// all peeks accepted;
				filteredpeaks = new IPeak[count];
				for (int j = 0; j < count; j++)
					filteredpeaks[j] = inpeaks[j].deepClone();
			} else {

				Arrays.sort(inpeaks, comparator);

				filteredpeaks = new IPeak[this.topn];

				int c = 0;
				L1: for (int j = 0; j < count; j++) {
					IPeak pk = inpeaks[j];
					double mz = pk.getMz();

					for (int m = 0; m < c; m++) {
						IPeak selected = filteredpeaks[m];

						if (Math.abs(mz - selected.getMz()) < ISOTOPE_TOL) {
							continue L1;
						}
					}

					filteredpeaks[c++] = pk.deepClone();

					if (c >= this.topn) {
						break;
					}
				}

				/*
				 * Less than top n peaks, trim the null elements
				 */
				if (c < this.topn) {
					filteredpeaks = Arrays.copyOf(filteredpeaks, c);
				}

				Arrays.sort(filteredpeaks, mzcomparator);
			}

			for (int j = 0; j < filteredpeaks.length; j++) {
				finallist.add(filteredpeaks[j]);
			}
			
		}

		int size = finallist.size();

		for (int i = 0; i < size; i++) {
			copy.add(finallist.get(i));
		}
		return copy;
	}
	
	public IPeak [] filter(IPeak [] peaks) {
		
		ArrayList <IPeak> list = new ArrayList <IPeak>();
		Arrays.sort(peaks);
		int len = peaks.length;

		/*
		 * Start from the first peak
		 */
		int lowlimit = (int) peaks[0].getMz();

		List<IPeak> finallist = new ArrayList<IPeak>();

		int p = 0;

		for (int i = lowlimit + window; p < len; i += window) {
			int count = p;
			for (; p < len; p++) {
				IPeak peak = peaks[p];
				double mz = peak.getMz();

				if (mz >= i)
					break;
			}

			count = p - count;

			IPeak[] inpeaks = new IPeak[count];
			System.arraycopy(peaks, p - count, inpeaks, 0, count);

			IPeak[] filteredpeaks = null;

			if (count <= this.topn) {// all peeks accepted;
				filteredpeaks = new IPeak[count];
				for (int j = 0; j < count; j++)
					filteredpeaks[j] = inpeaks[j].deepClone();
			} else {

				Arrays.sort(inpeaks, comparator);

				filteredpeaks = new IPeak[this.topn];

				int c = 0;
				L1: for (int j = 0; j < count; j++) {
					IPeak pk = inpeaks[j];
					double mz = pk.getMz();

					for (int m = 0; m < c; m++) {
						IPeak selected = filteredpeaks[m];

						if (Math.abs(mz - selected.getMz()) < ISOTOPE_TOL) {
							continue L1;
						}
					}

					filteredpeaks[c++] = pk.deepClone();

					if (c >= this.topn) {
						break;
					}
				}

				/*
				 * Less than top n peaks, trim the null elements
				 */
				if (c < this.topn) {
					filteredpeaks = Arrays.copyOf(filteredpeaks, c);
				}

				Arrays.sort(filteredpeaks, mzcomparator);
			}

			for (int j = 0; j < filteredpeaks.length; j++) {
				finallist.add(filteredpeaks[j]);
			}
			
		}

		int size = finallist.size();

		for (int i = 0; i < size; i++) {
			list.add(finallist.get(i));
		}
		
		return list.toArray(new IPeak[list.size()]);
	}
	
}
