/* 
 ******************************************************************************
 * File: ProWriterException.java * * * Created on 12-31-2007
 *
 * Copyright (c) 2007 Xinning Jiang vext@163.com
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
 * When something goes wrong in pplwriter, pplswriter while writing,
 * this exception will be threw.
 * 
 * @author Xinning
 * @version 0.3, 05-31-2008, 18:16:06
 */
public class ProWriterException extends MyException {


	/**
     * 
     */
    private static final long serialVersionUID = -8326634773038470504L;

	public ProWriterException() {
	    super();
	    // TODO Auto-generated constructor stub
    }

	public ProWriterException(String message, Throwable cause) {
	    super(message, cause);
	    // TODO Auto-generated constructor stub
    }

	public ProWriterException(String message) {
	    super(message);
	    // TODO Auto-generated constructor stub
    }

	public ProWriterException(Throwable cause) {
	    super(cause);
	    // TODO Auto-generated constructor stub
    }
	
}
