/* 
 ******************************************************************************
 * File: PeakSegment.java * * * Created on 2012-2-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.structure;

import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.peakSegment.PeakSegment;

/**
 * 
 * @author JiaweiMao
 * @version Jun 24, 2016, 12:25:40 PM
 */
public class NCorePeakSegment extends PeakSegment {

	/**
	 * 0=pep+NexNAc; 1=pep+NexNAc*2...
	 */
	private int type;
	private double originalMz;

	public NCorePeakSegment(IPeak peak, int charge, double originalMz, int type) {
		super(peak, charge, originalMz);
		this.type = type;
	}

	public NCorePeakSegment(double mz, double inten, int charge, double originalMz, int type) {
		super(mz, inten, charge, originalMz);
		this.type = type;
	}

	public NCorePeakSegment(IPeak peak, int charge, double ppm, double originalMz, int type) {
		super(peak, charge, ppm);
		this.originalMz = originalMz;
		this.type = type;
	}

	/**
	 * 
	 * @param mz m/z value of the peptide
	 * @param inten intensity
	 * @param charge charge
	 * @param ppm delta ppm
	 * @param originalMz original peak m/z
	 * @param type y type
	 */
	public NCorePeakSegment(double mz, double inten, int charge, double ppm, double originalMz, int type) {
		super(mz, inten, charge, ppm);
		this.originalMz = originalMz;
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public double getOriginalMz() {

		return originalMz;
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(originalMz).append("-").append(super.getPeakMz()).append("-").append(super.getMass()).append("-")
				.append(super.getCharge()).append("-").append(super.getPeakInten());

		return sb.toString();
	}
}
