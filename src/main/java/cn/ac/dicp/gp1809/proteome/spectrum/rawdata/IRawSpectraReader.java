/*
 * *****************************************************************************
 * File: IMassSpectraReader.java * * * Created on 07-29-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;

/**
 * Interface of reader for Raw data files collected by various mass
 * spectrometers. The mass spectra informations may be read from the following
 * formats:
 * <p>
 * 1. Raw files which are directly collected by mass spectrometers.
 * <p>
 * 2. The Mzdata files which are converted from the raw files for the each
 * reading and the global standards. These files include (1)mzdata files of PSI,
 * (2)mzxml files of ISB and (3)the currently new developed mzml files.
 * 
 * @author Xinning
 * @version 0.2, 07-29-2008, 20:45:07
 */
public interface IRawSpectraReader {

	/**
	 * Parsing the file to generate scan list, contain ms1 and ms2.
	 * 
	 */
	public void rapScanList();

	/**
	 * Parsing the file to generate ms1 scan list.
	 */
	public void rapMS1ScanList();

	/**
	 * Parsing the file to generate ms2 scan list.
	 */
	public void rapMS2ScanList();

	public ISpectrum getNextSpectrum();

	public MS1Scan getNextMS1Scan();

	public MS2Scan getNextMS2Scan();

	public IPeakList getPeakList(int scan_num);

	public IMS2PeakList getMS2PeakList(int scan_num);

	public MS1ScanList getMS1ScanList();

	public MS2ScanList getMS2ScanList();

	public double getMS1TotalCurrent();

	/**
	 * Close the reader, but other resources in memory or in temporary buffer
	 * will not be closed.
	 */
	public void close();
}
