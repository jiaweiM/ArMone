/* 
 ******************************************************************************
 * File: MPFCalculator.java * * * Created on 12-09-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.calculators.MPF;

import cn.ac.dicp.gp1809.proteome.proteometools.calculators.IPropertyCalculator;

/**
 * The peptide mobile proton factor calculator
 * 
 * @author Xinning
 * @version 0.1, 12-09-2008, 20:52:59
 */
public class MPFCalculator implements IPropertyCalculator {

	/**
	 * Calculate the mobile proton factor values for the aminoacid sequence.
	 * Same as the static calling of {@link #compute(String)};
	 */
	@Override
	public double calculate(String sequence) {
		return compute(sequence);
	}
	
	/**
	 * Calculate the mobile proton factor values for the aminoacid sequence.
	 * Equals to {@link #calculate(String)}.
	 * 
	 * 
	 * @param sequence
	 */
	public static double compute(String sequence){
		double mpf = 0d;
		for (int i = 0, n = sequence.length(); i < n; i++) {
			char c = sequence.charAt(i);

			switch (c) {
			case 'R':
				mpf += 1d;
				break;
			case 'K':
				mpf += 0.8d;
				break;
			case 'H':
				mpf += 0.5d;
				break;
			}
		}

		return mpf;
	}

}
