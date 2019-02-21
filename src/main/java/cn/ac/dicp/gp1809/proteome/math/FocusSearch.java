/*
 * *****************************************************************************
 * File: FocusSearch.java * * * Created on 12-10-2007 
 * Copyright (c) 2007 Xinning Jiang vext@163.com 
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
package cn.ac.dicp.gp1809.proteome.math;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.BioException;

/**
 * solves y = f(x) = 0 by focus search.
 * Only really suitable for monotonic functions as
 * the method will check that the initial values
 * lie on opposite sides of the X=0 axis.
 * 
 * @author Xinning
 * @version 0.1, 03-17-2008, 16:16:05
 */

public class FocusSearch {
	
	
	private static int MAXLOOP = 2000;//default loop
	
    public static double solve(double min, double max, double tolerance, ComputeObject obj)
    throws BioException{
    	
    	return solve(min,max,tolerance,MAXLOOP,obj);
    }
    
    /**
     * method that will attempt solving the equation.
     *
     * @param min lower bound of search space.
     * @param max upper bound of search space.
     * @param tolerance change in x required to continue iteration.
     * @param obj the class of ComputeObject class representing the equation to be solved.
     */
    public static double solve(double min, double max, double tolerance, int maxloop, ComputeObject obj)
    throws BioException
    {
    	int counts = 0;
    	   // compute initial values
        double x1 = min;
        double y1 = obj.compute(min);
        double x2 = max;
        double y2 = obj.compute(max);

        // validate that function standas some chance of monotonicity
        if ((y1 <  0d)&&(y2 < 0d)) throw new BioException("Illegal initial range limits (< 0).");
        if ((y1 >  0d)&&(y2 > 0d)) throw new BioException("Illegal initial range limits (> 0).");

        // iterate
        while (Math.abs(x1 - x2) > tolerance) {
        	if(counts>maxloop)
        		break;
        	
            // compute a value midway within the current interval
            double newX = 0.5 * (x1 + x2);
            double newY = obj.compute(newX);

            // determine new interval
            if (newY >= 0d) {
                if (y1 >= 0d) {
                    y1 = newY;
                    x1 = newX;
                }
                else {
                    y2 = newY;
                    x2 = newX;
                }
            }
            else if (newY < 0d) {
                if (y1 >= 0d) {
                    y2 = newY;
                    x2 = newX;
                }
                else {
                    y1 = newY;
                    x1 = newX;
                }
            }
            
            counts++;
        }
        
        // converged: return midpoint of interval
        return 0.5 * (x1 + x2);
    }
}
