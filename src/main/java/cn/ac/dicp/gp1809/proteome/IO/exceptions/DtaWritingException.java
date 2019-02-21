/* 
 ******************************************************************************
 * File: DtaWritingException.java * * * Created on 08-10-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.exceptions;

import cn.ac.dicp.gp1809.exceptions.MyException;

/**
 * Threw while some exception occurs when the writing of dta files
 * 
 * @author Xinning
 * @version 0.1, 08-10-2009, 19:23:11
 */
public class DtaWritingException extends MyException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DtaWritingException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DtaWritingException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DtaWritingException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DtaWritingException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
