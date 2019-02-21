/* 
 ******************************************************************************
 * File: IScan.java * * * Created on 02-24-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;

/**
 * A raw scan collected from mass spectrometer
 * 
 * @author Xinning
 * @version 0.1, 02-24-2009, 15:32:16
 */
public interface IMS2Scan extends ISpectrum {

	/**
	 * For ms2 only one precursor ms is valide; while for ms3, the mslev of
	 * precursor ms must be defined; e.g. ion with m/z value of 800 cid to ms2,
	 * and a neutral loss peek of 751 is selected to form a ms3 the mz value
	 * return is 2 ? 800 : 751; <b> Warning: </b> this method currently can only
	 * be used for MzData file type.
	 * 
	 * @param mslev
	 *            (2, or 3) (else return as 2)
	 * @return m/z value
	 */
	public double getPrecursorMZ(int mslev);

	/**
	 * Get the default precursor MZ value. That is, if this scan event is MS2
	 * then return the precursor MZ of MS2 else if the scan event if MS3, it
	 * return the precursor mz of MS3 scan (the ion in MS2 scan). <b>Warning:
	 * before excution of this method, getMSLev() should be excuted to confurm
	 * that this scan is not a full MS scan </b>
	 * 
	 * @return the precursor mz
	 */
	public double getPrecursorMZ();

	/**
	 * 
	 * @return
	 */
	public int getPrecursorScannum();
	
	/**
	 * 
	 * @return the precursor intensity
	 */
	public double getPrecursorInten();
	/**
	 * For MzXML and high accuracy mass spectra, the value is known, otherwise,
	 * 0 will be returned.
	 * 
	 * @return
	 */
	public short getCharge();
	
	/**
	 * The peak list in this spectrum. If the current spectrum is unreachable or
	 * the peaks are not pared while reading, null be returned.
	 * 
	 * @return the peaklist
	 */
	public IMS2PeakList getPeakList();
}
