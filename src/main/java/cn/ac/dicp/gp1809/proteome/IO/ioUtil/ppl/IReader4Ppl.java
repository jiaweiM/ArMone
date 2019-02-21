/* 
 ******************************************************************************
 * File: IWriter4Ppl.java * * * Created on 05-23-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl;

/**
 * The reader for peptide list writer
 * 
 * @author Xinning
 * @version 0.1, 05-23-2010, 23:00:37
 */
public interface IReader4Ppl {

	/**
	 * The position for random access the file reader
	 * 
	 * @param position
	 */
	public void position(long position);

	/**
	 * Read the next line
	 * 
	 * @return
	 */
	public String readLine();

	/**
	 * Close the file
	 */
	public void close();

}
