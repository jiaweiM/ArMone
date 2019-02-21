/* 
 ******************************************************************************
 * File: ArraysConverter.java * * * Created on 2011-1-12
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.arrayutil;

/**
 * @author ck
 *
 * @version 2011-1-12, 13:05:43
 */
public class ArraysConverter {

	public static float [] doubleToFloat(double [] list){
		float [] con = new float [list.length];
		for(int i=0;i<list.length;i++){
			con[i] = (float) list[i];
		}
		return con;
	}
	
	public static double [] floatToDouble(float [] list){
		double [] con = new double [list.length];
		for(int i=0;i<list.length;i++){
			con[i] = list[i];
		}
		return con;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
