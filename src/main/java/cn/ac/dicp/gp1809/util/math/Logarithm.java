/*
 * *****************************************************************************
 * File: Logarithm.java * * * Created on 10-28-2007 
 * Copyright (c) 2007 Xinning Jiang
 * vext@163.com This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.util.math;

/**
 * The easy used Logarithm
 * 
 * @author Xinning
 * @version 0.1, 10-28-2007, 14:27:07
 */
public class Logarithm
{
	public static double log(double base,double value)
	{
		return Math.log(value)/Math.log(base);
	}
	
	public static double log2(double value)
	{
		return log(2d,value);
	}
	
	/**
	 * @param value
	 * @return log10(value)
	 */
	public static double lg(double value){
		return log(10,value);
	}
	
}