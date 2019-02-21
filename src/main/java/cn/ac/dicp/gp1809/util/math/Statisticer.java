/*
 * *****************************************************************************
 * File: Statisticer.java * * * Created on 03-10-2008 
 * Copyright (c) 2008 Xinning Jiang vext@163.com 
 * 
 * This program is free software; you can redistribute it and/or
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

import java.util.Arrays;

/**
 * Some static method for statisticer;
 * 
 * @author Xinning
 * @version 0.1, 03-10-2008, 09:43:25
 */
public class Statisticer {
	
	/**
	 * Get the relative SD of the specific array.
	 */
	public static double getCV(int[] values){
		int replicateNum = values.length;
		if(replicateNum==1)
			return 0;
		
		int sum2=0, sum=0;
		for(int i=0;i<replicateNum;i++){
			int v = values[i];
			sum2 += v*v;
			sum += v;
		}
		double sd = Math.pow((sum2-(double)(sum*sum)/replicateNum)/(replicateNum-1),0.5d);
		return sd/sum*replicateNum;
	}
	
	/**
	 * Get the statistic value for this array.
	 * The statistic value is the average value or the median value of this array.
	 * @param values
	 * @param isAvg true for average value and false for median value.
	 */
	public static double getStatisticValue(int[] values, boolean isAvg){
		int replicateNum = values.length;
		if(replicateNum==1)
			return values[0];
		
		double avg;
		if(isAvg){
			int sum = 0;
			for(int i=0;i<replicateNum;i++){
				sum += values[i];
			}
			avg = (double)sum/replicateNum;
		}
		else{
			int[] spcountc = new int[replicateNum];
			System.arraycopy(values, 0, spcountc, 0, replicateNum);
			Arrays.sort(spcountc);
			int p = replicateNum/2;
			
			if(replicateNum%2==0)
				avg = (double)(spcountc[p]+spcountc[p-1])/2;
			else
				avg = spcountc[p];
		}
		return avg;
	}
	
}
