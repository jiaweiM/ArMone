/* 
 ******************************************************************************
 * File: ISolution.java * * * Created on 01-15-2008
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


/**
 * Calculate the solution(s) for a nonlinear function group.
 * 
 * 
 * @author Xinning
 * @version 0.1, 01-15-2008, 14:53:17
 */
public interface ISolution {
	
	/**
	 * The main method for the calculation of solution for this function group.
	 * The ModeFunction must be specified as the sum of the square values of all functions
	 * in this function group, indicating that the fitness of this set of solutions.
	 * When the actual solutions of this function group are compute() in modefuncation, the returned
	 * value exactly equals to 0, therefore solutions can be calculated when the precise of this 
	 * function of the returned value in IModeFuncation. 
	 * 
	 * @param x0 the starting set of solutions
	 * @param function the mode function for this function group.
	 * @param precise the precise of the returned value of mode function to which the loop ended.
	 * @return the solution
	 */
	public double[] solute(double[] x0, IModeFunction function, double precise);
	
	
	
	/**
	 * The mode function.
	 * e.q. for function group 
	 * |2x1-x2x1-3 = 0
	 * |5x1+3x2 = 0
	 * the mode function should be (2x1-x2x1-3)^2+(5x1+3x2)^2.
	 * 
	 * @author Xinning
	 * @version 0.1, 01-15-2008, 15:07:12
	 */
	public interface IModeFunction{
		
		/**
		 * The returned value for this set of solution.
		 * This value indicates the precise of this set of solution as the return value 
		 * for the actual solution is 0;
		 * 
		 * @param x the set of solution
		 * @return the precise (or the return value of mode function)
		 */
		public double compute(double[] x);
	}
}
