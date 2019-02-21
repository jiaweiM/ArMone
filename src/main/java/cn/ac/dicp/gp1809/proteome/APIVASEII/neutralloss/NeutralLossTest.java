/* 
 ******************************************************************************
 * File: NeutralLossTest.java * * * Created on 02-25-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.neutralloss;

import java.util.Arrays;
import java.util.Comparator;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.IntensityComparator;

/**
 * Test whether the peaklist contain neutral loss peak.
 * 
 * <p>Changes:
 * <li>0.1.1, 07-05-2009: change the tolerance for the determine of neutral loss
 * 
 * @author Xinning
 * @version 0.1.1, 07-05-2009, 15:20:48
 */
public class NeutralLossTest {

	private static Comparator<IPeak> intenscmp = new IntensityComparator();

	/**
	 * Test whether the peaklist contains significant neutral loss peaks. The
	 * significance of the neutral loss peak can be configured by the Spectrum
	 * threshold. That is the neutral loss peak must be with intensity bigger
	 * than a specific value.
	 * 
	 * @param peaklist
	 * @param charge
	 *            the charge state of the precursor ion
	 * @param threshold
	 * @param lostmass
	 * @return
	 */
	public static NeutralInfo testNeutralLoss(IMS2PeakList peaklist, short charge,
	        ISpectrumThreshold threshold, double lostmass) {

		PrecursePeak parent = peaklist.getPrecursePeak();

		IPeak[] sortpeak = peaklist.getPeakArray();
		Arrays.sort(sortpeak, intenscmp);

		double acceptedIntense = sortpeak[0].getIntensity()
		        * threshold.getInstensityThreshold();

		double neutralIon = parent.getMz() - lostmass / charge;
		double tol = threshold.getMassTolerance();

		/*
		 * If there are more than one peak within the tolerance which can match
		 * the neutral loss peak, only consider the peak with most intensity.
		 */
		for (int i = 0, n = sortpeak.length; i < n; i++) {
			IPeak temp = sortpeak[i];
			//less than the accepted intensity 
			if (temp.getIntensity() < acceptedIntense)
				break;

			double tempmz = temp.getMz();

			if (approximateEqual(neutralIon, tempmz, tol)) {
				return new NeutralInfo(peaklist, lostmass, threshold, true,
				        i + 1, temp);
			}
		}

		return new NeutralInfo(peaklist, lostmass, threshold, false, 0, null);
	}

	/**
	 * The mass difference is within the threshold tolerance. 
	 * 
	 * <p><b>Here, we use the [tol, 1+tol] as the tolerance for the determine of neutral loss
	 * 
	 * @param neutralIon
	 * @param peak
	 * @return
	 */
	private static boolean approximateEqual(double neutralIon, double peak,
	        double tolerance) {
		double gap = Math.abs(neutralIon - peak);
		if (-tolerance <= gap && gap <= tolerance)
			return true;

		return false;
	}

	/**
	 * The Neutral loss informations after the determination of the neutral loss
	 * peak.
	 * 
	 * 
	 * @author Xinning
	 * @version 0.1, 02-25-2009, 16:50:08
	 */
	public static class NeutralInfo {
		private IMS2PeakList peaklist;
		private double lostmass;
		private ISpectrumThreshold threshold;
		private boolean isNeutralLoss;
		private int topn;
		private IPeak neutralPeak;

		/**
		 * @param peaklist
		 * @param lostmass
		 * @param threshold
		 * @param isNeutralLoss
		 * @param topn
		 * @param neutralPeak
		 */
		private NeutralInfo(IMS2PeakList peaklist, double lostmass,
		        ISpectrumThreshold threshold, boolean isNeutralLoss, int topn,
		        IPeak neutralPeak) {
			this.peaklist = peaklist;
			this.lostmass = lostmass;
			this.threshold = threshold;
			this.isNeutralLoss = isNeutralLoss;
			this.topn = topn;
			this.neutralPeak = neutralPeak;
		}

		/**
		 * The original peak list
		 * 
		 * @return the peaklist
		 */
		public IMS2PeakList getPeaklist() {
			return peaklist;
		}

		/**
		 * The lost mass of neutral loss
		 * 
		 * @return the lostmass
		 */
		public double getLostmass() {
			return lostmass;
		}

		/**
		 * The used threshold for neutral loss peak detection
		 * 
		 * @return the threshold
		 */
		public ISpectrumThreshold getThreshold() {
			return threshold;
		}

		/**
		 * If this peak list contains neutral loss peak within the threshold.
		 * 
		 * @return the isNeutralLoss
		 */
		public boolean isNeutralLoss() {
			return isNeutralLoss;
		}

		/**
		 * The neutral loss peak is the top n peak in the peak list
		 * 
		 * @return the topn
		 */
		public int getTopn() {
			return topn;
		}

		/**
		 * If this PeakList contains neutral loss peaks, return this peak.
		 * Otherwise, return null.
		 * 
		 * @return the neutralPeak
		 */
		public IPeak getNeutralPeak() {
			return neutralPeak;
		}

		/**
		 * 
		 * @return The percentage of intensity against the base peak
		 */
		public double getIntensityPercent() {
			return this.neutralPeak == null ? 0 : this.neutralPeak
			        .getIntensity()
			        / this.peaklist.getBasePeak().getIntensity();
		}
	}
}
