/*
 * *****************************************************************************
 * File: ISpectrum.java Created on 04-25-2008
 * 
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * *****************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

/**
 * This interface is the Spectrum. The feature of a spectrum is the inner peak
 * list.
 * 
 * @author Xinning
 * @version 0.1, 04-25-2008, 10:40:48
 */
public interface ISpectrum {

	/**
	 * The peak list in this spectrum. If the current spectrum is unreachable or
	 * the peaks are not pared while reading, null be returned.
	 * 
	 * @return the peaklist
	 */
	public IPeakList getPeakList();
	

	/**
	 * The MSLev of this scan, typically, if the precursor ion of this scan is
	 * from the full MS, this scan is a second level scan-MS2. Otherwise, if the
	 * precursor ion is from MS2 scan , this scan is a MS3 scan and so on. <b>
	 * Warning: <b> 
	 * this method currently can only be used for MzData file type.
	 * 
	 * @return
	 */
	public int getMSLevel();

	/**
	 * The scan number of this scan
	 * 
	 * @return
	 */
	public int getScanNum();

	/**
	 * The Integer instance of this scan number
	 * 
	 * @return
	 */
	public Integer getScanNumInteger();

	/**
	 * The index of this scan for all the scans (int some conditions, the MS1
	 * scan will not be reported, then the scan index doesn't equal to the scan
	 * number.)
	 * 
	 * @return the Nth number of this scan;
	 */
	public int getIndex();

	/**
	 * Sometimes, for fast reading, the peaklist may not parsed into the scan
	 * instance. In this condition, false will be returned and null will be
	 * returned when the method {@link #getPeakList()} is invoked.
	 * 
	 * @return
	 */
	public boolean isContainPeaklist();
	
	/**
	 * 
	 * @return The retention time.
	 */
	public double getRTMinute();
	
	/**
	 * 
	 * @return The total ion current.
	 */
	public double getTotIonCurrent();

}
