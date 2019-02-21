/* 
 ******************************************************************************
 * File: IHashable.java * * * Created on 06-05-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util;

/**
 * A mark of hash able
 * 
 * @author Xinning
 * @version 0.1, 06-05-2009, 14:14:26
 */
public interface IHashable {
	
	
	/**
	 * equals
	 * 
	 * @param obj
	 * @return
	 */
	public boolean equals(Object obj);
	
	/**
	 * The hash code
	 * 
	 * @return
	 */
	public int hashCode();
}
