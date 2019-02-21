/*
 * *****************************************************************************
 * File: Searcher.java * * * Created on 04-08-2008
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
package cn.ac.dicp.gp1809.util.math;

/**
 * Search for the specific value using fast search algorithm.
 * 
 * @author Xinning
 * @version 0.1, 04-08-2008, 20:06:48
 */
public class Searcher {
	
	/**
	 * Using the dichotomy strategy to search for the point at which the
	 * target value equals or smaller than the value in the point, but the
	 * previous value of this point is smaller than the target value.
	 * <p><b>The array must be sorted from small to big first</b>
	 * 
	 * 
	 * @param target target value to be search, 
	 * @param array
	 * @return the point
	 */
	public static int dichotomySearch(double target, double[] array){
		int lowb = 0, upb = array.length-1;
		int current = 0;
		 //the first one
		if(target<=array[current])
			return 0;
		  
		while(lowb<=upb){
			current = (lowb+upb)/2;
			  
			if(target<=array[current]){
				  
				if(target>array[current-1]){
					return current;
				}
				else {
					upb = current-1;
				}
			}
			else{
				lowb = current + 1;
			}
		}
		return 0;
	}
	
	/**
	 * Using the dichotomy strategy to search for the point at which the
	 * target value equals or smaller than the value in the point, but the
	 * previous value of this point is smaller than the target value.
	 * <p><b>The array must be sorted from small to big first</b>
	 * 
	 * 
	 * @param target target value to be search, 
	 * @param array
	 * @return the point
	 */
	public static int dichotomySearch(float target, float[] array){
		int lowb = 0, upb = array.length-1;
		int current = 0;
		 //the first one
		if(target<=array[current])
			return 0;
		  
		while(lowb<=upb){
			current = (lowb+upb)/2;
			  
			if(target<=array[current]){
				  
				if(target>array[current-1]){
					return current;
				}
				else {
					upb = current-1;
				}
			}
			else{
				lowb = current + 1;
			}
		}
		return 0;
	}
}
