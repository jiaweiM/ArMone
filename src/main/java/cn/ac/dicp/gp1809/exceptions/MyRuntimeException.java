/* 
 ******************************************************************************
 * File: MyException.java * * * Created on 05-31-2008
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
 * A modified Exception which can be conveniently used in graphic
 * user interface to ouput the exception information with JOptionPanel. 
 * One of the mainly uncomfortable for the exception ouput is that the string
 * is too long. Therefore, this class split the message with a fix small length
 * so that the output is beautiful. 
 * 
 * <p> All my Exceptions extends this class
 * 
 * @author Xinning
 * @version 0.1, 05-31-2008, 17:53:14
 */
public class MyRuntimeException extends RuntimeException {

	/**
     * 
     */
    private static final long serialVersionUID = 7737861871293701786L;
	/**
	 * The max length of the message in a row, the characters with index bigger than
	 * this value will be wrapped into the next row.
	 * 
	 */
	public static final int maxLen = 80;
	
	/**
	 * 
	 */
	public MyRuntimeException() {
		super();
	}

	/**
	 * @param message
	 */
	public MyRuntimeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public MyRuntimeException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MyRuntimeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public String getMessage(){
		String mess = super.getMessage();
		if(mess == null)
			mess = "";
		int len = mess.length();
		if(len <= maxLen)
			return mess;
		

		int rowcount = len/ maxLen;
		StringBuilder sb = new StringBuilder(len + rowcount);
		sb.append(mess);
		for(int i=1;i<=rowcount;i++){
			sb.insert(maxLen*i+i, '\n');
		}
		return sb.toString();
	}
}
