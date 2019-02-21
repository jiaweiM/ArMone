/* 
 ******************************************************************************
 * File: MascotDatParsingException.java * * * Created on 11-10-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

import cn.ac.dicp.gp1809.exceptions.MyException;

/**
 * Threw while error of parsing Mascot dat file.
 * 
 * @author Xinning
 * @version 0.1, 11-10-2008, 21:34:53
 */
public class MascotDatParsingException extends MyException {

	/**
     * 
     */
    private static final long serialVersionUID = 7564361722276623434L;

	/**
	 * 
	 */
	public MascotDatParsingException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public MascotDatParsingException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public MascotDatParsingException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MascotDatParsingException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
