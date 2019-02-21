/* 
 ******************************************************************************
 * File: ObjectPool.java * * * Created on 07-09-2008
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

/**
 * Pool of objects used as a buffer so that same object will not
 * be created more and more. Sometimes we don't want the objects with
 * same value to be created more than once because these objects are
 * the same object even though they are created before and after. In 
 * other hand, the creation of redundant same valued objects slows down
 * the efficiency of programs.
 * 
 * <p>Notice: 1. objects with the same key value are the same (==)</p>
 * <p>        2. objects with different key values may equal (.equals()),
 *               but they are not the same (==).</p>
 * 
 * @author Xinning
 * @version 0.1, 07-09-2008, 13:51:28
 */
public interface ObjectPool <K,V> {
	
	/**
	 * 
	 * 
	 * @param key the key used 
	 * @return the object of V
	 */
	public V get(K key);
	
	
}
