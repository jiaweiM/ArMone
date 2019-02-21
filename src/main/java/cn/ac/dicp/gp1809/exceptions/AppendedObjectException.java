/* 
 ******************************************************************************
 * File: AppendedObjectException.java * * * Created on 09-09-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.exceptions;

/**
 * One may want to append the used parameter at the end of the file so that
 * these parameters can be regenerated at next time for reading. If some
 * exceptions occurs while reading and writing, this exception will be threw.
 * 
 * @author Xinning
 * @version 0.1, 09-09-2008, 14:48:28
 */
public class AppendedObjectException extends Exception {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public AppendedObjectException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AppendedObjectException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public AppendedObjectException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public AppendedObjectException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
