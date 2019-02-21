/* 
 ******************************************************************************
 * File: XmlReadingException.java * * * Created on 07-16-2008
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
package cn.ac.dicp.gp1809.util.ioUtil.xml;

import cn.ac.dicp.gp1809.exceptions.MyException;

/**
 * If some exceptions occurs wile reading of xml file, this exception
 * will be threw.
 * 
 * @author Xinning
 * @version 0.1, 07-16-2008, 16:53:53
 */
public class XmlReadingException extends MyException {

	/**
     * 
     */
    private static final long serialVersionUID = -4827015135931615937L;

	/**
	 * 
	 */
	public XmlReadingException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public XmlReadingException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public XmlReadingException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public XmlReadingException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
