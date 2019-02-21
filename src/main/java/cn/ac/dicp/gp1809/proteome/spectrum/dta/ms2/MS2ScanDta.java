/* 
 ******************************************************************************
 * File: MS2ScanDta.java * * * Created on 03-29-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.ms2;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ScanDta;
import cn.ac.dicp.gp1809.proteome.util.IScanName;

/**
 * The scan dta of MS2 dta file
 * 
 * @author Xinning
 * @version 0.1, 03-29-2009, 19:36:04
 */
public class MS2ScanDta extends ScanDta implements IScanDta {

	/**
	 * @param scanName
	 * @param precursorMh
	 */
	public MS2ScanDta(IScanName scanName, double precursorMh) {
		super(scanName, precursorMh);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param scanName
	 * @param peaks
	 */
	public MS2ScanDta(IScanName scanName, IMS2PeakList peaks) {
		super(scanName, peaks);
		// TODO Auto-generated constructor stub
	}

}
