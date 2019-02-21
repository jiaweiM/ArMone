/* 
 ******************************************************************************
 * File: SQTReadingException.java * * * Created on 04-01-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sqt;

import cn.ac.dicp.gp1809.exceptions.MyException;

/**
 * Threw when error occurs while the reading of SQT file
 * 
 * @author Xinning
 * @version 0.1, 04-01-2009, 09:50:23
 */
public class SQTReadingException extends MyException {

	/**
     * 
     */
    private static final long serialVersionUID = -352547189129736814L;

	/**
     * 
     */
    public SQTReadingException() {
	    // TODO Auto-generated constructor stub
    }

	/**
     * @param message
     * @param cause
     */
    public SQTReadingException(String message, Throwable cause) {
	    super(message, cause);
	    // TODO Auto-generated constructor stub
    }

	/**
     * @param message
     */
    public SQTReadingException(String message) {
	    super(message);
	    // TODO Auto-generated constructor stub
    }

	/**
     * @param cause
     */
    public SQTReadingException(Throwable cause) {
	    super(cause);
	    // TODO Auto-generated constructor stub
    }
	
}
