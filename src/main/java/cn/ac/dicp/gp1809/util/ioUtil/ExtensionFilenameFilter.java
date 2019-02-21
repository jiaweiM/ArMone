/*
 ******************************************************************************
 * File: ExtensionFilenameFilter.java * * * Created on 03-20-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil;

import java.io.File;
import java.io.FilenameFilter;

/**
 * File name filter using the extension of the file name
 * 
 * @author Xinning
 * @version 0.1, 03-20-2009, 16:15:06
 */
public class ExtensionFilenameFilter implements FilenameFilter{
	private String extension;
	private boolean caseSensitive;
	
	/**
	 * Case insensitive
	 * 
	 * @param suffix the extension
	 */
	public ExtensionFilenameFilter(String extension){
		this(extension, false);
	}
	
	/**
	 * 
	 * @param suffix the extension
	 * @param caseSensitive if the filter is case sensitive
	 */
	public ExtensionFilenameFilter(String extension, boolean caseSensitive){
		this.caseSensitive = caseSensitive;
		if(!caseSensitive)
			this.extension = extension.toLowerCase();
		else
			this.extension = extension;
		
	}
	
	public boolean accept(File parent, String filename){
		if(this.caseSensitive) {
			if(filename.endsWith(extension))
				return true;
			else
				return false;
		}
		else {
			if(filename.toLowerCase().endsWith(extension))
				return true;
			else
				return false;
		}
	}
}
