/* 
 ******************************************************************************
 * File: IScanName.java * * * Created on 11-07-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.util;

import cn.ac.dicp.gp1809.lang.IDeepCloneable;

/**
 * The formatted name indicates a scan of mass spectrum.
 * 
 * @author Xinning
 * @version 0.2, 08-10-2009, 20:02:22
 */
public interface IScanName extends IDeepCloneable{

	/**
	 * The scan name. If the extension(dta or out et. al.) is not predefined,
	 * the returned scan name will contains no extension (e.g. for sequest scan
	 * name, XXXXX.0000.0000.0). Otherwise, the full file name will be returned
	 * (e.g. XXXXX.0000.0000.0.dta).
	 * 
	 * @return
	 */
	public String getScanName();

	/**
	 * The scan name without extensions. For example, for sequest scan name,
	 * XXXXX.0000.0000.0
	 * 
	 * @return
	 */
	public String getScanNamenoExtension();

	/**
	 * The base name is commonly the name of the raw file.
	 * 
	 * @return the baseName
	 */
	public String getBaseName();

	/**
	 * The start scan number for a group scan
	 * 
	 * @return the scanNumBeg
	 */
	public int getScanNumBeg();

	/**
	 * The end scan number for a group scan. This number may equals to the start
	 * scan number if the scan is not a groupped scan
	 * 
	 * @return the scanNumEnd
	 */
	public int getScanNumEnd();

	/**
	 * The charge state of the scan
	 * 
	 * @return the charge state
	 */
	public short getCharge();
	
	/**
	 * The base name is commonly the name of the raw file.
	 * 
	 * @param baseName the baseName
	 */
	public void setBaseName(String baseName);

	/**
	 * The start scan number for a group scan
	 * 
	 * @param scanNumBeg the scanNumBeg
	 */
	public void setScanNumBeg(int scanNumBeg);

	/**
	 * The end scan number for a group scan. This number may equals to the start
	 * scan number if the scan is not a groupped scan
	 * 
	 * @param scanNumEnd the scanNumEnd
	 */
	public void setScanNumEnd(int scanNumEnd);

	/**
	 * The charge state of the scan
	 * 
	 * @param charge the charge state
	 */
	public void setCharge(short charge);
	
	/**
	 * The extension of the scan file. (dta or out and so on)
	 * 
	 * @param ext
	 */
	public void setExtension(String ext);
	
	/**
	 * If this scan name has the extension (dta or out and so on). 
	 * 
	 * @return
	 */
	public boolean hasExtension();
	
	
	/**
	 * The extension of the file (dta or out and so on)
	 * 
	 * @return
	 */
	public String getExtension();

	/**
	 * Test whether this scan name is an out scan name. (end with .out)
	 * 
	 * @return
	 */
	public boolean isOutFile();

	/**
	 * Test whether this scan name is an dta scan name. (end with .dta)
	 * 
	 * @return
	 */
	public boolean isDtaFile();
	
	/**
	 * {@inheritDoc}
	 */
	public IScanName deepClone();
}
