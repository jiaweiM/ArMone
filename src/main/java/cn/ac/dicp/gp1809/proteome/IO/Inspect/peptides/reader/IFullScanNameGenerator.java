/* 
 ******************************************************************************
 * File: IFullScanNameGenerator.java * * * Created on 03-25-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.reader;

import cn.ac.dicp.gp1809.proteome.util.IScanName;

/**
 * Inspect doesn't give the full scan name for both mgf and xml file. Use this
 * class to get the full scan name
 * 
 * @author Xinning
 * @version 0.1, 03-25-2009, 14:02:28
 */
public interface IFullScanNameGenerator {
	
	/**
	 * Get the full scan name for the 
	 * 
	 * @param idxorNum
	 * @return
	 */
	public IScanName getFullScanName(int idxorNum);
	
}
