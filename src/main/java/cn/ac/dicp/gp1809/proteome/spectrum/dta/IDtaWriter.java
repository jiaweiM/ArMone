/* 
 ******************************************************************************
 * File: IDtaWriter.java * * * Created on 06-04-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;

/**
 * The single dta writer
 * 
 * @author Xinning
 * @version 0.1.1, 08-10-2009, 19:25:27
 */
public interface IDtaWriter {
	
	/**
	 * Write a scan dta to a file 
	 * 
	 * @param dta
	 * @param path
	 * @return
	 * @throws DtaWritingException
	 */
	public boolean write(IScanDta dta, String path) throws DtaWritingException;
	
}
