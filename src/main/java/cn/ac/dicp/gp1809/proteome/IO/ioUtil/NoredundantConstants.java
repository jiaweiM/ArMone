/* 
 ******************************************************************************
 * File: NoredundantInstants.java * * * Created on 03-30-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

/**
 * The instants of noredundant and unduplicated file
 * 
 * @author Xinning
 * @version 0.1, 03-30-2010, 13:18:59
 */
public interface NoredundantConstants {
	
	/**
	 * The file extension of the noredundant file
	 */
	public String NORED_EXTENSION = "nord";
	
	/**
	 * The previouse used extension for noredundant file
	 * 
	 */
	public String[] PREV_USED_NORED_EXTENSION = new String[] {"noredundant"};
	
	
	/**
	 * The file extension of the unduplicated file
	 */
	public String UNDUP_EXTENSION = "nodup";
	
	/**
	 * The previouse used extension for noredundant file
	 * 
	 */
	public String[] PREV_USED_UNDUP_EXTENSION = new String[] {"unduplicated"};
	
}
