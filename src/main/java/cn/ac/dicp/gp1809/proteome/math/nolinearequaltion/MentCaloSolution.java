/* 
 ******************************************************************************
 * File: MentCaloSolution.java * * * Created on 01-15-2008
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
package cn.ac.dicp.gp1809.proteome.math.nolinearequaltion;

import java.util.Arrays;
import java.util.Random;


/**
 * 
 * Monte carlo algorithm
 * 
 * @author Xinning
 * @version 0.1, 01-15-2008, 15:14:20
 */
public class MentCaloSolution implements ISolution {
	
	private double boundary = 2d;
	
	private int boundlimit = 10;
	
	private Random rand = new Random();
	
	/**
	 * 
	 * @param boundary the value between which ([-boundary, boundary]) the random values are
	 * 				   generated and added to the start solutions.
	 *  
	 * @param boundlimit if there have been more than boundlimit set of random values generated but none
	 * 					 gave a more precise solution (mode function with smaller return value), 
	 * 					 the boundary will be divided by 2 and regenerated. 
	 */
	public MentCaloSolution(double boundary, int boundlimit){
		this.boundary = boundary;
		this.boundlimit = boundlimit;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.math.nolinearequaltion.ISolution#solute(double[], cn.ac.dicp.gp1809.proteome.math.nolinearequaltion.ISolution.IModeFunction, double)
	 */
	@Override
	public double[] solute(double[] x0, IModeFunction function, double precise) {
		int len = x0.length;
		double bound = this.boundary;
		
		double[] xk = Arrays.copyOf(x0, len);
		double[] xk1 = new double[len];
		
		//current precise
		double curt = function.compute(xk);
		
		for(int i=1;;i++){
			for(int j=0;j<len;j++){
				double add = -bound + 2d*bound*rand.nextDouble();
				xk1[j] = xk[j]+add;
			}
			double prc = function.compute(xk1);
			if(prc<curt){
				curt = prc;
				System.arraycopy(xk1, 0, xk, 0, len);
				
				if(curt<=precise)
					break;
				
				StringBuilder sb = new StringBuilder();
				for(int j=0;j<xk1.length;j++){
					sb.append(xk1[j]).append(" ");
				}
				System.out.println(sb);
				
				i=1;
				continue;
			}
			
			if(i==boundlimit){
				bound /= 2d;
				i=1;
				
				if(bound == 0d)
					bound = this.boundary;
			}
		}
		
		return xk;
	}
	
	public static void main(String[] args){
		IModeFunction mfun = new IModeFunction(){
			public double compute(double[] x) {
				double f1=3.0*x[0]+x[1]+2.0*x[2]*x[2]-3.0;
				double f2=-3.0*x[0]+5.0*x[1]*x[1]+2.0*x[0]*x[2]-1.0;
			    double f3=25.0*x[0]*x[1]+20.0*x[2]+12.0;
			    double f=Math.sqrt(f1*f1+f2*f2+f3*f3);
			    return f;
			}
			
		};
		
		MentCaloSolution  solution = new MentCaloSolution(2d,10);
		double[] solutions = solution.solute(new double[]{0d,0d,0d}, mfun, 0.000001d);
		System.out.println(solutions[0]+" "+solutions[1]+" "+solutions[2]+" ");
	}
	
}
