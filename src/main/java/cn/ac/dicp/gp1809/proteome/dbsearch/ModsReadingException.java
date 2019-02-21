/* 
 ******************************************************************************
 * File: ModsReadingException.java * * * Created on 09-03-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch;

import cn.ac.dicp.gp1809.exceptions.MyException;

/**
 * Threw when some error occurs while paring OMSSA mods file.
 * 
 * @author Xinning
 * @version 0.1, 09-03-2008, 16:49:26
 */
public class ModsReadingException extends MyException {

	/**
     * 
     */
    private static final long serialVersionUID = -3560823349362552912L;

	/**
	 * 
	 */
	public ModsReadingException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ModsReadingException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ModsReadingException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ModsReadingException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
