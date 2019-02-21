/* 
 ******************************************************************************
 * File: PeptideListStamp.java * * * Created on 08-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import cn.ac.dicp.gp1809.format.IStamp;

/**
 * The stamp used for the validation of the intact of peptide list file
 * 
 * @author Xinning
 * @version 0.1, 08-29-2008, 20:29:05
 */
public class PeptideListStamp implements IStamp {

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.format.IStamp#getStamp()
	 */
	@Override
	public String getStamp() {
		
		
		
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.format.IStamp#validateStamp(java.lang.String)
	 */
	@Override
	public boolean validateStamp(String stamp) {
		return false;
	}

}
