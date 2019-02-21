/* 
 ******************************************************************************
 * File: LoginErrorException.java * * * Created on 11-23-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.net.exception;

import cn.ac.dicp.gp1809.exceptions.MyException;

/**
 * Threw while error occurred when login to a website
 * 
 * @author Xinning
 * @version 0.1, 11-23-2008, 10:58:36
 */
public class LoginErrorException extends MyException {
	
	/**
     * 
     */
    private static final long serialVersionUID = 1l;

	/**
	 * 
	 */
	public LoginErrorException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public LoginErrorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public LoginErrorException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public LoginErrorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
