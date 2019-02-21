/* 
 ******************************************************************************
 * File: IFormat.java * * * Created on 09-12-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters;

/**
 * Formatter used to parse or generate object formatted strings.
 * 
 * @author Xinning
 * @version 0.3, 05-04-2009, 16:32:56
 */
public interface IFormat <T> extends java.io.Serializable{
	
	/**
	 * Title indicates name of each column. The title should be output first into
	 * a file for easy reading by users. Then the formatted reference string can
	 * be output.
	 * 
	 * @return
	 */
	public String getTitleString();
	
	/**
	 * The cells for the title name
	 * 
	 * @return
	 */
	public String[] getTitle();
	
	/**
	 * Use this formatter to format the peptide into a String for output.
	 * 
	 * @param peptide
	 * @return formatted String for this peptide instance
	 */
	public String format(T obj);

	/**
	 * Parse a formatted string into the T instance.
	 * 
	 * @param string
	 * @return
	 */
	public T parse(String string);
	
}
