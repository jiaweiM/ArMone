/*
 * ********************************************************************************
 * File: PeakForMatch.java Created on 02-18-2009
 * 
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * ********************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * The peak which contains some convenient methods for match with ions
 * 
 * @author Xinning
 * @version 0.3, 05-13-2009, 10:06:14
 */
public class PeakForMatch extends Peak {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	/**
	 * The max charge used for the match
	 */
	private static final int MAX_CHARGE = 4;

	/*
	 * Map contain all the match information.
	 */
//	private HashMap<Integer, String> map;

	/**
	 * The type of matches and matched ions versus chrage
	 */
	private HashMap<Integer, Ion[]> ionMap;

	private double maxInten;
	
	private String label;
	
	private static double distance = -1;
	
	/**
	 * In order to facilite the match, all peaks are normalized by the max
	 * intensity in this spectrum. That is , the max intensity is 1d;
	 * 
	 * @param peak
	 * @param maxintense
	 *            the max intensity in the spectrum list
	 */
	public PeakForMatch(IPeak peak, double maxintense) {
		this(peak.getMz(), peak.getIntensity() / maxintense);
		this.maxInten = maxintense;
	}

	/**
	 * In order to facilite the match, all peaks are normalized by the max
	 * intensity in this spectrum. That is , the max intensity is 1d;
	 * 
	 * @param mz
	 * @param intens
	 *            the intensity of the peak. this value must be normalized to 1
	 *            by the max intensity in the spectrum
	 */
	public PeakForMatch(double mz, double intens) {
		super(mz, intens);
	}

	/**
	 * The intensity in the constructor is relative intensity, using this method you 
	 * can get the absolute intensity.
	 * @return
	 */
	public double getAbIntensity(){
		return getIntensity()*maxInten;
	}
	
	/**
	 * Try to match to the specific ion. If the ion match to this peak, return
	 * true and the match information will be set for this peak.
	 * 
	 * <p>
	 * </b>Some detail instruction need to be followed<b>
	 * 
	 * @param type
	 * @param ion
	 * @return
	 */
	public boolean match(int type, Ion ion, short charge,
	        ISpectrumThreshold threshold) {
		if (isMatch2(this, ion.getMzVsCharge(charge), threshold)) {

			if (this.ionMap == null) {
				this.ionMap = new HashMap<Integer, Ion[]>();
			}

			Ion[] ions = this.ionMap.get(type);
			if (ions == null) {
				ions = new Ion[MAX_CHARGE];
				this.ionMap.put(type, ions);
			}

			/*
			 * Limit the max charge state
			 */
			int idx = charge;
			if (idx > MAX_CHARGE) {
				idx = MAX_CHARGE;
			}

			idx--;
			/*
			 * Only retain one ion with the same type and same charge state
			 */
			if (idx>=0 && ions[idx] == null) {
				ions[idx] = ion;
			}

			return true;
		} else
			return false;
	}

	/**
	 * If this peak matches to the ions with the type specified. For example, b
	 * or y ions or b* ions
	 * <p>
	 * <b>Warning: the type string must equal to that in
	 * setMatch(type,mstring).</b>
	 * 
	 * @param type
	 */
	public boolean isMatch2(int type) {
		return this.isMatched() ? this.ionMap.get(type) != null : false;
	}

	/**
	 * If this peak matches to the ions of b series
	 * 
	 * @param type
	 */
	public boolean isMatch2BIons() {
		return this.isMatch2(Ion.TYPE_B);
	}

	/**
	 * If this peak matches to the ions of y series
	 * 
	 * @param type
	 */
	public boolean isMatch2YIons() {
		return this.isMatch2(Ion.TYPE_Y);
	}

	/**
	 * If this peak has been set as matched for at least one type of the ions.
	 * 
	 * @return
	 */
	public boolean isMatched() {
		return this.ionMap != null;
	}

	/**
	 * The types of ion series for this peak matched to
	 * 
	 * @return
	 */
	public int[] getMatchedTypes() {
		if (!this.isMatched())
			return null;

		int size = this.ionMap.size();
		int[] types = new int[size];
		Iterator<Integer> iterator = this.ionMap.keySet().iterator();

		for (int i = 0; i < size; i++) {
			types[i] = iterator.next();
		}

		return types;
	}

	/**
	 * The matched ions. If no match, return null. The returned
	 * 
	 * @param type
	 * @return
	 */
	public Ion[] getMatchIons(int type) {
		if (this.ionMap == null)
			return null;

		return this.ionMap.get(type);
	}

	/**
	 * Check whether this peek is matched to the specific mzvalue. only peak
	 * bigger than the 1% of the max value can be evaluate. That is if the
	 * intensity of this peak is less than 1% of the max intensity, it always
	 * return false
	 * 
	 * @param mzvalue
	 * @param threshold
	 *            the match threshold, only if the experimental and theoretical
	 *            mass are within the mass tolerance and the intensity is higher
	 *            than the threshold, they are tend to be match.
	 * @return if this peek is matched to the mzvalue (true).
	 */
	public static boolean isMatch2(PeakForMatch peak, double mzvalue,
	        ISpectrumThreshold threshold) {
		if (peak.getIntensity() < threshold.getInstensityThreshold())
			return false;

		if (toleranceMatch(peak.getMz(), mzvalue, threshold.getMassTolerance()) == 0)
			return true;
		return false;
	}

	/**
	 * Match within the tolerance. If v1 > v2+tol return 1; if v1 +tol < v2
	 * return -1; else return 0
	 * 
	 * @param v1
	 *            M/Z value 1
	 * @param v2
	 *            M/Z value 2
	 * @param tolerance
	 *            a positive value for the mass tolerance.
	 * @return return 0 if difference between the two m/z values is within the
	 *         threshold; return 1 if bigger, and -1 for small;
	 */
	public static int toleranceMatch(double v1, double v2, double tolerance) {
		DecimalFormat f = DecimalFormats.DF0_4;
		double v = v1 - v2;
		if (v > 0) {
			if (v > tolerance)
				return 1;
			distance = Math.abs(Double.parseDouble(f.format(v)));
			return 0;
		} else {
			if (v < -tolerance)
				return -1;
			distance = Math.abs(Double.parseDouble(f.format(v)));
			return 0;
		}
	}

	public double getDistance(){
		return distance;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
	public String getLabel(){
		return label;
	}
	
}
