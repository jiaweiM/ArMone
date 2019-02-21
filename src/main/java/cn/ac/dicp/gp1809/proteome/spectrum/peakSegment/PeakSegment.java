/* 
 ******************************************************************************
 * File: PeakSegment.java * * * Created on 2012-9-10
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.peakSegment;

import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;

/**
 * 
 * @author JiaweiMao
 * @version Jun 24, 2016, 12:27:45 PM
 */
public class PeakSegment implements Comparable<PeakSegment> {

	private double peakMz;
	private double peakInten;
	private double mr;
	private double ppm;
	private int charge;
	private double originalMz;
	/**
	 * the begin value of the mz window
	 */
	private double beg;
	/**
	 * the end value of the mz window
	 */
	private double end;

	public PeakSegment(IPeak peak, int charge, double originalMz) {
		this.peakMz = peak.getMz();
		this.peakInten = peak.getIntensity();
		this.charge = charge;
		this.ppm = 20;
		this.originalMz = originalMz;

		this.mr = (peakMz - AminoAcidProperty.PROTON_W) * (double) charge;
		this.beg = mr - mr * ppm * 1.0E-6;
		this.end = mr + mr * ppm * 1.0E-6;
	}

	public PeakSegment(double mz, double inten, int charge, double originalMz) {
		this.peakMz = mz;
		this.peakInten = inten;
		this.charge = charge;
		this.ppm = 20;
		this.originalMz = originalMz;

		this.mr = (peakMz - AminoAcidProperty.PROTON_W) * (double) charge;
		this.beg = mr - mr * ppm * 1.0E-6;
		this.end = mr + mr * ppm * 1.0E-6;
	}

	public PeakSegment(IPeak peak, int charge, double ppm, double originalMz) {
		this.peakMz = peak.getMz();
		this.peakInten = peak.getIntensity();
		this.charge = charge;
		this.ppm = ppm;
		this.originalMz = originalMz;

		this.mr = (peakMz - AminoAcidProperty.PROTON_W) * (double) charge;
		this.beg = mr - mr * ppm * 1.0E-6;
		this.end = mr + mr * ppm * 1.0E-6;
	}

	public PeakSegment(double mz, double inten, int charge, double ppm, double originalMz) {
		this.peakMz = mz;
		this.peakInten = inten;
		this.charge = charge;
		this.ppm = ppm;
		this.originalMz = originalMz;

		this.mr = (peakMz - AminoAcidProperty.PROTON_W) * (double) charge;
		this.beg = mr - mr * ppm * 1.0E-6;
		this.end = mr + mr * ppm * 1.0E-6;
	}

	/**
	 * @param ps another PeakSegment
	 * @return true if this PeakSegment is overlap with the other
	 */
	public boolean isOverlap(PeakSegment ps) {

		double b0 = this.beg;
		double e0 = this.end;
		double b1 = ps.beg;
		double e1 = ps.end;

		if (b0 < b1) {
			if (e0 < b1) {
				return false;
			} else {
				return true;
			}
		} else if (b0 == b1) {
			return true;
		} else {
			if (e1 < b0) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	/**
	 * @return mass of the peak
	 */
	public double getMass() {
		return mr;
	}

	public double getPeakMz() {
		return peakMz;
	}

	public double getPeakInten() {
		return peakInten;
	}

	public double getBeg() {
		return beg;
	}

	public double getEnd() {
		return end;
	}

	public int getCharge() {
		return charge;
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append(originalMz).append("-").append(peakMz).append("-").append(mr).append("-").append(charge).append("-")
				.append(peakInten);

		return sb.toString();
	}

	@Override
	public int compareTo(PeakSegment o) {
		double beg0 = o.beg;

		if (this.beg < beg0) {
			return -1;
		} else if (this.beg == beg0) {
			return 0;
		} else {
			return 1;
		}
	}
}
