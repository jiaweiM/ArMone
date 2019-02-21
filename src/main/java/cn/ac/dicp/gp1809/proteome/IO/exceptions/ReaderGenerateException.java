/*
 * *****************************************************************************
 * File: ReaderGenerateException.java * * * Created on 08-31-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.exceptions;

import cn.ac.dicp.gp1809.exceptions.MyException;


/**
 * This exception is threw by reader factory when there is no proper reader for this file(s)
 * 
 * @author Xinning
 * @version 0.2.1, 08-31-2008, 09:23:56
 */
public class ReaderGenerateException extends MyException{


	/**
     * 
     */
    private static final long serialVersionUID = -7013828330755455492L;

	public ReaderGenerateException() {
	    super();
	    // TODO Auto-generated constructor stub
    }

	public ReaderGenerateException(String message, Throwable cause) {
	    super(message, cause);
	    // TODO Auto-generated constructor stub
    }

	public ReaderGenerateException(String message) {
	    super(message);
	    // TODO Auto-generated constructor stub
    }

	public ReaderGenerateException(Throwable cause) {
	    super(cause);
	    // TODO Auto-generated constructor stub
    }
	
}
