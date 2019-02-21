/* 
 ******************************************************************************
 * File: IProteinReader.java * * * Created on 09-15-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProteinIOException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;

/**
 * Reader for protein
 * 
 * @author Xinning
 * @version 0.1, 09-15-2008, 16:21:58
 */
public interface IProteinReader {

	/**
	 * Read the next protein. If there is no protein remained, return null.
	 * 
	 * @return
	 */
	public Protein getProtein() throws ProteinIOException;
	
	
	/**
	 * Close the stream or reader for protein reading.
	 * 
	 */
	public void close();

}
