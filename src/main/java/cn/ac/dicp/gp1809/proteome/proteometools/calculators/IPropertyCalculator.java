/* 
 ******************************************************************************
 * File: IPropertyCalculator.java * * * Created on 12-09-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.calculators;

/**
 * The property calculator for aminoacid sequences. These properties includes pi
 * values, mpf values, gravy values and so on.
 * 
 * @author Xinning
 * @version 0.1, 12-09-2008, 21:01:32
 */
public interface IPropertyCalculator {

	/**
	 * Calculate the property values for the aminoaicd sequence.
	 * 
	 * @param sequence
	 *            the sequence of aminoacids
	 * @return
	 */
	public double calculate(String sequence);

}
