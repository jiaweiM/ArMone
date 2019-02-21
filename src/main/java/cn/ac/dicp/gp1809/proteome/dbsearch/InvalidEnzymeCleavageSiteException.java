/* 
 ******************************************************************************
 * File: InvalidEnzymeCleavageSiteException.java * * * Created on 03-04-2008
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
package cn.ac.dicp.gp1809.proteome.dbsearch;

import cn.ac.dicp.gp1809.exceptions.MyException;


/**
 * If the cleavage site of enzyme is invalid while creating of enzyme instance,
 * this exception will be threw.
 * 
 * @author Xinning
 * @version 0.1, 03-04-2008, 18:46:19
 */
public class InvalidEnzymeCleavageSiteException extends MyException {


	/**
     * 
     */
    private static final long serialVersionUID = 2262521777346462949L;

	/**
	 * 
	 */
	public InvalidEnzymeCleavageSiteException() {
	}

	/**
	 * @param message
	 */
	public InvalidEnzymeCleavageSiteException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidEnzymeCleavageSiteException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidEnzymeCleavageSiteException(String message, Throwable cause) {
		super(message, cause);
	}

}
