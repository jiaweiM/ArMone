/* 
 ******************************************************************************
 * File:IMS1Scan.java * * * Created on 2010-4-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;

/**
 * @author ck
 *
 * @version 2010-4-25, 10:00:11
 */
public interface IMS1Scan extends ISpectrum{


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
	
	public double getRTMinute();

}
