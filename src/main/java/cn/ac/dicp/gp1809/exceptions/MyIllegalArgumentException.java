/* 
 ******************************************************************************
 * File: MyNullPointerException.java * * * Created on 07-13-2008
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
package cn.ac.dicp.gp1809.exceptions;

/**
 * Same as IllegalArgumentException, but for convenience of panel output.
 * 
 * @author Xinning
 * @version 0.1, 07-13-2008, 10:55:44
 */
public class MyIllegalArgumentException extends MyRuntimeException {


	/**
     * 
     */
    private static final long serialVersionUID = 7980796652638217647L;

	/**
	 * 
	 */
	public MyIllegalArgumentException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public MyIllegalArgumentException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public MyIllegalArgumentException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MyIllegalArgumentException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
