/* 
 ******************************************************************************
 * File: IPeakListGettor.java * * * Created on 02-25-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

/**
 * Get the peaklist for the specific scan
 * 
 * @author Xinning
 * @version 0.1, 02-25-2009, 09:25:44
 */
public interface IPeakListGetter {

	/**
	 * Get the peak list for the scan. If the scan cannot be found, null will be
	 * returned.
	 * 
	 * @param scan_num
	 * @return
	 */
	public IPeakList getPeakList(int scan_num);

	/**
	 * Call this method after this getter is not needed yet. Close all the
	 * resources.
	 * 
	 * <p>
	 * <b>This method must be invoked for the clean up of the temporary file
	 */
	public void dispose();

}
