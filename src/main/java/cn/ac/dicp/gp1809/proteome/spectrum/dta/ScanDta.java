/* 
 ******************************************************************************
 * File: ScanDta.java * * * Created on 11-14-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta;

import java.text.DecimalFormat;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.util.SpectrumUtil;
import cn.ac.dicp.gp1809.proteome.util.IScanName;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * A package of class containing all the informations of a dta file. These
 * informations include: scan number begin and scan number end, peaklist. This
 * implements ISpectrum interface.
 * 
 * @author Xinning
 * @version 0.2, 11-14-2008, 14:25:21
 */
public class ScanDta implements Comparable<ScanDta>, IScanDta {

	// To avoid the confusing situation that peak intensity have been normalized
	// to 1;
	private static final DecimalFormat DF3 = new DecimalFormat(".###");

	private static final DecimalFormat DF4 = new DecimalFormat(".####");

	private static final DecimalFormat DF6 = new DecimalFormat(".000000");

	/**
	 * PlatForm dependent turn for file writing.
	 */
	private static final String lineSeparator = IOConstant.lineSeparator;

	private IScanName scanName;
	private double precursormh;
	private IMS2PeakList peaks;

	// The level of ms scan for this dta
	private short mslevel = 2;

	/**
	 * The precursor MH and charge state is useful only when the peak list is null. Other wise
	 * the value returned by the method {@link #getPrecursorMH()} and
	 * {@link #getPrecursorMZ()} and {@link #getCharge()} will always be the value in the peak list.
	 * 
	 * @param scanName
	 * @param precursorMH
	 */
	public ScanDta(IScanName scanName, double precursorMH) {
		this.scanName = scanName;
		this.precursormh = precursorMH;
	}

	//	private ScanDta(IScanName scanName, double precursorMH, IMS2PeakList<?> peaks) {
	//		this(scanName, precursorMH);
	//		this.peaks = peaks;
	//	}

	/**
	 * The precursor peak must not be null. And should contain the charge state.
	 * 
	 * @param scanName
	 * @param peaks
	 */
	public ScanDta(IScanName scanName, IMS2PeakList peaks) {
		this.scanName = scanName;
		this.peaks = peaks;
		this.precursormh = peaks.getPrecursePeak().getMH();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#getScanName()
	 */
	public IScanName getScanName() {
		return this.scanName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#getScanNumberBeg()
	 */
	public int getScanNumberBeg() {
		return this.scanName.getScanNumBeg();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#getScanNumberEnd()
	 */
	public int getScanNumberEnd() {
		return this.scanName.getScanNumEnd();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#getCharge()
	 */
	public short getCharge() {
		return this.peaks == null ? this.scanName.getCharge(): this.peaks.getPrecursePeak().getCharge();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#getPrecursorMH()
	 */
	public double getPrecursorMH() {
		return peaks==null ? this.precursormh :this.peaks.getPrecursePeak().getMH();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#getPrecursorMZ()
	 */
	public double getPrecursorMZ() {
		return peaks==null ? SpectrumUtil.getMZ(this.precursormh, this.getCharge()) :this.peaks.getPrecursePeak().getMz();
	}

	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum#getPeakList()
	 */
	public IMS2PeakList getPeakList() {
		return this.peaks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#setScanNumberBeg(int)
	 */
	public void setScanNumberBeg(int scanNumberBeg) {
		this.scanName.setScanNumBeg(scanNumberBeg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#setScanNumberEnd(int)
	 */
	public void setScanNumberEnd(int scanNumberEnd) {
		this.scanName.setScanNumEnd(scanNumberEnd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#setCharge(short)
	 */
	public void setCharge(short charge) {
		this.scanName.setCharge(charge);
		this.peaks.getPrecursePeak().setCharge(charge);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#setPrecursorMH(double)
	 */
	public void setPrecursorMH(double mh) {
		if(this.peaks != null)
			this.peaks.getPrecursePeak().setMH(mh);
		
		this.precursormh = mh;
	}
	
	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#setPrecursorMZ(double)
	 */
	@Override
    public void setPrecursorMZ(double mz) {
		
		if(this.peaks != null)
			this.peaks.getPrecursePeak().setMz(mz);
		
		this.precursormh = SpectrumUtil.getMH(mz, this.getCharge());
		
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#setPeaks(cn.ac.dicp.
	 * gp1809.proteome.spectrum.PeakList)
	 */
	public void setPeaks(IMS2PeakList peaks) {
		this.peaks = peaks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#getMslevel()
	 */
	public short getMslevel() {
		return mslevel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta#setMslevel(short)
	 */
	public void setMslevel(short mslevel) {
		this.mslevel = mslevel;
	}

	/**
	 * Fist sort by the scan number from small to big, then sort by the charge
	 * state from small to big.
	 */
	public int compareTo(ScanDta dtafile) {
		int sbef = dtafile.getScanNumberBeg();

		if (this.scanName.getScanNumBeg() > sbef)
			return 1;
		if (this.scanName.getScanNumBeg() < sbef)
			return -1;

		short thischarge = this.getCharge();
		short ocharge = dtafile.getCharge();

		if (thischarge > ocharge)
			return 1;
		return thischarge < ocharge ? -1 : 0;
	}

	/**
	 * Output a well formatted dta file.
	 */
	@Override
	public String toString() {
		int size = this.peaks.size();
		StringBuilder sb = new StringBuilder(size * 16);
		sb.append(DF6.format(this.getPrecursorMH())).append(' ').append(
		        this.getCharge()).append(lineSeparator);

		for (int i = 0; i < size; i++) {
			IPeak peak = peaks.getPeak(i);
			sb.append(DF4.format(peak.getMz())).append(' ').append(
			        DF3.format(peak.getIntensity())).append(lineSeparator);
		}

		return sb.toString();
	}
}
