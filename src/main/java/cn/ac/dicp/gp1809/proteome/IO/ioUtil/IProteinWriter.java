/* 
 ******************************************************************************
 * File: IProteinWriter.java * * * Created on 08-19-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;

/**
 * Protein writer
 * 
 * @author Xinning
 * @version 0.1, 08-19-2008, 09:56:52
 */
public interface IProteinWriter {
	
	/**
	 * Write a protein.
	 * 
	 * @param protein
	 */
	public void write(Protein protein) throws Exception;
	
	/**
	 * Close the writer while the writing has been finished.
	 */
	public void close() throws Exception;
	
}
