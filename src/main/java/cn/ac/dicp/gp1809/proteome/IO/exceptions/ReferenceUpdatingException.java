/* 
 ******************************************************************************
 * File: ReferenceUpdatingException.java * * * Created on 09-10-2008
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
 * Threw if error occurs while the updating of ProteinReference when reading a
 * peptide. 
 * 
 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.AbstractPeptideReader.
 * 
 * @author Xinning
 * @version 0.1, 09-10-2008, 11:03:10
 */
public class ReferenceUpdatingException extends MyException {


	/**
     * 
     */
    private static final long serialVersionUID = 2087299248607010724L;

	/**
	 * 
	 */
	public ReferenceUpdatingException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ReferenceUpdatingException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ReferenceUpdatingException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ReferenceUpdatingException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
