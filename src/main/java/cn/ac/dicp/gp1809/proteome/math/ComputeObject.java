/*
 * *****************************************************************************
 * File: ComputeObject.java * * * Created on 12-09-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.math;

/**
 * Interface for classes that return a single double precision value for a
 * single double precision argument. Used to represent equations of type y =
 * f(x) = 0.
 * 
 * @author Xinning
 * @version 0.1, 12-09-2008, 21:22:16
 */
public interface ComputeObject {

	/**
	 * workhorse method for this class. computes f(x) for given x.
	 */

	public double compute(double arg);
}
