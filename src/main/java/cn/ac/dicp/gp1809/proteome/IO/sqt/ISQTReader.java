/* 
 ******************************************************************************
 * File: ISQTReader.java * * * Created on 03-31-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sqt;

/**
 * The reader interface for SQT result file
 * 
 * @author Xinning
 * @version 0.1, 03-31-2009, 19:29:52
 */
public interface ISQTReader {
	
	/**
	 * The header
	 * 
	 * @return
	 */
	public ISQTHeader getHeader();
	
	/**
	 * The next spectrum and the identified peptides
	 * 
	 * @return
	 */
	public IPepMatches getNextMatch() throws SQTReadingException;
	
	/**
	 * Close
	 */
	public void close();
}
