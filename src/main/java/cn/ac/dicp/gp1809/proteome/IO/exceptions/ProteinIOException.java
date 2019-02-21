/* 
 ******************************************************************************
 * File: ProteinReadingException.java * * * Created on 09-15-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.exceptions;

import cn.ac.dicp.gp1809.exceptions.MyException;

/**
 * Threw when some exceptions occur while the reading of protein
 * 
 * @author Xinning
 * @version 0.1, 09-15-2008, 16:25:20
 */
public class ProteinIOException extends MyException {

	/**
     * 
     */
    private static final long serialVersionUID = -4067904713041716448L;

	/**
     * 
     */
    public ProteinIOException() {
	    super();
	    // TODO Auto-generated constructor stub
    }

	/**
     * @param message
     * @param cause
     */
    public ProteinIOException(String message, Throwable cause) {
	    super(message, cause);
	    // TODO Auto-generated constructor stub
    }

	/**
     * @param message
     */
    public ProteinIOException(String message) {
	    super(message);
	    // TODO Auto-generated constructor stub
    }

	/**
     * @param cause
     */
    public ProteinIOException(Throwable cause) {
	    super(cause);
	    // TODO Auto-generated constructor stub
    }

}
