/* 
 ******************************************************************************
 * File: MascotScanDta.java * * * Created on 05-19-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ScanDta;
import cn.ac.dicp.gp1809.proteome.util.IScanName;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-19-2010, 10:38:33
 */
public class MascotScanDta extends ScanDta {

	/**
     * @param scanName
     * @param precursorMH
     */
    public MascotScanDta(IScanName scanName, double precursorMH) {
	    super(scanName, precursorMH);
    }

	/**
     * @param scanName
     * @param peaks
     */
    public MascotScanDta(IScanName scanName, IMS2PeakList peaks) {
	    super(scanName, peaks);
    }
}
