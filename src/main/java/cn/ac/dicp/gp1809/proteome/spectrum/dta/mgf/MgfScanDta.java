/* 
 ******************************************************************************
 * File: MgfScanDta.java * * * Created on 03-25-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.Description;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.proteome.util.IScanName;

/**
 * The scan dta in mgf file
 * 
 * @author Xinning
 * @version 0.1.1, 06-10-2009, 17:01:56
 */
public class MgfScanDta extends ScanDta {

	private int query;
	
	/**
	 * @param scanName
	 * @param precursorMh
	 */
	public MgfScanDta(IScanName scanName, double precursorMh) {
		super(scanName, precursorMh);
	}

	/**
	 * @param scanName
	 * @param peaks
	 */
	public MgfScanDta(IScanName scanName, IMS2PeakList peaks) {
		super(scanName, peaks);
	}

	public void setQuery(int query){
		this.query = query;
	}
	
	public int getQuery(){
		return this.query;
	}
/*	
	public IMS2Scan createMS2Scan(){
		int scanNum = this.getScanNumberBeg();
		int msLevel = this.getMslevel();
		Description des = new Description(scanNum, msLevel);
		
		IMS2Scan scan = new MS2Scan(des);
		return scan;
	}
*/	
}
