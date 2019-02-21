/*
 ******************************************************************************
 * File: IProtein.java * * * Created on 07-09-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome;

import java.text.DecimalFormat;

import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;


/**
 * A protein containing the protein identification informations of peptides.
 * 
 * @author Xinning
 * @version 0.2, 05-20-2010, 15:18:29
 */
public interface IProtein {

	/**
	 * The decimal format for protein probability 
	 */
	public static final DecimalFormat PROBDF = DecimalFormats.DF0_4;

	/**
	 * The line separator;
	 */
	public static final String lineSeparator = IOConstant.lineSeparator;

	/**
	 * Get the protein format used for protein string output (toString())
	 * 
	 * @return
	 */
//	public IProteinFormat getProteinFormat();
	
	
	
}
