/* 
 ******************************************************************************
 * File: ParamFileParseException.java * * * Created on 03-04-2008
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
 * For cases, the configuration parameter file must be parse for the 
 * loading of parameters. If some errors occur while parsing, this 
 * exception will be threw.
 * 
 * @author Xinning
 * @version 0.1, 03-04-2008, 15:23:23
 */
public class ParameterParseException extends MyException {


	/**
     * 
     */
    private static final long serialVersionUID = -7775473699564606405L;

	/**
	 * 
	 */
	public ParameterParseException() {
	}

	/**
	 * @param message
	 */
	public ParameterParseException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ParameterParseException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ParameterParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
