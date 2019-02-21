/* 
 ******************************************************************************
 * File: IUnknownFormatScanName.java * * * Created on 03-03-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.util;

/**
 * The scan name with unknown format.
 * 
 * @author Xinning
 * @version 0.1, 03-03-2009, 14:20:18
 */
public interface IUnknownFormatScanName extends IScanName {

	
	/**
	 * {@inheritDoc}
	 */
	public IUnknownFormatScanName deepClone();
	
}
