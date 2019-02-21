/* 
 ******************************************************************************
 * File: ISpectrumDataset.java * * * Created on 04-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf;

import cn.ac.dicp.gp1809.drawjf.IJFDataset;

/**
 * The spectrum data set
 * 
 * @author Xinning
 * @version 0.1, 04-13-2009, 14:44:32
 */
public interface ISpectrumDataset extends IJFDataset{
	
	/**
	 * The x axis label (M/Z)
	 */
	static String xlabel = "M/Z";
	/**
	 * The y axis label (Relative Intensity)
	 */
	static String ylabel = "Relative Intensity";

	/**
	 * The width of the bar (ticket)
	 */
	static final double BarWidth = 0.00005d;
	

}
