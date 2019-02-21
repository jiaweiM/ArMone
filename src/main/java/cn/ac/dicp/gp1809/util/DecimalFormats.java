/* 
 ******************************************************************************
 * File: DecimalFormatUtilities.java * * * Created on 04-05-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Decimal format utilities
 * 
 * @author Xinning
 * @version 0.1, 04-05-2010, 21:34:39
 */
public class DecimalFormats {

	/**
	 * new DecimalFormat("0")
	 * 
	 * 1, 12, 123
	 */
	public static final DecimalFormat DF0_0;
	
	/**
	 * new DecimalFormat("0.#")
	 * 0.1,123.2, 123(.0)
	 */
	public static final DecimalFormat DF0_1;
	
	/**
	 * new DecimalFormat("0.##")
	 */
	public static final DecimalFormat DF0_2;
	
	/**
	 * new DecimalFormat("0.###")
	 */
	public static final DecimalFormat DF0_3;
	
	/**
	 * new DecimalFormat("0.####")
	 */
	public static final DecimalFormat DF0_4;
	
	/**
	 * new DecimalFormat("0.#####")
	 */
	public static final DecimalFormat DF0_5;
	
	/**
	 * new DecimalFormat("0.#######")
	 */
	public static final DecimalFormat DF0_6;
	
	/**
	 * new DecimalFormat("00")
	 * 01, 12, 123
	 */
	public static final DecimalFormat DF2_0;
	
	/**
	 * new DecimalFormat("000")
	 * 001, 012, 123
	 */
	public static final DecimalFormat DF3_0;
	
	/**
	 * new DecimalFormat("0000")
	 * 0001, 0012, 0123
	 */
	public static final DecimalFormat DF4_0;
	
	/**
	 * new DecimalFormat("00000")
	 * 00001, 00012, 00123
	 */
	public static final DecimalFormat DF5_0;
	
	/**
	 * new DecimalFormat("000000")
	 * 000001, 000012, 000123
	 */
	public static final DecimalFormat DF6_0;
	
	/**
	 * PRECENT (0.##%)
	 * 
	 */
	public static final DecimalFormat DF_PRECENT0_2;
	
	/**
	 * PRECENT (0.##%)
	 * 
	 */
	public static final DecimalFormat DF_PRECENT0_3;
	
	/**
	 *  (0.##E#)
	 * 
	 */
	public static final DecimalFormat DF_E2;
	
	/**
	 *  (0.###E#)
	 * 
	 */
	public static final DecimalFormat DF_E3;
	
	/**
	 *  (0.####E#)
	 * 
	 */
	public static final DecimalFormat DF_E4;
	
	
	static {
		Locale def = Locale.getDefault();
		Locale.setDefault(Locale.US);
		
		DF_PRECENT0_2 = new DecimalFormat("#0.##%");
		DF_PRECENT0_3 = new DecimalFormat("#0.###%");
		
		DF0_0 = new DecimalFormat("0");
		DF0_1 = new DecimalFormat("0.0");
		DF0_2 = new DecimalFormat("0.0#");
		DF0_3 = new DecimalFormat("0.0##");
		DF0_4 = new DecimalFormat("0.0###");
		DF0_5 = new DecimalFormat("0.0####");
		DF0_6 = new DecimalFormat("0.0#####");
		
		DF2_0 = new DecimalFormat("00");
		DF3_0 = new DecimalFormat("000");
		DF4_0 = new DecimalFormat("0000");
		DF5_0 = new DecimalFormat("00000");
		DF6_0 = new DecimalFormat("000000");
		
		
		DF_E2 = new DecimalFormat("#.##E0");
		DF_E3 = new DecimalFormat("#.###E0");
		DF_E4 = new DecimalFormat("#.####E0");
		
		Locale.setDefault(def);
	}
	
	
	private DecimalFormats() {
		
	}
}
