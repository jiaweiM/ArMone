/* 
 ******************************************************************************
 * File: IScanDta.java * * * Created on 03-05-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.util.IScanName;

/**
 * The scanDta
 * 
 * @author Xinning
 * @version 0.1, 03-05-2009, 10:32:54
 */
public interface IScanDta {

	/**
	 * Get the scan name.
	 * 
	 * @return
	 */
	public IScanName getScanName();

	/**
	 * The first scan number of the dta file. (XXXX.1200.1201.2.dta, return
	 * 1200)
	 * 
	 * @return the first scan number
	 */
	public int getScanNumberBeg();

	/**
	 * The last scan number of the dta file. (XXXX.1200.1201.2.dta, return 1201)
	 * 
	 * @return the last scan number
	 */
	public int getScanNumberEnd();

	/**
	 * The charge state of this dta file (the corresponding spectrum)
	 * 
	 * @return
	 */
	public short getCharge();

	/**
	 * The MH+ value of the precursor ion. This value equals to the value in the
	 * first line of dta file.
	 * 
	 * @return
	 */
	public double getPrecursorMH();

	/**
	 * The m/z value of the precursor ion for this dta file
	 * 
	 * @return
	 */
	public double getPrecursorMZ();

	/**
	 * @param scanNumberBeg
	 *            the scanNumberBeg to set
	 */
	public void setScanNumberBeg(int scanNumberBeg);

	/**
	 * @param scanNumberEnd
	 *            the scanNumberEnd to set
	 */
	public void setScanNumberEnd(int scanNumberEnd);

	/**
	 * @param charge
	 *            the charge to set
	 */
	public void setCharge(short charge);

	/**
	 * @param mh
	 *            precursor MH+ to be set
	 */
	public void setPrecursorMH(double mh);
	
	/**
	 * @param mh
	 *            precursor mz to be set. The charge state must be known
	 */
	public void setPrecursorMZ(double mz);

	/**
	 * @param peaks
	 *            the peaks to set
	 */
	public void setPeaks(IMS2PeakList peaks);

	/**
	 * the mslevel of this dta file. If not specified, it will be considered as
	 * MS/MS (ms2). (dta generated from sequest separated .dta)
	 * 
	 * @return the mslevel of this dta file
	 */
	public short getMslevel();

	/**
	 * Set the ms level for this dta file. If not specified, it will be
	 * considered as ms2
	 * 
	 * @param mslevel
	 *            the mslevel to set
	 */
	public void setMslevel(short mslevel);
	
	public IMS2PeakList getPeakList();

}