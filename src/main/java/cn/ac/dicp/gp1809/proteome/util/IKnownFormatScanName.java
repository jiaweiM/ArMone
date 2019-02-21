/* 
 ******************************************************************************
 * File: IKnownFormatScanName.java * * * Created on 03-03-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.util;

/**
 * The scan name with known formats.
 * 
 * @author Xinning
 * @version 0.1.1, 08-10-2009, 20:06:17
 */
public interface IKnownFormatScanName extends IScanName {
	
	
	/**
	 * Get the formatted scan name from the known format scan name. 
	 * 
	 * @see {@code FormattedScanName}
	 * @return
	 */
	public FormattedScanName getFormattedScanName();
	
	
	/**
	 * {@inheritDoc}
	 */
	public IKnownFormatScanName deepClone();
	
}
