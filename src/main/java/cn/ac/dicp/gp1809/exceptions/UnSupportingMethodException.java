/* 
 ******************************************************************************
 * File: UnSupportingMethodException.java * * * Created on 11-01-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.exceptions;

/**
 * Some methods declared in the interface or super class may not being fully
 * supported by all of the sub classes. If the method which is not supported by
 * the sub class is called, this exception will be threw.
 * 
 * @author Xinning
 * @version 0.1, 11-01-2008, 16:15:54
 */
public class UnSupportingMethodException extends MyRuntimeException {


	/**
     * 
     */
    private static final long serialVersionUID = -2094583648635209152L;

	/**
	 * 
	 */
	public UnSupportingMethodException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnSupportingMethodException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public UnSupportingMethodException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public UnSupportingMethodException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
