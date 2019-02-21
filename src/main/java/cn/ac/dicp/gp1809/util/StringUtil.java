/*
 * *****************************************************************************
 * File: StringUtil.java * * * Created on 09-03-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.util;

import java.util.ArrayList;

/**
 * Utilities for String.
 * 
 * <p>
 * Changes:
 * <li>0.1.1, 04-01-2009: Add method {@link #splitAndTrim(String, char)} and
 * {@link #splitAndTrim(String, String)}
 * 
 * @author Xinning
 * @version 0.1.1, 04-01-2009, 22:11:51
 */
public class StringUtil {

	/**
	 * This method uses the indexOf() and the subString() methdos to split the
	 * target String by the splitter, compared with String.split, this method
	 * greatly improves the performance for the splitting
	 * 
	 * @param s
	 * @return String array after splitting, the last blank string will not be
	 *         included. (e.g. "asdf:asdf:" after splitted with ':', [asdf,
	 *         asdf] will be generated£©
	 */
	public static String[] split(String input, String spliter) {
		ArrayList<String> matchList = new ArrayList<String>();
		int len = spliter.length();
		int index = -1;
		int start = 0;

		// Add segments before each match found
		while ((index = input.indexOf(spliter, start)) != -1) {
			if (start == index) {
				matchList.add("");
				start += len;
			} else {
				String match = input.substring(start, index);
				matchList.add(match);
				start = index + len;
			}
		}

		// If no match was found, return this
		if (start == 0)
			return new String[] { input.toString() };

		// Add remaining segment
		if (start != input.length()) {
			matchList.add(input.substring(start));
		}

		return matchList.toArray(new String[0]);
	}

	/**
	 * This method uses the indexOf() and the subString() methdos to split the
	 * target String by the splitter, compared with String.split, this method
	 * greatly improves the performance for the splitting
	 * 
	 * @param s
	 * @return String array after splitting, the last blank string will not be
	 *         included. (e.g. "asdf:asdf:" after splitted with ':', [asdf,
	 *         asdf] will be generated£©
	 */
	public static String[] split(String input, char spliter) {
		ArrayList<String> matchList = new ArrayList<String>();
		int index = -1;
		int start = 0;

		// Add segments before each match found
		while ((index = input.indexOf(spliter, start)) != -1) {
			if (start == index) {
				matchList.add("");
				start++;
			} else {
				String match = input.substring(start, index);
				matchList.add(match);
				start = index + 1;
			}
		}

		// If no match was found, return this
		if (start == 0)
			return new String[] { input.toString() };

		// Add remaining segment
		if (start != input.length()) {
			matchList.add(input.substring(start));
		}

		return matchList.toArray(new String[0]);
	}

	/**
	 * Split and trim() each cell.
	 * 
	 * This method uses the indexOf() and the subString() methdos to split the
	 * target String by the splitter, compared with String.split, this method
	 * greatly improves the performance for the splitting.
	 * 
	 * @param s
	 * @return String array after splitting, the last blank string will not be
	 *         included. (e.g. "asdf:asdf:" after splitted with ':', [asdf,
	 *         asdf] will be generated£©
	 */
	public static String[] splitAndTrim(String input, String spliter) {
		String[] cells = split(input, spliter);
		for(int i=0; i<cells.length; i++) {
			cells[i] = cells[i].trim();
		}
		return cells;
	}

	/**
	 * Split and trim() each cell.
	 * 
	 * This method uses the indexOf() and the subString() methdos to split the
	 * target String by the splitter, compared with String.split, this method
	 * greatly improves the performance for the splitting
	 * 
	 * @param s
	 * @return String array after splitting, the last blank string will not be
	 *         included. (e.g. "asdf:asdf:" after splitted with ':', [asdf,
	 *         asdf] will be generated£©
	 */
	public static String[] splitAndTrim(String input, char spliter) {
		String[] cells = split(input, spliter);
		for(int i=0; i<cells.length; i++) {
			cells[i] = cells[i].trim();
		}
		return cells;
	}
	
	/**
	 * The index of next none-blank character. If there is no, return -1
	 * 
	 * @param str
	 * @param start
	 * @return
	 */
	public static int getNextNoneBlankIndex(String str, int start) {
		for(int i=start; i< str.length(); i++) {
			if(str.charAt(i)!=' ') {
				return i;
			}
		}
		
		return -1;
	}
	
	
	/**
	 * Merge the two String array to a new one
	 * 
	 * @param strs1
	 * @param strs2
	 * @return the combined array, may be null is both of these two string arrays for merging are null
	 */
	public static String[] mergeStrArray(String[] strs1, String[] strs2) {
		if(strs1 == null || strs1.length == 0)
			return strs2==null ? null : strs2.clone();
		
		if(strs2 == null || strs2.length == 0)
			return strs1==null ? null : strs1.clone();
		
		String[] strs = new String[strs1.length+strs2.length];
		
		int idx = 0;
		for(String str : strs1) {
			strs[idx++] = str;
		}
		
		for(String str : strs2) {
			strs[idx++] = str;
		}
		
		return strs;
	}
	
	/**
	 * Merge the two String array to a new one
	 * 
	 * @param strs1
	 * @param strs2
	 * @return the combined array, may be null is both of these two string arrays for merging are null
	 */
	public static String[] mergeStrArray(String[] strs1, String strs2) {
		
		if(strs1 == null)
			return new String[] {strs2};
		
		String[] strs = new String[strs1.length+1];
		
		int idx = 0;
		for(String str : strs1) {
			strs[idx++] = str;
		}
		
		strs[idx] = strs2;
		
		return strs;
	}
	
	/**
	 * Merge the two String array to a new one
	 * 
	 * @param strs1
	 * @param strs2
	 * @return the combined array, may be null is both of these two string arrays for merging are null
	 */
	public static String[] mergeStrArray(String[][] strss) {
		
		if(strss == null)
			return null;
		
		ArrayList<String> list = new ArrayList<String>();
		
		for(String[] strs : strss) {
			if(strs == null)
				continue;
			
			for(String str : strs) {
				list.add(str);
			}
		}

		return list.toArray(new String[list.size()]);
	}
	
}
