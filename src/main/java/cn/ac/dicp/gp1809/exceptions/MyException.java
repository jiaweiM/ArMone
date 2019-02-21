/*
 ******************************************************************************
 * File: MyException.java * * * Created on 05-31-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.exceptions;

import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * A modified Exception which can be conveniently used in graphic user interface
 * to ouput the exception information with JOptionPanel. One of the mainly
 * uncomfortable for the exception ouput is that the string is too long.
 * Therefore, this class split the message with a fix small length so that the
 * output is beautiful.
 * 
 * <p>
 * All my Exceptions extends this class
 * 
 * @author Xinning
 * @version 0.2, 02-18-2009, 16:53:52
 */
public class MyException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = 4286889664004882675L;
	/**
	 * The max length of the message in a row, the characters with index bigger
	 * than this value will be wrapped into the next row.
	 * 
	 */
	public static final int maxLen = 20;

	/**
	 * The platform dependent line separator. For Windows, this value is "\r\n"
	 * while for linux this value will be "\n".
	 */
	private final static String lineSeparator = IOConstant.lineSeparator;

	/**
	 * 
	 */
	public MyException() {
		super();
	}

	/**
	 * @param message
	 */
	public MyException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public MyException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MyException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/*
	 * Auto word wrap (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	/*
	public String getMessage() {
		String mess = super.getMessage();
		if (mess == null)
			mess = "";
		int len = mess.length();
		if (len <= maxLen)
			return mess;

		StringBuilder sb = new StringBuilder(mess.length() + 10);
		int curtlineLen = 0;
		int idx = 0;
		for (int i = 0; i < len; i++) {
			if (curtlineLen >= maxLen) {
				char c = mess.charAt(i);

				if (c == ' '){
					sb.append(mess.substring(idx, i+1)).append(lineSeparator);
					idx = i+1;
					
					curtlineLen = 0;
				}
			}
			
			curtlineLen++;
		}
		
		sb.append(mess.substring(idx));
		return sb.toString();
	}*/
}
