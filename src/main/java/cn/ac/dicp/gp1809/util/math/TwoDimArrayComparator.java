/* 
 ******************************************************************************
 * File: TwoDimArrayComparator.java * * * Created on 2010-11-30
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.math;

import java.util.Comparator;

/**
 * @author ck
 *
 * @version 2010-11-30, 22:53:08
 */
public class TwoDimArrayComparator implements Comparator <Object> {

	private int keyColumn = 0;
	private int sortOrder = 1;
	
	public TwoDimArrayComparator () {
		
	}
	
	public TwoDimArrayComparator (int keyColumn) {
		this.keyColumn = keyColumn;
	}
	
	public TwoDimArrayComparator (int keyColumn,int sortOrder) {
		this.keyColumn = keyColumn;
		this.sortOrder = sortOrder;
	}
	
	public int compare(Object a, Object b) {
		
		if (a instanceof String []) {
			return sortOrder * ((String[])a)[keyColumn].compareTo(((String[])b)[keyColumn]);
		} else if (a instanceof int []){
			Integer i1 = ((int[])a)[keyColumn];
			Integer i2 = ((int[])b)[keyColumn];
			return sortOrder * i1.compareTo(i2);
		} else if (a instanceof double []){
			Double d1 = ((double[])a)[keyColumn];
			Double d2 = ((double[])b)[keyColumn];
			return sortOrder * d1.compareTo(d2);
		} else {
			return 0;
		}
		
	}

}
