/* 
 ******************************************************************************
 * File: IScanWriter.java * * * Created on 04-05-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;

/**
 * Write for the mass spectrum in each scan
 * 
 * @author Xinning
 * @version 0.1, 04-05-2010, 17:34:41
 */
public interface IScanWriter {
	
	/**
	 * Write the scan into file
	 * 
	 * @param scan
	 */
	public void write(ISpectrum scan);
	
	/**
	 * Finish and close the writer
	 */
	public void close();
	
}
