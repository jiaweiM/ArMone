/* 
 ******************************************************************************
 * File: ReferenceDetailFormatFactory.java * * * Created on 09-15-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;

/**
 * Factory for reference detail creation
 * 
 * @author Xinning
 * @version 0.1, 09-15-2008, 15:58:00
 */
public class ReferenceDetailFormatFactory {

	private ReferenceDetailFormatFactory() {

	}

	/**
	 * Create a reftitle by the title of the reference detail
	 * 
	 * @param reftitle
	 * @return
	 * @throws NullPointerException
	 * @throws IllegalFormaterException
	 */
	public static IReferenceDetailFormat createFormat(String reftitle)
	        throws IllegalFormaterException, NullPointerException {

		// Currently only one constructor

		return DefaultReferenceDetailFormat.parseTitle(reftitle);

	}
	
	public static IReferenceDetailFormat createFormat(String [] reftitle)
    	throws IllegalFormaterException, NullPointerException {

		// Currently only one constructor

		return DefaultReferenceDetailFormat.parseTitle(reftitle);

	}

}
