/* 
 ******************************************************************************
 * File: UnknownReaderTypeException.java * * * Created on 08-01-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.exceptions;

import cn.ac.dicp.gp1809.exceptions.MyException;

/**
 * While creating of the peptide reader, if the file type is unknown, this
 * exception will be threw.
 * <p>
 * This exception is may for being caught so that the unknown file type can be
 * skipped or processed.
 * 
 * @author Xinning
 * @version 0.1, 08-01-2008, 20:18:12
 */
public class UnknownReaderTypeException extends MyException {

	/**
     * 
     */
    private static final long serialVersionUID = -4107109933064818894L;

	/**
	 * 
	 */
	public UnknownReaderTypeException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public UnknownReaderTypeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public UnknownReaderTypeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnknownReaderTypeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
