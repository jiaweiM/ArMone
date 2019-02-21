/* 
 ******************************************************************************
 * File: IllegalFormaterException.java * * * Created on 05-13-2008
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
 * Threw when the formater for peptide is illegal.
 * For example: sequence must be included for a peptide, otherwise,
 * the formater is illegal.
 * 
 * @author Xinning
 * @version 0.2, 05-31-2008, 18:14:09
 */
public class IllegalFormaterException extends MyException {


	/**
     * 
     */
    private static final long serialVersionUID = 8601158367250226583L;

	/**
	 * 
	 */
	public IllegalFormaterException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public IllegalFormaterException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public IllegalFormaterException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public IllegalFormaterException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
