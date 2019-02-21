/*
 * *****************************************************************************
 * File: SpectrumUtil.java Created on 02-18-2009
 * 
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * *****************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.util;

import java.util.ArrayList;

import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.PeakForMatch;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;

/**
 * Some useful static methods for spectrum
 * 
 * @author Xinning
 * @version 0.2.1, 06-04-2009, 10:16:26
 */
public class SpectrumUtil {

	private static final double PROTON = AminoAcidProperty.PROTON_W;

	/**
	 * Generate PeakForMatch[] after removal of neutral loss related peaks.
	 * Including neutral loses peak (1, 2... phosphoric acid loss), water loss
	 * after neutral loss
	 * 
	 * @param peaklist raw peaklist.
	 * @param neuSiteNum site may be loss a neutral fragment.
	 * @param lossms the minus of mass when neutral loss occurred.(without
	 *            consider of charge)
	 * @param tolerence within +-tolerance two peaks are considered as match.
	 */
	public static PeakForMatch[] getPeaksNoNeuLossPeak(IMS2PeakList peaklist, int neuSiteNum, double lossms,
			double tolerence) {
		IMS2PeakList peaksnoNL = getPeakListNoNLPeak(peaklist, neuSiteNum, lossms, tolerence);
		int size = peaksnoNL.size();
		double mxmz = peaksnoNL.getBasePeak().getIntensity();
		PeakForMatch[] peaks = new PeakForMatch[size];
		for (int i = 0; i < size; i++) {
			IPeak peak = peaklist.getPeak(i);
			peaks[i] = new PeakForMatch(peak, mxmz);
		}
		return peaks;
	}

	/**
	 * Generate the new peaklist after removal of neutral loss related peaks.
	 * Including neutral loses peak (1, 2... phosphoric acid loss), water loss
	 * after neutral loss
	 * 
	 * @param peaklist raw peaklist.
	 * @param neuSiteNum site may be loss a neutral fragment.
	 * @param lossms the minus of mass when neutral loss occurred.(without
	 *            consider of charge)
	 * @param tolerence within +-tolerance two peaks are considered as match.
	 */
	public static IMS2PeakList getPeakListNoNLPeak(IMS2PeakList peaklist, int neuSiteNum, double lossms,
			double tolerence) {

		int size = peaklist.size();
		int charge = peaklist.getPrecursePeak().getCharge();
		double mz = peaklist.getPrecursePeak().getMz();
		int len = neuSiteNum * 3;
		double[] neus = new double[len];

		for (int i = 0; i < neuSiteNum; i++) {
			int t = (neuSiteNum - i - 1) * 3;
			double n = mz - (i + 1) * lossms / charge;
			neus[t] = n - 18d / charge;// -H2O
			neus[t + 1] = n - 17d / charge;// -NH3
			neus[t + 2] = n;
		}

		ArrayList<Integer> idxlist = new ArrayList<Integer>();
		int n = 0;
		for (int i = 0; i < size; i++) {
			IPeak peak = peaklist.getPeak(i);
			double tmz = peak.getMz();

			if (n < len) {
				double neu = neus[n];
				double sub = tmz - neu;
				if (sub > tolerence) {
					n++;// next ms;
					i--;// rematch this peak with the next ms;
					continue;
				} else if (Math.abs(sub) <= tolerence) {
					idxlist.add(i);
					continue;
				}
			}
		}

		n = 0;
		len = idxlist.size();
		IMS2PeakList rlist = new MS2PeakList(size - len);
		rlist.setPrecursePeak(peaklist.getPrecursePeak());
		for (int i = 0; i < size; i++) {
			IPeak peak = peaklist.getPeak(i);
			if (n < len) {
				int tid = idxlist.get(n).intValue();
				if (tid == i) {
					n++;// next ms;
					continue;
				}
			}
			rlist.add(peak);
		}

		return rlist;
	}

	/**
	 * get the mh from the mz and charge state
	 * 
	 * @since 0.2.1
	 * @param mz
	 * @param charge
	 * @return
	 */
	public static double getMH(double mz, short charge) {
		if (charge > 0)
			return (mz - PROTON) * charge + PROTON;
		else
			return mz;
	}

	/**
	 * Get the mz from the mh and charge state
	 * 
	 * @since 0.2.1
	 * @param mh
	 * @param charge
	 * @return
	 */
	public static double getMZ(double mh, short charge) {
		if (charge > 0)
			return (mh - PROTON) / charge + PROTON;
		else
			return mh;
	}
}
