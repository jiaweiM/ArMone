/* 
 ******************************************************************************
 * File: DtaFileReadingException.java * * * Created on 04-25-2008
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
package cn.ac.dicp.gp1809.proteome.spectrum.dta;

import cn.ac.dicp.gp1809.exceptions.MyException;
/**
 * Threw while some error occurred in reading a dta file.
 * 
 * @author Xinning
 * @version 0.1, 04-25-2008, 10:50:58
 */
public class DtaFileParsingException extends MyException {


	/**
     * 
     */
    private static final long serialVersionUID = 2462353947983972814L;

	/**
	 * 
	 */
	public DtaFileParsingException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DtaFileParsingException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DtaFileParsingException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DtaFileParsingException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
