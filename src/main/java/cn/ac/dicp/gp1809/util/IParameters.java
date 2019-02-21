/* 
 ******************************************************************************
 * File: IParameters.java * * * Created on 07-25-2008
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
package cn.ac.dicp.gp1809.util;

import java.io.File;
import java.io.IOException;

/**
 * Parameters which can be used to save different type of parameters.
 * 
 * @author Xinning
 * @version 0.1, 07-25-2008, 08:54:02
 */
public interface IParameters {

	/**
	 * Save the parameters to the file
	 * 
	 * @param filename
	 * @param comments
	 * @return
	 * @throws IOException
	 */
	public abstract File saveToFile(String filename, String comments)
	        throws IOException;

	/**
	 * Save the parameters to xml file format
	 * 
	 * @param filename
	 * @param comments
	 * @return
	 * @throws IOException
	 */
	public abstract File saveToXmlFile(String filename, String comments)
	        throws IOException;

	/**
	 * Load from a saved configuration file
	 * 
	 * @param filename
	 * @throws IOException 
	 */
	public abstract void loadFromFile(String filename) throws IOException;

	/**
	 * Load from a saved xml formated configuration file
	 * 
	 * @param filename
	 * @throws IOException 
	 */
	public abstract void loadFromXmlFile(String xmlfilename) throws IOException;

}