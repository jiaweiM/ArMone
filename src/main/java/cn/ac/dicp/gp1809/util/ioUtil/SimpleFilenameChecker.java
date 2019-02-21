/* 
 ******************************************************************************
 * File: SimpleFilenameChecker.java * * * Created on 01-03-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil;

/**
 * A simple file name checker which checks the extension of the file. If the
 * input file name doesn't end with the extension, this checker will append the
 * extension at the end.
 * 
 * @author Xinning
 * @version 0.2, 03-17-2010, 17:18:51
 */
public class SimpleFilenameChecker {

	/**
	 * A simple file name checker which checks the extension of the file. If the
	 * input file name doesn't end with the extension, this checker will append
	 * the extension at the end.
	 * 
	 * @param filename
	 * @param extension
	 * @return
	 */
	public static String check(String filename, String extension) {
		if (!filename.toLowerCase().endsWith(extension.toLowerCase()))
			return filename + "." + extension;
		else
			return filename;
	}

	/**
	 * A simple file name checker. If the file name is not with one of the
	 * specified extensions, it will be renamed automatically with the prior
	 * extension
	 * 
	 * @param filename
	 * @param extensions extensions
	 * @param prior_ext the prefer extension (!! without ".")
	 * @return
	 */
	public static String check(String filename, String[] extensions,
	        String prior_ext) {
		String lowcase = filename.toLowerCase();
		
		for(String ext : extensions) {
			if(lowcase.endsWith(ext.toLowerCase())) {
				return filename;
			}
		}
		
		return filename + "." + prior_ext;
	}
	
	/**
	 * Check whether the file name is with the specified extension
	 * 
	 * @param filename
	 * @param extension
	 * @return
	 */
	public static boolean checkExt(String filename, String extension) {
		if (!filename.toLowerCase().endsWith(extension.toLowerCase()))
			return false;
		else
			return true;
	}
	
	/**
	 * Check whether the file name is with one of the extensions. 
	 * 
	 * @param filename
	 * @param extensions
	 * @return
	 */
	public static boolean checkExt(String filename, String[] extensions) {
		String lowcase = filename.toLowerCase();
		
		for(String ext : extensions) {
			if(lowcase.endsWith(ext.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}
}
