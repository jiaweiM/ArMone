/*
 * *****************************************************************************
 * File: Tag.java * * * Created on 08-29-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.format;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An simple tag for file output; sometime the completion of a file need to be
 * checked; This class output a formated time when writing file and check the
 * file when when reading
 * 
 * @author Xinning
 * @version 0.2, 08-29-2008, 17:01:21
 */
public class Tag {
	// The time format for time tag
	private static final DateFormat datetimeformat = new SimpleDateFormat(
	        "yyyy-MM-dd HH:mm:ss");

	/**
	 *  The format of date time is "yyyy-MM-dd HH:mm:ss".
	 * 
	 * @return a tag at the begin of file indicated the creation time formated
	 *         as:"2007-4-9 15:29:41"
	 */
	public static String getTimeTag() {
		return datetimeformat.format(new Date());
	}

	/**
	 * Check whether the time tag indicated by the string is legal. Because the
	 * format of date may be different, this method can only parse date which
	 * was output by getTimeTag();
	 * 
	 * @param tag
	 *            the first line of the file which created with simpleTag;
	 * @return the creation time date instance; If can't find the date tage at
	 *         the first line return null;
	 */
	public static Date checkTimeTag(String tag) {
		Date date = null;
		try {
			date = datetimeformat.parse(tag);
		} catch (ParseException e) {
			// do nothing but return null;
		}
		return date;
	}
}
