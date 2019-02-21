/*
 * *****************************************************************************
 * File: OutFilenameFilter.java * * * Created on 05-01-2008
 * 
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.out;

import java.io.File;
import java.io.FilenameFilter;

/**
 * File name filter of .out files
 * 
 * @author Xinning
 * @version 0.1, 05-01-2008, 14:05:28
 */
public class OutFilenameFilter implements FilenameFilter{
	
	public boolean accept(File parent, String filename){
		String lowfilename = filename.toLowerCase();
		if(lowfilename.endsWith(".out"))
			return true;
		else 
			return false;
	}
}
